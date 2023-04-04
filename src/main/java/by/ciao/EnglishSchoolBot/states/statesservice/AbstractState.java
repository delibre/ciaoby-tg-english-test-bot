package by.ciao.EnglishSchoolBot.states.statesservice;

import by.ciao.EnglishSchoolBot.bot.ServiceCallback;
import lombok.AllArgsConstructor;
import lombok.Getter;
import by.ciao.EnglishSchoolBot.dto.UserInfoDTO;
import by.ciao.EnglishSchoolBot.enums.States;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public abstract class AbstractState {
    private final ServiceCallback serviceCallback;
    private final States state;

    protected void sendText(final Long who, final String what){
        var sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what).build();

        serviceCallback.execute(sm, null, null);
    }

    protected void sendStartButton(final UserInfoDTO user) {
        var sm = SendMessage.builder()
                .chatId(user.getChatId().toString())
                .text("Ну что же, приступим к тесту. Сейчас Вам нужно будет ответить на 30 вопросов.\uD83E\uDDD0 " +
                        "Ограничений по времени нет.\n\n" +
                        "Нажмите кнопку \"Начать тестирование\", когда будете готовы.").build();

        var keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        var row = new KeyboardRow();
        row.add(new KeyboardButton("Начать тестирование\uD83C\uDFC1"));

        keyboardRows.add(row);

        keyboard.setKeyboard(keyboardRows);
        sm.setReplyMarkup(keyboard);

        serviceCallback.execute(sm, null, null);
    }
}
