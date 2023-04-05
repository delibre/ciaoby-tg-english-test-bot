package by.ciao.EnglishSchoolBot.states.statesservice;

import by.ciao.EnglishSchoolBot.user.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface UserHandlerState {
    void apply(User user) throws TelegramApiException;
}
