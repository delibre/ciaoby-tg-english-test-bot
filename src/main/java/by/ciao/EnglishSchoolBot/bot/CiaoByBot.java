package by.ciao.EnglishSchoolBot.bot;

import by.ciao.EnglishSchoolBot.user.User;
import by.ciao.EnglishSchoolBot.enums.StateEnum;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

public class CiaoByBot extends TelegramLongPollingBot {
    private final BotService service = new BotService((sm, dm, em) -> {
        Optional<Message> msg = Optional.empty();

        try {
            if (sm != null) { msg =  Optional.of(execute(sm)); }
            else if (em != null) { execute(em); }
            else if (dm != null) { execute(dm); }
        } catch (TelegramApiException e) {
            throw new TelegramApiException(e);
        }

        return msg;
    });

    @Override
    public void onUpdateReceived(Update update) {
        if (service.msgHasText(update)) {
            var msg = update.getMessage();
            var id = msg.getChatId();

            if(service.isCheckAnswerState(id)) {
                sendText(service.getRegisteredUsers().get(id).getChatId(), "Отвечать можно только нажав кнопку с одним из вариантов ответа");
                return;
            }

            service.addUserIfAbsent(id, msg);
            messageProcessing(msg.getText(), service.getRegisteredUsers().get(id));

        }  else if (service.hasContact(update)) {
            var id = update.getMessage().getChatId();
            addingPhone(update, id);

        } else if (service.hasCallback(update)) {
            var qry = update.getCallbackQuery();

            messageProcessing(qry.getData(), service.getRegisteredUsers().get(qry.getFrom().getId()));
            closeQuery(qry.getId());
        }
    }

    @Override
    public String getBotUsername() {
        return "testForProd_bot";
    }

    @Override
    public String getBotToken() {
        return "6167400176:AAGg4892WjlM0mMxUgdmdoQXID2X9UPw4lo";
    }

    private void processMessage(String textMsg, User user) throws TelegramApiException {
        if (textMsg.equals("/start")) {
            user.setState(StateEnum.START);
            user.clearTest();
        } else if (user.getState() == null) {
            sendText(user.getChatId(), "Нет такой команды");
            return;
        }

        if (textMsg.equals("Начать тестирование\uD83C\uDFC1")) {
            user.setState(StateEnum.SEND_QUESTION);
            user.clearTest();
        }

        switch (user.getState()) {
            case START:
                service.startHandler(user);
                break;
            case GET_FULL_NAME:
                service.getFullNameHandler(textMsg, user);
                break;
            case GET_PHONE:
                service.getPhoneHandler(textMsg, user);
                break;
            case GET_REFERRAL:
                service.getReferralHandler(textMsg, user);
                break;
            case START_TEST:
                service.startTestHandler(textMsg, user);
                break;
            case SEND_QUESTION:
                service.sendQuestionHandler(user);
                break;
            case CHECK_ANSWER:
                service.checkAnswerHandler(textMsg, user);
                break;
            case TEST_FINISHED:
                service.testFinishedHandler(user);
                break;
            case INFO_SENT:
                service.infoSentHandler(user);
                break;
            default:
                throw new IllegalStateException();
        }
    }

    private void messageProcessing(String textMsg, User user) {
        try {
            processMessage(textMsg, user);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void addingPhone(Update update, Long id) {
        try {
            service.addPhone(update, id);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void closeQuery(String id) {
        try {
            execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(id).build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendText(final Long who, final String what){
        var sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what).build();

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
