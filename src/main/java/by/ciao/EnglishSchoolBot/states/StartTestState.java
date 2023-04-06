package by.ciao.EnglishSchoolBot.states;

import by.ciao.EnglishSchoolBot.bot.ServiceCallback;
import by.ciao.EnglishSchoolBot.enums.StateEnum;
import by.ciao.EnglishSchoolBot.states.statesservice.AbstractState;
import by.ciao.EnglishSchoolBot.states.statesservice.UserHandlerState;
import by.ciao.EnglishSchoolBot.states.statesservice.UserMessageHandlerState;
import by.ciao.EnglishSchoolBot.user.User;
import by.ciao.EnglishSchoolBot.utils.BotResponses;

import java.util.Objects;

public class StartTestState extends AbstractState implements UserMessageHandlerState {
    public StartTestState(ServiceCallback serviceCallback) {
        super(serviceCallback);
    }

    @Override
    public void apply(final String textMsg, final User user) throws Exception {
        if (Objects.equals(textMsg, BotResponses.startTestButton())) {
            user.setState(StateEnum.SEND_QUESTION);
            user.clearTest();

            UserHandlerState state = new SendQuestionState(getServiceCallback());
            state.apply(user);
        }
        else sendStartButton(user);
    }
}
