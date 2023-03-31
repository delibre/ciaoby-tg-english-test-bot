package org.ciaobyTestBot.states.statesservice;

import org.ciaobyTestBot.dto.UserInfoDTO;

public interface State {
    void apply(String textMsg, UserInfoDTO user);
}
