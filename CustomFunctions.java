package common;

import io.appium.java_client.FindsByAndroidUIAutomator;
import io.appium.java_client.MobileElement;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TimeZone;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.winium.DesktopOptions;
import org.openqa.selenium.winium.WiniumDriver;
import org.testng.Assert;

import com.relevantcodes.extentreports.LogStatus;

import common.CustomFunctions.imailOperations;
import common.DriverSetup.processes;
import controls.ExcelRead;

public class CustomFunctions extends WebFunctions {
	public static String message_format = System.getProperty("user.dir") + "\\src\\resources\\messages\\formats\\";
	public static HashMap<String, String> parameters = new HashMap<String, String>();
	public static String message_files = System.getProperty("user.dir") + "\\src\\resources\\messages\\files\\";
	public static String proppath = "\\src\\resources\\GlobalVariable.properties";
	public static String proppathhht = "\\src\\resources\\HHTLocators.properties";
	public static String proppathsst = "\\src\\resources\\SSTLocators.properties";
	public static String proppathuldsight = "\\src\\resources\\ULDSighting.properties";
	
	public String testrunner_path = "D:\\Applns\\SmartBear\\SoapUI-5.2.0\\bin\\testrunner.bat";
	public static String jmeterFilePath=System.getProperty("user.dir")+"\\jmeter\\";
	int countWait, j, k = 0;
	public int retryCount = 0;

	public CustomFunctions(WebDriver driver, ExcelReadWrite excelReadWrite, Xls_Read xls_Read2) {

		this.driver = driver;
		this.excelreadwrite = excelReadWrite;
		commonUtility = new CommonUtility();
		this.xls_Read = xls_Read2;
		excelRead = new ExcelRead();
		excelfilename = this.getClass().getSimpleName();
		actions = new Actions(driver);

	}

	public void clickExpandButton(String sheetName, String locator, String eleName, String screenName)
			throws InterruptedException, IOException {
		clickWebElement(sheetName, locator, eleName, screenName);
	}

	public void searchAWB(String awbNo) throws InterruptedException, IOException {
		enterValueInTextbox("AVI", "inbx_awbNo;xpath", awbNo, "AWB No", "AVI");
		clickWebElement("AVI", "btn_search;xpath", "Search Button", "AVI");
		waitForSync(4);
	}
	
	/**
	 * @author A-7271
	 * @param locatorName
	 * @param propFile
	 * Desc : Enter value in hht
	 * @throws IOException 
	 */
	public void enterValueInHHT(String locatorName,String propFile,String value,String ele,String screenName) throws IOException
	{
		String locatorValue="";
		
		
		try
		{
			
			locatorValue=getPropertyValue(propFile, locatorName);
			locatorName=locatorName.split(";")[1].toString();
			switch (locatorName) {
			
			case "xpath":
				androiddriver.findElement(By.xpath(locatorValue)).sendKeys(value);
				break;
				
			case "accessibilityId":
				androiddriver.findElementByAccessibilityId(locatorValue).sendKeys(value);
				break;
			}
		 
			writeExtent("Pass", "Entered " +ele+" as "+value + " on "+screenName);
					
		
		
		}
		
		catch(Exception e)
		{
			captureScreenShot("Android");
			writeExtent("Fail", "Could not enter " +ele+" as "+value + " on "+screenName);
		}
		
	}
	/**
     * Description : Kill the processes
       * @param process: Should specify the required process to be killed
       * 
       *@Sample format:(CMD)
       * @author A-7271
       */

     public enum processes {
       CMD, WinDriver,Putty
     }
	
	 public void killProcesses(String process) throws IOException
     {
            Runtime runtime = Runtime.getRuntime();
            
            switch (processes.valueOf(process)) {
            
               case CMD:
            runtime.exec("taskkill /f /im cmd.exe") ;   
            
                   break;
				   
				     case WinDriver:
                   runtime.exec("taskkill /f /im Winium.Desktop.Driver.exe") ;
                   break;
				     case Putty:
            	runtime.exec("taskkill /f /im putty.exe") ; 
               			
            	break;
     }
     }

	/**
	 * Description... Verfies any number of column data in a table
	 * 
	 * @param sheetName
	 *            xpath sheet name
	 * @param locator
	 *            xpath locator
	 * @param tableTag
	 *            tagname of the element whose value is to be verified
	 * @param verfCols
	 *            array of column numbers
	 * @param pmyKey
	 *            unique number for selecting a row
	 * @param actVerfValues
	 *            values to be verified
	 */

	public void verify_tbl_records_multiple_cols_info_inreport(String sheetName, String locator, String tableTag, int verfCols[],
			String pmyKey, String actVerfValues[],String VP,boolean isAssertReq) {
		try {
			boolean flag = false;
			int row = 0;
			String ScreenName = sheetName.split("_")[0];
			// get the required row
			String tableBody = xls_Read.getCellValue(sheetName, locator);
			List<WebElement> rows = driver.findElements(By.xpath(xls_Read.getCellValue(sheetName, locator)));
			String dynXpath = xls_Read.getCellValue(sheetName, locator) + tableTag;

			System.out.println("row size  " + rows.size());
			switch (tableTag) {
			case "//input": {
				for (int i = 0; i < rows.size(); i++) {

					List<WebElement> cols = driver.findElements(By.xpath(dynXpath));

					for (int j = 0; j < cols.size(); j++) {

						System.out.println("col text = " + cols.get(j).getAttribute("value"));
						if (cols.get(j).getAttribute("value").contains(pmyKey)) {
							flag = true;
							break;

						}
					}
					if (flag) {
						row = i + 1;
						break;
					}
				}
				for (int i = 0; i < verfCols.length; i++) {
					dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
					WebElement ele = null;
					dynXpath = "(" + tableBody + ")[" + row + "]" + "//td[" + verfCols[i] + "]//input";
					ele = driver.findElement(By.xpath(dynXpath));

					String actual = ele.getText().toLowerCase().replace(" ", "");
					String a1 = ele.getAttribute("value");
					if (actual.length() == 0)
						actual = a1.toLowerCase();
					String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
					expected="'"+VP+"'"+" "+expected;
					if (expected.contains(actual)) {
						System.out.println("found true for " + actVerfValues[i]);

						onPassUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					} else {
						onInfoUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
								"Table verification",isAssertReq);

					}
				}
			}
				break;
			case "//td":

			{

				rows = driver.findElements(By.xpath(tableBody));
				dynXpath = tableBody + tableTag;
				{
					System.out.println(pmyKey);
					for (int i = 0; i <= rows.size(); i++) {
						System.out.println("i= " + i);
						System.out.println(rows.get(i).getText().toLowerCase());

						if (rows.get(i).getText().toLowerCase().replace(" ", "")
								.contains(pmyKey.toLowerCase().replace(" ", ""))) {

							flag = true;

						}

						if (flag) {
							row = i + 1;
							break;
						}
					}

					System.out.println("row = " + row);
					for (int i = 0; i < verfCols.length; i++) {

						dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
						WebElement ele = null;

						ele = driver.findElement(By.xpath(dynXpath));

						String actual = ele.getText().toLowerCase().replace(" ", "");
						System.out.println(actual);
						String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
						expected="'"+VP+"'"+" "+expected;
						System.out.println(expected);
						if (expected.contains(actual)) {
							System.out.println("found true for " + actVerfValues[i]);

							onPassUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
									"Table verification");

						} else {
							onInfoUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
									"Table verification",isAssertReq);

						}

					}

				}
			}
				break;

			case "//div":

			{
				for (int i = 0; i <= rows.size(); i++) {
					System.out.println("i= " + i);

					if (rows.get(i).getText().toLowerCase().replace(" ", "")
							.contains(pmyKey.toLowerCase().replace(" ", ""))) {

						flag = true;

					}

					if (flag) {
						row = i + 1;
						break;
					}
				}
			}
				System.out.println("row = " + row);
				for (int i = 0; i < verfCols.length; i++) {

					dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
					WebElement ele = null;

					ele = driver.findElement(By.xpath(dynXpath));

					String actual = ele.getText().toLowerCase().replace(" ", "");
					String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
					expected="'"+VP+"'"+" "+expected;
					if (expected.contains(actual)) {
						System.out.println("found true for " + actVerfValues[i]);

						onPassUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					} else {
						onInfoUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
								"Table verification",isAssertReq);

					}

					break;
				}
			case "input": {

				String Xpath = xls_Read.getCellValue(sheetName, locator);
				List<WebElement> rows1 = driver.findElements(By.xpath(Xpath));
				for (int i = 0; i < rows1.size(); i++) {

					String dynxpath = "(" + Xpath + ")[" + (i + 1) + "]//input";
					List<WebElement> cols = driver.findElements(By.xpath(dynxpath));

					for (int j = 0; j < cols.size(); j++) {

						System.out.println("col text = " + cols.get(j).getAttribute("value"));
						if (cols.get(j).getAttribute("value").contains(pmyKey)) {
							flag = true;
							break;
						}
					}
					if (flag) {
						row = i + 1;
						break;
					}
				}
				for (int i = 0; i < verfCols.length; i++) {
					dynXpath = "(" + tableBody + ")[" + row + "]//" + tableTag + "[" + verfCols[i] + "]";
					WebElement ele = null;
					dynXpath = "(" + tableBody + ")[" + row + "]" + "//td[" + verfCols[i] + "]//input";
					ele = driver.findElement(By.xpath(dynXpath));

					String actual = ele.getText().toLowerCase().replace(" ", "");
					String a1 = ele.getAttribute("value");
					if (actual.length() == 0)
						actual = a1.toLowerCase();
					String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
					expected="'"+VP+"'"+" "+expected;
					if (expected.contains(actual)) {
						System.out.println("found true for " + actVerfValues[i]);

						onPassUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					} else {
						onInfoUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
								"Table verification",isAssertReq);

					}
				}
			}
				break;
			case "//label": {

				String Xpath = xls_Read.getCellValue(sheetName, locator);
				List<WebElement> rows1 = driver.findElements(By.xpath(Xpath));
				for (int i = 0; i < rows1.size(); i++) {

					String dynxpath = "(" + Xpath + "[" + (i + 1) + "])//label";
					List<WebElement> cols = driver.findElements(By.xpath(dynxpath));

					for (int j = 0; j < cols.size(); j++) {

						System.out.println("col text = " + cols.get(j).getText());
						if (cols.get(j).getText().contains(pmyKey)) {
							flag = true;
							break;
						}
					}
					if (flag) {
						row = i + 1;
						break;
					}
				}
				for (int i = 0; i < verfCols.length; i++) {
					dynXpath = tableBody + "[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
					WebElement ele = null;
					dynXpath = tableBody + "[" + row + "]" + "//td[" + verfCols[i] + "]//label";
					ele = driver.findElement(By.xpath(dynXpath));

					String actual = ele.getText().toLowerCase().replace(" ", "");
					String a1 = ele.getText();
					if (actual.length() == 0)
						actual = a1.toLowerCase();
					String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
					expected="'"+VP+"'"+" "+expected;
					if (expected.contains(actual)) {
						System.out.println("found true for " + actVerfValues[i]);

						onPassUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					} else {
						onInfoUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
								"Table verification",isAssertReq);

					}
				}
			}
				break;

			}

		} catch (Exception e) {
			

				test.log(LogStatus.INFO, "Could not perform table record verification or no records found for "+VP+" for "+pmyKey);
				System.out.println("Table contents are not verified or verification failed");
			
				
			

		}
	}
	/**
	 * Description... Verfies any number of column data in a table
	 * 
	 * @param sheetName
	 *            xpath sheet name
	 * @param locator
	 *            xpath locator
	 * @param tableTag
	 *            tagname of the element whose value is to be verified
	 * @param verfCols
	 *            array of column numbers
	 * @param pmyKey
	 *            unique number for selecting a row
	 * @param actVerfValues
	 *            values to be verified
	 * @throws IOException 
	 */

	public void verify_tbl_records_multiple_cols(String sheetName, String locator, String tableTag, int verfCols[],
			String pmyKey, String actVerfValues[],String VP,boolean isAssertReq) throws IOException {
		try {
			boolean flag = false;
			int row = 0;
			String ScreenName = sheetName.split("_")[0];
			// get the required row
			String tableBody = xls_Read.getCellValue(sheetName, locator);
			List<WebElement> rows = driver.findElements(By.xpath(xls_Read.getCellValue(sheetName, locator)));
			String dynXpath = xls_Read.getCellValue(sheetName, locator) + tableTag;

			System.out.println("row size  " + rows.size());
			switch (tableTag) {
			case "//input": {
				for (int i = 0; i < rows.size(); i++) {

					List<WebElement> cols = driver.findElements(By.xpath(dynXpath));

					for (int j = 0; j < cols.size(); j++) {

						System.out.println("col text = " + cols.get(j).getAttribute("value"));
						if (cols.get(j).getAttribute("value").contains(pmyKey)) {
							flag = true;
							break;

						}
					}
					if (flag) {
						row = i + 1;
						break;
					}
				}
				for (int i = 0; i < verfCols.length; i++) {
					dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
					WebElement ele = null;
					dynXpath = "(" + tableBody + ")[" + row + "]" + "//td[" + verfCols[i] + "]//input";
					ele = driver.findElement(By.xpath(dynXpath));

					String actual = ele.getText().toLowerCase().replace(" ", "");
					String a1 = ele.getAttribute("value");
					if (actual.length() == 0)
						actual = a1.toLowerCase();
					String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
					expected="'"+VP+"'"+" "+expected;
					if (expected.contains(actual)) {
						System.out.println("found true for " + actVerfValues[i]);

						onPassUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					} else {
						onFailUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
								"Table verification",isAssertReq);

					}
				}
			}
				break;
			case "//td":

			{

				rows = driver.findElements(By.xpath(tableBody));
				dynXpath = tableBody + tableTag;
				{
					System.out.println(pmyKey);
					for (int i = 0; i <= rows.size(); i++) {
						System.out.println("i= " + i);
						System.out.println(rows.get(i).getText().toLowerCase());

						if (rows.get(i).getText().toLowerCase().replace(" ", "")
								.contains(pmyKey.toLowerCase().replace(" ", ""))) {

							flag = true;

						}

						if (flag) {
							row = i + 1;
							break;
						}
					}

					System.out.println("row = " + row);
					for (int i = 0; i < verfCols.length; i++) {

						dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
						WebElement ele = null;

						ele = driver.findElement(By.xpath(dynXpath));

						String actual = ele.getText().toLowerCase().replace(" ", "");
						System.out.println(actual);
						String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
						expected="'"+VP+"'"+" "+expected;
						System.out.println(expected);
						if (expected.contains(actual)) {
							System.out.println("found true for " + actVerfValues[i]);

							onPassUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
									"Table verification");

						} else {
							onFailUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
									"Table verification",isAssertReq);

						}

					}

				}
			}
				break;

			case "//div":

			{
				for (int i = 0; i <= rows.size(); i++) {
					System.out.println("i= " + i);

					if (rows.get(i).getText().toLowerCase().replace(" ", "")
							.contains(pmyKey.toLowerCase().replace(" ", ""))) {

						flag = true;

					}

					if (flag) {
						row = i + 1;
						break;
					}
				}
			}
				System.out.println("row = " + row);
				for (int i = 0; i < verfCols.length; i++) {

					dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
					WebElement ele = null;

					ele = driver.findElement(By.xpath(dynXpath));

					String actual = ele.getText().toLowerCase().replace(" ", "");
					String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
					expected="'"+VP+"'"+" "+expected;
					if (expected.contains(actual)) {
						System.out.println("found true for " + actVerfValues[i]);

						onPassUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					} else {
						onFailUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
								"Table verification",isAssertReq);

					}

					break;
				}
			case "input": {

				String Xpath = xls_Read.getCellValue(sheetName, locator);
				List<WebElement> rows1 = driver.findElements(By.xpath(Xpath));
				for (int i = 0; i < rows1.size(); i++) {

					String dynxpath = "(" + Xpath + ")[" + (i + 1) + "]//input";
					List<WebElement> cols = driver.findElements(By.xpath(dynxpath));

					for (int j = 0; j < cols.size(); j++) {

						System.out.println("col text = " + cols.get(j).getAttribute("value"));
						if (cols.get(j).getAttribute("value").contains(pmyKey)) {
							flag = true;
							break;
						}
					}
					if (flag) {
						row = i + 1;
						break;
					}
				}
				for (int i = 0; i < verfCols.length; i++) {
					dynXpath = "(" + tableBody + ")[" + row + "]//" + tableTag + "[" + verfCols[i] + "]";
					WebElement ele = null;
					dynXpath = "(" + tableBody + ")[" + row + "]" + "//td[" + verfCols[i] + "]//input";
					ele = driver.findElement(By.xpath(dynXpath));

					String actual = ele.getText().toLowerCase().replace(" ", "");
					String a1 = ele.getAttribute("value");
					if (actual.length() == 0)
						actual = a1.toLowerCase();
					String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
					expected="'"+VP+"'"+" "+expected;
					if (expected.contains(actual)) {
						System.out.println("found true for " + actVerfValues[i]);

						onPassUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					} else {
						onFailUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
								"Table verification",isAssertReq);

					}
				}
			}
				break;
			case "//label": {

				String Xpath = xls_Read.getCellValue(sheetName, locator);
				List<WebElement> rows1 = driver.findElements(By.xpath(Xpath));
				for (int i = 0; i < rows1.size(); i++) {

					String dynxpath = "(" + Xpath + "[" + (i + 1) + "])//label";
					List<WebElement> cols = driver.findElements(By.xpath(dynxpath));

					for (int j = 0; j < cols.size(); j++) {

						System.out.println("col text = " + cols.get(j).getText());
						if (cols.get(j).getText().contains(pmyKey)) {
							flag = true;
							break;
						}
					}
					if (flag) {
						row = i + 1;
						break;
					}
				}
				for (int i = 0; i < verfCols.length; i++) {
					dynXpath = tableBody + "[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
					WebElement ele = null;
					dynXpath = tableBody + "[" + row + "]" + "//td[" + verfCols[i] + "]//label";
					ele = driver.findElement(By.xpath(dynXpath));

					String actual = ele.getText().toLowerCase().replace(" ", "");
					String a1 = ele.getText();
					if (actual.length() == 0)
						actual = a1.toLowerCase();
					String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
					expected="'"+VP+"'"+" "+expected;
					if (expected.contains(actual)) {
						System.out.println("found true for " + actVerfValues[i]);

						onPassUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					} else {
						onFailUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
								"Table verification",isAssertReq);

					}
				}
			}
				break;

			}

		} catch (Exception e) {
			retryCount = retryCount + 1;

			if (retryCount <= 3) {
				
				verify_tbl_records_multiple_cols(sheetName, locator, tableTag, verfCols, pmyKey,actVerfValues,VP,isAssertReq);
				
			}

			else {

				test.log(LogStatus.FAIL, "Could not perform table record verification or no records found for "+VP+" for "+pmyKey);
				System.out.println("Table contents are not verified or verification failed");
				if(!customFunction.getPropertyValue(globalVarPath, "isClubbedTC").equals("Yes"))
				{
					if(isAssertReq)
					{
						Assert.assertFalse(true, "Could not perform table record verification");
					}
				}
			}

		}
	}
	/**
	 * Description... Verfies any number of column data in a table
	 * 
	 * @param sheetName
	 *            xpath sheet name
	 * @param locator
	 *            xpath locator
	 * @param tableTag
	 *            tagname of the element whose value is to be verified
	 * @param verfCols
	 *            array of column numbers
	 * @param pmyKey
	 *            unique number for selecting a row
	 * @param actVerfValues
	 *            values to be verified
	 * @throws IOException 
	 */

	public void verify_tbl_records_multiple_cols(String sheetName, String locator, String tableTag, int verfCols[],
			String pmyKey, String actVerfValues[],boolean isAssertReq) throws IOException {
		try {
			boolean flag = false;
			int row = 0;
			String ScreenName = sheetName.split("_")[0];
			// get the required row
			String tableBody = xls_Read.getCellValue(sheetName, locator);
			List<WebElement> rows = driver.findElements(By.xpath(xls_Read.getCellValue(sheetName, locator)));
			String dynXpath = xls_Read.getCellValue(sheetName, locator) + tableTag;

			System.out.println("row size  " + rows.size());
			switch (tableTag) {
			case "//input": {
				for (int i = 0; i < rows.size(); i++) {

					List<WebElement> cols = driver.findElements(By.xpath(dynXpath));

					for (int j = 0; j < cols.size(); j++) {

						System.out.println("col text = " + cols.get(j).getAttribute("value"));
						if (cols.get(j).getAttribute("value").contains(pmyKey)) {
							flag = true;
							break;

						}
					}
					if (flag) {
						row = i + 1;
						break;
					}
				}
				for (int i = 0; i < verfCols.length; i++) {
					dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
					WebElement ele = null;
					dynXpath = "(" + tableBody + ")[" + row + "]" + "//td[" + verfCols[i] + "]//input";
					ele = driver.findElement(By.xpath(dynXpath));

					String actual = ele.getText().toLowerCase().replace(" ", "");
					String a1 = ele.getAttribute("value");
					if (actual.length() == 0)
						actual = a1.toLowerCase();
					String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
					
					if (expected.contains(actual)) {
						System.out.println("found true for " + actVerfValues[i]);

						onPassUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					} else {
						onFailUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
								"Table verification",isAssertReq);

					}
				}
			}
				break;
			case "//td":

			{

				rows = driver.findElements(By.xpath(tableBody));
				dynXpath = tableBody + tableTag;
				{
					System.out.println(pmyKey);
					for (int i = 0; i <= rows.size(); i++) {
						System.out.println("i= " + i);
						System.out.println(rows.get(i).getText().toLowerCase());

						if (rows.get(i).getText().toLowerCase().replace(" ", "")
								.contains(pmyKey.toLowerCase().replace(" ", ""))) {

							flag = true;

						}

						if (flag) {
							row = i + 1;
							break;
						}
					}

					System.out.println("row = " + row);
					for (int i = 0; i < verfCols.length; i++) {

						dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
						WebElement ele = null;

						ele = driver.findElement(By.xpath(dynXpath));

						String actual = ele.getText().toLowerCase().replace(" ", "");
						System.out.println(actual);
						String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
						
						System.out.println(expected);
						if (expected.contains(actual)) {
							System.out.println("found true for " + actVerfValues[i]);

							onPassUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
									"Table verification");

						} else {
							onFailUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
									"Table verification",isAssertReq);

						}

					}

				}
			}
				break;

			case "//div":

			{
				for (int i = 0; i <= rows.size(); i++) {
					System.out.println("i= " + i);

					if (rows.get(i).getText().toLowerCase().replace(" ", "")
							.contains(pmyKey.toLowerCase().replace(" ", ""))) {

						flag = true;

					}

					if (flag) {
						row = i + 1;
						break;
					}
				}
			}
				System.out.println("row = " + row);
				for (int i = 0; i < verfCols.length; i++) {

					dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
					WebElement ele = null;

					ele = driver.findElement(By.xpath(dynXpath));

					String actual = ele.getText().toLowerCase().replace(" ", "");
					String expected = (actVerfValues[i].replace(" ", "").toLowerCase());

					if (expected.contains(actual)) {
						System.out.println("found true for " + actVerfValues[i]);

						onPassUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					} else {
						onFailUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
								"Table verification",isAssertReq);

					}

					break;
				}
			case "input": {

				String Xpath = xls_Read.getCellValue(sheetName, locator);
				List<WebElement> rows1 = driver.findElements(By.xpath(Xpath));
				for (int i = 0; i < rows1.size(); i++) {

					String dynxpath = "(" + Xpath + ")[" + (i + 1) + "]//input";
					List<WebElement> cols = driver.findElements(By.xpath(dynxpath));

					for (int j = 0; j < cols.size(); j++) {

						System.out.println("col text = " + cols.get(j).getAttribute("value"));
						if (cols.get(j).getAttribute("value").contains(pmyKey)) {
							flag = true;
							break;
						}
					}
					if (flag) {
						row = i + 1;
						break;
					}
				}
				for (int i = 0; i < verfCols.length; i++) {
					dynXpath = "(" + tableBody + ")[" + row + "]//" + tableTag + "[" + verfCols[i] + "]";
					WebElement ele = null;
					dynXpath = "(" + tableBody + ")[" + row + "]" + "//td[" + verfCols[i] + "]//input";
					ele = driver.findElement(By.xpath(dynXpath));

					String actual = ele.getText().toLowerCase().replace(" ", "");
					String a1 = ele.getAttribute("value");
					if (actual.length() == 0)
						actual = a1.toLowerCase();
					String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
				
					if (expected.contains(actual)) {
						System.out.println("found true for " + actVerfValues[i]);

						onPassUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					} else {
						onFailUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
								"Table verification",isAssertReq);

					}
				}
			}
				break;
			case "//label": {

				String Xpath = xls_Read.getCellValue(sheetName, locator);
				List<WebElement> rows1 = driver.findElements(By.xpath(Xpath));
				for (int i = 0; i < rows1.size(); i++) {

					String dynxpath = "(" + Xpath + "[" + (i + 1) + "])//label";
					List<WebElement> cols = driver.findElements(By.xpath(dynxpath));

					for (int j = 0; j < cols.size(); j++) {

						System.out.println("col text = " + cols.get(j).getText());
						if (cols.get(j).getText().contains(pmyKey)) {
							flag = true;
							break;
						}
					}
					if (flag) {
						row = i + 1;
						break;
					}
				}
				for (int i = 0; i < verfCols.length; i++) {
					dynXpath = tableBody + "[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
					WebElement ele = null;
					dynXpath = tableBody + "[" + row + "]" + "//td[" + verfCols[i] + "]//label";
					ele = driver.findElement(By.xpath(dynXpath));

					String actual = ele.getText().toLowerCase().replace(" ", "");
					String a1 = ele.getText();
					if (actual.length() == 0)
						actual = a1.toLowerCase();
					String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
				
					if (expected.contains(actual)) {
						System.out.println("found true for " + actVerfValues[i]);

						onPassUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					} else {
						onFailUpdate(ScreenName, expected+" for "+pmyKey, actual, "Table verification against " + pmyKey + " On ",
								"Table verification",isAssertReq);

					}
				}
			}
				break;

			}

		} catch (Exception e) {
			retryCount = retryCount + 1;

			if (retryCount <= 3) {
				verify_tbl_records_multiple_cols(sheetName, locator, tableTag, verfCols, pmyKey,actVerfValues,isAssertReq);
			}

			else {

				test.log(LogStatus.FAIL, "Could not perform table record verification or no records found for "+pmyKey);
				System.out.println("Table contents are not verified or verification failed");
				if(!customFunction.getPropertyValue(globalVarPath, "isClubbedTC").equals("Yes"))
				{
					if(isAssertReq)
					{
						Assert.assertFalse(true, "Could not perform table record verification");
					}
				}
			}

		}
	}
	/**
	 * @author A-7271
	 * @param locatorName
	 * @param propFile
	 * @param ele
	 * @param screenName
	 * @return
	 * @throws IOException
	 * Desc : getTextAndroid
	 */
	public String getTextAndroid(String locatorName,String propFile,String ele,String screenName) throws IOException
	{
		String locatorValue="";
		String textValue="";
		
		try
		{
			
			locatorValue=getPropertyValue(propFile, locatorName);
			locatorName=locatorName.split(";")[1].toString();
			
			
			switch (locatorName) {
			
			case "xpath":
				textValue=androiddriver.findElement(By.xpath(locatorValue)).getText();
				break;
				
			case "accessibilityId":
				textValue=androiddriver.findElementByAccessibilityId(locatorValue).getText();
				break;
			}
		 
			 writeExtent("Pass", "Returned text value as '"+textValue+"' of " + ele + " on "+screenName);
				return 	textValue;
				 
		
		}
		
		catch(Exception e)
		{
			captureScreenShot("Android");
			writeExtent("Fail", "Could not return text of " + ele + " on "+screenName);
			return 	textValue;
		}
		
	}
	/**
	 * @author A-7271
	 * @param locatorName
	 * @param propFile
	 * Desc : Click action in hht
	 * @throws IOException 
	 */
	public void clickActionInHHT(String locatorName,String propFile,String ele,String screenName) throws IOException
	{
		String locatorValue="";
		
		
		try
		{
			
			locatorValue=getPropertyValue(propFile, locatorName);
			locatorName=locatorName.split(";")[1].toString();
			
			
			switch (locatorName) {
			
			case "xpath":
				androiddriver.findElement(By.xpath(locatorValue)).click();
				
				
				
				break;
				
			case "accessibilityId":
				androiddriver.findElementByAccessibilityId(locatorValue).click();
				break;
			}
		 
			writeExtent("Pass", "Clicked on " + ele + " on "+screenName);
					
		
		
		}
		
		catch(Exception e)
		{
			captureScreenShot("Android");
			writeExtent("Fail", "Could not click on " + ele + " on "+screenName);
		}
		
	}
	
	/**
	 * @author A-7271
	 * @param locatorName
	 * @param propFile
	 * Desc : Click action in hht
	 */
	public int getSizeOfMobileElement(String locatorName,String propFile)
	{
		String locatorValue="";
		int size=0;
		
		
		try
		{
			
			locatorValue=getPropertyValue(propFile, locatorName);
			locatorName=locatorName.split(";")[1].toString();
			switch (locatorName) {
			
			case "xpath":
				
				size=androiddriver.findElements(By.xpath(locatorValue)).size();
				
				return size;
				
			case "accessibilityId":
				size=androiddriver.findElementsByAccessibilityId(locatorValue).size();
				return size;
			}
		 
			return size;
		
		
		}
		
		catch(Exception e)
		{
			return size;
		}
		
	}
	
	/**
	 * @author A-7271
	 * @param screenName
	 * Desc : Verify save details in hht screen
	 * @throws IOException 
	 */
	public void verifyHHTSaveDetails(String screenName) throws IOException
	{
		try
		{
		 int size=getSizeOfMobileElement("txt_msgConfimation;xpath",proppathhht);
			
			/*** CLOSE CONFIRMATION MESSAGE**/
  	    clickActionInHHT("btn_msgConfirmation;xpath",proppathhht,"Close confirmation message",screenName);	
			
			waitForSync(2);
			
			if(size==1)
			{
			 writeExtent("Pass", "Details saved successfully in "+screenName);
			}
			else
			{
				captureScreenShot("Android");
				 writeExtent("Fail", "Details not saved successfully in "+screenName);
			}
		}
		
		catch(Exception e)
		{
			 captureScreenShot("Android");
			 writeExtent("Fail", "Details not saved successfully in "+screenName);
		}
	}
	
	/**
	* Desc : Clearing an existing value for an Web element.
	* @author A-9175
	* @param locatorName
	* @param propFile
	* @param ele
	* @param screenName
	 * @throws IOException 
	*/

	public void clearValueInHHT(String locatorName,String propFile,String ele,String screenName) throws IOException
	{
	       String locatorValue="";
	       
	       
	       try
	       {
	             
	             locatorValue=getPropertyValue(propFile, locatorName);
	             locatorName=locatorName.split(";")[1].toString();
	             switch (locatorName) {
	             
	             case "xpath":
	                    androiddriver.findElement(By.xpath(locatorValue)).clear();
	                    break;
	                    
	             case "accessibilityId":
	                    androiddriver.findElementByAccessibilityId(locatorValue).clear();
	                    break;
	             }
	       
	             writeExtent("Pass", "Cleared " +ele+" value on "+screenName);
	                           
	       
	       
	       }
	       
	       catch(Exception e)
	       {
	    	   captureScreenShot("Android");
	             writeExtent("Fail", "Could not Clear " +ele+" value on "+screenName);
	       }
	       
	}
	/**
	 * @author A-7271
	 * @param locatorName
	 * @param propFile
	 * @param ele
	 * @param screenName
	 * @return
	 * @throws IOException
	 * Desc : get Text from HHt and return as the string value
	 */

	public String getTextFromHHT(String locatorName,String propFile,String ele,String screenName) throws IOException
	{
	       String locatorValue="";
	       String value="";
	       
	       
	       try
	       {
	             
	             locatorValue=getPropertyValue(propFile, locatorName);
	             locatorName=locatorName.split(";")[1].toString();
	             switch (locatorName) {
	             
	             case "xpath":
	            	 value= androiddriver.findElement(By.xpath(locatorValue)).getText();
	                  break;
	                    
	             case "accessibilityId":
	            	 value=  androiddriver.findElementByAccessibilityId(locatorValue).getText();
	            	 break;
	            	 
	             }
	       
	             writeExtent("Pass", "Returned value "+value+" from the field '"+ele+"' on "+screenName);
	             return value;         
	       
	       
	       }
	       
	       catch(Exception e)
	       {
	    	   captureScreenShot("Android");
	    	   writeExtent("Fail", "Could not return value from the field '"+ele+"' on "+screenName);
	    	   return value; 
	       }
	       
	}
	/**
	 * Description : closing the extent report instance
	 * @author A-7271
	 * 
	 */
	public void closeExtentReport()
	{
		
		ExtentManager.getReporterInstance().endTest(test);
		ExtentManager.getReporterInstance().flush();
	}
	/**
	 * Description : Initializing of test instance
	 * @author A-7271
	 * @param testName
	 */
	public void openExtentReport(String testName)
	{
		System.out.println(testName);
		test = ExtentManager.getReporterInstance().startTest(testName);
	}
/**
* Description... Get all the text from the alert
* @return
* @throws Exception
*/
public String handleAlertAndReturnText(){
      switchToFrame("default");
      String AlertText = "";
      AlertText = driver.findElement(By.xpath(xls_Read.getCellValue("Generic_Elements", "txt_AlertText;xpath")))
                        .getText();
      return AlertText;

}

	public void logineTracking(String username, String password) throws InterruptedException, IOException {
		
		waitForSync(5);		
		clickWebElement("eTracking", "lnk_login;xpath", "Login Link", "eTracking");
		enterValueInTextbox("eTracking", "inbx_username;xpath", username, "username", "eTracking");
		enterValueInTextbox("eTracking", "inbx_password;xpath", password, "password", "eTracking");
		clickWebElement("eTracking", "btn_login;xpath", "Login Button", "eTracking");

		waitForSync(20);
		clickWebElement("eTracking", "lnk_login;xpath", "Login Link", "eTracking");
		enterValueInTextbox("eTracking", "inbx_username;xpath", username, "username", "eTracking");
		enterValueInTextbox("eTracking", "inbx_password;xpath", password, "password", "eTracking");
		clickWebElement("eTracking", "btn_login;xpath", "Login Button", "eTracking");
		waitForSync(20);
		By b = getElement("eTracking", "logo_lufthansa;xpath");
		boolean logo = driver.findElement(b).isDisplayed();
		if (logo)
			onPassUpdate("Lufthansa Cargo", "cargo Logo Displayed", "cargo Logo Displayed", "Login",
					"1. Open URL \n2. Enter Username, Password \n3. Click Login");
		else
			onFailUpdate("Lufthansa Cargo", "cargo Logo Displayed", "cargo Logo not Displayed", "Login",
					"1. Open URL \n2. Enter Username, Password \n3. Click Login");

	}
	/**
		 * Description... Login to loginmICAPScale
		 * 
		 * @param UserName
		 * @param Password
		 * @throws Exception
		 */
		public void loginmICAPScale(String UserName, String Password,String deviceID) throws Exception {

			waitForSync(4);
            waitForLoad(driver);
            enterValueInTextbox("miCap", "inbx_userName_micap;xpath", UserName, "Username", "miCap");
            enterValueInTextbox("miCap", "inbx_password_micap;xpath", Password, "Password", "miCap");
            enterValueInTextbox("miCap", "inbx_deviceID_micap;xpath", data(deviceID), "DeviceID", "miCap");
           
            Thread.sleep(5000);
         
           
           clickWebElementByActionClass("miCap", "btn_login_micap;xpath","login Button", "miCap");
            waitForLoad(driver);
            waitForSync(4);
            try {
                

                  onPassUpdate("miCap", "miCap Logo is Displayed", "Login is Successful", "Login",
                              "1. Enter Username \n2. Enter Password \n3. Click Login Button");
            } catch (Exception e) {

                  onFailUpdate("miCap", "miCap Logo is not Displayed", "Login Failed", "Login",
                              "1. Enter Username \n2. Enter Password \n3. Click Login Button");
            }
            }
		
		/**
		 * @author A-7271
		 * @param text
		 * Description : Scroll in mobile device
		 */
		public void scrollInMobileDevice(String text)
		{
			((FindsByAndroidUIAutomator<MobileElement>) androiddriver).findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\""+text+"\").instance(0))");
		}
		
		/**
		 * @author A-9175
		 * Description : Scroll to exact match
		 * @param text
		 */
		public void scrollInMobileDeviceToExactTextMatch(String text)
		{
			((FindsByAndroidUIAutomator<MobileElement>) androiddriver).findElementByAndroidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(new UiSelector().text(\""+text+"\").instance(0))");
		}
	      /**
	      * @author A-9478
	      * Description: Scroll till end of the Screen
	      */
	      public void scrollTillEnd()
	      {
	            //To scroll till end
	            Dimension size = androiddriver.manage().window().getSize();
	        int y_start=(int)(size.height*0.60);
	        int y_end=(int)(size.height*0.30);
	        int x=size.width/2;
	        androiddriver.swipe(x,y_start,x,y_end,4000);
	      }

/**
	 * Description... Login to iCargo
	 * 
	 * @param UserName
	 * @param Password
	 * @throws Exception
	 */
	public void loginICargoIE(String UserName, String Password) throws Exception {
		
		
		String win=driver.getWindowHandle();
		
		System.out.println("win1"+win);
		waitForSync(4);
		waitForLoad(driver);
		enterValueInTextbox("Login", "inbx_userName;xpath", UserName, "Username", "Login");
		enterValueInTextbox("Login", "inbx_password;xpath", Password, "Password", "Login");
		clickWebElement("Login", "btn_login;xpath", "Click Button", "Login");
		waitForLoad(driver);
		waitForSync(4);
		
		Robot r=new Robot();
		r.keyPress(KeyEvent.VK_ENTER);
		
		waitForSync(4);
				
		
		
		try
		{
			
		
		Set<String> windowhandle = driver.getWindowHandles();
		
		
		for(String wind:windowhandle)
		{
			driver.switchTo().window(wind);
			waitForSync(10);
		}
		}
		
		catch(Exception e)
		{
			
			driver.switchTo().window(win);
		}
		
      
		
	}

	public void loginMicap(String UserName, String Password) throws Exception {

                  waitForSync(4);
                  waitForLoad(driver);
                  enterValueInTextbox("miCap", "inbx_userName_micap;xpath", UserName, "Username", "miCap");
                  enterValueInTextbox("miCap", "inbx_password_micap;xpath", Password, "Password", "miCap");
                  Thread.sleep(5000);
                  clickWebElementByActionClass("miCap", "btn_login_micap;xpath","login Button", "miCap");
                  waitForLoad(driver);
                  waitForSync(4);
                  try {
                        waitTillOverlayDisappear(By.xpath(xls_Read.getCellValue("miCap", "btn_TO_List;xpath")));
                        driver.findElement(By.xpath(xls_Read.getCellValue("Login", "btn_TO_List;xpath")));

                        onPassUpdate("miCap", "miCap Logo is Displayed", "Login is Successful", "Login",
                                    "1. Enter Username \n2. Enter Password \n3. Click Login Button");
                  } catch (Exception e) {

                        onFailUpdate("miCap", "miCap Logo is not Displayed", "Login Failed", "Login",
                                    "1. Enter Username \n2. Enter Password \n3. Click Login Button");
                  }
                  }
				  /***Open putty**/
	public void openPutty(String sessionName)
			throws IOException, InterruptedException {

		try {
			String puttyPath = getPropertyValue(proppath, "puttyPath");
			String pvtKeyPath = getPropertyValue(proppath, "pvtKeyPath");
			String pwd=getPropertyValue(proppath, "puttyPwd");
			String hostName=getPropertyValue(proppath, "jumpServerHostName");
			
			System.out.println(puttyPath+" -ssh "+hostName+" -load "+sessionName+" -i "+pvtKeyPath+" -pw "+pwd);
					
			Runtime runtime = Runtime.getRuntime();

			runtime.exec("cmd /c start " +puttyPath+" -ssh "+hostName+" -load "+sessionName+" -i "+pvtKeyPath+" -pw "+pwd );

			waitForSync(4);
		} catch (Exception e) {

			System.out.println("Could not open putty connection");
			test.log(LogStatus.FAIL, "Could not open putty connection");
		}
	}
	
	/**
	 * Description : Kill the processes
	 * 
	 * @param process:
	 *            Should specify the required process to be killed
	 * 
	 * @Sample format:(CMD)
	 * @author A-7271
	 */

	/**
	 * Description : Invoke the SOAP Suit
	 * 
	 * 
	 */
	public void triggerRESTSuit(String project, String testSuit, String testCase)
			throws IOException, InterruptedException {

		try {
			String sOAPPath = getPropertyValue(proppath, "SoapPath2");
			Runtime runtime = Runtime.getRuntime();

			runtime.exec("cmd /c start " + sOAPPath + " -s\"" + testSuit + "\" -c\"" + testCase
					+ "\" -r -A -a -j -S -f \"D:\\SoapUIResults\" \"" + projDir + "\\" + project + ".xml" + "\""
					+ " -P TCName=" + DriverSetup.testName);

			waitForSync(2);
		} catch (Exception e) {

			System.out.println("Could not trigger soap suit");
			test.log(LogStatus.FAIL, "Could not trigger soap suit");
		}
	}

public void verifyScreenName(String screenTitle) throws InterruptedException
{
       
       String actTitle = getAttributeWebElement("", "", "", "title", "");

       if(screenTitle.contains(actTitle)){
	   
      writeExtent("Pass", "Page title verified for"+ screenTitle + eleName + " : "
                                            + actTitle);
	   }
	   else{
	     writeExtent("Fail", "Page title not verified for"+ screenTitle + eleName + " : "
                                            + actTitle);
	   }
	   
}

	public void loginPortal(String username, String password) throws InterruptedException, IOException {

		enterValueInTextbox("ADC", "inbx_username;xpath", username, "username", "ADC");
		enterValueInTextbox("ADC", "inbx_password;xpath", password, "password", "ADC");
		clickWebElement("ADC", "btn_login;xpath", "Login Button", "ADC");

		waitForSync(20);
		By b = getElement("ADC", "logo_lufthansa;xpath");
		boolean logo = driver.findElement(b).isDisplayed();
		if (logo)
			onPassUpdate("Lufthansa Cargo", "cargo Logo Displayed", "cargo Logo Displayed", "Login",
					"1. Open URL \n2. Enter Username, Password \n3. Click Login");
		else
			onFailUpdate("Lufthansa Cargo", "cargo Logo Displayed", "cargo Logo not Displayed", "Login",
					"1. Open URL \n2. Enter Username, Password \n3. Click Login");

	}

	public void loginBE(String UserName, String Password) throws Exception {

		String browserName=DriverSetup.browser;
		
		

			waitForSync(3);
			try {
				switchToWindow("storeParent");
				driver.findElement(By.name("Ecom_User_ID")).clear();
				driver.findElement(By.name("Ecom_User_ID")).sendKeys(UserName);
				driver.findElement(By.name("Ecom_Password")).sendKeys(Password);
				driver.findElement(By.name("Abschicken")).click();
				waitForSync(30);
				
				
				if(browserName.equals("iexplore"))
				{
				switchToWindow("storeParent");
				switchToWindow("childWindow");
				}
				
				else
				{
					switchToWindow("child_BE");
				}
				

				waitForSync(5);
				String actTitle = driver.getTitle();
				String expTitle = "EasyBooking";

				if (actTitle.contains(expTitle))
					test.log(LogStatus.PASS, "Login Successful to Booking Engine");
				else {
					test.log(LogStatus.FAIL, "Failed to Login to Booking Engine");
					Assert.assertFalse(true, "Failed to Login to Booking Engine");
				}

			} catch (Exception e) {

				test.log(LogStatus.FAIL, "Failed to Login to Booking Engine");
				Assert.assertFalse(true, "Failed to Login to Booking Engine");
			}

	}
/**
	 * @author A-7271
	 * @param filename
	 * @param HA
	 * @param assigneeHA
	 * @param assigneeType
	 * @param vehicleType
	 * @throws IOException 
	 */
	public int filterTOConfigMasterForApplicableRoutes(String filename,String HA,String assigneeHA,String assigneeType,String vehicleType,String certificate,String filterType) throws IOException
	{
			try
		{
         FileInputStream file = new FileInputStream(new File(filename));
		 
		 HSSFWorkbook workbook =new HSSFWorkbook(file);
		 
		 HSSFSheet sheet = workbook.getSheetAt(0);
		 
		 int routes=0;
		 Row row=null;
		 for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			  row = sheet.getRow(rowIndex);
			  if (row != null) {
				  //Origin
			    Cell cell = row.getCell(2);
			    
			    //Destination
			    Cell cell4 = row.getCell(3);
			    
			    //Vehicle Type
			    Cell cell5 = row.getCell(11);
			    
			    //assigneeHA
			    Cell cell2 = row.getCell(12);
			    //assigneeType
			    Cell cell3 = row.getCell(6);
			    
			  //User certificate
			    Cell cell6 = row.getCell(13);
			    
			    
			    
			   
			    if(filterType.equals("Origin"))
			    {
			    	if( cell.getStringCellValue().equals(HA)&& cell2.getStringCellValue().equals(assigneeHA)&& !cell3.getStringCellValue().equals(assigneeType))
			    	{
			    		if( row.getZeroHeight()!=true)
			    		{
			    			routes=routes+1;
			    		}
			    	}
			    }
			    else if(filterType.equals("Destination"))
			    {
			    	if( cell4.getStringCellValue().equals(HA)&& cell2.getStringCellValue().equals(assigneeHA)&& !cell3.getStringCellValue().equals(assigneeType))
			    	{
			    		if( row.getZeroHeight()!=true)
			    		{
			    			routes=routes+1;
			    		}
			    	}
			    }
			    else if(filterType.equals("UserCertificate"))
			    {
			    	if( cell.getStringCellValue().equals(HA)&& cell5.getStringCellValue().equals(vehicleType)&& cell6.getStringCellValue().equals(certificate)&&!cell3.getStringCellValue().equals(assigneeType))
			    	{
			    		if( row.getZeroHeight()!=true)
			    		{
			    			routes=routes+1;
			    		}
			    	}
			    }
			    else if(filterType.equals("WithoutUserCertificate_DST"))
			    {
			    	if( cell4.getStringCellValue().equals(HA)&& cell5.getStringCellValue().equals(vehicleType)&&cell2.getStringCellValue().equals(assigneeHA)&&!cell3.getStringCellValue().equals(assigneeType))
			    	{
			    		if( row.getZeroHeight()!=true)
			    		{
			    			routes=routes+1;
			    		}
			    	}
			    } 
			  }
			}
		 
		 writeExtent("Pass", "Routes are returned for HA "+HA+" and the assignee HA as "+assigneeHA+ "and the vehicle Type as "+vehicleType
				 
				 + " and the certificate type as "+ certificate +" and the no : of routes returned are "+routes);
		 return routes;
		}
		
		catch(Exception e)
		{
			writeExtent("Fail", "No applicable routes are returned for HA "+HA+" and the assignee HA as "+assigneeHA);
			return 0 ;
		}
	}
	public void verify_tbl_records_multiple_cols_AVI(String sheetName, String locator, String tableTag, int verfCols[],
			String pmyKey, String actVerfValues[]) {

		try {
			boolean flag = false;
			int row = 0;
			String ScreenName = sheetName.split("_")[0];
			// get the required row
			String tableBody = xls_Read.getCellValue(sheetName, locator);

			switch (tableTag) {
			case "//input":

				String dynXpath = tableBody + tableTag;
				List<WebElement> rows = driver.findElements(By.xpath(dynXpath));

			{
				for (int i = 0; i <= rows.size(); i++) {
					System.out.println("i= " + i);

					if (rows.get(i).getAttribute("value").toLowerCase().replace(" ", "")
							.contains(pmyKey.toLowerCase().replace(" ", ""))) {

						flag = true;

					}

					if (flag) {
						row = i + 1;
						break;
					}
				}

				System.out.println("row = " + row);
				for (int i = 0; i < verfCols.length; i++) {
					tableTag = "//td[" + verfCols[i] + "]//input";
					dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag;
					WebElement ele = null;

					ele = driver.findElement(By.xpath(dynXpath));
					String actual = ele.getAttribute("value").toLowerCase().replace(" ", "");
					String expected = actVerfValues[i].replace(" ", "").toLowerCase();
					if (actual.contains(expected)) {
						System.out.println("found true for " + actVerfValues[i]);
						// test.log(LogStatus.PASS, "Verified " +
						// actVerfValues[i] + " On " + ScreenName + " Screen");
						onPassUpdate(ScreenName, expected, actual, "AVI Table verification" + " On " + ScreenName,
								"AVI Table verification");

					} else {

						// test.log(LogStatus.FAIL,
						// "Could not Verify " + actVerfValues[i] + " On " +
						// ScreenName + " Screen");
						onFailUpdate(ScreenName, expected, actual, "AVI Table verification" + " On " + ScreenName,
								"AVI Table verification");
					}
				}
			}
			}

		}

		catch (Exception e) {

			System.out.println("Could not verify table data");
			test.log(LogStatus.FAIL, "Could not verify table data");

		}
	}

	/*** Verify multiple column values in webtable ***/
	public void verify_col_records(String sheetName, String attribute, String locator, int verfCols[],
			String actVerfValues[]) {
		String dynXpath = xls_Read.getCellValue(sheetName, locator);
		String dynamicXpath = "";
		String actualValues = "";

		try {

			for (int i = 0; i < verfCols.length; i++) {
				dynamicXpath = "(" + dynXpath + ")" + "[" + verfCols[i] + "]";

				actualValues = driver.findElement(By.xpath(dynamicXpath)).getAttribute(attribute);
				
				System.out.println(actualValues.trim());
				System.out.println(actVerfValues[i].toString().trim());

				if (actualValues.trim().contains(actVerfValues[i].toString().trim())) {
					writeExtent("Pass", "Column values matched ; Actual value : " + actualValues + " expected value : "
							+ actVerfValues[i].toString());
				} else {
					writeExtent("Fail", "Column values not matched ; Actual value : " + actualValues
							+ " expected value : " + actVerfValues[i].toString());
				}
			}

		} catch (Exception e) {
			writeExtent("Fail", "Column values not matched ");
		}
	}


	public void loginAVI(String username, String password) throws InterruptedException, IOException {

		waitForSync(6);
		enterValueInTextbox("LoginAVI", "inbx_AVI_username;xpath", username, "Username", "AVI");

		enterValueInTextbox("LoginAVI", "inbx_AVI_password;xpath", password, "Password", "AVI");
		clickWebElement("LoginAVI", "btn_AVI_login;xpath", "Click Button", "AVI");

	}

	/*** Switch to Main screen ***/
	public void switchToMainScreen(String screenId) {
		switchToFrame("default");
		waitForSync(2);

		String frameName = "iCargoContentFrame" + screenId;
		driver.switchTo().frame(frameName);
	}

	/**** Finding unused AWB from BE 
	 * @throws IOException ****/

	public boolean getNewAwbFromBE(String AwbNo) throws InterruptedException, IOException {

		boolean result = false;
		setPropertyValue("showStopper", "flase", globalVarPath);
		String awbNo = createAWB(AwbNo);
		driver.switchTo().defaultContent();
		driver.switchTo().frame("Main");
		enterValueInTextbox("BE", "inbx_AWBNo;name", awbNo, "Awb Number", "BookingEngine");
		clickWebElement("BE", "btn_displayOrder;name", "Click Button", "BookingEngine");

		Thread.sleep(6000);

		try {
			String xpath = xls_Read.getCellValue("BE", "lbl_displyOrderStatus;xpath");
			if (driver.findElements(By.xpath(xpath)).size() != 0) {
				String str1 = driver.findElement(By.xpath(xpath)).getText().toString().trim();

				if (str1.contains("This booking does not exist")) {

					result = true;
				}

				else if (str1.contains("Error")) {
					setPropertyValue("showStopper", "true", globalVarPath);
				} else if (str1.contains("Unexpected Service response")) {
					setPropertyValue("showStopper", "true", globalVarPath);
				} else if (str1.contains("Failed")) {
					setPropertyValue("showStopper", "true", globalVarPath);
				}

			}
			return result;
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("Could not get new AWB from BE" + e);
			test.log(LogStatus.FAIL, "Could not get new AWB from BE");

			return result;

		}

	}

	/**Method To verify the excel values **/
	
	
public void verifyExcelFile(String path,String [] expectedValues)
{
	
try
{
    
    List<String> list = new ArrayList<String>(Arrays.asList(expectedValues));

    HashMap hm = new HashMap();
    int flag=expectedValues.length;
    
    Cell cell=null;

    FileInputStream file = new FileInputStream(new File(path));
    
 
    HSSFWorkbook workbook = null;
    HSSFSheet sheet = null;
    
    XSSFWorkbook workbook2 =null;
    XSSFSheet sheet2=null;
    
    Iterator<Row> rowIterator=null;
    if(path.contains("xlsx"))
    {
       //Create Workbook instance holding reference to .xlsx file
         workbook2 = new XSSFWorkbook(file);

        //Get first/desired sheet from the workbook
         sheet2 = workbook2.getSheetAt(0);
         
         rowIterator = sheet2.iterator();
    }

    else
    {
       //Create Workbook instance holding reference to .xls file
          workbook = new HSSFWorkbook(file);

        //Get first/desired sheet from the workbook
          sheet = workbook.getSheetAt(0);
          rowIterator = sheet.iterator();
    }
    

    //Iterate through each rows one by one
  
    int i=0;
    while (rowIterator.hasNext()) 
    {
          Row row = rowIterator.next();
          //For each row, iterate through all the columns
          Iterator<Cell> cellIterator = row.cellIterator();
          
          String value="";
          
          while (cellIterator.hasNext()) 
          {
                 cell = cellIterator.next();
                 
                 
                 hm.put(i, value+cell.toString());
                 
                 value=value+cell.toString();
                 
                 System.out.println(value);
                 
               
                 
          }
          
       
          
          
          i=i+1;
          
        
          
          
    }
    int mapSize=hm.size();
  
   
    
   
    for(int j=0;j<=mapSize;j++)
    {
          
          for(int k=0;k<expectedValues.length;k++)
          {
                if(hm.get(j)!=null)
                {
                   
                    
                 if(hm.get(j).toString().contains(expectedValues[k].toString()))
                 {
                        flag=flag-1;
                        
                       
                          writeExtent("Pass", "Value '"+expectedValues[k].toString()+ "' is found in excel "+path);
                        
                        list.remove(expectedValues[k].toString());
                 }
                }
                 
                 
          }
         
          
    }
   
    if(flag==0)
    {
         
          writeExtent("Pass", "All values matched in excel "+path);
    }
    else
    {
        
        
          for(int i1=0;i1<list.size();i1++)
          {
                  System.out.println(list.size());
                  System.out.println(list);
                 writeExtent("Fail", "Value '"+ list + "' is not found in excel "+path);
          }
    }
    file.close();
} 
catch (Exception e) 
{
    e.printStackTrace();
}

}

	/**
	 * Description... Login to TDService
	 * 
	 * @param UserName
	 * @param Password
	 */
	public void loginTDService(String UserName, String Password) throws Exception {
		try {
			waitForSync(4);
			waitForLoad(driver);
			waitForSync(3);
			enterValueInTextbox("TD Services_LCAGSIT", "inbx_username;xpath", UserName, "Username",
					" td.Data-LCAG-SIT");
			enterValueInTextbox("TD Services_LCAGSIT", "inbx_password;xpath", Password, "Password",
					" td.Data-LCAG-SIT");
			clickWebElement("TD Services_LCAGSIT", "btn_login;xpath", "Login Button", "td.Data-LCAG-SIT");
			waitForLoad(driver);
			waitForSync(4);
			onPassUpdate("TD Services_LCAGSIT", "Login is Successful", "Login is Successful", "Login",
					"1. Enter Username \n2. Enter Password \n3. Click Login Button");
		} catch (Exception e) {

			onFailUpdate("TD Services_LCAGSIT", "Login is Successful", "Login Failed", "Login",
					"1. Enter Username \n2. Enter Password \n3. Click Login Button");

		}
	}
		/**
	 * Description : Create the required date format and stores to propertyFile
	 * 
	 * @param dateFormat:
	 *            Should specify the required format
	 * @param value
	 *            : provide the value of day/month/year to be added or
	 *            substracted
	 * @param formats:
	 *            Specify DAY , MONTH or YEAR
	 * @param propsKey:Specify
	 *            the property file key whether the data to be saved
	 * @Sample format:(ddMMMYY,1,DAY,startDate)
	 * @author A-7271
	 */

	public enum formats {
		integer, floatVal, string, doubleVal
	}
public String createTypeCasting(String valueToBeFormated,String formatValue, String propsKey) throws Exception {

		try {
			String formatedVal="";
			int formatedIntVal;
			float formatedFloatVal;

			switch (formats.valueOf(formatValue)) {
			
			case floatVal:

				formatedFloatVal=Float.parseFloat(valueToBeFormated);
				
				 formatedVal=String.valueOf(formatedFloatVal);

			
			}

		
			

			if (!propsKey.equals("")) {
				setPropertyValue(propsKey, formatedVal, proppath);
			}

			return formatedVal;

		}

		catch (Exception e) {
			System.out.println("Could not create type casting");
			test.log(LogStatus.FAIL, "Could not create type casting");
			return "";
		}
	}

	/**
	 * Description... Performs the following mail operations in Outlook.
	 * findMail
	 * /clickMail/countMailTrigger/dataCaptureLink/clickHereLink/checkContent
	 * 
	 * @param expectedMailTriggerCount
	 * @param expSubject
	 * @param expText
	 * @param title
	 * @param IssueFoundText
	 * @param RecActionText
	 * @param imailOps
	 * @return
	 * @throws Exception
	 */
	public void imailOps(int expectedMailTriggerCount, String expSubject, String expText, String title,
			String IssueFoundText, String RecActionText) throws Exception {

		try {
			List<WebElement> subList = driver.findElements(By.xpath("txt_subList;xpath"));
			int j = 0, k = 0;
			for (int i = 0; i < subList.size(); i++)
				if (subList.get(i).getText().equals(expSubject)) {
					System.out.println("index = " + i + " " + subList.get(i).getText());
					j++;
					k = i;

				}

			System.out.println(j);
			subList.get(k).click();
			waitForSync(3);
			String identifiedIssue = getElementText("iMail", "txt_identifiedIssue;xpath", "Identified Issues", "iMail");

			if (expText.equalsIgnoreCase(identifiedIssue))
				onPassUpdate("iMail", expText, identifiedIssue, "Identified Issues Text", "");
			else
				onFailUpdate("iMail", expText, identifiedIssue, "Identified Issues Text", "");

			String sentMultipleTimes = getElementText("iMail", "txt_sentMulTimes;xpath", "Sent multiple times",
					"iMail");

			if (IssueFoundText.equalsIgnoreCase(sentMultipleTimes))
				onPassUpdate("iMail", IssueFoundText, sentMultipleTimes, "Sent multiple times Text", "");
			else
				onFailUpdate("iMail", IssueFoundText, sentMultipleTimes, "Sent multiple times Text", "");

			String actRecActionText = getElementText("iMail", "txt_msgRejection;xpath", "Recommended Action Text",
					"iMail");

			if (RecActionText.equalsIgnoreCase(actRecActionText))
				onPassUpdate("iMail", RecActionText, actRecActionText, "Recommended Action Text", "");
			else
				onFailUpdate("iMail", RecActionText, actRecActionText, "Recommended Action Text", "");

			switchToWindow("storeParent");
			javaScriptToclickElement("iMail", "lnk_dataCapture;xpath", "Data Capture Link", "iMail");

			waitForSync(3);
			switchToWindow("child");
			String LufTitle = driver.getTitle();

			waitForSync(3);

			verifyScreenText("Lufthansa Login", data("lufthansaTitle"), LufTitle, "Lufthansa Login Title", "");

			enterValueInTextbox("iMail", "inbx_LufUsername;xpath", "UserName", "UserName", "iMailLogin");

			// driver.close();
			switchToWindow("getParent");
			javaScriptToclickElement("iMail", "lnk_clickHere;xpath", "Click here Link", "iMail");

			String expAWBTitle = data("expAWBTitle");
			String actAWBTitle = driver.getTitle();
			verifyScreenText(actAWBTitle, expAWBTitle, actAWBTitle, "AWB", "");

			driver.close();
			switchToWindow("getParent");
		}

		catch (Exception e) {
			// e.printStackTrace();
			System.out.println("Could not perform imail ops" + e);
			test.log(LogStatus.FAIL, "Could not perform imail ops");
		}
	}

	/**
	 * Description : Kill the processes
	 * 
	 * @param process:
	 *            Should specify the required process to be killed
	 * 
	 * @Sample format:(CMD)
	 * @author A-7271
	 */

	/**
	 * Description : Invoke the SOAP Suit
	 * 
	 * 
	 */
	public void triggerSOAPSuit(String project, String testSuit, String testCase)
			throws IOException, InterruptedException {

		try {
			String sOAPPath = getPropertyValue(proppath, "SoapPath");
			Runtime runtime = Runtime.getRuntime();

			runtime.exec("cmd /c start " + sOAPPath + " -s\"" + testSuit + "\" -c\"" + testCase
					+ "\" -r -A -a -j -S -f \"D:\\SoapUIResults\" \"" + projDir + "\\" + project + ".xml" + "\""
					+ " -P TCName=" + DriverSetup.testName);

			waitForSync(2);
		} catch (Exception e) {

			System.out.println("Could not trigger soap suit");
			test.log(LogStatus.FAIL, "Could not trigger soap suit");
		}
	}

	/**
	 * Description : Create the required date format and stores to propertyFile
	 * 
	 * @param dateFormat:
	 *            Should specify the required format
	 * @param value
	 *            : provide the value of day/month/year to be added or
	 *            substracted
	 * @param formats:
	 *            Specify DAY , MONTH or YEAR
	 * @param propsKey:Specify
	 *            the property file key whether the data to be saved
	 * @Sample format:(ddMMMYY,1,DAY,startDate)
	 * @author A-7271
	 */

	public enum format {
		CURRENT, DAY, MONTH, YEAR
	}

	public String createDateFormat(String dateFormat, int value, String formats, String propsKey) throws Exception {
		dateFormat=dateFormat.replaceAll("Y", "y");
		
		
		try {
			Date date = new Date();

			Calendar c = Calendar.getInstance();
			c.setTime(date);

			switch (format.valueOf(formats)) {

			case DAY:
				c.add(Calendar.DATE, value);
				break;

			case MONTH:
				c.add(Calendar.MONTH, value);
				break;

			case YEAR:
				c.add(Calendar.YEAR, value);
				break;

			case CURRENT:

				break;
			}

			date = c.getTime();

			DateFormat fmt = new SimpleDateFormat(dateFormat);
			String fromattedDate = fmt.format(date);

			if (!propsKey.equals("")) {
				setPropertyValue(propsKey, fromattedDate, proppath);
			}

			
			if(getPropertyValue(proppath, "isTimeZoneSet").equals("Yes"))
			{
				
				fromattedDate=createDateFormatWithTimeZone(dateFormat,value,formats);
				
				
			}
			System.out.println(fromattedDate);
			
			return fromattedDate;
			

		}

		catch (Exception e) {
			System.out.println("Could not create date format");
			test.log(LogStatus.FAIL, "Could not create date format");
			return "";
		}
	}
	
	public enum format2 {
		DAY, MONTH, YEAR
	}
	public String createDateFormatWithTimeZone(String dateFormat,int value, String formats)
	{
		String fromattedDate=null;
		try
		{
			 

			
				
			            Date date = new Date();
			        DateFormat fmt = new SimpleDateFormat(dateFormat);
					String fromattedDate2= fmt.format(date);
					System.out.println(fromattedDate2);
				
				// To TimeZone 
		        SimpleDateFormat sdfCountry= new SimpleDateFormat(dateFormat+" HH:mm");
		        TimeZone tzCountry = TimeZone.getTimeZone(getPropertyValue(proppath, "timeZoneStation"));
		        Calendar c = Calendar.getInstance();
				c.setTime(date);
				
		        switch (format2.valueOf(formats)) {

				 case DAY:
						c.add(Calendar.DATE, value);
						break;

					case MONTH:
						c.add(Calendar.MONTH, value);
						break;

					case YEAR:
						c.add(Calendar.YEAR, value);
						break;
						
			        
						
			        }
		       
		        date = c.getTime();
		        sdfCountry.setTimeZone(tzCountry);
		       System.out.println(sdfCountry.format(date)	);
			 fromattedDate = sdfCountry.format(date).split(" ")[0];
			System.out.println(fromattedDate);
			return fromattedDate;
		}
		catch(Exception e)
		{
			return fromattedDate;
		}
		
	}

	/**
	 * Description : Create the required date format with different locale and
	 * stores to propertyFile
	 * 
	 * @param dateFormat:
	 *            Should specify the required format
	 * @param value
	 *            : provide the value of day/month/year to be added or
	 *            substracted
	 * @param formats:
	 *            Specify DAY , MONTH or YEAR
	 * @param propsKey:Specify
	 *            the property file key whether the data to be saved
	 * @Sample format:(ddMMMYY,Germany,1,DAY,startDate)
	 * @author A-7271
	 */
	public enum format3 {
		CURRENT, DAY, MONTH, YEAR
	}

	public String createDateFormatWithLocale(String dateFormat, String locale, int value, String formats,
			String propsKey) throws Exception {

		try {
			Date date = new Date();

			Calendar c = Calendar.getInstance();
			c.setTime(date);

			switch (format3.valueOf(formats)) {

			case DAY:
				c.add(Calendar.DATE, value);
				break;

			case MONTH:
				c.add(Calendar.MONTH, value);
				break;

			case YEAR:
				c.add(Calendar.YEAR, value);
				break;

			case CURRENT:

				break;
			}

			date = c.getTime();
			String fromattedDate = "";
			DateFormat fmt = null;

			if(locale.equalsIgnoreCase("Germany"))
			{

			fmt = new SimpleDateFormat(dateFormat,Locale.GERMAN);
			fmt.setTimeZone(TimeZone.getTimeZone(locale));
			fromattedDate = fmt.format(date);
			}
			else if(locale.equalsIgnoreCase("English"))
			{

			fmt = new SimpleDateFormat(dateFormat,Locale.ENGLISH);
			fmt.setTimeZone(TimeZone.getTimeZone(locale));
			fromattedDate = fmt.format(date);
			}
			else
			{
				
			}

			if (!propsKey.equals("")) {
				setPropertyValue(propsKey, fromattedDate, proppath);
			}

			return fromattedDate;

		}

		catch (Exception e) {
			System.out.println("Could not create date format");
			test.log(LogStatus.FAIL, "Could not create date format");
			return "";
		}
	}

	/**
	 * Description... Clicks the certificate error which comes in Internet
	 * Explorer browser.
	 * 
	 * @throws Exception
	 */
	public void clickCertificateError() throws Exception {
        try {

              driver.findElement(By.linkText(xls_Read.getCellValue("Login", "lnk_certificateError;linkText"))).click();
               waitForSync(3);
              
        
        } catch (Exception e) {
               
         try{
               
      WebElement element= driver.findElement(By.linkText("More information"));
                JavascriptExecutor executor = (JavascriptExecutor) driver;
       executor.executeScript("arguments[0].click();", element);
                waitForSync(5);
                element= driver.findElement(By.linkText("Go on to the webpage (not recommended)"));
                executor.executeScript("arguments[0].click();", element);
                
      }catch(Exception e2){
       
                      System.out.println("Not clicked on the object: certificate Error");
                }
        }
          

        }
	
	/*
	 * Author : A-8468 Date Modified : 30/1/2019 Purpose : Method to
	 * encrypt characters of string
	 */
	public String encryptCharacters(String initialString, String encryptionChar,String encrytPart, String [] startIndex , String [] endIndex){

	 String str1 ="";
	 String str2 ="";
	 if(endIndex.length != 0){ 
	  str1 = initialString.substring(Integer.parseInt(startIndex[0]), Integer.parseInt(endIndex[0]));
	 }else{
	  str1 = initialString.substring(Integer.parseInt(startIndex[0]));
	 }

	if(startIndex.length>1)
	{
	 if(endIndex.length > 1){
	 str2 = initialString.substring(Integer.parseInt(startIndex[1]), Integer.parseInt(endIndex[1]));
	 }else{
	  
	  str2 = initialString.substring(Integer.parseInt(startIndex[1]));
	 }
	}

	int encryptLength = initialString.length() - str1.length() - str2.length();
	String encrypt ="";
	String finalString = "";

	for (int i= 0 ; i < encryptLength ; i++){ 
	 encrypt = encrypt + encryptionChar;
	 }

	switch(encrytPart){

	case "starting" :
	 finalString = encrypt + str1;
	 break;
	 
	 
	case "middle" :
	 finalString = str1 + encrypt + str2;
	 break;
	 
	case "ending" :
	 finalString = str1 + encrypt;
	 break;
	 
	 }

	return finalString;
	 
	}

	public void verifyeDGDStatus(String Capable_nonCapableLane) throws InterruptedException {
		 
		 String sheetName="Generic_Elements";
		 switch (Capable_nonCapableLane) {
		 
		 case "NonCapableLane":
		  String xpath = xls_Read.getCellValue(sheetName, "div_edgdNonCapableLane;xpath");
		  WebElement img = driver.findElement(By.xpath(xpath));
		  verifyElementDisplayed(img, "eDGD on Noncapable lane", "Capture AWB", "eDGD info");
		  break;
		  
		 case "CapableLane":
		  String xpath2 = xls_Read.getCellValue(sheetName, "div_edgdCapableLane;xpath");
		  WebElement img2 = driver.findElement(By.xpath(xpath2));
		  verifyElementDisplayed(img2, "eDGD on Capable lane", "Capture AWB", "eDGD info");
		  break;
		 
		 }
		 }
	public void verifyAWBlisted(String AWBno, String ShipmentPrefix, String screenName){
		 
		 String sheetName = "Generic_Elements";
		 
		 String actShipmentPrefix = getAttributeWebElement(sheetName, "inbx_shipmentPrefix;xpath", "Shipment Prefix", "value", screenName);
		 String actAWBno = getAttributeWebElement(sheetName, "inbx_AWBnumber;xpath", "AWB No", "value", screenName);
		 
		 verifyScreenText(screenName, ShipmentPrefix, actShipmentPrefix, "Shipment Prefix", "Verification of AWB listed");
		 verifyScreenText(screenName, AWBno, actAWBno, "AWB No", "Verification of AWB listed");
		 
		 waitForSync(4);
		 
		}


	/**** SET UP APPLICATION ****/
	public WiniumDriver setUpApplication(String applnPath) throws IOException {

		DesktopOptions options = new DesktopOptions();
		options.setApplicationPath(data(applnPath));

		String WiniumEXEpath = winium_driver_path;
		File file = new File(WiniumEXEpath);
		if (!file.exists()) {
			throw new IllegalArgumentException("The file " + WiniumEXEpath + " does not exist");
		}
		Runtime.getRuntime().exec(file.getAbsolutePath());
		try {
			windriver = new WiniumDriver(new URL("http://localhost:9999"), options);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		System.out.println("www" + windriver);
		return windriver;
	}
	

	/**
	 * Description... Searches the Screen with the screen ID
	 * 
	 * @param ScreenID
	 * @param ScreenName
	 * @throws InterruptedException
	 */

	public void searchScreen(String ScreenID, String ScreenName) throws InterruptedException {
		try {
			waitForSync(4);
			String sheetName = "Login";
			clickWebElement(sheetName, "inbx_searchScreen;xpath", "Screen Search Field", ScreenID);
			enterValueInTextbox(sheetName, "inbx_searchScreen;xpath", ScreenID,"Screen ID", ScreenName);
			waitForSync(1);
			String screenXpath = xls_Read.getCellValue("Generic_Elements", "lst_searchScreen;xpath").replace("ScreenID",
					ScreenID);

			driver.findElement(By.xpath(screenXpath)).click();

			driver.findElement(By.xpath(xls_Read.getCellValue("Login", "logo_verfLogin;xpath"))).click();
			waitForSync(2);
			driver.switchTo().frame("iCargoContentFrame");
			waitForWhiteScreen();
			waitForSync(2);
			switchToFrame("default");
			waitForSync(2);

			String frameName = "iCargoContentFrame" + ScreenID;
			driver.switchTo().frame(frameName);
			test.log(LogStatus.PASS, "Entered " + ScreenID + " and invoked " + ScreenName + " Screen");
			System.out.println("Entered " + ScreenID + " and invoked " + ScreenName + " Screen");
			
		}

		catch (Exception e) {
			System.out.println("Could not enter " + ScreenID + " and invoke " + ScreenName + " Screen");
			test.log(LogStatus.FAIL, "Could not enter " + ScreenID + " and invoke " + ScreenName + " Screen");
			Assert.assertFalse(true, "Could not enter " + ScreenID + " and invoke " + ScreenName + " Screen");

		}
	}
	
    /**
     * Description: Verify table records both text and dropdown value
     * @param sheetName
     * @param locator
     * @param pmKeyCol
     * @param pmKeyVal
     * @param colVal
     * @param colVal2
     * @param actVal
     * @param actVal2
     * @param ScreenName
     */
     
     public void verify_tbl_records_multiple_cols(String sheetName,String locator,String pmKeyCol,String pmKeyVal,int[] colVal,int[] colVal2,String[] actVal,String[] actVal2,String ScreenName)
     {
           
           
           String val="";
           int temp=0;
           String text="";
           String loc = xls_Read.getCellValue(sheetName, locator);           
           int len = driver.findElements(By.xpath(loc)).size();
           
           for(int i=1;i<=len;i++)
           {
                 try
                 {
                       String dynmlocator = "("+loc+")"+"["+i+"]"+"//td["+pmKeyCol+"]//select";
                       WebElement ele=driver.findElement(By.xpath(dynmlocator));
                       Select sel=new Select(ele);
                       text=sel.getFirstSelectedOption().getText();
                 }

                 catch(Exception e)
                 {

                 }


                 if(text.equals(pmKeyVal))
                 {
                       temp=i;
                       break;
                 }


           }

           /*************** VERIFICATION OF TEXT FIELD VALUES****/

           for(int j=0;j<colVal.length;j++)
           {
                 String dynXpath1 = "("+loc+")["+temp+"]//td["+colVal[j]+"]";
                 val=driver.findElement(By.xpath(dynXpath1)).getText();                  
                 if(val.equals(actVal[j]))
                 {
                       writeExtent("Pass", "Successfully verified " +actVal[j] + " value in " + ScreenName + " Page");                   
                 }
                 else
                 {
                       writeExtent("Fail", "Couldn't verify " +actVal[j] + " value in " + ScreenName + " Page");
                 }
           }


           /************* VERIFICATION OF DROP DOWNS****/

           for(int j=0;j<colVal2.length;j++)
           {
                 try
                 {
                       String dynXpath2 = "("+loc+")["+temp+"]//td["+colVal2[j]+"]//select";
                       WebElement ele=driver.findElement(By.xpath(dynXpath2));
                       Select sel=new Select(ele);
                       text=sel.getFirstSelectedOption().getText();
                 }

                 catch(Exception e)
                 {

                 }

                 if(text.equals(actVal2[j]))
                 {
                       writeExtent("Pass", "Successfully verified " +actVal2[j] + " value in " + ScreenName + " Page");                   
                 }
                 else
                 {
                       writeExtent("Fail", "Couldn't verify " +actVal2[j] + " value in " + ScreenName + " Page");
                 }
           }
     }

	/**
	 * Description... login to HHT
	 * 
	 * @param username
	 * @param password
	 * @throws printer
	 */
	public void loginHHT(String username, String password, String printer) throws InterruptedException {

		try {
			waitForSync(4);
			windriver.findElement(By.id("txtUserID")).sendKeys(username);
			waitForSync(4);
			windriver.findElement(By.id("txtPassword")).sendKeys(password);
			windriver.findElement(By.id("btnLogin")).click();
			waitForSync(8);
			windriver.findElement(By.id("txtPrinter")).sendKeys(data(printer));
			windriver.findElement(By.id("btnOk")).click();
			waitForSync(5);

			writeExtent("Pass", "Login to HHT is successful");
		}

		catch (Exception e) {
			writeExtent("Fail", "Login to HHT is not successful");
		}
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public void handleConnectivityPopUp() throws IOException
	{
		clickActionInHHT("btn_serverConnectivity;xpath",proppathsst,"Connectivity button","SST Login");
		waitForSync(8);
	}
	/**
	 * Description... login to HHT
	 * 
	 * @param username
	 * @param password
	 * @throws printer
	 */
	public void loginHHT(String username, String password) throws InterruptedException {

		try {
			
			enterValueInHHT("inbx_userName;accessibilityId",proppathhht,username,"Username","HHT Login");
			enterValueInHHT("inbx_password;accessibilityId",proppathhht,password,"Password","HHT Login");
			clickActionInHHT("btn_login;accessibilityId",proppathhht,"Login button","HHT Login");
			waitForSync(2);
			clickActionInHHT("btn_submitChanges;xpath",proppathhht,"Submit Changes button","HHT Login");
			waitForSync(10);

			writeExtent("Pass", "Logged in to to HHT");
		}

		catch (Exception e) {
			writeExtent("Fail", "Could not login to HHT");
		}
	}

	/**
	 * Description... login out HHT
	 * 
	 *
	 */
	public void logOutHHT() throws InterruptedException {

		waitForSync(2);
		windriver.quit();
		waitForSync(2);
	}

	/**
	 * Description... Web Login to outlook
	 * 
	 * @param UserName
	 * @param Password
	 * @throws Exception
	 */
	public void loginImail(String UserName, String Password) throws Exception {

		try {
			waitForSync(4);
			enterValueInTextbox("IMail", "inbx_username;name", UserName, "Username", "IMail");
			enterValueInTextbox("IMail", "inbx_password;name", Password, "Password", "IMail");
			clickWebElement("IMail", "btn_login;xpath", "Click Button", "IMail");
		} catch (Exception e) {
			System.out.println("Could not login in imail");
			test.log(LogStatus.FAIL, "Could not login in imail");

		}
	}

	/**
	 * Description... Login to iCargo
	 * 
	 * @param UserName
	 * @param Password
	 * @throws Exception
	 */
	public void loginICargo(String UserName, String Password) throws Exception {

		waitForSync(1);
		waitForLoad(driver);
		enterValueInTextbox("Login", "inbx_userName;xpath", UserName, "Username", "Login");
		enterValueInTextbox("Login", "inbx_password;xpath", Password, "Password", "Login");
		clickWebElement("Login", "btn_login;xpath", "Click Button", "Login");
		waitForLoad(driver);
		waitForSync(1);

		switchToWindow("child_BE");
		driver.switchTo().frame("iCargoContentFrame");
		waitForWhiteScreen();
		switchToFrame("default");
		try {
			waitTillOverlayDisappear(By.xpath(xls_Read.getCellValue("Login", "logo_verfLogin;xpath")));
			driver.findElement(By.xpath(xls_Read.getCellValue("Login", "logo_verfLogin;xpath")));

			onPassUpdate("Login", "iCargo Logo is Displayed", "Login is Successful", "Login",
					"1. Enter Username \n2. Enter Password \n3. Click Login Button");
			
		} catch (Exception e) {

			onFailUpdate("Login", "iCargo Logo is Displayed", "Login Failed", "Login",
					"1. Enter Username \n2. Enter Password \n3. Click Login Button");

		}

		/*
		 * driver.switchTo().frame("iCargoContentFrame"); waitForWhiteScreen();
		 * switchToFrame("default");
		 */
	}

	public void waitForWhiteScreen() {

		waitForSync(1);
		try {
			while (!driver.findElement(By.xpath("//body/div[@class='iCargoContent']")).isDisplayed()
					&& countWait <= 300) {
				System.out.println("Screen is up...");
				break;
			}
		} catch (Exception e) {
			countWait++;
			waitForWhiteScreen();

		}

	}

	public static void waitForLoad(WebDriver driver) {

		try {

			ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver driver) {
					return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
				}
			};
			WebDriverWait wait = new WebDriverWait(driver, 120);
			wait.until(pageLoadCondition);
		} catch (Exception e) {
			System.out.println("Could not login in icargo");
			test.log(LogStatus.FAIL, "Could not login in icargo");

		}

	}

	/*
	 * public void loginICargo(String UserName, String Password) throws
	 * Exception { waitForSync(3); clickCertificateError();
	 * enterValueInTextbox("Login", "inbx_userName;xpath", UserName, "Username",
	 * "Login"); enterValueInTextbox("Login", "inbx_password;xpath", Password,
	 * "Password", "Login"); clickWebElement("Login", "btn_login;xpath",
	 * "Click Button", "Login"); waitForSync(5); switchToAlert("Accept",
	 * "Login"); switchToWindow("child"); waitForSync(3); }
	 */
	/**
	 * Description... Login to MESWEB
	 * 
	 * @param UserName
	 * @param Password
	 *
	 */

	public void loginMESWEB(String username, String password) throws InterruptedException {

		try {
			String SheetName = "Mesx_Screen";
			waitForSync(3);
			enterValueInTextbox(SheetName, "inbx_userName;name", username, "Username", "Login");
			enterValueInTextbox(SheetName, "inbx_password;name", password, "Password", "Login");
			clickWebElement(SheetName, "Login_button;xpath", "Click Button", "Login");
			waitForSync(3);
		} catch (Exception e) {
			System.out.println("Could not login in Mesweb");
			test.log(LogStatus.FAIL, "Could not login in Mesweb");

		}

	}

	/**
	 * Description... Entering the Telexaddress in the MESWEB screen
	 * 
	 * @param Address
	 * 
	 *
	 */

	public void enterTelexAddress(String address) throws InterruptedException, IOException {

		try {
			String SheetName = "Mesx_Screen";
			waitForSync(3);
			clickWebElement(SheetName, "link_CPYC1LH;xpath", "CPYC1LH (3453)", "Mesx_Screen");
			waitForSync(3);
			clickWebElement(SheetName, "btn_writeNewMsg;xpath", "NEW", "Mesx_Screen");
			waitForSync(3);
			enterValueInTextbox(SheetName, "inbx_telexAddr;name", data(address), "Telex-Address", "Mesx_Screen");
		} catch (Exception e) {
			System.out.println("Could not enter Telex address in Mesex");
			test.log(LogStatus.FAIL, "Could not enter Telex address in Mesex");

		}
	}

	/**
	 * Description... Sending the message in the MESWEB screen
	 * @throws IOException 
	 * 
	 */
	public void sendMessage() throws InterruptedException, IOException {
		String SheetName = "Mesx_Screen";

		enterValueInTextbox(SheetName, "txtarea_msg;name", parameters.get("messageLine"), "Message", "Mesx_Screen");
		clickWebElement(SheetName, "btn_sendMsg;xpath", "Send Button", "Mesx_Screen");
		waitForSync(3);
		ele = findDynamicXpathElement("txt_sentMsgSuccess;xpath", SheetName, "Message sent", "Lufthansa Systems");
		try {
			String actText = ele.getText();

			String expText = " Message sent";

			verifyScreenText("Lufthansa Systems", actText, expText, " Message sent", " Message sent");

		} catch (Exception e) {
			test.log(LogStatus.FAIL, "Could not send message from MESWEB screen");
			System.out.println("Could not send message from MESWEB screen");

		}

	}

	/**
	 * Description... Create an AWB No depending on the stock_range_from in
	 * Global Variable properties file
	 * 
	 * @param AWBNo
	 */
	public String createAWB(String AWBNo) {

		String awbNumber = "";

		try {
			String propValue = "stock_range_from";

			// loading the property file
			String value = getPropertyValue(proppath, propValue);

			// loading the property file

			int val = Integer.parseInt(value);
			int modValue = val % 7;

			awbNumber = Integer.toString(val) + Integer.toString(modValue);

			if (awbNumber.length() < 8) {
				awbNumber = "0" + awbNumber;
			}
			String valToStore = Integer.toString(val + 1);

			if (valToStore.length() < 7) {
				valToStore = "0" + valToStore;
			}
			setPropertyValue(propValue, valToStore, proppath);
			setPropertyValue(AWBNo, awbNumber, proppath);
			

		}

		catch (Exception e) {
			test.log(LogStatus.FAIL, "Failed to create AWB");
			System.out.println("Failed to create AWB");
		}
		return awbNumber;

	}

	/**
	 * Description... List an AWB No on any Screen
	 * 
	 * @param awbNo
	 * @param ShipmentPrefix
	 * @param ScreenName
	 * @throws InterruptedException
	 * @throws IOException 
	 */
	public void listAWB(String awbNo, String ShipmentPrefix, String ScreenName) throws InterruptedException, IOException {

		String sheetName = "Generic_Elements";
		awbNo = getPropertyValue(proppath, awbNo);

		System.out.println("AWBnumber is ---" + awbNo);
		waitForSync(2);
		enterValueInTextbox(sheetName, "inbx_shipmentPrefix;xpath", data(ShipmentPrefix), "Shipment Prefix",
				ScreenName);
		enterValueInTextbox(sheetName, "inbx_AWBnumber;xpath", awbNo, "AWB No", ScreenName);
		clickWebElement(sheetName, "btn_List;xpath", "List Button", ScreenName);
		waitForSync(4);

	}

	public void listAWBPreadvice(String awbNo, String ShipmentPrefix, String ScreenName) throws InterruptedException, IOException {

		String sheetName = "Generic_Elements";

		enterValueInTextbox(sheetName, "inbx_shipmentPrefix;xpath", data(ShipmentPrefix), "Shipment Prefix",
				ScreenName);
		enterValueInTextbox(sheetName, "inbx_AWBnumber;xpath", awbNo, "AWB No", ScreenName);
		clickWebElement(sheetName, "btn_List;xpath", "List Button", ScreenName);
		waitForSync(4);

	}

	/**
	 * Description... Verifies the Screen Text and logs the result in the Extent
	 * Report
	 * 
	 * @param screenName
	 * @param expText
	 * @param actText
	 * @param functinalityName
	 * @param testSteps
	 */
	public void verifyScreenText(String screenName, String expText, String actText, String functinalityName,
			String testSteps) {

		    if (actText.trim().contains(expText.trim()))
			onPassUpdate(screenName, expText, actText, functinalityName, testSteps);
		else
			onFailUpdate(screenName, expText, actText, functinalityName, testSteps);
	}
/**
	 * Description... Verifies the Screen Text with exact match and logs the result in the Extent
	 * Report
	 * 
	 * @param screenName
	 * @param expText
	 * @param actText
	 * @param functinalityName
	 * @param testSteps
	 */
	public void verifyScreenTextWithExactMatch(String screenName, String expText, String actText, String functinalityName,
			String testSteps) {

		    if (actText.trim().equals(expText.trim()))
			onPassUpdate(screenName, expText, actText, functinalityName, testSteps);
		else
			onFailUpdate(screenName, expText, actText, functinalityName, testSteps);
	}
	public void verifyScreenTextNotExists(String screenName, String expText, String actText, String functinalityName,
			String testSteps) {

		if (!actText.contains(expText))
			onPassUpdate(screenName, expText, actText, functinalityName, testSteps);
		else
			onFailUpdate(screenName, expText, actText, functinalityName, testSteps);
	}

	/*
	 * Author: A-7271 Date Modified :30-05-2018
	 */
	public enum applications {
		iCargo, BE, MESX, iMail
	}

	public String[] getApplicationParams(String appln) {

		String[] params = new String[3];
		try {

			switch (applications.valueOf(appln)) {

			case iCargo:
				params[0] = getPropertyValue(globalVarPath, "iCargoURL");
				params[1] = getPropertyValue(globalVarPath, "iCargoUN");
				params[2] = getPropertyValue(globalVarPath, "iCargoPWD");
				return params;

			case BE:
				params[0] = getPropertyValue(globalVarPath, "BEURL");
				params[1] = getPropertyValue(globalVarPath, "BEUN");
				params[2] = getPropertyValue(globalVarPath, "BEPWD");
				return params;

			case MESX:
				params[0] = getPropertyValue(globalVarPath, "MESWEBURL");
				params[1] = getPropertyValue(globalVarPath, "MESWEBUN");
				params[2] = getPropertyValue(globalVarPath, "MESWEBPWD");
				return params;
			case iMail:
				params[0] = getPropertyValue(globalVarPath, "iMailURL");
				params[1] = getPropertyValue(globalVarPath, "iMailUN");
				params[2] = getPropertyValue(globalVarPath, "iMailPWD");
				return params;

			}
		} catch (Exception e) {
			test.log(LogStatus.FAIL, "Failed to get Application Params");
			System.out.println("Failed to get Application Params");
		}

		return params;

	}

	/**
	 * Description... Login to MBQ application
	 * 
	 * @param UserName
	 * @param Password
	 * @throws Exception
	 */
	public void loginMBQ(String UserName, String Password) throws Exception {

		try {
			waitForSync(4);
			waitForLoad(driver);
			enterValueInTextbox("Login", "inbx_userName;xpath", UserName, "Username", "Login");
			enterValueInTextbox("Login", "inbx_password;xpath", Password, "Password", "Login");
			clickWebElement("Login", "btn_login;xpath", "Click Button", "Login");
			waitForLoad(driver);
			waitForSync(1);
			onPassUpdate("Login", "MBQ Login successful", "Login is Successful", "Login",
					"1. Enter Username \n2. Enter Password \n3. Click Login Button");
		} catch (Exception e) {

			onFailUpdate("Login", "MBQ Login successful", "Login Failed", "Login",
					"1. Enter Username \n2. Enter Password \n3. Click Login Button");

		}

	}

	/********** TRIGGERING SOAP TEST CASE FROM SELENIUM *******/
	public boolean invokeSoapSuite(String testCaseId, String soapProjectPath) throws InterruptedException, IOException {

		try {
			Runtime runtime = Runtime.getRuntime();

			// invokes testRunner.bat command
			String testrunnerPath = "cmd /c start ".concat(testrunner_path).concat(" ");

			// appends -P argument for project custom properties, to get current
			// TC id which is running
			String testcaseName = " -P TCName=" + testCaseId;

			// Final command to be invoked
			String message = testrunnerPath.concat(data(soapProjectPath)).concat(testcaseName);

			runtime.exec(message);
			return true;

		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Description... To generate ULD no in format - AKE10569LH *
	 * 
	 * @return ULD Number
	 */
	// To generate ULD no in format - AKE10569LH
	public String create_uld_number(String uldtype, String FltNumStationCode) {

		String randStr = "";

		try {

			String randomNum_length = "5";
			int digit = Integer.parseInt(randomNum_length);
			long value1 = 1;
			long value2 = 9;

			for (int i = 1; i < digit; i++) {
				value1 = value1 * 10;
				value2 = value2 * 10;
			}

			Long randomlong = (long) (value1 + Math.random() * value2);

			randStr = randomlong.toString();

			randStr = data(uldtype) + randStr + data(FltNumStationCode);

			writeExtent("Pass", "ULD number is generated " + randStr);
			System.out.println("ULD number is generated " + randStr);

		}

		catch (Exception e) {
			System.out.println("ULD number could not be generated");
			test.log(LogStatus.FAIL, "ULD number could not be generated");

		}
		return randStr;
	}

	/**
	 * Description... Takes the message format from the Message Excel sheet,
	 * replaces all the parameters and stores the message in the text file named
	 * same as that of Excel sheet name
	 * @author A-7271
	 * @param MessageExcelAndSheet
	 * @param MessageParam
	 * @return
	 * @throws IOException
	 */
	public boolean createTextMessage(String MessageExcelAndSheet, String MessageParam) throws IOException {

		String messageLine = "";
		String messageType = "";
		String messageSubType = "";
		String values = "";
		try {

			// Excel name
			messageType = data(MessageExcelAndSheet).split(",")[0];

			// Sheet name
			messageSubType = data(MessageExcelAndSheet).split(",")[1];
			values = data(MessageParam);

			// creating the text file

			String filePath = createFile(messageSubType, ".txt");

			/**** EXCEL OPS ***/
			InputStream inp = null;
			XSSFWorkbook wb = null;

			int rowIndex = 0;

			Row row = null;

			String value = "";

			try {

				String path = message_format + messageType + ".xlsx";
				inp = new FileInputStream(path);
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			}
			try {
				wb = new XSSFWorkbook(inp);
			} catch (IOException e) {
				e.printStackTrace();
			}

			Sheet sheet = wb.getSheet(messageSubType);

			Iterator<Row> rows = sheet.rowIterator();
			while (rows.hasNext()) {
				row = rows.next();

				for (Cell cell : row) {
					row = sheet.getRow(rowIndex);
					cell = row.getCell(0, Row.CREATE_NULL_AS_BLANK);
					value = cell.toString();

					if (value.contains("<")) {

						String valuee = "";

						for (int j = 0; j < value.length(); j++) {
							String val = "";

							if (value.charAt(j) == '<') {
								while (value.charAt(j + 1) != '>') {
									val = val + value.charAt(j + 1);
									j = j + 1;

								}

								// Getting the awb value...

								for (int k = 0; k < values.split(",").length; k++) {
									if (values.split(",")[k].toString().contains(val)) {
										val = values.split(",")[k].toString().split("=")[1].toString();
										if (val.toLowerCase().contains("date"))
											val = data(val).toUpperCase();
										else
											val = data(val);
										break;

									}
								}

								valuee = valuee + val;

							}

							else {
								if (value.charAt(j) != '>') {
									valuee = valuee + value.charAt(j);

								}

							}

						}

						value = valuee;
					}

					frameMessage(filePath, value);
					rowIndex++;

				}
				messageLine = messageLine + value + "\n";
			}

			parameters.put("messageLine", messageLine);

			return true;
		}

		catch (Exception e) {
			System.out.println("Message is not created for Type : " + messageType);
			test.log(LogStatus.FAIL, "Message is not created for Type : " + messageType);
			// e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * @author A-7271
	 * @param fileName
	 * @param sccs
	 * Desc : Create XFWB With MultipleSCCs
	 */
	public void createXFWBMessageWithSCCs(String fileName,String[] sccs)
	{
		try
		{
			String path = message_files + fileName + ".xml";
			
			
	         String oldtext = "";
	         BufferedReader reader = new BufferedReader(new FileReader(path));
	         String line = "";
	         String newLine="";
	       
	         boolean msgUpdated=false;
	         while ((line = reader.readLine()) != null) {
	        	 

	        	 if (line.contains("<HandlingSPHInstructions>")) {

	        		 if(msgUpdated==false)
	        		 {

	        			 for(int i=0;i<sccs.length;i++)
	        			 {
	        				 newLine=newLine+"<HandlingSPHInstructions>"+System.getProperty("line.separator")
	        						 +"<DescriptionCode>"+sccs[i]+"</DescriptionCode>"+System.getProperty("line.separator")
	        						 +"</HandlingSPHInstructions>"+System.getProperty("line.separator");
	        				 
	        				
	        				 
	        			 }



	        			 line = newLine;
	        			 oldtext += line
	        					 + System.getProperty("line.separator");
	        			

	        			 msgUpdated=true;
	        		 }



	        	 }

	      		 

	      		  

	      		  else{
	      			  oldtext += line+System.getProperty("line.separator");;
	      		  }	

	      	
	      		
	         }
	         BufferedWriter erasor = new BufferedWriter(new FileWriter(path));
	         erasor.write(oldtext);
	         erasor.close();
	         reader.close();
	         
	         writeExtent("Pass","XFWB is created with Multiple SCCs "+sccs);
			
	}
	
			
		
		
		catch(Exception e)
		{
			   writeExtent("Fail","XFWB is not created with Multiple SCCs "+sccs);
		}
	}
	/**
	 * @author A-7271
	 * @param fileName
	 * @param sccs
	 * Desc : Create XFWB With MultipleSCCs
	 */
	public void createXFWBMutliLineShipment(String fileName,String[] sccs,String [] flightDetails,String[] commDetails)
	{
		try
		{
			String path = message_files + fileName + ".xml";
			
			
	         String oldtext = "";
	         BufferedReader reader = new BufferedReader(new FileReader(path));
	         String line = "";
	         String newLine="";
	       
	         boolean msgUpdated=false;
	         boolean msgUpdatedFlt=false;
	         boolean msgUpdateComm=false;
	         int counter=0;
	         
	         while ((line = reader.readLine()) != null) {
	        	 

	        	 //Updating the SCC Details
	        	 if (line.startsWith("<HandlingSPHInstructions>")) {

	        		 if(msgUpdated==false)
	        		 {

	        			 for(int i=0;i<sccs.length;i++)
	        			 {
	        				 newLine=newLine+"<HandlingSPHInstructions>"+System.getProperty("line.separator")
	        						 +"<DescriptionCode>"+sccs[i]+"</DescriptionCode>"+System.getProperty("line.separator")
	        						 +"</HandlingSPHInstructions>"+System.getProperty("line.separator");
	        						 
	        				 
	        				
	        				 
	        			 }



	        			 line = newLine;
	        			 oldtext += line
	        					 + System.getProperty("line.separator");
	        			

	        			 msgUpdated=true;
	        			 newLine="";
	        		 }



	        	 }

	      		 
	        	 else if(line.startsWith("<SpecifiedLogisticsTransportMovement>")) {
	        		 
	        		 
	        		 
	        		 if(msgUpdatedFlt==false)
	        		 {

	        			 for(int i=0;i<flightDetails.length;i++)
	        				 
	        			 {
	        				
	        				 newLine=newLine+"<SpecifiedLogisticsTransportMovement>"+System.getProperty("line.separator")
	        						 +"<StageCode>Main-Carriage</StageCode>"+System.getProperty("line.separator")
	        						 +"<ModeCode>4</ModeCode>"+System.getProperty("line.separator")
	        						 +"<Mode>AIR TRANSPORT</Mode>"+System.getProperty("line.separator")
	        						 +"<ID>"+flightDetails[i].split(";")[0]+"</ID>"+System.getProperty("line.separator")
	        						 +"<SequenceNumeric>"+(i+1)+"</SequenceNumeric>"+System.getProperty("line.separator")
	        						 +"<UsedLogisticsTransportMeans>"+System.getProperty("line.separator")
	        						 +"<Name>"+flightDetails[i].split(";")[0].substring(0,2)+"</Name>"+System.getProperty("line.separator")
	        						 +"</UsedLogisticsTransportMeans>"+System.getProperty("line.separator")
	        						 +"<ArrivalEvent>"+System.getProperty("line.separator")
	        						 +"<OccurrenceArrivalLocation>"+System.getProperty("line.separator")
	        						 +"<ID>"+flightDetails[i].split(";")[2]+"</ID>"+System.getProperty("line.separator")
	        						 +"<Name>CHARLES DE GAULLE AIRPORT</Name>"+System.getProperty("line.separator")
	        						 +"<TypeCode>Airport</TypeCode>"+System.getProperty("line.separator")
	        						 +"</OccurrenceArrivalLocation>"+System.getProperty("line.separator")
	        						 +"</ArrivalEvent>"+System.getProperty("line.separator")
	        						 +"<DepartureEvent>"+System.getProperty("line.separator")
	        						 +"<ScheduledOccurrenceDateTime>"+map.get("XFWBDate")+"T00:00:00</ScheduledOccurrenceDateTime>"+System.getProperty("line.separator")
	        						 +"<OccurrenceDepartureLocation>"+System.getProperty("line.separator")
	        						 +"<ID>"+flightDetails[i].split(";")[1]+"</ID>"+System.getProperty("line.separator")
	        						 +"<Name>SCHIPHOL AIRPORT</Name>"+System.getProperty("line.separator")
	        						 +"<TypeCode>Airport</TypeCode>"+System.getProperty("line.separator")
	        						 +"</OccurrenceDepartureLocation>"+System.getProperty("line.separator")
	        						 +"</DepartureEvent>"+System.getProperty("line.separator")
	        						 +"</SpecifiedLogisticsTransportMovement>"+System.getProperty("line.separator");
	        				 
	        			 }



	        			 line = newLine;
	        			 oldtext += line
	        					 + System.getProperty("line.separator");
	        			

	        			 msgUpdatedFlt=true;
	        			 newLine="";
	        		 }
	        	 }
	        	 else if(line.startsWith("<IncludedMasterConsignmentItem>")&&counter==0) {
	        		 
	        		 counter=counter+1;
	        		 oldtext += line+System.getProperty("line.separator");;
	        		 
	        	 }
                else if(line.startsWith("<IncludedMasterConsignmentItem>")&&counter==1) {
                	
                	String wtUnit="\"KGM\"";
                	String volUnit="\"MTQ\"";
	        		 
                	if(msgUpdateComm==false)
	        		 {

	        			 for(int i=0;i<commDetails.length;i++)
	        				 
	        			 {
	        				
	        				 newLine=newLine+"<IncludedMasterConsignmentItem>"+System.getProperty("line.separator")
	        						+"<SequenceNumeric>"+(i+2)+"</SequenceNumeric>"+System.getProperty("line.separator")
	        						+"<GrossWeightMeasure unitCode="+wtUnit+">"+commDetails[i].split(";")[1]+"</GrossWeightMeasure>"+System.getProperty("line.separator")
	        						+"<GrossVolumeMeasure unitCode="+volUnit+">"+commDetails[i].split(";")[2]+"</GrossVolumeMeasure>"+System.getProperty("line.separator")
	        						+"<PieceQuantity>"+commDetails[i].split(";")[0]+"</PieceQuantity>"+System.getProperty("line.separator")
	        						+"<Information>NDA</Information>"+System.getProperty("line.separator")
	        						+"<NatureIdentificationTransportCargo>"+System.getProperty("line.separator")
	        						+"<Identification>"+commDetails[i].split(";")[3]+"</Identification>"+System.getProperty("line.separator")
	        						+"</NatureIdentificationTransportCargo>"+System.getProperty("line.separator")
	        						+"<ApplicableFreightRateServiceCharge>"+System.getProperty("line.separator")
	        						+"<ChargeableWeightMeasure unitCode="+wtUnit+">0</ChargeableWeightMeasure>"+System.getProperty("line.separator")
	        						+"<AppliedRate>3</AppliedRate>"+System.getProperty("line.separator")
	        						+"<AppliedAmount>0</AppliedAmount>"+System.getProperty("line.separator")
	        						+"</ApplicableFreightRateServiceCharge>"+System.getProperty("line.separator")
	        						+"</IncludedMasterConsignmentItem>"+System.getProperty("line.separator");
	        						
	        						
	        			 }



	        			 line = newLine;
	        			 oldtext += line
	        					 + System.getProperty("line.separator");
	        			

	        			 msgUpdateComm=true;
	        			 newLine="";
	        		 }
	        		 
	        	 }
	      		  else{
	      			  oldtext += line+System.getProperty("line.separator");;
	      		  }	

	      	
	      		
	         }
	         BufferedWriter erasor = new BufferedWriter(new FileWriter(path));
	         erasor.write(oldtext);
	         erasor.close();
	         reader.close();
	         
	         writeExtent("Pass","XFWB is created with Multiple line shipments "+sccs);
			
	}
	
			
		
		
		catch(Exception e)
		{
			   writeExtent("Fail","XFWB is not created with Multiple line shipments "+sccs);
		}
	}
	/**
	 * Description... Takes the message format from the Message Excel sheet,
	 * replaces all the parameters and stores the message in the text file named
	 * same as that of Excel sheet name
	 * @author A-7271
	 * @param MessageExcelAndSheet
	 * @param MessageParam
	 * @return
	 * @throws IOException
	 */
	public boolean createXMLMessage(String MessageExcelAndSheet, String MessageParam) throws IOException {

		String messageLine = "";
		String messageType = "";
		String messageSubType = "";
		String values = "";
		try {

			// Excel name
			messageType = data(MessageExcelAndSheet).split(",")[0];

			// Sheet name
			messageSubType = data(MessageExcelAndSheet).split(",")[1];
			values = data(MessageParam);

			// creating the text file

			String filePath = createFile(messageSubType, ".xml");

			/**** EXCEL OPS ***/
			InputStream inp = null;
			XSSFWorkbook wb = null;

			int rowIndex = 0;

			Row row = null;

			String value = "";

			try {

				String path = message_format + messageType + ".xlsx";
				inp = new FileInputStream(path);
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			}
			try {
				wb = new XSSFWorkbook(inp);
			} catch (IOException e) {
				e.printStackTrace();
			}

			Sheet sheet = wb.getSheet(messageSubType);

			Iterator<Row> rows = sheet.rowIterator();
			while (rows.hasNext()) {
				row = rows.next();

				for (Cell cell : row) {
					row = sheet.getRow(rowIndex);
					cell = row.getCell(0, Row.CREATE_NULL_AS_BLANK);
					value = cell.toString();

					if (value.contains("[")) {

						String valuee = "";

						for (int j = 0; j < value.length(); j++) {
							String val = "";

							if (value.charAt(j) == '[') {
								while (value.charAt(j + 1) != ']') {
									val = val + value.charAt(j + 1);
									j = j + 1;

								}

								// Getting the awb value...

								for (int k = 0; k < values.split(",").length; k++) {
									if (values.split(",")[k].toString().contains(val)) {
										val = values.split(",")[k].toString().split("=")[1].toString();
										if (val.toLowerCase().contains("date"))
											val = data(val).toUpperCase();
										else
											val = data(val);
										break;

									}
								}

								valuee = valuee + val;

							}

							else {
								if (value.charAt(j) != ']') {
									valuee = valuee + value.charAt(j);

								}

							}

						}

						value = valuee;
					}

					frameMessage(filePath, value);
					rowIndex++;

				}
				messageLine = messageLine + value + "\n";
			}

			parameters.put("messageLine", messageLine);

			return true;
		}

		catch (Exception e) {
			System.out.println("Message is not created for Type : " + messageType);
			test.log(LogStatus.FAIL, "Message is not created for Type : " + messageType);
			// e.printStackTrace();
			return false;
		}
	}
	public String createFile(String messageType, String fileType) throws IOException {
		String filePath = message_files + messageType + fileType;
		File file = new File(filePath);

		if (file.exists()) {
			file.delete();
		}

		file.createNewFile();

		return filePath;
	}

	/**
	 * Description... Creates the message line by line
	 * @author A-7271
	 * @param filePath
	 * @param msg
	 */
	public void frameMessage(String filePath, String msg) {
		String fileName = filePath;
		PrintWriter printWriter = null;
		File file = new File(fileName);
		try {

			printWriter = new PrintWriter(new FileOutputStream(fileName, true));
			printWriter.write(msg + System.getProperty("line.separator"));
		} catch (IOException ioex) {
			ioex.printStackTrace();
		} finally {
			if (printWriter != null) {
				printWriter.flush();
				printWriter.close();
			}
		}
	}
	
	/**
	 * @author A-7271
	 * Desc : Click close tab
	 */
	public void closeTab()
	{
		switchToFrame("default");
		try
		{
			int size=driver.findElements(By.xpath("(//a[contains(@title,'Close Tab')])[2]")).size();

			if(size>=1)
			{
				driver.findElement(By.xpath("(//a[contains(@title,'Close Tab')])[2]")).click();
				waitForSync(2);

				try {
					String xpath = xls_Read.getCellValue("Generic_Elements", "btn_yes;xpath");
					driver.findElement(By.xpath(xpath)).click();
				} catch (Exception e) {


				}
			}
		}

		catch(Exception e)
		{

		}
	}

	/**
	 * Description... Switches to the default frame and Close any Screen Tab.
	 * 
	 * @param ScreenID
	 * @param ScreenName
	 * @throws InterruptedException
	 */
	/*
	 * Author : A-7688 Date Modified : 29/05/2018 Purpose : Close the Screen tab
	 * in iCapsit, creates xpath dynamically
	 */
	public void closeTab(String ScreenID, String ScreenName) throws InterruptedException {
		switchToFrame("default");
		String xpath = xls_Read.getCellValue("Generic_Elements", "btn_closeTab;xpath").replace("ScreenID", ScreenID);
		ele = findDynamicXpathElement(xpath, "Close TAB", ScreenID);
		javaScriptToclickElement(ele, "Close TAB", ScreenName);

		try {
			xpath = xls_Read.getCellValue("Generic_Elements", "btn_yes;xpath");
			driver.findElement(By.xpath(xpath)).click();
		} catch (Exception e) {
			System.out.println("Could not close tab on screen " + ScreenName);

		}

	}

	// overloaded method to click an element using javascript
	public void javaScriptToclickElement(WebElement ele, String elename, String ScreenName) {

		try {

			waitForSync(2);
			WebDriverWait wait = new WebDriverWait(driver, 60);
			wait.until(ExpectedConditions.visibilityOf(ele));
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", ele);
			writeExtent("Pass", "Clicked on " + elename + " On " + ScreenName + " Page");
			System.out.println("Clicked on " + elename + " On " + ScreenName + " Page");

		} catch (Exception e) {
			System.out.println("Could not click on element " + elename);
			writeExtent("Fail", "Could not click on " + elename + " On " + ScreenName + " Page");
			Assert.assertFalse(true, "Could not click on " + elename + " On " + ScreenName + " Page");
		}

	}

	/**
	 * Description... Switch to the required Station
	 * 
	 * @param Office
	 * @param RoleGroup
	 * @param stationCode
	 * @throws InterruptedException
	 * @throws AWTException
	 */

	public void switchRole(String stationCode, String Office, String RoleGroup)
			throws InterruptedException, AWTException {
		try {
			waitForSync(2);
			clickWebElement("SwitchRole", "btn_more;xpath", "More Button", "Switch Role");
			clickWebElement("SwitchRole", "btn_switchRole;xpath", "switch Role Button", "Switch Role");
			waitForSync(2);
			switchToFrame("frameLocator", "SwitchRole");
			waitForSync(4);
			selectValueInDropdown("SwitchRole", "lst_fromStation;name", data(stationCode), "StationCode",
					"VisibleText");
			keyPress("TAB");
			keyRelease("TAB");
			selectValueInDropdown("SwitchRole", "lst_office;name", data(Office), "Office", "VisibleText");
			selectValueInDropdown("SwitchRole", "lst_roleGroup;name", data(RoleGroup), "RoleGroup", "VisibleText");
			clickWebElement("SwitchRole", "btn_ok;xpath", "OK Button", "Switch Role");
			waitForSync(3);
			switchToFrame("default");
		} catch (Exception e) {
			System.out.println("Could not perform swicth role");
			test.log(LogStatus.FAIL, "Could not perform swicth role");

		}
	}
	
	/**
	 * @author A-7271
	 * @param csvFile
	 * @param flightDetails
	 * @throws IOException
	 * Desc : create CSV file
	 */
	public void createCSVFile(String csvFile,List<String> flightDetails) throws IOException
	{
		try
		{
         String fileContent="";
		
		File file2 = new File(jmeterFilePath+"csv\\"+csvFile+".csv");
		
		if(file2.exists())
		{
			file2.delete();
		}
		
		for(int i=0;i<flightDetails.size();i++)
		{
			
			fileContent=fileContent+flightDetails.get(i)+",";
		}
		fileContent=fileContent.substring(0,fileContent.length()-1);
		System.out.println(fileContent);
		BufferedWriter writer = new BufferedWriter(new FileWriter(jmeterFilePath+"csv\\"+csvFile+".txt"));
		writer.write(fileContent);
		writer.close();
		
		File file  = new File(jmeterFilePath+"csv\\"+csvFile+".txt"); // handler to your ZIP file
		 // destination dir of your file
		file.renameTo(file2);
		}
		
		catch(Exception e)
		{
			
		}
	}
	
	/**
	 * @author A-7271
	 * @param jmxFile
	 * @throws IOException
	 * @Desc : trigger JMX script
	 *
	 */
	public void triggerJMXScript(String jmxFile) throws IOException
	{
		try
		{
		        //Starting jmeter
				String path=getPropertyValue(proppath, "jmeterPath")+jmxFile+".bat";
				Runtime runtime = Runtime.getRuntime();
				
				runtime.exec("cmd /c start "+ path);
		}
		
		catch(Exception e)
		{
			
		}
				
	}
	
	/**
	 * @author A-7271
	 * @param expResponse
	 * @param responseFile
	 * @param verifyType
	 * DEsc : verify the response of JMX trigger
	 */
	public void verifyResponseOfJMXTrigger(String expResponse,String responseFile,String verifyType)
	{

		try {
		      File myObj = new File(jmeterFilePath+"response\\"+responseFile+".txt");
		      Scanner myReader = new Scanner(myObj);
		      while (myReader.hasNextLine()) {
		        String actResponse = myReader.nextLine();
		        
		        if(verifyType.equals("equals"))
		        {
		        	if(actResponse.equals(data(expResponse)))
		        	{
		        		writeExtent("Pass","JMX Response matches . Response is : "+actResponse);
		        	}
		        	else
		        	{
		        		writeExtent("Fail","Mismatch in JMX response Actual response : "+actResponse+" Expected response : "+data(expResponse));
		        	}
		        }
		        
		        else if(verifyType.equals("contains"))
		        {
		        	if(actResponse.contains(data(expResponse)))
		        	{
		        		writeExtent("Pass","JMX Response matches . Response is : "+actResponse);
		        	}
		        	else
		        	{
		        		writeExtent("Fail","Mismatch in JMX response Actual response : "+actResponse+" Expected response : "+data(expResponse));
		        	}
		        }
		      }
		      myReader.close();
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred while verification of JMX reponse");
		      writeExtent("Fail","Error occurred while processing JMX response");
		      e.printStackTrace();
		
		    }
	}

	/**
	 * Description... List Flight
	 * 
	 * @param ScreenID
	 * @throws InterruptedException
	 * @throws IOException 
	 */

	public void listFlight(String ScreenID, String carrierCode, String flightNumber, String flightDate,
			String sheetName) throws InterruptedException, AWTException, IOException {
		try {
			enterValueInTextbox("Generic_Elements", "inbx_carrierCode;xpath", carrierCode, "Carrier Code", ScreenID);
			enterValueInTextbox(sheetName, "inbx_flightNumber;xpath", flightNumber, "Flight Number", ScreenID);
			enterValueInTextbox("Generic_Elements", "inbx_flightDate;xpath", flightDate, "Flight Date", ScreenID);
			waitForSync(2);
			keyPress("TAB");
			keyRelease("TAB");
			waitForSync(3);
			clickWebElement("Generic_Elements", "btn_List;xpath", "List Button", ScreenID);
			waitForSync(5);
		} catch (Exception e) {
			System.out.println("Could not perform list flight operations");
			test.log(LogStatus.FAIL, "Could not perform list flight operations");

		}
	}

	/**
	 * Description... Verfies any number of column data in a table with
	 * contains() method
	 * 
	 * @param sheetName
	 *            xpath sheet name
	 * @param locator
	 *            xpath locator
	 * @param tableTag
	 *            tagname of the element whose value is to be verified
	 * @param verfCols
	 *            array of column numbers
	 * @param pmyKey
	 *            unique number for selecting a row
	 * @param actVerfValues
	 *            values to be verified
	 */

	public void verify_tbl_records_multiple_cols_contains(String sheetName, String locator, String tableTag,
			int verfCols[], String pmyKey, String actVerfValues[], String verify) {

		try {
			boolean flag = false;
			int row = 0;
			String ScreenName = sheetName.split("_")[0];
			// get the required row
			String tableBody = xls_Read.getCellValue(sheetName, locator);
			List<WebElement> rows = driver.findElements(By.xpath(xls_Read.getCellValue(sheetName, locator)));
			String dynXpath = xls_Read.getCellValue(sheetName, locator) + tableTag;

			System.out.println("row size  " + rows.size());
			switch (tableTag) {
			case "//input": {
				for (int i = 0; i < rows.size(); i++) {

					List<WebElement> cols = driver.findElements(By.xpath(dynXpath));

					for (int j = 0; j < cols.size(); j++) {

						System.out.println("col text = " + cols.get(j).getAttribute("value"));
						if (cols.get(j).getAttribute("value").contains(pmyKey)) {
							flag = true;
							break;

						}
					}
					if (flag) {
						row = i + 1;
						break;
					}
				}
				for (int i = 0; i < verfCols.length; i++) {
					dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
					WebElement ele = null;
					dynXpath = "(" + tableBody + ")[" + row + "]" + "//td[" + verfCols[i] + "]//input";
					ele = driver.findElement(By.xpath(dynXpath));

					String actual = ele.getText().toLowerCase().replace(" ", "");
					String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
					if (actual.contains(expected)) {
						System.out.println("found true for " + actVerfValues[i]);

						onPassUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					} else {
						onFailUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					}
				}
			}
				break;
			case "//td":

			{

				rows = driver.findElements(By.xpath(tableBody));
				dynXpath = tableBody + tableTag;
				{
					for (int i = 0; i <= rows.size(); i++) {
						System.out.println("i= " + i);

						if (rows.get(i).getText().toLowerCase().replace(" ", "")
								.contains(pmyKey.toLowerCase().replace(" ", ""))) {

							row = i + 1;

							System.out.println("row = " + row);
							for (int j = 0; j < verfCols.length; j++) {

								dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag + "[" + verfCols[j] + "]";
								WebElement ele = null;

								ele = driver.findElement(By.xpath(dynXpath));
								if (!ele.getText().contains(verify)) {
									break;
								} else {
									flag = true;

									String actual = ele.getText().toLowerCase().replace(" ", "");
									String expected = (actVerfValues[j].replace(" ", "").toLowerCase());
									if (actual.contains(expected)) {
										System.out.println("found true for " + actVerfValues[j]);

										onPassUpdate(ScreenName, expected, actual,
												"Table verification against " + pmyKey + " On ", "Table verification");

									} else {
										onFailUpdate(ScreenName, expected, actual,
												"Table verification against " + pmyKey + " On ", "Table verification");

									}
								}
							}

						}

						if (flag) {
							break;
						}
					}

				}
			}
				break;
			case "//div":

			{
				for (int i = 0; i <= rows.size(); i++) {
					System.out.println("i= " + i);

					if (rows.get(i).getText().toLowerCase().replace(" ", "")
							.contains(pmyKey.toLowerCase().replace(" ", ""))) {

						flag = true;

					}

					if (flag) {
						row = i + 1;
						break;
					}
				}
			}
				System.out.println("row = " + row);
				for (int i = 0; i < verfCols.length; i++) {

					dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
					WebElement ele = null;

					ele = driver.findElement(By.xpath(dynXpath));

					String actual = ele.getText().toLowerCase().replace(" ", "");
					String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
					if (actual.contains(expected)) {
						System.out.println("inside true condition");
						System.out.println("found true for " + actVerfValues[i]);

						onPassUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					} else {
						onFailUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					}

					test.log(LogStatus.FAIL, "Could not perform table record verification");
					System.out.println("Table contents are not verified or verification failed");
					Assert.assertFalse(true, "Could not perform table record verification");
					break;
				}
			}

		} catch (Exception e) {

		}

	}

	/**
	 * Description... Verfies any number of column data in a table with
	 * contains() method
	 * 
	 * @param sheetName
	 *            xpath sheet name
	 * @param locator
	 *            xpath locator
	 * @param tableTag
	 *            tagname of the element whose value is to be verified
	 * @param verfCols
	 *            array of column numbers
	 * @param pmyKey
	 *            unique number for selecting a row
	 * @param actVerfValues
	 *            values to be verified
	 */

	/**
	 * Description... Verfies any number of column data in a table
	 * 
	 * @param sheetName
	 *            xpath sheet name
	 * @param locator
	 *            xpath locator
	 * @param tableTag
	 *            tagname of the element whose value is to be verified
	 * @param verfCols
	 *            array of column numbers
	 * @param pmyKey
	 *            unique number for selecting a row
	 * @param actVerfValues
	 *            values to be verified
	 * @throws IOException 
	 */

	public void verify_tbl_records_multiple_cols(String sheetName, String locator, String tableTag, int verfCols[],
			String pmyKey, String actVerfValues[]) throws IOException {
		try {
			boolean flag = false;
			int row = 0;
			String ScreenName = sheetName.split("_")[0];
			// get the required row
			String tableBody = xls_Read.getCellValue(sheetName, locator);
			List<WebElement> rows = driver.findElements(By.xpath(xls_Read.getCellValue(sheetName, locator)));
			String dynXpath = xls_Read.getCellValue(sheetName, locator) + tableTag;

			System.out.println("row size  " + rows.size());
			switch (tableTag) {
			case "//input": {
				for (int i = 0; i < rows.size(); i++) {

					List<WebElement> cols = driver.findElements(By.xpath(dynXpath));

					for (int j = 0; j < cols.size(); j++) {

						System.out.println("col text = " + cols.get(j).getAttribute("value"));
						if (cols.get(j).getAttribute("value").contains(pmyKey)) {
							flag = true;
							break;

						}
					}
					if (flag) {
						row = i + 1;
						break;
					}
				}
				for (int i = 0; i < verfCols.length; i++) {
					dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
					WebElement ele = null;
					dynXpath = "(" + tableBody + ")[" + row + "]" + "//td[" + verfCols[i] + "]//input";
					ele = driver.findElement(By.xpath(dynXpath));

					String actual = ele.getText().toLowerCase().replace(" ", "");
					String a1 = ele.getAttribute("value");
					if (actual.length() == 0)
						actual = a1.toLowerCase();
					String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
					if (actual.equals(expected)) {
						System.out.println("found true for " + actVerfValues[i]);

						onPassUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					} else {
						captureScreenShot("Web");
						onFailUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					}
				}
			}
				break;
			case "//td":

			{

				rows = driver.findElements(By.xpath(tableBody));
				dynXpath = tableBody + tableTag;
				{
					System.out.println(pmyKey);
					for (int i = 0; i <= rows.size(); i++) {
						System.out.println("i= " + i);
						System.out.println(rows.get(i).getText().toLowerCase());

						if (rows.get(i).getText().toLowerCase().replace(" ", "")
								.contains(pmyKey.toLowerCase().replace(" ", ""))) {

							flag = true;

						}

						if (flag) {
							row = i + 1;
							break;
						}
					}

					System.out.println("row = " + row);
					for (int i = 0; i < verfCols.length; i++) {

						dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
						WebElement ele = null;

						ele = driver.findElement(By.xpath(dynXpath));

						String actual = ele.getText().toLowerCase().replace(" ", "");
						System.out.println(actual);
						String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
						System.out.println(expected);
						if (expected.contains(actual)) {
							System.out.println("found true for " + actVerfValues[i]);

							onPassUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
									"Table verification");

						} else {
							captureScreenShot("Web");
							onFailUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
									"Table verification");

						}

					}

				}
			}
				break;

			case "//div":

			{
				for (int i = 0; i <= rows.size(); i++) {
					System.out.println("i= " + i);

					if (rows.get(i).getText().toLowerCase().replace(" ", "")
							.contains(pmyKey.toLowerCase().replace(" ", ""))) {

						flag = true;

					}

					if (flag) {
						row = i + 1;
						break;
					}
				}
			}
				System.out.println("row = " + row);
				for (int i = 0; i < verfCols.length; i++) {

					dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
					WebElement ele = null;

					ele = driver.findElement(By.xpath(dynXpath));

					String actual = ele.getText().toLowerCase().replace(" ", "");
					String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
					if (actual.equals(expected)) {
						System.out.println("found true for " + actVerfValues[i]);

						onPassUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					} else {
						captureScreenShot("Web");
						onFailUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					}

					break;
				}
			case "input": {

				String Xpath = xls_Read.getCellValue(sheetName, locator);
				List<WebElement> rows1 = driver.findElements(By.xpath(Xpath));
				for (int i = 0; i < rows1.size(); i++) {

					String dynxpath = "(" + Xpath + ")[" + (i + 1) + "]//input";
					List<WebElement> cols = driver.findElements(By.xpath(dynxpath));

					for (int j = 0; j < cols.size(); j++) {

						System.out.println("col text = " + cols.get(j).getAttribute("value"));
						if (cols.get(j).getAttribute("value").contains(pmyKey)) {
							flag = true;
							break;
						}
					}
					if (flag) {
						row = i + 1;
						break;
					}
				}
				for (int i = 0; i < verfCols.length; i++) {
					dynXpath = "(" + tableBody + ")[" + row + "]//" + tableTag + "[" + verfCols[i] + "]";
					WebElement ele = null;
					dynXpath = "(" + tableBody + ")[" + row + "]" + "//td[" + verfCols[i] + "]//input";
					ele = driver.findElement(By.xpath(dynXpath));

					String actual = ele.getText().toLowerCase().replace(" ", "");
					String a1 = ele.getAttribute("value");
					if (actual.length() == 0)
						actual = a1.toLowerCase();
					String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
					if (actual.equals(expected)) {
						System.out.println("found true for " + actVerfValues[i]);

						onPassUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					} else {
						captureScreenShot("Web");
						onFailUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					}
				}
			}
				break;
			case "//label": {

				String Xpath = xls_Read.getCellValue(sheetName, locator);
				List<WebElement> rows1 = driver.findElements(By.xpath(Xpath));
				for (int i = 0; i < rows1.size(); i++) {

					String dynxpath = "(" + Xpath + "[" + (i + 1) + "])//label";
					List<WebElement> cols = driver.findElements(By.xpath(dynxpath));

					for (int j = 0; j < cols.size(); j++) {

						System.out.println("col text = " + cols.get(j).getText());
						if (cols.get(j).getText().contains(pmyKey)) {
							flag = true;
							break;
						}
					}
					if (flag) {
						row = i + 1;
						break;
					}
				}
				for (int i = 0; i < verfCols.length; i++) {
					dynXpath = tableBody + "[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
					WebElement ele = null;
					dynXpath = tableBody + "[" + row + "]" + "//td[" + verfCols[i] + "]//label";
					ele = driver.findElement(By.xpath(dynXpath));

					String actual = ele.getText().toLowerCase().replace(" ", "");
					String a1 = ele.getText();
					if (actual.length() == 0)
						actual = a1.toLowerCase();
					String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
					if (actual.equals(expected)) {
						System.out.println("found true for " + actVerfValues[i]);

						onPassUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					} else {
						captureScreenShot("Web");
						onFailUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					}
				}
			}
				break;

			}

		} catch (Exception e) {
			retryCount = retryCount + 1;

			if (retryCount <= 3) {
				verify_tbl_records_multiple_cols(sheetName, locator, tableTag, verfCols, pmyKey, actVerfValues);
			}

			else {
				captureScreenShot("Web");
				test.log(LogStatus.FAIL, "Could not perform table record verification");
				test.log(LogStatus.INFO, test.addScreenCapture(getPropertyValue(globalVarPath,"screenShotPath")));
				System.out.println("Table contents are not verified or verification failed");
				if(!customFunction.getPropertyValue(globalVarPath, "isClubbedTC").equals("Yes"))
				{
				Assert.assertFalse(true, "Could not perform table record verification");
				}
			}

		}
	}
	
	/**
	 * Description... Verfies any number of column data in a table
	 * 
	 * @param sheetName
	 *            xpath sheet name
	 * @param locator
	 *            xpath locator
	 * @param tableTag
	 *            tagname of the element whose value is to be verified
	 * @param verfCols
	 *            array of column numbers
	 * @param pmyKey
	 *            unique number for selecting a row
	 * @param actVerfValues
	 *            values to be verified
	 * @throws IOException 
	 */

	public ArrayList<String> retrieve_tbl_records_multiple_cols(String sheetName, String locator, String tableTag, int verfCols[],
			String pmyKey) throws IOException {
		
		ArrayList<String>tblValues=new ArrayList<String>();
		try {
			boolean flag = false;
			int row = 0;
			String ScreenName = sheetName.split("_")[0];
			// get the required row
			String tableBody = xls_Read.getCellValue(sheetName, locator);
			List<WebElement> rows = driver.findElements(By.xpath(xls_Read.getCellValue(sheetName, locator)));
			String dynXpath = xls_Read.getCellValue(sheetName, locator) + tableTag;

			System.out.println("row size  " + rows.size());
			switch (tableTag) {
			case "//input": {
				for (int i = 0; i < rows.size(); i++) {

					List<WebElement> cols = driver.findElements(By.xpath(dynXpath));

					for (int j = 0; j < cols.size(); j++) {

						System.out.println("col text = " + cols.get(j).getAttribute("value"));
						if (cols.get(j).getAttribute("value").contains(pmyKey)) {
							flag = true;
							break;

						}
					}
					if (flag) {
						row = i + 1;
						break;
					}
				}
				for (int i = 0; i < verfCols.length; i++) {
					dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
					WebElement ele = null;
					dynXpath = "(" + tableBody + ")[" + row + "]" + "//td[" + verfCols[i] + "]//input";
					ele = driver.findElement(By.xpath(dynXpath));

					String actual = ele.getText();
					
                       tblValues.add(actual);
						writeExtent("Pass","Table value fetched is "+actual);
						

					
				}
			}
				return tblValues;
			case "//td":

			{

				rows = driver.findElements(By.xpath(tableBody));
				dynXpath = tableBody + tableTag;
				{
					System.out.println(pmyKey);
					for (int i = 0; i <= rows.size(); i++) {
						System.out.println("i= " + i);
						System.out.println(rows.get(i).getText().toLowerCase());

						if (rows.get(i).getText().toLowerCase().replace(" ", "")
								.contains(pmyKey.toLowerCase().replace(" ", ""))) {

							flag = true;

						}

						if (flag) {
							row = i + 1;
							break;
						}
					}

					System.out.println("row = " + row);
					for (int i = 0; i < verfCols.length; i++) {

						dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
						WebElement ele = null;

						ele = driver.findElement(By.xpath(dynXpath));

						String actual = ele.getText();
						System.out.println(actual);
						 tblValues.add(actual);
							writeExtent("Pass","Table value fetched is "+actual);

						

					}

				}
			}
			return tblValues;

			
		} }catch (Exception e) {
			
			
				test.log(LogStatus.FAIL, "Could not fetch value from the table");
				
				return tblValues;
				
			}
		return tblValues;

		
	}
	/**
	 * @author A-7271
	 * @param sheetName
	 * @param locator
	 * @param tableTag
	 * @param verfCols
	 * @param pmyKey
	 * @param actVerfValues
	 * DEscription : select a particular record based on the table values
	 */
	public void verify_tbl_records_multiple_cols_and_select(String sheetName, String locator, String tableTag, int verfCols[],
			String pmyKey, String actVerfValues[]) {
		try {
			boolean flag = false;
			int row = 0;
			String ScreenName = sheetName.split("_")[0];
			// get the required row
			String tableBody = xls_Read.getCellValue(sheetName, locator);
			List<WebElement> rows = driver.findElements(By.xpath(xls_Read.getCellValue(sheetName, locator)));
			String dynXpath = xls_Read.getCellValue(sheetName, locator) + tableTag;

			System.out.println("row size  " + rows.size());
			switch (tableTag) {
			
			case "//td":

			{

				rows = driver.findElements(By.xpath(tableBody));
				dynXpath = tableBody + tableTag;
				{
					for (int i = 0; i <= rows.size(); i++) {
						System.out.println("i= " + i);
						System.out.println(rows.get(i).getText().toLowerCase());

						if (rows.get(i).getText().toLowerCase().replace(" ", "")
								.contains(pmyKey.toLowerCase().replace(" ", ""))) {

							flag = true;

						}

						if (flag) {
							row = i + 1;
							break;
						}
					}

					System.out.println("row = " + row);
					for (int i = 0; i < verfCols.length; i++) {

						dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
						WebElement ele = null;

						ele = driver.findElement(By.xpath(dynXpath));

						String actual = ele.getText().toLowerCase().replace(" ", "");
						String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
						if (expected.contains(actual)) {
							System.out.println("found true for " + actVerfValues[i]);

							onPassUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
									"Table verification");
							driver.findElement(By.xpath(dynXpath)).click();
							break;

						} else {
							onFailUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
									"Table verification");

						}

					}

				}
			}
				break;

			

			}

		} catch (Exception e) {
			

		}
	}
	public void verify_tbl_records_multiple_cols_RampHandle(String sheetName, String locator, int verfCols[],
			String pmyKey, String actVerfValues[]) {
		try {
			boolean flag = false;
			int row = 0;
			String ScreenName = sheetName.split("_")[0];
			// get the required row
			String tableBody = xls_Read.getCellValue(sheetName, locator);
			List<WebElement> rows = driver.findElements(By.xpath(tableBody));
			String dynXpath = tableBody + "//td";
			{
				for (int i = 0; i <= rows.size(); i++) {
					System.out.println("i= " + i);

					if (rows.get(i).getText().toLowerCase().replace(" ", "")
							.contains(pmyKey.toLowerCase().replace(" ", ""))) {

						flag = true;

					}

					if (flag) {
						row = i + 1;
						break;
					}
				}
				for (int i = 0; i < verfCols.length; i++) {

					dynXpath = "(" + tableBody + ")[" + row + "]" + "//td[" + verfCols[i] + "]//input";
					ele = driver.findElement(By.xpath(dynXpath));

					String actual = ele.getAttribute("value").toLowerCase().replace(" ", "");
					Thread.sleep(2000);
					String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
					if (actual.equals(expected)) {
						System.out.println("found true for " + actVerfValues[i]);

						onPassUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					} else {
						onFailUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					}

				}
			}
		} catch (Exception e) {

			test.log(LogStatus.FAIL, "Could not perform table record verification");
			System.out.println("Table contents are not verified or verification failed");
			Assert.assertFalse(true, "Could not perform table record verification");

		}
	}

	public void verify_tbl_records_multiple_cols_Picklist(String sheetName, String locator, String tableTag,
			int verfCols[], String pmyKey, String actVerfValues[]) {
		try {
			boolean flag = false;
			int row = 0;
			String ScreenName = sheetName.split("_")[0];
			// get the required row
			String tableBody = xls_Read.getCellValue(sheetName, locator);
			String dynXpath = tableBody;
			List<WebElement> rows = driver.findElements(By.xpath(dynXpath));

			{
				for (int i = 0; i <= rows.size(); i++) {
					System.out.println("i= " + i);

					if (rows.get(i).getAttribute("value").toLowerCase().replace(" ", "")
							.contains(pmyKey.toLowerCase().replace(" ", ""))) {

						flag = true;

					}

					if (flag) {
						row = i + 1;
						break;
					}
				}

				System.out.println("row = " + row);
				for (int i = 0; i < verfCols.length; i++) {

					dynXpath = "((" + tableBody + ")[" + row + "]/../.." + tableTag + ")[" + verfCols[i] + "]";
					WebElement ele = null;

					ele = driver.findElement(By.xpath(dynXpath));
					String actual = ele.getAttribute("value").toLowerCase().replace(" ", "");
					String expected = actVerfValues[i].replace(" ", "").toLowerCase();
					if (actual.equals(expected)) {
						System.out.println("found true for " + actVerfValues[i]);

						onPassUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					} else {
						onFailUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					}
				}
			}
		}

		catch (Exception e) {

			System.out.println("Could not verify table data");

		}
	}

	/**
	 * Description... defines flight type for createFlight method
	 * 
	 * @author A-7688
	 *
	 */
	public enum flightTypes {
		FullFlightNumber, FlightNumber, FlightNo, FlightNo2,FullFlightNumber2
	}

	/**
	 * Description... Creates the new flight
	 * 
	 * @param flightType
	 * @throws InterruptedException
	 */
	public void createFlight(String flightType) throws InterruptedException {

		try {
			String propValue = "flight_range_from";
			// loading the property file
			String value = getPropertyValue(proppath, propValue);
			String flightCode = getPropertyValue(proppath, "flight_code");
			String valToStore = "";
			int val = Integer.parseInt(value) + 1;
			Random r = new Random();
			String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			Character base = alphabet.charAt(r.nextInt(alphabet.length()));

			valToStore = Integer.valueOf(val).toString();
			switch (flightTypes.valueOf(flightType)) {

			case FullFlightNumber: {
				//value = flightCode + value + Character.toString(base);
				setPropertyValue("flightNo", value, proppath);
				value = flightCode + value;
				setPropertyValue("flightNumber", value, proppath);
				
				break;

			}
			case FullFlightNumber2: {
				//value = flightCode + value + Character.toString(base);
				 setPropertyValue("flightNo2", value, proppath);
				value = flightCode + value ;
				setPropertyValue("flightNumber2", value, proppath);
				break;
			}
             case FlightNo: {
				//value =  value + Character.toString(base);
            
				setPropertyValue("flightNo", value, proppath);
				break;
			}
             case FlightNo2: {
                 //value =  value + Character.toString(base);
       
                  setPropertyValue("flightNo2", value, proppath);
                 break;
           }

			default:
				break;

			}
			valToStore = Integer.valueOf(Integer.parseInt(valToStore) + 1).toString();
			setPropertyValue(propValue, valToStore, proppath);
		}

		catch (Exception e) {
			test.log(LogStatus.FAIL, "Could not create Flight");
			System.out.println("Could not create Flight");

		}

	}

	/**
	 * Description... Handles an alert with options getText/Accept/Dismiss/Close
	 * 
	 * @param alertOps
	 * @param ScreenName
	 */
	public boolean handleAlert(String alertOps, String ScreenName) {
		switchToFrame("default");
		String AlertText = "";

		try {
			AlertText = driver.findElement(By.xpath(xls_Read.getCellValue("Generic_Elements", "txt_AlertText;xpath")))
					.getText();
			if (!AlertText.equals("")) {
				switch (alertOps.valueOf(alertOps)) {
				case "getText":
					setPropertyValue("AlertText", AlertText, proppath);
					break;

				case "Accept":
					driver.findElement(By.xpath(xls_Read.getCellValue("Generic_Elements", "btn_yes;xpath"))).click();
					writeExtent("Pass", "Accepted Alert with text " + AlertText + " on " + ScreenName + " Screen");

					break;
				case "Dismiss":
					driver.findElement(By.xpath(xls_Read.getCellValue("Generic_Elements", "btn_no;xpath"))).click();
					writeExtent("Pass", "Dismissed Alert with text " + AlertText + " on " + ScreenName + " Screen");
					break;
				case "Close":
					driver.findElement(By.xpath(xls_Read.getCellValue("Generic_Elements", "btn_close;xpath"))).click();
					writeExtent("Pass", "Closed Alert with text " + AlertText + " on " + ScreenName + " Screen");
					break;
				case "GetTextAndClose":
					setPropertyValue("AlertText", AlertText, proppath);
				driver.findElement(By.xpath(xls_Read.getCellValue("Generic_Elements", "btn_closePopUp;xpath"))).click();
				writeExtent("Pass", "Closed Alert with text " + AlertText + " on " + ScreenName + " Screen");
				break;
				}

			}
			
			return true;
		} catch (Exception e) {
	
			//writeExtent("Info", "Failed to handle Alert with text " + AlertText + " On " + ScreenName + " Screen");
				return false;

		}
	}

	/**
	 * Description... defines options for imail_operations
	 * 
	 * @author A-7688
	 *
	 */
	public enum imailOperations {
		countMailTrigger, clickMail, findMail, checkFMAContent, checkFNAContent, checkMailContent, checkFNAContent2
	}

	/**
	 * Description... Performs the following mail operations in Outlook.
	 * findMail
	 * /clickMail/countMailTrigger/dataCaptureLink/clickHereLink/checkContent
	 * 
	 * @param expectedMailTriggerCount
	 * @param expSubject
	 * @param expText
	 * @param IssueFoundText
	 * @param RecActionText
	 * @param imailOps
	 * @return
	 * @throws Exception
	 */

	public boolean imail_operations(int expectedMailTriggerCount, String expSubject, String expText,
			String IssueFoundText, String RecActionText, String imailOps) throws Exception {

		try {
			List<WebElement> subList = driver
					.findElements(By.xpath(xls_Read.getCellValue("IMail", "txt_subList;xpath")));

			switch (imailOperations.valueOf(imailOps)) {
			case findMail:

				for (int i = 0; i < subList.size(); i++) {
					if (subList.get(i).getText().replaceAll(" ", "").contains(expSubject)) {
						System.out.println("index = " + i + " " + subList.get(i).getText());
						j++;
						k = i;
						break;
					}
				}
				break;

			case clickMail:
				waitForSync(1);
				String xpath = "(" + xls_Read.getCellValue("IMail", "txt_subList;xpath") + ")[" + (k + 1) + "]";
				ele = driver.findElement(By.xpath(xpath));
				javaScriptToclickElement(ele, "mail", "iMail");

				waitForSync(1);
				switchToWindow("storeParent");
				break;

			case countMailTrigger:
				int actualMC = 0;
				for (int i = 0; i < subList.size(); i++) {

					if (subList.get(i).getText().replaceAll(" ", "").contains(expSubject)) {
						actualMC = actualMC + 1;

					}

				}
				if (actualMC == expectedMailTriggerCount) {
					onPassUpdate("Imail", "Exp MailtriggerCountis " + expectedMailTriggerCount,
							"ACT MailtriggerCountis " + j, "Mail count Matches", "");
					System.out.println("Mail trigger Count matches");
				} else {
					onFailUpdate("Imail", "Exp MailtriggerCountis " + expectedMailTriggerCount,
							"ACT MailtriggerCountis " + j, "Mail count does not Matches", "");
				}

				break;

			case checkFMAContent:

				waitForSync(1);

				String mailContent = getElementText("iMail", "txt_mailContent;xpath", "Verify Mail Content", "iMail")
						.replaceAll(" ", "");
				System.out.println("mailContent" + mailContent);
				System.out.println("expText" + expText);

				if (mailContent.contains(expText)) {
					onPassUpdate("iMail", expText, mailContent, "Verify Mail Content", "");
					System.out.println("mail content verified");
				} else
					onFailUpdate("iMail", expText, mailContent, "Verify Mail Content", "");

				break;

				
			case checkFNAContent:

				waitForSync(1);
				String issueMailContent = getElementText("iMail", "txt_mailContent;xpath", "Verify Mail Content",
						"iMail").replaceAll(" ", "");

				if (issueMailContent.contains((expText).replaceAll(" ", ""))) {
					onPassUpdate("iMail", expText, issueMailContent, "Verify Mail Content", "");
					System.out.println("mail content verified");
				} else
					onFailUpdate("iMail", expText, issueMailContent, "Verify Mail Content", "");

				String actIssueText = getElementText("iMail", "txt_issueFound;xpath", "Verify Issue Text", "iMail")
						.replaceAll(" ", "").trim();

				if (actIssueText.contains((IssueFoundText.replaceAll(" ", "")))) {
					onPassUpdate("iMail", IssueFoundText, actIssueText, "Verify Issue Text", "");
					System.out.println("Issue found text verified");
				} else
					onFailUpdate("iMail", IssueFoundText, actIssueText, "Verify Issue Text", "");

				String actRecActionText = getElementText("iMail", "txt_recommendedAction;xpath",
						"Recommended Action Text", "iMail").replaceAll(" ", "").trim();
				System.out.println("actRecActionText---" + actRecActionText);
				System.out.println("RecActionText------" + RecActionText);

				if (actRecActionText.contains(RecActionText)) {
					
					onPassUpdate("iMail", RecActionText, actRecActionText, "Verify Recommended Action Text", "");
					System.out.println("Recommended action text verified" + actRecActionText);
				} else
					onFailUpdate("iMail", RecActionText, actRecActionText, "Verify Recommended Action Text", "");
					System.out.println("Recommended action text not verified" + actRecActionText);
				break;

				
			case checkFNAContent2:

				waitForSync(1);
				 issueMailContent = getElementText("iMail", "txt_mailContent;xpath", "Verify Mail Content",
						"iMail").replaceAll(" ", "");

				if (issueMailContent.contains((expText).replaceAll(" ", ""))) {
					onPassUpdate("iMail", expText, issueMailContent, "Verify Mail Content", "");
					System.out.println("mail content verified");
				} else
					onFailUpdate("iMail", expText, issueMailContent, "Verify Mail Content", "");

				 actIssueText = getElementText("iMail", "txt_issueFound2;xpath", "Verify Issue Text", "iMail")
						.replaceAll(" ", "");

				if (actIssueText.contains((IssueFoundText.replaceAll(" ", "")))) {
					onPassUpdate("iMail", IssueFoundText, actIssueText, "Verify Issue Text", "");
					System.out.println("Issue found text verified");
				} else
					onFailUpdate("iMail", IssueFoundText, actIssueText, "Verify Issue Text", "");

				 actRecActionText = getElementText("iMail", "txt_recommendedAction2;xpath",
						"Recommended Action Text", "iMail").replaceAll(" ", "").trim();

				if (actRecActionText.contains((RecActionText.replaceAll(" ", "")))) {
					onPassUpdate("iMail", RecActionText, actRecActionText, "Verify Recommended Action Text", "");
					System.out.println("Recommended action text verified");
				} else
					onFailUpdate("iMail", RecActionText, actRecActionText, "Verify Recommended Action Text", "");
				break;

				
				
			case checkMailContent:

				waitForSync(1);
				String issueMailContent1 = getElementText("iMail", "txt_mailContent;xpath", "Verify Mail Content",
						"iMail").replaceAll(" ", "");

				    if (issueMailContent1.contains(expText.replace(" ", ""))) {
					onPassUpdate("iMail", expText, issueMailContent1, "Verify Mail Content", "");
					System.out.println("mail content verified");
				} else
					onFailUpdate("iMail", expText, issueMailContent1, "Verify Mail Content", "");
				break;

			}

		} catch (Exception e) {
			test.log(LogStatus.FAIL, "Could not perform imail_operations");
		}
		return Status;

	}

	public void imailVerificationMultipleIssues(String expSubject, String expMailContent, List<String> expIssues)
			throws Exception {

		try {

			String subListXpath = xls_Read.getCellValue("IMail", "txt_subList;xpath");

			List<WebElement> subList = driver.findElements(By.xpath(subListXpath));
			int i = 0;
			for (i = 0; i < subList.size(); i++) {
				if ((subList.get(i).getText().replaceAll(" ", "")).contains(expSubject)) {
					System.out.println("index = " + i + " " + subList.get(i).getText());
					break;
				}
			}
			System.out.println("i val is---" + i);
			subList.get(i).click();
			waitForSync(5);

			String issueSubjectXpath = xls_Read.getCellValue("IMail", "txt_IssueSubject;xpath");
			ele = driver.findElement(By.xpath(issueSubjectXpath));
			String issueSubject = ele.getText().replaceAll(" ", "");
			if (issueSubject.contains(expMailContent)) {

				test.log(LogStatus.PASS, "Successfully Verified " + expMailContent + " in iMail");
				System.out.println("Successfully Verified " + expMailContent + "in iMail");

			} else {
				test.log(LogStatus.FAIL, "Failed to Verify " + expMailContent);
				System.out.println("Failed to Verify " + expMailContent);
				Assert.assertFalse(true, "Failed to Verify mailSubject");

			}

			String Issues = xls_Read.getCellValue("IMail", "table_issues;xpath");
			// boolean flag = false;

			for (int j = 1; j < (expIssues.size()); j++) {
				boolean flag = false;
				String dynXpath = Issues + "[" + j + "]";
				System.out.println("dynXpath is--" + dynXpath);
				ele = driver.findElement(By.xpath(dynXpath));
				String actIssue = ele.getText().replaceAll(" ", "").replaceAll("-", "");
				for (int k = 1; k < expIssues.size(); k++) {
					String expIssue = (expIssues.get(k)).replaceAll(" ", "").replaceAll(":", "").replaceAll("-", "");

					if (actIssue.contains(expIssue)) {
						flag = true;
						test.log(LogStatus.PASS, "Successfully Verified " + expIssue + " in iMail");

						System.out.println("Successfully Verified " + expIssue + "in iMail");
						break;
					}

				}

				if (flag == false) {
					test.log(LogStatus.FAIL, "Failed to Verify " + expIssues.get(k));
					System.out.println("Failed to Verify " + expIssues.get(k));
					Assert.assertFalse(true, "Element is not found");
				}
			}

			/*
			 * switchToWindow("storeParent"); javaScriptToclickElement("iMail",
			 * "lnk_dataCapture;xpath", "Data Capture Link", "iMail");
			 * 
			 * waitForSync(3); switchToWindow("child"); String LufTitle =
			 * driver.getTitle();
			 * 
			 * waitForSync(3);
			 * 
			 * verifyScreenText("Lufthansa Login", data("lufthansaTitle"),
			 * LufTitle, "Lufthansa Login Title", "");
			 * 
			 * enterValueInTextbox("iMail", "inbx_LufUsername;xpath",
			 * "UserName", "UserName", "iMailLogin");
			 * 
			 * // driver.close(); switchToWindow("getParent");
			 * javaScriptToclickElement("iMail", "lnk_clickHere;xpath",
			 * "Click here Link", "iMail");
			 * 
			 * String expAWBTitle = data("expAWBTitle"); String actAWBTitle =
			 * driver.getTitle(); verifyScreenText(actAWBTitle, expAWBTitle,
			 * actAWBTitle, "AWB", "");
			 * 
			 * driver.close(); switchToWindow("getParent");
			 */

		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("Could not perform imail verification" + e);
			test.log(LogStatus.FAIL, "Could not perform imail verification");
		}
	}

	/**
	 * Description... write the text message stored in the messageLine variable
	 * by createTextMessage method
	 * 
	 * @param sheetName
	 * @param locator
	 * @param eleName
	 * @param screenName
	 * @throws IOException
	 */
	public void writeTextMsg(String sheetName, String locator, String eleName, String screenName) throws IOException {

		try {
			clearText(sheetName, locator, eleName, screenName);
			String messageLine = parameters.get("messageLine");
			driver.findElement(By.xpath(xls_Read.getCellValue(sheetName, locator))).sendKeys(messageLine);
		} catch (Exception e) {
			test.log(LogStatus.FAIL, "Could not write text message");
		}

	}

	public void verify_tbl_records_pmyKeyDropdown(String sheetName, String locator, String tableTag, int verfCols[],
			String pmyKey, String actVerfValues[]) {

		try {
			boolean flag = false;
			int row = 0;
			String ScreenName = sheetName.split("_")[0];
			// get the required row
			String tableBody = xls_Read.getCellValue(sheetName, locator);
			List<WebElement> rows = driver.findElements(By.xpath(xls_Read.getCellValue(sheetName, locator)));
			String dynXpath = xls_Read.getCellValue(sheetName, locator) + tableTag;

			System.out.println("row size  " + rows.size());
			switch (tableTag) {
			case "//select": {
				for (int i = 0; i < rows.size(); i++) {
					dynXpath = xls_Read.getCellValue(sheetName, "lst_custDetails;xpath");
					List<WebElement> cols = driver.findElements(By.xpath(dynXpath));

					for (int j = 1; j < cols.size(); j++) {

						Select select = new Select(cols.get(j));
						if (select.getFirstSelectedOption().getText().contains(pmyKey)) {
							flag = true;
							i = j;
							break;

						}
					}
					if (flag) {
						row = i;
						break;
					}
				}
				for (int i = 0; i < verfCols.length; i++) {

					WebElement ele = null;
					dynXpath = "(" + tableBody + ")[" + row + "]" + "//td[" + verfCols[i] + "]//input";
					ele = driver.findElement(By.xpath(dynXpath));
					/*
					 * if (ele.getAttribute("value").toLowerCase().replace(" ",
					 * "") .contains(actVerfValues[i].replace(" ",
					 * "").toLowerCase())) {
					 * System.out.println("found true for " + actVerfValues[i]);
					 * test.log(LogStatus.PASS, "Verified " + actVerfValues[i] +
					 * " On " + ScreenName + " Screen");
					 * 
					 * } else { test.log(LogStatus.FAIL, "Could not Verify " +
					 * actVerfValues[i] + " On " + ScreenName + " Screen");
					 * 
					 * }
					 */

					String actual = ele.getAttribute("value").toLowerCase().replace(" ", "");
					String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
					if (actual.contains(expected)) {
						System.out.println("found true for " + actVerfValues[i]);

						onPassUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					} else {
						onFailUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					}

				}
			}
				break;

			}

		}

		catch (Exception e) {

			test.log(LogStatus.FAIL, "Failed to verify table records");
		}

	}

	/**
	 * Description... Verfies any number of column data in a table
	 * 
	 * @param sheetName
	 *            xpath sheet name
	 * @param locator
	 *            xpath locator
	 * @param tableTag
	 *            tagname of the element whose value is to be verified
	 * @param verfCols
	 *            array of column numbers
	 * @param pmyKey
	 *            unique number for selecting a row
	 * @param actVerfValues
	 *            values to be verified
	 */

	public void click_tbl_records_multiple_cols(String sheetName, String locator, String tableCell, String xpath,
			String pmyKey) {

		try {
			boolean flag = false;
			int row = 0;

			// get the required row
			String tableBody = xls_Read.getCellValue(sheetName, locator);
			List<WebElement> rows = driver.findElements(By.xpath(xls_Read.getCellValue(sheetName, locator)));

			System.out.println("row size  " + rows.size());

			for (int i = 0; i < rows.size(); i++) {
				String dynXpath = xls_Read.getCellValue(sheetName, locator);
				List<WebElement> cols = driver.findElements(By.xpath(dynXpath));

				for (int j = 1; j < cols.size(); j++) {

					Select select = new Select(cols.get(j));
					if (select.getFirstSelectedOption().getText().contains(pmyKey)) {
						flag = true;
						i = j;
						break;

					}
				}
				if (flag) {
					row = i;
					break;
				}
			}

			tableCell = xls_Read.getCellValue(sheetName, tableCell);
			xpath = xls_Read.getCellValue(sheetName, xpath);
			WebElement ele = null;
			String dynXpath = "(" + xpath + ")[" + row + "]" + tableCell;
			ele = driver.findElement(By.xpath(dynXpath));
			ele.click();
		} catch (Exception e) {

			test.log(LogStatus.FAIL, "Failed to click table records");
		}
	}

	/**
	 * Description... Verfies any number of column data in a table
	 * 
	 * @param sheetName
	 *            xpath sheet name
	 * @param locator
	 *            xpath locator
	 * @param tableTag
	 *            tagname of the element whose value is to be verified
	 * @param verfCols
	 *            array of column numbers
	 * @param pmyKey
	 *            unique number for selecting a row
	 * @param actVerfValues
	 *            values to be verified
	 */

	public void click_tbl_records_multiple_cols(String sheetName, String locator, String tableCell, String xpath,
			String pmyKey, String tableTag) {
		boolean flag = false;
		int row = 0;

		// get the required row
		try {
			List<WebElement> rows = driver.findElements(By.xpath(xls_Read.getCellValue(sheetName, locator)));

			String dynXpath = xls_Read.getCellValue(sheetName, locator) + tableTag;
			System.out.println("row size  " + rows.size());

			{
				for (int i = 0; i < rows.size(); i++) {
					dynXpath = xls_Read.getCellValue(sheetName, locator);
					List<WebElement> cols = driver.findElements(By.xpath(dynXpath));

					for (int j = 0; j < cols.size(); j++) {

						Select select = new Select(cols.get(j));
						System.out.println(select.getFirstSelectedOption().getText());
						if (select.getFirstSelectedOption().getText().equals(pmyKey)) {
							flag = true;
							i = j;
							break;

						}
					}
					if (flag) {
						row = i + 1;
						break;
					}
				}

				String tableCell1 = xls_Read.getCellValue(sheetName, tableCell);
				String xpath1 = xls_Read.getCellValue(sheetName, xpath);
				WebElement ele = null;

				String dynXpath1 = "(" + xpath1 + tableCell1 + ")[" + row + "]";
				System.out.println(dynXpath1);
				ele = driver.findElement(By.xpath(dynXpath1));
				ele.click();
			}
		} catch (Exception e) {

			test.log(LogStatus.FAIL, "Failed to click table records");
		}
	}
/**
 * 
 * @param ScreenName
 * @throws InterruptedException
 * @throws Exception
 */
	public void save(String ScreenName) throws InterruptedException, Exception {
		clickWebElement("Generic_Elements", "btn_save;xpath", "Save Button", ScreenName);
		waitForSync(4);
	}
/**
 * 
 * @param ScreenName
 * @throws InterruptedException
 * @throws IOException 
 */
	public void clear(String ScreenName) throws InterruptedException, IOException {
		clickWebElement("Generic_Elements", "btn_clear;xpath", "Clear Button", ScreenName);

	}

	/**
	 * Description... NNO DB verification
	 * 
	 * @param QAStatus
	 *            QAStatus
	 * @param AWB
	 *            AWBNo
	 * @param date
	 *            date on which AWB operations like EQA/IQA are performed , to
	 *            be passed to DB in format YYYY-MM-dd
	 */
	public void verifyNNODB(String QAStatus, String AWB, String date) {
		java.sql.Statement statement = null;
		java.sql.Connection connection = null;
		// boolean result = false;
		String query = null;
		String environment = WebFunctions.getPropertyValue(globalVarPath, "testEnv");
		AWB = AWB.substring(0, AWB.length() - 1);

		try {

			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			System.out.println("Connecting to the database...");
			if (environment.equalsIgnoreCase("LCAGSIT")) {

				// connection to LCAG-SIT DB
				connection = DriverManager.getConnection("jdbc:oracle:thin:@alcagcavdbs1.lsy.fra.dlh.de:1850:XCRASV",
						"U167851", "MXSEMWSCFWBUEOOKHBGB");
			}

			// connection to iCapsit DB
			else if (environment.equalsIgnoreCase("ICAPSIT")) {

				connection = DriverManager.getConnection("jdbc:oracle:thin:@57.20.86.196:1850:XCRASI", "ICAP_RO",
						"hl9FvA5t");

			}

			statement = connection.createStatement();

			query = "SELECT a.awb_tmstmp_iv_entry,a.awb_TC_ISSUE , a.awb_ser_no_chk_dig,a.AWB_CHECK_DIGIT, a.awb_CRAS_SEQ_NUM , "
					+ "a.awb_status, a.awb_seq_status,a.AWB_PQ_STATUS,a.AWB_RECORD_TYPE, a.AWB_TN_DEST , a.AWB_TN_ORIG, a.AWB_CURR_DEST, a.AWB_SDB_DLV_FLAG,a.AWB_CESAR_DLV_FLAG,"
					+ " a.AWB_ORIG_SYSTEM_IND, a.AWB_USER_ID FROM B542T1HB a " + "WHERE a.awb_tmstmp_iv_entry like '"
					+ date + "%'" + "and a.awb_ser_no_chk_dig like '" + AWB + "'" + "and awb_record_type like'"
					+ QAStatus + "'" + "order by awb_tmstmp_iv_entry asc";
			ResultSet resultset = statement.executeQuery(query);

			while (resultset.next()) {
				// Retrieve by column name

				// AWB_Seq_Status column verification
				String AWB_Seq_Status = resultset.getString("awb_seq_status");
				if (AWB_Seq_Status.equalsIgnoreCase("V")) {
					onPassUpdate("NNO DB", "V", AWB_Seq_Status, "AWB_Seq_Status value verification in DB",
							"NNO DB column Verification");
					System.out.println("AWB_Seq_Status is correct" + AWB_Seq_Status);

				} else {
					System.out.println("AWB_Seq_Status is NOT correct" + AWB_Seq_Status);
					onFailUpdate("NNO DB", "V", AWB_Seq_Status, "AWB_Seq_Status value verification in DB",
							"NNO DB column Verification");

				}

				// AWB_Status column verification
				String AWB_Status = resultset.getString("awb_status");
				if (AWB_Status.equalsIgnoreCase("V")) {
					onPassUpdate("NNO DB", "V", AWB_Status, "AWB_Status value verification in DB",
							"NNO DB column Verification");
					System.out.println("AWB_Status is correct" + AWB_Status);

				} else {
					System.out.println("AWB_Status is not correct" + AWB_Status);
					onFailUpdate("NNO DB", "V", AWB_Status, "AWB_Status value verification in DB",
							"NNO DB column Verification");

				}

				// AWB_PQ_Status column verification
				String AWB_PQ_Status = resultset.getString("awb_pq_status");
				if (AWB_PQ_Status.equalsIgnoreCase("V")) {
					onPassUpdate("NNO DB", "V", AWB_PQ_Status, "AWB_PQ_Status value verification in DB",
							"NNO DB column Verification");
					System.out.println("AWB_PQ_Status is correct" + AWB_PQ_Status);
				} else {
					System.out.println("AWB_PQ_Status is not correct" + AWB_PQ_Status);
					onFailUpdate("NNO DB", "V", AWB_PQ_Status, "AWB_PQ_Status value verification in DB",
							"NNO DB column Verification");

				}

				// AWB_RECORD_TYPE verification
				String AWB_RECORD_TYPE = resultset.getString("AWB_RECORD_TYPE");
				if ((AWB_RECORD_TYPE.equalsIgnoreCase(QAStatus))) {

					onPassUpdate("NNO DB", QAStatus, AWB_RECORD_TYPE, "AWB_RECORD_TYPE value verification in DB",
							"NNO DB column Verification");
					System.out.println("AWB_RECORD_TYPE is correct" + AWB_RECORD_TYPE);

				} else {
					System.out.println("AWB_RECORD_TYPE is not correct" + AWB_RECORD_TYPE);
					onFailUpdate("NNO DB", QAStatus, AWB_RECORD_TYPE, "AWB_RECORD_TYPE value verification in DB",
							"NNO DB column Verification");

				}

				String Dest = resultset.getString("AWB_TN_DEST");

				String Origin = resultset.getString("AWB_TN_ORIG");
				String currency = resultset.getString("AWB_CURR_DEST");

				// SDB_DLV_FLAG verification
				String SDB_DLV_FLAG = resultset.getString("AWB_SDB_DLV_FLAG");

				if (SDB_DLV_FLAG.equalsIgnoreCase("Y")) {
					onPassUpdate("NNO DB", "Y", SDB_DLV_FLAG, "SDB_DLV_FLAG value verification in DB",
							"NNO DB column Verification");
					System.out.println("SDB_DLV_FLAG is correct" + SDB_DLV_FLAG);

				} else {
					System.out.println("AWB_RECORD_TYPE is not correct" + SDB_DLV_FLAG);
					onFailUpdate("NNO DB", "Y", SDB_DLV_FLAG, "SDB_DLV_FLAG value verification in DB",
							"NNO DB column Verification");

				}

				// CESAR_DLV_FLAG column verification
				String CESAR_DLV_FLAG = resultset.getString("AWB_CESAR_DLV_FLAG");
				if (CESAR_DLV_FLAG.equalsIgnoreCase("Y")) {
					onPassUpdate("NNO DB", "Y", CESAR_DLV_FLAG, "CESAR_DLV_FLAG value verification in DB",
							"NNO DB column Verification");
					System.out.println("CESAR_DLV_FLAG is correct" + CESAR_DLV_FLAG);

				} else {
					System.out.println("CESAR_DLV_FLAG is not correct" + CESAR_DLV_FLAG);
					onFailUpdate("NNO DB", "Y", CESAR_DLV_FLAG, "CESAR_DLV_FLAG value verification in DB",
							"NNO DB column Verification");

				}

				System.out.print("AWB_Seq_Status: " + AWB_Seq_Status);
				System.out.println(", AWB_Status: " + AWB_Status);
				System.out.println(", AWB_PQ_Status: " + AWB_PQ_Status);
				System.out.print(", Dest: " + Dest);
				System.out.println(", Origin: " + Origin);
				System.out.println(", currency: " + currency);
				System.out.println(", SDB_DLV_FLAG: " + SDB_DLV_FLAG);
				System.out.println(", CESAR_DLV_FLAG: " + CESAR_DLV_FLAG);

			}

			resultset.close();

		} catch (SQLException e) {

			// e.printStackTrace();
			e.printStackTrace();
			test.log(LogStatus.FAIL, "Exception in NNO DB : " + e);

		} catch (Exception e) {
			e.printStackTrace();
			test.log(LogStatus.FAIL, "Exception in NNO DB : " + e);

		} finally {

			try {
				if (statement != null)
					connection.close();
			} catch (SQLException e) {
				// e.printStackTrace();
				writeExtent("Fail", "NNO DB data could not be verified");
				test.log(LogStatus.FAIL, "Exception in NNO DB : " + e);

			}

		}
	}

	public void enterToFromDateListFlight(String fromDate, String toDate, String screenName)
			throws InterruptedException {
		enterValueInTextbox("Generic_Elements", "inbx_fromdate;xpath", fromDate, "from date", screenName);
		enterValueInTextbox("Generic_Elements", "inbx_todate;xpath", toDate, "to date", screenName);
	}

	public void verifySuccessMessage(String sheetName, String locator, String testSteps, String screenName,
			String functionalityName) {
		try {
			By b = getElement(sheetName, locator);
			for (int i = 0; i < 60; i++) {
				try {
					waitForSync(1);
					driver.findElement(b);
					verifyValueOnPage(true, true, testSteps, screenName, functionalityName);
					break;

				} catch (Exception e) {

				}
			}

		}

		catch (Exception e) {
			System.out.println("in exception ");
			verifyValueOnPage(true, false, testSteps, screenName, functionalityName);
		}
	}

	public void waitForSoapComplete() {
		int flag = 0;
		int i = 1;
		String soapStatus = WebFunctions.getPropertyValue(globalVarPath, "SoapTaskStatus");
		while (flag == 0) {
			if (soapStatus.equalsIgnoreCase("Yes")) {
				flag = 1;
			} else {
				waitForSync(4);
				soapStatus = WebFunctions.getPropertyValue(globalVarPath, "SoapTaskStatus");
				i++;
			}
			if (i == 30) {
				break;
			}
		}
	}

	public void verify_tbl_records_multiple_cols_contains(String sheetName, String locator, String tableTag,
   int verfCols[], String pmyKey, String actVerfValues[]) {
  try {

   boolean flag = false;
   int row = 0;
   String ScreenName = sheetName.split("_")[0];
   // get the required row
   String tableBody = xls_Read.getCellValue(sheetName, locator);
   List<WebElement> rows = driver.findElements(By.xpath(xls_Read.getCellValue(sheetName, locator)));
   String dynXpath = xls_Read.getCellValue(sheetName, locator) + tableTag;

   System.out.println("row size  " + rows.size());
   switch (tableTag) {

   case "//input": {
    for (int i = 0; i < rows.size(); i++) {

     List<WebElement> cols = driver.findElements(By.xpath(dynXpath));

     for (int j = 0; j < cols.size(); j++) {

      System.out.println("col text = " + cols.get(j).getAttribute("value"));
      if (cols.get(j).getAttribute("value").contains(pmyKey)) {
       flag = true;
       break;
      }
     }
     if (flag) {
      row = i + 1;
      break;
     }
    }
    for (int i = 0; i < verfCols.length; i++) {
     dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
     WebElement ele = null;
     dynXpath = "(" + tableBody + ")[" + row + "]" + "//td[" + verfCols[i] + "]//input";
     ele = driver.findElement(By.xpath(dynXpath));
     if (ele.getAttribute("value").toLowerCase().replace(" ", "")
       .contains(actVerfValues[i].replace(" ", "").toLowerCase())) {
      System.out.println("found true for " + actVerfValues[i]);
      test.log(LogStatus.PASS, "Verified " + actVerfValues[i] + " On " + ScreenName + " Screen");

     } else {
      test.log(LogStatus.FAIL,
        "Could not Verify " + actVerfValues[i] + " On " + ScreenName + " Screen");
      Status = false;
      map.put("FailedVal", actVerfValues[i]);
     }
    }
   }
    break;
   case "//td":

   {

    rows = driver.findElements(By.xpath(tableBody));

    dynXpath = tableBody + tableTag;
    {
     for (int i = 0; i <= rows.size(); i++) {
      System.out.println("i= " + i);

      if (rows.get(i).getText().toLowerCase().replace(" ", "")
        .contains(pmyKey.toLowerCase().replace(" ", ""))) {

       flag = true;

      }

      if (flag) {
       row = i + 1;
       break;
      }
     }

     System.out.println("row = " + row);
     for (int i = 0; i < verfCols.length; i++) {

      dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
      WebElement ele = null;

      ele = driver.findElement(By.xpath(dynXpath));
      if (ele.getText().toLowerCase().replace(" ", "")
        .contains(actVerfValues[i].replace(" ", "").toLowerCase())) {
       System.out.println("found true for " + actVerfValues[i]);
       test.log(LogStatus.PASS, "Verified " + actVerfValues[i] + " On " + ScreenName + " Screen");

      } else {
       test.log(LogStatus.FAIL,
         "Could not Verify " + actVerfValues[i] + " On " + ScreenName + " Screen");
       Status = false;
       map.put("FailedVal", actVerfValues[i]);
      }
     }

    }
   }
    break;
   case "//div":

   {
    for (int i = 0; i <= rows.size(); i++) {
     System.out.println("i= " + i);

     if (rows.get(i).getText().toLowerCase().replace(" ", "")
       .contains(pmyKey.toLowerCase().replace(" ", ""))) {

      flag = true;

     }

     if (flag) {
      row = i + 1;
      break;
     }
    }
   }
    System.out.println("row = " + row);
    for (int i = 0; i < verfCols.length; i++) {

     dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
     WebElement ele = null;

     ele = driver.findElement(By.xpath(dynXpath));
     if (ele.getText().toLowerCase().replace(" ", "")
       .contains(actVerfValues[i].replace(" ", "").toLowerCase())) {
      System.out.println("found true for " + actVerfValues[i]);
      test.log(LogStatus.PASS, "Verified " + actVerfValues[i] + " On " + ScreenName + " Screen");

     } else {
      test.log(LogStatus.FAIL,
        "Could not Verify " + actVerfValues[i] + " On " + ScreenName + " Screen");
      Status = false;
      map.put("FailedVal", actVerfValues[i]);
     }

     break;
    }
    
   case "//select":

   {

    rows = driver.findElements(By.xpath(tableBody));

    dynXpath = tableBody + tableTag;
    {
     for (int i = 0; i <= rows.size(); i++) {
      System.out.println("i= " + i);

      if (rows.get(i).getText().toLowerCase().replace(" ", "")
        .contains(pmyKey.toLowerCase().replace(" ", ""))) {

       flag = true;

      }

      if (flag) {
       row = i + 1;
       break;
      }
     }

     System.out.println("row = " + row);
     for (int i = 0; i < verfCols.length; i++) {

      dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
      WebElement ele = null;

      ele = driver.findElement(By.xpath(dynXpath));
      Select select = new Select(ele);
      String SelectedOption = select.getFirstSelectedOption().getText();
      if (SelectedOption.toLowerCase().replace(" ", "")
        .contains(actVerfValues[i].replace(" ", "").toLowerCase())) {
       System.out.println("found true for " + actVerfValues[i]);
       test.log(LogStatus.PASS, "Verified " + actVerfValues[i] + " On " + ScreenName + " Screen");

      } else {
       test.log(LogStatus.FAIL,
         "Could not Verify " + actVerfValues[i] + " On " + ScreenName + " Screen");
       Status = false;
       map.put("FailedVal", actVerfValues[i]);
      }
     }

    }
    
    break;
   }
   

   }
  } catch (Exception e) {

   test.log(LogStatus.FAIL, "Could not perform table record verification");
   System.out.println("Table contents are not verified or verification failed");
   Assert.assertFalse(true, "Could not perform table record verification");
  }
 }

	public void clearTimeFilter(String screenName) {
		clearText("Generic_Elements", "inbx_fromTimeFilter;xpath", "from Time Filter", screenName);
		clearText("Generic_Elements", "inbx_toTimeFilter;xpath", "to Time Filter", screenName);

	}

	/**
	 * @author A-7271
	 * Description... Check if AWB already exists in OPR026 screen
	 * 
	 * @param screenName
	 *            CaptureAWB
	 * @param screenId
	 *            OPR026
	 */
	public void checkAWBExists_OPR026(String screenName, String screenId) throws InterruptedException {

		
		try {
			do {
				createAWB("AWBNo");

				

				clickWebElement("CaptureAWB_OPR026", "btn_clear;name", "Clear Button", screenName);
				listAWB("AWBNo", "CarrierNumericCode", screenName);
				
				
				
				
			}
			
			while (driver.findElements(By.xpath("//div[@class='toast-item-close-success']")).size() == 0);
			

		} catch (Exception e) {
			System.out.println("In catch block of checkAWBExists_OPR026 methid");
			

		}

		
		closeTab(screenId, screenName);
	}

	/**
	 * Description... Method is used to create random two number and one
	 * alphabet combination
	 * 
	 * @param range_from
	 *            Range from which the number should start use property name
	 *            "number_range_from"
	 * @param set_value
	 *            Property value to which random number is to be assigned
	 * @throws Interrupted
	 *             exception
	 */

	public void createRandomNumber(String propath, String range_from, String set_value) throws InterruptedException {
		try {

			String value = getPropertyValue(proppath, range_from);

			int val = Integer.parseInt(value) + 1;

			Random r = new Random();
			String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

			Character base = alphabet.charAt(r.nextInt(alphabet.length()));
			value = value + Character.toString(base);
			setPropertyValue(set_value, value, proppath);

			String valToStore = Integer.valueOf(val).toString();
			setPropertyValue(range_from, valToStore, proppath);

		} catch (Exception e) {

			test.log(LogStatus.FAIL, "Could not create number");
			System.out.println("Could not create Number");
		}
	}

	/**
	 * @author A-7271
	 * @param len
	 * @return
	 * Desc : Craete random alphabets of set size
	 */
	public String createRandomAlphabets(int len)
	{
		try
		{
		 // chose a Character random from this String 
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                                   
  
        // create StringBuffer size of AlphaNumericString 
        StringBuilder sb = new StringBuilder(len); 
  
        for (int i = 0; i < len; i++) { 
  
            // generate a random number between 
            // 0 to AlphaNumericString variable length 
            int index 
                = (int)(AlphaNumericString.length() 
                        * Math.random()); 
  
            // add Character one by one in end of sb 
            sb.append(AlphaNumericString 
                          .charAt(index)); 
        } 
  
        return sb.toString(); 
		}
		
		catch(Exception e)
		{
			return "";
		}
	
	}
	public void verify_tbl_records_pmyKeyDropdown_equals(String sheetName, String locator, String tableTag,
			int verfCols[], String pmyKey, String actVerfValues[]) {

		try {
			boolean flag = false;
			int row = 0;
			String ScreenName = sheetName.split("_")[0];
			// get the required row
			String tableBody = xls_Read.getCellValue(sheetName, locator);
			List<WebElement> rows = driver.findElements(By.xpath(xls_Read.getCellValue(sheetName, locator)));
			String dynXpath = xls_Read.getCellValue(sheetName, locator) + tableTag;

			System.out.println("row size  " + rows.size());
			switch (tableTag) {
			case "//select": {
				for (int i = 0; i < rows.size(); i++) {
					dynXpath = xls_Read.getCellValue(sheetName, "lst_custDetails;xpath");
					List<WebElement> cols = driver.findElements(By.xpath(dynXpath));

					for (int j = 0; j < cols.size(); j++) {

						Select select = new Select(cols.get(j));
						if (select.getFirstSelectedOption().getText().equals(pmyKey)) {
							flag = true;
							i = j;
							break;

						}
					}
					if (flag) {
						row = i + 1;
						break;
					}
				}
				for (int i = 0; i < verfCols.length; i++) {

					WebElement ele = null;
					dynXpath = "(" + tableBody + ")[" + row + "]" + "//td[" + verfCols[i] + "]//input";
					ele = driver.findElement(By.xpath(dynXpath));

					String actual = ele.getAttribute("value").toLowerCase().replace(" ", "");
					String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
					if (actual.equals(expected)) {
						System.out.println("found true for " + actVerfValues[i]);

						onPassUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					} else {
						onFailUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					}

				}
			}
				break;

			}

		}

		catch (Exception e) {

			test.log(LogStatus.FAIL, "Failed to verify table records");
		}

	}

	/*
	 * Author: A-8468 Date Modified :21-08-2018
	 */

	/**
	 * Description... Method is used to create random two number and one
	 * alphabet combination
	 * 
	 * @param sheetName
	 *            name of sheet in locators.xls
	 * @param screenName
	 *            name of screen
	 * @param verfCols[]
	 *            table columns to be verified
	 * @param actVerfValues[]
	 *            value against which verification has to be done
	 * @param pmKey
	 *            primary key
	 * @throws Interrupted
	 *             exception
	 */

	public void verifyEachRecord(String sheetName, String screenName, int verfCols[], String actVerfValues[],
			String pmKey) throws InterruptedException {
		try {

			String table_row = xls_Read.getCellValue(sheetName, "table_legCapacity;xpath");
			List<WebElement> rows = driver.findElements(By.xpath(table_row));
			for (int i = 1; i <= rows.size(); i++) {
				System.out.println("i= " + i);
				String dynXpath = table_row + "[" + i + "]";

				for (int k = 0; k < verfCols.length; k++) {
					int x = verfCols[k];

					String td = dynXpath + "//td" + "[" + x + "]";
					ele = driver.findElement(By.xpath(td));

					String actual = ele.getText().toLowerCase().replace(" ", "");
					String expected = (actVerfValues[k].replace(" ", "").toLowerCase());

					if (actual.equals(expected)) {
						System.out.println("found true for " + actVerfValues[k]);

						onPassUpdate(screenName, expected, actual, "Table verification against " + pmKey + " On ",
								"Table verification");

					} else {
						onFailUpdate(screenName, expected, actual, "Table verification against " + pmKey + " On ",
								"Table verification");

					}
				}

			}
		} catch (Exception e) {

			retryCount = retryCount + 1;

			if (retryCount <= 3) {
				verifyEachRecord(sheetName, screenName, verfCols, actVerfValues, pmKey);
			}

			else {

				test.log(LogStatus.FAIL, "Could not perform table record verification");
				System.out.println("Table contents are not verified or verification failed");
			}

		}
	}

	/*
	 * * Description... Verifies multiple data in the downloaded excel based on
	 * the primary key
	 * 
	 * @param pmyKey
	 * 
	 * @param dwnloadPath can be given as
	 * C:\\Users\\"+System.getProperty("user.name")+"\\Downloads"
	 * 
	 * @param sheetName excel sheetname for in which data is present
	 * 
	 * @param expValue
	 * 
	 * @param ScreenName application screenname
	 * 
	 * @throws Exception
	 */

	public void verify_excel_records_multiple_cols(String pmyKey, String dwnloadPath, String sheetName,
			String expValue[], String ScreenName) throws Exception {
		try {
			int pmyKeyRowNum = 0;
			FileInputStream ff = new FileInputStream(dwnloadPath);
			Workbook wb = WorkbookFactory.create(ff);
			Sheet ss = wb.getSheet(sheetName);

			int lastRow = ss.getLastRowNum();

			for (int i = 0; i <= lastRow; i++) {
				int lastCell = ss.getRow(i).getLastCellNum();

				for (int j = 0; j < lastCell; j++) {
					String cellValue = ss.getRow(i).getCell(j).getStringCellValue();
					if (cellValue.replace(" ", "").trim().equals(pmyKey.replace(" ", "").trim())) {
						System.out.println(pmyKey + " found");
						pmyKeyRowNum = i;
						break;
					} else

						System.err.println(pmyKey + " not found");
				}
			}

			for (int k = 0; k < expValue.length; k++) {
				int lastCell = ss.getRow(pmyKeyRowNum).getLastCellNum();
				for (int j = 0; j < lastCell; j++) {
					String actValue = ss.getRow(pmyKeyRowNum).getCell(j).getStringCellValue();
					if (actValue.replace(" ", "").trim().equals(expValue[k].replace(" ", "").trim())) {
						System.out.println("found " + expValue[k]);
						onPassUpdate(ScreenName, expValue[k], actValue, "Excel Verification" + " On " + ScreenName,
								"Excel Verification");

						break;
					} else
						System.err.println(expValue[k] + " not found");
					onFailUpdate(ScreenName, expValue[k], actValue, "Excel Verification" + " On " + ScreenName,
							"Excel Verification");
				}
			}

		} catch (Exception e) {
			System.err.println("Failed in Excel Verification");
			Assert.assertFalse(true, "Failed in Excel Verification");
		}
	}

	/**
	 * Description... Verifies the file is downloaded successfully in the
	 * downloads folder *
	 * 
	 * @param filePath
	 * @param fileName
	 * @param pageName
	 */

	public static boolean verifyFileDownload(String filePath, String fileName, String pageName) {

		File folder = new File(filePath);
		File[] listOfFiles = folder.listFiles();
		boolean fileFound = false;
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println("File " + listOfFiles[i].getName());
				if (listOfFiles[i].toString().contains(fileName)) {
					fileFound = true;
					System.out.println("File found");
					break;
				}
			}

		}
		if (fileFound == false) {
			System.err.println("File not found");
		}
		return fileFound;
	}
	
	//To rename a file
public void renameFile(String filePath,String fileName)
       {
              try
              {
                     
                     
                     
              
                     
                     File folder = new File(filePath.substring(0,filePath.length()-1));
                     
              
                     File[] listOfFiles = folder.listFiles();

                     for (int i = 0; i < listOfFiles.length; i++) {

                           if (listOfFiles[i].isFile()) {

                                  File f = new File(filePath+listOfFiles[i].getName()); 

                                  f.renameTo(new File(filePath+fileName));
                           }
                     }

                     
              }

              catch(Exception e)
              {

              }

       }

	/**
	 * Description...Delete file if present in a folder.
	 * 
	 * @param filePath
	 * @param fileName
	 * @author A-7688
	 */

	public void deleteFileIfPresent(String filePath, String fileName) {
		File folder = new File(filePath);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {

				if (listOfFiles[i].toString().contains(fileName)) {
					writeExtent("Info", "Deleting file " + fileName + " from  " + filePath);
					System.out.println("Deleting file " + fileName + " from  " + filePath);
					listOfFiles[i].delete();
					break;
				}
			}
		}
	}

	// 22OCT18,ddMMMyy,dd-MMM-yyyy
	public String changeDateFormat(String date, String originalFormat, String targetFormat) throws Exception {
		try {

			DateFormat orgFormat = new SimpleDateFormat(originalFormat);
			DateFormat tarFormat = new SimpleDateFormat(targetFormat);
			Date dateValue = orgFormat.parse(date);
			String formattedDate = tarFormat.format(dateValue);

			return formattedDate;
		}

		catch (Exception e) {
			return "";
		}
	}

	public int randomNumberInList(int min, int max) {
		Random random = new Random();
		return random.nextInt((max - min) + 1) + min;

	}

	public void close(String screenName) throws InterruptedException, IOException {
		clickWebElement("Generic_Elements", "btn_close;xpath", "Close Button", screenName);
	}

	public String selectRandomValueFromDropdown(String sheetName, String locator) {
		WebElement element = null;
		try {
			List<WebElement> listOfValues = driver.findElements(By.xpath(xls_Read.getCellValue(sheetName, locator)));
			element = listOfValues.get(randomNumberInList(1, listOfValues.size() - 1));
			String value = element.getText();
			System.out.println("Selected value is " + value);
			element.click();

		}

		catch (Exception e) {

		}

		return element.getText().toString();

	}

public String[] getElementTextFromPDF(String sheetName, String locator[],String eleName[], String screenName) throws Exception {
                
                String remarks[]= new String[locator.length];
    waitForSync(3);
    switchToWindow("storeParent");
    switchToWindow("child");
    waitForSync(3);
    switchToFrame("default");
    waitForSync(3);
    driver.switchTo().frame("ReportContainerFrame");
    waitForSync(3);
    for(int i=0;i<locator.length;i++)
    {
     remarks[i]= getElementText(sheetName, locator[i], eleName[i], screenName);      
    }
    switchToWindow("closeChild");
    switchToWindow("getParent");
    return remarks;
                 
}


	public void loginNewBE(String UserName, String Password) throws Exception {
     String browserName=DriverSetup.browser;
		waitForSync(3);
		try {
			driver.findElement(By.name("txt_userId")).clear();
			driver.findElement(By.name("txt_userId")).sendKeys(UserName);
			driver.findElement(By.name("txt_password")).sendKeys(Password);
			driver.findElement(By.name("btn_Login")).click();
			waitForSync(30);
			if(browserName.equals("iexplore"))
            {
            	switchToWindow("storeParent");
				switchToWindow("childWindow");
            }
				
				else
				{
					switchToWindow("child_BE");
				}
			waitForSync(5);
			String actTitle = driver.getTitle();
			String expTitle = "EasyBooking";

			if (actTitle.contains(expTitle))
				test.log(LogStatus.PASS, "Login Successful to Booking Engine");
			else {
				test.log(LogStatus.FAIL, "Failed to Login to Booking Engine");
				Assert.assertFalse(true, "Failed to Login to Booking Engine");
			}

		} catch (Exception e) {

			test.log(LogStatus.FAIL, "Failed to Login to Booking Engine");
			Assert.assertFalse(true, "Failed to Login to Booking Engine");
		}
	}

	public void closeSuccessMessage(String screenName) {
		/*try {
			By b = getElement("Generic_Elements", "btn_closeSuccessMsg;xpath");
			for (int i = 0; i < 60; i++) {
				try {
					waitForSync(1);
					driver.findElement(b).click();
					verifyValueOnPage(true, true, testSteps, screenName, "Close Success Message");
					break;

				} catch (Exception e) {

				}
			}

		}

		catch (Exception e) {
			System.out.println("in exception ");
			verifyValueOnPage(true, false, testSteps, screenName, "Close Success Message");
		}*/

	}

	/**
	 * Description... Verifies row count in a table
	 * 
	 * @param sheetName
	 *            xpath sheet name
	 * @param locator
	 *            xpath locator
	 * @param pmyKey
	 *            unique number for selecting a row
	 * @param expRowSize
	 *            expected row count
	 * 
	 * @author A-6978
	 */

	public void verifyRowCount(int expRowSize, String pkey, String sheetName, String locator)
			throws InterruptedException, AWTException {
		String dynXpath = xls_Read.getCellValue(sheetName, locator) + "[contains(.,'" + pkey + "')]";
		System.out.println("dynXpath is---" + dynXpath);

		List<WebElement> rows = driver.findElements(By.xpath(dynXpath));
		System.out.println("row size is---" + rows.size());
		int actRowSize = rows.size();

		if (expRowSize == actRowSize)
			onPassUpdate((sheetName.split("_"))[0], String.valueOf(expRowSize), String.valueOf(actRowSize),
					"Table row verification matches in " + (sheetName.split("_"))[0],
					"No of row verification in " + (sheetName.split("_"))[0]);
		else
			onFailUpdate((sheetName.split("_"))[0], String.valueOf(expRowSize), String.valueOf(actRowSize),
					"Table row verification does not match in " + (sheetName.split("_"))[0],
					"No of row verification in " + (sheetName.split("_"))[0]);

	}

	/**
	 * Checks whether a list of elements is displayed and logs the result in
	 * custom report
	 * 
	 * @param list
	 * @param testSteps
	 * @param screenName
	 * @param eleName
	 * @return
	 */
	public int verifyElementListDisplayed(List<WebElement> list, String testSteps, String screenName, String eleName) {

		for (int i = 0; i < list.size(); i++) {

			if (list.get(i).isDisplayed()) {
				customFunction.onPassUpdate(screenName, eleName + (i + 1) + " is Displayed", eleName + " is Displayed",
						eleName + " is Displayed", testSteps);

			} else {
				Status = false;
				customFunction.onFailUpdate(screenName, eleName + " is Displayed", eleName + " is Not Displayed",
						eleName + " is Displayed", testSteps);

			}

		}
		return list.size();
	}

	// Overloaded Method to enter AWB in BE and to change Shipment Prefix
	public boolean getNewAwbFromBE(String ShipmentPrefix, String AwbNo) throws InterruptedException, IOException {

		boolean result = false;
		String awbNo = createAWB(AwbNo);
		driver.switchTo().defaultContent();
		driver.switchTo().frame("Main");
		selectValueInDropdown("BE", "dropDown_ShipmentPrefix;xpath", data(ShipmentPrefix), "Shipment Prefix", "Value");
		enterValueInTextbox("BE", "inbx_AWBNo;name", awbNo, "Awb Number", "BookingEngine");
		clickWebElement("BE", "btn_displayOrder;name", "Click Button", "BookingEngine");

		Thread.sleep(6000);

		try {
			String xpath = xls_Read.getCellValue("BE", "lbl_displyOrderStatus;xpath");
			if (driver.findElements(By.xpath(xpath)).size() != 0) {
				String str1 = driver.findElement(By.xpath(xpath)).getText().toString().trim();

				if (str1.contains("This booking does not exist")) {

					result = true;
				}

				else if (str1.contains("Error")) {
					setPropertyValue("showStopper", "true", globalVarPath);
				} else if (str1.contains("Unexpected Service response")) {
					setPropertyValue("showStopper", "true", globalVarPath);
				} else if (str1.contains("Failed")) {
					setPropertyValue("showStopper", "true", globalVarPath);
				}

			}
			return result;
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("Could not get new AWB from BE" + e);
			test.log(LogStatus.FAIL, "Could not get new AWB from BE");

			return result;

		}

	}


public void verify_rows_multipleData(String sheetName, String locatorRow, String expValue[], String functionalityName, String screenName) throws InterruptedException, IOException{
	String rowText=getElementText(sheetName, locatorRow, functionalityName,screenName);
	
	for(int i=0;i<expValue.length;i++)
		verifyValueOnPageContains(rowText.trim().replace(" ", ""), expValue[i].trim().replace(" ", ""), "Verify "+ functionalityName, screenName, functionalityName);
	
	

}

public void loginBETracker(String UserName, String Password) throws InterruptedException{
	waitForSync(5);
	try {
		
		driver.findElement(By.name("Ecom_User_ID")).clear();
		driver.findElement(By.name("Ecom_User_ID")).sendKeys(UserName);
		driver.findElement(By.name("Ecom_Password")).sendKeys(Password);
		driver.findElement(By.name("Abschicken")).click();
		waitForSync(10);
		switchToWindow("child_BE");
		waitForSync(5);
		String actTitle = driver.getTitle();
		String expTitle = "EasyBooking";

		if (actTitle.contains(expTitle))
			test.log(LogStatus.PASS, "Login Successful to Booking Engine");
		else {
			test.log(LogStatus.FAIL, "Failed to Login to Booking Engine");
			Assert.assertFalse(true, "Failed to Login to Booking Engine");
		}

	} catch (Exception e) {

		test.log(LogStatus.FAIL, "Failed to Login to Booking Engine");
		Assert.assertFalse(true, "Failed to Login to Booking Engine");
	}
}


public  void verify_excel_records_particular_col(String downloadPath, String pmyKey, String fileName, String sheetName, String ScreenName) throws Exception{
		boolean pryBol = true;
		try{
	
			String cellValue="";
			int pmyKeyRowNum=0;
			FileInputStream ff= new FileInputStream(downloadPath+fileName);             
			Workbook wb=WorkbookFactory.create(ff);
			Sheet ss=wb.getSheet(sheetName);

			int lastRow=ss.getLastRowNum();

			for(int i=1;i<=lastRow;i++)
			{   			
				 cellValue=ss.getRow(i).getCell(0).getStringCellValue();
				if(cellValue.replace(" ", "").trim().equals(pmyKey.replace(" ", "").trim()))
				{
					pryBol=true;
					pmyKeyRowNum++;
				}
				else{

					pryBol=false;
				}
			}
		
			if (pryBol){
			onPassUpdate(ScreenName, pmyKey+" "+" of rows "+pmyKeyRowNum+" ", cellValue, "Excel Verification" + " On " + ScreenName,
					"Excel Verification");
			}
			else {
				onFailUpdate(ScreenName, pmyKey, cellValue, "Excel Verification" + " On " + ScreenName,
			
					"Excel Verification");
			}
		

		}
		catch(Exception e){
			System.err.println("Failed in Excel Verification");
			e.printStackTrace();
			Assert.assertFalse(true, "Failed in Excel Verification");
		}
	}
/**
 * Description... Perform file operations on file : delete/replace lines in
 * file
 * 
 * @param file
 *            filename
 * @param fileType
 *            '.txt'
 * @param noOfLinesToDelete
 *            no of lines from top of the file you want to delete
 * @param operationType
 *            delete_lines / replace_lines
 * 
 * @author A-6978
 */

public void file_updation(String file, String fileType, int noOfLinesToDelete, String operationType, String oldVal,
		String newVal) throws FileNotFoundException {
	int lineNum = 0;

	try {
		// FileName
		String fileName = message_files + file + fileType;

		File inputFile = new File(fileName);
		BufferedReader br = new BufferedReader(new FileReader(fileName));

		if (!inputFile.isFile()) {
			System.out.println("Parameter is not an existing file");

		}

		switch (operationType) {

		case "delete_lines":
			// Construct the new file that will later be renamed to the
			// original filename.
			File tempFile = new File(inputFile.getAbsolutePath() + ".tmp");

			PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
			String line = null;
			lineNum = 1;

			// Read from the original file and write to the new
			// unless content matches data to be removed.
			while ((line = br.readLine()) != null) {

				if (lineNum > noOfLinesToDelete) {

					pw.println(line);

					pw.flush();

				}
				lineNum++;
			}
			pw.close();
			br.close();

			// Delete the original file
			if (!inputFile.delete()) {
				System.out.println("Could not delete file");

			}

			// Rename the new file to the filename the original file had.
			if (!tempFile.renameTo(inputFile))
				System.out.println("Could not rename file");
			break;

			
		case "replace_lines":

			String oldtext = "";
			while ((line = br.readLine()) != null) {
				oldtext += line + "\r\n";
			}
			br.close();

			// To replace a line in a file
			String newtext = oldtext.replaceAll(oldVal, newVal);
			parameters.put("messageLine", newtext);
			FileWriter writer = new FileWriter(fileName);
			writer.write(newtext);
			writer.close();

			break;
			
		

		}
	}

	catch (Exception e) {
		System.out.println("Message is not created for Type : " + file);
		test.log(LogStatus.FAIL, "Message is not created for Type : " + file);
		// e.printStackTrace();

	}
}


public String getErrorMessage( String screenName) throws InterruptedException{
	return getElementText("Generic_Elements", "txt_errorMessage;xpath", "Error Message", screenName).trim();
}
/**
 * Description... Get all the text from the PDF 
 * @return
 * @throws Exception
 */
public String getTextFromPDF() throws Exception {
	
	
    waitForSync(3);
    switchToWindow("storeParent");
    switchToWindow("child");
    waitForSync(3);
    switchToFrame("default");
    waitForSync(3);
    driver.switchTo().frame("ReportContainerFrame");
    waitForSync(3);
    
     String remarks= driver.findElement(By.xpath("//body")).getText();     
   
    switchToWindow("closeChild");
    switchToWindow("getParent");
    return remarks;
                 
}
/**
 * Description... Clicks on the List Button
 * @param ScreenName
 * @throws InterruptedException
 * @throws IOException 
 */
public void clickList(String ScreenName) throws InterruptedException, IOException{
	clickWebElement("Generic_Elements", "btn_List;xpath", "List Button",ScreenName);
}
/**
 * Description... Verify no alert is displayed
 */
public void verifyNoAlertDisplayed(){
	try{
		driver.switchTo().defaultContent();		
			String AlertText = driver.findElement(By.xpath(xls_Read.getCellValue("Generic_Elements", "txt_AlertText;xpath")))
					.getText();
			if(!AlertText.equals(""))
			{
				System.out.println("Found alert with text " + AlertText);
				writeExtent("Fail", "Found alert with text " + AlertText);
				Assert.assertFalse(true, "Found alert with text " + AlertText);
			}
	}
	catch(Exception e){
		System.out.println("No Alert/Error Message found");
		writeExtent("Pass", "No Alert/Error Message found");
		
	}
}

/**
 * Description... Verify the error message thrown in any Cargo Screen contains the error message collected from test data 
 * @param screenName
 * @throws InterruptedException
 * @throws IOException 
 */
public void verifyErrorMessage(String screenName, String expErrorMessage ) throws InterruptedException, IOException{
    String actErrorMessage =getElementText("Generic_Elements", "txt_errorMessage;xpath", "Error Message", screenName);
    verifyValueOnPageContains(actErrorMessage, data(expErrorMessage), "Verify Error Message", screenName, "Error Message");
    
}

public void sendMessageQueueSender() throws InterruptedException, IOException {
    waitForSync(5);
    String SheetName = "Queue_Sender";

    enterValueInTextbox(SheetName, "txt_box;name", parameters.get("messageLine"), "Message", "Queue_Sender");
    clickWebElement(SheetName,"btn_submit;name", "Submit", "Queue_Sender");
    waitForSync(3);

}

public void logineBooking(String username, String password) throws InterruptedException, AWTException, IOException {

	waitForSync(5);
	enterValueInTextbox("eBooking", "inbx_eBookingUN;name", username, "username", "eBooking");
	waitForSync(4);
	enterValueInTextbox("eBooking", "inbx_eBookingPWD;name", password, "password", "eBooking");
	waitForSync(4);
	clickWebElement("eBooking", "inbx_eBookingLogin;xpath", "Login Button", "eBooking");
	waitForSync(20);
	By b = getElement("ADC", "logo_lufthansa;xpath");
	boolean logo = driver.findElement(b).isDisplayed();
	if (logo)
		onPassUpdate("Lufthansa Cargo", "cargo Logo Displayed", "cargo Logo Displayed", "Login",
				"1. Open URL \n2. Enter Username, Password \n3. Click Login");
	else
		onFailUpdate("Lufthansa Cargo", "cargo Logo Displayed", "cargo Logo not Displayed", "Login",
				"1. Open URL \n2. Enter Username, Password \n3. Click Login");

}
public void logineBookingagain(String username, String password) throws InterruptedException, AWTException, IOException {

	waitForSync(5);
	By b1 = getElement("eBooking", "inbx_eBookingUN;name");
	boolean user = driver.findElement(b1).isDisplayed();
	if(user)
	{
	enterValueInTextbox("eBooking", "inbx_eBookingUN;name", username, "username", "eBooking");
	waitForSync(4);
	enterValueInTextbox("eBooking", "inbx_eBookingPWD;name", password, "password", "eBooking");
	waitForSync(4);
	clickWebElement("eBooking", "inbx_eBookingLogin;xpath", "Login Button", "eBooking");
	waitForSync(20);
	}
	else
	{
		System.out.println("User already logged in");
	}
	By b = getElement("ADC", "logo_lufthansa;xpath");
	boolean logo = driver.findElement(b).isDisplayed();
	if (logo)
		onPassUpdate("Lufthansa Cargo", "cargo Logo Displayed", "cargo Logo Displayed", "Login",
				"1. Open URL \n2. Enter Username, Password \n3. Click Login");
	else
		onFailUpdate("Lufthansa Cargo", "cargo Logo Displayed", "cargo Logo not Displayed", "Login",
				"1. Open URL \n2. Enter Username, Password \n3. Click Login");

}

public String  createHAWB(){
	Random r = new Random();
	int Result = r.nextInt(99 - 10) + 10;
	Integer randomNo = Result;
	String newHAWB = getRandomString().substring(0, 4).toUpperCase() + randomNo.toString();
		map.put("HAWB", newHAWB);
		return newHAWB;
}

/**
 * Description... Verify the flight is created for multi segment booking
 * @param prevFlightNo1
 * @param prevFlightNo2
 * @param currFlightNo1
 * @param currFlightNo2
 */
public void verifyFlightCreatedSoap(String prevFlightNo1,String prevFlightNo2,String currFlightNo1,String currFlightNo2) {
	if(!(prevFlightNo1.equals(currFlightNo1)&prevFlightNo2.equals(currFlightNo2)))
			{
		test.log(LogStatus.PASS, "Sucessfully created flight 1 " +currFlightNo1 );
		test.log(LogStatus.PASS, "Sucessfully created flight 2 " +currFlightNo2 );	
			}
			
else
	writeExtent("Fail", "Failed in flight creation");
	
}
/**
 * Description... Verify the flight is created for Single segment booking
 * @param prevFlightNo1
 * @param currFlightNo1
 */
public void verifyFlightCreatedSoapSingleSegment(String prevFlightNo1,String currFlightNo1) {
	if(!(prevFlightNo1.equals(currFlightNo1)))
			{
		test.log(LogStatus.PASS, "Sucessfully created flight 1 " +currFlightNo1 );
			}
			
else
{
	writeExtent("Fail", "Failed in flight creation");
	Assert.assertFalse(true,"Failed in flight creation");
}
	
}
/**
 * Description... Verify the Booking is created through Soap
 * @param prevAWBNo
 * @param newAWBNo
 */
public void  verifyBookingCreatedSoap(String prevAWBNo,String newAWBNo){
	if(!(prevAWBNo.equals(newAWBNo)))
			test.log(LogStatus.PASS, "Sucessfully created Booking " +newAWBNo );
	else
		{writeExtent("Fail", "Failed in Booking");
		Assert.assertFalse(true, "Failed in Booking");
		}
		
}



/*
*//**
	 * Description...Delete/Replace lines in file
	 * 
	 * @param filePath
	 * @param fileName
	 * @author A-6978
	 * @throws IOException 
	 *//*

	public void fileOperations(String file, String fileOps) {
		
		
		
		switch(fileOps){
		
		case "replace" : {
			 try {
			 Path path = Paths.get(file);
	            Stream <String> lines = Files.lines(path);
	            List <String> replaced = lines.map(line -> line.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&quot;", "\""))  
	            	.collect(Collectors.toList());
	            
	            Files.write(path, replaced);						
	            lines.close();
	            System.out.println("Find and Replace done!!!");
			 } catch (IOException e) {
		            e.printStackTrace();
		        }
			
		}
		
		case "delete" : {
			try{
			 File fileXCSN = new File(file);
	            List<String> out = Files.lines(fileXCSN.toPath())
                     .filter(line -> !(line.contains("ns2:iCargoBusinessHeader") || 
                     		line.contains("ibsplc.com")	 ||
                     		line.contains("purposeCode")	 ||
                     		line.contains("issueDateTime")	 ||
                     		line.contains("messageData")
                     		))
                     .collect(Collectors.toList());
 Files.write(fileXCSN.toPath(), out, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
            e.printStackTrace();
        }
			
		}
		
		case "default":
			break;
		}
	}

*/



public void selectValueInDropdownWithoutFail(String sheetName, String locator, String option, String eleName,
		String selectBy) {
	waitForSync(2);
	By ele = getElement(sheetName, locator);
	WebElement ele1 = driver.findElement(ele);
	Select select = new Select(ele1);
	try {
		

		switch (selectBy) {
		case "Value": {
			String actopt = select.getFirstSelectedOption().getText();
			if (!actopt.equalsIgnoreCase(option))
				select.selectByValue(option);

		}
			break;
		case "VisibleText": {
			String actopt = select.getFirstSelectedOption().getText();
			if (!actopt.equalsIgnoreCase(option))
				select.selectByVisibleText(option);

		}
			break;
		case "Index": {
			int index = Integer.parseInt(option);
			String actopt = select.getFirstSelectedOption().getText();
			if (!actopt.equalsIgnoreCase(option))
				select.selectByIndex(index);

		}
			break;

		}
		writeExtent("Pass", "Entered " + option + " as " + eleName + " on " + sheetName.split("_")[0] + " Screen");
		System.out.println("Entered " + option + " as " + eleName + " on " + sheetName.split("_")[0] + " Screen");

	} catch (Exception e) {
		
		
		
			select.selectByIndex(1);

	}
}

/**
	 * Description... Verify the Booking is created through Soap
	 * 
	 * @param prevAWBNo
	 * @param newAWBNo
	 */
	public void verifyBookingCreatedSoap() {
		String bookingStatus = getPropertyValue(globalVarPath, "BookingDone");
		
		
		if (bookingStatus.equalsIgnoreCase("yes")) {

			writeExtent("Pass", "Booking created successfully");

		} else {
			String bookingReasonCode = getPropertyValue(globalVarPath, "BookingFailureReason");
			
			writeExtent("Fail", "Failed in Booking due to " + bookingReasonCode );
			Assert.assertFalse(true, "Failed in Booking");
		}

	}

public void waitForSoapComplete(String cxmlVarPath) {
	int flag = 0;
	int i = 1;
	String soapStatus = WebFunctions.getPropertyValue(cxmlVarPath, "SoapTaskStatus");
	while (flag == 0) {
		if (soapStatus.equalsIgnoreCase("Yes")) {
			flag = 1;
		} else {
			waitForSync(4);
			soapStatus = WebFunctions.getPropertyValue(cxmlVarPath, "SoapTaskStatus");
			i++;
		}
		if (i == 30) {
			break;
		}
	}
}

/**
 * Description : To verify Custom bubble color
 * @param Colour : Expected color
 * @param screenName 
 * @throws Exception
 */
public void verifyCustomsBubbleColor(String Color, String screenName) throws Exception
{
	JavascriptExecutor js = (JavascriptExecutor) driver;
	WebElement value = (WebElement) js.executeScript("return document.getElementsByTagName('circle')[0]");
	moveScrollBar(value);
	String actResult = value.getAttribute("fill");
	
	switch (Color) {

	case "Red":
		if (actResult.equals("#ff0000"))
			onPassUpdate(screenName, "Custom bubble colour : red", "Custom bubble colour : red", "Customs bubble colour", "Verification of " + "Customs bubble colour");

		else
			onFailUpdate(screenName, "Custom bubble colour : red", "Custom bubble colour : Not red", "Customs bubble colour", "Verification of " + "Customs bubble colour");
		break;

	case "Green":
		
		if (actResult.equals("#008000"))
			onPassUpdate(screenName, "Custom bubble colour : green", "Custom bubble colour : green", "Customs bubble colour", "Verification of " + "Customs bubble colour");

		else
			onFailUpdate(screenName, "Custom bubble colour : green", "Custom bubble colour : Not green", "Customs bubble colour", "Verification of " + "Customs bubble colour");
		break;

	

	}
	
	
	
}





/**
 * Description : To verify Custom bubble color
 * @param Colour : Expected color
 * @param screenName 
 * @throws Exception
 */
public void verifyCustomsBubbleColor(String Color, String screenName, int[] circleNo) throws Exception
{
	JavascriptExecutor js = (JavascriptExecutor) driver;
	
	for(int i : circleNo){
	WebElement value = (WebElement) js.executeScript("return document.getElementsByTagName('circle')[" + i + "]");
	String actResult = value.getAttribute("fill");
	
	switch (Color) {

	case "Red":
		if (actResult.equals("#ff0000"))
			onPassUpdate(screenName, "Custom bubble colour : red", "Custom bubble colour : red", "Customs bubble colour", "Verification of " + "Customs bubble colour");

		else
			onFailUpdate(screenName, "Custom bubble colour : red", "Custom bubble colour : Not red", "Customs bubble colour", "Verification of " + "Customs bubble colour");
		break;

	case "Green":
		
		if (actResult.equals("#008000"))
			onPassUpdate(screenName, "Custom bubble colour : green", "Custom bubble colour : green", "Customs bubble colour", "Verification of " + "Customs bubble colour");

		else
			onFailUpdate(screenName, "Custom bubble colour : green", "Custom bubble colour : Not green", "Customs bubble colour", "Verification of " + "Customs bubble colour");
		break;

	

	}
	
	}
}




public void verify_tbl_records_multiple_cols(String sheetName, String locatortbody,String locatortheader, String tableTag, String verfColsNames[], String actVerfValues[], String pmyKey) {
	try {
		boolean flag = false;
		int row = 0;
		String ScreenName = sheetName.split("_")[0];
		
		// get the required column nos
		String tableHeaders = xls_Read.getCellValue(sheetName, locatortheader)+ "//td";
		List<WebElement> headers = driver.findElements(By.xpath(tableHeaders));
		int [] verfCols = new int[verfColsNames.length];
		
		for(int i=0 ;i<verfColsNames.length;i++ ){
			
			for(int j=0; j< headers.size(); j++){
				if((headers.get(j)).getText().equals(verfColsNames[i]))
					verfCols[i] = (j+1);
			}
			
		}	
		
		// get the required row
		String tableBody = xls_Read.getCellValue(sheetName, locatortbody);
		List<WebElement> rows = driver.findElements(By.xpath(xls_Read.getCellValue(sheetName, locatortbody)));
		String dynXpath = xls_Read.getCellValue(sheetName, locatortbody) + tableTag;

		System.out.println("row size  " + rows.size());
		switch (tableTag) {
		case "//input": {
			for (int i = 0; i < rows.size(); i++) {

				List<WebElement> cols = driver.findElements(By.xpath(dynXpath));

				for (int j = 0; j < cols.size(); j++) {

					System.out.println("col text = " + cols.get(j).getAttribute("value"));
					if (cols.get(j).getAttribute("value").contains(pmyKey)) {
						flag = true;
						break;

					}
				}
				if (flag) {
					row = i + 1;
					break;
				}
			}
			for (int i = 0; i < verfCols.length; i++) {
				dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
				WebElement ele = null;
				dynXpath = "(" + tableBody + ")[" + row + "]" + "//td[" + verfCols[i] + "]//input";
				ele = driver.findElement(By.xpath(dynXpath));

				String actual = ele.getText().toLowerCase().replace(" ", "");
				String a1 = ele.getAttribute("value");
				if (actual.length() == 0)
					actual = a1.toLowerCase();
				String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
				if (actual.equals(expected)) {
					System.out.println("found true for " + actVerfValues[i]);

					onPassUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
							"Table verification");

				} else {
					onFailUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
							"Table verification");

				}
			}
		}
			break;
		case "//td":

		{

			rows = driver.findElements(By.xpath(tableBody));
			dynXpath = tableBody + tableTag;
			{
				for (int i = 0; i <= rows.size(); i++) {
					System.out.println("i= " + i);

					if (rows.get(i).getText().toLowerCase().replace(" ", "")
							.contains(pmyKey.toLowerCase().replace(" ", ""))) {

						flag = true;

					}

					if (flag) {
						row = i + 1;
						break;
					}
				}

				System.out.println("row = " + row);
				for (int i = 0; i < verfCols.length; i++) {

					dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
					WebElement ele = null;

					ele = driver.findElement(By.xpath(dynXpath));

					String actual = ele.getText().toLowerCase().replace(" ", "");
					String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
					if (expected.contains(actual)) {
						System.out.println("found true for " + actVerfValues[i]);

						onPassUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					} else {
						onFailUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
								"Table verification");

					}

				}

			}
		}
			break;

		case "//div":

		{
			for (int i = 0; i <= rows.size(); i++) {
				System.out.println("i= " + i);

				if (rows.get(i).getText().toLowerCase().replace(" ", "")
						.contains(pmyKey.toLowerCase().replace(" ", ""))) {

					flag = true;

				}

				if (flag) {
					row = i + 1;
					break;
				}
			}
		}
			System.out.println("row = " + row);
			for (int i = 0; i < verfCols.length; i++) {

				dynXpath = "(" + tableBody + ")[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
				WebElement ele = null;

				ele = driver.findElement(By.xpath(dynXpath));

				String actual = ele.getText().toLowerCase().replace(" ", "");
				String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
				if (actual.equals(expected)) {
					System.out.println("found true for " + actVerfValues[i]);

					onPassUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
							"Table verification");

				} else {
					onFailUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
							"Table verification");

				}

				break;
			}
		case "input": {

			String Xpath = xls_Read.getCellValue(sheetName, locatortbody);
			List<WebElement> rows1 = driver.findElements(By.xpath(Xpath));
			for (int i = 0; i < rows1.size(); i++) {

				String dynxpath = "(" + Xpath + ")[" + (i + 1) + "]//input";
				List<WebElement> cols = driver.findElements(By.xpath(dynxpath));

				for (int j = 0; j < cols.size(); j++) {

					System.out.println("col text = " + cols.get(j).getAttribute("value"));
					if (cols.get(j).getAttribute("value").contains(pmyKey)) {
						flag = true;
						break;
					}
				}
				if (flag) {
					row = i + 1;
					break;
				}
			}
			for (int i = 0; i < verfCols.length; i++) {
				dynXpath = "(" + tableBody + ")[" + row + "]//" + tableTag + "[" + verfCols[i] + "]";
				WebElement ele = null;
				dynXpath = "(" + tableBody + ")[" + row + "]" + "//td[" + verfCols[i] + "]//input";
				ele = driver.findElement(By.xpath(dynXpath));

				String actual = ele.getText().toLowerCase().replace(" ", "");
				String a1 = ele.getAttribute("value");
				if (actual.length() == 0)
					actual = a1.toLowerCase();
				String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
				if (actual.equals(expected)) {
					System.out.println("found true for " + actVerfValues[i]);

					onPassUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
							"Table verification");

				} else {
					onFailUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
							"Table verification");

				}
			}
		}
			break;
		case "//label": {

			String Xpath = xls_Read.getCellValue(sheetName, locatortbody);
			List<WebElement> rows1 = driver.findElements(By.xpath(Xpath));
			for (int i = 0; i < rows1.size(); i++) {

				String dynxpath = "(" + Xpath + "[" + (i + 1) + "])//label";
				List<WebElement> cols = driver.findElements(By.xpath(dynxpath));

				for (int j = 0; j < cols.size(); j++) {

					System.out.println("col text = " + cols.get(j).getText());
					if (cols.get(j).getText().contains(pmyKey)) {
						flag = true;
						break;
					}
				}
				if (flag) {
					row = i + 1;
					break;
				}
			}
			for (int i = 0; i < verfCols.length; i++) {
				dynXpath = tableBody + "[" + row + "]" + tableTag + "[" + verfCols[i] + "]";
				WebElement ele = null;
				dynXpath = tableBody + "[" + row + "]" + "//td[" + verfCols[i] + "]//label";
				ele = driver.findElement(By.xpath(dynXpath));

				String actual = ele.getText().toLowerCase().replace(" ", "");
				String a1 = ele.getText();
				if (actual.length() == 0)
					actual = a1.toLowerCase();
				String expected = (actVerfValues[i].replace(" ", "").toLowerCase());
				if (actual.equals(expected)) {
					System.out.println("found true for " + actVerfValues[i]);

					onPassUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
							"Table verification");

				} else {
					onFailUpdate(ScreenName, expected, actual, "Table verification against " + pmyKey + " On ",
							"Table verification");

				}
			}
		}
			break;

		}

	} catch (Exception e) {
		retryCount = retryCount + 1;

		if (retryCount <= 3) {
			verify_tbl_records_multiple_cols(sheetName, locatortbody,locatortheader, tableTag,  verfColsNames,  actVerfValues,  pmyKey);
		}

		else {

			test.log(LogStatus.FAIL, "Could not perform table record verification");
			System.out.println("Table contents are not verified or verification failed");
			Assert.assertFalse(true, "Could not perform table record verification");
		}

	}
	
}
/**
* Desc : Click back in android
* @author A-9478
* @throws AWTException
* @throws InterruptedException
* @throws IOException 
 */
public void clickBack(String ScreenName) throws AWTException, InterruptedException, IOException
{
      try
      {
            String locatorValue=getPropertyValue(proppathhht, "btn_clickBack;xpath");
            locatorValue=locatorValue.replace("SCREEN", ScreenName);
            androiddriver.findElement(By.xpath(locatorValue)).click(); 
            waitForSync(3);
            writeExtent("Pass", "Clicked back in "+ScreenName+" screen");
      }
      catch(Exception e)
      {
            writeExtent("Fail", "Couldn't click back in "+ScreenName+" screen");
      }
      }

/**
* Description... Get toast message
* 
 */
public String getToastMessage(){
      waitForSync(2);
      
      By b = getElement("Generic_Elements", "txt_toastMessage;xpath");
      WebDriverWait wait = new WebDriverWait(driver, 30);
      wait.until(ExpectedConditions.visibilityOfElementLocated(b));
      ele = driver.findElement(b);
      
      String toast= ele.getText();
      System.out.println("toast Message=>"+toast);
      
      if(!toast.isEmpty()){
            writeExtent("Pass", "Toast message: "+toast);
      }else{
            writeExtent("Fail", "Toast message is not available");
      }
    return toast;

}


}