package by.ciao.EnglishSchoolBot.userinfo;

import by.ciao.EnglishSchoolBot.englishtest.UsersTestState;
import by.ciao.EnglishSchoolBot.enums.StateEnum;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Message;

@Getter
@Setter
public class UserInfo {
    private StateEnum state = null;
    private Message lastMessage;
    private Long chatId;
    private String fullName;
    private String username;
    private String phone = "нет";
    private String getReferral;
    private UsersTestState testState = new UsersTestState();

    public UserInfo(final Long chatId, final String username) {
        this.chatId = chatId;
        this.username = username;
    }

    public void clearTest() {
        testState = new UsersTestState();
        lastMessage = null;
    }
}