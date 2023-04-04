package by.ciao.EnglishSchoolBot.states;

import by.ciao.EnglishSchoolBot.bot.ServiceCallback;
import by.ciao.EnglishSchoolBot.dto.UserInfoDTO;
import by.ciao.EnglishSchoolBot.enums.States;
import by.ciao.EnglishSchoolBot.states.statesservice.AbstractState;
import by.ciao.EnglishSchoolBot.states.statesservice.UserHandlerState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

public class TestEndedState extends AbstractState implements UserHandlerState {
    public TestEndedState(final ServiceCallback serviceCallback) {
        super(serviceCallback, States.TEST_ENDED);
    }

    @Override
    public void apply(final UserInfoDTO user) {
        user.setState(States.END_ALL);
        deleteMessage(user);
        sendText(user.getChatId(), "Вы ответили верно на " + user.getTestState().getCorrectAnswers() + " вопросов.\n" +
                "Ваш уровень английского " + user.getTestState().getResults() + ".\n" +
                "Вы молодец, Вам осталось совсем немного, и скоро мы свяжемся с Вами для прохождения устного тестирования\uD83D\uDE0A");
        sendDataToAdmin(user);
    }

    private void deleteMessage(final UserInfoDTO user) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(user.getChatId().toString());
        deleteMessage.setMessageId(user.getLastMessage().getMessageId());

        getServiceCallback().execute(null, deleteMessage, null);
    }

    private void sendDataToAdmin(final UserInfoDTO user) {
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
