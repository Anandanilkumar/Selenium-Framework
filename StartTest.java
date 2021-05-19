package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.testng.IReporter;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.xml.XmlSuite;
import org.testng.collections.Lists;
public class StartTest extends BaseSetup{

    static CreateDynamicSuite cds;
    static String s2 = System.getProperty("user.dir");
    public CustomFunctions customfunctions;
   public static String testNGPath=System.getProperty("user.dir")+"//TestNG_" + "failed_cases" + ".xml";
	public static File file2 = new File(testNGPath);
	 
	static StartTest ts=new StartTest();
	
	
	public static String getProperty(String key,String propFile)
    {
    	Properties prop = new Properties();
		
		String path = s2 + "\\src\\resources\\"+propFile+".properties";
		try {
			prop.load(new FileInputStream(path));
		} catch (Exception e) {

		}
		String value = prop.getProperty(key);
		return value;
    }
	public void tearDownScript() throws IOException
	{
		//Copy the extentReport
		copyReport();
		renameFile();
		storeFailedCases();
		System.out.println(failedCases.size());
	}
    /* Starting point of automated testing */
    public static void main(String[] args) throws InterruptedException, IOException {
	Reporter.log("##############Starting test #################");
	
	
	/******Checking if property file copying is required*******/
	String value= getProperty("isPropFileCopyReq","Applet");
	
	if(value.equals("Yes"))
		
	{
		System.out.println(s2+"//applet_for_prop.bat");
		try {
	        Process p =  Runtime.getRuntime().exec(s2+"//applet_for_prop.bat") ;  
	        while(p.isAlive())
	        {
	        	Thread.sleep(1000);
	        }
	    } catch (IOException ex) {
	    }
	}
	 
	 
	 /**********************************************************/
	

	/******Checking if property file copying is required*******/
	String value2= getProperty("isSelectiveExecReq","Applet");
	
	if(value2.equals("Yes"))
		
	{
		
		try {
	        Process p =  Runtime.getRuntime().exec(s2+"//selective_execution.bat") ;  
	        while(p.isAlive())
	        {
	        	Thread.sleep(1000);
	        }
	    } catch (IOException ex) {
	    }
	}
	 
	 
	 /**********************************************************/
	
	
	
	cds = new CreateDynamicSuite();
	int suiteCount = cds.getSuite_Count();
	
	
	if(file2.exists())
	{
		
		file2.delete();	
	}

	
	for (int suite_index = 1; suite_index <= suiteCount; suite_index++) {
	    /* checking whether suite run status is Yes */
	    if (cds.getSuitePropertyValue(suite_index, 3).equalsIgnoreCase(
		    "yes")) {
		XmlSuite suite = new XmlSuite();
		suite.setName(cds.getSuitePropertyValue(suite_index, 1));
		suite.setThreadCount(4);		
		suite.setParameters(cds.getSuiteParameters(suite_index));
		suite = cds.addAllTest(suite, suite_index);

		File file = new File("TestNG_" + suite_index + ".xml");
		System.out.println("file" + file);
		FileWriter writer;
		try {
		    writer = new FileWriter(file);
		    writer.write(suite.toXml());
		    writer.close();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		

		List<XmlSuite> suites = new ArrayList<XmlSuite>();
		suites.add(suite);
		TestNG testng = new TestNG();
		// For Custom report generation
		TestListenerAdapter tla = new controls.CustomTestReport();
		testng.addListener(tla);
		// Added for extent reports
		IReporter extent = new controls.ExtentReporterNG();
		testng.addListener(extent);
		testng.setXmlSuites(suites);
		testng.setVerbose(1);		
		testng.run();
		
	
		
		/******* RE EXECUTION OF FAILED CASES*****/
		String retryFlag= getProperty("retryFlag","GlobalVariable");
		
	
		
		if(retryFlag.equals("Yes"))
		{
			//Call tear down script
			ts.tearDownScript();
		for (int suite_index2 = 1; suite_index2 <= suiteCount; suite_index2++) {
			 //checking whether suite run status is Yes 
			if (cds.getSuitePropertyValue(suite_index2, 3).equalsIgnoreCase(
					"yes")) {
				XmlSuite suite2 = new XmlSuite();
				suite2.setName(cds.getSuitePropertyValue(suite_index2, 1));
				suite2.setThreadCount(4);		
				suite2.setParameters(cds.getSuiteParameters(suite_index2));
				suite2 = cds.addAllFailedTest(suite2, suite_index2);

				File file2 = new File("TestNG_" + "failed_cases" + ".xml");
				System.out.println("file" + file2);
				FileWriter writer2;
				try {
					writer2 = new FileWriter(file2);
					writer2.write(suite2.toXml());
					writer2.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

       
				List<XmlSuite> suites2 = new ArrayList<XmlSuite>();
				suites2.add(suite2);
				TestNG testng2 = new TestNG();
				// For Custom report generation
				TestListenerAdapter tla2 = new controls.CustomTestReport();
				testng2.addListener(tla2);
				// Added for extent reports
				IReporter extent2 = new controls.ExtentReporterNG();
				testng2.addListener(extent2);
				testng2.setXmlSuites(suites2);
				testng2.setVerbose(1);		
				testng2.run();
				
			
				
				

			}
		}
		}
	    }
	}
    }
}



