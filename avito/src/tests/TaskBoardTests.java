package tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.BoardPage;
import pages.TaskModal;
import utilities.TestDataGenerator;
import static org.testng.Assert.*;

public class TaskBoardTests {
    private WebDriver driver;
    private BoardPage boardPage;
    private TaskModal taskModal;
    private String testTaskName;

    @BeforeMethod
    public void setUp() {
        // Настройка Edge Driver
        System.setProperty("webdriver.edge.driver", "C:\\webdrivers\\msedgedriver.exe");

        // Настройка опций Edge
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-features=VizDisplayCompositor");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

        driver = new EdgeDriver(options);

        // Навигация к сайту
        driver.get("https://avito-tech-internship-psi.vercel.app/");

        // Инициализация page objects
        boardPage = new BoardPage(driver);
        taskModal = new TaskModal(driver);

        testTaskName = TestDataGenerator.generateUniqueTaskName();

        System.out.println("Edge Browser Test Setup");
        System.out.println("Browser: Microsoft Edge");
        System.out.println("Test Task: " + testTaskName);
        System.out.println("URL: " + driver.getCurrentUrl());
    }

    @Test(priority = 1)
    public void testSuccessfulTaskCreation() {
        System.out.println("Тест 1: Создание задачи в Edge");

        // Получаем начальное количество задач
        int initialTaskCount = boardPage.getTaskCountInColumn("Нужно сделать");
        System.out.println("Начальное количество задач в 'Нужно сделать': " + initialTaskCount);

        // Создаем задачу
        boardPage.createTask(testTaskName, "Нужно сделать");

        // Проверяем, что задача создана
        boolean isTaskCreated = boardPage.isTaskInColumn(testTaskName, "Нужно сделать");
        assertTrue(isTaskCreated, "Задача не была создана в колонке 'Нужно сделать'");

        // Проверяем увеличение количества задач
        int finalTaskCount = boardPage.getTaskCountInColumn("Нужно сделать");
        assertEquals(finalTaskCount, initialTaskCount + 1, "Количество задач не увеличилось");

        System.out.println(" Задача успешно создана: " + testTaskName);
        System.out.println(" Количество задач увеличилось с " + initialTaskCount + " до " + finalTaskCount);
    }

    @Test(priority = 2, dependsOnMethods = "testSuccessfulTaskCreation")
    public void testTaskDetailsOpeningAndEditing() {
        System.out.println("Тест 2: Открытие и редактирование задачи в Edge");

        boardPage.openTaskByName(testTaskName);
        taskModal.waitForModalToBeVisible();
        assertTrue(taskModal.isModalDisplayed(), "Модальное окно задачи не открылось");

        String newDescription = TestDataGenerator.generateTaskDescription();
        String newAssignee = TestDataGenerator.generateAssignee();

        taskModal.updateTaskDescription(newDescription);
        taskModal.updateTaskAssignee(newAssignee);

        taskModal.clickSaveButton();
        boardPage.waitForModalToClose();

        boardPage.openTaskByName(testTaskName);
        taskModal.waitForModalToBeVisible();

        String actualDescription = taskModal.getTaskDescription();
        String actualAssignee = taskModal.getTaskAssignee();

        assertEquals(actualDescription, newDescription, "Описание не сохранилось");
        assertEquals(actualAssignee, newAssignee, "Исполнитель не сохранился");

        System.out.println(" Данные задачи успешно обновлены в Edge");
        System.out.println("  Описание: " + actualDescription);
        System.out.println("  Исполнитель: " + actualAssignee);

        taskModal.closeModal();
    }

    @Test(priority = 3, dependsOnMethods = "testSuccessfulTaskCreation")
    public void testTaskVisualSearch() {
        System.out.println("Тест 3: Визуальный поиск задачи в Edge");

        boolean isTaskFound = boardPage.isTaskPresent(testTaskName);
        assertTrue(isTaskFound, "Задача не найдена на доске");

        boolean isInTodoColumn = boardPage.isTaskInColumn(testTaskName, "Нужно сделать");
        assertTrue(isInTodoColumn, "Задача не в колонке 'Нужно сделать'");

        System.out.println(" Задача найдена на доске: " + testTaskName);
        System.out.println(" Задача находится в колонке 'Нужно сделать'");

        System.out.println("Все задачи на доске: " + boardPage.getAllTaskTitles());
    }

    @Test(priority = 4, dependsOnMethods = "testSuccessfulTaskCreation")
    public void testTaskStatusChange() {
        System.out.println("Тест 4: Изменение статуса задачи в Edge");

        boardPage.openTaskByName(testTaskName);
        taskModal.updateTaskStatus("В работе");
        taskModal.clickSaveButton();
        boardPage.waitForModalToClose();

        boolean isInProgress = boardPage.isTaskInColumn(testTaskName, "В работе");
        assertTrue(isInProgress, "Задача не переместилась в 'В работе'");
        System.out.println("Задача перемещена в 'В работе'");

        boardPage.openTaskByName(testTaskName);
        taskModal.updateTaskStatus("Готово");
        taskModal.clickSaveButton();
        boardPage.waitForModalToClose();

        boolean isDone = boardPage.isTaskInColumn(testTaskName, "Готово");
        assertTrue(isDone, "Задача не переместилась в 'Готово'");
        System.out.println(" Задача перемещена в 'Готово'");

        boardPage.openTaskByName(testTaskName);
        taskModal.updateTaskStatus("Нужно сделать");
        taskModal.clickSaveButton();
        System.out.println(" Задача возвращена в 'Нужно сделать'");
    }

    @Test(priority = 5)
    public void testMultipleTaskCreation() {
        System.out.println("Тест 5: Создание нескольких задач в Edge");

        String[] testTasks = {
                TestDataGenerator.generateUniqueTaskName(),
                TestDataGenerator.generateUniqueTaskName(),
                TestDataGenerator.generateUniqueTaskName()
        };

        for (int i = 0; i < testTasks.length; i++) {
            boardPage.createTask(testTasks[i], "Нужно сделать");
            boolean isCreated = boardPage.isTaskInColumn(testTasks[i], "Нужно сделать");
            assertTrue(isCreated, "Задача " + testTasks[i] + " не была создана");
            System.out.println(" Создана задача " + (i + 1) + ": " + testTasks[i]);
        }

        System.out.println(" Все " + testTasks.length + " задач успешно созданы в Edge");

        // Очистка
        for (String task : testTasks) {
            try {
                boardPage.openTaskByName(task);
                taskModal.clickDeleteButton();
            } catch (Exception e) {
                System.out.println("Не удалось удалить задачу: " + task);
            }
        }
    }

    @Test(priority = 6)
    public void testBrowserSpecificFeatures() {
        System.out.println("Тест 6: Проверка специфичных возможностей Edge");

        // Проверяем заголовок страницы
        String pageTitle = driver.getTitle();
        assertNotNull(pageTitle, "Заголовок страницы не должен быть null");
        System.out.println("Заголовок страницы: " + pageTitle);

        // Проверяем URL
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("vercel.app"), "URL должен содержать vercel.app");
        System.out.println("Текущий URL: " + currentUrl);

        // Проверяем, что страница загружена
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.length() > 0, "Страница должна иметь содержимое");
        System.out.println("Размер страницы: " + pageSource.length() + " символов");

        System.out.println(" Все проверки Edge пройдены успешно");
    }

    @AfterMethod
    public void tearDown() {
        // Очистка
        try {
            if (boardPage.isTaskPresent(testTaskName)) {
                System.out.println("Очистка тестовой задачи: " + testTaskName);
                boardPage.openTaskByName(testTaskName);
                taskModal.clickDeleteButton();
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.out.println("Очистка не требуется или не удалась: " + e.getMessage());
        }

        // Закрытие браузера
        if (driver != null) {
            driver.quit();
            System.out.println("Edge Browser Closed");
        }
    }
}