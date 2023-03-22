package org.helensbot.englishtest;

import org.helensbot.utils.ReadFile;

import java.util.LinkedList;

public final class EnglishTest {
    public static final LinkedList<Question> questions = new LinkedList<>();
    private static boolean initialized = false;

    static {
        try {
            initialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static synchronized void initialize() throws Exception {
        if (!initialized) {
            questions.addAll(ReadFile.readFile());
            initialized = true;
        }
    }
}
