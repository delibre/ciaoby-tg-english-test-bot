package by.ciao.EnglishSchoolBot.states;

import by.ciao.EnglishSchoolBot.bot.ServiceCallback;
import by.ciao.EnglishSchoolBot.enums.StateEnum;
import by.ciao.EnglishSchoolBot.states.statesservice.AbstractState;
import by.ciao.EnglishSchoolBot.states.statesservice.UserMessageHandlerState;
import by.ciao.EnglishSchoolBot.user.User;
import by.ciao.EnglishSchoolBot.utils.BotResponses;
import by.ciao.EnglishSchoolBot.utils.KeyboardCreator;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class GetFullNameState extends AbstractState implements UserMessageHandlerState {
    public GetFullNameState(final ServiceCallback serviceCallback) {
        super(serviceCallback);
    }

    @Override
    public void apply(final String textMsg, final User user) throws TelegramApiException {
        user.setFullName(textMsg);
        user.setState(StateEnum.GET_PHONE);

        sendPhoneButton(user);
    }

    private void sendPhoneButton(final User user) throws TelegramApiException {
        var sm = createMessage(user.getChatId(), BotResponses.askForPhone());
        sm.setReplyMarkup(KeyboardCreator.createReplyKeyboard(BotResponses.sharePhoneButton(), true));

        getServiceCallback().execute(sm);
    }
}
