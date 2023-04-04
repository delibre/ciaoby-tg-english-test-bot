package by.ciao.EnglishSchoolBot.bot;

import by.ciao.EnglishSchoolBot.dto.UserInfoDTO;
import by.ciao.EnglishSchoolBot.states.*;
import by.ciao.EnglishSchoolBot.states.statesservice.State;

import java.util.HashMap;
import java.util.Map;

public class BotService {
    private final Map<Long, UserInfoDTO> registeredUsers = new HashMap<>();
    private final ServiceCallback serviceCallback;

    protected BotService(final ServiceCallback serviceCallback) {
        this.serviceCallback = serviceCallback;
    }

    protected void startHandler(final UserInfoDTO user) {
        State states = new StartState(serviceCallback);
        states.apply("", user);
    }

    protected void getNameAndSurnameHandler(final String textMsg, final UserInfoDTO user) {
        State states = new GetNameAndSurnameState(serviceCallback);
        states.apply(textMsg, user);
    }

    protected void getPhoneNumberHandler(final String textMsg, final UserInfoDTO user) {
        State states = new GetPhoneNumberState(serviceCallback);
        states.apply(textMsg, user);
    }

    protected void getReviewHandler(final String textMsg, final UserInfoDTO user) {
        State states = new GetReviewState(serviceCallback);
        states.apply(textMsg, user);
    }

    protected void testToDoHandler(final String textMsg, final UserInfoDTO user) {
        State states = new TestToDoState(serviceCallback);
        states.apply(textMsg, user);
    }

    protected void questionToSendHandler(final UserInfoDTO user){
        State states = new QuestionToSendState(serviceCallback);
        states.apply("", user);
    }

    protected void checkAnswerHandler(final String answer, final UserInfoDTO user) {
        State states = new CheckAnswerState(serviceCallback);
        states.apply(answer, user);
    }

    protected void testEndedHandler(final UserInfoDTO user) {
        State states = new TestEndedState(serviceCallback);
        states.apply("", user);
    }

    protected void testEndAllHandler(final UserInfoDTO user) {
        State states = new EndAllState(serviceCallback);
        states.apply("", user);
    }

    protected Map<Long, UserInfoDTO> getRegisteredUsers() {
        return registeredUsers;
    }
}
