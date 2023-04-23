package by.ciao.bot;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

public interface ServiceCallback {
    Optional<Message> execute(Object obj) throws TelegramApiException;
}
