package org.helensbot.bot;

import org.helensbot.dto.UserInfoDTO;
import org.helensbot.enums.States;
import org.helensbot.utils.Regex;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
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
        if (update.hasMessage()){
            var msg = update.getMessage();
            var user = msg.getFrom();
            var id = user.getId();

            if(!contains(id))
                dto.add(new UserInfoDTO(id, user.getUserName()));

            parseMessage(msg.getText(), getUserById(id));
        }
        else if(update.hasCallbackQuery() && Objects.equals(update.getCallbackQuery().getData(), "Начать")) {
            parseMessage(update.getCallbackQuery().getData(), getUserById(update.getCallbackQuery().getFrom().getId()));
        }
//        else if (update.hasCallbackQuery() &&
//                Objects.equals(update.getCallbackQuery().getMessage().getMessageId(),
//                        getUserById(update.getCallbackQuery().getMessage().getFrom().getId()).getLastMessage().getMessageId())) {
//
//            parseMessage(update.getCallbackQuery().getData(), getUserById(update.getCallbackQuery().getFrom().getId()));
//        } else if(update.hasCallbackQuery()){
//            sendText(update.getCallbackQuery().getMessage().getFrom().getId(), "Вы должны отвечать только на последний вопрос");
//            sendLastMessage(getUserById(update.getCallbackQuery().getMessage().getFrom().getId()));
//        }
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
            if (Objects.equals(userInfoDTO.getId(), id))
                return userInfoDTO;

        throw new AssertionError();
    }

    private boolean contains(Long id) {
        boolean containsId = false;

        for(var userInfoDTO : dto)
            if(Objects.equals(userInfoDTO.getId(), id)) {
                containsId = true;
                break;
            }

        return containsId;
    }

    private void parseMessage(String textMsg, UserInfoDTO user) {
        if (textMsg.equals("/start")) {
            user.setState(States.START);
            user.clearAll();
        } else if (user.getState() == null) {
            sendText(user.getId(), "Нет такой комманды");
            return;
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
//            case GET_PHONE_NUMBER:
//                getPhoneNumberHandler(textMsg, user);
//                break;
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
            default:
                throw new IllegalStateException();
        }
    }

    private void startHandler(UserInfoDTO user) {
        user.setState(States.GET_NAME);

        sendText(user.getId(), "Привет!\uD83D\uDC4B\n\n" +
                                    "Сейчас мы проверим Ваши знания английского\uD83D\uDD25\n" +
                                    "Но для начала давайте познакомимся\uD83D\uDE42\n\n" +
                                    "Введите, пожалуйста, Ваше имя");
    }

    private void getNameHandler(String textMsg, UserInfoDTO user) {
        user.setName(textMsg);
        user.setState(States.GET_SURNAME);

        sendText(user.getId(), "Теперь введите, пожалуйста, Фамилию");
    }

    private void getSurnameHandler(String textMsg, UserInfoDTO user) {
        user.setSurname(textMsg);
//        user.setState(States.GET_PHONE_NUMBER);
        user.setState(States.GET_REVIEW);


//        sendText(user.getId(), "Теперь введите, пожалуйста, номер телефона");
        sendText(user.getId(), "Откуда вы о нас услышали?");
    }

    private void getPhoneNumberHandler(String textMsg, UserInfoDTO user) {
        if(!Regex.checkPhoneNumber(textMsg)) {
            sendText(user.getId(), "Неверный формат номера. Попробуйте, пожалуйста, ещё раз");
            return;
        }

        user.setPhoneNumber(textMsg);
        user.setState(States.GET_REVIEW);

        sendText(user.getId(), "Откуда вы о нас услышали?");
    }

    private void getReviewHandler(String textMsg, UserInfoDTO user) {
        user.setReview(textMsg);
        user.setState(States.TEST_TODO);

        sendStartButton(user.getId(), "Ну что же, приступим к тесту.\nНажите кнопку \"начать тест\", когда будете готовы.");
    }

    private void testToDoHandler(String textMsg, UserInfoDTO user) {
        if (Objects.equals(textMsg, "Начать")) {
            user.setState(States.QUESTION_TO_SEND);
            questionToSendHandler(user);
        }
        else
            sendText(user.getId(), "Нажите старт чтобы начать");
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
        sendText(user.getId(),
                "Вы ответили верно на " + user.getTestState().getCorrectAnswers() + " вопросов.\n" +
                        "Ваш уровень английского " + user.getTestState().getResults() + ".\n" +
                        "Вы молодец, Вам осталось совсем немного, и скоро мы свяжемся для прохождения устного тестирования"
                );
    }

    private void sendQuestion(UserInfoDTO user){
        var sm = SendMessage.builder()
                .chatId(user.getId().toString())
                .text(user.getTestState().getCurrentQuestion().getNumberOfQuestion() + ". "
                        + user.getTestState().getCurrentQuestion().getQuestion()).build();

        var keyboardMarkup  = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();

//        var markup = new InlineKeyboardMarkup();
//
//        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (String answer : user.getTestState().getCurrentQuestion().getAnswers()) {
            var row = new KeyboardRow();
            row.add(new KeyboardButton(answer));
            keyboard.add(row);
            keyboardMarkup.setKeyboard(keyboard);

//            List<InlineKeyboardButton> rowInline = new ArrayList<>();
//
//            var inlineKeyboardButton = new InlineKeyboardButton();
//            inlineKeyboardButton.setText(answer);
//            inlineKeyboardButton.setCallbackData(answer);
//
//            rowInline.add(inlineKeyboardButton);
//            rowsInline.add(rowInline);
        }

//        markup.setKeyboard(rowsInline);
//        sm.setReplyMarkup(markup);
        sm.setReplyMarkup(keyboardMarkup);

        try {
//            user.setLastMessage(execute(sm));
            execute(sm);
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

    public void sendStartButton(Long who, String what) {
        var sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what).build();

//        var sm = SendMessage.builder()
//                .chatId(message.getChatId().toString())
//                .text("Hello, this is your start message!").build();

        var markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        var inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Начать");
        inlineKeyboardButton.setCallbackData("Начать");

        rowInline.add(inlineKeyboardButton);
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        sm.setReplyMarkup(markupInline);

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

//    private void sendLastMessage(UserInfoDTO user) {
//        sendText(user.getId(), user.getLastMessage().getText());
//    }

}
