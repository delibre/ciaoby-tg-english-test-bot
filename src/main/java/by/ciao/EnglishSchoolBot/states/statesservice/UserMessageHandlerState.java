package by.ciao.EnglishSchoolBot.states.statesservice;

import by.ciao.EnglishSchoolBot.dto.UserInfoDTO;

public interface UserMessageHandlerState {
    void apply(final String textMsg, final UserInfoDTO user);
}
