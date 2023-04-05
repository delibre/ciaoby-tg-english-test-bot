package by.ciao.EnglishSchoolBot.states;

import by.ciao.EnglishSchoolBot.bot.ServiceCallback;
import by.ciao.EnglishSchoolBot.states.statesservice.AbstractState;
import by.ciao.EnglishSchoolBot.user.User;
import by.ciao.EnglishSchoolBot.states.statesservice.UserHandlerState;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class InfoSentState extends AbstractState implements UserHandlerState {
    public InfoSentState(final ServiceCallback serviceCallback) {
        super(serviceCallback);
    }

    @Override
    public void apply(final User user) throws TelegramApiException {
        sendText(user.getChatId(),
                "Извините, не роспознал Вашу команду.\n" +
                        "Если хотите пройти тест заново - нажмите кнопку \"Начать тестирование\"\uD83E\uDD17"
        );
    }
}
