package by.ciao.states;

import by.ciao.bot.ServiceCallback;
import by.ciao.enums.StateEnum;
import by.ciao.states.statesservice.AbstractState;
import by.ciao.states.statesservice.UserMessageHandlerState;
import by.ciao.user.User;
import by.ciao.utils.BotResponses;
import by.ciao.utils.KeyboardCreator;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class GetFullNameState extends AbstractState implements UserMessageHandlerState {

    public GetFullNameState(final ServiceCallback serviceCallback) {
        super(serviceCallback);
    }

    @Override
    public void apply(final String textMsg, final User user) throws TelegramApiException {
        user.setFullName(textMsg);
        user.setState(StateEnum.GET_PHONE);

        sendPhoneButton(user);
    }

    private void sendPhoneButton(final User user) throws TelegramApiException {
        var sm = createMessage(user.getChatId(), BotResponses.askForPhone());
        sm.setReplyMarkup(KeyboardCreator.createReplyKeyboard(BotResponses.sharePhoneButton(), true));

        getServiceCallback().execute(sm);
    }

}
