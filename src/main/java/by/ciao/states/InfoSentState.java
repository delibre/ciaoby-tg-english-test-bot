package by.ciao.states;

import by.ciao.bot.ServiceCallback;
import by.ciao.states.statesservice.AbstractState;
import by.ciao.states.statesservice.UserHandlerState;
import by.ciao.user.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class InfoSentState extends AbstractState implements UserHandlerState {

    public InfoSentState(final ServiceCallback serviceCallback) {
        super(serviceCallback);
    }

    @Override
    public void apply(final User user) throws TelegramApiException {
        sendText(user.getChatId(), getBotResponses().infoSent());
    }

}
