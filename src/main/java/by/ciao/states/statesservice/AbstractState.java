package by.ciao.states.statesservice;

import by.ciao.bot.ServiceCallback;
import by.ciao.controller.RestController;
import by.ciao.enums.StateEnum;
import by.ciao.states.TestFinishedState;
import by.ciao.user.User;
import by.ciao.utils.BotResponses;
import by.ciao.utils.KeyboardCreator;
import by.ciao.utils.LoggerMessages;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@Getter
@RequiredArgsConstructor
public abstract class AbstractState {

    @Value("admin_id")
    private String adminId;
    @Value("tech_admin_id")
    private String techAdminId;
    private final ServiceCallback serviceCallback;
    private static final Logger log = LoggerFactory.getLogger(AbstractState.class);
    private RestController restController;
    private BotResponses botResponses;
    private KeyboardCreator keyboardCreator;
    private LoggerMessages loggerMessages;

    @Autowired
    public void setLoggerMessages(LoggerMessages loggerMessages) {
        this.loggerMessages = loggerMessages;
    }

    @Autowired
    public void setKeyboardCreator(KeyboardCreator keyboardCreator) {
        this.keyboardCreator = keyboardCreator;
    }

    @Autowired
    public void setBotResponses(BotResponses botResponses) {
        this.botResponses = botResponses;
    }

    @Autowired
    void setRestController(RestController restController) {
        this.restController = restController;
    }

    protected void sendText(final Long id, final String textMsg) throws TelegramApiException {
        serviceCallback.execute(createMessage(id, textMsg));
    }

    protected void sendStartButton(final User user) throws TelegramApiException {
        var sm = createMessage(user.getChatId(), botResponses.startTest());
        sm.setReplyMarkup(keyboardCreator.createReplyKeyboard(botResponses.startTestButton(), false));

        serviceCallback.execute(sm);
    }

    protected SendMessage createMessage(final Long id, final String textMsg) {
        var sm = SendMessage.builder()
                .chatId(id)
                .text(textMsg).build();
        sm.setParseMode(ParseMode.HTML);
        return sm;
    }

    // Made to humanise bot's responses, so it is not sending lots of messages in one second.
    protected void setDelay() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            log.error(loggerMessages.setDelayException(), e);
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
            isTimeOver(user);
            changeStateToTestFinished(user);
            return true;
        }
        return false;
    }

    protected void changeStateToTestFinished(User user) throws Exception {
        user.setState(StateEnum.TEST_FINISHED);
        UserHandlerState state = new TestFinishedState(getServiceCallback());
        state.apply(user);
    }

    private void isTimeOver(User user) {
        if (!user.getTestState().isTimeOver()) {
            user.getTestState().getTimer().cancel();
        }
    }
}
