package common;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFCellUtil;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.formula.functions.Hyperlink;
import org.apache.poi.ss.usermodel.Hyperlink.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import controls.CustomTestReport;

/**
 * Description : Functional Test Script
 * 
 * @author A-5835
 */

public class ExcelReadWrite {
	/**
	 * Script Name : <b>ExcelReadWrite</b> Generated : <b>Mar 12, 2013 1:12:30
	 * PM</b> Description : Functional Test Script Original Host : WinNT Version
	 * 6.1 Build 7600 ()
	 * 
	 * @since 2013/03/12
	 * @author Saroj Gouda
	 */
	public CommonUtility commonUtility;
	String callingClassName = null;
	public CustomTestReport customTestReport;
	WebDriver driver;
	String browserName;
	String failedTestImage;
	String passedTestImage;
	ArrayList<String>  browserName1;
	public String fileName1 = null;
	String s2 = System.getProperty("user.dir");
	String path = s2 + "\\src\\resources\\log";
	String xls_output_folder = s2 + "\\reports\\xls";
	
	String NO_RESULTS = "No results found";
	String screenShotPath;
	String testStartTime;
public WebFunctions com=new WebFunctions();
	public String fileName = null;

	/**
	 * ExcelReadWrite constructor ..
	 * 
	 * @author
	 * @param callingClass
	 *            - This name is used for creating the xls file
	 * @param driver
	 *            - webdriver instance
	 * @param browser
	 *            - this is used for taking screenshot
	 * @param screenShotPath
	 *            - Location in which screenshots will be saved
	 * @return null.
	 */
	public ExcelReadWrite(String callingClass, WebDriver driver,
			String browser, String screenShotPath) {
		commonUtility = new CommonUtility();
		callingClassName = callingClass;
		fileName = createSheetWithHeaders(callingClass);
		// customTestReport = new CustomTestReport();
		this.driver = driver;
		this.browserName = browser;
		this.screenShotPath = screenShotPath;
		testStartTime = commonUtility.getcurrentDateTime();
	}
	/**
	 * Constructor,for custom report
	 * 
	 * @author:Deepti Shikha Padhee
	 * @param callingClass
	 *            - This name is used for creating the xls file
	 * @return null.
	 */
	public ExcelReadWrite(String callingClass) {
		commonUtility = new CommonUtility();
		callingClassName = callingClass;
		fileName = createSheetWithHeadersforCustomeReport(callingClass);
		// customTestReport = new CustomTestReport();
		//this.driver = driver;
		//this.browserName1 = BrowserName;
		
		testStartTime = commonUtility.getcurrentDateTime();
	}
	public String createSheetWithHeadersforCustomeReport(String excelfilename) {


		fileName1 = xls_output_folder + "\\" + excelfilename + ".xls";
		testStartTime = commonUtility.getcurrentDateTime();
		HSSFWorkbook workBook = new HSSFWorkbook();
		HSSFSheet sheet = workBook.createSheet("ExecutionReport");

		int rownum = 0;
		HSSFRow row = sheet.createRow(rownum);
		HSSFCell cell = row.createCell(0);

		// Formating Cell Style
		HSSFCellStyle cellStyle = workBook.createCellStyle();
		cellStyle.setBorderTop((short) 1);
		cellStyle.setBorderBottom((short) 1);
		cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		// Formating Cell Font
		HSSFFont font = workBook.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setColor(HSSFColor.BLACK.index);
		cellStyle.setFont(font);

		HSSFRow TitleRow = sheet.createRow(2);
		cell = TitleRow.createCell(1);
		cell.setCellValue("Report:" + excelfilename);

		cell = TitleRow.createCell(5);
		cell.setCellValue("Test start time : " + testStartTime);

		row = sheet.createRow(4);
		
		cell = row.createCell(1);
		cell.setCellValue("ScriptName");
		cell.setCellStyle(cellStyle);

		sheet.setColumnWidth(1, 8000);
		cell = row.createCell(2);
		cell.setCellValue("Status");
		cell.setCellStyle(cellStyle);

		sheet.setColumnWidth(2, 6000);


		// cell = row.createCell(5);
		// cell.setCellValue("ScreenShot");
		// cell.setCellStyle(cellStyle);

		

		try {
			
			// this.fileName = fileName1;
			File fileToWrite = new File(fileName1);
			Path path = fileToWrite.toPath();
			// Files fileToCreate = new Files();
			if (Files.notExists(path, LinkOption.NOFOLLOW_LINKS)) {
				FileOutputStream out = new FileOutputStream(new File(fileName1));
				workBook.write(out);
				out.close();
				System.out.println("Excel written successfully..");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileName1;

		

	}

	public void insertDataCustomeReport(String className, String res) {

		try {
			/*
			 * fileName1 = xls_output_folder+""+testcaseID+".xls"; this.fileName
			 * = fileName1;
			 */
			String result = "result not defined";
			CreateWorkbook(fileName1);
			FileInputStream file = new FileInputStream(new File(fileName1));
			HSSFWorkbook workbook = new HSSFWorkbook(file);
			HSSFSheet sheet = workbook.getSheetAt(0);
			CreationHelper createHelper = workbook.getCreationHelper();
			CellStyle hlink_style = workbook.createCellStyle();

			CellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setWrapText(true);

			org.apache.poi.ss.usermodel.Hyperlink link = createHelper
					.createHyperlink(org.apache.poi.ss.usermodel.Hyperlink.LINK_FILE);

			HSSFCell cell = null;
			HSSFRow row = null;

			// Update the value of cell
			int lastRow = sheet.getLastRowNum();
			row = sheet.createRow(++lastRow);

			// cell = row.createCell(0);
			// cell.setCellValue(testcaseID);

			

				cell = row.createCell(1);
				cell.setCellValue(className);
				cell.setCellStyle(cellStyle);
				cell = row.createCell(2);
				cell.setCellValue(res);
				cell.setCellStyle(cellStyle);
			// Formating Cell Style
			HSSFCellStyle resultStyle = workbook.createCellStyle();

			if (res.equals("PASS")) {
				result = "Pass";
				resultStyle
						.setFillForegroundColor(HSSFColor.BRIGHT_GREEN.index);
				resultStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

			} else if (res.equals("FAIL")) {
				result = "Fail";
				resultStyle.setFillForegroundColor(HSSFColor.ORANGE.index);
				resultStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			} else {
				result = "SKIP";
				resultStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
				resultStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			}

			cell.setCellValue(result);
			cell.setCellStyle(resultStyle);

			file.close();

			FileOutputStream outFile = new FileOutputStream(new File(fileName1));
			workbook.write(outFile);
			outFile.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	/**
	 * Constructor, to ensure backward compatibility.
	 * 
	 * @author
	 * @param
	 * @return null.
	 */
	public ExcelReadWrite() {
		commonUtility = new CommonUtility();
	}

	/**
	 * Create new file with headers.
	 * 
	 * @author
	 * @param excelfilename
	 *            - File name to create
	 * @return name of file created.
	 */
	public String createSheetWithHeaders(String excelfilename) {

		fileName1 = xls_output_folder + "\\" + excelfilename + ".xls";
		testStartTime = commonUtility.getcurrentDateTime();
		HSSFWorkbook workBook = new HSSFWorkbook();
		HSSFSheet sheet = workBook.createSheet("Execution Report");

		int rownum = 0;
		HSSFRow row = sheet.createRow(rownum);
		HSSFCell cell = row.createCell(0);

		// Formating Cell Style
		HSSFCellStyle cellStyle = workBook.createCellStyle();
		cellStyle.setBorderTop((short) 1);
		cellStyle.setBorderBottom((short) 1);
		cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		// Formating Cell Font
		HSSFFont font = workBook.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setColor(HSSFColor.BLACK.index);
		cellStyle.setFont(font);

		HSSFRow TitleRow = sheet.createRow(2);
		cell = TitleRow.createCell(1);
		cell.setCellValue("Test Name :" + excelfilename);

		cell = TitleRow.createCell(5);
		cell.setCellValue("Test start time : " + testStartTime);

		row = sheet.createRow(4);

		cell = row.createCell(1);
		cell.setCellValue("Step No:");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(2);
		cell.setCellValue("Test Script Name");
		cell.setCellStyle(cellStyle);
		sheet.setColumnWidth(2, 6000);

		cell = row.createCell(3);
		cell.setCellValue("Step Description");
		cell.setCellStyle(cellStyle);
		sheet.setColumnWidth(3, 6000);

		cell = row.createCell(4);
		cell.setCellValue("Message");
		cell.setCellStyle(cellStyle);
		sheet.setColumnWidth(4, 6000);

		cell = row.createCell(5);
		cell.setCellValue("Result (Pass/Fail)");
		cell.setCellStyle(cellStyle);

		// cell = row.createCell(5);
		// cell.setCellValue("ScreenShot");
		// cell.setCellStyle(cellStyle);

		cell = row.createCell(6);
		cell.setCellValue("Screenshot");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(7);
		cell.setCellValue("Actual Result");
		cell.setCellStyle(cellStyle);
		sheet.setColumnWidth(7, 6000);

		cell = row.createCell(8);
		cell.setCellValue("Expected Result");
		cell.setCellStyle(cellStyle);
		sheet.setColumnWidth(8, 6000);

		try {
			// #Moshe : Only if a file is not existing , new file will be
			// created
			// this.fileName = fileName1;
			File fileToWrite = new File(fileName1);
			Path path = fileToWrite.toPath();
			// Files fileToCreate = new Files();
			if (Files.notExists(path, LinkOption.NOFOLLOW_LINKS)) {
				FileOutputStream out = new FileOutputStream(new File(fileName1));
				workBook.write(out);
				out.close();
				System.out.println("Excel written successfully..");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileName1;

	}

	/**
	 * Create new file stream.
	 * 
	 * @author
	 * @param filename1
	 *            - File name to create
	 * @return void.
	 */
	public void CreateWorkbook(String filename1) {
		try {
			FileInputStream file = new FileInputStream(new File(fileName1));
		} catch (FileNotFoundException e) {
			String errorMessage = e.getMessage().toUpperCase();
		}
	}

	/**
	 * insertData - write a row to excel file. time stamp added automatically
	 * 
	 * @author
	 * @param moduleName
	 *            - Module name
	 * @param stepDescription
	 *            - Steps to execute the test case
	 * @param message
	 *            - Message
	 * @param logStatus
	 *            - based on this value , pass or fail is decided
	 * @param remarks
	 *            - Remarks
	 * @param Actual
	 *            value
	 * @param ExpectedValue
	 * @return void.
	 */
	public void insertData(String moduleName, String stepDescription,
			String message, boolean logStatus, String remarks,
			String ActualValue, String ExpectedValue) {
		String timeStamp = commonUtility.getcurrentDateTime();
		insertData(callingClassName, timeStamp, moduleName, stepDescription,
				message, logStatus, remarks, ActualValue, ExpectedValue);
	}

	/**
	 * insertData - write a row to excel file No need to pass boolean
	 * true/false. Comparison of expected and Actual is done and based on that
	 * pass/fail is decided time stamp/counter added automatically
	 * 
	 * @author
	 * @param moduleName
	 *            - Module name
	 * @param stepDescription
	 *            - Steps to execute the test case
	 * @param message
	 *            - Message
	 * @param remarks
	 *            - Remarks
	 * @param Actual
	 *            value
	 * @param ExpectedValue
	 * @return void.
	 */
	public void insertData(String moduleName, String stepDescription,
			String message, String remarks, String ActualValue,
			String ExpectedValue) {
		String timeStamp = commonUtility.getcurrentDateTime();
		boolean result = false;
		if (ActualValue.equals(ExpectedValue))
			result = true;

		insertData(callingClassName, timeStamp, moduleName, stepDescription,
				message, result, remarks, ActualValue, ExpectedValue);
	}

	/**
	 * insertData - write a row to excel file.
	 * 
	 * @author
	 * @param testcaseID
	 *            - Not used, kept for backward compatibility
	 * @param stepNo
	 * @param moduleName
	 *            - Module name
	 * @param stepDescription
	 *            - Steps to execute the test case
	 * @param message
	 *            - Message
	 * @param logStatus
	 *            - based on this value , pass or fail is decided
	 * @param remarks
	 *            - Remarks
	 * @param Actual
	 *            value
	 * @param ExpectedValue
	 * @return void.
	 */
	public void insertData(String testcaseID, String stepNo, String moduleName,
			String stepDescription, String message, boolean j, String remarks,
			String ActualValue, String ExpectedValue) {

		try {
			/*fileName1 = xls_output_folder+""+testcaseID+".xls";
			this.fileName = fileName1;*/
			String result = "result not defined";
			CreateWorkbook(fileName1);
			FileInputStream file = new FileInputStream(new File(fileName1));
			HSSFWorkbook workbook = new HSSFWorkbook(file);
			HSSFSheet sheet = workbook.getSheetAt(0);
			CreationHelper createHelper = workbook.getCreationHelper();
			CellStyle hlink_style = workbook.createCellStyle();

			CellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setWrapText(true);

			org.apache.poi.ss.usermodel.Hyperlink link = createHelper
					.createHyperlink(org.apache.poi.ss.usermodel.Hyperlink.LINK_FILE);

			HSSFCell cell = null;
			HSSFRow row = null;

			// Update the value of cell
			int lastRow = sheet.getLastRowNum();
			row = sheet.createRow(++lastRow);

			// cell = row.createCell(0);
			// cell.setCellValue(testcaseID);

			cell = row.createCell(1);
			cell.setCellValue(stepNo);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(2);
			cell.setCellValue(moduleName);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(3);
			cell.setCellValue(stepDescription);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(4);
			cell.setCellValue(message);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(5);
			cell.setCellValue(j);
			// Formating Cell Style
			HSSFCellStyle resultStyle = workbook.createCellStyle();

			if (j) {
				result = "Pass";
				resultStyle
						.setFillForegroundColor(HSSFColor.BRIGHT_GREEN.index);
				resultStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

			} else {
				result = "Fail";
				resultStyle.setFillForegroundColor(HSSFColor.ORANGE.index);
				resultStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			}

			if (remarks.equals(NO_RESULTS)) {
				resultStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
			}

			cell.setCellValue(result);
			cell.setCellStyle(resultStyle);

			// Creating hyper link cell 6

			Font hlink_font = workbook.createFont();
			hlink_font.setUnderline(Font.U_SINGLE);
			hlink_font.setColor(IndexedColors.BLUE.getIndex());
			hlink_style.setFont(hlink_font);
			String s2 = System.getProperty("user.dir");
	        String path1 = s2 + "\\src\\resources\\GlobalVariable.properties";
			/*if (com.getPropertyValue(path1,"TCScreenShotFlag").equalsIgnoreCase("yes")) {
				passedTestImage = commonUtility.getScreenShot(driver,
						browserName, callingClassName, screenShotPath);

				link.setAddress(passedTestImage);

				cell = row.createCell(6);
				cell.setHyperlink(link);
				cell.setCellStyle(hlink_style);

				// cell 6
				// cell = row.createCell(6);
				cell.setCellValue("Screen Shot");

			} else*/ {
				
				cell = row.createCell(6);

				cell.setHyperlink(link);
				cell.setCellStyle(hlink_style);

				// cell 6
				cell = row.createCell(6);
				cell.setCellValue(remarks);
			}
			cell = row.createCell(7);
			cell.setCellValue(ActualValue);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(8);
			cell.setCellValue(ExpectedValue);
			cell.setCellStyle(cellStyle);
			file.close();

			FileOutputStream outFile = new FileOutputStream(new File(fileName1));
			workbook.write(outFile);
			outFile.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	/**
	 * insertFailedData - write a row to excel file (to write failed entry). But
	 * still we need to pass boolean false as parameter. time stamp/counter
	 * added automatically
	 * 
	 * @author Moshe George
	 * @param moduleName
	 *            - Module name
	 * @param stepDescription
	 *            - Steps to execute the test case
	 * @param message
	 *            - Message
	 * @param logStatus
	 *            - based on this value , pass or fail is decided
	 * @param remarks
	 *            - Remarks
	 * @param Actual
	 *            value
	 * @param ExpectedValue
	 * @return void.
	 */
	public void insertFailedData(String moduleName, String stepDescription,
			String message, boolean logStatus, String remarks,
			String ActualValue, String ExpectedValue) {
		// Moshe: This will help pages to log data.

		String timeStamp = commonUtility.getcurrentDateTime();
		insertData(callingClassName, timeStamp, moduleName, stepDescription,
				message, logStatus, remarks, ActualValue, ExpectedValue);
	}

	/**
	 * insertFailedData - write a row to excel file (to write failed entry). No
	 * need to pass "false" parameter time stamp/counter added automatically
	 * 
	 * @author Moshe George
	 * @param moduleName
	 *            - Module name
	 * @param stepDescription
	 *            - Steps to execute the test case
	 * @param message
	 *            - Message
	 * @param logStatus
	 *            - based on this value , pass or fail is decided
	 * @param remarks
	 *            - Remarks
	 * @param Actual
	 *            value
	 * @param ExpectedValue
	 * @return void.
	 */
	public void insertFailedData(String moduleName, String stepDescription,
			String message, String remarks, String ActualValue,
			String ExpectedValue) {

		String timeStamp = commonUtility.getcurrentDateTime();
		insertData(callingClassName, timeStamp, moduleName, stepDescription,
				message, false, remarks, ActualValue, ExpectedValue);
	}

	/**
	 * insertPassData - write a row to excel file (to write failed entry). But
	 * still we need to pass boolean true as parameter. time stamp/counter added
	 * automatically
	 * 
	 * @author Moshe George
	 * @param moduleName
	 *            - Module name
	 * @param stepDescription
	 *            - Steps to execute the test case
	 * @param message
	 *            - Message
	 * @param remarks
	 *            - Remarks
	 * @param Actual
	 *            value
	 * @param ExpectedValue
	 * @return void.
	 */
	public void insertPassData(String moduleName, String stepDescription,
			String message, String remarks, String ActualValue,
			String ExpectedValue) {
		String timeStamp = commonUtility.getcurrentDateTime();
		insertData(callingClassName, timeStamp, moduleName, stepDescription,
				message, true, remarks, ActualValue, ExpectedValue);
	}

	/**
	 * insertFailedData - write a row to excel file (to write failed entry). But
	 * still we need to pass boolean false as parameter. time stamp/counter
	 * added automatically
	 * 
	 * @author Moshe George
	 * @param testcaseID
	 *            - Not used, kept for backward compatibility
	 * @param stepNo
	 * @param moduleName
	 *            - Module name
	 * @param stepDescription
	 *            - Steps to execute the test case
	 * @param message
	 *            - Message
	 * @param logStatus
	 *            - based on this value , pass or fail is decided
	 * @param remarks
	 *            - Remarks
	 * @param Actual
	 *            value
	 * @param ExpectedValue
	 * @return void.
	 */
	public void insertFailedData(String testcaseID, String stepNo,
			String moduleName, String stepDescription, String message,
			boolean j, String remarks, String ActualValue, String ExpectedValue) {

		try {
			/* Code to get image path */
			failedTestImage = commonUtility.getScreenShot(driver, browserName,
					callingClassName, screenShotPath);

			// fileName1 = xls_output_folder + "\\" + fileName + ".xls";
			fileName1 = fileName;
			// this.fileName = fileName1;
			String result = "result not defined";
			CreateWorkbook(fileName1);
			FileInputStream file = new FileInputStream(new File(fileName1));
			HSSFWorkbook workbook = new HSSFWorkbook(file);
			HSSFSheet sheet = workbook.getSheetAt(0);
			CreationHelper createHelper = workbook.getCreationHelper();
			CellStyle hlink_style = workbook.createCellStyle();
			CellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setWrapText(true);
			org.apache.poi.ss.usermodel.Hyperlink link = createHelper
					.createHyperlink(org.apache.poi.ss.usermodel.Hyperlink.LINK_FILE);

			link.setAddress(failedTestImage);
			HSSFCell cell = null;
			HSSFRow row = null;

			// Update the value of cell
			int lastRow = sheet.getLastRowNum();
			row = sheet.createRow(++lastRow);

			// cell = row.createCell(0);
			// cell.setCellValue(testcaseID);

			cell = row.createCell(1);
			cell.setCellValue(stepNo);

			cell = row.createCell(2);
			cell.setCellValue(moduleName);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(3);
			cell.setCellValue(stepDescription);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(4);
			cell.setCellValue(message);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(5);
			cell.setCellValue(j);
			// Formating Cell Style
			HSSFCellStyle resultStyle = workbook.createCellStyle();

			/*
			 * if (j) { result = "Pass"; resultStyle
			 * .setFillForegroundColor(HSSFColor.BRIGHT_GREEN.index);
			 * resultStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			 * 
			 * } else { result = "Fail";
			 * resultStyle.setFillForegroundColor(HSSFColor.ORANGE.index);
			 * resultStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); }
			 * 
			 * if (remarks.equals(NO_RESULTS)) {
			 * resultStyle.setFillForegroundColor(HSSFColor.YELLOW.index); }
			 */
			// result was not defined
			result = "Fail";
			resultStyle.setFillForegroundColor(HSSFColor.ORANGE.index);
			resultStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

			cell.setCellValue(result);
			cell.setCellStyle(resultStyle);

			// Creating hyper link cell 6

			Font hlink_font = workbook.createFont();
			hlink_font.setUnderline(Font.U_SINGLE);
			hlink_font.setColor(IndexedColors.BLUE.getIndex());
			hlink_style.setFont(hlink_font);

			cell = row.createCell(6);

			cell.setHyperlink(link);

			cell.setCellStyle(hlink_style);

			// cell 6
			// cell = row.createCell(6);
			cell.setCellValue("Screen Shot");

			cell = row.createCell(7);
			cell.setCellValue(ActualValue);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(8);
			cell.setCellValue(ExpectedValue);
			cell.setCellStyle(cellStyle);

			file.close();

			FileOutputStream outFile = new FileOutputStream(new File(fileName1));
			workbook.write(outFile);
			outFile.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
	
	/**
	 * insertWarningData - write a row to excel file (to write Warning entry). But
	 * still we need to pass boolean Warning as parameter. time stamp/counter
	 * added automatically
	 * 
	 * @author A-5874 Arjun B M
	 * @param testcaseID
	 *            - Not used, kept for backward compatibility
	 * @param stepNo
	 * @param moduleName
	 *            - Module name
	 * @param stepDescription
	 *            - Steps to execute the test case
	 * @param message
	 *            - Message
	 * @param logStatus
	 *            - based on this value , pass or fail is decided
	 * @param remarks
	 *            - Remarks
	 * @param Actual
	 *            value
	 * @param ExpectedValue
	 * @return void.
	 */
	public void insertWarningData(String testcaseID, String stepNo,
			String moduleName, String stepDescription, String message,
			boolean j, String remarks, String ActualValue, String ExpectedValue) {

		try {
			/* Code to get image path */
			failedTestImage = commonUtility.getScreenShot(driver, browserName,
					callingClassName, screenShotPath);

			// fileName1 = xls_output_folder + "\\" + fileName + ".xls";
			fileName1 = fileName;
			// this.fileName = fileName1;
			String result = "result not defined";
			CreateWorkbook(fileName1);
			FileInputStream file = new FileInputStream(new File(fileName1));
			HSSFWorkbook workbook = new HSSFWorkbook(file);
			HSSFSheet sheet = workbook.getSheetAt(0);
			CreationHelper createHelper = workbook.getCreationHelper();
			CellStyle hlink_style = workbook.createCellStyle();
			CellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setWrapText(true);
			org.apache.poi.ss.usermodel.Hyperlink link = createHelper
					.createHyperlink(org.apache.poi.ss.usermodel.Hyperlink.LINK_FILE);

			link.setAddress(failedTestImage);
			HSSFCell cell = null;
			HSSFRow row = null;

			// Update the value of cell
			int lastRow = sheet.getLastRowNum();
			row = sheet.createRow(++lastRow);

			// cell = row.createCell(0);
			// cell.setCellValue(testcaseID);

			cell = row.createCell(1);
			cell.setCellValue(stepNo);

			cell = row.createCell(2);
			cell.setCellValue(moduleName);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(3);
			cell.setCellValue(stepDescription);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(4);
			cell.setCellValue(message);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(5);
			cell.setCellValue(j);
			// Formating Cell Style
			HSSFCellStyle resultStyle = workbook.createCellStyle();

			/*
			 * if (j) { result = "Pass"; resultStyle
			 * .setFillForegroundColor(HSSFColor.BRIGHT_GREEN.index);
			 * resultStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			 * 
			 * } else { result = "Fail";
			 * resultStyle.setFillForegroundColor(HSSFColor.ORANGE.index);
			 * resultStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); }
			 * 
			 * if (remarks.equals(NO_RESULTS)) {
			 * resultStyle.setFillForegroundColor(HSSFColor.YELLOW.index); }
			 */
			// result was not defined
			result = "Warning";
			resultStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
			resultStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

			cell.setCellValue(result);
			cell.setCellStyle(resultStyle);

			// Creating hyper link cell 6

			Font hlink_font = workbook.createFont();
			hlink_font.setUnderline(Font.U_SINGLE);
			hlink_font.setColor(IndexedColors.BLUE.getIndex());
			hlink_style.setFont(hlink_font);

			cell = row.createCell(6);

			cell.setHyperlink(link);

			cell.setCellStyle(hlink_style);

			// cell 6
			// cell = row.createCell(6);
			cell.setCellValue("Screen Shot");

			cell = row.createCell(7);
			cell.setCellValue(ActualValue);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(8);
			cell.setCellValue(ExpectedValue);
			cell.setCellStyle(cellStyle);

			file.close();

			FileOutputStream outFile = new FileOutputStream(new File(fileName1));
			workbook.write(outFile);
			outFile.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}


	public void insertRowsBetweenTests(String excelfilename) {

		try {
			fileName1 = xls_output_folder + "\\" + excelfilename + ".xls";
			this.fileName = fileName1;
			CreateWorkbook(fileName1);
			FileInputStream file = new FileInputStream(new File(fileName1));
			HSSFWorkbook workbook = new HSSFWorkbook(file);
			HSSFSheet sheet = workbook.getSheetAt(0);
			// int rows=sheet.getLastRowNum();
			int lastRow = sheet.getLastRowNum();
			// HSSFRow row = sheet.createRow(lastRow);
			// HSSFCell cell = row.createCell(0);
			// cell.setCellValue("Sushanth");
			sheet.getRepeatingRows();
			sheet.createRow(lastRow + 5);
			file.close();

			FileOutputStream outFile = new FileOutputStream(new File(fileName1));
			workbook.write(outFile);
			outFile.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	/**
	 * Method to create new excel report
	 * 
	 * @param excelfilename
	 */
	public void createExcelReport(String excelfilename) {

		fileName1 = xls_output_folder + "\\" + excelfilename + ".xls";

		HSSFWorkbook workBook = new HSSFWorkbook();
		HSSFSheet sheet = workBook.createSheet("Execution Report");

		try {
			this.fileName = fileName1;
			FileOutputStream out = new FileOutputStream(new File(fileName1));
			workBook.write(out);
			out.close();
			System.out.println("Excel written successfully..");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Method to Create header for report Used for Tests which is having
	 * multiple test scenarios
	 * 
	 * @param excelfilename
	 * @param serialNumber
	 */
	public void insertReportHeader(String excelfilename, String serialNumber) {

		try {
			fileName1 = xls_output_folder + "\\" + excelfilename + ".xls";
			CreateWorkbook(fileName1);
			FileInputStream file;

			file = new FileInputStream(new File(fileName1));

			HSSFWorkbook workbook = new HSSFWorkbook(file);
			HSSFSheet sheet = workbook.getSheetAt(0);
			int lastRow;
			if (sheet.getLastRowNum() != 0) {
				lastRow = sheet.getLastRowNum() + 5;
			} else {
				lastRow = 1;
			}
			HSSFRow row = sheet.createRow(lastRow);
			HSSFCell cell = row.createCell(0);

			// Formating Cell Style
			HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setBorderTop((short) 1);
			cellStyle.setBorderBottom((short) 1);
			cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

			// Formating Cell Font
			HSSFFont font = workbook.createFont();
			font.setFontHeightInPoints((short) 10);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			font.setColor(HSSFColor.BLACK.index);
			cellStyle.setFont(font);

			cell = row.createCell(0);
			cell.setCellValue("Test : " + serialNumber);

			cell = row.createCell(1);
			cell.setCellValue("Step No:");
			cell.setCellStyle(cellStyle);

			cell = row.createCell(2);
			cell.setCellValue("Test Script Name");
			cell.setCellStyle(cellStyle);

			cell = row.createCell(3);
			cell.setCellValue("Step Description");
			cell.setCellStyle(cellStyle);

			cell = row.createCell(4);
			cell.setCellValue("Message");
			cell.setCellStyle(cellStyle);

			cell = row.createCell(5);
			cell.setCellValue("Result (Pass/Fail)");
			cell.setCellStyle(cellStyle);

			cell = row.createCell(6);
			cell.setCellValue("Remarks");
			cell.setCellStyle(cellStyle);

			cell = row.createCell(7);
			cell.setCellValue("Expected Result");
			cell.setCellStyle(cellStyle);

			cell = row.createCell(8);
			cell.setCellValue("Actual Result");
			cell.setCellStyle(cellStyle);

			this.fileName = fileName1;
			FileOutputStream out = new FileOutputStream(new File(fileName1));
			workbook.write(out);
			out.close();
			System.out.println("Excel written successfully..");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	/**
	 * insertData - write a row to excel file.
	 * 
	 * @author
	 * @param testcaseID
	 *            - Not used, kept for backward compatibility
	 * @param stepNo
	 * @param moduleName
	 *            - Module name
	 * @param stepDescription
	 *            - Steps to execute the test case
	 * @param message
	 *            - Message
	 * @param logStatus
	 *            - based on this value , pass or fail is decided
	 * @param remarks
	 *            - Remarks
	 * @param Actual
	 *            value
	 * @param ExpectedValue
	 * @return void.
	 */
	public void insertWarning(String testcaseID, String stepNo, String moduleName,
			String stepDescription, String message, String remarks,
			String ActualValue, String ExpectedValue) {

		try {
			String result = "result not defined";
			CreateWorkbook(fileName1);
			FileInputStream file = new FileInputStream(new File(fileName1));
			HSSFWorkbook workbook = new HSSFWorkbook(file);
			HSSFSheet sheet = workbook.getSheetAt(0);
			CreationHelper createHelper = workbook.getCreationHelper();
			CellStyle hlink_style = workbook.createCellStyle();

			CellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setWrapText(true);

			org.apache.poi.ss.usermodel.Hyperlink link = createHelper
					.createHyperlink(org.apache.poi.ss.usermodel.Hyperlink.LINK_FILE);

			HSSFCell cell = null;
			HSSFRow row = null;

			// Update the value of cell
			int lastRow = sheet.getLastRowNum();
			row = sheet.createRow(++lastRow);

			// cell = row.createCell(0);
			// cell.setCellValue(testcaseID);

			cell = row.createCell(1);
			cell.setCellValue(stepNo);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(2);
			cell.setCellValue(moduleName);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(3);
			cell.setCellValue(stepDescription);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(4);
			cell.setCellValue(message);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(5);
			cell.setCellValue("Warning");
			// Formating Cell Style
			HSSFCellStyle resultStyle = workbook.createCellStyle();

			result = "Warning";
			resultStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
			resultStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

			cell.setCellValue(result);
			cell.setCellStyle(resultStyle);

			// Creating hyper link cell 6

			Font hlink_font = workbook.createFont();
			hlink_font.setUnderline(Font.U_SINGLE);
			hlink_font.setColor(IndexedColors.BLUE.getIndex());
			hlink_style.setFont(hlink_font);

			cell = row.createCell(6);

			cell.setHyperlink(link);
			cell.setCellStyle(hlink_style);

			// cell 6
			cell = row.createCell(6);
			cell.setCellValue(remarks);

			cell = row.createCell(7);
			cell.setCellValue(ActualValue);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(8);
			cell.setCellValue(ExpectedValue);
			cell.setCellStyle(cellStyle);

			file.close();

			FileOutputStream outFile = new FileOutputStream(new File(fileName1));
			workbook.write(outFile);
			outFile.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
