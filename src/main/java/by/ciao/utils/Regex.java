package by.ciao.utils;

import java.util.regex.Pattern;

public final class Regex {

    public static boolean isCorrectPhoneFormat(final String phone) {
        String patterns
                = "^(\\+?\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
                + "|^(\\+?\\d{1,3}( )?)?(\\d{3}''?){2}\\d{3}$"
                + "|^(\\+?\\d{1,3}( )?)?(\\d{3}''?)(\\d{2}''?){2}\\d{2}$";


        return Pattern.compile(patterns).matcher(phone).matches();
    }

}
