package org.ciaobyTestBot.states;

import org.ciaobyTestBot.bot.ServiceCallback;
import org.ciaobyTestBot.dto.UserInfoDTO;
import org.ciaobyTestBot.enums.States;
import org.ciaobyTestBot.states.statesservice.AbstractState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class GetNameAndSurnameState extends AbstractState {
    public GetNameAndSurnameState(ServiceCallback serviceCallback) {
        super(serviceCallback, States.GET_NAME_AND_SURNAME);
    }

    @Override
    public void apply(String textMsg, UserInfoDTO user) {
        user.setNameAndSurname(textMsg);
        user.setState(States.GET_PHONE_NUMBER);

        sendPhoneButton(user);
    }

    private void sendPhoneButton(UserInfoDTO user) {
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
