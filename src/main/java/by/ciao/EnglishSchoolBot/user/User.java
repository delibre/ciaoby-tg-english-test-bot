package by.ciao.EnglishSchoolBot.user;

import by.ciao.EnglishSchoolBot.englishtest.UsersTestState;
import by.ciao.EnglishSchoolBot.enums.StateEnum;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Message;

@Getter
@Setter
public class User {
    private StateEnum state;
    private Message lastMessage;
    private Long chatId;
    private String fullName;
    private String username;
    private String phone;
    private String referral;
    private UsersTestState testState;

    public User(final Long chatId, final String username) throws Exception {
        this.state = StateEnum.NEW_USER;
        this.chatId = chatId;
        this.username = username;
        this.phone = "нет";
        this.testState = new UsersTestState();
    }

    public void clearTest() throws Exception {
        testState = new UsersTestState();
        lastMessage = null;
    }
}
