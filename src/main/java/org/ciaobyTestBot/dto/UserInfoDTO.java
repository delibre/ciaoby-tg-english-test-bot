package org.ciaobyTestBot.dto;

import lombok.Getter;
import lombok.Setter;
import org.ciaobyTestBot.englishtest.UsersTestState;
import org.ciaobyTestBot.enums.States;
import org.telegram.telegrambots.meta.api.objects.Message;

@Getter
@Setter
public class UserInfoDTO {
    private States state = null;
    private Message lastMessage;
    private Long chatId;
    private String name;
    private String surname;
    private String username;
    private String phoneNumber = "нет";
    private String review;
    private UsersTestState testState = new UsersTestState();

    public UserInfoDTO(Long chatId, String username) {
        this.chatId = chatId;
        this.username = username;
    }

    public void clearTest() {
        testState = new UsersTestState();
        lastMessage = null;
    }
}
