package by.ciao.EnglishSchoolBot.states.statesservice;

import by.ciao.EnglishSchoolBot.bot.ServiceCallback;
import by.ciao.EnglishSchoolBot.enums.StateEnum;
import by.ciao.EnglishSchoolBot.states.TestFinishedState;
import by.ciao.EnglishSchoolBot.user.User;
import by.ciao.EnglishSchoolBot.utils.BotResponses;
import by.ciao.EnglishSchoolBot.utils.ExceptionLogger;
import by.ciao.EnglishSchoolBot.utils.ExceptionMessages;
import by.ciao.EnglishSchoolBot.utils.KeyboardCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.logging.Level;

@AllArgsConstructor
@Getter
public abstract class AbstractState {
    private final ServiceCallback serviceCallback;

    protected void sendText(final Long id, final String textMsg) throws TelegramApiException {
        serviceCallback.execute(createMessage(id, textMsg));
    }

    protected void sendStartButton(final User user) throws TelegramApiException {
        var sm = createMessage(user.getChatId(), BotResponses.startTest());
        sm.setReplyMarkup(KeyboardCreator.createReplyKeyboard(BotResponses.startTestButton(), false));

        serviceCallback.execute(sm);
    }

    protected SendMessage createMessage(Long id, String textMsg) {
        var sm = SendMessage.builder()
                .chatId(id.toString())
                .text(textMsg).build();
        sm.setParseMode(ParseMode.HTML);
        return sm;
    }

    // Made to humanise bot's responses, so it is not sending lots of messages in one second.
    protected void setDelay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            ExceptionLogger.logException(Level.SEVERE, ExceptionMessages.setDelayException(), e);
        }
    }

    protected EditMessageText editMessage(User user, InlineKeyboardMarkup markup, String text) {
        var editMessageText = EditMessageText.builder()
                .chatId(user.getChatId().toString())
                .messageId(user.getLastMessage().getMessageId())
                .text(text).build();
        editMessageText.setReplyMarkup(markup);

        return editMessageText;
    }

    protected boolean testFinished(User user) throws Exception {
        if (user.getTestState().isFinished() || user.getTestState().isTimeOver()) {
            user.setState(StateEnum.TEST_FINISHED);
            UserHandlerState state = new TestFinishedState(getServiceCallback());
            state.apply(user);
            return true;
        }
        return false;
    }
}
