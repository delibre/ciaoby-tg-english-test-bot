package by.ciao.EnglishSchoolBot.utils;

public final class ExceptionMessages {
    public static String argumentExceptionInServiceVar() {
        return "IllegalArgumentException on execute method call in BotService class: Object type is not supported\n";
    }

    public static String tgApiExceptionInServiceVar() {
        return "TelegramApiException on execute method call in BotService class\n";
    }

    public static String processMessageException() {
        return "processMessage() IIllegalStateException in CiaoByBot class\n";
    }

    public static String addUserIfAbsentException() {
        return "addUserIfAbsent() method in CiaoByBot class\n";
    }

    public static String sendWarningException() {
        return "sendWarning() method in CiaoByBot class\n";
    }

    public static String messageProcessingException() {
        return "messageProcessing() method in CiaoByBot class\n";
    }

    public static String addPhoneException() {
        return "addPhone() method in CiaoByBot class\n";
    }

    public static String closeQueryException() {
        return "closeQuery() method in CiaoByBot class\n";
    }

    public static String setDelayException() {
        return "setDelay() method in AbstractState class\n";
    }

    public static String sendTextException() {
        return "sendText() method in CiaoByBot class\n";
    }
}
