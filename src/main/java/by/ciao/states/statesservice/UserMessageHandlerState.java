package by.ciao.states.statesservice;

import by.ciao.user.User;

public interface UserMessageHandlerState {
    void apply(final String textMsg, final User user) throws Exception;
}
