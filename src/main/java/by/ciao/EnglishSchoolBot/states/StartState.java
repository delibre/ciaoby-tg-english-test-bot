package by.ciao.EnglishSchoolBot.states;

import by.ciao.EnglishSchoolBot.bot.ServiceCallback;
import by.ciao.EnglishSchoolBot.dto.UserInfoDTO;
import by.ciao.EnglishSchoolBot.enums.States;
import by.ciao.EnglishSchoolBot.states.statesservice.AbstractState;

public class StartState extends AbstractState {
    public StartState(ServiceCallback serviceCallback) {
        super(serviceCallback, States.START);
    }

    @Override
    public void apply(String textMsg, UserInfoDTO user) {
        user.setState(States.GET_NAME_AND_SURNAME);

        sendText(user.getChatId(), "Привет!\uD83D\uDC4B\n\n" +
                "Сейчас мы проверим Ваши знания английского\uD83D\uDD25\n" +
                "Вы пройдете тест, который состоит из 30 вопросов.\n" +
                "После этого, вы сможете проходить его, когда захотите.\uD83D\uDE0A\n\n" +
                "Но для начала давайте познакомимся\uD83D\uDE09\n\n" +
                "Введите, пожалуйста, Ваше имя и фамилию");
    }
}