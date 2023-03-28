package org.ciaobyTestBot.bot;

import org.ciaobyTestBot.dto.UserInfoDTO;
import org.ciaobyTestBot.enums.States;
import org.ciaobyTestBot.utils.Regex;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CiaoByBot extends TelegramLongPollingBot {
    private final ArrayList<UserInfoDTO> dto = new ArrayList<>();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()){
            var msg = update.getMessage();

            if(contains(msg.getChatId()) && getUserById(msg.getChatId()).getState() == States.CHECK_ANSWER) {
                sendText(getUserById(msg.getChatId()).getChatId(), "Отвечать можно только нажав кнопку с одним из вариантов ответа");
                return;
            }

            if (!contains(msg.getChatId()))
                dto.add(new UserInfoDTO(msg.getChatId(), msg.getFrom().getUserName()));

            parseMessage(msg.getText(), getUserById(msg.getChatId()));
        }
        else if (update.getMessage() != null && update.getMessage().getContact() != null) {
            getPhoneNumberHandler(update.getMessage().getContact().getPhoneNumber(), getUserById(update.getMessage().getChatId()));
        }
        else if (update.hasCallbackQuery()) {
            parseMessage(update.getCallbackQuery().getData(), getUserById(update.getCallbackQuery().getFrom().getId()));
            var close = AnswerCallbackQuery.builder()
                    .callbackQueryId(update.getCallbackQuery().getId()).build();

            try {
                execute(close);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "ciaoby_bot";
    }

    @Override
    public String getBotToken() {
        return "6118270229:AAEAs0HoHAKmcD_fMEsFXO1fdIbFpXCyhuY";
    }

    private UserInfoDTO getUserById(Long id) {
        for (var userInfoDTO : dto)
            if (Objects.equals(userInfoDTO.getChatId(), id))
                return userInfoDTO;

        throw new AssertionError();
    }

    private boolean contains(Long id) {
        var containsId = false;

        for(var userInfoDTO : dto)
            if(Objects.equals(userInfoDTO.getChatId(), id)) {
                containsId = true;
                break;
            }

        return containsId;
    }

    private void parseMessage(String textMsg, UserInfoDTO user) {
        if (textMsg.equals("/start")) {
            user.setState(States.START);
            user.clearTest();
        } else if (user.getState() == null) {
            sendText(user.getChatId(), "Нет такой команды");
            return;
        }

        if (textMsg.equals("Начать тестирование\uD83C\uDFC1")) {
            user.setState(States.QUESTION_TO_SEND);
            user.clearTest();
        }

        switch (user.getState()) {
            case START:
                startHandler(user);
                break;
            case GET_NAME_AND_SURNAME:
                getNameAndSurnameHandler(textMsg, user);
                break;
            case GET_PHONE_NUMBER:
                getPhoneNumberHandler(textMsg, user);
                break;
            case GET_REVIEW:
                getReviewHandler(textMsg, user);
                break;
            case TEST_TODO:
                testToDoHandler(textMsg, user);
                break;
            case QUESTION_TO_SEND:
                questionToSendHandler(user);
                break;
            case CHECK_ANSWER:
                checkAnswerHandler(textMsg, user);
                break;
            case TEST_ENDED:
                testEndedHandler(user);
                break;
            case END_ALL:
                testEndAllHandler(user);
                break;
            default:
                throw new IllegalStateException();
        }
    }

    private void startHandler(UserInfoDTO user) {
        user.setState(States.GET_NAME_AND_SURNAME);

        sendText(user.getChatId(), "Привет!\uD83D\uDC4B\n\n" +
                                    "Сейчас мы проверим Ваши знания английского\uD83D\uDD25\n" +
                                    "Вы пройдете тест, который состоит из 30 вопросов.\n" +
                                    "После этого, вы сможете проходить его, когда захотите.\uD83D\uDE0A\n\n" +
                                    "Но для начала давайте познакомимся\uD83D\uDE09\n\n" +
                                    "Введите, пожалуйста, Ваше имя и фамилию");
    }

    private void getNameAndSurnameHandler(String textMsg, UserInfoDTO user) {
        user.setNameAndSurname(textMsg);
        user.setState(States.GET_PHONE_NUMBER);

        sendPhoneOrSkipButtons(user);
    }

    private void getPhoneNumberHandler(String textMsg, UserInfoDTO user) {
        if (!Regex.checkPhoneNumber(textMsg)) {
            sendText(user.getChatId(), "Неверный формат номера. Попробуйте, пожалуйста, ещё раз");
            return;
        }

        removeReplyKeyboard(user);

        user.setPhoneNumber(textMsg);
        user.setState(States.GET_REVIEW);
        sendOptionsForReview(user);
    }

    private void removeReplyKeyboard(UserInfoDTO user) {
        String msg = "Спасибо! Можем продолжать\uD83D\uDE0A";

        var replyKeyboardRemove = new ReplyKeyboardRemove(true);
        var removeMessage = new SendMessage(user.getChatId().toString(), msg);
        removeMessage.setReplyMarkup(replyKeyboardRemove);

        try {
            execute(removeMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        try {
            Thread.sleep(500); // Delay for 1.5 seconds (1500 milliseconds)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void getReviewHandler(String textMsg, UserInfoDTO user) {
        user.setReview(textMsg);
        user.setState(States.TEST_TODO);

        sendStartButton(user);
    }

    private void testToDoHandler(String textMsg, UserInfoDTO user) {
        if (Objects.equals(textMsg, "Начать тестирование\uD83C\uDFC1")) {
            user.setState(States.QUESTION_TO_SEND);
            user.clearTest();
            questionToSendHandler(user);
        }
        else
            sendStartButton(user);
    }
    private void questionToSendHandler(UserInfoDTO user){
        if (user.getTestState().isFinished()) {
            user.setState(States.TEST_ENDED);
            testEndedHandler(user);
            return;
        }

        sendQuestion(user);
        user.setState(States.CHECK_ANSWER);
    }

    private void checkAnswerHandler(String answer, UserInfoDTO user) {
        user.getTestState().registerAnswer(answer);
        questionToSendHandler(user);
    }

    private void testEndedHandler(UserInfoDTO user) {
        deleteMessage(user);

        user.setState(States.END_ALL);
        sendText(user.getChatId(),
                "Вы ответили верно на " + user.getTestState().getCorrectAnswers() + " вопросов.\n" +
                        "Ваш уровень английского " + user.getTestState().getResults() + ".\n" +
                        "Вы молодец, Вам осталось совсем немного, и скоро мы свяжемся с Вами для прохождения усного тестирования"
                );

        sendDataToAdmin(user);
    }

    private void testEndAllHandler(UserInfoDTO user) {
        sendText(user.getChatId(),
                "Извините, не роспознал Вашу команду.\n" +
                        "Если хотите пройти тест заново - нажмите кнопку \"Начать тестирование\"\uD83E\uDD17"
        );
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

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendPhoneOrSkipButtons(UserInfoDTO user) {
        var sm = SendMessage.builder()
                .chatId(user.getChatId().toString())
                .text("Чтобы нам было удобнее с Вами связаться для прохождения устного теста, " +
                        "укажите, пожалуйста, Ваш номер телефона\uD83D\uDE0A\n\n" +
                        "Вы можете нажать на кнопку \"Поделиться номером\", и указать номер, к которому привязан Ваш телеграм, " +
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

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendStartButton(UserInfoDTO user) {
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

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendQuestion(UserInfoDTO user){
        var sm = SendMessage.builder()
                .chatId(user.getChatId().toString())
                .text(user.getTestState().getCurrentQuestion().getNumberOfQuestion() + ". "
                        + user.getTestState().getCurrentQuestion().getQuestion()).build();

        var markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (String answer : user.getTestState().getCurrentQuestion().getAnswers()) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();

            var inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(answer);
            inlineKeyboardButton.setCallbackData(answer);

            rowInline.add(inlineKeyboardButton);
            rowsInline.add(rowInline);
        }

        markup.setKeyboard(rowsInline);
        sm.setReplyMarkup(markup);

        try {
            if (user.getLastMessage() != null && user.getLastMessage().getChatId() != 0 && user.getLastMessage().getMessageId() != 0) {
                var editMessageText = EditMessageText.builder()
                        .chatId(user.getLastMessage().getChatId().toString())
                        .messageId(user.getLastMessage().getMessageId())
                        .text(user.getTestState().getCurrentQuestion().getNumberOfQuestion() + ". "
                                + user.getTestState().getCurrentQuestion().getQuestion()).build();

                editMessageText.setReplyMarkup(markup);

                execute(editMessageText);
            } else {
                user.setLastMessage(execute(sm));
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteMessage(UserInfoDTO user) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(user.getChatId().toString());
        deleteMessage.setMessageId(user.getLastMessage().getMessageId());

        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendText(Long who, String what){
        var sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendDataToAdmin(UserInfoDTO user) {
        var sm = SendMessage.builder()
                .chatId("931441991").
                text(   "Имя и Фамилия: " + user.getNameAndSurname() + "\n" +
                        "Номер телефона: " + user.getPhoneNumber() + "\n" +
                        "Ник в телеграмм: @" + user.getUsername() + "\n" +
                        "Откуда узнали: " + user.getReview() + "\n" +
                        "Отвечено верно на: " + user.getTestState().getCorrectAnswers() + " вопросов.\n" +
                        "Уровень английского: " + user.getTestState().getLvl()).build();

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
