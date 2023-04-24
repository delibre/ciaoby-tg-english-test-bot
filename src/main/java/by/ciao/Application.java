package by.ciao;

import by.ciao.bot.CiaoByBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@EnableScheduling
public class Application implements ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static ApplicationContext applicationContext;

    public static void main(String[] args) throws TelegramApiException {
        applicationContext = SpringApplication.run(Application.class, args);

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            log.info("Registering bot...");
            CiaoByBot ciaoByBot = applicationContext.getBean(CiaoByBot.class);
            telegramBotsApi.registerBot(ciaoByBot);
        } catch (TelegramApiException e) {
            log.error("Failed to register bot (check internet connection / bot token or make sure only one instance of bot is running).", e);
        }
        log.info("Telegram bot is ready to accept updates from user......");
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        Application.applicationContext = applicationContext;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
