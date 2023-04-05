package by.ciao.EnglishSchoolBot.englishtest;

import by.ciao.EnglishSchoolBot.utils.ReadFile;
import lombok.Getter;

import java.util.LinkedList;

@Getter
public final class EnglishTest {
    private static EnglishTest INSTANCE;
    private final LinkedList<Question> questions = new LinkedList<>();


    private EnglishTest() throws Exception {
        this.questions.addAll(ReadFile.readFile());
    }

    public static EnglishTest getInstance() throws Exception {
        if (INSTANCE == null) {
            INSTANCE = new EnglishTest();
        }

        return INSTANCE;
    }
}
