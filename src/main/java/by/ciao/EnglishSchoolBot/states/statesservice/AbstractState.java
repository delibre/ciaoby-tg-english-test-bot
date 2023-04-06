package by.ciao.EnglishSchoolBot.states.statesservice;

import by.ciao.EnglishSchoolBot.bot.ServiceCallback;
import by.ciao.EnglishSchoolBot.utils.BotResponses;
import lombok.AllArgsConstructor;
import lombok.Getter;
import by.ciao.EnglishSchoolBot.user.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public abstract class AbstractState {
    private final ServiceCallback serviceCallback;

    protected void sendText(final Long id, final String textMsg) throws TelegramApiException {
        serviceCallback.execute(createMessage(id, textMsg));
    }

    protected void sendStartButton(final User user) throws TelegramApiException {
        var sm = createMessage(user.getChatId(), BotResponses.startTest());

        var keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        var row = new KeyboardRow();
        row.add(new KeyboardButton("Начать тестирование\uD83C\uDFC1"));

        keyboardRows.add(row);

        keyboard.setKeyboard(keyboardRows);
        sm.setReplyMarkup(keyboard);

        serviceCallback.execute(sm);
    }

    protected SendMessage createMessage(Long id, String textMsg) {
        return SendMessage.builder()
                .chatId(id.toString())
                .text(textMsg).build();
    }
}
