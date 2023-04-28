package by.ciao.utils;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public final class KeyboardCreator {

    public static InlineKeyboardMarkup createInlineKeyboard(final List<String> answerOptions) {
        var markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (String option : answerOptions) {
            var button = new InlineKeyboardButton();
            button.setText(option);
            button.setCallbackData(option);
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            keyboard.add(row);
        }

        markup.setKeyboard(keyboard);

        return markup;
    }

    public static ReplyKeyboardMarkup createReplyKeyboard(final String buttonText, final boolean requestContact) {
        var keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        var row = new KeyboardRow();
        var button = new KeyboardButton(buttonText);
        button.setRequestContact(requestContact);
        row.add(button);

        keyboardRows.add(row);

        keyboard.setKeyboard(keyboardRows);

        return keyboard;
    }

}
