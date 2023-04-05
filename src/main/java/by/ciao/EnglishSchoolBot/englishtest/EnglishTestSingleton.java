package by.ciao.EnglishSchoolBot.englishtest;

import by.ciao.EnglishSchoolBot.utils.ReadFile;
import lombok.Getter;

import java.util.LinkedList;

@Getter
public final class EnglishTestSingleton {
    private static EnglishTestSingleton INSTANCE;
    private final LinkedList<Question> questions = new LinkedList<>();


    private EnglishTestSingleton() throws Exception {
        this.questions.addAll(ReadFile.readFile());
    }

    public static EnglishTestSingleton getInstance() throws Exception {
        if (INSTANCE == null) {
            INSTANCE = new EnglishTestSingleton();
        }

        return INSTANCE;
    }
}
