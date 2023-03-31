package org.ciaobyTestBot.bot;

import org.ciaobyTestBot.dto.UserInfoDTO;
import org.ciaobyTestBot.enums.States;
import org.ciaobyTestBot.utils.Regex;
import org.jvnet.hk2.annotations.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class BotService {
    protected final ArrayList<UserInfoDTO> dto = new ArrayList<>();
    private final Execute ex;

    public BotService(Execute ex) {
        this.ex = ex;
    }

    protected void startHandler(UserInfoDTO user) {
        user.setState(States.GET_NAME_AND_SURNAME);

        sendText(user.getChatId(), "Привет!\uD83D\uDC4B\n\n" +
                "Сейчас мы проверим Ваши знания английского\uD83D\uDD25\n" +
                "Вы пройдете тест, который состоит из 30 вопросов.\n" +
                "После этого, вы сможете проходить его, когда захотите.\uD83D\uDE0A\n\n" +
                "Но для начала давайте познакомимся\uD83D\uDE09\n\n" +
                "Введите, пожалуйста, Ваше имя и фамилию");
    }

    protected void getNameAndSurnameHandler(String textMsg, UserInfoDTO user) {
        user.setNameAndSurname(textMsg);
        user.setState(States.GET_PHONE_NUMBER);

        sendPhoneButton(user);
    }

    protected void getPhoneNumberHandler(String textMsg, UserInfoDTO user) {
        if (!Regex.checkPhoneNumber(textMsg)) {
            sendText(user.getChatId(), "Неверный формат номера. Попробуйте, пожалуйста, ещё раз");
            return;
        }

        user.setPhoneNumber(textMsg);
        user.setState(States.GET_REVIEW);

        removeReplyKeyboard(user);
        sendOptionsForReview(user);
    }

    protected void getReviewHandler(String textMsg, UserInfoDTO user) {
        user.setReview(textMsg);
        user.setState(States.TEST_TODO);

        sendStartButton(user);
    }

    protected void testToDoHandler(String textMsg, UserInfoDTO user) {
        if (Objects.equals(textMsg, "Начать тестирование\uD83C\uDFC1")) {
            user.setState(States.QUESTION_TO_SEND);
            user.clearTest();
            questionToSendHandler(user);
        }
        else sendStartButton(user);
    }

    protected void questionToSendHandler(UserInfoDTO user){
        if (user.getTestState().isFinished()) {
            user.setState(States.TEST_ENDED);
            testEndedHandler(user);
            return;
        }

        sendQuestion(user);
        user.setState(States.CHECK_ANSWER);
    }

    protected void checkAnswerHandler(String answer, UserInfoDTO user) {
        user.getTestState().registerAnswer(answer);
        questionToSendHandler(user);
    }

    protected void testEndedHandler(UserInfoDTO user) {
        user.setState(States.END_ALL);
        deleteMessage(user);
        sendText(user.getChatId(), "Вы ответили верно на " + user.getTestState().getCorrectAnswers() + " вопросов.\n" +
                "Ваш уровень английского " + user.getTestState().getResults() + ".\n" +
                "Вы молодец, Вам осталось совсем немного, и скоро мы свяжемся с Вами для прохождения устного тестирования\uD83D\uDE0A");
        sendDataToAdmin(user);
    }

    protected void testEndAllHandler(UserInfoDTO user) {
        sendText(user.getChatId(),
                "Извините, не роспознал Вашу команду.\n" +
                        "Если хотите пройти тест заново - нажмите кнопку \"Начать тестирование\"\uD83E\uDD17"
        );
    }

    protected void sendPhoneButton(UserInfoDTO user) {
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

        ex.execute(sm, null, null);
    }

    protected void removeReplyKeyboard(UserInfoDTO user) {
        String msg = "Спасибо! Можем продолжать\uD83D\uDE0A";

        var replyKeyboardRemove = new ReplyKeyboardRemove(true);
        var removeMessage = new SendMessage(user.getChatId().toString(), msg);
        removeMessage.setReplyMarkup(replyKeyboardRemove);

        ex.execute(removeMessage, null, null);

        try {
            Thread.sleep(500); // Delay for 1.5 seconds (1500 milliseconds)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void sendOptionsForReview(UserInfoDTO user) {
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

        ex.execute(sm, null, null);
    }

    protected void sendStartButton(UserInfoDTO user) {
        var sm = SendMessage.builder()
                .chatId(user.getChatId().toString())
                .text("Ну что же, приступим к тесту. Сейчас Вам нужно будет ответить на 30 вопросов.\uD83E\uDDD0 " +
                        "Ограничений по времени нет.\n" +
                        "\nНажмите кнопку \"Начать тестирование\", когда будете готовы.").build();

        var keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        var row = new KeyboardRow();
        row.add(new KeyboardButton("Начать тестирование\uD83C\uDFC1"));

        keyboardRows.add(row);

        keyboard.setKeyboard(keyboardRows);
        sm.setReplyMarkup(keyboard);

        ex.execute(sm, null, null);
    }

    protected void sendQuestion(UserInfoDTO user) {
        var sm = SendMessage.builder()
                .chatId(user.getChatId().toString())
                .text(user.getTestState().getCurrentQuestion().getNumberOfQuestion() + ". "
                        + user.getTestState().getCurrentQuestion().getQuestion()).build();

        var markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> rowInline;

        for (String answer : user.getTestState().getCurrentQuestion().getAnswers()) {
            rowInline = new ArrayList<>();

            var inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(answer);
            inlineKeyboardButton.setCallbackData(answer);

            rowInline.add(inlineKeyboardButton);
            keyboard.add(rowInline);
        }

        rowInline = new ArrayList<>();

        var inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Пропустить");
        inlineKeyboardButton.setCallbackData("Пропустить");

        rowInline.add(inlineKeyboardButton);
        keyboard.add(rowInline);

        markup.setKeyboard(keyboard);
        sm.setReplyMarkup(markup);

        if (user.getLastMessage() != null) {
            var editMessageText = EditMessageText.builder()
                    .chatId(user.getLastMessage().getChatId().toString())
                    .messageId(user.getLastMessage().getMessageId())
                    .text(user.getTestState().getCurrentQuestion().getNumberOfQuestion() + ". "
                            + user.getTestState().getCurrentQuestion().getQuestion()).build();

            editMessageText.setReplyMarkup(markup);

            ex.execute(null, null, editMessageText);
        } else {
            user.setLastMessage(ex.execute(sm, null, null));
        }
    }

    protected void deleteMessage(UserInfoDTO user) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(user.getChatId().toString());
        deleteMessage.setMessageId(user.getLastMessage().getMessageId());

        ex.execute(null, deleteMessage, null);
    }

    protected void sendDataToAdmin(UserInfoDTO user) {
        ex.execute(SendMessage.builder()
                .chatId("5105539803").
                text(   "Имя и Фамилия: " + user.getNameAndSurname() + "\n" +
                        "Номер телефона: " + user.getPhoneNumber() + "\n" +
                        "Ник в телеграмм: @" + user.getUsername() + "\n" +
                        "Откуда узнали: " + user.getReview() + "\n" +
                        "Отвечено верно на: " + user.getTestState().getCorrectAnswers() + " вопросов.\n" +
                        "Уровень английского: " + user.getTestState().getLvl()).build(), null, null);
    }

    protected void sendText(Long who, String what){
        var sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what).build();

        ex.execute(sm, null, null);
    }

    protected UserInfoDTO getUserById(Long id) {
        for (var userInfoDTO : dto)
            if (Objects.equals(userInfoDTO.getChatId(), id))
                return userInfoDTO;

        throw new RuntimeException("No such user");
    }

    protected boolean contains(Long id) {
        var containsId = false;

        for(var userInfoDTO : dto)
            if(Objects.equals(userInfoDTO.getChatId(), id)) {
                containsId = true;
                break;
            }

        return containsId;
    }

}
