package common;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;

import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.google.common.base.Function;

import java.time.Duration;
import java.util.Date;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
 
//import io.github.bonigarcia.wdm.WebDriverManager;






public abstract class DriverSetup {

	public static String browser;
	private static ChromeDriverService service = null;
	static DesiredCapabilities capabilities = null;
	public static WebDriver driver = null;
	public String language;
	private static String gridStatus;
	private String tcSheetName;
	public String baseURL;
	public static String testName;
	public String testURL;
	public String ibeURL;
	public String xpathFilePath = null;
	public String languageFilePath = null;
	public String description = null;
	public String browserversion = null;
	public static boolean suitesNotStarted = true;
	int testTotalCount;
	int currentTestCount;
	public String screnshotfilepath;
	CreateDynamicSuite cds;

	public int getTestCount() {
		return testTotalCount;
	}

	public enum selectedBrowser {
		FIREFOX, IEXPLORE, GOOGLECROME, NONE, HTMLUNIT, HEADLESS;
	}

	@BeforeSuite
	public void setUp(ITestContext context) throws IOException, InterruptedException, AWTException {
		XmlSuite xmlSuite = context.getSuite().getXmlSuite();
		Map<String, String> allParameters = xmlSuite.getAllParameters();
		gridStatus = allParameters.get("gridFlag");
		tcSheetName = allParameters.get("tcSheetName"); // Added to get testcase
														// sheet name
		cds = new CreateDynamicSuite();
		List<XmlTest> testList = xmlSuite.getTests();
		currentTestCount = 1;
		

		if (gridStatus.equalsIgnoreCase("true")) {
			if (suitesNotStarted) {
				/*
				 * startHub(); // need to run manually startNode("firefox",
				 * "34"); startNode("iexplore", "8"); startNode("chrome", "39");
				 */
				suitesNotStarted = false;
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		Desktop desktop = Desktop.getDesktop();
        File myFile = new File(".//src//resources//TestData.xls");
      //  desktop.open(myFile);
       Thread.sleep(2000);
     
     /*  Robot rb=new Robot();
       rb.keyPress(KeyEvent.VK_CONTROL);
       rb.keyPress(KeyEvent.VK_S);
       rb.keyRelease(KeyEvent.VK_CONTROL);
       rb.keyRelease(KeyEvent.VK_S);
       Thread.sleep(2000);
     
       rb.keyPress(KeyEvent.VK_ALT);
       rb.keyPress(KeyEvent.VK_F4);
       rb.keyRelease(KeyEvent.VK_ALT);
       rb.keyRelease(KeyEvent.VK_F4);
       rb=null;*/


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

	
	public int getCurrentTestCount() {
		return currentTestCount;
	}

	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	/**
	 * This method get the parameters from the suite file
	 * 
	 * @param browser
	 *            the browser type
	 * @param url
	 *            the application url
	 * @param gridFlag
	 *            Whether the test will be executed in Grid
	 * @param language
	 *            the opted language
	 * @throws MalformedURLException
	 *             Thrown to indicate that a malformed URL has occurred
	 * @throws InterruptedException
	 */
	@BeforeTest
	@Parameters({ "browser", "browserversion", "url", "IBEurl", "gridFlag",
			"languagexls", "language", "delaybetweenscripts", "xpathXLS",
			"description", "testName", "testURL", "screnshotfilepath",
			"Newparam", "tcSheetName" })
	// All the parameters in @Parameters section should be in lower case. This
	// is to avoid case mismatch while adding new parameters.
	public void testSetup(@Optional("firefox") String browser,
			@Optional("0") String browserversion,
			@Optional("false") String url, @Optional("false") String IBEurl,
			@Optional("false") String gridFlag,
			@Optional("false") String languageXLS,
			@Optional("english") String language,
			@Optional("5") String delayBetweenScripts,
			@Optional("english") String xpathFilepath,
			@Optional("") String description, @Optional("") String testName,
			@Optional("") String testURL,
			@Optional("") String screnshotfilepath,
			@Optional("") String NEWPARAM, @Optional("") String tcSheetName)
			// Added to get testcase sheet name

			throws MalformedURLException, InterruptedException,
			StaleElementReferenceException {
		Integer startUpDelaySec = Integer.parseInt(delayBetweenScripts);
		Thread.sleep(startUpDelaySec * 1000);
		this.browser = browser;
		baseURL = url;
		this.language = language;
		this.description = description;
		this.browserversion = browserversion;
		this.browser=browser;
		this.testName = testName;
		this.testURL = testURL;
		ibeURL=IBEurl;
		this.screnshotfilepath = screnshotfilepath;
		this.tcSheetName = tcSheetName;
		if (gridFlag.equalsIgnoreCase("false")) {

			this.languageFilePath = languageXLS;
			this.xpathFilePath = xpathFilepath;
			// driver.manage().deleteAllCookies();

			/*
			 * if (browser != "firefox") { driver.manage().window().maximize();
			 * }
			 */
		} else if (gridFlag.equalsIgnoreCase("true")) {

			try {

				this.driver = getRemoteDriver(getCapabilities());
				System.out.println("Driver instance is  " + driver);
				this.languageFilePath = languageXLS;
				this.xpathFilePath = xpathFilepath;
			} catch (MalformedURLException e) {

				e.printStackTrace();
			} catch (org.openqa.selenium.WebDriverException e) {
				Assert.fail(
						"Seems grid was not initialized propertly,  this test is failed",
						e);
			}

		}

	}

	public String getScrenshotfilepath() {
		return screnshotfilepath;
	}

	public String getBrowserversion() {
		return browserversion;
	}

	public String getDescription() {
		return description;
	}

	public String getBrowser() {
		return browser;
	}

	public String getLanguage() {
		return language;
	}

	// Added to get testcase sheet name
	public String getCurrentSuiteSheetName() {
		return tcSheetName;
	}

	public String getTestName() {
		return testName;
	}

	public String getTestURL() {
		return testURL;
	}
	
	
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() throws IOException {
	
	   /*  Runtime runtime = Runtime.getRuntime();
		 runtime.exec("taskkill /f /im chrome.exe") ; 
   	     runtime.exec("taskkill /f /im chromedriver.exe *32") ; */
		if (gridStatus.equalsIgnoreCase("false")) {
			driver = getDriver(getCapabilities());			
			driver.manage().window().maximize();
		} else {			
			driver.manage().window().maximize();
		}
	}

	/**
	 * This method select the browser for run the application.
	 * 
	 * @param browser
	 *            the browser type
	 * @return the browser type if condition satisfied else return null
	 */
	public final selectedBrowser getBrowserType(String browser) {
		if (browser.equalsIgnoreCase("firefox")) {
			return selectedBrowser.FIREFOX;
		} else if (browser.equalsIgnoreCase("iexplore")) {
			return selectedBrowser.IEXPLORE;
		} else if (browser.equalsIgnoreCase("chrome")) {
			return selectedBrowser.GOOGLECROME;
		} else if (browser.equalsIgnoreCase("htmlunit")) {
			return selectedBrowser.HTMLUNIT;
		} else if (browser.equalsIgnoreCase("headless")) {
			return selectedBrowser.HEADLESS;
		} else {
			return null;
		}
	}

	/**
	 * Return the number of browser contained in this container
	 * 
	 * @return capabilities for the browser
	 */
	// @Parameters({ "googlepath"})
	public DesiredCapabilities getCapabilities() {
		switch (getBrowserType(this.browser)) {
		case FIREFOX:
			String sFF = System.getProperty("user.dir");
			String pathFF = sFF + "\\lib\\geckodriver.exe";
			System.setProperty("webdriver.gecko.driver",pathFF);			
			capabilities = DesiredCapabilities.firefox();
			capabilities.setBrowserName("firefox");
			//capabilities.setVersion(browserversion);
			capabilities.setPlatform(org.openqa.selenium.Platform.ANY);
			capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			capabilities.setCapability("marionette", true);
			// Below line can be added to avoid full page load.
			// profile.setPreference("webdriver.load.strategy", "unstable");
			break;
		case IEXPLORE:
			capabilities = DesiredCapabilities.internetExplorer();

			String s2 = System.getProperty("user.dir");
			String path = s2 + "\\lib\\IEDriverServer.exe";

			System.out.println("@getCapabilities() - ie driver path :" + path);

			System.setProperty("webdriver.ie.driver", path);
			capabilities.setBrowserName("iexplore");

			capabilities.setPlatform(org.openqa.selenium.Platform.WINDOWS);

			capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

			capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
			capabilities.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
					/*	capabilities.setCapability("initialBrowserUrl",
								"https://icargo-icapsit.lcag.fra.dlh.de/icargo/");
			*/
			break;
		case GOOGLECROME:
			capabilities = DesiredCapabilities.chrome();
			String sc2 = System.getProperty("user.dir");
			String pathc = sc2 + "\\lib\\chromedriver.exe";

			System.setProperty("webdriver.chrome.driver", pathc);
			ChromeOptions options = new ChromeOptions();// Added for checking
			HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
			chromePrefs.put("download.default_directory", sc2+"\\src\\resources\\Downloads\\");
		    options.setExperimentalOption("prefs",chromePrefs);
			
			Proxy proxy = new Proxy();// Added for checking for proxy settings
			proxy.setProxyType(Proxy.ProxyType.SYSTEM);// Added for checking for
														// proxy settings
			capabilities.setBrowserName("chrome");
			capabilities.setPlatform(org.openqa.selenium.Platform.WINDOWS);
			capabilities.setCapability("proxy", proxy);// Added for checking for
														// proxy settings
			capabilities.setCapability(ChromeOptions.CAPABILITY, options);// Added
																			// for
																			// checking
			capabilities.setCapability("chrome.switches",
					Arrays.asList("--start-maximized"));
			break;
		case HEADLESS:
			capabilities = DesiredCapabilities.phantomjs();
			String scH = System.getProperty("user.dir");
			String pathhead = scH + "\\lib\\phantomjs.exe";

			capabilities.setJavascriptEnabled(true);
			capabilities.setCapability(
					PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
					pathhead);
			capabilities.setBrowserName("headless");
			capabilities.setCapability("takesScreenshot", true);
			break;
		}
		return capabilities;

	}

	@SuppressWarnings("deprecation")
	public WebDriver getDriver(DesiredCapabilities cap)
			throws MalformedURLException {
try {
		switch (getBrowserType(this.browser)) {
		case FIREFOX:
			driver = new FirefoxDriver(cap);

			break;
		case IEXPLORE:
			driver = new InternetExplorerDriver(cap);
			break;

		case GOOGLECROME:
			driver = new ChromeDriver(cap);
			break;

		case HTMLUNIT:
			driver = new HtmlUnitDriver(true);
			break;

		case HEADLESS:
			driver = new PhantomJSDriver(cap);
			break;

		case NONE:
			driver = null;
			break;

		}
}
catch(Exception e)
{
e.printStackTrace();
System.out.println("Failed in launching browser");
}
		return driver;
	}

	/**
	 * @param cap
	 *            desiredCapabilities for the browser
	 * @return capabilities for the RemoteWebdriver
	 */
	public WebDriver getRemoteDriver(DesiredCapabilities cap)
			throws MalformedURLException {
		driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"),
				cap);
		return driver;
	}

	public void startHub() {
		String line = null;
		String[] command = {
				"cmd.exe",
				"/C",
				"Start",
				System.getProperty("user.dir")
						+ "\\src\\resources\\startHub.bat" };
		System.out.println("current dir is" + System.getProperty("user.dir"));
		try {
			Runtime r = Runtime.getRuntime();
			System.out.println("Hub command is " + command);
			Process p = r.exec(command);
			p.waitFor();
			/*
			 * ProcessBuilder pb = new ProcessBuilder(command); Process p =
			 * pb.start(); p.waitFor();
			 */
		} catch (IOException e) {

			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void startNode(String browser, String browserversion) {
		String line = null;
		String[] command = null;
		if (browser.equalsIgnoreCase("firefox")) {
			command = new String[] {
					"cmd.exe",
					"/C",
					"Start",
					System.getProperty("user.dir")
							+ "\\src\\resources\\startNode_FF_5557.bat",
					browserversion };
		} else if (browser.equalsIgnoreCase("iexplore")) {

			command = new String[] {
					"cmd.exe",
					"/C",
					"Start",
					System.getProperty("user.dir")
							+ "\\src\\resources\\startNode_IE_5565.bat",
					browserversion };
		} else if (browser.equalsIgnoreCase("chrome")) {
			command = new String[] {
					"cmd.exe",
					"/C",
					"Start",
					System.getProperty("user.dir")
							+ "\\src\\resources\\startNode_Chrome_5558.bat" };
		}
		try {
			Thread.sleep(5000);
			Runtime r = Runtime.getRuntime();
			Process p = r.exec(command);
			p.waitFor();

			/*
			 * ProcessBuilder pb = new ProcessBuilder(command); Process p =
			 * pb.start(); p.waitFor();
			 */
			Thread.sleep(5000);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Quit the driver if its running else get the exception message
	 */

	@AfterClass(alwaysRun = true)
	public void tearDownClass() {
		// if (gridStatus.equalsIgnoreCase("false")) {
		if (driver != null) {

			try {
				driver.quit();
				Process processIE=Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
				Process processChrome=Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
				Process processFF=Runtime.getRuntime().exec("taskkill /F /IM geckodriver.exe");
			
			} catch (Exception e) {
				e.getMessage();
			}
			finally
			{
				System.gc();
			}
			// }
		}
	}

	@AfterTest(alwaysRun = true)
	public void tearDownTest(ITestResult result) {
		if (gridStatus.equalsIgnoreCase("true")) {
			if (driver != null) {

				try {
					driver.quit();
				} catch (Exception e) {
					e.getMessage();
				}
			}
		}
		if (ITestResult.FAILURE == result.getStatus()) {

		}
	}

	@AfterSuite(alwaysRun = true)
	public void tearDownSuite() {
		driver.manage().deleteAllCookies();
		if (gridStatus.equalsIgnoreCase("true")) {
			if (driver != null) {

				try {
					driver.quit();
				} catch (Exception e) {
					e.getMessage();
				}
			}
		}
	}
}
