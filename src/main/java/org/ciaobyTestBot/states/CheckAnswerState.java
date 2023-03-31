package org.ciaobyTestBot.states;

import org.ciaobyTestBot.bot.ServiceCallback;
import org.ciaobyTestBot.dto.UserInfoDTO;
import org.ciaobyTestBot.enums.States;
import org.ciaobyTestBot.states.statesservice.State;
import org.ciaobyTestBot.states.statesservice.AbstractState;

public class CheckAnswerState extends AbstractState {
    public CheckAnswerState(ServiceCallback serviceCallback) {
        super(serviceCallback, States.CHECK_ANSWER);
    }

    @Override
    public void apply(String textMsg, UserInfoDTO user) {
        user.getTestState().registerAnswer(textMsg);
        State state = new QuestionToSendState(getServiceCallback());
        state.apply(textMsg, user);
    }
}
