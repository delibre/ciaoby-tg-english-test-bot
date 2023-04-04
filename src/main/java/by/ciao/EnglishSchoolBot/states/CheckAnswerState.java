package by.ciao.EnglishSchoolBot.states;

import by.ciao.EnglishSchoolBot.bot.ServiceCallback;
import by.ciao.EnglishSchoolBot.dto.UserInfoDTO;
import by.ciao.EnglishSchoolBot.enums.States;
import by.ciao.EnglishSchoolBot.states.statesservice.State;
import by.ciao.EnglishSchoolBot.states.statesservice.AbstractState;

public class CheckAnswerState extends AbstractState {
    public CheckAnswerState(final ServiceCallback serviceCallback) {
        super(serviceCallback, States.CHECK_ANSWER);
    }

    @Override
    public void apply(final String textMsg, final UserInfoDTO user) {
        user.getTestState().registerAnswer(textMsg);
        State state = new QuestionToSendState(getServiceCallback());
        state.apply(textMsg, user);
    }
}
