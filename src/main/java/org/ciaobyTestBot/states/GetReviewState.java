package org.ciaobyTestBot.states;

import org.ciaobyTestBot.bot.ServiceCallback;
import org.ciaobyTestBot.dto.UserInfoDTO;
import org.ciaobyTestBot.enums.States;
import org.ciaobyTestBot.states.statesservice.AbstractState;

public class GetReviewState extends AbstractState {
    public GetReviewState(ServiceCallback serviceCallback) {
        super(serviceCallback, States.GET_REVIEW);
    }

    @Override
    public void apply(String textMsg, UserInfoDTO user) {
        user.setReview(textMsg);
        user.setState(States.TEST_TODO);

        sendStartButton(user);
    }
}
