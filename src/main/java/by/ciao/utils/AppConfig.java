package by.ciao.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class AppConfig {

    private static final String ENV_VAR_NAME = "PROFILE";
    private static Properties props = new Properties();
    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    static {
        String profile = System.getenv(ENV_VAR_NAME);
        String commonFilename = "application.properties";
        String profileFilename = String.format("application-%s.properties", profile);

        try {
            ClassLoader classLoader = AppConfig.class.getClassLoader();
            List<Properties> propertiesList = new ArrayList<>();

            // Load common properties file
            try (InputStream commonInput = classLoader.getResourceAsStream(commonFilename)) {
                Properties commonProps = new Properties();
                commonProps.load(commonInput);
                propertiesList.add(commonProps);
            }

            // Load profile-specific properties file
            try (InputStream profileInput = classLoader.getResourceAsStream(profileFilename)) {
                Properties profileProps = new Properties();
                profileProps.load(profileInput);
                propertiesList.add(profileProps);
            }

            // Merge properties from all files into a single Properties object
            Properties mergedProps = new Properties();
            for (Properties props : propertiesList) {
                mergedProps.putAll(props);
            }
            props = mergedProps;

        } catch (IOException ex) {
            log.error(LoggerMessages.propertiesException(commonFilename), ex);
        }
    }

    public static String getProperty(String key) {
        return props.getProperty(key);
    }

}