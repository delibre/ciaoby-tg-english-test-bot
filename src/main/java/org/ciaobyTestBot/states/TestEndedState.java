package org.ciaobyTestBot.states;

import org.ciaobyTestBot.bot.ServiceCallback;
import org.ciaobyTestBot.dto.UserInfoDTO;
import org.ciaobyTestBot.enums.States;
import org.ciaobyTestBot.states.statesservice.AbstractState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

public class TestEndedState extends AbstractState {
    public TestEndedState(ServiceCallback serviceCallback) {
        super(serviceCallback, States.TEST_ENDED);
    }

    @Override
    public void apply(String textMsg, UserInfoDTO user) {
        user.setState(States.END_ALL);
        deleteMessage(user);
        sendText(user.getChatId(), "Вы ответили верно на " + user.getTestState().getCorrectAnswers() + " вопросов.\n" +
                "Ваш уровень английского " + user.getTestState().getResults() + ".\n" +
                "Вы молодец, Вам осталось совсем немного, и скоро мы свяжемся с Вами для прохождения устного тестирования\uD83D\uDE0A");
        sendDataToAdmin(user);
    }

    private void deleteMessage(UserInfoDTO user) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(user.getChatId().toString());
        deleteMessage.setMessageId(user.getLastMessage().getMessageId());

        getServiceCallback().execute(null, deleteMessage, null);
    }

    private void sendDataToAdmin(UserInfoDTO user) {
        getServiceCallback().execute(SendMessage.builder()
                            .chatId("5105539803").
                            text(   "Имя и Фамилия: " + user.getNameAndSurname() + "\n" +
                                    "Номер телефона: " + user.getPhoneNumber() + "\n" +
                                    "Ник в телеграмм: @" + user.getUsername() + "\n" +
                                    "Откуда узнали: " + user.getReview() + "\n" +
                                    "Отвечено верно на: " + user.getTestState().getCorrectAnswers() + " вопросов.\n" +
                                    "Уровень английского: " + user.getTestState().getLvl()).build(), null, null);
    }
}
