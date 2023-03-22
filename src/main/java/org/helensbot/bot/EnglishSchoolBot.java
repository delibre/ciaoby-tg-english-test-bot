package org.helensbot.bot;

import org.helensbot.dto.UserInfoDTO;
import org.helensbot.enums.States;
import org.helensbot.utils.Regex;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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
        if (update.hasCallbackQuery()) {
            parseMessage(update.getCallbackQuery().getData(), getUserById(update.getCallbackQuery().getFrom().getId()));
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

    public void parseMessage(String textMsg, UserInfoDTO user) {
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
        user.setState(States.GET_PHONE_NUMBER);

        sendText(user.getId(), "Теперь введите, пожалуйста, номер телефона");
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

        sendText(user.getId(), "Ну что же, приступим к тесту.\n  Нажите кнопку \"начать тест\", когда будете готовы.");
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
        }
        sendQuestion(user);
        user.setState(States.CHECK_ANSWER);
    }

    private void checkAnswerHandler(String answer, UserInfoDTO user) {
        user.getTestState().registerAnswer(answer);
        user.setState(States.QUESTION_TO_SEND);
        questionToSendHandler(user);
    }

    private void testEndedHandler(UserInfoDTO user) {
        sendText(user.getId(),
                "Вы ответили верно на " + user.getTestState().getCorrectAnswers() + "вопросов.\n" +
                        "Ваш уровень английского " + user.getTestState().getResults() + ".\n" +
                        "Вы молодец, Вам осталось совсем немного, и скоро мы свяжемся для прохождения устного тестирования"
                );
    }

    public void sendQuestion(UserInfoDTO user){
        var sm = SendMessage.builder()
                .chatId(user.getId().toString())
                .text(user.getTestState().getCurrentQuestion().getNumberOfQuestion() + ". "
                        + user.getTestState().getCurrentQuestion().getQuestion()).build();

        var markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (String buttonText : user.getTestState().getCurrentQuestion().getAnswers()) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();

            var inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(buttonText);
            inlineKeyboardButton.setCallbackData(buttonText);

            rowInline.add(inlineKeyboardButton);
            rowsInline.add(rowInline);
        }

        markup.setKeyboard(rowsInline);
        sm.setReplyMarkup(markup);

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendText(Long who, String what){
        var sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
