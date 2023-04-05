package by.ciao.EnglishSchoolBot.states;

import by.ciao.EnglishSchoolBot.bot.ServiceCallback;
import by.ciao.EnglishSchoolBot.user.User;
import by.ciao.EnglishSchoolBot.enums.StateEnum;
import by.ciao.EnglishSchoolBot.states.statesservice.AbstractState;
import by.ciao.EnglishSchoolBot.states.statesservice.UserMessageHandlerState;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class GetReferralState extends AbstractState implements UserMessageHandlerState {
    public GetReferralState(final ServiceCallback serviceCallback) {
        super(serviceCallback);
    }

    @Override
    public void apply(final String textMsg, final User user) throws TelegramApiException {
        user.setReferral(textMsg);
        user.setState(StateEnum.START_TEST);

        sendStartButton(user);
    }
}
