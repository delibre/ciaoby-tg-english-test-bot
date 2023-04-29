package by.ciao.utils;

import by.ciao.englishtest.Question;
import com.aspose.cells.Cells;
import com.aspose.cells.Workbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public final class ReadFile {

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
                    addSkipOptionToAnswers(cells, row),
                    cells.get(row, 3).getIntValue()
            ));
        }

        return questions;
    }

    private static ArrayList<String> addSkipOptionToAnswers(Cells cells, int row) {
        String[] answers = cells.get(row, 2).getStringValue().split("\\|");
        ArrayList<String> answersAndSkipOption = new ArrayList<>(Arrays.asList(answers));
        answersAndSkipOption.add("Пропустить");

        return answersAndSkipOption;
    }

}
