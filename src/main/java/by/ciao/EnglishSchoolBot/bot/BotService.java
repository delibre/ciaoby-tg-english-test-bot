package by.ciao.EnglishSchoolBot.bot;

import by.ciao.EnglishSchoolBot.enums.StateEnum;
import by.ciao.EnglishSchoolBot.states.*;
import by.ciao.EnglishSchoolBot.states.statesservice.UserHandlerState;
import by.ciao.EnglishSchoolBot.states.statesservice.UserMessageHandlerState;
import by.ciao.EnglishSchoolBot.user.User;
import by.ciao.EnglishSchoolBot.utils.BotResponses;
import by.ciao.EnglishSchoolBot.utils.LoggerMessages;
import lombok.Getter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class BotService {

    private final Map<Long, User> registeredUsersMap;
    private final ServiceCallback serviceCallback;
    private final Logger log;
    private final PropertiesConfiguration config = new PropertiesConfiguration("application.properties");

    BotService(final ServiceCallback serviceCallback) throws ConfigurationException {
        this.serviceCallback = serviceCallback;
        this.registeredUsersMap = new HashMap<>();
        this.log = LoggerFactory.getLogger(BotService.class);
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
        return update.hasCallbackQuery() && registeredUsersMap.containsKey(update.getCallbackQuery().getFrom().getId())
                && (registeredUsersMap.get(update.getCallbackQuery().getFrom().getId()).getState() == StateEnum.GET_REFERRAL ||
                registeredUsersMap.get(update.getCallbackQuery().getFrom().getId()).getState() == StateEnum.CHECK_ANSWER);
    }

    void sendWarning(Long id) {
        sendText(registeredUsersMap.get(id).getChatId(), BotResponses.questionAnsweringWarning());
    }

    void addUserIfAbsent(final Long id, final Message msg) {
        try {
            if (registeredUsersMap.putIfAbsent(id, new User(id, msg.getFrom().getUserName())) == null) {
                log.info(LoggerMessages.mapSize(registeredUsersMap.size()));
            }
        } catch (Exception e) {
            log.error(LoggerMessages.addUserIfAbsentException(), new RuntimeException(e));
            sendText(config.getLong("admin_id"), e.toString());
        }
    }

    void addPhone(final Update update, final Long id) {
        try {
            getPhoneHandler(update.getMessage().getContact().getPhoneNumber(), registeredUsersMap.get(id));
        } catch (Exception e) {
            log.error(LoggerMessages.addPhoneException(), e);
            sendText(config.getLong("admin_id"), e.toString());
        }
    }

    void closeQuery(final String id) {
        try {
            serviceCallback.execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(id).build());
        } catch (TelegramApiException e) {
            log.error(LoggerMessages.closeQueryException(), e);
            sendText(config.getLong("admin_id"), e.toString());
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

    void startTestIfStartButtonIsPressed(final String textMsg, final User user) {
        if (textMsg.equals("Начать тестирование\uD83C\uDFC1") && user.isUserDataCollected()) {
            user.setState(StateEnum.START_TEST);
        }
    }

    void sendText(final Long id, final String textMsg) {
        var sm = SendMessage.builder()
                .chatId(id.toString())
                .text(textMsg).build();

        try {
            serviceCallback.execute(sm);
        } catch (TelegramApiException e) {
            log.error(LoggerMessages.sendTextException(), e);
            sendText(config.getLong("admin_id"), e.toString());
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
    }

    void infoSentHandler(final User user) throws Exception {
        UserHandlerState state = new InfoSentState(serviceCallback);
        state.apply(user);
    }

    Map<Long, User> getRegisteredUsersMap() {
        return registeredUsersMap;
    }
}
