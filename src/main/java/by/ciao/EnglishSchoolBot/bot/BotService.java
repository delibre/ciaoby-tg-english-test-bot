package by.ciao.EnglishSchoolBot.bot;

import by.ciao.EnglishSchoolBot.dto.UserInfoDTO;
import by.ciao.EnglishSchoolBot.states.*;
import by.ciao.EnglishSchoolBot.states.statesservice.State;

import java.util.HashMap;
import java.util.Map;

public class BotService {
    private final Map<Long, UserInfoDTO> registeredUsers = new HashMap<>();
    private final ServiceCallback serviceCallback;

    BotService (ServiceCallback serviceCallback) {
        this.serviceCallback = serviceCallback;
    }

    protected void startHandler(UserInfoDTO user) {
        State states = new StartState(serviceCallback);
        states.apply("", user);
    }

    protected void getNameAndSurnameHandler(String textMsg, UserInfoDTO user) {
        State states = new GetNameAndSurnameState(serviceCallback);
        states.apply(textMsg, user);
    }

    protected void getPhoneNumberHandler(String textMsg, UserInfoDTO user) {
        State states = new GetPhoneNumberState(serviceCallback);
        states.apply(textMsg, user);
    }

    protected void getReviewHandler(String textMsg, UserInfoDTO user) {
        State states = new GetReviewState(serviceCallback);
        states.apply(textMsg, user);
    }

    protected void testToDoHandler(String textMsg, UserInfoDTO user) {
        State states = new TestToDoState(serviceCallback);
        states.apply(textMsg, user);
    }

    protected void questionToSendHandler(UserInfoDTO user){
        State states = new QuestionToSendState(serviceCallback);
        states.apply("", user);
    }

    protected void checkAnswerHandler(String answer, UserInfoDTO user) {
        State states = new CheckAnswerState(serviceCallback);
        states.apply(answer, user);
    }

    protected void testEndedHandler(UserInfoDTO user) {
        State states = new TestEndedState(serviceCallback);
        states.apply("", user);
    }

    protected void testEndAllHandler(UserInfoDTO user) {
        State states = new EndAllState(serviceCallback);
        states.apply("", user);
    }

    public Map<Long, UserInfoDTO> getRegisteredUsers() {
        return registeredUsers;
    }
}
