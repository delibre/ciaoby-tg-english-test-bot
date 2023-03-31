package org.ciaobyTestBot.bot;

import org.ciaobyTestBot.dto.UserInfoDTO;
import org.ciaobyTestBot.enums.States;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class CiaoByBot extends TelegramLongPollingBot {
    private final BotService service = new BotService((sm, dm, em) -> {
        Message msg = null;

        try {
            if (dm == null && em == null) msg = execute(sm);
            else if (sm == null && em == null) execute(dm);
            else if (dm == null && sm == null) execute(em);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        return msg;
    });

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()){
            var msg = update.getMessage();
            var id = msg.getChatId();

            if(service.contains(id) && service.getUserById(id).getState() == States.CHECK_ANSWER) {
                sendText(service.getUserById(id).getChatId(), "Отвечать можно только нажав кнопку с одним из вариантов ответа");
                return;
            }
            if (!service.contains(id))  service.dto.add(new UserInfoDTO(id, msg.getFrom().getUserName()));

            parseMessage(msg.getText(), service.getUserById(id));

        }  else if (update.hasMessage() && update.getMessage().getContact() != null) {
            service.getPhoneNumberHandler(update.getMessage().getContact().getPhoneNumber(),
                                    service.getUserById(update.getMessage().getChatId()));

        } else if (update.hasCallbackQuery() && service.contains(update.getCallbackQuery().getFrom().getId())) {
            var qry = update.getCallbackQuery();
            parseMessage(qry.getData(), service.getUserById(qry.getFrom().getId()));

            try {
                execute(AnswerCallbackQuery.builder()
                        .callbackQueryId(qry.getId()).build());
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
                service.startHandler(user);
                break;
            case GET_NAME_AND_SURNAME:
                service.getNameAndSurnameHandler(textMsg, user);
                break;
            case GET_PHONE_NUMBER:
                service.getPhoneNumberHandler(textMsg, user);
                break;
            case GET_REVIEW:
                service.getReviewHandler(textMsg, user);
                break;
            case TEST_TODO:
                service.testToDoHandler(textMsg, user);
                break;
            case QUESTION_TO_SEND:
                service.questionToSendHandler(user);
                break;
            case CHECK_ANSWER:
                service.checkAnswerHandler(textMsg, user);
                break;
            case TEST_ENDED:
                service.testEndedHandler(user);
                break;
            case END_ALL:
                service.testEndAllHandler(user);
                break;
            default:
                throw new IllegalStateException();
        }
    }

    public void sendText(Long who, String what){
        var sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what).build();

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
