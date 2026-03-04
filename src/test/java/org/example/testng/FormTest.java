package org.example.testng;

import org.example.pom.FormPom;
import org.example.utils.Driver;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;

public class FormTest {

    public static WebDriver driver;

    public static final String URL = "https://demoqa.com/";
    public static final String FIRST_NAME = "kiril";
    public static final String LAST_NAME = "bozbeq";
    public static final String EMAIL = "kbozb@gmail.com";
    public static final String GENDER = "Male";
    public static final String NUMBER = "0123456789";
    public static final String DATE = "21 Jul 1995";
    public static final String SUBJECT = "Football";
    public static final String STATE = "Rajasthan";
    public static final String CITY = "Jaipur";
    public static final String HOBBY = "Sports"; // DemoQA: Sports / Reading / Music

    private static boolean isCI() {
        return "true".equalsIgnoreCase(System.getenv("CI")) ||
                "true".equalsIgnoreCase(System.getenv("GITHUB_ACTIONS"));
    }

    @BeforeMethod
    public void beforeMethod() throws MalformedURLException {
        // В CI хотим Remote (Selenoid) -> будет видео
        // Локально можно Local
        if (isCI()) {
            driver = Driver.getRemoteOrLocalDriver(); // если remote упадет, возьмет local
        } else {
            driver = Driver.getAutoLocalDriver();
        }

        driver.manage().window().maximize();
    }

    @Test
    public void formTest() {
        driver.get(URL);

        FormPom formPom = new FormPom(driver);
        formPom.clickForms();
        formPom.clickPracticeForm();
        formPom.closeAdvert();

        formPom.setFirstName(FIRST_NAME);
        formPom.setLastName(LAST_NAME);
        formPom.setEmail(EMAIL);
        formPom.setGender(GENDER);
        formPom.setNumber(NUMBER);
        formPom.setDate(DATE);
        formPom.setHobby(HOBBY);

        formPom.setSubject(SUBJECT);
        formPom.setState(STATE);
        formPom.setCity(CITY);
        formPom.clickSubmit();

        String actualName = formPom.getTableDataByLabel("Student Name");
        Assert.assertEquals(actualName, FIRST_NAME + " " + LAST_NAME);
    }

    @AfterMethod
    public void afterMethod() {
        if (driver != null) {
            driver.quit();
        }
    }
}