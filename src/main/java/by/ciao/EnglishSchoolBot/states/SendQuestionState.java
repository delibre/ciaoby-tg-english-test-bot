package by.ciao.EnglishSchoolBot.states;

import by.ciao.EnglishSchoolBot.bot.ServiceCallback;
import by.ciao.EnglishSchoolBot.user.User;
import by.ciao.EnglishSchoolBot.enums.StateEnum;
import by.ciao.EnglishSchoolBot.states.statesservice.AbstractState;
import by.ciao.EnglishSchoolBot.states.statesservice.UserHandlerState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
        if (user.getTestState().isFinished()) {
            user.setState(StateEnum.TEST_FINISHED);
            UserHandlerState state = new TestFinishedState(getServiceCallback());
            state.apply(user);
            return;
        }

        sendQuestion(user);
        user.setState(StateEnum.CHECK_ANSWER);
    }

    private void sendQuestion(final User user) throws TelegramApiException {
        var sm = SendMessage.builder()
                .chatId(user.getChatId().toString())
                .text(user.getTestState().getCurrentQuestion().getNumberOfQuestion() + ". "
                        + user.getTestState().getCurrentQuestion().getQuestion()).build();

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
        sm.setReplyMarkup(markup);

        if (user.getLastMessage() != null) {
            var editMessageText = EditMessageText.builder()
                    .chatId(user.getLastMessage().getChatId().toString())
                    .messageId(user.getLastMessage().getMessageId())
                    .text(user.getTestState().getCurrentQuestion().getNumberOfQuestion() + ". "
                            + user.getTestState().getCurrentQuestion().getQuestion()).build();

            editMessageText.setReplyMarkup(markup);

            getServiceCallback().execute(null, null, editMessageText);
        } else {
            getServiceCallback().execute(sm, null, null).ifPresent(user::setLastMessage);
        }
    }
}
