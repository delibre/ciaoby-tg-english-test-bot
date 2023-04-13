package by.ciao.EnglishSchoolBot.bot;

import by.ciao.EnglishSchoolBot.user.User;
import by.ciao.EnglishSchoolBot.utils.BotResponses;
import by.ciao.EnglishSchoolBot.utils.LoggerService;
import by.ciao.EnglishSchoolBot.utils.LoggerMessages;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;
import java.util.logging.Level;

public class CiaoByBot extends TelegramLongPollingBot {
    private final BotService service = new BotService((obj) -> {
        Optional<Message> msg = Optional.empty();

        try {
            if (obj instanceof SendMessage) {
                msg = Optional.of(execute((SendMessage) obj));
            } else if (obj instanceof DeleteMessage) {
                execute((DeleteMessage) obj);
            } else if (obj instanceof EditMessageText) {
                execute((EditMessageText) obj);
            } else if (obj instanceof EditMessageReplyMarkup) {
                execute((EditMessageReplyMarkup) obj);
            } else if (obj instanceof AnswerCallbackQuery) {
                execute((AnswerCallbackQuery) obj);
            } else {
                LoggerService.logInfo(Level.SEVERE, LoggerMessages.argumentExceptionInServiceVar(), new IllegalArgumentException());
            }
        } catch (TelegramApiException e) {
            LoggerService.logInfo(Level.SEVERE, LoggerMessages.tgApiExceptionInServiceVar(), e);
        }

        return msg;
    });

    @Override
    public void onUpdateReceived(Update update) {
        if (service.msgHasText(update) && update.getMessage().getChatId() == 5105539803L) {
            broadcast(update.getMessage().getText());

        } else if (service.msgHasText(update)) {
            var msg = update.getMessage();
            var id = msg.getChatId();

            if(service.isCheckAnswerState(id)) {
                service.sendWarning(id);
                return;
            }

            service.addUserIfAbsent(id, msg);
            messageProcessing(msg.getText(), service.getRegisteredUsersMap().get(id));

        }  else if (service.hasContact(update)) {
            var id = update.getMessage().getChatId();
            service.addPhone(update, id);

        } else if (service.hasCallbackAndCorrectState(update)) {
            var qry = update.getCallbackQuery();
            var user = service.getRegisteredUsersMap().get(qry.getFrom().getId());

            catchMessageProcessingException(qry.getData(), user);
            closeQuery(qry.getId());
        } else {
            LoggerService.logInfo(Level.INFO, LoggerMessages.unexpectedCase(), new IllegalStateException());
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

    private void processMessage(String textMsg, User user) throws Exception {
        if (service.startBot(textMsg, user)) {
            return;
        }

        service.startTestIfStartButtonIsPressed(textMsg, user);

        switch (user.getState()) {
            case SEND_QUESTION:
                service.sendQuestionHandler(user);
                break;
            case CHECK_ANSWER:
                service.checkAnswerHandler(textMsg, user);
                break;
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
            case TEST_FINISHED:
                service.testFinishedHandler(user);
                break;
            case INFO_SENT:
                service.infoSentHandler(user);
                break;
            default:
                LoggerService.logInfo(Level.SEVERE, LoggerMessages.processMessageException(), new IllegalStateException());
        }
    }

    private void catchMessageProcessingException(String textMsg, User user) {
        try {
            processMessage(textMsg, user);
        } catch (Exception e) {
            LoggerService.logInfo(Level.SEVERE, LoggerMessages.messageProcessingException(), e);
        }
    }

    private void broadcast(String textMsg) {
        int counter = 0;
        for (User user : service.getRegisteredUsers().values()) {
            var sm = SendMessage.builder()
                    .chatId(user.getChatId().toString())
                    .text(textMsg).build();
            try {
                execute(sm);
                counter++;
            } catch (TelegramApiException ignore) {}
        }
        service.sendText(5105539803L, BotResponses.notificationReceivedBy(counter));
    }
}
