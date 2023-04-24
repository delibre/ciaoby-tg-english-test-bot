package by.ciao.controller;

import by.ciao.user.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RestController {

    private final RestTemplate restTemplate;
    private HttpHeaders headers;
    @Value("url")
    private String url;
    private static final Logger log = LoggerFactory.getLogger(RestController.class);

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
