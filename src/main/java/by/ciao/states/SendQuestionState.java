package by.ciao.states;

import by.ciao.bot.ServiceCallback;
import by.ciao.enums.StateEnum;
import by.ciao.states.statesservice.AbstractState;
import by.ciao.states.statesservice.UserHandlerState;
import by.ciao.user.User;
import by.ciao.utils.BotResponses;
import by.ciao.utils.KeyboardCreator;
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
        var sm = createMessage(user.getChatId(), BotResponses.getQuestion(user));

        var markup = KeyboardCreator.createInlineKeyboard(BotResponses.optionsForAnswers(user));
        sm.setReplyMarkup(markup);

        if (user.getLastMessage() != null) {
            getServiceCallback().execute(editMessage(user, markup, BotResponses.getQuestion(user)));
        } else {
            getServiceCallback().execute(sm).ifPresent(user::setLastMessage);
        }
    }

}
