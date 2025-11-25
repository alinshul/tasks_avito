package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class TaskModal extends BasePage {

    public TaskModal(WebDriver driver) {
        super(driver);
    }

    @FindBy(css = "[role='dialog'], .modal, [aria-modal='true']")
    private WebElement modal;

    @FindBy(css = "input[name='title'], input[placeholder*='название'], #title")
    private WebElement titleInput;

    @FindBy(css = "textarea[name='description'], textarea[placeholder*='описание'], #description")
    private WebElement descriptionInput;

    @FindBy(css = "select[name='status'], select[id='status']")
    private WebElement statusSelect;

    @FindBy(css = "input[name='assignee'], input[placeholder*='исполнитель'], #assignee")
    private WebElement assigneeInput;

    @FindBy(xpath = "//button[contains(text(),'Сохранить')]")
    private WebElement saveButton;

    @FindBy(xpath = "//button[contains(text(),'Удалить')]")
    private WebElement deleteButton;

    @FindBy(css = "button[aria-label*='close'], button[class*='close'], .close-button")
    private WebElement closeButton;

    public boolean isModalDisplayed() {
        return isElementVisible(modal);
    }

    public String getTaskTitle() {
        return titleInput.getAttribute("value");
    }

    public String getTaskDescription() {
        return descriptionInput.getAttribute("value");
    }

    public String getTaskStatus() {
        Select select = new Select(statusSelect);
        return select.getFirstSelectedOption().getText();
    }

    public String getTaskAssignee() {
        return assigneeInput.getAttribute("value");
    }

    public void updateTaskTitle(String newTitle) {
        enterText(titleInput, newTitle);
    }

    public void updateTaskDescription(String newDescription) {
        enterText(descriptionInput, newDescription);
    }

    public void updateTaskStatus(String newStatus) {
        Select select = new Select(statusSelect);
        select.selectByVisibleText(newStatus);
    }

    public void updateTaskAssignee(String newAssignee) {
        enterText(assigneeInput, newAssignee);
    }

    public void clickSaveButton() {
        clickElement(saveButton);
        waitForElementToDisappear(modal);
    }

    public void clickDeleteButton() {
        clickElement(deleteButton);
        // Подтверждение удаления
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void closeModal() {
        try {
            clickElement(closeButton);
        } catch (Exception e) {
            modal.click();
        }
        waitForElementToDisappear(modal);
    }

    public void fillTaskDetails(String title, String description, String status, String assignee) {
        if (title != null) updateTaskTitle(title);
        if (description != null) updateTaskDescription(description);
        if (status != null) updateTaskStatus(status);
        if (assignee != null) updateTaskAssignee(assignee);
    }

    public void waitForModalToBeVisible() {
        wait.until(ExpectedConditions.visibilityOf(modal));
    }
}