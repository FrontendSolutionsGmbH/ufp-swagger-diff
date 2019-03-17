package com.froso.ufp;

import com.deepoove.swagger.diff.*;
import com.deepoove.swagger.diff.output.*;
import java.io.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Component
public class DiffRenderer {

    @Value("${diff.html.css}")
    private String htmlCss;

    private static Logger LOG = LoggerFactory
            .getLogger(SpringBootConsoleApplication.class);

    public void renderDiff(String fileName1, String fileName2, String output) {

        renderDiffHTML(fileName1,fileName2,output) ;
        renderDiffMarkDown(fileName1,fileName2,output) ;





    }
    public void renderDiffHTML(String fileName1, String fileName2, String output) {


        LOG.info("HTML Comparing {} {}", fileName1, fileName2);
        SwaggerDiff diff = SwaggerDiff.compareV2(fileName1, fileName2);
        String html = new HtmlRender("Changelog",
                htmlCss)
                .render(diff);

        try {
            FileWriter fw = new FileWriter(
                    output+".html");
            fw.write(html);
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void renderDiffMarkDown(String fileName1, String fileName2, String output) {


        SwaggerDiff diff = SwaggerDiff.compareV2(fileName1, fileName2);

        LOG.info("Markdown Comparing {} {}", fileName1, fileName2);
        String markdown = new MarkdownRender()
                .render(diff);
        LOG.info("Markdown Diff is");
        LOG.info(markdown);
        try {
            FileWriter fw = new FileWriter(
                    output+".md");
            fw.write(markdown);
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
