package by.ciao.controller;

import by.ciao.user.User;
import by.ciao.utils.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public final class RestControllerSingleton {

    private static RestControllerSingleton INSTANCE;
    private final RestTemplate restTemplate;
    private final HttpHeaders headers;
    private final String url;
    private static final Logger log = LoggerFactory.getLogger(RestControllerSingleton.class);

    public RestControllerSingleton(RestTemplate restTemplate, HttpHeaders headers, String url) {
        this.restTemplate = restTemplate;
        this.headers = headers;
        this.url = url;
    }

    public static RestControllerSingleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RestControllerSingleton(new RestTemplate(), new HttpHeaders(), AppConfig.getProperty("url"));
        }

        return INSTANCE;
    }

    public void addUserToDB(User user) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("chatId", String.valueOf(user.getChatId()));
        requestBody.put("username", user.getUsername());

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        log.info(restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class).getBody());
    }

    public void updateContactInfoInDB(User user) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("fullName", user.getFullName());
        requestBody.put("phone", user.getPhone());
        requestBody.put("username", user.getUsername());
        requestBody.put("referral", user.getReferral());

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        log.info(restTemplate.exchange(
                url + "/update-contactinfo/" + user.getChatId(),
                HttpMethod.PUT, requestEntity, String.class).getBody());
    }

    public void updateTestInfoInDB(User user) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("englishLvl", String.valueOf(user.getTestState().getLvl()));
        requestBody.put("numOfCorrectAnswers", String.valueOf(user.getTestState().getCorrectAnswers()));

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        log.info(restTemplate.exchange(
                url + "/update-testinfo/" + user.getChatId(),
                HttpMethod.PUT, requestEntity, String.class).getBody());
    }

    public User getUserByChatId(Long chatId) {
        return restTemplate.exchange(url + "/" + chatId, HttpMethod.GET, null, User.class).getBody();
    }
}
