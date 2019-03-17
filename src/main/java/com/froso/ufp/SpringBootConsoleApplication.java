package com.froso.ufp;

import com.deepoove.swagger.diff.*;
import com.froso.ufp.model.*;
import com.github.jknack.handlebars.*;
import com.github.jknack.handlebars.helper.*;
import com.google.common.collect.*;
import java.io.*;
import java.util.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;

@SpringBootApplication
public class SpringBootConsoleApplication
        implements CommandLineRunner, ExitCodeGenerator {

    @Value("${ufp.swagger-diff.validate.url}")
    private String validateUrl;
    @Value("${ufp.swagger-diff.output.folder}")
    private String outputFolder;
    @Value("${ufp.swagger-diff.output.html.title}")
    private String htmlTitle;
    @Value("${ufp.swagger-diff.input.folder}")
    private String inputFolder;
    @Autowired
    private DiffRenderer diffRenderer;

    @Autowired
    private SchemasInFolder filesInFolder;


    private int exitCode = 0;

    private static Logger LOG = LoggerFactory
            .getLogger(SpringBootConsoleApplication.class);

    public static void main(String[] args) {
        LOG.info("Starting ufp-swagger-diff application");
        SpringApplication.run(SpringBootConsoleApplication.class, args);
        LOG.info("ufp-swagger-diff finished");
    }


    private void determineExitCode(DiffResult diffResult) {

        SwaggerDiff diff = diffResult.getDiff();

        LOG.info("Validation result {} -> {}", diff.getOldVersion(), diff.getNewVersion());
        if (diff.getChangedEndpoints().size() > 0) {

            LOG.info("Changed endpoints exist, ecit code 1");
            exitCode = 1;

        }
        if (diff.getMissingEndpoints().size() > 0) {

            LOG.info("Changed endpoints exist, ecit code 1");
            exitCode = 1;

        }

    }

    @Override
    public int getExitCode() {
        return exitCode;
    }

    @Override
    public void run(String... args) throws IOException {
        LOG.info("EXECUTING : command line runner");

        Set<String> files = filesInFolder.schemasInFolder(inputFolder);

        List<DiffResult> diffs = new ArrayList<>();
        String last = null;
        for (String s : files) {
            if (last != null) {
                try {
                    String diffName = "diff-" + filesInFolder.getSchemaVersion(last) + '-' + filesInFolder.getSchemaVersion(s);

                    DiffResult currentDiff = diffRenderer.renderDiff(inputFolder + "/" + last, inputFolder + "/" + s, outputFolder + "/" + diffName);

                    currentDiff.setName(diffName);

                    diffs.add(currentDiff);
                } catch (Exception e) {

                    LOG.error("diff error ", e);

                }
            }
            last = s;
        }


        try {
            String diffName = "diff-" + filesInFolder.getSchemaVersion(last) + '-' + "live";

            DiffResult currentDiff = diffRenderer.renderDiff(inputFolder + "/" + last, validateUrl, outputFolder + "/" + diffName);

            currentDiff.setName(diffName);

            diffs.add(currentDiff);
            determineExitCode(currentDiff);
        } catch (Exception e) {

            exitCode = 1;
        }

        Map<String, Object> contextData = new HashMap<>();
        contextData.put("data", Lists.reverse(diffs));
        contextData.put("title", htmlTitle);

        Context context2 = Context.newBuilder(contextData).build();

        renderAndSaveHandlebarsTemplate("templates/index", context2, outputFolder + "/index.html");
        renderAndSaveHandlebarsTemplate("templates/defaultCSS", context2, outputFolder + "/defaultCSS.hbs");
    }

    private void renderAndSaveHandlebarsTemplate(String templatePath, Context context, String target) throws IOException {
        Handlebars handlebars = new Handlebars();
//        StringHelpers.register(handlebars);
        handlebars.registerHelpers(ConditionalHelpers.class);
        handlebars.registerHelpers(StringHelpers.class);
        handlebars.registerHelpers(PartialHelper.class);
        handlebars.registerHelpers(EachHelper.class);
        handlebars.registerHelpers(IfHelper.class);
        Template template = handlebars.compile(templatePath);


        String indexHtml = template.apply(context);

        diffRenderer.writeFile(target, indexHtml);


    }

}
