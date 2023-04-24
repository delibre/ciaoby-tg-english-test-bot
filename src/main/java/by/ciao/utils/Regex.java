package by.ciao.utils;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class Regex {

    public boolean isCorrectPhoneFormat(final String phone) {
        String patterns
                = "^(\\+?\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
                + "|^(\\+?\\d{1,3}( )?)?(\\d{3}''?){2}\\d{3}$"
                + "|^(\\+?\\d{1,3}( )?)?(\\d{3}''?)(\\d{2}''?){2}\\d{2}$";


        return Pattern.compile(patterns).matcher(phone).matches();
    }

}
