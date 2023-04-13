package by.ciao.EnglishSchoolBot.states.statesservice;

import by.ciao.EnglishSchoolBot.user.User;

public interface UserHandlerState {
    void apply(final User user) throws Exception;
}
