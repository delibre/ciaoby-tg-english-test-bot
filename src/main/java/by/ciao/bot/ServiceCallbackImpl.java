package by.ciao.bot;

import by.ciao.utils.LoggerMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Service
public class ServiceCallbackImpl implements ServiceCallback {

    private static final Logger log = LoggerFactory.getLogger(ServiceCallbackImpl.class);
    private LoggerMessages loggerMessages;

    @Autowired
    public void setLoggerMessages(LoggerMessages loggerMessages) {
        this.loggerMessages = loggerMessages;
    }

    @Override
    public Optional<Message> execute(Object obj) throws TelegramApiException {

        Optional<Message> msg = Optional.empty();

        try {
            if (obj instanceof SendMessage) {
                msg = execute(obj);
            } else if (obj instanceof DeleteMessage) {
                execute(obj);
            } else if (obj instanceof EditMessageText) {
                execute(obj);
            } else if (obj instanceof EditMessageReplyMarkup) {
                execute(obj);
            } else if (obj instanceof AnswerCallbackQuery) {
                execute(obj);
            } else {
                log.error(loggerMessages.argumentExceptionInServiceVar(), new IllegalArgumentException());
            }
        } catch (TelegramApiException e) {
            log.error(loggerMessages.tgApiExceptionInServiceVar(), e);
        }

        return msg;
    }

}
