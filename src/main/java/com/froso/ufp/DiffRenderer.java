package com.froso.ufp;

import com.deepoove.swagger.diff.*;
import com.deepoove.swagger.diff.output.*;
import com.froso.ufp.model.*;
import com.github.zafarkhaja.semver.*;
import java.io.*;
import java.util.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Component
public class DiffRenderer {

    @Value("${ufp.swagger-diff.output.html.css}")
    private String htmlCss;

    @Value("${ufp.swagger-diff.output.html.changelog.title}")
    private String title;

    /**
     * initialise the reference for allowed swagger diff types per version jumps
     */
    private static final Map<DiffType, List<SwaggerDiffType>> allowedSwaggerDiffTypesForDiffType = new HashMap<DiffType, List<SwaggerDiffType>>() {{
        put(DiffType.MAJOR, new ArrayList<SwaggerDiffType>() {{
            add(SwaggerDiffType.NEW);
            add(SwaggerDiffType.CHANGED);
            add(SwaggerDiffType.MISSING);
        }});
        put(DiffType.MINOR, new ArrayList<SwaggerDiffType>() {{
            add(SwaggerDiffType.CHANGED);
            add(SwaggerDiffType.NEW);
        }});
        put(DiffType.PATCH, new ArrayList<SwaggerDiffType>() {{
            add(SwaggerDiffType.CHANGED);
        }});
        put(DiffType.NONE, new ArrayList<SwaggerDiffType>() {{
        }});

    }};

    private List<DiffRemark> getRemarks(SwaggerDiff diff) {

        List<DiffRemark> result = new ArrayList<>();

        Version oldVersion = getVersion(diff.getOldVersion());
        Version newVersion = getVersion(diff.getNewVersion());


        /**
         * determine if new version is a consecutive sucessor
         * having a zero for big jumps, and consecutive values
         * for small ones
         *
         * 0.1.0 -> 1.0.0 is allowed but 1.0.1 is not allowed
         *
         */

        if (oldVersion.getMajorVersion() != newVersion.getMajorVersion()) {
            // new major has to be greater than old major
            if (oldVersion.getMajorVersion() > newVersion.getMajorVersion()) {
                result.add(DiffRemark.MAJOR_JUMP_BACKWARDS_BREAK);
            }

            // new major needs to be exactly +1 increased from old
            if (oldVersion.getMajorVersion() + 1 != newVersion.getMajorVersion()) {
                result.add(DiffRemark.MAJOR_CONSECUTIVITY_BREAK);
            }

            // all minor annd patch have to be 0
            if (newVersion.getMinorVersion() != 0) {
                result.add(DiffRemark.MINOR_CONSECUTIVITY_BREAK);
            }
            // all minor annd patch have to be 0
            if (newVersion.getPatchVersion() != 0) {
                result.add(DiffRemark.PATCH_CONSECUTIVITY_BREAK);
            }


        } else if (oldVersion.getMinorVersion() != newVersion.getMinorVersion()) {
            // new major has to be greater than old major
            if (oldVersion.getMinorVersion() > newVersion.getMinorVersion()) {
                result.add(DiffRemark.MINOR_JUMP_BACKWARDS_BREAK);
            }

            // new major needs to be exactly +1 increased from old
            if (oldVersion.getMinorVersion() + 1 != newVersion.getMinorVersion()) {
                result.add(DiffRemark.MINOR_CONSECUTIVITY_BREAK);
            }


            //  patch have to be 0
            if (newVersion.getPatchVersion() != 0) {
                result.add(DiffRemark.PATCH_CONSECUTIVITY_BREAK);
            }


        } else if (oldVersion.getPatchVersion() != newVersion.getPatchVersion()) {
            // new major has to be greater than old major
            if (oldVersion.getPatchVersion() > newVersion.getPatchVersion()) {
                result.add(DiffRemark.PATCH_JUMP_BACKWARDS_BREAK);
            }

            // new major needs to be exactly +1 increased from old
            if (oldVersion.getPatchVersion() + 1 != newVersion.getPatchVersion()) {
                result.add(DiffRemark.PATCH_CONSECUTIVITY_BREAK);
            }

        }


        /**
         * check expected semver behaviour, warn on version jump and no change
         */


        DiffType diffType = getUpdateType(diff.getOldVersion(), diff.getNewVersion());
        List<SwaggerDiffType> realizedDiffType = getSwaggerDifftypes(diff);
        List<SwaggerDiffType> allowed = allowedSwaggerDiffTypesForDiffType.get(diffType);

        // check how many of the allowed are inside realised
        int realizedButNotExpected = 0;
        for (SwaggerDiffType realized : realizedDiffType) {

            if (allowed.contains(realized)) {
                // ok an expected entry is contained
            } else {
                // hm, an expected entry is contained, which is ok

                realizedButNotExpected++;
            }


        }
        if (realizedButNotExpected > 0) {

            result.add(DiffRemark.REALIZED_BUT_NOT_EXPECTED);


        }

        if (realizedDiffType.size() == 0) {
            result.add(DiffRemark.NO_CHANGE);

        }

        return result;


    }

    private static Logger LOG = LoggerFactory
            .getLogger(SpringBootConsoleApplication.class);

    private List<SwaggerDiffType> getSwaggerDifftypes(SwaggerDiff diff) {

        List<SwaggerDiffType> resultList = new ArrayList<SwaggerDiffType>();

        if (diff.getMissingEndpoints().size() > 0) {
            resultList.add(SwaggerDiffType.MISSING);
        }

        if (diff.getChangedEndpoints().size() > 0) {
            resultList.add(SwaggerDiffType.CHANGED);
        }

        if (diff.getNewEndpoints().size() > 0) {
            resultList.add(SwaggerDiffType.NEW);
        }
        return resultList;

    }

    public DiffResult renderDiff(String fileName1, String fileName2, String output) {
        DiffResult result = new DiffResult();
        SwaggerDiff diff1 = renderDiffHTML(fileName1, fileName2, output);
        renderDiffMarkDown(fileName1, fileName2, output);
        DiffType diffType = getUpdateType(diff1.getOldVersion(), diff1.getNewVersion());
        List<SwaggerDiffType> swaggerDiffTypes = getSwaggerDifftypes(diff1);

        LOG.info("SEMVER UPDATE TYPE {}", diffType);
        LOG.info("REALISED UPDATE TYPES {}", swaggerDiffTypes);
        LOG.info("EXPECTED UPDATE TYPES ARE {}", allowedSwaggerDiffTypesForDiffType.get(diffType));

        result.setRealisedDiffTypes(swaggerDiffTypes);
        result.setSemverDiffType(diffType);
        result.setExpectedDiffTypes(allowedSwaggerDiffTypesForDiffType.get(diffType));
        result.setRemarks(getRemarks(diff1));
        result.setDiff(diff1);
        return result;

    }

    private Version getVersion(String version) {
        try {

            Version v1 = Version.valueOf(version);
            return v1;
        } catch (Exception e) {

            LOG.error("Error parsing semver {}", e.getMessage());
        }

        return Version.forIntegers(0);
    }

    /**
     * the getUpdateType shall return if the jump between to semver versions is major/minor/patch
     * <p>
     * 1.0.0->1.0.1 patch - new allowed
     * 1.0.0->1.1.0 minor - new+changed allowed
     * 1.0.0->2.0.1 major - new+changed+deprecated allowed
     *
     * @return
     */
    public DiffType getUpdateType(String ver1, String ver2) {
        LOG.info("Comparing version {} {}", ver1, ver2);

        Version v1 = getVersion(ver1);
        Version v2 = getVersion(ver2);
        if (v1.getMajorVersion() != v2.getMajorVersion()) {

            return DiffType.MAJOR;

        } else if (v1.getMinorVersion() != v2.getMinorVersion()) {

            return DiffType.MINOR;
        }
        if (v1.getPatchVersion() != v2.getPatchVersion()) {

            return DiffType.PATCH;
        }
        return DiffType.NONE;
    }

    public SwaggerDiff renderDiffHTML(String fileName1, String fileName2, String output) {

        LOG.info("HTML Comparing {} {}", fileName1, fileName2);
        SwaggerDiff diff = SwaggerDiff.compareV2(fileName1, fileName2);
        String html = new HtmlRender(title,
                htmlCss)
                .render(diff);


        writeFile(output + ".html", html);
        return diff;

    }


    public SwaggerDiff renderDiffMarkDown(String fileName1, String fileName2, String output) {

        SwaggerDiff diff = SwaggerDiff.compareV2(fileName1, fileName2);

        LOG.info("Markdown Comparing {} {}", fileName1, fileName2);
        String markdown = new MarkdownRender()
                .render(diff);
        // LOG.info("Markdown Diff is {}", diff);
        //  LOG.info("\\n" + markdown);
        writeFile(output + ".md", markdown);
        return diff;
    }

    public void writeFile(String fileName, String content) {

        try {
            FileWriter fw = new FileWriter(fileName);
            fw.write(content);
            fw.close();

        } catch (IOException e) {
            LOG.error("ERROR WRITING File {} {}", fileName, e.getMessage());
        }

    }

}
