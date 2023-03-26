package org.helensbot.bot;

import org.helensbot.dto.UserInfoDTO;
import org.helensbot.enums.States;
import org.helensbot.utils.Regex;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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

public class EnglishSchoolBot extends TelegramLongPollingBot {
    private final ArrayList<UserInfoDTO> dto = new ArrayList<>();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()){
            var msg = update.getMessage();

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
        return "English_Schoo_lBot";
    }

    @Override
    public String getBotToken() {
        return "5915260285:AAGpgfcCYCu6kxD38WcE6QYVFXCv4JjbdwA";
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
            case GET_NAME:
                getNameHandler(textMsg, user);
                break;
            case GET_SURNAME:
                getSurnameHandler(textMsg, user);
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
            default:
                throw new IllegalStateException();
        }
    }

    private void startHandler(UserInfoDTO user) {
        user.setState(States.GET_NAME);

        sendText(user.getChatId(), "Привет!\uD83D\uDC4B\n\n" +
                                    "Сейчас мы проверим Ваши знания английского\uD83D\uDD25\n" +
                                    "Но для начала давайте познакомимся\uD83D\uDE42\n\n" +
                                    "Введите, пожалуйста, Ваше имя");
    }

    private void getNameHandler(String textMsg, UserInfoDTO user) {
        user.setName(textMsg);
        user.setState(States.GET_SURNAME);

        sendText(user.getChatId(), "Теперь введите, пожалуйста, Фамилию");
    }

    private void getSurnameHandler(String textMsg, UserInfoDTO user) {
        user.setSurname(textMsg);
        if (user.getUsername() == null) {
            user.setState(States.GET_PHONE_NUMBER);
            sendPhoneOrSkipButtons(user);
            return;
        }

        user.setState(States.GET_REVIEW);
        sendText(user.getChatId(), "Откуда вы о нас услышали?");
    }

    //TODO clean the code
    private void getPhoneNumberHandler(String textMsg, UserInfoDTO user) {
        if (textMsg.equals("Пропустить")) {
            var replyKeyboardRemove = new ReplyKeyboardRemove(true);
            var removeMessage = new SendMessage(user.getChatId().toString(), "Откуда вы о нас услышали?");
            removeMessage.setReplyMarkup(replyKeyboardRemove);

            try {
                execute(removeMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

            user.setState(States.GET_REVIEW);
            return;
        }

        if (!Regex.checkPhoneNumber(textMsg)) {
            sendText(user.getChatId(), "Неверный формат номера. Попробуйте, пожалуйста, ещё раз");
            return;
        }

        var replyKeyboardRemove = new ReplyKeyboardRemove(true);
        var removeMessage = new SendMessage(user.getChatId().toString(), "Откуда вы о нас услышали?");
        removeMessage.setReplyMarkup(replyKeyboardRemove);

        try {
            execute(removeMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        user.setPhoneNumber(textMsg);
        user.setState(States.GET_REVIEW);
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
        sendText(user.getChatId(),
                "Вы ответили верно на " + user.getTestState().getCorrectAnswers() + " вопросов.\n" +
                        "Ваш уровень английского " + user.getTestState().getResults() + ".\n" +
                        "Вы молодец, Вам осталось совсем немного, и скоро мы свяжемся с Вами для прохождения устного тестирования"
                );
        sendDataToAdmin(user);
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

    private void sendStartButton(UserInfoDTO user) {
        var sm = SendMessage.builder()
                .chatId(user.getChatId().toString())
                .text("Ну что же, приступим к тесту.\nНажмите кнопку \"Начать тестирование\", когда будете готовы.").build();

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

    private void sendPhoneOrSkipButtons(UserInfoDTO user) {
        var sm = SendMessage.builder()
                .chatId(user.getChatId().toString())
                .text("Мы заметили, что на вашем аккаунте нет никнейма. " +
                        "Чтобы нам было удобнее с вами связаться после прохождения теста, " +
                        "укажите, пожалуйста, ваш номер телефона.").build();


        var keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        var row1 = new KeyboardRow();
        var button1 = new KeyboardButton("Поделится номером");
        button1.setRequestContact(true);
        row1.add(button1);

        var row2 = new KeyboardRow();
        row1.add(new KeyboardButton("Пропустить"));

        keyboardRows.add(row1);
        keyboardRows.add(row2);

        keyboard.setKeyboard(keyboardRows);
        sm.setReplyMarkup(keyboard);

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendDataToAdmin(UserInfoDTO user) {
        var sm = SendMessage.builder()
                .chatId("5105539803").
                text(   "Имя: " + user.getName() + "\n" +
                        "Фамилия: " + user.getSurname() + "\n" +
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
