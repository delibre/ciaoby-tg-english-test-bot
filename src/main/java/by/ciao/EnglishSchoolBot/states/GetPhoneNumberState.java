package by.ciao.EnglishSchoolBot.states;

import by.ciao.EnglishSchoolBot.bot.ServiceCallback;
import by.ciao.EnglishSchoolBot.enums.States;
import by.ciao.EnglishSchoolBot.states.statesservice.AbstractState;
import by.ciao.EnglishSchoolBot.utils.Regex;
import by.ciao.EnglishSchoolBot.dto.UserInfoDTO;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class GetPhoneNumberState extends AbstractState {
    public GetPhoneNumberState(ServiceCallback serviceCallback) {
        super(serviceCallback, States.GET_PHONE_NUMBER);
    }

    @Override
    public void apply(String textMsg, UserInfoDTO user) {
        if (!Regex.checkPhoneNumber(textMsg)) {
            sendText(user.getChatId());
            return;
        }

        user.setPhoneNumber(textMsg);
        user.setState(States.GET_REVIEW);

        removeReplyKeyboard(user);
        sendOptionsForReview(user);
    }

    private void sendText(Long who){
        var sm = SendMessage.builder()
                .chatId(who.toString())
                .text("Неверный формат номера. Попробуйте, пожалуйста, ещё раз").build();

        getServiceCallback().execute(sm, null, null);
    }

    private void removeReplyKeyboard(UserInfoDTO user) {
        String msg = "Спасибо! Можем продолжать\uD83D\uDE0A";

        var replyKeyboardRemove = new ReplyKeyboardRemove(true);
        var removeMessage = new SendMessage(user.getChatId().toString(), msg);
        removeMessage.setReplyMarkup(replyKeyboardRemove);

        getServiceCallback().execute(removeMessage, null, null);

        try {
            Thread.sleep(500); // Delay for 0.5 seconds (500 milliseconds)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendOptionsForReview(UserInfoDTO user) {
        var sm = SendMessage.builder()
                .chatId(user.getChatId().toString())
                .text("Откуда вы о нас узнали?\n" +
                        "Выберете один из вариантов ниже или же впишите свой").build();

        var markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        var button1 = new InlineKeyboardButton();
        button1.setText("Google");
        button1.setCallbackData("Google");
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(button1);
        keyboard.add(row1);

        var button2 = new InlineKeyboardButton();
        button2.setText("Яндекс");
        button2.setCallbackData("Яндекс");
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(button2);
        keyboard.add(row2);

        var button3 = new InlineKeyboardButton();
        button3.setText("Instagram/Facebook/VK/Tik-Tok");
        button3.setCallbackData("Instagram/Facebook/VK/Tik-Tok");
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(button3);
        keyboard.add(row3);

        var button4 = new InlineKeyboardButton();
        button4.setText("Vse-kursy");
        button4.setCallbackData("Vse-kursy");
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        row4.add(button4);
        keyboard.add(row4);

        var button5 = new InlineKeyboardButton();
        button5.setText("Еnguide");
        button5.setCallbackData("Еnguide");
        List<InlineKeyboardButton> row5 = new ArrayList<>();
        row5.add(button5);
        keyboard.add(row5);

        var button6 = new InlineKeyboardButton();
        button6.setText("Рекомендация от друзей");
        button6.setCallbackData("Рекомендация от друзей");
        List<InlineKeyboardButton> row6 = new ArrayList<>();
        row6.add(button6);
        keyboard.add(row6);

        markup.setKeyboard(keyboard);
        sm.setReplyMarkup(markup);

        getServiceCallback().execute(sm, null, null);
    }
}