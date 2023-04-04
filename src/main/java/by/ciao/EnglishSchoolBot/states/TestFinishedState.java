package by.ciao.EnglishSchoolBot.states;

import by.ciao.EnglishSchoolBot.bot.ServiceCallback;
import by.ciao.EnglishSchoolBot.user.User;
import by.ciao.EnglishSchoolBot.enums.StateEnum;
import by.ciao.EnglishSchoolBot.states.statesservice.AbstractState;
import by.ciao.EnglishSchoolBot.states.statesservice.UserHandlerState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

public class TestFinishedState extends AbstractState implements UserHandlerState {
    public TestFinishedState(final ServiceCallback serviceCallback) {
        super(serviceCallback);
    }

    @Override
    public void apply(final User user) {
        user.setState(StateEnum.INFO_SENT);
        deleteMessage(user);
        sendText(user.getChatId(), "Вы ответили верно на " + user.getTestState().getCorrectAnswers() + " вопросов.\n" +
                "Ваш уровень английского " + user.getTestState().getResults() + ".\n" +
                "Вы молодец, Вам осталось совсем немного, и скоро мы свяжемся с Вами для прохождения устного тестирования\uD83D\uDE0A");
        sendDataToAdmin(user);
    }

    private void deleteMessage(final User user) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(user.getChatId().toString());
        deleteMessage.setMessageId(user.getLastMessage().getMessageId());

        getServiceCallback().execute(null, deleteMessage, null);
    }

    private void sendDataToAdmin(final User user) {
        getServiceCallback().execute(SendMessage.builder()
                            .chatId("5105539803").
                            text(   "Имя и Фамилия: " + user.getFullName() + "\n" +
                                    "Номер телефона: " + user.getPhone() + "\n" +
                                    "Ник в телеграмм: @" + user.getUsername() + "\n" +
                                    "Откуда узнали: " + user.getGetReferral() + "\n" +
                                    "Отвечено верно на: " + user.getTestState().getCorrectAnswers() + " вопросов.\n" +
                                    "Уровень английского: " + user.getTestState().getLvl()).build(), null, null);
    }
}
