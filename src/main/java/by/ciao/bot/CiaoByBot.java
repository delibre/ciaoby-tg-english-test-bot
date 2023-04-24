package by.ciao.bot;

import by.ciao.user.User;
import by.ciao.utils.BotResponses;
import by.ciao.utils.LoggerMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class CiaoByBot extends TelegramLongPollingBot {

    @Value("bot_username")
    private String botUsername;
    @Value("bot_token")
    private String botToken;
    @Value("admin_id")
    private String adminId;
    @Value("tech_admin_id")
    private String techAdminId;
    private static final Logger log = LoggerFactory.getLogger(CiaoByBot.class);
    private BotService service;

    @Autowired
    void setService(BotService service) {
        this.service = service;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (service.isMsgFromAdmin(update)) {
            broadcast(update.getMessage().getText());

        } else if (service.msgHasText(update)) {
            var msg = update.getMessage();
            var chatId = msg.getChatId();

            if(service.isCheckAnswerState(chatId)) {
                service.sendWarning(chatId);
                return;
            }

            service.addUserIfAbsent(chatId, msg.getFrom().getUserName());
            catchMessageProcessingException(msg.getText(), service.getRegisteredUsersMap().get(chatId));

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
        return "slava_local_test_bot";
    }

    @Override
    public String getBotToken() {
        return "6070947188:AAHgnO_J7nkqq5tsIQtKGFIMYNZw96rxm0M";
    }

    private void processMessage(String textMsg, User user) throws Exception {

        if (service.startBot(textMsg, user)) {
            return;
        }

        service.startTestIfStartButtonPressed(textMsg, user);

        switch (user.getState()) {
            case SEND_QUESTION -> service.sendQuestionHandler(user);
            case CHECK_ANSWER -> service.checkAnswerHandler(textMsg, user);
            case START -> service.startHandler(user);
            case GET_FULL_NAME -> service.getFullNameHandler(textMsg, user);
            case GET_PHONE -> service.getPhoneHandler(textMsg, user);
            case GET_REFERRAL -> service.getReferralHandler(textMsg, user);
            case START_TEST -> service.startTestHandler(textMsg, user);
            case TEST_FINISHED -> service.testFinishedHandler(user);
            case INFO_SENT -> service.infoSentHandler(user);
            default -> {
                log.error(LoggerMessages.processMessageException(), new IllegalStateException());
                sendToTechAdmin(LoggerMessages.processMessageException());
            }
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
        service.sendText(Long.parseLong(adminId), BotResponses.notificationReceivedBy(counter));
    }

    private void sendToTechAdmin(final String textMsg) {
        var sm = SendMessage.builder()
                .chatId(techAdminId)
                .text(textMsg).build();

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            log.error(LoggerMessages.sendTextException(), e);
        }
    }
}
