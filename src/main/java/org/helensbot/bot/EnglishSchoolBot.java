package org.helensbot.bot;

import org.helensbot.dto.UserInfoDTO;
import org.helensbot.enums.States;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Objects;

public class EnglishSchoolBot extends TelegramLongPollingBot {
    private final ArrayList<UserInfoDTO> dto = new ArrayList<>();

    @Override
    public void onUpdateReceived(Update update) {
        var msg = update.getMessage();
        var user = msg.getFrom();
        var id = user.getId();

        if(!contains(id))
            dto.add(new UserInfoDTO(id, user.getUserName()));

        parseMessage(msg.getText(), getUserById(id));
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
        for (UserInfoDTO userInfoDTO : dto)
            if (Objects.equals(userInfoDTO.getId(), id))
                return userInfoDTO;

        throw new AssertionError();
    }

    private boolean contains(Long id) {
        boolean containsId = false;

        for(UserInfoDTO userInfoDTO : dto)
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
            case GETNAME:
                getNameHandler(textMsg, user);
                break;
            case GETSURNAME:
                getSurnameHandler(textMsg, user);
                break;
            case GETREVIEW:
                getReviewHandler(textMsg, user);
                break;
            case TEST:
                testHandler();
            default:
                throw new IllegalStateException();
        }
    }

    private void startHandler(UserInfoDTO user) {
        user.setState(States.GETNAME);

        sendText(user.getId(), "Привет!\uD83D\uDC4B\n\n" +
                                    "Сейчас мы проверим Ваши знания английского\uD83D\uDD25\n" +
                                    "Но для начала давайте познакомимся\uD83D\uDE42\n\n" +
                                    "Введите, пожалуйста, Ваше имя");
    }

    private void getNameHandler(String textMsg, UserInfoDTO user) {
        user.setName(textMsg);
        user.setState(States.GETSURNAME);

        sendText(user.getId(), "Теперь введите, пожалуйста, Фамилию");
    }

    private void getSurnameHandler(String textMsg, UserInfoDTO user) {
        user.setSurname(textMsg);
        user.setState(States.GETREVIEW);

        sendText(user.getId(), "Откуда вы о нас услышали?");
    }

    private void getReviewHandler(String textMsg, UserInfoDTO user) {
        user.setReview(textMsg);
        user.setState(States.TEST);

        sendText(user.getId(), "Ну что же, приступим к тесту. Нажите кнопку \"начать тест\", когда будете готовы.");
    }

    private void testHandler(){}

    public void sendText(Long who, String what){
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
