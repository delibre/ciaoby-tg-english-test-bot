package by.ciao.EnglishSchoolBot.utils;

import com.aspose.cells.Workbook;
import by.ciao.EnglishSchoolBot.englishtest.Question;

import java.io.InputStream;
import java.util.LinkedList;

public class ReadFile {

    public static LinkedList<Question> readFile() throws Exception {
        InputStream inputStream = ReadFile.class.getResourceAsStream("/questions30.ods");
        var book = new Workbook(inputStream);
        var sheet = book.getWorksheets().get(0);
        var cells = sheet.getCells();
        LinkedList<Question> questions = new LinkedList<>();

        for (int row = 0; row < cells.getMaxDataRow() + 1; row++) {
            questions.add(new Question(
                    row+1,
                    cells.get(row, 1).getStringValue(),
                    cells.get(row, 2).getStringValue().split("\\|"),
                    cells.get(row, 3).getIntValue()
            ));
        }

        return questions;
    }
}
