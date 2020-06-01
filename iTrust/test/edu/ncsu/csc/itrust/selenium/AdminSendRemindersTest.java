package edu.ncsu.csc.itrust.selenium;

import edu.ncsu.csc.itrust.enums.TransactionType;
import org.openqa.selenium.By;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;//use for webElement
import java.util.List;
import edu.ncsu.csc.itrust.beans.Email;
import edu.ncsu.csc.itrust.dao.mysql.ApptDAO;
import edu.ncsu.csc.itrust.dao.mysql.FakeEmailDAO;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;

public class AdminSendRemindersTest extends iTrustSeleniumTest {
	
    private FakeEmailDAO emailDAO;
    private HtmlUnitDriver driver;
    public static final String ADDRESS = "http://localhost:8080/iTrust/";
    @Override
    public void setUp() throws Exception {
        super.setUp();
        
	emailDAO = TestDAOFactory.getTestInstance().getFakeEmailDAO();
	gen.clearAllTables();
        gen.standardData();
    }

    public void testAdminSendReminders() throws Exception {
	List<Email> initial_emails = emailDAO.getAllEmails();

       HtmlUnitDriver wd = new HtmlUnitDriver(); 
	   wd.get(ADDRESS); 

        driver = (HtmlUnitDriver) login("9000000001", "pw");
        assertEquals("iTrust - Admin Home", driver.getTitle());
        assertLogged(TransactionType.HOME_VIEW, 9000000001L, 0L, "");

        driver.findElement(By.linkText("Send Reminders")).click();
        assertEquals("iTrust - Send Reminders", driver.getTitle());
    
        driver.findElement(By.name("days")).sendKeys("7"); 
 	driver.findElement(By.name("action")).click();
        assertTrue(driver.getPageSource().contains("Reminders sent successfully"));
     	List<Email> final_emails = emailDAO.getAllEmails();
        assertEquals(3, final_emails.size() - initial_emails.size());	


	// then check the outmail box
        driver.findElement(By.linkText("Reminder Message Outbox")).click();
        assertEquals("iTrust - View My Sent Messages", driver.getTitle());

    }
/*
	public void testAdminSendReminders_replace() throws Exception {
        List<Email> initial_emails = emailDAO.getAllEmails();

       HtmlUnitDriver wd = new HtmlUnitDriver();
        wd.get(ADDRESS);

        driver = (HtmlUnitDriver) login("9000000001", "pw");
        assertEquals("iTrust - Admin Home", driver.getTitle());
        assertLogged(TransactionType.HOME_VIEW, 9000000001L, 0L, "");

        driver.findElement(By.linkText("Send Reminders")).click();
        assertEquals("iTrust - Send Reminders", driver.getTitle());

        driver.findElement(By.name("days")).sendKeys("7");
        driver.findElement(By.name("action")).click();
        assertTrue(driver.getPageSource().contains("Reminders sent successfully"));
        List<Email> final_emails = emailDAO.getAllEmails();
        assertEquals(7, final_emails.size() - initial_emails.size());	

    }	*/
 
 } 
