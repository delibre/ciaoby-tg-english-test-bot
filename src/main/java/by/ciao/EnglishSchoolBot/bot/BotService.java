package by.ciao.EnglishSchoolBot.bot;

import by.ciao.EnglishSchoolBot.enums.StateEnum;
import by.ciao.EnglishSchoolBot.states.*;
import by.ciao.EnglishSchoolBot.states.statesservice.UserHandlerState;
import by.ciao.EnglishSchoolBot.states.statesservice.UserMessageHandlerState;
import by.ciao.EnglishSchoolBot.user.User;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

@Getter
public class BotService {

    private final Map<Long, User> registeredUsersMap;
    private final ServiceCallback serviceCallback;

    BotService(final ServiceCallback serviceCallback) {
        this.serviceCallback = serviceCallback;
        this.registeredUsersMap = new HashMap<>();
    }

    boolean msgHasText(final Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    boolean isCheckAnswerState(final Long id) {
        return getRegisteredUsersMap().containsKey(id) && getRegisteredUsersMap().get(id).getState() == StateEnum.CHECK_ANSWER;
    }

    boolean hasContact(final Update update) {
        return update.hasMessage() && update.getMessage().getContact() != null;
    }

    boolean hasCallback(final Update update) {
        return update.hasCallbackQuery() && getRegisteredUsersMap().containsKey(update.getCallbackQuery().getFrom().getId());
    }

    void addUserIfAbsent(final Long id, final Message msg) throws Exception {
        getRegisteredUsersMap().putIfAbsent(id, new User(id, msg.getFrom().getUserName()));
    }

    void addPhone(final Update update, final Long id) throws Exception {
        getPhoneHandler(update.getMessage().getContact().getPhoneNumber(), getRegisteredUsersMap().get(id));
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
