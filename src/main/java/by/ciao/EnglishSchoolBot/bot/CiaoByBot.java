package by.ciao.EnglishSchoolBot.bot;

import by.ciao.EnglishSchoolBot.user.User;
import by.ciao.EnglishSchoolBot.utils.BotResponses;
import by.ciao.EnglishSchoolBot.utils.LoggerMessages;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class CiaoByBot extends TelegramLongPollingBot {
    private final PropertiesConfiguration config = new PropertiesConfiguration("application.properties");
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
            } else if (obj instanceof EditMessageReplyMarkup) {
                execute((EditMessageReplyMarkup) obj);
            } else if (obj instanceof AnswerCallbackQuery) {
                execute((AnswerCallbackQuery) obj);
            } else {
                log.error(LoggerMessages.argumentExceptionInServiceVar(), new IllegalArgumentException());
                sendToTechAdmin(new IllegalArgumentException().toString());
            }
        } catch (TelegramApiException e) {
            log.error(LoggerMessages.tgApiExceptionInServiceVar(), e);
            sendToTechAdmin(e.toString());
        }

        return msg;
    });

    public CiaoByBot() throws ConfigurationException {
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (service.msgHasText(update) && update.getMessage().getChatId() == config.getLong("admin_id")) {
            broadcast(update.getMessage().getText());

        } else if (service.msgHasText(update)) {
            var msg = update.getMessage();
            var id = msg.getChatId();

            if(service.isCheckAnswerState(id)) {
                service.sendWarning(id);
                return;
            }

            service.addUserIfAbsent(id, msg);
            catchMessageProcessingException(msg.getText(), service.getRegisteredUsersMap().get(id));

        }  else if (service.hasContact(update)) {
            var id = update.getMessage().getChatId();
            service.addPhone(update, id);

        } else if (service.hasCallbackAndCorrectState(update)) {
            var qry = update.getCallbackQuery();
            var user = service.getRegisteredUsersMap().get(qry.getFrom().getId());

            catchMessageProcessingException(qry.getData(), user);
            service.closeQuery(qry.getId());
        } else {
            log.info(LoggerMessages.unexpectedCase(update.toString()));
            sendToTechAdmin(LoggerMessages.unexpectedCase(update.toString()));
        }
    }

    @Override
    public String getBotUsername() {
        return config.getString("bot_username");
    }

    @Override
    public String getBotToken() {
        return config.getString("bot_token");
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
                log.error(LoggerMessages.processMessageException(), new IllegalStateException());
                sendToTechAdmin(LoggerMessages.processMessageException());
        }
    }

    private void catchMessageProcessingException(final String textMsg, final User user) {
        try {
            processMessage(textMsg, user);
        } catch (Exception e) {
            log.error(LoggerMessages.messageProcessingException(), e);
            sendToTechAdmin(e.toString());
        }
    }

    private void broadcast(String textMsg) {
        int counter = 0;
        for (User user : service.getRegisteredUsersMap().values()) {
            var sm = SendMessage.builder()
                    .chatId(user.getChatId().toString())
                    .text(textMsg).build();
            try {
                execute(sm);
                counter++;
            } catch (TelegramApiException ignore) {}
        }
        service.sendText(config.getLong("admin_id"), BotResponses.notificationReceivedBy(counter));
    }

    private void sendToTechAdmin(final String textMsg) {
        var sm = SendMessage.builder()
                .chatId(config.getLong("tech_admin_id"))
                .text(textMsg).build();

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            log.error(LoggerMessages.sendTextException(), e);
        }
    }
}
