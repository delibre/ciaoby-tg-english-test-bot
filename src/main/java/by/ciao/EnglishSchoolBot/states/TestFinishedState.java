package by.ciao.EnglishSchoolBot.states;

import by.ciao.EnglishSchoolBot.bot.ServiceCallback;
import by.ciao.EnglishSchoolBot.user.User;
import by.ciao.EnglishSchoolBot.enums.StateEnum;
import by.ciao.EnglishSchoolBot.states.statesservice.AbstractState;
import by.ciao.EnglishSchoolBot.states.statesservice.UserHandlerState;
import by.ciao.EnglishSchoolBot.utils.BotResponses;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TestFinishedState extends AbstractState implements UserHandlerState {
    public TestFinishedState(final ServiceCallback serviceCallback) {
        super(serviceCallback);
    }

    @Override
    public void apply(final User user) throws TelegramApiException {
        user.setState(StateEnum.INFO_SENT);
        deleteMessage(user);
        sendText(user.getChatId(), BotResponses.testFinished(user));
        sendDataToAdmin(user);
    }

    private void deleteMessage(final User user) throws TelegramApiException {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(user.getChatId().toString());
        deleteMessage.setMessageId(user.getLastMessage().getMessageId());

        getServiceCallback().execute(null, deleteMessage, null);
    }

    private void sendDataToAdmin(final User user) throws TelegramApiException {
        getServiceCallback().execute(SendMessage.builder()
                            .chatId("5105539803").
                            text(BotResponses.dataForAdmin(user)).build(), null, null);
    }
}
