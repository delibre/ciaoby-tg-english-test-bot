package org.helensbot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class EnglishSchoolBot extends TelegramLongPollingBot{
    @Override
    public void onUpdateReceived(Update update) {
        try{
            if(update.hasMessage() && update.getMessage().hasText())
            {
                Message inMess = update.getMessage();
                String chatId = inMess.getChatId().toString();
                String response = parseMessage(inMess.getText());
                SendMessage outMess = new SendMessage();

                outMess.setChatId(chatId);
                outMess.setText(response);

                execute(outMess);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getBotUsername() {
        return "English_Schoo_lBot";
    }

    @Override
    public String getBotToken() {
        return "5915260285:AAGpgfcCYCu6kxD38WcE6QYVFXCv4JjbdwA";
    }

    public String parseMessage(String textMsg) {
        String response;

        if(textMsg.equals("/start"))
            response =  "Привет!\n\n" +
                        "Сейчас мы проверим Ваши знания английского\uD83D\uDD25\n" +
                        "Но для начала давайте познакомимся\uD83D\uDE42\n\n" +
                        "Введите пожалуйста Ваше имя";
        else
            response =  "Сообщение не распознано";

        return response;
    }
}
