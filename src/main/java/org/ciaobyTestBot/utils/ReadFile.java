package org.ciaobyTestBot.utils;

import com.aspose.cells.Workbook;
import org.ciaobyTestBot.englishtest.Question;

import java.util.LinkedList;

public class ReadFile {

    public static LinkedList<Question> readFile() throws Exception {
        var book = new Workbook( "src/main/resources/questions30.ods");
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
