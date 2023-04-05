package by.ciao.EnglishSchoolBot.states.statesservice;

import by.ciao.EnglishSchoolBot.user.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface UserMessageHandlerState {
    void apply(final String textMsg, final User user) throws TelegramApiException;
}
