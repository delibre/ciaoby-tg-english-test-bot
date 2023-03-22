package org.helensbot.englishtest;

import java.util.Arrays;

public class Question {
    private final int numberOfQuestion;
    private final String question;
    private final String[] answers;
    private final int correctAnswer;

    public Question(int numberOfQuestion, String question, String[] answers, int correctAnswer) {
        this.numberOfQuestion = numberOfQuestion;
        this.question = question;
        this.answers = answers;
        this.correctAnswer = correctAnswer;
    }

    public int getNumberOfQuestion() {
        return numberOfQuestion;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getAnswers() {
        return answers;
    }

    public String getCorrectAnswer() {
        return answers[correctAnswer-1];
    }

    @Override
    public String toString() {
        return "Question{" +
                "question='" + question + '\'' +
                ", answers=" + Arrays.toString(answers) +
                '}';
    }
}
