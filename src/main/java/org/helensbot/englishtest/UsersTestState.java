package org.helensbot.englishtest;

import org.helensbot.enums.EnglishLevel;

import java.util.LinkedList;
import java.util.Objects;

public class UsersTestState {
    private final LinkedList<Question> questions = new LinkedList<>();
    private int correctAnswers = 0;
    private EnglishLevel lvl;

    public UsersTestState() {
        this.questions.addAll(EnglishTest.questions);
    }

    public Question getCurrentQuestion() {
        return questions.getFirst();
    }

    public boolean isFinished() {
        return questions.isEmpty();
    }

    public void registerAnswer(String answer) {
        if (Objects.equals(answer, questions.getFirst().getCorrectAnswer()))
            correctAnswers++;
        questions.removeFirst();
    }

    public EnglishLevel getResults() {
        if (!isFinished()) throw new IllegalStateException();

        if (lvl != null) return lvl;

        if (correctAnswers > 28) {
            lvl = EnglishLevel.C1;
            return lvl;
        }
        if (correctAnswers > 25) {
            lvl = EnglishLevel.B2;
            return lvl;
        }
        if (correctAnswers > 19) {
            lvl = EnglishLevel.B1;
            return lvl;
        }
        if (correctAnswers > 13) {
            lvl = EnglishLevel.A2;
            return lvl;
        }
        if (correctAnswers > 6) {
            lvl = EnglishLevel.A1;
            return lvl;
        }

        lvl = EnglishLevel.A0;
        return lvl;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }
}
