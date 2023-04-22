package by.ciao.EnglishSchoolBot.bot;

import by.ciao.EnglishSchoolBot.controller.RestController;
import by.ciao.EnglishSchoolBot.enums.StateEnum;
import by.ciao.EnglishSchoolBot.states.*;
import by.ciao.EnglishSchoolBot.states.statesservice.UserHandlerState;
import by.ciao.EnglishSchoolBot.states.statesservice.UserMessageHandlerState;
import by.ciao.EnglishSchoolBot.user.User;
import by.ciao.EnglishSchoolBot.user.UserDTO;
import by.ciao.EnglishSchoolBot.utils.AppConfig;
import by.ciao.EnglishSchoolBot.utils.BotResponses;
import by.ciao.EnglishSchoolBot.utils.LoggerMessages;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.NumberFormat;
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

    boolean msgHasText(final Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    boolean isCheckAnswerState(final Long id) {
        return registeredUsersMap.containsKey(id) && registeredUsersMap.get(id).getState() == StateEnum.CHECK_ANSWER;
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

    void sendWarning(Long id) {
        sendText(registeredUsersMap.get(id).getChatId(), BotResponses.questionAnsweringWarning());
    }

    void addUserIfAbsent(final Long chatId, final String username) {
        try {
            UserDTO dto = RestController.getInstance().getUserByChatId(chatId);
            if (dto == null) {
                registeredUsersMap.putIfAbsent(chatId, new User(chatId, username));
                RestController.getInstance().addUserToDB(registeredUsersMap.get(chatId));
                log.info(LoggerMessages.mapSize(registeredUsersMap.size()));
                // sending data to tech admin
                sendText(Long.parseLong(AppConfig.getProperty("tech_admin_id")), LoggerMessages.mapSize(registeredUsersMap.size()));
                sendText(Long.parseLong(AppConfig.getProperty("tech_admin_id")), getAppLoad().toString());
            } else {
                registeredUsersMap.putIfAbsent(chatId, new User(dto.getChatId(), dto.getFullName(), dto.getUsername(), dto.getPhone(), dto.getReferral()));
            }
        } catch (Exception e) {
            log.error(LoggerMessages.addUserIfAbsentException(), new RuntimeException(e));
            sendText(Long.parseLong(AppConfig.getProperty("tech_admin_id")), e.toString());
        }
    }

    StringBuilder getAppLoad() {
        Runtime runtime = Runtime.getRuntime();

        NumberFormat format = NumberFormat.getInstance();

        StringBuilder sb = new StringBuilder();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        sb.append("free memory: ").append(format.format(freeMemory / 1024)).append("\n\n");
        sb.append("allocated memory: ").append(format.format(allocatedMemory / 1024)).append("\n\n");
        sb.append("max memory: ").append(format.format(maxMemory / 1024)).append("\n\n");
        sb.append("total free memory: ").append(format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024)).append("\n\n");

        return sb;
    }

    void addPhone(final Update update, final Long id) {
        try {
            getPhoneHandler(update.getMessage().getContact().getPhoneNumber(), registeredUsersMap.get(id));
        } catch (Exception e) {
            log.error(LoggerMessages.addPhoneException(), e);
            sendText(Long.parseLong(AppConfig.getProperty("tech_admin_id")), e.toString());
        }
    }

    boolean isMsgFromAdmin(Update update) {
        return msgHasText(update) && update.getMessage().getChatId() == Long.parseLong(AppConfig.getProperty("admin_id"));
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
            sendText(Long.parseLong(AppConfig.getProperty("tech_admin_id")), e.toString());
        }
    }

    void startHandler(final User user) throws Exception {
        UserHandlerState state = new StartState(serviceCallback);
        state.apply(user);
    }

    void getFullNameHandler(final String textMsg, final User user) throws Exception {
        UserMessageHandlerState state = new GetFullNameState(serviceCallback);
        state.apply(textMsg, user);
    }

    void getPhoneHandler(final String textMsg, final User user) throws Exception {
        UserMessageHandlerState state = new GetPhoneState(serviceCallback);
        state.apply(textMsg, user);
    }

    void getReferralHandler(final String textMsg, final User user) throws Exception {
        UserMessageHandlerState state = new GetReferralState(serviceCallback);
        state.apply(textMsg, user);
    }

    void startTestHandler(final String textMsg, final User user) throws Exception {
        UserMessageHandlerState state = new StartTestState(serviceCallback);
        state.apply(textMsg, user);
    }

    void sendQuestionHandler(final User user) throws Exception {
        UserHandlerState state = new SendQuestionState(serviceCallback);
        state.apply(user);
    }

    void checkAnswerHandler(final String answer, final User user) throws Exception {
        UserMessageHandlerState state = new CheckAnswerState(serviceCallback);
        state.apply(answer, user);
    }

    void testFinishedHandler(final User user) throws Exception {
        UserHandlerState state = new TestFinishedState(serviceCallback);
        state.apply(user);
        registeredUsersMap.remove(user.getChatId());
    }

    void infoSentHandler(final User user) throws Exception {
        UserHandlerState state = new InfoSentState(serviceCallback);
        state.apply(user);
    }

    Map<Long, User> getRegisteredUsersMap() {
        return registeredUsersMap;
    }
}
