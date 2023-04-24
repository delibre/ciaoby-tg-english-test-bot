package by.ciao.user;

import by.ciao.englishtest.UsersTestState;
import by.ciao.enums.StateEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@Getter @Setter
@NoArgsConstructor
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
    }

    public void clearTest() {
        testState = new UsersTestState();
        lastMessage = null;
    }

    public boolean isUserDataCollected() {
        return fullName != null && phone != null && referral != null;
    }

    @Autowired
    void setTestState(UsersTestState testState) {
        this.testState = testState;
    }

}
