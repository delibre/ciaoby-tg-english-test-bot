package by.ciao.states;

import by.ciao.bot.ServiceCallback;
import by.ciao.controller.RestControllerSingleton;
import by.ciao.enums.StateEnum;
import by.ciao.states.statesservice.AbstractState;
import by.ciao.states.statesservice.UserMessageHandlerState;
import by.ciao.user.User;
import by.ciao.utils.BotResponses;
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
        RestControllerSingleton.getInstance().updateContactInfo(user);

        getServiceCallback().execute(editMessage(user, null, BotResponses.askReferral() + textMsg));
        user.setLastMessage(new Message());  // made for future question edition

        setDelay();
        sendStartButton(user);
    }

}
