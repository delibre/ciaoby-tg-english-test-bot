package by.ciao.EnglishSchoolBot.states;

import by.ciao.EnglishSchoolBot.bot.ServiceCallback;
import by.ciao.EnglishSchoolBot.enums.StateEnum;
import by.ciao.EnglishSchoolBot.states.statesservice.AbstractState;
import by.ciao.EnglishSchoolBot.userinfo.UserInfo;
import by.ciao.EnglishSchoolBot.states.statesservice.UserMessageHandlerState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class GetFullNameState extends AbstractState implements UserMessageHandlerState {
    public GetFullNameState(final ServiceCallback serviceCallback) {
        super(serviceCallback);
    }

    @Override
    public void apply(final String textMsg, final UserInfo user) {
        user.setFullName(textMsg);
        user.setState(StateEnum.GET_PHONE);

        sendPhoneButton(user);
    }

    private void sendPhoneButton(final UserInfo user) {
        var sm = SendMessage.builder()
                .chatId(user.getChatId().toString())
                .text("Чтобы нам было удобнее с Вами связаться для согласования второго этапа (устного тестирования), " +
                        "укажите, пожалуйста, Ваш номер телефона\uD83D\uDE0A\n\n" +
                        "Вы можете нажать на кнопку \"Поделиться номером\" и указать номер, к которому привязан Ваш телеграм, " +
                        "либо же указать другой, вписав его в формате +12345678900.\uD83D\uDE0A\n\n").build();


        var keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        var row1 = new KeyboardRow();
        var button1 = new KeyboardButton("Поделиться номером");
        button1.setRequestContact(true);
        row1.add(button1);

        keyboardRows.add(row1);

        keyboard.setKeyboard(keyboardRows);
        sm.setReplyMarkup(keyboard);

        getServiceCallback().execute(sm, null, null);
    }
}
