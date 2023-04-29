package by.ciao.englishtest;

import java.util.ArrayList;

public record Question(int numberOfQuestion, String question, ArrayList<String> answers, int correctAnswerIndex) {
    public String getCorrectAnswer() {
        return answers.get(correctAnswerIndex - 1);
    }
}
