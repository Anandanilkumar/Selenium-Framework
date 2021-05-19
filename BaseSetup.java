package common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;



public abstract class BaseSetup extends DriverSetup {
	protected ExtentReports extent;
	protected ExtentTest test;
	 static String s2 = System.getProperty("user.dir");
	 public static ArrayList<String> failedCases=new ArrayList<String>();
	 public static ArrayList<String> extentReportName=new ArrayList<String>();
	 public static  List<String> tcName=new ArrayList<String>();
	 public static  List<String> tcStatus=new ArrayList<String>();
	
	Calendar calendar = Calendar.getInstance();
	Date date = calendar.getTime();
	DateFormat dateFormat = new SimpleDateFormat("dd_MMM_yyyy__HH_mm_ss");
	final String filePath = ".\\reports\\html\\ExtentReport_"+ dateFormat.format(date)+".html";
	
	
	public String extentReportFileName="ExtentReport_FailedCases";
	
	public String globalVarPath="\\src\\resources\\GlobalVariable.properties";
	public ExcelReadWrite excelreadwrite;
	Xls_Read xls_Read;
	public CustomFunctions customfunctions;
	
	public static int suit=0;
	@AfterMethod
	protected void afterMethod(ITestResult result) throws IOException, InterruptedException {
		if (result.getStatus() == ITestResult.FAILURE) {
			if(!customfunctions.getPropertyValue(globalVarPath, "isClubbedTC").equals("Yes"))
			{
				test.log(LogStatus.FAIL, result.getThrowable());
			}

		} else if (result.getStatus() == ITestResult.SKIP) {
			if(!customfunctions.getPropertyValue(globalVarPath, "isClubbedTC").equals("Yes"))
			{
				test.log(LogStatus.SKIP, "Test skipped " + result.getThrowable());
			}

		} else {
			if(!customfunctions.getPropertyValue(globalVarPath, "isClubbedTC").equals("Yes"))
			{
				test.log(LogStatus.PASS, "Test passed");
			}


		}
		
		ExtentManager.getReporterInstance().endTest(test);
		ExtentManager.getReporterInstance().flush();
		
		
		excelreadwrite = new ExcelReadWrite(testName, driver, getBrowser(), getScrenshotfilepath());
		xls_Read = new Xls_Read(null, xpathFilePath);
		customfunctions = new CustomFunctions(driver, excelreadwrite, xls_Read);
        
        if(customfunctions.getPropertyValue(globalVarPath, "isClubbedTC").equals("Yes"))
        {
              
              customfunctions.setPropertyValue("isClubbedTC", "No", globalVarPath);      
        }

        
        //Updating the intermediate text file.
        
        File fromFile=new File(s2+"\\reports\\html\\"+getPropertyValue(globalVarPath, "extent_report_name") );
        File toFile=new File(s2+"\\reports\\extentReportText\\"+getPropertyValue(globalVarPath, "extent_report_name") );
        if(toFile.exists())
        {
        	toFile.delete();
        	
        }
        deleteTextFile(toFile,"txt");
        copyFileUsingApache(fromFile, toFile);
        File file=changeExtension(toFile,"txt");
        getExecutionResults(file);
        updateIntermediateTextFile();
        copyReport(getPropertyValue(globalVarPath, "extent_report_name").split(".html")[0]);
	}
	
	
	public static void updateIntermediateTextFile() throws IOException
	{
		String extentReportText=getPropertyValue("\\src\\resources\\GlobalVariable.properties", "extent_report_name").split(".html")[0];
		
		 BufferedReader br1 = new BufferedReader(new FileReader(s2+"\\reports\\intermediateText\\"+extentReportText+".txt"));
		     int i=1;
			String wid="10%";
			
			
		 
			 String line1 = br1.readLine(); 
			
			 String newLine="";
			 
			 while (line1 != null ) 
	     	{
					if(i==12)
					{
						for(int j=0;j<tcStatus.size();j++)
						{
							
						   newLine=newLine+"<tr>"+"\n"+"<th width="+wid+">"+tcName.get(j)+"</th>"+"\n"+"<th width="+wid+">"+tcStatus.get(j)+"</th>"+"\n"+"</tr>";
					      
						}
						
						line1=newLine.replaceAll("\n", System.lineSeparator());
					     
						break;
					}
			 
					else
					{
						
						line1=br1.readLine(); 
	        			i=i+1;
					}

		}
			 System.out.println(line1);
			 FileWriter writer = new FileWriter(s2+"\\reports\\intermediateText\\"+extentReportText+".txt");
			 String fileContent="<html>"+"\n"+"<head>"+"\n"+"<title>Test Report</title>"+
					 "\n"+"<head>"+"\n"+"<body>"+"\n"+"<h3>Test results</h3>"+"\n"+
							 "<table>"+"\n"+"<tr>"+"\n"+"<th width="+wid+">Test Case</th>"+"\n"+
					 "<th width="+wid+">Result</th>"+"\n"+"</tr>"+"\n";
			 
			 writer.write(fileContent.replaceAll("\n", System.lineSeparator())+line1);
				writer.close();
				br1.close();
				
	}

	/**
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static void getExecutionResults(File file) throws IOException
	{
		BufferedReader br1 = new BufferedReader(new FileReader(file));
		
		String line1 = br1.readLine(); 
		String tc="";String status="";
		tcName.clear();
		tcStatus.clear();
		
		while (line1 != null ) 
	        
	    { 
			
	        //TC NAME
	        if(line1.startsWith("<span class='test-name'>")) 
	        { 
	        	
	        	tc=line1.split("<span class='test-name'>")[1].split("</span>")[0].trim();
	        	System.out.println(tc);
	        	tcName.add(tc);
	        	line1 = br1.readLine(); 
	        }
	        
	        //STATUS
	        else if(line1.startsWith("<span class='test-status")) 
	        { 
	        	
	           // <span class='test-status label right outline capitalize pass'>pass</span>

	        	status=line1.split("'>")[1].split("</span>")[0].trim();
	        	tcStatus.add(status);
	        	line1 = br1.readLine(); 
	        }
	        
	        else
	        {
	        	line1 = br1.readLine(); 
	        }
		
	}
		br1.close(); 
	}
	/***
	 * 
	 * @param file
	 * @param extension
	 * @return
	 */
	  public static File changeExtension(File file, String extension) {
	        String filename = file.getName();
	        
	        System.out.println(filename);

	        if (filename.contains(".")) {
	            filename = filename.substring(0, filename.lastIndexOf('.'));
	        }
	        filename += "." + extension;
	        
	       

	        file.renameTo(new File(file.getParentFile(), filename));
	        return new File(file.getParentFile(), filename);
	    }
	  
	  public static void deleteTextFile(File file, String extension) {
	        String filename = file.getName();
	        
	        System.out.println(filename);

	        if (filename.contains(".")) {
	            filename = filename.substring(0, filename.lastIndexOf('.'));
	        }
	        filename += "." + extension;
	        
	        file=new File(s2+"\\reports\\extentReportText\\"+filename);
	        
	        if(file.exists())
	        {
	        	file.delete();
	        }

	       
	      
	    }
	/**
	 * 
	 * @param from
	 * @param to
	 * @throws IOException
	 */
	 public static void copyFileUsingApache(File from, File to) throws IOException{ 
	    	
	    	FileUtils.copyFile(from, to); 
	    	
	    }
	/***
	 * @author A-7271
	 * Desc : Copy report
	 */
	public  void copyReport()
	{
		
		System.out.println(extentReportName.get(0));
		File source = new File(extentReportName.get(0));
		
		File dest = new File(s2+"\\reports\\html\\Reports\\"+extentReportFileName+".html");
		
		if(dest.exists())
		{
			dest.delete();	
		}
	
		try {
		    FileUtils.copyFile(source, dest);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	public  void copyReport(String fName)
	{
		
		
		File source = new File(s2+"\\reports\\intermediateText\\"+fName+".txt");
		
		
		File dest = new File(s2+"\\reports\\intermediateHtml\\"+fName+".html");
		
		if(dest.exists())
		{
			dest.delete();	
		}
	
		try {
		    FileUtils.copyFile(source, dest);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	/**
	 * @author A-7271
	 * Desc : rename File
	 */
	 public  void renameFile()
	 {
			File file = new File(s2+"\\reports\\html\\Reports\\"+extentReportFileName+".html");
		 
	        file.renameTo(new File(s2+"\\reports\\html\\Reports\\"+extentReportFileName+".txt"));
	 }
	 /**
	  * @author A-7271
	  * @throws IOException
	  * Desc : store failed cases
	  */
	 public  void storeFailedCases() throws IOException
		{
			 BufferedReader br1 = new BufferedReader(new FileReader(s2+"\\reports\\html\\Reports\\"+extentReportFileName+".txt"));
			  String line= br1.readLine(); 
			  int i=1;
			  int lineNum=0;
			  String testCase="";
			  
			  while (line != null ) 
	      	{
				  if(line.contains("<span class='test-name'>"))
	      		{ 
					 lineNum=i;
					 testCase=line.split(">")[1].split("</")[0];
	      		}
				  
				  else if(i==(lineNum+1))
		           {
					  if(line.contains("fail"))
					  {
						  failedCases.add(testCase);
						  
					  }
				   }
				  
				 
			 line = br1.readLine(); 
				  
				 i=i+1; 
	      	}
			  
			  br1.close();   
		}
	@BeforeMethod
	public void startExtent(Method method) {
		
		
		test = ExtentManager.getReporterInstance().startTest(method.getDeclaringClass().getSimpleName());
       
	}
	public void setPropertyValue(String key, String value, String s3) {

		Properties prop = new Properties();
		String s2 = System.getProperty("user.dir");
		String path = s2 + s3;
		FileOutputStream output;
		FileInputStream fileIn;
		try {
			fileIn = new FileInputStream(path);
			prop.load(fileIn);
			output = new FileOutputStream(path);
			prop.setProperty(key, value);
			prop.store(output, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 /**
	  * 
	  * @param s3
	  * @param Key
	  * @return
	  */
	public static String getPropertyValue(String s3, String Key) {
		Properties prop = new Properties();
		String s2 = System.getProperty("user.dir");
		String path = s2 + s3;
		try {
			prop.load(new FileInputStream(path));
		} catch (Exception e) {

		}
		String value = prop.getProperty(Key);
		return value;
	}
	/**
	 * @throws IOException 
	 * 
	 */
	public void updateIntReport(String fName,String fileName) throws IOException
	{
		try
		{
			String wid="10%";
			System.out.println(fName);
		 FileWriter fw=new FileWriter(fName); 
		 String fileContent="<html>"+"\n"+"<head>"+"\n"+"<title>Test Report</title>"+
		 "\n"+"<head>"+"\n"+"<body>"+"\n"+"<h3>Test results</h3>"+"\n"+
				 "<table>"+"\n"+"<tr>"+"\n"+"<th width="+wid+">Test Case</th>"+"\n"+
		 "<th width="+wid+">Result</th>"+"\n"+"</tr>"+"\n"+"</table>"+"\n"+"</body>"+"\n";
		 fileContent=fileContent.replaceAll("\n", System.lineSeparator());
         fw.write(fileContent);    
         fw.close(); 
         copyReport(fileName);
         
		}
		
		catch(Exception e)
		{
			
		}
		  
		  
	}
	/**
	 * 
	 * @param fName
	 * @throws IOException
	 */
	public void createIntReport(String fName) throws IOException
	{
		System.out.println(fName);
		final File parentDir = new File(System.getProperty("user.dir")+"//reports//intermediateText//");
		parentDir.mkdir();
		final String fileName = fName + ".txt";
		final File file = new File(parentDir, fileName);
		file.createNewFile();
		updateIntReport(parentDir+"//"+fileName,fName);
	}
	
	/**
	 * @author A-7271
	 * Desc : print FW name
	 */
	public void printFWName()
	{
		System.out.println();
    	System.out.println("*******  ******** ******   ****** ********");
    	System.out.println("   *        *     *        *         *");
    	System.out.println("   *        *     *        *         *");
    	System.out.println("   *        *     ******   ******    *");
    	System.out.println("   *        *     *             *    *");
    	System.out.println("   *        *     *             *    *");
    	System.out.println("*******     *     ******   ******    *");
    	System.out.println();
	}
	@BeforeSuite
	public void beforeSuite() throws IOException {
		suit=suit+1;
		String fileName="ExtentReport_"+filePath.split("ExtentReport_")[1];
		
		if(suit==1)
		{
		printFWName();
		
		
		
		
		//Writing the extent report name to the property file
		
		
		setPropertyValue("extent_report_name","ExtentReport_"+filePath.split("ExtentReport_")[1],"\\src\\resources\\GlobalVariable.properties");
		extentReportName.add(filePath);
		}
		extent = ExtentManager.getReporter(filePath);
		
		if(suit==1)
		{
		createIntReport(fileName.split(".html")[0]);
		}
		
	}

	@AfterSuite
	public void afterSuite() throws IOException {
		extent.close();
		

	}
	

}