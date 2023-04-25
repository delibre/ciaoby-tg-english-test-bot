package by.ciao.bot;

import by.ciao.controller.RestController;
import by.ciao.enums.StateEnum;
import by.ciao.user.User;
import by.ciao.utils.AppConfig;
import by.ciao.utils.BotResponses;
import by.ciao.utils.LoggerMessages;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class BotService {

    private final Map<Long, User> registeredUsersMap;
    private final ServiceCallback serviceCallback;
    private static final Logger log = LoggerFactory.getLogger(BotService.class);

    BotService(final ServiceCallback serviceCallback) {
        this.serviceCallback = serviceCallback;
        this.registeredUsersMap = new HashMap<>();
    }

    // onUpdateReceived service methods
    boolean isMsgFromAdmin(Update update) {
        return msgHasText(update) && update.getMessage().getChatId() == Long.parseLong(AppConfig.getProperty("admin_id"));
    }

    void broadcast(String textMsg) {
        int counter = 0;
        for (User user : registeredUsersMap.values()) {
            var sm = SendMessage.builder()
                    .chatId(user.getChatId().toString())
                    .text(textMsg).build();
            try {
                serviceCallback.execute(sm);
                counter++;
            } catch (TelegramApiException ignore) {}
        }
        sendText(Long.parseLong(AppConfig.getProperty("admin_id")), BotResponses.notificationReceivedBy(counter));
    }

    boolean msgHasText(final Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    boolean isCheckAnswerState(final Long id) {
        return registeredUsersMap.containsKey(id) && registeredUsersMap.get(id).getState() == StateEnum.CHECK_ANSWER;
    }

    void sendWarning(Long id) {
        sendText(registeredUsersMap.get(id).getChatId(), BotResponses.questionAnsweringWarning());
    }

    void addUserIfAbsent(final Long chatId, final String username) {

        try {
            User user = RestController.getInstance().getUserByChatId(chatId);

            if (user == null) {
                registeredUsersMap.putIfAbsent(chatId, new User(chatId, username));
                RestController.getInstance().addUserToDB(registeredUsersMap.get(chatId));
                // sending data to tech admin
                log.info(LoggerMessages.mapSize(registeredUsersMap.size()));
                sendText(Long.parseLong(AppConfig.getProperty("tech_admin_id")), LoggerMessages.mapSize(registeredUsersMap.size()));
            } else {
                registeredUsersMap.putIfAbsent(chatId, user);
            }

        } catch (Exception e) {
            log.error(LoggerMessages.addUserIfAbsentException(), new RuntimeException(e));
            sendText(Long.parseLong(AppConfig.getProperty("tech_admin_id")), e.toString());
        }
    }

    boolean hasContact(final Update update) {
        return update.hasMessage() && update.getMessage().getContact() != null;
    }

    boolean hasCallbackAndCorrectState(final Update update) {
        var updateHasCallbackQuery = update.hasCallbackQuery();
        var userExists = registeredUsersMap.containsKey(update.getCallbackQuery().getFrom().getId());
        var userStateIsGetReferral = registeredUsersMap.get(update.getCallbackQuery().getFrom().getId()).getState() == StateEnum.GET_REFERRAL;
        var userStateIsCheckAnswer = registeredUsersMap.get(update.getCallbackQuery().getFrom().getId()).getState() == StateEnum.CHECK_ANSWER;

        return updateHasCallbackQuery && userExists && (userStateIsGetReferral || userStateIsCheckAnswer);
    }

    void closeQuery(final String id) {
        try {
            serviceCallback.execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(id).build());
        } catch (TelegramApiException e) {
            log.error(LoggerMessages.closeQueryException(), e);
            sendText(Long.parseLong(AppConfig.getProperty("tech_admin_id")), e.toString());
        }
    }

    // processMessage service methods
    boolean startBot(final String textMsg, final User user) throws Exception {
        if (textMsg.equals("/start")) {
            user.setState(StateEnum.START);
            user.clearTest();
        } else if (user.getState() == StateEnum.NEW_USER) {
            sendText(user.getChatId(), BotResponses.noSuchCommand());
            return true;
        }

        return false;
    }

    void startTestIfStartButtonPressed(final String textMsg, final User user) {
        if (textMsg.equals("Начать тестирование\uD83C\uDFC1") && user.isUserDataCollected()) {
            user.setState(StateEnum.START_TEST);
        }
    }

    void sendText(final Long id, final String textMsg) {
        var sm = SendMessage.builder()
                .chatId(id)
                .text(textMsg).build();

        try {
            serviceCallback.execute(sm);
        } catch (TelegramApiException e) {
            log.error(LoggerMessages.sendTextException(), e);
        }
    }

}
