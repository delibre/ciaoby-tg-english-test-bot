package org.ciaobyTestBot.states;

import org.ciaobyTestBot.bot.ServiceCallback;
import org.ciaobyTestBot.dto.UserInfoDTO;
import org.ciaobyTestBot.enums.States;
import org.ciaobyTestBot.states.statesservice.State;
import org.ciaobyTestBot.states.statesservice.AbstractState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class QuestionToSendState extends AbstractState {
    public QuestionToSendState(ServiceCallback serviceCallback) {
        super(serviceCallback, States.QUESTION_TO_SEND);
    }

    @Override
    public void apply(String textMsg, UserInfoDTO user) {
        if (user.getTestState().isFinished()) {
            user.setState(States.TEST_ENDED);
            State state = new TestEndedState(getServiceCallback());
            state.apply(textMsg, user);
            return;
        }

        sendQuestion(user);
        user.setState(States.CHECK_ANSWER);
    }

    private void sendQuestion(UserInfoDTO user) {
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
            user.setLastMessage(getServiceCallback().execute(sm, null, null));
        }
    }
}
