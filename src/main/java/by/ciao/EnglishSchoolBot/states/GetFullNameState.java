package by.ciao.EnglishSchoolBot.states;

import by.ciao.EnglishSchoolBot.bot.ServiceCallback;
import by.ciao.EnglishSchoolBot.enums.StateEnum;
import by.ciao.EnglishSchoolBot.states.statesservice.AbstractState;
import by.ciao.EnglishSchoolBot.states.statesservice.UserMessageHandlerState;
import by.ciao.EnglishSchoolBot.user.User;
import by.ciao.EnglishSchoolBot.utils.BotResponses;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class GetFullNameState extends AbstractState implements UserMessageHandlerState {
    public GetFullNameState(final ServiceCallback serviceCallback) {
        super(serviceCallback);
    }

    @Override
    public void apply(final String textMsg, final User user) throws TelegramApiException {
        user.setFullName(textMsg);
        user.setState(StateEnum.GET_PHONE);

        sendPhoneButton(user);
    }

    private void sendPhoneButton(final User user) throws TelegramApiException {
        var sm = createMessage(user.getChatId(), BotResponses.askForPhone());
        sm.setReplyMarkup(createKeyboard());

        getServiceCallback().execute(sm);
    }

    private ReplyKeyboardMarkup createKeyboard() {
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

        return keyboard;
    }
}
