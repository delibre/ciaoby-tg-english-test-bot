package by.ciao.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public final class AppBanner {

    private static final Logger log = LoggerFactory.getLogger(AppBanner.class);

    public static void printBanner() {

        try {
            InputStream inputStream = Objects.requireNonNull(ReadFile.class.getResourceAsStream("/banner.txt"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            log.error(LoggerMessages.printBannerException(), e);
        }

    }

}
