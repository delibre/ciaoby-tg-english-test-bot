package by.ciao.EnglishSchoolBot.states.statesservice;

import by.ciao.EnglishSchoolBot.dto.UserInfoDTO;

public interface UserHandlerState {
    void apply(UserInfoDTO user);
}
