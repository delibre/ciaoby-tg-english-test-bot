package by.ciao.englishtest;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;

@Getter
@AllArgsConstructor
public class Question {
    private final int numberOfQuestion;
    private final String question;
    private final ArrayList<String> answers;
    private final int correctAnswerIndex;

    public String getCorrectAnswer() {
        return answers.get(correctAnswerIndex - 1);
    }
}
