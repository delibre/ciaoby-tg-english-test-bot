package by.ciao.EnglishSchoolBot.englishtest;

import lombok.Getter;
import by.ciao.EnglishSchoolBot.enums.EnglishLevel;

import java.util.LinkedList;
import java.util.Objects;

@Getter
public class UsersTestState {
    private final LinkedList<Question> questions;
    private int correctAnswers;
    private EnglishLevel lvl;

    public UsersTestState() throws Exception {
        this.questions = new LinkedList<>();
        this.questions.addAll(EnglishTest.getInstance().getQuestions());
        this.correctAnswers = 0;
    }

    public Question getCurrentQuestion() {
        return questions.getFirst();
    }

    public boolean isFinished() {
        return questions.isEmpty();
    }

    public void registerAnswer(final String answer) {
        if (Objects.equals(answer, questions.getFirst().getCorrectAnswer())) {
            correctAnswers++;
        }
        questions.removeFirst();
    }

    public EnglishLevel getResult() {
        if (!isFinished()) { throw new IllegalStateException(); }

        if (lvl != null) { return lvl; }

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
}
