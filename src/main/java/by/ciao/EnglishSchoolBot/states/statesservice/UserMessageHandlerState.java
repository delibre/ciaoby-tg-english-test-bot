package by.ciao.EnglishSchoolBot.states.statesservice;

import by.ciao.EnglishSchoolBot.userinfo.UserInfo;

public interface UserMessageHandlerState {
    void apply(final String textMsg, final UserInfo user);
}
