package by.ciao.EnglishSchoolBot.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.logging.Level;

public final class AppBanner {
    public static void printBanner() {
        try {
            InputStream inputStream = Objects.requireNonNull(ReadFile.class.getResourceAsStream("/banner.txt"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            LoggerService.logInfo(Level.SEVERE, LoggerMessages.printBannerException(), e);
        }
    }
}
