package by.ciao.EnglishSchoolBot.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class LoggerService {
    private static final Logger logger = Logger.getAnonymousLogger();

    public static void logInfo(Level lvl, String msg, Exception e) {
        logger.log(lvl, msg, e);
    }
}
