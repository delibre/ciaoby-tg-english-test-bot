package by.ciao.EnglishSchoolBot.dto;

import by.ciao.EnglishSchoolBot.englishtest.UsersTestState;
import by.ciao.EnglishSchoolBot.enums.States;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Message;

@Getter
@Setter
public class UserInfoDTO {
    private States state = null;
    private Message lastMessage;
    private Long chatId;
    private String nameAndSurname;
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
