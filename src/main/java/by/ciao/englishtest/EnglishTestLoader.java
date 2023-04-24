package by.ciao.englishtest;

import by.ciao.utils.ReadFile;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
@Getter
public class EnglishTestLoader {

    private final LinkedList<Question> questions = new LinkedList<>();

    public EnglishTestLoader() throws Exception {
        this.questions.addAll(ReadFile.readFile());
    }

}
