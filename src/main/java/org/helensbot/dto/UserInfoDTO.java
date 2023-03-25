package org.helensbot.dto;

import lombok.Getter;
import lombok.Setter;
import org.helensbot.englishtest.UsersTestState;
import org.helensbot.enums.States;
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
    private String phoneNumber;
    private String review;
    private UsersTestState testState = new UsersTestState();

    public UserInfoDTO(Long chatId, String username) {
        this.chatId = chatId;
        this.username = username;
    }

    public void clearTest() {
        testState = new UsersTestState();
    }
}
