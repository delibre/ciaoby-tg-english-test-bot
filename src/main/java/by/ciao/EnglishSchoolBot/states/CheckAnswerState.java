package by.ciao.EnglishSchoolBot.states;

import by.ciao.EnglishSchoolBot.bot.ServiceCallback;
import by.ciao.EnglishSchoolBot.user.User;
import by.ciao.EnglishSchoolBot.states.statesservice.UserMessageHandlerState;
import by.ciao.EnglishSchoolBot.states.statesservice.AbstractState;
import by.ciao.EnglishSchoolBot.states.statesservice.UserHandlerState;

public class CheckAnswerState extends AbstractState implements UserMessageHandlerState {
    public CheckAnswerState(final ServiceCallback serviceCallback) {
        super(serviceCallback);
    }

    @Override
    public void apply(final String textMsg, final User user) {
        user.getTestState().registerAnswer(textMsg);
        UserHandlerState state = new SendQuestionState(getServiceCallback());
        state.apply(user);
    }
}
