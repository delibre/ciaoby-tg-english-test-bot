package by.ciao.EnglishSchoolBot.states;

import by.ciao.EnglishSchoolBot.bot.ServiceCallback;
import by.ciao.EnglishSchoolBot.userinfo.UserInfo;
import by.ciao.EnglishSchoolBot.enums.StateEnum;
import by.ciao.EnglishSchoolBot.states.statesservice.AbstractState;
import by.ciao.EnglishSchoolBot.states.statesservice.UserMessageHandlerState;

public class GetReferralState extends AbstractState implements UserMessageHandlerState {
    public GetReferralState(final ServiceCallback serviceCallback) {
        super(serviceCallback);
    }

    @Override
    public void apply(final String textMsg, final UserInfo user) {
        user.setGetReferral(textMsg);
        user.setState(StateEnum.START_TEST);

        sendStartButton(user);
    }
}
