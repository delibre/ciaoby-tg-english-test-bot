package by.ciao.englishtest;

import by.ciao.utils.ReadFile;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
@Getter
@NoArgsConstructor
public class EnglishTestLoader {

    private final LinkedList<Question> questions = new LinkedList<>();
    private ReadFile readFile;

    @PostConstruct
    public void init() throws Exception {
        this.questions.addAll(readFile.readFile());
    }

    @Autowired
    public void setReadFile(ReadFile readFile) {
        this.readFile = readFile;
    }
}
