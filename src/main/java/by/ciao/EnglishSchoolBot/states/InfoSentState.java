package by.ciao.EnglishSchoolBot.states;

import by.ciao.EnglishSchoolBot.bot.ServiceCallback;
import by.ciao.EnglishSchoolBot.states.statesservice.AbstractState;
import by.ciao.EnglishSchoolBot.user.User;
import by.ciao.EnglishSchoolBot.states.statesservice.UserHandlerState;
import by.ciao.EnglishSchoolBot.utils.BotResponses;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class InfoSentState extends AbstractState implements UserHandlerState {
    public InfoSentState(final ServiceCallback serviceCallback) {
        super(serviceCallback);
    }

    @Override
    public void apply(final User user) throws TelegramApiException {
        sendText(user.getChatId(), BotResponses.infoSent());
    }
}
