package by.ciao.EnglishSchoolBot.bot;

import by.ciao.EnglishSchoolBot.enums.StateEnum;
import by.ciao.EnglishSchoolBot.states.*;
import by.ciao.EnglishSchoolBot.states.statesservice.UserHandlerState;
import by.ciao.EnglishSchoolBot.states.statesservice.UserMessageHandlerState;
import by.ciao.EnglishSchoolBot.user.User;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

public class BotService {
    private final Map<Long, User> registeredUsers = new HashMap<>();
    private final ServiceCallback serviceCallback;

    BotService(final ServiceCallback serviceCallback) {
        this.serviceCallback = serviceCallback;
    }

    boolean msgHasText(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    boolean isCheckAnswerState(Long id) {
        return getRegisteredUsers().containsKey(id) && getRegisteredUsers().get(id).getState() == StateEnum.CHECK_ANSWER;
    }

    boolean hasContact(Update update) {
        return update.hasMessage() && update.getMessage().getContact() != null;
    }

    boolean hasCallback(Update update) {
        return update.hasCallbackQuery() && getRegisteredUsers().containsKey(update.getCallbackQuery().getFrom().getId());
    }

    void addUserIfAbsent(Long id, Message msg) {
        getRegisteredUsers().putIfAbsent(id, new User(id, msg.getFrom().getUserName()));
    }

    void startHandler(final User user) throws TelegramApiException {
        UserHandlerState state = new StartState(serviceCallback);
        state.apply(user);
    }

    void getFullNameHandler(final String textMsg, final User user) throws TelegramApiException {
        UserMessageHandlerState state = new GetFullNameState(serviceCallback);
        state.apply(textMsg, user);
    }

    void getPhoneHandler(final String textMsg, final User user) throws TelegramApiException {
        UserMessageHandlerState state = new GetPhoneState(serviceCallback);
        state.apply(textMsg, user);
    }

    void getReferralHandler(final String textMsg, final User user) throws TelegramApiException {
        UserMessageHandlerState state = new GetReferralState(serviceCallback);
        state.apply(textMsg, user);
    }

    void startTestHandler(final String textMsg, final User user) throws TelegramApiException {
        UserMessageHandlerState state = new StartTestState(serviceCallback);
        state.apply(textMsg, user);
    }

    void sendQuestionHandler(final User user) throws TelegramApiException {
        UserHandlerState state = new SendQuestionState(serviceCallback);
        state.apply(user);
    }

    void checkAnswerHandler(final String answer, final User user) throws TelegramApiException {
        UserMessageHandlerState state = new CheckAnswerState(serviceCallback);
        state.apply(answer, user);
    }

    void testFinishedHandler(final User user) throws TelegramApiException {
        UserHandlerState state = new TestFinishedState(serviceCallback);
        state.apply(user);
    }

    void infoSentHandler(final User user) throws TelegramApiException {
        UserHandlerState state = new InfoSentState(serviceCallback);
        state.apply(user);
    }

    Map<Long, User> getRegisteredUsers() {
        return registeredUsers;
    }
}
