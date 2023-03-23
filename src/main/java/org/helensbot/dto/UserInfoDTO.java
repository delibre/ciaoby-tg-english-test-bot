package org.helensbot.dto;

import org.helensbot.englishtest.UsersTestState;
import org.helensbot.enums.States;
import org.telegram.telegrambots.meta.api.objects.Message;

public class UserInfoDTO {
    private States state = null;
    private Message lastMessage;
    private Long id;
    private String name;
    private String surname;
    private String username;
    private String phoneNumber;
    private String review;
    private UsersTestState testState = new UsersTestState();

    public UserInfoDTO(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public void clearAll() {
        this.name = "";
        this.surname = "";
        this.username = "";
        this.phoneNumber = "";
        this.review = "";
        testState = new UsersTestState();
    }

    public States getState() {
        return state;
    }

    public void setState(States state) {
        this.state = state;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public UsersTestState getTestState() {
        return testState;
    }

    public void setTestState(UsersTestState testState) {
        this.testState = testState;
    }
}
