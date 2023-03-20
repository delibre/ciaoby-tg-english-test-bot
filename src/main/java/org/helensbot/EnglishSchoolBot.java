package org.helensbot;

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
            dto.add(new UserInfoDTO(id));

        parseMessage(msg.getText(), getUserById(id));
    }

    private UserInfoDTO getUserById(Long id) {
        for (UserInfoDTO userInfoDTO : dto) {
            if (Objects.equals(userInfoDTO.getId(), id)) {
                return userInfoDTO;
            }
        }

        throw new AssertionError();
    }

    private boolean contains(Long id) {
        boolean containsId = false;

        for(UserInfoDTO userInfoDTO : dto) {
            if(Objects.equals(userInfoDTO.getId(), id)) {
                containsId = true;
                break;
            }
        }

        return containsId;
    }

    @Override
    public String getBotUsername() {
        return "English_Schoo_lBot";
    }

    @Override
    public String getBotToken() {
        return "5915260285:AAGpgfcCYCu6kxD38WcE6QYVFXCv4JjbdwA";
    }

    public void parseMessage(String textMsg, UserInfoDTO user) {
        switch (user.getState()) {
            case START:
                startHandler(user);
                break;
            case GETNAME:
                getnameHandler(textMsg, user);
                break;
            case GETSURNAME:
                getsurnameHandler(textMsg, user);
                break;
            case GETUSERNAME:
                getusernameHandler(textMsg, user);
                break;
            case GETPHONE:
                getphoneHandler(textMsg, user);
                break;
            case GETREVIEW:
                getreviewHandler(textMsg, user);
                break;
            case TEST:
                testHandler();
            default:
                throw new IllegalStateException();
        }

    }

    private void startHandler(UserInfoDTO user) {
        user.setState(States.GETNAME);

        sendText(user.getId(), "Привет!\n\n" +
                                    "Сейчас мы проверим Ваши знания английского\uD83D\uDD25\n" +
                                    "Но для начала давайте познакомимся\uD83D\uDE42\n\n" +
                                    "Введите, пожалуйста, Ваше имя");
    }

    private void getnameHandler(String textMsg, UserInfoDTO user) {
        user.setName(textMsg);
        user.setState(States.GETSURNAME);

        sendText(user.getId(), "Теперь введите, пожалуйста, Фамилию");
    }

    private void getsurnameHandler(String textMsg, UserInfoDTO user) {
        user.setSurname(textMsg);
        user.setState(States.GETUSERNAME);

        sendText(user.getId(), "Теперь введите, пожалуйста, юзернейм в телеграм");
    }

    private void getusernameHandler(String textMsg, UserInfoDTO user) {
        user.setUsername(textMsg);
        user.setState(States.GETPHONE);

        sendText(user.getId(), "Теперь введите, пожалуйста, номер телефона");
    }

    private void getphoneHandler(String textMsg, UserInfoDTO user) {
        user.setPhoneNumber(textMsg);
        user.setState(States.GETREVIEW);

        sendText(user.getId(), "Откуда вы о нас услышали?");
    }

    private void getreviewHandler(String textMsg, UserInfoDTO user) {
        user.setReview(textMsg);
        user.setState(States.TEST);

        sendText(user.getId(), "Ну что же, приступим к тесту. Нажите кнопку \"начать тест\", когда будете готовы.");
    }

    private void testHandler(){}

    public void sendText(Long who, String what){
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(what).build();    //Message content
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

}
