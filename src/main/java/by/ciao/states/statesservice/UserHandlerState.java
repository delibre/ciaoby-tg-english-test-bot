package by.ciao.states.statesservice;

import by.ciao.user.User;

public interface UserHandlerState {
    void apply(final User user) throws Exception;
}
