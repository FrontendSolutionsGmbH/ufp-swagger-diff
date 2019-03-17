package com.froso.ufp;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.slf4j.*;
import org.springframework.stereotype.*;

@Component
public class SchemasInFolder {
    final String regexp = ".*([0-9]+)\\.([0-9]+)\\.([0-9]+).*(json|yml|yaml)";

    private static Logger LOG = LoggerFactory
            .getLogger(SpringBootConsoleApplication.class);

    public String getSchemaVersion(final String a) {
        Pattern r = Pattern.compile(regexp);
        Matcher m1 = r.matcher(a);
        Integer aMajor = 0, aMinor = 0, aPatch = 0;
        if (m1.find()) {
            aMajor = Integer.parseInt(m1.group(1));
            aMinor = Integer.parseInt(m1.group(2));
            aPatch = Integer.parseInt(m1.group(3));
        }
        return aMajor + "." + aMinor + "." + aPatch;
    }

    public Set<String> schemasInFolder(final String folder) {

        File[] files = new File(folder).listFiles();

        Set<String> result = new TreeSet<>(new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                Pattern r = Pattern.compile(regexp);
                Matcher m1 = r.matcher(a);
                Matcher m2 = r.matcher(b);
                Integer aMajor = 0, aMinor = 0, aPatch = 0;
                Integer bMajor = 0, bMinor = 0, bPatch = 0;
                if (m1.find()) {
                    aMajor = Integer.parseInt(m1.group(1));
                    aMinor = Integer.parseInt(m1.group(2));
                    aPatch = Integer.parseInt(m1.group(3));
                }
                if (m2.find()) {

                    bMajor = Integer.parseInt(m2.group(1));
                    bMinor = Integer.parseInt(m2.group(2));
                    bPatch = Integer.parseInt(m2.group(3));
                }
                if (!aMajor.equals(bMajor)) {
                    return aMajor - bMajor;
                } else if (!aMinor.equals(bMinor)) {
                    return aMinor - bMinor;
                } else {
                    return aPatch - bPatch;
                }


            }
        });

        if (files != null) {
            for (final File fileEntry : files) {
                if (fileEntry.isDirectory()) {
                } else {
                    if (Pattern.matches(regexp, fileEntry.getName())) {
                        result.add(fileEntry.getName());
                    }
                }
            }
            LOG.info("Found swagger schemas {}", result);
        }
        return result;
    }


}
