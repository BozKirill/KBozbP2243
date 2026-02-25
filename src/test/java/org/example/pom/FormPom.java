package org.example.pom;

import org.example.utils.Utils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class FormPom {

    static public WebDriver driver;
    static public JavascriptExecutor js;

    @FindBy(xpath = "//*[text()='Forms']")
    WebElement forms;

    @FindBy(xpath = "//*[text()='Practice Form']")
    WebElement practiceForm;

    @FindBy(xpath = "//*[@id='firstName']")
    WebElement firstName;

    @FindBy(xpath = "//*[@id='lastName']")
    WebElement lastName;

    @FindBy(xpath = "//*[@id='userEmail']")
    WebElement userEmail;

    @FindBy(xpath = "//*[@id='userNumber']")
    WebElement userNumber;

    @FindBy(xpath = "//*[@id='dateOfBirthInput']")
    WebElement dateOfBirthInput;

    @FindBy(xpath = "//*[@id='subjectsInput']")
    WebElement subjectsInput;

    @FindBy(xpath = "//*[@id='state']")
    WebElement state;

    @FindBy(xpath = "//*[@id='city']")
    WebElement city;

    @FindBy(xpath = "//*[@id='submit']")
    WebElement submit;

    public FormPom(WebDriver driverParam) {
        driver = driverParam;
        js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    public String getTableDataByLabel(String labelParam) {
        WebElement data = driver.findElement(By.xpath("//table//*[text()='" + labelParam  + "']/../*[2]"));
        return data.getText();
    }

    public void clickSubmit() {
        submit.click();
    }

    public void setCity(String cityParam) {
        city.click();
        WebElement ddSCity = city.findElement(By.xpath("//*[text()='" + cityParam  + "']"));
        ddSCity.click();
    }

    public void setState(String stateParam) {
        state.click();
        WebElement ddState = state.findElement(By.xpath("//*[text()='" + stateParam  + "']"));
//        Utils.explicitWait(driver, ExpectedConditions.elementToBeClickable(ddState), 10);
        pause(1000);
        ddState.click();
    }

    public void setHobby(String hobbyParam) {
        WebElement hobby = driver.findElement(By.xpath("//*[@id='hobbiesWrapper']//label[text()='" + hobbyParam + "']/../input"));
        hobby.sendKeys(" ");
    }

    public void setSubject(String subjectParam) {
        subjectsInput.sendKeys(subjectParam);
        subjectsInput.sendKeys(Keys.ENTER);
    }

    public void setDate(String dateParam) {
        dateOfBirthInput.sendKeys(Keys.CONTROL, "a");
        dateOfBirthInput.sendKeys(dateParam);
        dateOfBirthInput.sendKeys(Keys.ENTER);
    }

    public void setNumber(String numberParam) {
        userNumber.clear();
        userNumber.sendKeys(numberParam);
    }

    public void setGender(String genderParam) {
        WebElement gender = driver.findElement(By.xpath("//*[@id='genterWrapper']//label[text()='" + genderParam + "']"));
        gender.click();
    }

    public void setEmail(String emailParam) {
        userEmail.clear();
        userEmail.sendKeys(emailParam);
    }

    public void setLastName(String lastNameParam) {
        lastName.clear();
        lastName.sendKeys(lastNameParam);
    }

    public void setFirstName(String firstNameParam) {
        firstName.clear();
        firstName.sendKeys(firstNameParam);
    }

    public void clickPracticeForm() {
        Utils.explicitWait(driver, ExpectedConditions.visibilityOf(practiceForm), 10);
        practiceForm.click();
    }

    public void clickForms() {
        forms.click();
    }

    public void pause(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void scrollToElement(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public void closeAdvert() {
        try {
            js.executeScript("var elem = document.evaluate(\"//*[@id='adplus-anchor']\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;" +
                    "elem.parentNode.removeChild(elem);");
        } catch (Exception ignored) {}
        try {
            js.executeScript("var elem = document.evaluate(\"//footer\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;" +
                    "elem.parentNode.removeChild(elem);");
        } catch (Exception ignored) {}
    }
}
