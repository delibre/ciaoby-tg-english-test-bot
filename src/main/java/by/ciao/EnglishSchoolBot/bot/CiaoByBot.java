package by.ciao.EnglishSchoolBot.bot;

import by.ciao.EnglishSchoolBot.enums.StateEnum;
import by.ciao.EnglishSchoolBot.user.User;
import by.ciao.EnglishSchoolBot.utils.BotResponses;
import by.ciao.EnglishSchoolBot.utils.LoggerMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

public class CiaoByBot extends TelegramLongPollingBot {
    private final Logger log = LoggerFactory.getLogger(CiaoByBot.class);
    private final BotService service = new BotService((obj) -> {
        Optional<Message> msg = Optional.empty();

        try {
            if (obj instanceof SendMessage) {
                msg = Optional.of(execute((SendMessage) obj));
            } else if (obj instanceof DeleteMessage) {
                execute((DeleteMessage) obj);
            } else if (obj instanceof EditMessageText) {
                execute((EditMessageText) obj);
            } else {
                log.error(LoggerMessages.argumentExceptionInServiceVar(), new IllegalArgumentException());
            }
        } catch (TelegramApiException e) {
            log.error(LoggerMessages.tgApiExceptionInServiceVar(), e);
        }

        return msg;
    });

    @Override
    public void onUpdateReceived(Update update) {
        if (service.msgHasText(update)) {
            var msg = update.getMessage();
            var id = msg.getChatId();

            if(service.isCheckAnswerState(id)) {
                sendWarning(id);
                return;
            }

            addUserIfAbsent(id, msg);
            catchMessageProcessingException(msg.getText(), service.getRegisteredUsersMap().get(id));

        }  else if (service.hasContact(update)) {
            var id = update.getMessage().getChatId();
            addPhone(update, id);

        } else if (service.hasCallback(update)) {
            var qry = update.getCallbackQuery();
            var user = service.getRegisteredUsersMap().get(qry.getFrom().getId());

            catchMessageProcessingException(qry.getData(), user);
            closeQuery(qry.getId());
        } else {
            log.info(LoggerMessages.unexpectedCase());
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
        if (startBot(textMsg, user)) {
            return;
        }

        startTestIfStartButtonIsPressed(textMsg, user);

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
                log.error(LoggerMessages.processMessageException(), new IllegalStateException());
        }
    }

    private void sendWarning(final Long id) {
        try {
            sendText(service.getRegisteredUsersMap().get(id).getChatId(), BotResponses.questionAnsweringWarning());
        } catch (TelegramApiException e) {
            log.error(LoggerMessages.sendWarningException(), e);
        }
    }

    private void addUserIfAbsent(final Long id, final Message msg) {
        try {
            service.addUserIfAbsent(id, msg);
        } catch (Exception e) {
            log.error(LoggerMessages.addUserIfAbsentException(), new RuntimeException(e));
        }
    }

    private void catchMessageProcessingException(final String textMsg, final User user) {
        try {
            processMessage(textMsg, user);
        } catch (Exception e) {
            log.error(LoggerMessages.messageProcessingException(), e);
        }
    }

    private void addPhone(final Update update, final Long id) {
        try {
            service.addPhone(update, id);
        } catch (Exception e) {
            log.error(LoggerMessages.addPhoneException(), e);
        }
    }

    private void closeQuery(final String id) {
        try {
            execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(id).build());
        } catch (TelegramApiException e) {
            log.error(LoggerMessages.closeQueryException(), e);
        }
    }

    private boolean startBot(final String textMsg, final User user) throws Exception {
        if (textMsg.equals("/start")) {
            user.setState(StateEnum.START);
            user.clearTest();
        } else if (user.getState() == null) {
            sendText(user.getChatId(), BotResponses.noSuchCommand());
            return true;
        }

        return false;
    }

    private void startTestIfStartButtonIsPressed(final String textMsg, final User user) throws Exception {
        if (textMsg.equals("Начать тестирование\uD83C\uDFC1")) {
            user.clearTest();
            user.setState(StateEnum.SEND_QUESTION);
        }
    }

    private void sendText(final Long id, final String textMsg) throws TelegramApiException {
        var sm = SendMessage.builder()
                .chatId(id.toString())
                .text(textMsg).build();

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            log.error(LoggerMessages.sendTextException(), e);
        }
    }
}
