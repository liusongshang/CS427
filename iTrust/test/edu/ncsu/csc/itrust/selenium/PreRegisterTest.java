package edu.ncsu.csc.itrust.selenium;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.openqa.selenium.*;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.junit.Test;
import org.openqa.selenium.support.ui.Select;

public class PreRegisterTest extends iTrustSeleniumTest {
    private String baseUrl;
    private StringBuffer verificationErrors = new StringBuffer();

    public void setUp() throws Exception {
        super.setUp();
        gen.clearAllTables();
        gen.standardData();
    }

    @Test
    public void testPatientPreRegister() throws Exception {
        //set up for the start location and driver
        WebDriver driver = new HtmlUnitDriver();
        baseUrl = "http://localhost:8080/iTrust/";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.get(baseUrl + "auth/forwardUser.jsp");

        //enter pre-register patient mandatory information
        driver.findElement(By.id("pre_firstname")).sendKeys("hello");
        driver.findElement(By.id("pre_lastname")).sendKeys("world");
        driver.findElement(By.id("pre_email")).sendKeys("hello@world.com");
        driver.findElement(By.id("pre_password")).sendKeys("helloworld");
        driver.findElement(By.id("pre_verify")).sendKeys("helloworld");

        //enter pre-register patient optional information
        driver.findElement(By.id("pre_city")).sendKeys("Champaign");
        WebElement stateDropDown = driver.findElement(By.id("pre_state"));
        Select stateSelect = new Select(stateDropDown);
        stateSelect.selectByValue("IL");
        driver.findElement(By.id("pre_zip")).sendKeys(String.valueOf(61820));
        driver.findElement(By.id("pre_smoker")).click();

        //make sure that the pre-register process succeeded
        driver.findElement(By.cssSelector("input[value=\"Register\"]")).click();
        try {
            assertEquals("http://localhost:8080/iTrust/preRegister.jsp", driver.getCurrentUrl());
        } catch (Error e) {
            verificationErrors.append(e.toString());
            fail();
        }
        assertNotNull(driver.findElement(By.id("pre_new_mid")));
        String preNewMid = driver.findElement(By.id("pre_new_mid")).getText();

        //try to log in as a pre-register patient
        driver.findElement(By.id("j_username")).clear();
        driver.findElement(By.id("j_username")).sendKeys(preNewMid);
        driver.findElement(By.id("j_password")).clear();
        driver.findElement(By.id("j_password")).sendKeys("helloworld");
        driver.findElement(By.cssSelector("input[value=\"Login\"]")).click();
        try {
            assertEquals("iTrust - Pre-registered Patient Home", driver.getTitle());
        } catch (Error e) {
            verificationErrors.append(e.toString());
            fail();
        }
        assertEquals(driver.findElement(By.xpath("//*[@id=\"iTrustContent\"]/div/h2")).getText(), "Welcome hello world!");


        //try to modify password
        driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/ul/li[3]/a")).click();
        try {
            assertEquals("iTrust - Change Password", driver.getTitle());
        } catch (Error e) {
            verificationErrors.append(e.toString());
            fail();
        }

        driver.findElement(By.xpath("//*[@id=\"mainForm\"]/table/tbody/tr[1]/td[2]/input")).sendKeys("helloworld");
        driver.findElement(By.xpath("//*[@id=\"mainForm\"]/table/tbody/tr[2]/td[2]/input")).sendKeys("helloworld123");
        driver.findElement(By.xpath("//*[@id=\"mainForm\"]/table/tbody/tr[3]/td[2]/input")).sendKeys("helloworld123");
        driver.findElement(By.xpath("//*[@id=\"mainForm\"]/table/tbody/tr[4]/td/input")).click();

        //log in with new password
        driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/ul/li[2]/a")).click();
        driver.findElement(By.id("j_username")).clear();
        driver.findElement(By.id("j_username")).sendKeys(preNewMid);
        driver.findElement(By.id("j_password")).clear();
        driver.findElement(By.id("j_password")).sendKeys("helloworld123");
        driver.findElement(By.cssSelector("input[value=\"Login\"]")).click();
        try {
            assertEquals("iTrust - Pre-registered Patient Home", driver.getTitle());
        } catch (Error e) {
            verificationErrors.append(e.toString());
            fail();
        }
        assertEquals(driver.findElement(By.xpath("//*[@id=\"iTrustContent\"]/div/h2")).getText(), "Welcome hello world!");

    }

}
