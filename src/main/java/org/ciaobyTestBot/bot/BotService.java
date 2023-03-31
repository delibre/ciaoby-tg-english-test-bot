package org.ciaobyTestBot.bot;

import org.ciaobyTestBot.dto.UserInfoDTO;
import org.ciaobyTestBot.states.*;
import org.ciaobyTestBot.states.statesservice.State;
import org.jvnet.hk2.annotations.Service;

import java.util.ArrayList;
import java.util.Objects;

@Service
public class BotService {
    protected final ArrayList<UserInfoDTO> dto = new ArrayList<>();
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

    protected UserInfoDTO getUserById(Long id) {
        for (var userInfoDTO : dto)
            if (Objects.equals(userInfoDTO.getChatId(), id))
                return userInfoDTO;

        throw new RuntimeException("No such user");
    }

    protected boolean contains(Long id) {
        var containsId = false;

        for(var userInfoDTO : dto)
            if(Objects.equals(userInfoDTO.getChatId(), id)) {
                containsId = true;
                break;
            }

        return containsId;
    }

}
