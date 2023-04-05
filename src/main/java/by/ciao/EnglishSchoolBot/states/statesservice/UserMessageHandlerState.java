package by.ciao.EnglishSchoolBot.states.statesservice;

import by.ciao.EnglishSchoolBot.user.User;

public interface UserMessageHandlerState {
    void apply(final String textMsg, final User user) throws Exception;
}
