package edu.ncsu.csc.itrust.selenium;

import edu.ncsu.csc.itrust.enums.TransactionType;
import org.openqa.selenium.By;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;//use for webElement



public class DeactivateTest extends iTrustSeleniumTest {

    private HtmlUnitDriver driver;
    public static final String ADDRESS = "http://localhost:8080/iTrust/";
    @Override
    public void setUp() throws Exception {
        super.setUp();
        gen.clearAllTables();
        gen.standardData();
    }

    public void testDeactivatePreRegistrationPatient() throws Exception {
        HtmlUnitDriver wd = new HtmlUnitDriver();    // WebDriver driver = new FirefoxDriver();, but I am not use firefox now, so just like this
		wd.get(ADDRESS); 

        WebElement txtbox = wd.findElement(By.name("pre_firstname"));// get the box 
        txtbox.sendKeys("bo");
        txtbox = wd.findElement(By.name("pre_lastname"));// get the box 
        txtbox.sendKeys("zeng");
        txtbox = wd.findElement(By.name("pre_email"));// get the box 
        txtbox.sendKeys("qwe@zju.cn");
        txtbox = wd.findElement(By.name("pre_password"));// get the box 
        txtbox.sendKeys("zeng");
        txtbox = wd.findElement(By.name("pre_verify"));// get the box 
        txtbox.sendKeys("zeng");
        WebElement btn = wd.findElement(By.xpath("//input[@value='Register']"));//get the button  in 2
		btn.click();//click in 2
                
          
        driver = (HtmlUnitDriver) login("9000000000", "pw");
        assertEquals("iTrust - HCP Home", driver.getTitle());
        assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");

        driver.findElement(By.linkText("Pre-registered Patient")).click();
        assertEquals("iTrust - Pre-registered Patients", driver.getTitle());
        driver.findElement(By.xpath("//a[@href='viewprePHR.jsp?patient=0']")).click();

        driver.findElement(By.xpath("//a[@href='/iTrust/auth/hcp-uap/deactivatePatient.jsp']")).click();
        wd.get("http://localhost:8080/iTrust/auth/hcp-uap/viewprePHR.jsp?patient=0");
        driver.findElement(By.name("understand")).sendKeys("I UNDERSTAND"); 
 	driver.findElement(By.name("action")).click();
        assertTrue(driver.getPageSource().contains("Patient Successfully Deactivated"));
    }
    public void testActivatePreRegistrationPatient() throws Exception {
        HtmlUnitDriver wd = new HtmlUnitDriver();    // WebDriver driver = new FirefoxDriver();, but I am not use firefox now, so just like this
		wd.get(ADDRESS); 

        WebElement txtbox = wd.findElement(By.name("pre_firstname"));// get the box 
        txtbox.sendKeys("bo");
        txtbox = wd.findElement(By.name("pre_lastname"));// get the box 
        txtbox.sendKeys("zeng");  
        txtbox = wd.findElement(By.name("pre_email"));// get the box 
        txtbox.sendKeys("qwe@zju.cn");
        txtbox = wd.findElement(By.name("pre_password"));// get the box 
        txtbox.sendKeys("zeng");
        txtbox = wd.findElement(By.name("pre_verify"));// get the box 
        txtbox.sendKeys("zeng");
        WebElement btn = wd.findElement(By.xpath("//input[@value='Register']"));//get the button  in 2
		btn.click();//click in 2
                
          
        driver = (HtmlUnitDriver) login("9000000000", "pw");
        assertEquals("iTrust - HCP Home", driver.getTitle());
        assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");

        driver.findElement(By.linkText("Pre-registered Patient")).click();
        assertEquals("iTrust - Pre-registered Patients", driver.getTitle());
        driver.findElement(By.xpath("//a[@href='viewprePHR.jsp?patient=0']")).click();
        wd.get("http://localhost:8080/iTrust/auth/hcp-uap/viewprePHR.jsp?patient=0");
        //assertTrue(driver.getPageSource().contains("Activate Patient"));
	driver.findElement(By.name("understand")).sendKeys("I UNDERSTAND");
        driver.findElement(By.name("action")).click();
	assertTrue(driver.getPageSource().contains("Patient Successfully Activated"));
    }
}
