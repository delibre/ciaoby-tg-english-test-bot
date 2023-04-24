package by.ciao.bot;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Service
public interface ServiceCallback {

    Optional<Message> execute(Object obj) throws TelegramApiException;

}
