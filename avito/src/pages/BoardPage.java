package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.stream.Collectors;

public class BoardPage extends BasePage {

    public BoardPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "//button[contains(text(),'Добавить карточку')]")
    private List<WebElement> addCardButtons;

    @FindBy(xpath = "//textarea[@placeholder='Введите название задачи']")
    private WebElement taskNameInput;

    @FindBy(xpath = "//button[text()='Добавить']")
    private WebElement addTaskButton;

    @FindBy(xpath = "//button[text()='Отмена']")
    private WebElement cancelButton;


    @FindBy(css = "[data-testid*='task-card']")
    private List<WebElement> taskCards;

    @FindBy(css = "[data-testid*='task-card'] h3, [data-testid*='task-card'] div")
    private List<WebElement> taskTitles;

    @FindBy(xpath = "//h2[text()='Нужно сделать']/ancestor::div[contains(@class, 'column')]")
    private WebElement todoColumn;

    @FindBy(xpath = "//h2[text()='В работе']/ancestor::div[contains(@class, 'column')]")
    private WebElement inProgressColumn;

    @FindBy(xpath = "//h2[text()='Готово']/ancestor::div[contains(@class, 'column')]")
    private WebElement doneColumn;

    @FindBy(css = "[role='dialog'], .modal, [aria-modal='true']")
    private WebElement modal;

    public void clickAddCardInColumn(String columnName) {
        WebElement column = getColumnByName(columnName);
        WebElement addButton = column.findElement(org.openqa.selenium.By.xpath(".//button[contains(text(),'Добавить карточку')]"));
        clickElement(addButton);
        wait.until(ExpectedConditions.visibilityOf(taskNameInput));
    }

    public void createTask(String taskName, String columnName) {
        clickAddCardInColumn(columnName);
        enterText(taskNameInput, taskName);
        clickElement(addTaskButton);
        waitForTaskToAppear(taskName);
    }

    public void openTaskByName(String taskName) {
        try {
            taskCards.stream()
                    .filter(task -> task.getText().contains(taskName))
                    .findFirst()
                    .ifPresent(this::clickElement);

            wait.until(ExpectedConditions.visibilityOf(modal));
        } catch (Exception e) {
            System.out.println("Не удалось найти или открыть задачу: " + taskName);
            throw e;
        }
    }

    public boolean isTaskInColumn(String taskName, String columnName) {
        try {
            WebElement column = getColumnByName(columnName);
            List<WebElement> tasksInColumn = column.findElements(org.openqa.selenium.By.cssSelector("[data-testid*='task-card']"));
            return tasksInColumn.stream()
                    .anyMatch(task -> task.getText().contains(taskName));
        } catch (Exception e) {
            return false;
        }
    }

    public int getTaskCountInColumn(String columnName) {
        WebElement column = getColumnByName(columnName);
        List<WebElement> tasks = column.findElements(org.openqa.selenium.By.cssSelector("[data-testid*='task-card']"));
        return tasks.size();
    }

    public List<String> getAllTaskTitles() {
        return taskTitles.stream()
                .map(WebElement::getText)
                .filter(text -> !text.isEmpty())
                .collect(Collectors.toList());
    }

    public boolean isTaskPresent(String taskName) {
        return getAllTaskTitles().stream()
                .anyMatch(title -> title.contains(taskName));
    }

    private WebElement getColumnByName(String columnName) {
        switch (columnName.toLowerCase()) {
            case "нужно сделать":
            case "todo":
                return todoColumn;
            case "в работе":
            case "in progress":
                return inProgressColumn;
            case "готово":
            case "done":
                return doneColumn;
            default:
                throw new IllegalArgumentException("Unknown column: " + columnName);
        }
    }

    private void waitForTaskToAppear(String taskName) {
        wait.until(driver -> isTaskPresent(taskName));
    }

    public void waitForModalToClose() {
        wait.until(ExpectedConditions.invisibilityOf(modal));
    }
}