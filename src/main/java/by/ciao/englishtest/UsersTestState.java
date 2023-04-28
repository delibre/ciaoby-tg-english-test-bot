package by.ciao.englishtest;

import by.ciao.enums.EnglishLevel;
import by.ciao.utils.AppConfig;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Timer;

@Getter @Setter
public class UsersTestState {
    private final LinkedList<Question> questions;
    private final LinkedList<String> userAnswers;
    private int correctAnswers;
    private EnglishLevel lvl;
    private final Timer timer;
    private LocalTime startTime;

    public UsersTestState() throws Exception {
        this.questions = new LinkedList<>();
        this.questions.addAll(EnglishTestSingleton.getInstance().getQuestions());
        this.userAnswers = new LinkedList<>();
        this.correctAnswers = 0;
        this.timer = new Timer();
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
        if (userAnswers.isEmpty()) {
            return null;
        }
        String userAnswer = userAnswers.getFirst();
        userAnswers.removeFirst();
        return userAnswer;
    }

    public EnglishLevel getResult() {
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

    public String countTime() {
        LocalTime currentTime = LocalTime.now();
        Duration elapsed = Duration.between(startTime, currentTime);
        Duration duration = Duration.ofMinutes(Long.parseLong(AppConfig.getProperty("test_duration")));

        Duration timeLeft = duration.minus(elapsed);

        long MM = timeLeft.toMinutesPart();
        long SS = timeLeft.toSecondsPart();

        return String.format("%02d:%02d", MM, SS);
    }

    public boolean isTimeOver() {
        LocalTime currentTime = LocalTime.now();
        Duration elapsed = Duration.between(startTime, currentTime);
        Duration duration = Duration.ofMinutes(Long.parseLong(AppConfig.getProperty("test_duration")));

        return elapsed.compareTo(duration) >= 0;
    }
}
