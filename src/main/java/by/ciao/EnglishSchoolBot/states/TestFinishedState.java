package by.ciao.EnglishSchoolBot.states;

import by.ciao.EnglishSchoolBot.bot.ServiceCallback;
import by.ciao.EnglishSchoolBot.enums.StateEnum;
import by.ciao.EnglishSchoolBot.states.statesservice.AbstractState;
import by.ciao.EnglishSchoolBot.states.statesservice.UserHandlerState;
import by.ciao.EnglishSchoolBot.user.User;
import by.ciao.EnglishSchoolBot.utils.AppConfig;
import by.ciao.EnglishSchoolBot.utils.BotResponses;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TestFinishedState extends AbstractState implements UserHandlerState {
    public TestFinishedState(final ServiceCallback serviceCallback) {
        super(serviceCallback);
    }

    @Override
    public void apply(final User user) throws Exception {
        user.setState(StateEnum.INFO_SENT);
        deleteLastMessage(user);

        sendText(user.getChatId(), BotResponses.userAnswers(user).toString());
        sendDataToAdmin(user);
    }

    private void deleteLastMessage(final User user) throws TelegramApiException {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(user.getChatId().toString());
        deleteMessage.setMessageId(user.getLastMessage().getMessageId());

        getServiceCallback().execute(deleteMessage);
    }

    private void sendDataToAdmin(final User user) throws TelegramApiException {
        getServiceCallback().execute(createMessage(Long.parseLong(AppConfig.getProperty("admin_id")), BotResponses.dataForAdmin(user)));
        getServiceCallback().execute(createMessage(Long.parseLong(AppConfig.getProperty("tech_admin_id")), BotResponses.dataForAdmin(user)));
    }
}
