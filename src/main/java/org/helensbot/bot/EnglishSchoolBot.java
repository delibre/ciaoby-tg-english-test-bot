package org.helensbot.bot;

import org.helensbot.dto.UserInfoDTO;
import org.helensbot.enums.States;
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
            var id = msg.getChatId();

            if(!contains(id))
                dto.add(new UserInfoDTO(id, user.getUserName()));

            parseMessage(msg.getText(), getUserById(id));
        }
        else if(update.hasCallbackQuery() && Objects.equals(update.getCallbackQuery().getData(), "Начать")) {
            parseMessage(update.getCallbackQuery().getData(), getUserById(update.getCallbackQuery().getFrom().getId()));
        }
        else if (update.hasCallbackQuery() &&
                Objects.equals(update.getCallbackQuery().getMessage().getMessageId(),
                        getUserById(update.getCallbackQuery().getMessage().getChatId()).getLastMessage().getMessageId()) &&
                !getUserById(update.getCallbackQuery().getMessage().getChatId()).getTestState().isFinished()) {

            parseMessage(update.getCallbackQuery().getData(), getUserById(update.getCallbackQuery().getFrom().getId()));
        } else if(update.hasCallbackQuery() && !getUserById(update.getCallbackQuery().getMessage().getChatId()).getTestState().isFinished()){
            sendText(update.getCallbackQuery().getMessage().getChatId(), "Вы должны отвечать только на последний вопрос");
//            sendLastMessage(getUserById(update.getCallbackQuery().getMessage().getChatId()).getLastMessage());
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
            user.clearAll();
        } else if (user.getState() == null) {
            sendText(user.getChatId(), "Нет такой комманды");
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
        user.setState(States.GET_REVIEW);


        sendText(user.getChatId(), "Откуда вы о нас услышали?");
    }

    private void getReviewHandler(String textMsg, UserInfoDTO user) {
        user.setReview(textMsg);
        user.setState(States.TEST_TODO);

        sendStartButton(user.getChatId(), "Ну что же, приступим к тесту.\nНажите кнопку \"начать тест\", когда будете готовы.");
    }

    private void testToDoHandler(String textMsg, UserInfoDTO user) {
        if (Objects.equals(textMsg, "Начать")) {
            user.setState(States.QUESTION_TO_SEND);
            questionToSendHandler(user);
        }
        else
            sendStartButton(user.getChatId(), "Ну что же, приступим к тесту.\nНажите кнопку \"начать тест\", когда будете готовы.");
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
                        "Вы молодец, Вам осталось совсем немного, и скоро мы свяжемся для прохождения устного тестирования"
                );
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
            user.setLastMessage(execute(sm));
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

//    private void sendLastMessage(Message message) {
//        var sm = SendMessage.builder()
//                .chatId(message.getChatId().toString())
//                .text(message.getText()).build();
//
//        try {
//            execute(sm);
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//        }
//    }

}
