package by.ciao.EnglishSchoolBot.bot;

import by.ciao.EnglishSchoolBot.userinfo.UserInfo;
import by.ciao.EnglishSchoolBot.states.*;
import by.ciao.EnglishSchoolBot.states.statesservice.UserMessageHandlerState;
import by.ciao.EnglishSchoolBot.states.statesservice.UserHandlerState;

import java.util.HashMap;
import java.util.Map;

public class BotService {
    private final Map<Long, UserInfo> registeredUsers = new HashMap<>();
    private final ServiceCallback serviceCallback;

    protected BotService(final ServiceCallback serviceCallback) {
        this.serviceCallback = serviceCallback;
    }

    protected void startHandler(final UserInfo user) {
        UserHandlerState state = new StartState(serviceCallback);
        state.apply(user);
    }

    protected void getFullNameHandler(final String textMsg, final UserInfo user) {
        UserMessageHandlerState state = new GetFullNameState(serviceCallback);
        state.apply(textMsg, user);
    }

    protected void getPhoneHandler(final String textMsg, final UserInfo user) {
        UserMessageHandlerState state = new GetPhoneState(serviceCallback);
        state.apply(textMsg, user);
    }

    protected void getReferralHandler(final String textMsg, final UserInfo user) {
        UserMessageHandlerState state = new GetReferralState(serviceCallback);
        state.apply(textMsg, user);
    }

    protected void startTestHandler(final String textMsg, final UserInfo user) {
        UserMessageHandlerState state = new StartTestState(serviceCallback);
        state.apply(textMsg, user);
    }

    protected void sendQuestionHandler(final UserInfo user){
        UserHandlerState state = new SendQuestionState(serviceCallback);
        state.apply(user);
    }

    protected void checkAnswerHandler(final String answer, final UserInfo user) {
        UserMessageHandlerState state = new CheckAnswerState(serviceCallback);
        state.apply(answer, user);
    }

    protected void testFinishedHandler(final UserInfo user) {
        UserHandlerState state = new TestFinishedState(serviceCallback);
        state.apply(user);
    }

    protected void infoSentHandler(final UserInfo user) {
        UserHandlerState state = new InfoSentState(serviceCallback);
        state.apply(user);
    }

    protected Map<Long, UserInfo> getRegisteredUsers() {
        return registeredUsers;
    }
}
