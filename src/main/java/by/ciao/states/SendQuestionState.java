package by.ciao.states;

import by.ciao.bot.ServiceCallback;
import by.ciao.enums.StateEnum;
import by.ciao.states.statesservice.AbstractState;
import by.ciao.states.statesservice.UserHandlerState;
import by.ciao.user.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SendQuestionState extends AbstractState implements UserHandlerState {

    public SendQuestionState(final ServiceCallback serviceCallback) {
        super(serviceCallback);
    }

    @Override
    public void apply(final User user) throws Exception {
        if (testFinished(user)) { return; }
        sendQuestion(user);
        user.setState(StateEnum.CHECK_ANSWER);
    }

    private void sendQuestion(final User user) throws TelegramApiException {
        var sm = createMessage(user.getChatId(), getBotResponses().getQuestion(user));

        var markup = getKeyboardCreator().createInlineKeyboard(getBotResponses().optionsForAnswers(user));
        sm.setReplyMarkup(markup);

        if (user.getLastMessage() != null) {
            getServiceCallback().execute(editMessage(user, markup, getBotResponses().getQuestion(user)));
        } else {
            getServiceCallback().execute(sm).ifPresent(user::setLastMessage);
        }
    }

}
