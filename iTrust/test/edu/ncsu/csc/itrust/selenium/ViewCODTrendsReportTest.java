package edu.ncsu.csc.itrust.selenium;

import java.util.List;

import org.openqa.selenium.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.meterware.httpunit.HttpUnitOptions;

import edu.ncsu.csc.itrust.enums.TransactionType;


/**
 * Use Case 20
 */
public class ViewCODTrendsReportTest extends iTrustSeleniumTest {

	private HtmlUnitDriver driver;
	@Override
	protected void setUp() throws Exception{
		super.setUp();
		HttpUnitOptions.setScriptingEnabled(false);
		gen.clearAllTables();
		gen.standardData();
	}

	/*
	 * Precondition:
	 * LHCP 9000000000 is in the database
	 * LHCP 9000000000 has authenticated successfully.
	 *
	 * Description:
	 * 1. LHCP clicks on "" link.
	 * Expected Results:
	 * Screen changes to display two interactive blocks which LHCP can enter
	 * lower and upper bounds on the date range, and may or may not specify
	 * a gender, to search for patient deaths.
	 *
	 * 2. LHCP enters lower date and upper date of range.
	 * Expected Results:
	 * Blocks appear showing the top 2 most common causes of death for patients 
	 * within the specified range for each gender (and none) for both the LHCP 
	 * logged in and for all causes of patient death overall.
	 */
	public void testViewCODTrendsReportManualDate() throws Exception {
		driver = (HtmlUnitDriver)login("9000000000", "pw");

		// Objective 1: navigate to "View Cause of Death Statistics" page
		driver.findElement(By.linkText("Cause of Death Trends")).click();
		assertEquals("iTrust - View Cause of Death Statistics", driver.getTitle());

		// Objective 2: enter dates and gender into
		driver.findElement(By.name("startDate")).sendKeys("01/01/2000"); 
		driver.findElement(By.name("endDate")).sendKeys("01/01/2010"); 
		driver.findElement(By.id("select_cod")).click();

		assertTrue(driver.getPageSource().contains("2 Most Common Causes of Death"));
	}

	public void testViewCODTrendsReportInvalidDateFormat() throws Exception {
		driver = (HtmlUnitDriver)login("9000000000", "pw");

		// Objective 1: navigate to "View Cause of Death Statistics" page
		driver.findElement(By.linkText("Cause of Death Trends")).click();
		assertEquals("iTrust - View Cause of Death Statistics", driver.getTitle());

		// Objective 2: enter dates and gender into
		driver.findElement(By.name("startDate")).sendKeys("01-01-2000"); 
		driver.findElement(By.name("endDate")).sendKeys("01-01-2010"); 
		driver.findElement(By.id("select_cod")).click();

		assertTrue(driver.getPageSource().contains("Dates provided were incorrectly formatted or invalid"));
	}

	public void testViewCODTrendsReportInvalidDateRange() throws Exception {
		driver = (HtmlUnitDriver)login("9000000000", "pw");

		// Objective 1: navigate to "View Cause of Death Statistics" page
		driver.findElement(By.linkText("Cause of Death Trends")).click();
		assertEquals("iTrust - View Cause of Death Statistics", driver.getTitle());

		// Objective 2: enter dates and gender into
		driver.findElement(By.name("startDate")).sendKeys("01/01/2010"); 
		driver.findElement(By.name("endDate")).sendKeys("01/01/2000"); 
		driver.findElement(By.id("select_cod")).click();

		assertTrue(driver.getPageSource().contains("Dates provided were incorrectly formatted or invalid"));
	}

	/*
	public void testViewCODTrendsReportCalendarDate() throws Exception {
		driver = (HtmlUnitDriver)login("9000000000", "pw");

		// Objective 1: navigate to "View Cause of Death Statistics" page
		driver.findElement(By.linkText("Cause of Death Trends")).click();
		assertEquals("iTrust - View Cause of Death Statistics", driver.getTitle());
	
		// Objective 2: enter dates and gender into
		//String startLocator = String.format("//input[contains(@onclick,'displayDatePicker('%1$s');')]", "startDate");
		//driver.findElement(By.xpath(startLocator)).click();
		driver.findElement(By.id("startDatePicker")).click();
		WebElement startCalendar = driver.findElement(By.id("datepicker")).click();
		startCalendar.sendKeys("01/01/2000");
		List<WebElement> startDates = driver.findElements(By.xpath("//table[@class='dpTable']//td"));
		for (WebElement elem : startDates) {
			String date = elem.getText();
			if (date.equalsIgnoreCase("14")) {
				elem.click();
				break;
			}
		}
		assertTrue(driver.getPageSource().contains("01/01/2000"));
		//driver.findElement(By.id("startDatePicker")).click();

		//driver.findElement(By.id("datepicker")).click();
		//WebElement startDatePicker = driver.findElement(By.xpath("//div[contains(@id, 'datepicker')]"));
		//driver.findElement(By.linkText("14")).click();
		//driver.findElement(By.id("startDatePicker")).click();
		//driver.findElement(By.xpath("//input[contains(@onclick, 'startDate')]")).click();

		//driver.findElement(By.xpath("//input[contains(@onclick, 'endDate')]")).click();
		driver.findElement(By.id("endDatePicker")).click();
		List<WebElement> endDates = driver.findElements(By.xpath("//table[@class='dpTable']//td"));
		for (WebElement elem : endDates) {
			String date = elem.getText();
			if (date.equalsIgnoreCase("28")) {
				elem.click();
				break;
			}
		}
		assertTrue(driver.getPageSource().contains("11/28/2019"));

		//WebElement endDatePicker = driver.findElement(By.id("datepicker"));
		//driver.findElement(By.linkText("21")).click();
		//driver.findElement(By.id("endDatePicker")).click();
		//driver.findElement(By.xpath("//input[contains(@onclick, 'endDate')]")).click();

		assertTrue(driver.getPageSource().contains("2 Most Common Causes of Death"));
	}
	*/

}
