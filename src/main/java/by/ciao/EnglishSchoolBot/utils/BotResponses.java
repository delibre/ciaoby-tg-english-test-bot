package by.ciao.EnglishSchoolBot.utils;

import by.ciao.EnglishSchoolBot.user.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class BotResponses {
    public static String testFinished(User user) {
        return "Вы ответили верно на " + user.getTestState().getCorrectAnswers() + " вопросов.\n" +
                "Ваш уровень английского " + user.getTestState().getResult() + ".\n" +
                "Вы молодец, Вам осталось совсем немного, и скоро мы свяжемся с Вами для прохождения устного тестирования\uD83D\uDE0A";
    }

    public static String dataForAdmin(User user) {
        return  "Имя и Фамилия: " + user.getFullName() + "\n" +
                "Номер телефона: " + user.getPhone() + "\n" +
                "Ник в телеграмм: @" + user.getUsername() + "\n" +
                "Откуда узнали: " + user.getReferral() + "\n" +
                "Отвечено верно на: " + user.getTestState().getCorrectAnswers() + " вопросов.\n" +
                "Уровень английского: " + user.getTestState().getLvl();
    }

    public static String start() {
        return "Привет!\uD83D\uDC4B\n\n" +
                "Сейчас мы проверим Ваши знания английского\uD83D\uDD25\n" +
                "Вы пройдете тест, который состоит из 30 вопросов.\n" +
                "После этого, вы сможете проходить его, когда захотите.\uD83D\uDE0A\n\n" +
                "Но для начала давайте познакомимся\uD83D\uDE09\n\n" +
                "Введите, пожалуйста, Ваше имя и фамилию";
    }

    public static String infoSent() {
        return "Извините, не роспознал Вашу команду.\n" +
                "Если хотите пройти тест заново - нажмите кнопку \"Начать тестирование\"\uD83E\uDD17";
    }

    public static String phoneForamtWarning() {
        return "Неверный формат номера. Попробуйте, пожалуйста, ещё раз";
    }

    public static String replyKeyboardRemoved() {
        return "Спасибо! Можем продолжать\uD83D\uDE0A";
    }

    public static String askReferral() {
        return "Откуда вы о нас узнали?\n" +
                "Выберете один из вариантов ниже или же впишите свой";
    }

    public static String askForPhone() {
        return "Чтобы нам было удобнее с Вами связаться для согласования второго этапа (устного тестирования), " +
                "укажите, пожалуйста, Ваш номер телефона\uD83D\uDE0A\n\n" +
                "Вы можете нажать на кнопку \"Поделиться номером\" и указать номер, к которому привязан Ваш телеграм, " +
                "либо же указать другой, вписав его в формате +12345678900.\uD83D\uDE0A\n\n";
    }

    public static String startTest() {
        return "Ну что же, приступим к тесту. Сейчас Вам нужно будет ответить на 30 вопросов.\uD83E\uDDD0 " +
                "Ограничений по времени нет.\n\n" +
                "Нажмите кнопку \"Начать тестирование\", когда будете готовы.";
    }

    public static List<String> referralOptions() {
        return new ArrayList<>(Arrays.asList("Google", "Яндекс", "Instagram/Facebook/VK/Tik-Tok", "Vse-kursy", "Еnguide", "Рекомендация от друзей"));
    }

    public static List<String> optionsForAnswers(User user) {
        ArrayList<String> answers = new ArrayList<>(Arrays.asList(user.getTestState().getCurrentQuestion().getAnswers()));
        answers.add("Пропустить");
        return answers;
    }
}
