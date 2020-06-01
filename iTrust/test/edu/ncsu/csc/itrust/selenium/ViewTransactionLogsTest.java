package edu.ncsu.csc.itrust.selenium;

import com.meterware.httpunit.HttpUnitOptions;
import edu.ncsu.csc.itrust.enums.TransactionType;
import java.text.SimpleDateFormat;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Use Case 39
 */
public class ViewTransactionLogsTest extends iTrustSeleniumTest {

    private WebDriver driver;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        HttpUnitOptions.setScriptingEnabled(false);
        gen.clearAllTables();
        gen.standardData();
    }

    /**
     * Precondition:
     * Admin is in the database
     *
     * Description:
     * 1. Admin clicks on the "View Transaction Logs" link.
     * Expected Results:
     * Screen changes to display an interactive table where admin can enter in criteria for
     * logged-in role, secondary user role, date range, transaction type name, or to
     * optionally search over all values.
     *
     * 2. Admin specifies a logged-in role only and selects all values for all other fields.
     * Expected Results:
     * Tables appear showing transactions only matching the specified logged-in user role.
     *
     * 3. Admin searches all logged-in roles and specifies a secondary user role only. Selects
     * all values for all other fields.
     * Expected Results:
     * Tables appear showing transactions only matching the specified secondary user role.
     *
     * 4. Admin specifies both a logged-in role and a secondary user role. Selects all values
     * for all other fields.
     * Expected Results:
     * Tables appear showing transactions only matching the specified logged-in user role
     * and the specified secondary user role.
     */
    public void testViewTransactionLogsRoles() throws Exception {

        // Login
        driver = login("9000000001", "pw");
        // Expected result
        assertEquals("iTrust - Admin Home", driver.getTitle());

        // Objective 1: navigate to "View Transaction Logs" page
        WebElement pageLink = driver.findElement(By.xpath("//*[@id='other-menu']/ul/li[8]/a"));
        pageLink.click();
        // Expected result
        assertEquals("iTrust - View Transaction Logs", driver.getTitle());

        // Objective 2: enter a specified logged-in role
        Select dropLoggedInRole = new Select(driver.findElement(By.name("loggedInRole")));
        dropLoggedInRole.selectByVisibleText("patient");
        // Search for all dates is checked by default
        driver.findElement(By.id("view")).click();
        // Expected result
        List<WebElement> rows = driver.findElements(By.xpath("//*[@id='TransactionsViewTable']/tbody/tr"));
        for (int i = 2; i < rows.size(); i++) {
            // check first column in each row
            WebElement row = rows.get(i);
            assertEquals("PATIENT", row.findElement(By.xpath("//*[@id='TransactionsViewTable']/tbody/tr["+Integer.toString(i)+"]/td[1]")).getText());
        }

        // Objective 3: enter a specified secondary user role
        dropLoggedInRole = new Select(driver.findElement(By.name("loggedInRole")));
        dropLoggedInRole.selectByVisibleText("-- All Roles --");
        Select dropSecondaryRole = new Select(driver.findElement(By.name("secondaryRole")));
        dropSecondaryRole.selectByVisibleText("patient");
        // check all dates should still remain checked from the previous objective
        driver.findElement(By.id("view")).click();
        // Expected result
        rows = driver.findElements(By.xpath("//*[@id='TransactionsViewTable']/tbody/tr"));
        for (int i = 2; i < rows.size(); i++) {
            // check second column in each row
            WebElement row = rows.get(i);
            assertEquals("PATIENT", row.findElement(By.xpath("//*[@id='TransactionsViewTable']/tbody/tr["+Integer.toString(i)+"]/td[2]")).getText());
        }

        // Objective 4: enter a specified logged-in role and secondary user role
        dropLoggedInRole = new Select(driver.findElement(By.name("loggedInRole")));
        dropLoggedInRole.selectByVisibleText("hcp");
        dropSecondaryRole = new Select(driver.findElement(By.name("secondaryRole")));
        dropSecondaryRole.selectByVisibleText("patient");
        // check all dates should still remain checked from the previous objective
        driver.findElement(By.id("view")).click();
        // Expected result
        rows = driver.findElements(By.xpath("//*[@id='TransactionsViewTable']/tbody/tr"));
        for (int i = 2; i < rows.size(); i++) {
            // check first and second columns in each row
            WebElement row = rows.get(i);
            assertEquals("HCP", row.findElement(By.xpath("//*[@id='TransactionsViewTable']/tbody/tr["+Integer.toString(i)+"]/td[1]")).getText());
            assertEquals("PATIENT", row.findElement(By.xpath("//*[@id='TransactionsViewTable']/tbody/tr["+Integer.toString(i)+"]/td[2]")).getText());
        }
    }

    /**
     * Precondition:
     * Admin is in the database
     *
     * Description:
     * 1. Admin clicks on the "View Transaction Logs" link.
     * Expected Results:
     * Screen changes to display an interactive table where admin can enter in criteria for
     * logged-in role, secondary user role, date range, transaction type name, or to
     * optionally search over all values.
     *
     * 2. Admin specifies a start and end date. Selects all values for all other fields.
     * Expected Results:
     * Tables appear that show only transactions with a time stamp that fall within the start and end date.
     *
     * 3. Admin selects the "search all dates" checkbox. Selects all values for all other fields.
     * Expected Results:
     * Tables appear that show all transactions within the database.
     */
    public void testViewTransactionLogsDates() throws Exception {
        // Login
        driver = login("9000000001", "pw");
        // Expected result
        assertEquals("iTrust - Admin Home", driver.getTitle());

        // Objective 1: navigate to "View Transaction Logs" page
        WebElement pageLink = driver.findElement(By.xpath("//*[@id='other-menu']/ul/li[8]/a"));
        pageLink.click();
        // Expected result
        assertEquals("iTrust - View Transaction Logs", driver.getTitle());

        // Objective 2: enter a specified date range
        String startDate = "01/01/2008";
        String endDate = "12/01/2008";
        driver.findElement(By.name("startDate")).sendKeys(startDate);
        driver.findElement(By.name("endDate")).sendKeys(endDate);
        // Uncheck "search all dates"
        WebElement checkAllDates = driver.findElement(By.name("searchAllDates"));
        checkAllDates.click();
        driver.findElement(By.id("view")).click();
        // Expected result
        List<WebElement> rows = driver.findElements(By.xpath("//*[@id='TransactionsViewTable']/tbody/tr"));
        for (int i = 2; i <= rows.size(); i++) {
            // check that the date in each row is after the specified start date and before the specified end date
            WebElement row = rows.get(i);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String date = row.findElement(By.xpath("//*[@id='TransactionsViewTable']/tbody/tr["+Integer.toString(i)+"]/td[1]")).toString();
            assertTrue(sdf.parse(startDate).before(sdf.parse(date)));
            assertTrue(sdf.parse(date).before(sdf.parse(endDate)));
        }

        // Objective 3: search over all dates
        checkAllDates = driver.findElement(By.name("searchAllDates"));
        checkAllDates.click(); // re-check "search all dates"
        driver.findElement(By.id("view")).click();
        // Expected result
        rows = driver.findElements(By.xpath("//*[@id='TransactionsViewTable']/tbody/tr"));
        assertEquals(53, rows.size());
    }

    /**
     * Precondition:
     * Admin is in the database
     *
     * Description:
     * 1. Admin clicks on the "View Transaction Logs" link.
     * Expected Results:
     * Screen changes to display an interactive table where admin can enter in criteria for
     * logged-in role, secondary user role, date range, transaction type name, or to
     * optionally search over all values.
     *
     * 2. Admin specifies a transaction type name. Selects all values for all other fields.
     * Expected Results:
     * Tables appear that show only transactions with the specified transaction type name.
     *
     */
    public void testViewTransactionLogsTransactionName() throws Exception {
        // Login
        driver = login("9000000001", "pw");
        // Expected result
        assertEquals("iTrust - Admin Home", driver.getTitle());

        // Objective 1: navigate to "View Transaction Logs" page
        WebElement pageLink = driver.findElement(By.xpath("//*[@id='other-menu']/ul/li[8]/a"));
        pageLink.click();
        // Expected result
        assertEquals("iTrust - View Transaction Logs", driver.getTitle());

        // Objective 2: specifiy Transaction Type Name
        Select dropTransactionNames = new Select(driver.findElement(By.name("transactionTypeCode")));
        dropTransactionNames.selectByVisibleText("1102: Edit Office Visits");
        WebElement checkAllDates = driver.findElement(By.name("searchAllDates"));
        checkAllDates.click();
        driver.findElement(By.id("view")).click();
        // Expected result
        List<WebElement> rows = driver.findElements(By.xpath("//*[@id='TransactionsViewTable']/tbody/tr"));
        for (int i = 2; i < rows.size(); i++) {
            // check first column in each row
            WebElement row = rows.get(i);
            assertEquals("Edit Office Visits", row.findElement(By.xpath("//*[@id='TransactionsViewTable']/tbody/tr["+Integer.toString(i)+"]/td[3]")).getText());
        }
    }

    /**
     * Precondition:
     * Admin is in the database
     *
     * Description:
     * 1. Admin clicks on the "View Transaction Logs" link.
     * Expected Results:
     * Screen changes to display an interactive table where admin can enter in criteria for
     * logged-in role, secondary user role, date range, transaction type name, or to
     * optionally search over all values.
     *
     * 2. Admin leaves all info to search over all ranges. Admin clicks summarize button
     * Expected Results:
     * Screen changes to display four info graphs.
     *
     */
    public void testViewTransactionLogsGraph() throws Exception {
        // Login
        driver = login("9000000001", "pw");
        // Expected result
        assertEquals("iTrust - Admin Home", driver.getTitle());

        // Objective 1: navigate to "View Transaction Logs" page
        WebElement pageLink = driver.findElement(By.xpath("//*[@id='other-menu']/ul/li[8]/a"));
        pageLink.click();
        // Expected result
        assertEquals("iTrust - View Transaction Logs", driver.getTitle());

        // Objective 2: click summarize button
        driver.findElement(By.id("summarize")).click();
        assertTrue(driver.getPageSource().contains("Logged-in User Roles v.s. Transaction Logs"));
        assertTrue(driver.getPageSource().contains("Secondary Roles v.s. Transaction Logs"));
        assertTrue(driver.getPageSource().contains("Date v.s. Transaction Logs"));
        assertTrue(driver.getPageSource().contains("Transaction Types v.s. Transaction Logs"));
    }

    /**
     * Precondition:
     * Tester is in the database
     *
     * Description:
     * 1. Tester clicks on the "View Transaction Logs" link.
     * Expected Results:
     * Screen changes to display an interactive table where admin can enter in criteria for
     * logged-in role, secondary user role, date range, transaction type name, or to
     * optionally search over all values.
     */
    public void testViewTransactionLogsTester() throws Exception {
        // Login
        driver = login("9999999999", "pw");
        // Expected result
        assertEquals("iTrust - Tester Home", driver.getTitle());

        // Objective 1: navigate to "View Transaction Logs" page
        WebElement pageLink = driver.findElement(By.xpath("//*[@id='view-menu']/ul/li[2]/a"));
        pageLink.click();
        // Expected result
        assertEquals("iTrust - View Transaction Logs", driver.getTitle());
    }
}