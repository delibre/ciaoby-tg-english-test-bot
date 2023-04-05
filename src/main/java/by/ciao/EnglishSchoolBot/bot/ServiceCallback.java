package by.ciao.EnglishSchoolBot.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

public interface ServiceCallback {
    Optional<Message> execute(final SendMessage sm, final DeleteMessage dm, final EditMessageText em) throws TelegramApiException;
}
