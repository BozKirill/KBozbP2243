package org.example.pom;

import org.example.utils.Utils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class FormPom {

    public static WebDriver driver;
    public static JavascriptExecutor js;

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
        WebElement data = driver.findElement(By.xpath("//table//*[normalize-space(text())='" + labelParam + "']/../*[2]"));
        return data.getText();
    }

    public void clickSubmit() {
        scrollToElement(submit);
        Utils.explicitWait(driver, ExpectedConditions.elementToBeClickable(submit), 10);
        safeClick(submit);
    }

    public void setCity(String cityParam) {
        scrollToElement(city);
        safeClick(city);

        By option = By.xpath("//div[@id='react-select-4-listbox']//*[normalize-space()='" + cityParam + "']");
        Utils.explicitWait(driver, ExpectedConditions.visibilityOfElementLocated(option), 10);

        WebElement ddCity = driver.findElement(option);
        safeClick(ddCity);
    }

    public void setState(String stateParam) {
        scrollToElement(state);
        safeClick(state);

        By option = By.xpath("//div[@id='react-select-3-listbox']//*[normalize-space()='" + stateParam + "']");
        Utils.explicitWait(driver, ExpectedConditions.visibilityOfElementLocated(option), 10);

        WebElement ddState = driver.findElement(option);
        safeClick(ddState);
    }

    /**
     * FIX:
     * - На DemoQA нет "Maths"
     * - Кликаем по LABEL (input спрятан)
     * - Используем normalize-space() чтобы не зависеть от пробелов
     */
    public void setHobby(String hobbyParam) {
        By hobbyLabel = By.xpath("//*[@id='hobbiesWrapper']//label[normalize-space()='" + hobbyParam + "']");
        Utils.explicitWait(driver, ExpectedConditions.visibilityOfElementLocated(hobbyLabel), 10);

        WebElement label = driver.findElement(hobbyLabel);
        scrollToElement(label);
        Utils.explicitWait(driver, ExpectedConditions.elementToBeClickable(label), 10);
        safeClick(label);
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
        WebElement gender = driver.findElement(By.xpath("//*[@id='genterWrapper']//label[normalize-space()='" + genderParam + "']"));
        scrollToElement(gender);
        safeClick(gender);
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
        safeClick(practiceForm);
    }

    public void clickForms() {
        safeClick(forms);
    }

    public void pause(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void scrollToElement(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }

    private void safeClick(WebElement element) {
        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            js.executeScript("arguments[0].click();", element);
        }
    }

    public void closeAdvert() {
        try {
            js.executeScript(
                    "var elem = document.evaluate(\"//*[@id='adplus-anchor']\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;" +
                            "if(elem){elem.parentNode.removeChild(elem);}"
            );
        } catch (Exception ignored) {}
        try {
            js.executeScript(
                    "var elem = document.evaluate(\"//footer\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;" +
                            "if(elem){elem.parentNode.removeChild(elem);}"
            );
        } catch (Exception ignored) {}
        try {
            js.executeScript("var banner=document.querySelector('#fixedban'); if(banner){banner.remove();}");
        } catch (Exception ignored) {}
    }
}