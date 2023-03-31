package org.ciaobyTestBot.states;

import org.ciaobyTestBot.bot.ServiceCallback;
import org.ciaobyTestBot.dto.UserInfoDTO;
import org.ciaobyTestBot.enums.States;
import org.ciaobyTestBot.states.statesservice.State;
import org.ciaobyTestBot.states.statesservice.AbstractState;

import java.util.Objects;

public class TestToDoState extends AbstractState {
    public TestToDoState(ServiceCallback serviceCallback) {
        super(serviceCallback, States.TEST_TODO);
    }

    @Override
    public void apply(String textMsg, UserInfoDTO user) {
        if (Objects.equals(textMsg, "Начать тестирование\uD83C\uDFC1")) {
            user.setState(States.QUESTION_TO_SEND);
            user.clearTest();

            State state = new QuestionToSendState(getServiceCallback());
            state.apply(textMsg, user);
        }
        else sendStartButton(user);
    }
}
