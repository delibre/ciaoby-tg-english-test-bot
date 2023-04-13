package by.ciao.EnglishSchoolBot.englishtest;

import by.ciao.EnglishSchoolBot.enums.EnglishLevel;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Objects;

@Getter
public class UsersTestState {
    private final LinkedList<Question> questions;
    private final LinkedList<String> userAnswers;
    private int correctAnswers;
    private EnglishLevel lvl;
    private final Logger log;

    public UsersTestState() throws Exception {
        this.questions = new LinkedList<>();
        this.questions.addAll(EnglishTestSingleton.getInstance().getQuestions());
        this.userAnswers = new LinkedList<>();
        this.correctAnswers = 0;
        this.log = LoggerFactory.getLogger(UsersTestState.class);
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
        userAnswers.add(answer);
        questions.removeFirst();
    }

    public String getUserAnswer() {
        String userAnswer = userAnswers.getFirst();
        userAnswers.removeFirst();
        return userAnswer;
    }

    public EnglishLevel getResult() {
        if (!isFinished()) {
            log.error("User doesn't finished the test yet", new IllegalStateException());
        }

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
