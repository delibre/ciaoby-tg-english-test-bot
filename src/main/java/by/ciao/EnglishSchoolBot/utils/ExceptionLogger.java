package by.ciao.EnglishSchoolBot.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class ExceptionLogger {
    private static final Logger logger = Logger.getAnonymousLogger();

    public static void logException(Level lvl, String msg, Exception e) {
        logger.log(lvl, msg, e);
    }
}
