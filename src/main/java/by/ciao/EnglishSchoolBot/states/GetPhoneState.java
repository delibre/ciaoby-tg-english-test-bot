package by.ciao.EnglishSchoolBot.states;

import by.ciao.EnglishSchoolBot.bot.ServiceCallback;
import by.ciao.EnglishSchoolBot.enums.StateEnum;
import by.ciao.EnglishSchoolBot.states.statesservice.AbstractState;
import by.ciao.EnglishSchoolBot.states.statesservice.UserMessageHandlerState;
import by.ciao.EnglishSchoolBot.user.User;
import by.ciao.EnglishSchoolBot.utils.BotResponses;
import by.ciao.EnglishSchoolBot.utils.KeyboardCreator;
import by.ciao.EnglishSchoolBot.utils.Regex;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class GetPhoneState extends AbstractState implements UserMessageHandlerState {
    public GetPhoneState(final ServiceCallback serviceCallback) {
        super(serviceCallback);
    }

    @Override
    public void apply(final String textMsg, final User user) throws TelegramApiException {
        if (!Regex.isCorrectPhoneFormat(textMsg)) {
            sendText(user.getChatId(), BotResponses.phoneForamtWarning());
            return;
        }

        user.setPhone(textMsg);
        user.setState(StateEnum.GET_REFERRAL);

        removeReplyKeyboard(user);
        sendOptionsForReferral(user);
    }

    private void removeReplyKeyboard(final User user) throws TelegramApiException {
        var replyKeyboardRemove = new ReplyKeyboardRemove(true);
        var removeMessage = new SendMessage(user.getChatId().toString(), BotResponses.replyKeyboardRemoved());
        removeMessage.setReplyMarkup(replyKeyboardRemove);

        getServiceCallback().execute(removeMessage);

        setDelay(500); // Made to humanise bot's responses, so it is not sending lots of messages in one second.
    }

    private void sendOptionsForReferral(final User user) throws TelegramApiException {
        var sm = createMessage(user.getChatId(), BotResponses.askReferral());
        sm.setReplyMarkup(KeyboardCreator.createInlineKeyboard(BotResponses.referralOptions()));

        getServiceCallback().execute(sm).ifPresent(user::setLastMessage);
    }
}
