package org.ciaobyTestBot.bot;

import org.ciaobyTestBot.dto.UserInfoDTO;
import org.ciaobyTestBot.enums.States;
import org.ciaobyTestBot.utils.Regex;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Objects;

public class CiaoByBot extends TelegramLongPollingBot {
    private final BotService service = new BotService();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()){
            var msg = update.getMessage();

            if(service.contains(msg.getChatId()) && service.getUserById(msg.getChatId()).getState() == States.CHECK_ANSWER) {
                sendText(service.getUserById(msg.getChatId()).getChatId(), "Отвечать можно только нажав кнопку с одним из вариантов ответа");
                return;
            }

            if (!service.contains(msg.getChatId()))
                service.dto.add(new UserInfoDTO(msg.getChatId(), msg.getFrom().getUserName()));

            parseMessage(msg.getText(), service.getUserById(msg.getChatId()));
        }
        else if (update.getMessage() != null && update.getMessage().getContact() != null) {
            getPhoneNumberHandler(update.getMessage().getContact().getPhoneNumber(),
                    service.getUserById(update.getMessage().getChatId()));
        }
        else if (update.hasCallbackQuery() && service.contains(update.getCallbackQuery().getFrom().getId())) {
            parseMessage(update.getCallbackQuery().getData(), service.getUserById(update.getCallbackQuery().getFrom().getId()));
            var close = AnswerCallbackQuery.builder()
                    .callbackQueryId(update.getCallbackQuery().getId()).build();

            try {
                execute(close);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "testForProd_bot";
    }

    @Override
    public String getBotToken() {
        return "6167400176:AAGg4892WjlM0mMxUgdmdoQXID2X9UPw4lo";
    }

    private void parseMessage(String textMsg, UserInfoDTO user) {
        if (textMsg.equals("/start")) {
            user.setState(States.START);
            user.clearTest();
        } else if (user.getState() == null) {
            sendText(user.getChatId(), "Нет такой команды");
            return;
        }

        if (textMsg.equals("Начать тестирование\uD83C\uDFC1")) {
            user.setState(States.QUESTION_TO_SEND);
            user.clearTest();
        }

        switch (user.getState()) {
            case START:
                startHandler(user);
                break;
            case GET_NAME_AND_SURNAME:
                getNameAndSurnameHandler(textMsg, user);
                break;
            case GET_PHONE_NUMBER:
                getPhoneNumberHandler(textMsg, user);
                break;
            case GET_REVIEW:
                getReviewHandler(textMsg, user);
                break;
            case TEST_TODO:
                testToDoHandler(textMsg, user);
                break;
            case QUESTION_TO_SEND:
                questionToSendHandler(user);
                break;
            case CHECK_ANSWER:
                checkAnswerHandler(textMsg, user);
                break;
            case TEST_ENDED:
                testEndedHandler(user);
                break;
            case END_ALL:
                testEndAllHandler(user);
                break;
            default:
                throw new IllegalStateException();
        }
    }

    private void startHandler(UserInfoDTO user) {
        sendText(user.getChatId(), service.serviceStartHandler(user));
    }

    private void getNameAndSurnameHandler(String textMsg, UserInfoDTO user) {
        service.serviceGetNameAndSurnameHandler(textMsg, user);
        sendPhoneButton(user);
    }

    private void getPhoneNumberHandler(String textMsg, UserInfoDTO user) {
        if (!Regex.checkPhoneNumber(textMsg)) {
            sendText(user.getChatId(), "Неверный формат номера. Попробуйте, пожалуйста, ещё раз");
            return;
        }

        service.serviceGetPhoneNumberHandler(textMsg, user);

        removeReplyKeyboard(user);
        sendOptionsForReview(user);
    }

    private void removeReplyKeyboard(UserInfoDTO user) {
        executeMessage(service.serviceRemoveReplyKeyboard(user), null);

        try {
            Thread.sleep(500); // Delay for 1.5 seconds (1500 milliseconds)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void getReviewHandler(String textMsg, UserInfoDTO user) {
        service.serviceGetReviewHandler(textMsg, user);
        sendStartButton(user);
    }

    private void testToDoHandler(String textMsg, UserInfoDTO user) {
        if (Objects.equals(textMsg, "Начать тестирование\uD83C\uDFC1")) {
            service.serviceTestToDoHandler(user);
            questionToSendHandler(user);
        }
        else sendStartButton(user);
    }
    private void questionToSendHandler(UserInfoDTO user){
        if (user.getTestState().isFinished()) {
            user.setState(States.TEST_ENDED);
            testEndedHandler(user);
            return;
        }

        sendQuestion(user);
        user.setState(States.CHECK_ANSWER);
    }

    private void checkAnswerHandler(String answer, UserInfoDTO user) {
        user.getTestState().registerAnswer(answer);
        questionToSendHandler(user);
    }

    private void testEndedHandler(UserInfoDTO user) {
        deleteMessage(user);
        sendText(user.getChatId(), service.serviceTestEndedHandler(user));
        sendDataToAdmin(user);
    }

    private void testEndAllHandler(UserInfoDTO user) {
        sendText(user.getChatId(),
                "Извините, не роспознал Вашу команду.\n" +
                        "Если хотите пройти тест заново - нажмите кнопку \"Начать тестирование\"\uD83E\uDD17"
        );
    }

    private void sendOptionsForReview(UserInfoDTO user) {
        executeMessage(service.serviceSendOptionsForReview(user), null);
    }

    private void sendPhoneButton(UserInfoDTO user) {
        executeMessage(service.serviceSendPhoneButton(user), null);
    }

    private void sendStartButton(UserInfoDTO user) {
        executeMessage(service.serviceSendStartButton(user), null);
    }

    private void sendQuestion(UserInfoDTO user) {
        try {
            if (user.getLastMessage() != null && user.getLastMessage().getChatId() != 0 && user.getLastMessage().getMessageId() != 0) {
                execute(service.serviceEditQuestion(user,
                        (InlineKeyboardMarkup) service.serviceSendQuestion(user).getReplyMarkup()));
            } else {
                user.setLastMessage(execute(service.serviceSendQuestion(user)));
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteMessage(UserInfoDTO user) {
        executeMessage(null, service.serviceDeleteMessage(user));
    }

    private void sendText(Long who, String what){
        var sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what).build();

        executeMessage(sm, null);
    }

    private void sendDataToAdmin(UserInfoDTO user) {
        executeMessage(service.serviceSendDataToAdmin(user), null);
    }
    
    public void executeMessage(SendMessage sm, DeleteMessage dm) {
        try {
            if (dm == null) execute(sm);
            else execute(dm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
