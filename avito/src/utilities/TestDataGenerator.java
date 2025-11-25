package utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class TestDataGenerator {

    public static String generateUniqueTaskName() {
        String timestamp = new SimpleDateFormat("MMdd_HHmmss").format(new Date());
        int randomNum = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "ТестЗадача_" + timestamp + "_" + randomNum;
    }

    public static String generateTaskDescription() {
        return "Автоматически созданная задача для тестирования Edge " +
                new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date());
    }

    public static String generateAssignee() {
        String[] names = {"Иван Тестов", "Мария Проверкина", "Алексей Тестеров", "Елена QA"};
        return names[ThreadLocalRandom.current().nextInt(names.length)];
    }

    public static String generateEmail() {
        return "test" + System.currentTimeMillis() + "@example.com";
    }
}