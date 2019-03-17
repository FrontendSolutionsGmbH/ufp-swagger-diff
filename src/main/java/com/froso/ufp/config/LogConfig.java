package com.froso.ufp.config;

import java.util.*;
import javax.annotation.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.*;

@Configuration
public class LogConfig {

    @Autowired
    private AbstractEnvironment environment;


    private static Logger LOG = LoggerFactory
            .getLogger(LogConfig.class);


    @PostConstruct
    public void printProperties() {

        LOG.info("**** APPLICATION PROPERTIES SOURCES ****");

        Set<String> properties = new TreeSet<>();
        for (EnumerablePropertySource p : findPropertiesPropertySources()) {
            LOG.info(p.toString());
            properties.addAll(Arrays.asList(p.getPropertyNames()));
        }

        LOG.info("**** APPLICATION PROPERTIES VALUES ****");
        print(properties);
        LOG.info("**** APPLICATION PROPERTIES VALUES END****");

    }

    private List<EnumerablePropertySource> findPropertiesPropertySources() {
        List<EnumerablePropertySource> propertiesPropertySources = new LinkedList<>();
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            if (propertySource instanceof EnumerablePropertySource) {
                propertiesPropertySources.add((EnumerablePropertySource) propertySource);
            }
        }
        return propertiesPropertySources;
    }

    private void print(Set<String> properties) {
        for (String propertyName : properties) {
            if (propertyName.contains("swagger-diff")) {
                LOG.info("{}={}", propertyName, environment.getProperty(propertyName));
            }
        }
    }
}
