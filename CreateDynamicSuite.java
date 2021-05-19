/* Description : This class is used to read suite/Module and test cases info from xls files and creates the suite file dynamically
 * Author : Moshe George
 * Comments Author: Raghothama
 * Date created : 19-05-2014
 * Modification comments : New function getSuiteParametersDynamic, to get parameters and values dynamically added.
 * Modified by : Moshe George
 * Modified on : 03:July:2014  
 */
package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

public class CreateDynamicSuite extends BaseSetup{
	InputStream inp_, sinp, minp, tcinp = null;
	XSSFWorkbook suiteWorkBook, moduleWorkBook, tcWorkBook, workbook_ = null;
	XSSFSheet suiteSheet, moduleSheet, testCaseSheet, sheet_ = null;
	Iterator<Row> rowIterator;
	int total_row;
	String fileName_, suiteFileName, moduleFileName, tcFileName,tcSheetName;
	static int totalTestCount = 0;
	List<XmlTest> allTests;
	public CommonUtility comUtil;

	public enum selectedBrowser {
		FIREFOX, IEXPLORE, GOOGLECROME
	}

	public CreateDynamicSuite() {
		try {
			comUtil = new CommonUtility();
			/*
			 * Suite, Module and TestCase file names are fetched from
			 * ProjectConfig.properties
			 */
			String userDir = System.getProperty("user.dir");
			suiteFileName = comUtil.getPropertiesConfigValue("suiteFilePath");
			moduleFileName = comUtil.getPropertiesConfigValue("moduleFilePath");
			tcFileName = comUtil.getPropertiesConfigValue("testCaseFilePath");

			sinp = new FileInputStream(userDir+suiteFileName);
			suiteWorkBook = new XSSFWorkbook(sinp);
			minp = new FileInputStream(userDir+moduleFileName);
			moduleWorkBook = new XSSFWorkbook(minp);

			tcinp = new FileInputStream(userDir+tcFileName);
			tcWorkBook = new XSSFWorkbook(tcinp);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Map getCurrentSuitDetails() {
		return null;
	}

	public String getPropertyValue(XSSFSheet sheet, int tcCount, int property) {
		/*
		 * Since we are reading from the 7th row in the template, 7 is hard
		 * coded
		 */
		Cell cell = sheet.getRow(7 + tcCount).getCell(property);
		cell.setCellType(1);
		return cell.getStringCellValue();
	}

	public String getTCPropertyValue(XSSFSheet sheet, int tcCount, int property) {
		/*
		 * Since we are reading from the 7th row in the template, 7 is hard
		 * coded
		 */
		Cell cell = sheet.getRow(tcCount).getCell(property);
		cell.setCellType(1);
		return cell.getStringCellValue();
	}

	public int getTC_Count(XSSFSheet tcSheet) {
		String firstTC = tcSheet.getRow(7).getCell(1).getStringCellValue();
		int tcCount = 0;
		int iteration = 0;

		try {
			while (!tcSheet.getRow(7 + iteration).getCell(1)
					.getStringCellValue().isEmpty()) {
				iteration++;
			}
		} catch (NullPointerException e) {
		}
		return iteration;
	}

	public ArrayList<String> getModule_List(String moduleSheetName) {
		//Purpose : modules with runstatus as yes fetched from moduleSheetName.
		ArrayList<String> modulesToExecute = new ArrayList<String>();
		moduleSheet = moduleWorkBook.getSheet(moduleSheetName);

		int iteration = 1;
		String moduleName, runStatus;

		try {
			while (!moduleSheet.getRow(0 + iteration).getCell(0)
					.getStringCellValue().isEmpty()) {
				Cell paramCell = moduleSheet.getRow(0 + iteration).getCell(0);
				paramCell.setCellType(1);
				moduleName = paramCell.getStringCellValue();

				Cell paramStatus = moduleSheet.getRow(0 + iteration).getCell(1);
				paramStatus.setCellType(1);
				runStatus = paramStatus.getStringCellValue();

				if (runStatus.equalsIgnoreCase("yes")) {
					modulesToExecute.add(moduleName);
				}
				iteration++;
			}
		} catch (NullPointerException e) {
		}
		return modulesToExecute;
	}

	public int getSuite_Count() {
		//Purpose : Return total no: of suites
		XSSFSheet sheet = suiteWorkBook.getSheet("SuiteNames");

		String firstSuite = sheet.getRow(5).getCell(1).getStringCellValue();
		int tcCount = 0;
		int iteration = 0;

		try {
			while (!sheet.getRow(5 + iteration).getCell(1).getStringCellValue()
					.isEmpty()) {
				iteration++;
			}
		} catch (NullPointerException e) {
		}
		return iteration;
	}

	public String getStringVal(Cell cell) {
		//Purpose : Return string rep of a cell
		cell.setCellType(1);
		return cell.getStringCellValue();
	}

	public XmlSuite createFullSuite() {
		//Purpose : Creating empty suite dynamically.
		XmlSuite suite = new XmlSuite();
		List<XmlClass> classes;
		// suite.setName("TmpSuite");

		return suite;
	}
	public Map<String, String> getSuiteParametersDynamic(int suiteIndex) {
		//Purpose : Parameters are fetched from suite.xlsx dynamically and added in testng.xll
		Map<String, String> suiteParamMap = new HashMap<String, String>();
		int totalSuiteparameters;
		String propertyName;
		String propertyValue;
		totalSuiteparameters = getColumnCount(suiteIndex);

		for (int suiteParamCounter = 1; suiteParamCounter < totalSuiteparameters; suiteParamCounter++) {
			if (getCellValue(suiteIndex, suiteParamCounter) != null) {
				propertyName = getHeaderCellValue(suiteParamCounter);
				propertyValue = getCellValue(suiteIndex, suiteParamCounter);

				suiteParamMap.put(propertyName, propertyValue.toLowerCase());
			}
		}
		return suiteParamMap;
	}
	public String getHeaderCellValue(int cellIndex) {
		//Purpose : Will get geader value
		XSSFSheet sheet = suiteWorkBook.getSheet("SuiteNames");
		Cell cell;

		cell = sheet.getRow(4).getCell(cellIndex);
		if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK)
			return null;
		else {
			cell.setCellType(1);
			return cell.getStringCellValue().toLowerCase().replace(" ", "");
		}
	}
	
	public String getCellValue(int rowIndex, int cellIndex) {
		//Purpose : to get the string representation of any non header cell
		XSSFSheet sheet = suiteWorkBook.getSheet("SuiteNames");
		Cell cell;

		cell = sheet.getRow(4 + rowIndex).getCell(cellIndex);
		if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK)
			return null;
		else {
			cell.setCellType(1);
			return cell.getStringCellValue();
		}
	}

	public int getColumnCount(int rowIndex) {
		//Purpose : to get the total number of parameters names.
		XSSFSheet sheet = suiteWorkBook.getSheet("SuiteNames");
		// first identify maxRowCount
		Cell cell;
		int cellIndex = 1;
		boolean lastCellNotReached = true;
		while (lastCellNotReached) {
			cell = sheet.getRow(4 + rowIndex).getCell(cellIndex);
			if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK)
				lastCellNotReached = false;
			else
				cellIndex++;
		}
		return cellIndex;
	}

	public Map<String, String> getSuiteParameters(int suiteIndex) {
		//Purpose : This was the original method to get suite parameters. Parameters are statically mapped to parameter names.
		Map<String, String> suiteParamMap = new HashMap<String, String>();
		suiteParamMap.put("gridFlag", getSuitePropertyValue(suiteIndex, 4)
				.toLowerCase());
		suiteParamMap.put("url", getSuitePropertyValue(suiteIndex, 5));
		suiteParamMap.put("IBEurl", getSuitePropertyValue(suiteIndex, 6));
		suiteParamMap
				.put("delayBwScript", getSuitePropertyValue(suiteIndex, 7));
		suiteParamMap.put("language", getSuitePropertyValue(suiteIndex, 8));
		/*
		 * suiteParamMap.put("Description", getSuitePropertyValue(suiteIndex,
		 * 8));
		 */
		suiteParamMap.put("IBETestData", getSuitePropertyValue(suiteIndex, 9));
		suiteParamMap.put("xpathXLS", getSuitePropertyValue(suiteIndex, 10));
		suiteParamMap.put("filepath", getSuitePropertyValue(suiteIndex, 11));
		suiteParamMap.put("screnshotfilepath",
				getSuitePropertyValue(suiteIndex, 12));
		suiteParamMap.put("tcSheetName",
				getSuitePropertyValue(suiteIndex, 14));
		return suiteParamMap;
	}

	public String getSuitePropertyValue(int rowNum, int cellNum) {
		//Purpose : Similar to getCell value. Original implementation
		XSSFSheet sheet = suiteWorkBook.getSheet("SuiteNames");
		Cell cell = sheet.getRow(4 + rowNum).getCell(cellNum);
		
		
		cell.setCellType(1);
		
		
		return cell.getStringCellValue();
	}
	//Purpose : to get the grid flag, which will be used in starttest. 
	public String getGridFlagValue(int rowNum, int cellNum) {
		
		XSSFSheet sheet = suiteWorkBook.getSheet("SuiteNames");
		Cell cell = sheet.getRow(4 + rowNum).getCell(cellNum);
		cell.setCellType(1);
		return cell.getStringCellValue();
	}

	public XmlSuite addAllTest(XmlSuite suite, int suite_index) {
		/* All the suite level paraleters added.
		 * All the test with run status as Yes to suite */
		XmlSuite appendedSuite = new XmlSuite();
		String moduleSheetName = getSuitePropertyValue(suite_index, 13);
		ArrayList<String> modulesToRun = getModule_List(moduleSheetName);
		String tcSheetName = getSuitePropertyValue(suite_index, 14);
		appendedSuite = addTestToSuite(suite, modulesToRun, tcSheetName);
		return appendedSuite;
	}
	
	public XmlSuite addAllFailedTest(XmlSuite suite, int suite_index) {
		/* All the suite level paraleters added.
		 * All the test with run status as Yes to suite */
		XmlSuite appendedSuite = new XmlSuite();
		String moduleSheetName = getSuitePropertyValue(suite_index, 13);
		ArrayList<String> modulesToRun = getModule_List(moduleSheetName);
		String tcSheetName = getSuitePropertyValue(suite_index, 14);
		appendedSuite = addFailedTestToSuite(suite, modulesToRun, tcSheetName);
		return appendedSuite;
	}

	private XmlSuite addTestToSuite(XmlSuite suite,
			ArrayList<String> modulesToRun, String tcSheetName) {
		List<String> testCaseToExecute = new ArrayList<String>();
		testCaseSheet = tcWorkBook.getSheet(tcSheetName);

		int iterator = 1;
		boolean moduleRunStatus, tcRunStatus;
		String moduleName;
		allTests = new ArrayList<XmlTest>();

		try {
			while (!testCaseSheet.getRow(1 + iterator).getCell(1)
					.getStringCellValue().isEmpty()) {
				/*
				 * code to check if the TC belong to modules with run status as
				 * yes
				 */
				testCaseSheet.getSheetName();
				testCaseSheet.getRow(1 + iterator).getCell(0);
				Cell moduleCell = testCaseSheet.getRow(1 + iterator).getCell(7);
				moduleCell.setCellType(1);
				moduleName = moduleCell.getStringCellValue();
			System.out.println(moduleName);

				moduleRunStatus = checkTCModuleRunStatus(modulesToRun,
						moduleName);
				System.out.println(moduleRunStatus);
				
				List<XmlClass> classes;
				if (moduleRunStatus) {

					XmlTest test = new XmlTest(suite);
					Map<String, String> tcParamMap = new HashMap<String, String>();
					/* checking whether tc run status is yes */
					if (getTCPropertyValue(testCaseSheet, 1 + iterator, 3)
							.equalsIgnoreCase("yes")) {
						tcParamMap.put(
								"testName",
								getTCPropertyValue(testCaseSheet, 1 + iterator,
										2));
						
						tcParamMap.put(
								"browser",
								getTCPropertyValue(testCaseSheet, 1 + iterator,
										4));
						tcParamMap.put(
								"browserversion",
								getTCPropertyValue(testCaseSheet, 1 + iterator,
										5));
						tcParamMap.put(
								"description",
								getTCPropertyValue(testCaseSheet, 1 + iterator,
										6));
						tcParamMap.put(
								"testURL",
								getTCPropertyValue(testCaseSheet, 1 + iterator,
										11));
						test.setParameters(tcParamMap);
						test.setName(getTCPropertyValue(testCaseSheet,
								1 + iterator, 2));
						classes = new ArrayList<XmlClass>();
						// classes.add(new
						// XmlClass(getTCPropertyValue(testCaseSheet,
						// 1 + iterator, 0)+".class"));
						classes.add(new XmlClass(getTCPropertyValue(
								testCaseSheet, 1 + iterator, 1)));
						test.setXmlClasses(classes);
						// suite.addTest(test);
						allTests.add(test);
						iterator++;
					} else
						iterator++;
				} else
					iterator++;

			}

			suite.setTests(allTests);
		} catch (NullPointerException e) {
		}
		writeTestCountToProperties(allTests.size());
		return suite;
	}
	private XmlSuite addFailedTestToSuite(XmlSuite suite,
			ArrayList<String> modulesToRun, String tcSheetName) {
		List<String> testCaseToExecute = new ArrayList<String>();
		testCaseSheet = tcWorkBook.getSheet(tcSheetName);

		int iterator = 1;
		boolean moduleRunStatus, tcRunStatus;
		String moduleName;
		allTests = new ArrayList<XmlTest>();

		try {
			while (!testCaseSheet.getRow(1 + iterator).getCell(1)
					.getStringCellValue().isEmpty()) {
				/*
				 * code to check if the TC belong to modules with run status as
				 * yes
				 */
				testCaseSheet.getSheetName();
				testCaseSheet.getRow(1 + iterator).getCell(0);
				Cell moduleCell = testCaseSheet.getRow(1 + iterator).getCell(7);
				moduleCell.setCellType(1);
				moduleName = moduleCell.getStringCellValue();
				System.out.println(moduleName);

				moduleRunStatus = checkTCModuleRunStatus(modulesToRun,
						moduleName);
				System.out.println(moduleRunStatus);
				List<XmlClass> classes;
				if (moduleRunStatus) {

					XmlTest test = new XmlTest(suite);
					Map<String, String> tcParamMap = new HashMap<String, String>();
					/* checking whether tc run status is yes */
					System.out.println(failedCases.size());
					System.out.println(failedCases);
					if (getTCPropertyValue(testCaseSheet, 1 + iterator, 3)
							.equalsIgnoreCase("yes")) {
               System.out.println(getTCPropertyValue(testCaseSheet, 1 + iterator,2));
               boolean tc=false;
							for(int i=0;i<failedCases.size();i++)
							{
								String testCase=getTCPropertyValue(testCaseSheet, 1 + iterator,2).trim();
								System.out.println(failedCases.get(i).trim());
								if(failedCases.get(i).trim().equals(testCase))
								{
									tc=true;
								}
							}
							
						//if (failedCases.contains(getTCPropertyValue(testCaseSheet, 1 + iterator,2))) {
                             if (tc) {
								System.out.println("enterrrr");
							
							tcParamMap.put(
									"testName",
									getTCPropertyValue(testCaseSheet, 1 + iterator,
											2));
							/*	if (getTCPropertyValue(testCaseSheet, 1 + iterator, 8)
								.equalsIgnoreCase("fail")) {*/



							
							System.out.println("enterrr");
						
					
						
						
						tcParamMap.put(
								"browser",
								getTCPropertyValue(testCaseSheet, 1 + iterator,
										4));
						tcParamMap.put(
								"browserversion",
								getTCPropertyValue(testCaseSheet, 1 + iterator,
										5));
						tcParamMap.put(
								"description",
								getTCPropertyValue(testCaseSheet, 1 + iterator,
										6));
						tcParamMap.put(
								"testURL",
								getTCPropertyValue(testCaseSheet, 1 + iterator,
										11));
						test.setParameters(tcParamMap);
						test.setName(getTCPropertyValue(testCaseSheet,
								1 + iterator, 2));
						classes = new ArrayList<XmlClass>();
						// classes.add(new
						// XmlClass(getTCPropertyValue(testCaseSheet,
						// 1 + iterator, 0)+".class"));
						classes.add(new XmlClass(getTCPropertyValue(
								testCaseSheet, 1 + iterator, 1)));
						test.setXmlClasses(classes);
						// suite.addTest(test);
						
					}
						
						else
						{
							
						}
						
						allTests.add(test);
						iterator++;
					}else
						iterator++;
				} else
					iterator++;

			}

			suite.setTests(allTests);
		} catch (NullPointerException e) {
		}
		writeTestCountToProperties(allTests.size());
		return suite;
	}
	/*
	 * public int getAllTestsCount() { totalTestCount = allTests.size(); return
	 * totalTestCount; }
	 */

	private boolean checkTCModuleRunStatus(ArrayList<String> modulesToRun,
			String moduleName) {
		return modulesToRun.contains(moduleName);
	}

	/*
	 * SuiteConfig.properties is hard coded since, we are not planning the user
	 * to enter any value for the parameter Total test count
	 */
	public void writeTestCountToProperties(int count) {
		//Purpose : Used to store the total suite to execute in a properties file for future reference.
		Properties props = new Properties();
		props.setProperty("TotalTestCount", "" + count);
		OutputStream out = null;

		String userDir = System.getProperty("user.dir");
		String propertyPath = userDir
				+ "\\src\\resources\\Properties\\SuiteConfig.properties";
		try {
			File f = new File(propertyPath);
			out = new FileOutputStream(f);
		} catch (Exception e) {
			out = null;
		}
		try {
			props.store(out, "writing total count to properties file");
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
