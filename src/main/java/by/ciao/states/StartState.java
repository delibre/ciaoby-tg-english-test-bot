package by.ciao.states;

import by.ciao.bot.ServiceCallback;
import by.ciao.user.User;
import by.ciao.enums.StateEnum;
import by.ciao.states.statesservice.AbstractState;
import by.ciao.states.statesservice.UserHandlerState;
import by.ciao.utils.BotResponses;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class StartState extends AbstractState implements UserHandlerState {
    public StartState(final ServiceCallback serviceCallback) {
        super(serviceCallback);
    }

    @Override
    public void apply(final User user) throws TelegramApiException {
        user.setState(StateEnum.GET_FULL_NAME);
        sendText(user.getChatId(), BotResponses.start());
    }
}
