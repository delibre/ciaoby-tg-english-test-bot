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
    private final String userDataProviderUrl;
    private static final Logger log = LoggerFactory.getLogger(RestControllerSingleton.class);

    public RestControllerSingleton(RestTemplate restTemplate, HttpHeaders headers, String url) {
        this.restTemplate = restTemplate;
        this.headers = headers;
        this.userDataProviderUrl = url;
    }

    public static RestControllerSingleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RestControllerSingleton(new RestTemplate(), new HttpHeaders(), AppConfig.getProperty("user_data_provider_url"));
        }

        return INSTANCE;
    }

    public void addUserToDB(User user) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("chatId", String.valueOf(user.getChatId()));
        requestBody.put("username", user.getUsername());

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        log.info(restTemplate.exchange(userDataProviderUrl, HttpMethod.POST, requestEntity, String.class).getBody());
    }

    public void updateContactInfo(User user) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("fullName", user.getFullName());
        requestBody.put("phone", user.getPhone());
        requestBody.put("username", user.getUsername());
        requestBody.put("referral", user.getReferral());

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        log.info(restTemplate.exchange(
                userDataProviderUrl + "/update-contactinfo/" + user.getChatId(),
                HttpMethod.PUT, requestEntity, String.class).getBody());
    }

    public void updateTestInfo(User user) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("englishLvl", String.valueOf(user.getTestState().getLvl()));
        requestBody.put("numOfCorrectAnswers", String.valueOf(user.getTestState().getCorrectAnswers()));

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        log.info(restTemplate.exchange(
                userDataProviderUrl + "/update-testinfo/" + user.getChatId(),
                HttpMethod.PUT, requestEntity, String.class).getBody());
    }

    public User getUserByChatId(Long chatId) {
        return restTemplate.exchange(userDataProviderUrl + "/" + chatId, HttpMethod.GET, null, User.class).getBody();
    }
}
