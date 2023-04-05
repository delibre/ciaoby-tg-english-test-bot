package by.ciao.EnglishSchoolBot.states;

import by.ciao.EnglishSchoolBot.bot.ServiceCallback;
import by.ciao.EnglishSchoolBot.user.User;
import by.ciao.EnglishSchoolBot.enums.StateEnum;
import by.ciao.EnglishSchoolBot.states.statesservice.AbstractState;
import by.ciao.EnglishSchoolBot.states.statesservice.UserHandlerState;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class StartState extends AbstractState implements UserHandlerState {
    public StartState(final ServiceCallback serviceCallback) {
        super(serviceCallback);
    }

    @Override
    public void apply(final User user) throws TelegramApiException {
        user.setState(StateEnum.GET_FULL_NAME);

        sendText(user.getChatId(), "Привет!\uD83D\uDC4B\n\n" +
                "Сейчас мы проверим Ваши знания английского\uD83D\uDD25\n" +
                "Вы пройдете тест, который состоит из 30 вопросов.\n" +
                "После этого, вы сможете проходить его, когда захотите.\uD83D\uDE0A\n\n" +
                "Но для начала давайте познакомимся\uD83D\uDE09\n\n" +
                "Введите, пожалуйста, Ваше имя и фамилию");
    }
}
