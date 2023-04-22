package by.ciao.EnglishSchoolBot.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class UserDTO {
    private String fullName;
    private String phone;
    private String username;
    private String referral;
    private String englishLvl;
    private int numOfCorrectAnswers;
    private Long chatId;
}
