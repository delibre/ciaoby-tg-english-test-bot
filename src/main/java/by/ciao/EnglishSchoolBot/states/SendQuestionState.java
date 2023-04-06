package by.ciao.EnglishSchoolBot.states;

import by.ciao.EnglishSchoolBot.bot.ServiceCallback;
import by.ciao.EnglishSchoolBot.enums.StateEnum;
import by.ciao.EnglishSchoolBot.states.statesservice.AbstractState;
import by.ciao.EnglishSchoolBot.states.statesservice.UserHandlerState;
import by.ciao.EnglishSchoolBot.user.User;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class SendQuestionState extends AbstractState implements UserHandlerState {
    public SendQuestionState(final ServiceCallback serviceCallback) {
        super(serviceCallback);
    }

    @Override
    public void apply(final User user) throws TelegramApiException {
        if (testFinished(user)) { return; }
        sendQuestion(user);
        user.setState(StateEnum.CHECK_ANSWER);
    }

    private boolean testFinished(User user) throws TelegramApiException {
        if (user.getTestState().isFinished()) {
            user.setState(StateEnum.TEST_FINISHED);
            UserHandlerState state = new TestFinishedState(getServiceCallback());
            state.apply(user);
            return true;
        }
        return false;
    }

    private void sendQuestion(final User user) throws TelegramApiException {
        var sm = createMessage(user.getChatId(), user.getTestState().getCurrentQuestion().getNumberOfQuestion()
                + ". " + user.getTestState().getCurrentQuestion().getQuestion());

        var markup = createKeyboard(user);
        sm.setReplyMarkup(markup);

        if (user.getLastMessage() != null) {
            getServiceCallback().execute(editMessage(user, markup));
        } else {
            getServiceCallback().execute(sm).ifPresent(user::setLastMessage);
        }
    }

    private InlineKeyboardMarkup createKeyboard(User user) {
        var markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> rowInline;

        for (String answer : user.getTestState().getCurrentQuestion().getAnswers()) {
            rowInline = new ArrayList<>();

            var inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(answer);
            inlineKeyboardButton.setCallbackData(answer);

            rowInline.add(inlineKeyboardButton);
            keyboard.add(rowInline);
        }

        rowInline = new ArrayList<>();

        var inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Пропустить");
        inlineKeyboardButton.setCallbackData("Пропустить");

        rowInline.add(inlineKeyboardButton);
        keyboard.add(rowInline);

        markup.setKeyboard(keyboard);

        return markup;
    }

    private EditMessageText editMessage(User user, InlineKeyboardMarkup markup) {
        var editMessageText = EditMessageText.builder()
                .chatId(user.getLastMessage().getChatId().toString())
                .messageId(user.getLastMessage().getMessageId())
                .text(user.getTestState().getCurrentQuestion().getNumberOfQuestion() + ". "
                        + user.getTestState().getCurrentQuestion().getQuestion()).build();
        editMessageText.setReplyMarkup(markup);

        return editMessageText;
    }
}
