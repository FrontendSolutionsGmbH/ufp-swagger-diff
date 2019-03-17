package com.froso.ufp;

import java.util.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;

@SpringBootApplication
public class SpringBootConsoleApplication
        implements CommandLineRunner {

    @Autowired
    private DiffRenderer diffRenderer;

    @Autowired
    private SchemasInFolder filesInFolder;

    private static Logger LOG = LoggerFactory
            .getLogger(SpringBootConsoleApplication.class);

    public static void main(String[] args) {
        LOG.info("STARTING THE APPLICATION");
        SpringApplication.run(SpringBootConsoleApplication.class, args);
        LOG.info("APPLICATION FINISHED");
    }

    @Override
    public void run(String... args) {
        LOG.info("EXECUTING : command line runner");
        String pathName = "./petstore";
        Set<String> files = filesInFolder.schemasInFolder(pathName);


        String last = null;
        for (String s : files) {
            if (last != null) {
                try {
                    diffRenderer.renderDiff(pathName + "/" + last, pathName + "/" + s, pathName + "/diff-" + filesInFolder.getSchemaVersion(last) + '-' + filesInFolder.getSchemaVersion(s) + "");
                }catch(Exception e){

                    LOG.error("diff error ",e) ;

                }
            }
            last = s;
        }

        LOG.info("Finished");
    }
}
