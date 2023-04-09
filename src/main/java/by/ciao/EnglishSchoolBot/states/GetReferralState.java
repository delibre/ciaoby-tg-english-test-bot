package by.ciao.EnglishSchoolBot.states;

import by.ciao.EnglishSchoolBot.bot.ServiceCallback;
import by.ciao.EnglishSchoolBot.enums.StateEnum;
import by.ciao.EnglishSchoolBot.states.statesservice.AbstractState;
import by.ciao.EnglishSchoolBot.states.statesservice.UserMessageHandlerState;
import by.ciao.EnglishSchoolBot.user.User;
import by.ciao.EnglishSchoolBot.utils.BotResponses;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class GetReferralState extends AbstractState implements UserMessageHandlerState {
    public GetReferralState(final ServiceCallback serviceCallback) {
        super(serviceCallback);
    }

    @Override
    public void apply(final String textMsg, final User user) throws TelegramApiException {
        user.setReferral(textMsg);
        user.setState(StateEnum.START_TEST);

        getServiceCallback().execute(editMessage(user, null, BotResponses.askReferral() + textMsg));
        user.setLastMessage(new Message());  // made for future question edition

        setDelay(1000);  // Made to humanise bot's responses, so it is not sending lots of messages in one second.
        sendStartButton(user);
    }
}
