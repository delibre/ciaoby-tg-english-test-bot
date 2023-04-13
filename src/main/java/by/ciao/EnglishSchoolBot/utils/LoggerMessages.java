package by.ciao.EnglishSchoolBot.utils;

public final class LoggerMessages {
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
        return "sendText() method in BotService class\n";
    }

    public static String setTimerException() {
        return "setTimer() in AbstractState class\n";
    }

    public static String printBannerException(){
        return "Exception in printBanner() method, AppBanner class";
    }

    public static String unexpectedCase(String update){
        return "Unexpected case in onUpdateReceived() method in CiaoByBot class\n" + update + "\n";
    }

    public static String mapSize(int size) {
        return "Size of the map is " + size;
    }
}
