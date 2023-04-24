package by.ciao.states;

import by.ciao.bot.ServiceCallback;
import by.ciao.enums.StateEnum;
import by.ciao.states.statesservice.AbstractState;
import by.ciao.states.statesservice.UserMessageHandlerState;
import by.ciao.user.User;
import by.ciao.utils.Regex;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class GetPhoneState extends AbstractState implements UserMessageHandlerState {

    private Regex regex;

    public GetPhoneState(final ServiceCallback serviceCallback) {
        super(serviceCallback);
    }

    @Autowired
    public void setRegex(Regex regex) {
        this.regex = regex;
    }

    @Override
    public void apply(final String textMsg, final User user) throws TelegramApiException {
        if (!regex.isCorrectPhoneFormat(textMsg)) {
            sendText(user.getChatId(), getBotResponses().phoneFormatWarning());
            return;
        }

        user.setPhone(textMsg);
        user.setState(StateEnum.GET_REFERRAL);

        removeReplyKeyboard(user);
        sendOptionsForReferral(user);
    }

    private void removeReplyKeyboard(final User user) throws TelegramApiException {
        var replyKeyboardRemove = new ReplyKeyboardRemove(true);
        var removeMessage = new SendMessage(user.getChatId().toString(), getBotResponses().replyKeyboardRemoved());
        removeMessage.setReplyMarkup(replyKeyboardRemove);

        getServiceCallback().execute(removeMessage);

        setDelay();
    }

    private void sendOptionsForReferral(final User user) throws TelegramApiException {
        var sm = createMessage(user.getChatId(), getBotResponses().askReferral());
        sm.setReplyMarkup(getKeyboardCreator().createInlineKeyboard(getBotResponses().referralOptions()));

        getServiceCallback().execute(sm).ifPresent(user::setLastMessage);
    }

}
