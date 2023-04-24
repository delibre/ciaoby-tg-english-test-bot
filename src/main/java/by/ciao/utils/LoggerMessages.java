package by.ciao.utils;

import org.springframework.stereotype.Service;

@Service
public class LoggerMessages {

    public String argumentExceptionInServiceVar() {
        return "IllegalArgumentException on execute method call in BotService class: Object type is not supported\n";
    }

    public String tgApiExceptionInServiceVar() {
        return "TelegramApiException on execute method call in BotService class\n";
    }

    public String processMessageException() {
        return "processMessage() IIllegalStateException in CiaoByBot class\n";
    }

    public String addUserIfAbsentException() {
        return "addUserIfAbsent() method in CiaoByBot class\n";
    }

    public String messageProcessingException() {
        return "messageProcessing() method in CiaoByBot class\n";
    }

    public String addPhoneException() {
        return "addPhone() method in CiaoByBot class\n";
    }

    public String closeQueryException() {
        return "closeQuery() method in CiaoByBot class\n";
    }

    public String setDelayException() {
        return "setDelay() method in AbstractState class\n";
    }

    public String sendTextException() {
        return "sendText() method in BotService class\n";
    }

    public String setTimerException() {
        return "setTimer() in AbstractState class\n";
    }

    public String unexpectedCase(String update){
        return "Unexpected case in onUpdateReceived() method in CiaoByBot class\n" + update + "\n";
    }

    public String mapSize(int size) {
        return "Size of the map is " + size;
    }

}
