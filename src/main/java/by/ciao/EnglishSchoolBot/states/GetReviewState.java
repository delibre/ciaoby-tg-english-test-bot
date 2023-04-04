package by.ciao.EnglishSchoolBot.states;

import by.ciao.EnglishSchoolBot.bot.ServiceCallback;
import by.ciao.EnglishSchoolBot.dto.UserInfoDTO;
import by.ciao.EnglishSchoolBot.enums.States;
import by.ciao.EnglishSchoolBot.states.statesservice.AbstractState;
import by.ciao.EnglishSchoolBot.states.statesservice.UserMessageHandlerState;

public class GetReviewState extends AbstractState implements UserMessageHandlerState {
    public GetReviewState(final ServiceCallback serviceCallback) {
        super(serviceCallback, States.GET_REVIEW);
    }

    @Override
    public void apply(final String textMsg, final UserInfoDTO user) {
        user.setReview(textMsg);
        user.setState(States.TEST_TODO);

        sendStartButton(user);
    }
}
