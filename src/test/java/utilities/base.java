package utilities;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.AndTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.RecipientStringTerm;
import javax.mail.search.SearchTerm;

import org.apache.camel.component.mail.SearchTermBuilder;
import org.apache.commons.lang3.RandomStringUtils;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.yaml.snakeyaml.Yaml;

import com.google.common.io.Files;
import com.testautomationguru.ocular.Ocular;
import com.testautomationguru.ocular.comparator.OcularResult;
import com.vimalselvam.cucumber.listener.Reporter;

import cucumber.api.DataTable;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import io.github.bonigarcia.wdm.WebDriverManager;
import net.bytebuddy.asm.Advice.Exit;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

public class base 
{

	public static WebDriver driver;
	public static HashMap<String, String> configurations;
	public static HashMap<String, String> mspp = new HashMap<>();
	public static HashMap<String, String> emails = new HashMap<>();
	public static HashMap<String,String> objectRepository;
	public static int counter = 1;
	public static String messageContent = "";

	@Before
	public void initializeDriver() throws FileNotFoundException, IOException 
	{
		Properties prop = new Properties();
 		String configFilePath = System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"java"+File.separator+"resources"+File.separator+"Configuration.properties";
 		prop.load(new FileInputStream(configFilePath));
 		configurations = new HashMap(prop);
 		String objectRepositoryFilePath = System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"java"+File.separator+"Resources"+File.separator+"ObjectRepository.yaml";
 		Yaml yaml = new Yaml();
 		objectRepository = (HashMap<String, String>) yaml.load(new FileReader(objectRepositoryFilePath));
		if (configurations.get("Browser").equalsIgnoreCase("chrome")) 
		{
			String downloadFilepath = System.getProperty("user.dir") + File.separator + "target" + File.separator+ "Downloads";
			HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
			chromePrefs.put("profile.default_content_settings.popups", 0);
			chromePrefs.put("download.default_directory", downloadFilepath);
			chromePrefs.put("credentials_enable_service", false);
			chromePrefs.put("profile.password_manager_enabled", false);
			WebDriverManager.chromedriver().setup();
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--start-maximized");
			options.addArguments("--disable-browser-side-navigation");
			options.addArguments("--disable-extensions");
			options.addArguments("disable-infobars");
//			options.setExperimentalOption("useAutomationExtension", false);
			options.setExperimentalOption("excludeSwitches",Collections.singletonList("enable-automation"));
			options.addArguments("--disable-plugins-discovery");
			options.setExperimentalOption("prefs", chromePrefs);
//			options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
//			options.setExperimentalOption("useAutomationExtension", false);
			driver = new ChromeDriver(options);
			
		} 
		else if (configurations.get("Browser").equalsIgnoreCase("firefox")) 
		{
			WebDriverManager.firefoxdriver().setup();
			driver = new FirefoxDriver();
		} 
		else if (configurations.get("Browser").equalsIgnoreCase("ie")) 
		{
			WebDriverManager.iedriver().arch32().version("3.150.1").setup();
			InternetExplorerOptions ieoptions = new InternetExplorerOptions();
			ieoptions.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
			ieoptions.setCapability("ignoreZoomSetting", true);
			ieoptions.ignoreZoomSettings();
			ieoptions.introduceFlakinessByIgnoringSecurityDomains();
			ieoptions.withInitialBrowserUrl("http://www.google.com");
			driver = new InternetExplorerDriver(ieoptions);
			driver.manage().window().fullscreen();
		}
	}

	@After
	public void quitDriver(Scenario scenario) throws IOException 
	{
		try {
			Thread.sleep(2000);
			if (scenario.isFailed()) 
			{
				byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
				scenario.embed(screenshot, "image/png");
				File sourcePath = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				String screenshotName = RandomStringUtils.randomAlphabetic(8);
				File destinationPath = new File(System.getProperty("user.dir") + "/target/Extent-Report/" + screenshotName + ".png");
				Files.copy(sourcePath, destinationPath);
				Reporter.addScreenCaptureFromPath(destinationPath.toString());
			}
		} 
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
		} 
		finally 
		{
			driver.quit();
		}
	}

	public void navigateToURL(String URL, String browser, int implicitWait) 
	{
		driver.navigate().to(URL);
		driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
		if (browser.equalsIgnoreCase("ie")) 
		{
			driver.manage().window().maximize();
		}
	}

	public WebDriverWait waitFor() 
	{
		WebDriverWait wait = new WebDriverWait(driver, 30);
		return wait;
	}

	public void iWillWaitToSee(By locator) 
	{
		try 
		{
			waitFor().until(ExpectedConditions.visibilityOfElementLocated(locator));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public void refreshScreen()
	{
		driver.navigate().refresh();
	}
	
	public void switchToNewWindow() 
	{
		ArrayList<String> tabs = new ArrayList<String>(base.driver.getWindowHandles());
		driver.switchTo().window(tabs.get(tabs.size() - 1));
	}

	public void switchToOldWindow() 
	{
		ArrayList<String> tabs = new ArrayList<String>(base.driver.getWindowHandles());										// ArrayList
		driver.switchTo().window(tabs.get(0));
	}

	public void clickElement(String locator) 
	{
		iWillWaitToSee(By.xpath(locator));
		scrollIntoViewElement(locator);
		driver.findElement(By.xpath(locator)).click();
	}

	public void enterTextInElement(String text, String locator) 
	{
		iWillWaitToSee(By.xpath(locator));
		driver.findElement(By.xpath(locator)).sendKeys(text);
	}
	
	public void keyPressEvent(String action, String locator) throws AWTException 
	{
		iWillWaitToSee(By.xpath(locator));
		if(action.contains("ENTER"))
		{
			driver.findElement(By.xpath(locator)).sendKeys(Keys.ENTER);
		}
	}

//	public void verifyTextForElement(String locator, String text) 
//	{
//		WebElement element = driver.findElement(By.xpath(locator));
//		if (isElementPresentOnPage(locator,action)) 
//		{
//			Assert.assertEquals(element.getText(), text);
//		}
//	}

	public void clearTextFromElement(String locator) 
	{
		iWillWaitToSee(By.xpath(locator));
		driver.findElement(By.xpath(locator)).sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
	}

	public String getTextFromElement(String locator) 
	{
		iWillWaitToSee(By.xpath(locator));
		return driver.findElement(By.xpath(locator)).getText().toString().trim();
	}
	
	public String getAttributeValueFromElement(String locator, String attribute) 
	{
		iWillWaitToSee(By.xpath(locator));
		return driver.findElement(By.xpath(locator)).getAttribute(attribute).toString();
	}

	public boolean isElementPresentOnPage(String locator, String action) 
	{
		boolean value = true;
		try 
		{
			if(action.equals("visible"))
			{
				iWillWaitToSee(By.xpath(locator));
			}
			driver.findElement(By.xpath(locator));
		} 
		catch (Exception e) 
		{
			value = false;
		}
		return value;
	}

	public void scrollIntoViewElement(String locator) 
	{
		iWillWaitToSee(By.xpath(locator));
		WebElement element = driver.findElement(By.xpath(locator));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: \"center\"});", element);
	}

	public void mouseHover(String locator) 
	{
		iWillWaitToSee(By.xpath(locator));
		WebElement element = driver.findElement(By.xpath(locator));
		Actions action = new Actions(driver);
		action.moveToElement(element).build().perform();
	}
	
	public void scrollToTopOfThePage() 
	{
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollTo(0, -document.body.scrollHeight)");
	}

	public void selectAllHealthQuestions() 
	{
		List<WebElement> questions = driver.findElements(By.xpath("//div[@data-controller='questions']//input[@value='no']"));
		for(int i=0;i<questions.size();i++)
		{
			questions.get(i).click();
		}
	}

	public void scrollToBottomOfThePage() 
	{
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	}

	public void delay(long seconds) throws InterruptedException 
	{
		TimeUnit.SECONDS.sleep(seconds);
	}

	public void selectByVisibleText(String visibleText, String locator) 
	{
		try
		{
			Select sel = new Select(driver.findElement(By.xpath(locator)));
			sel.selectByVisibleText(visibleText);
		}
		catch(Exception e)
		{
			Select sel = new Select(driver.findElement(By.xpath(locator)));
			sel.selectByValue(visibleText);
		}
	}

	public boolean isValuePresentInDropdown(String text, String locator)
	{
		boolean flag = false;
		iWillWaitToSee(By.xpath(locator));
		Select sel = new Select(driver.findElement(By.xpath(locator)));
		List<WebElement> DrpDwnList=sel.getOptions();
		for(WebElement indElem:DrpDwnList)
		{
			if (indElem.getText().equals(text))
			{
				flag=true;
				break;
			}
		}
		return flag;
	}
	
	public void navigateBackwards()
	{
		driver.navigate().back();
	}
	
	public void navigateForward()
	{
		driver.navigate().forward();;
	}
	
	public void refresh()
	{
		driver.navigate().refresh();
	}
	
	public void removeSelectedCounties() throws InterruptedException
	{
		int a = driver.findElements(By.xpath("//div[@id='user_county_ids_chosen']//a[@class='search-choice-close']")).size();
		for(int i=0; i<a;i++)
		{
			driver.findElement(By.xpath("//div[@id='user_county_ids_chosen']//a[@class='search-choice-close']")).click();
			Thread.sleep(1000);
		}
	}
	
	public void switchToFrame() 
	{
		driver.switchTo().frame(0);
	}

	public void switchToDefaultContent() 
	{
		driver.switchTo().defaultContent();
	}

	public void dismissAlert() 
	{
		waitFor().until(ExpectedConditions.alertIsPresent());
		driver.switchTo().alert().dismiss();
	}

	public void acceptAlert() 
	{
		waitFor().until(ExpectedConditions.alertIsPresent());
		driver.switchTo().alert().accept();
	}
	
	public boolean ocularImageComparison(String fileName, String locator) throws IOException
	{
		boolean flag = false;
		Ocular.config().resultPath(Paths.get(System.getProperty("user.dir")+"\\target\\")).snapshotPath(Paths.get(System.getProperty("user.dir")+"\\src\\test\\Import\\")).globalSimilarity(95).saveSnapshot(false);
		Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100)).takeScreenshot(driver,driver.findElement(By.xpath(locator)));
        ImageIO.write(screenshot.getImage(),"PNG",new File(System.getProperty("user.dir")+"\\target\\elementAShotScreenshot.png"));
		OcularResult result = Ocular.snapshot().from(Paths.get(System.getProperty("user.dir")+"\\src\\test\\Import\\"+fileName)).sample().using(Paths.get(System.getProperty("user.dir")+"\\target\\elementAShotScreenshot.png")).compare();
		if(result.getSimilarity()==100)
		{
			flag = true;
		}
		return flag;
	}
	
	public HashMap getMessageFromMailbox(String username, String password, String action, String email) throws MessagingException, IOException, InterruptedException
	{
//		Thread.sleep(5000);
		HashMap<String,String> mailContents = new HashMap<>();
		Store store = null;
		Folder folderInbox = null;
			
		for(int j=1;j<=5;j++)
			{
				try
				{
					System.out.println("Attempt - "+j);
					Message[] foundMessages=null;
					String mail = email;
					if(mail.contains("FromHashMap"))
					{
						mail = base.mspp.get(email.substring(0, email.indexOf("FromHashMap")));
					}
					else if(mail.contains("FromEmailHashMap"))
					{
						mail = base.emails.get(email.substring(0, email.indexOf("FromEmailHashMap")));
					}
					final String mail1 = mail;
					Properties properties = new Properties();
					properties.put("mail.imap.host", "imap.gmail.com");
					properties.put("mail.imap.port", "993");
					properties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
					properties.setProperty("mail.imap.socketFactory.fallback", "false");
					properties.setProperty("mail.imap.socketFactory.port", String.valueOf("993"));
					Session session = Session.getDefaultInstance(properties);
					store = session.getStore("imap");
					store.connect(username, password);
					folderInbox = store.getFolder("INBOX");
					folderInbox.open(Folder.READ_WRITE);
					SearchTermBuilder stb = new SearchTermBuilder() {
				        @Override
				        public SearchTerm build() {
				            SearchTerm[] terms = {
				            	new AndTerm(new RecipientStringTerm(Message.RecipientType.TO, mail1),new FlagTerm(new Flags(Flags.Flag.SEEN), false))
				            };
				            return new OrTerm(terms);
				        }
				    };
		            foundMessages = folderInbox.search(stb.build());
					String body="";
					String subject="";
					String sender="";
					
					for (int i = 0; i<foundMessages.length; i++) 
					{
						Message message = foundMessages[i];
						if(message.getAllRecipients()[0].toString().trim().equals(mail1))
						{
							subject = message.getSubject();
							mailContents.put("Subject", subject);
							Address[] address =message.getFrom();
							sender=address[0].toString();
							mailContents.put("Sender",sender);
							if(message.getContentType().contains("TEXT/HTML"))
							{
								if(action.equals("Body"))
								{
									String a = message.getContent().toString();
									body = org.jsoup.Jsoup.parse(a).text();
									mailContents.put("Body",body);
								}
								else if(action.equals("Document"))
								{
									String a = message.getContent().toString();
									Document d = org.jsoup.Jsoup.parse(a);
									mailContents.put("Body",d.toString());
								}
							}
							else
							{
								Multipart multipart = (Multipart) foundMessages[i].getContent();
								for (int x = 0; x < multipart.getCount(); x++) 
								{	
									BodyPart bodyPart = multipart.getBodyPart(x);
									if(action.equals("Body"))
									{
										mailContents.put("Body",org.jsoup.Jsoup.parse((String) bodyPart.getContent()).text());
									}
									else if(action.equals("Document"))
									{
										Document d = org.jsoup.Jsoup.parse((String) bodyPart.getContent());
										mailContents.put("Body",d.toString());
									}
								}
							}
//							message.setFlag(Flags.Flag.DELETED, true);
//							folderInbox.expunge();
							break;
						}
					}
					if(mailContents.get("Body")!=null)
					{
						break;
					}
					else
					{
						Thread.sleep(3000);
					}
				}
			
			finally
			{
				if(folderInbox != null)
				{
					folderInbox.close();
				}
				if(store != null)
				{
					store.close();
				}
			}
		}
		return mailContents;
	}
	
	
//	public HashMap getMessageFromMailbox1() throws MessagingException, IOException, InterruptedException
//	{

	    Properties properties = null;
	    Session session = null;
	    private Store store = null;
	    private Folder inbox = null;
	    private String userName = "";
	    private String password = "";
	    private String saveDirectory = System.getProperty("user.dir") + "\\SaveEmails";


//	    @Test
//	    public void main1() {
//	        MailReading sample = new MailReading();
//	        sample.readMails();
//	    }

	    public String readMails() throws Exception {
	    	
	    	List<String> containedUrls = new ArrayList<String>();
	        properties = new Properties();
	        properties.setProperty("mail.store.protocol", "imaps");
	        try {
	            session = Session.getDefaultInstance(properties, null);
	            store = session.getStore("imaps");
	            store.connect("imap.gmail.com", userName, password);
	            inbox = store.getFolder("INBOX");
//no of mails
	            int unreadMailCount = inbox.getUnreadMessageCount();
	            inbox.open(Folder.READ_WRITE);
	            Message messages[] = inbox.getMessages();
	            for (int i = (messages.length); i > (messages.length - unreadMailCount); i--) {
	                Message message = messages[i - 1];
	                Address[] from = message.getFrom();
//	                System.out.println("Subject: " + message.getSubject());
	                String contentType = message.getContentType();
	                String messageContent = "";
	                String attachFiles = "";

	                if (contentType.contains("multipart")) {
	                    Multipart multiPart = (Multipart) message.getContent();
	                    int numberOfParts = multiPart.getCount();
	                    for (int partCount = 0; partCount < numberOfParts; partCount++) {
	                        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
//	                        System.out.println("Body---"+part);
	                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
	                            String fileName = part.getFileName();
	                            attachFiles += fileName + ", ";
	                            part.saveFile(saveDirectory + File.separator + fileName);
	                        } else {
	                            messageContent = part.getContent().toString();
	                            //For body
//	                            System.out.println("Body is "+messageContent);
//	                            messageContent.substring(0,messageContent.lastIndexOf(" opened"));
//	                            List<String> containedUrls = new ArrayList<String>();
	                            String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
	                            Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
	                            Matcher urlMatcher = pattern.matcher(messageContent);

	                            while (urlMatcher.find())
	                            {
	                                containedUrls.add(messageContent.substring(urlMatcher.start(0),
	                                        urlMatcher.end(0)));
	                            }
	                            System.out.println(containedUrls.get(0));
	                            break;
	                        }
	                        
	                    }

	                    if (attachFiles.length() > 1) {
	                        attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
	                    }
	                } else if (contentType.contains("text/plain")
	                        || contentType.contains("text/html")) {
	                    String content = (String) message.getContent();
	                    
	                    if (content != null) {
	                        messageContent = content.toString();	                 
	                        
	                    }
	                }
	            }
	          

	            // disconnect
	            inbox.close(false);
	            store.close();
	        } catch (NoSuchProviderException ex) {
	            System.out.println("No provider for pop3.");
	            ex.printStackTrace();
	        } catch (MessagingException ex) {
	            System.out.println("Could not connect to the message store");
	            ex.printStackTrace();
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	  
	        driver.navigate().to(containedUrls.get(0));
            Thread.sleep(5000);
            
			return containedUrls.get(0);

	    }  
					
	public void verifyUser() throws IOException, MessagingException
	{
	}
	
	public void fetchURLFromEmail(DataTable table) throws MessagingException, IOException, InterruptedException
	{
		Map<String,String> map =  table.asMap(String.class,String.class);
		HashMap<String,String> mailContents = getMessageFromMailbox("", "","Document",map.get("Email"));
		String body = mailContents.get("Body");
		for(String key : map.keySet())
		{
			if(key.equals("RescheduleURL"))
			{
				base.mspp.put("RescheduleURL", body.substring(body.indexOf("To reschedule your appointment, click <a href=\"")+47, body.indexOf("Reschedule")-2));
			}
			else if(key.equals("CancelURL"))
			{
				base.mspp.put("CancelURL", body.substring(body.indexOf("To cancel your appointment, click <a href=\"")+43, body.indexOf("Cancel")-2));
			}
			else if(key.equals("ScheduleURL"))
			{
				base.mspp.put("ScheduleURL", body.substring(body.indexOf("https"), body.indexOf("\">If you are still")));
			}
			else if(key.equals("ScheduleAppointmentURL"))
			{
				base.mspp.put("ScheduleAppointmentURL", body.substring(body.indexOf("https"), body.indexOf("\">Schedule Appointment")));
			}
			else if(key.equals("EmailVerificationURL"))
			{
				base.mspp.put("EmailVerificationURL", body.substring(body.indexOf("https"), body.indexOf("\">Verify my email address")));
			}
		}
		System.out.println(base.mspp.toString());
	}
		
	public boolean verifyEmailText(DataTable table) throws IOException, MessagingException, InterruptedException, ParseException
	{
		Map<String,String> map =  table.asMap(String.class,String.class);
		HashMap<String,String> mailContents = getMessageFromMailbox("", "","Body",map.get("Email"));
		String subject=mailContents.get("Subject");
		String body = mailContents.get("Body");
		System.out.println(body);
		System.out.println(body);
		String expectedBody="";
		String expectedBody1="";
		String expectedBody2="";
		String expectedBody3="";
		String date="";
		String date1="";
		String date2="";
        String sender=mailContents.get("Sender");
		boolean flag = false;
		if(map.get("Date").contains("TodaysDate"))
		{
			if(map.get("Date").contains("-"))
			{
				Date todaysDate = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				Calendar c = Calendar.getInstance();
				c.setTime(todaysDate);
				c.add(Calendar.DATE, -Integer.parseInt(map.get("Date").substring(map.get("Date").indexOf("-")+1)));
				date = sdf.format(c.getTime());
				Date day=sdf.parse(date);
				sdf.applyPattern("EEEE, MM/dd/yyyy");
				date1=sdf.format(day);
				//Date month=sdf.parse(date);
				sdf.applyPattern("EEEE MMMM dd, yyyy");
				date2=sdf.format(day);

			}
			else if(map.get("Date").contains("+"))
			{
				Date todaysDate = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				Calendar c = Calendar.getInstance();
				c.setTime(todaysDate);
				c.add(Calendar.DATE, Integer.parseInt(map.get("Date").substring(map.get("Date").indexOf("+")+1)));
				date = sdf.format(c.getTime());
				Date day=sdf.parse(date);
				sdf.applyPattern("EEEE, MM/dd/yyyy");
				date1=sdf.format(day);
				//Date month=sdf.parse(date);
				sdf.applyPattern("EEEE MMMM dd, yyyy");
				date2=sdf.format(day);

			}
		}
		else
		{
			date = map.get("Date");
		}
		
		if(map.get("Subject").equals("Your Appointment is Confirmed!") && map.containsKey("FirstDependentName") && map.containsKey("ClinicType"))
		{
			if(map.get("State").equals("Generic"))
			{
				expectedBody = "This message is to confirm that "+map.get("Name")+" is scheduled for a testing appointment at: Venue/Location: "+base.mspp.get("Venue")+" Address: "+map.get("Address")+" Date: "+date1+" Time: "+map.get("Time")+" To reschedule your appointment, click Reschedule To cancel your appointment, click Cancel";
				expectedBody1 = map.get("FirstDependentName")+" Time: "+map.get("FirstDependentTime")+" To reschedule your appointment, click Reschedule To cancel your appointment, click Cancel";
				expectedBody2 = map.get("SecondDependentName")+" Time: "+map.get("SecondDependentTime")+" To reschedule your appointment, click Reschedule To cancel your appointment, click Cancel";
				expectedBody3 = map.get("ThirdDependentName")+" Time: "+map.get("ThirdDependentTime")+" To reschedule your appointment, click Reschedule To cancel your appointment, click Cancel";
			}
			
			if(body.contains(expectedBody))
			{
				if(body.contains(expectedBody1))
				{
					if(body.contains(expectedBody2))
					{
						if(body.contains(expectedBody3))
						{
							if(subject.equals(map.get("Subject")))
							{
								if(sender.equals("Vaccination Clinics <no-reply@multistatep4p.com>"))
								{
									flag = true;
								}
							}
						}
					}
				}
			}
			return flag;
		}
		else if(map.get("Subject").equals("Your Appointment is Confirmed!") && map.containsKey("FirstDependentName"))
		{
			if(map.get("State").equals("Generic"))
			{
				expectedBody = "This message is to confirm that "+map.get("Name")+" is scheduled for a vaccination appointment at: Venue/Location: "+base.mspp.get("Venue")+" Address: "+map.get("Address")+" Date: "+date1+" Time: "+map.get("Time")+" To reschedule your appointment, click Reschedule To cancel your appointment, click Cancel";
				expectedBody1 = map.get("FirstDependentName")+" Time: "+map.get("FirstDependentTime")+" To reschedule your appointment, click Reschedule To cancel your appointment, click Cancel";
				expectedBody2 = map.get("SecondDependentName")+" Time: "+map.get("SecondDependentTime")+" To reschedule your appointment, click Reschedule To cancel your appointment, click Cancel";
				expectedBody3 = map.get("ThirdDependentName")+" Time: "+map.get("ThirdDependentTime")+" To reschedule your appointment, click Reschedule To cancel your appointment, click Cancel";
			}
			
			if(body.contains(expectedBody))
			{
				if(body.contains(expectedBody1))
				{
					if(body.contains(expectedBody2))
					{
						if(body.contains(expectedBody3))
						{
							if(subject.equals(map.get("Subject")))
							{
								if(sender.equals("Vaccination Clinics <no-reply@multistatep4p.com>"))
								{
									flag = true;
								}
							}
						}
					}
				}
			}
			return flag;
		}
		else if(map.get("Subject").equals("Your Appointment is Confirmed!") && map.containsKey("ClinicType"))
		{
			if(map.get("State").equals("Generic"))
			{
				if(map.get("Time").equals("Walk In"))
				{
					expectedBody = "This message is to confirm that "+map.get("Name")+" is scheduled for a testing appointment at: Venue/Location: "+base.mspp.get("Venue")+" Address: "+map.get("Address")+" Date: Walk In Time: "+map.get("Time")+" To cancel your appointment, click Cancel We look forward to seeing you!";
				}
				else
				{
					expectedBody = "This message is to confirm that "+map.get("Name")+" is scheduled for a testing appointment at: Venue/Location: "+base.mspp.get("Venue")+" Address: "+map.get("Address")+" Date: "+date1+" Time: "+map.get("Time")+" To reschedule your appointment, click Reschedule To cancel your appointment, click Cancel We look forward to seeing you!";
				}
			}
			else if(map.get("State").equals("Polk County"))
			{
				if(map.get("Time").equals("Walk In"))
				{
					expectedBody = "This message is to confirm that "+map.get("Name")+" is scheduled for a testing appointment at: Venue/Location: "+base.mspp.get("Venue")+" Address: "+map.get("Address")+" Date: Walk In Time: "+map.get("Time")+" To cancel your appointment, click Cancel We look forward to seeing you!";
				}
				else
				{
					expectedBody = "This message is to confirm that "+map.get("Name")+" is scheduled for a testing appointment at: Venue/Location: "+base.mspp.get("Venue")+" Address: "+map.get("Address")+" Date: "+date1+" Time: "+map.get("Time")+" To cancel your appointment, click Cancel We look forward to seeing you!";
				}
			}
//			else if(map.get("State").equals("Rhode Island"))
//			{
//				expectedBody = "This message is to confirm that "+map.get("Name")+" is scheduled for a vaccination appointment at: Venue/Location: "+base.mspp.get("Venue")+" Address: "+map.get("Address")+" Date: "+date1+" Time: "+map.get("Time")+" To reschedule your appointment, click Reschedule To cancel your appointment, click Cancel We look forward to seeing you! "+base.mspp.get("Organization")+" Please DO NOT REPLY TO OR SEND email to this address. Your message will not be returned. Please contact your vaccination provider. The contents of this email is confidential and intended for the recipient specified in the message only. It is strictly forbidden to share any part of this message with any third party. If you are not the intended recipient, you are hereby notified that you have received this communication in error and that any review, disclosure, dissemination, distribution or copying of it or its contents is strictly prohibited. If you received this message, please reply to this message and follow with its deletion, so that we can ensure such a mistake does not occur in the future. For more information about COVID-19 vaccine in Rhode Island, visit www.C19vaccineRI.org";
//			}
			System.out.println("ExpectedBody - "+expectedBody);
			System.out.println("ActualBody - "+body);
			if(body.contains(expectedBody))
			{
				if(subject.equals(map.get("Subject")))
				{
					if(sender.equals("Vaccination Clinics <no-reply@multistatep4p.com>"))
					{
						flag = true;
					}
				}
			}
			return flag;
		}
		else if(map.get("Subject").equals("Your Appointment is Confirmed!"))
		{
			if(map.get("State").equals("Generic"))
			{
				if(map.get("Time").equals("Walk In"))
				{
					expectedBody = "This message is to confirm that "+map.get("Name")+" is scheduled for a vaccination appointment at: Venue/Location: "+base.mspp.get("Venue")+" Address: "+map.get("Address")+" Date: Walk In Time: "+map.get("Time")+" To cancel your appointment, click Cancel We look forward to seeing you!";
				}
				else
				{
					expectedBody = "This message is to confirm that "+map.get("Name")+" is scheduled for a vaccination appointment at: Venue/Location: "+base.mspp.get("Venue")+" Address: "+map.get("Address")+" Date: "+date1+" Time: "+map.get("Time")+" To reschedule your appointment, click Reschedule To cancel your appointment, click Cancel We look forward to seeing you!";
				}
			}
			else if(map.get("State").equals("Polk County"))
			{
				if(map.get("Time").equals("Walk In"))
				{
					expectedBody = "This message is to confirm that "+map.get("Name")+" is scheduled for a vaccination appointment at: Venue/Location: "+base.mspp.get("Venue")+" Address: "+map.get("Address")+" Date: Walk In Time: "+map.get("Time")+" To cancel your appointment, click Cancel We look forward to seeing you!";
				}
				else
				{
					expectedBody = "This message is to confirm that "+map.get("Name")+" is scheduled for a vaccination appointment at: Venue/Location: "+base.mspp.get("Venue")+" Address: "+map.get("Address")+" Date: "+date1+" Time: "+map.get("Time")+" To cancel your appointment, click Cancel We look forward to seeing you!";
				}
			}
//			else if(map.get("State").equals("Rhode Island"))
//			{
//				expectedBody = "This message is to confirm that "+map.get("Name")+" is scheduled for a vaccination appointment at: Venue/Location: "+base.mspp.get("Venue")+" Address: "+map.get("Address")+" Date: "+date1+" Time: "+map.get("Time")+" To reschedule your appointment, click Reschedule To cancel your appointment, click Cancel We look forward to seeing you! "+base.mspp.get("Organization")+" Please DO NOT REPLY TO OR SEND email to this address. Your message will not be returned. Please contact your vaccination provider. The contents of this email is confidential and intended for the recipient specified in the message only. It is strictly forbidden to share any part of this message with any third party. If you are not the intended recipient, you are hereby notified that you have received this communication in error and that any review, disclosure, dissemination, distribution or copying of it or its contents is strictly prohibited. If you received this message, please reply to this message and follow with its deletion, so that we can ensure such a mistake does not occur in the future. For more information about COVID-19 vaccine in Rhode Island, visit www.C19vaccineRI.org";
//			}
			System.out.println("ExpectedBody - "+expectedBody);
			System.out.println("ActualBody - "+body);
			if(body.contains(expectedBody))
			{
				if(subject.equals(map.get("Subject")))
				{
					if(sender.equals("Vaccination Clinics <no-reply@multistatep4p.com>"))
					{
						flag = true;
					}
				}
			}
			return flag;
		}
		else if(map.get("Subject").equals("You are on the Waiting List"))
		{
			if(map.get("State").equals("Generic"))
			{
				//expectedBody = "You will be contacted if an appointment becomes available. Be safe!";
				expectedBody = "Your vaccination appointment has been updated. You have been added to the Wait List for "+date2+"."+" Venue/Location: "+base.mspp.get("Venue")+" Address: "+map.get("Address")+" Time: "+map.get("Time");

			}
//			else if(map.get("State").equals("Rhode Island"))
//			{
//				expectedBody = "You will be contacted if an appointment becomes available. Be safe! "+base.mspp.get("Organization")+" Please DO NOT REPLY TO OR SEND email to this address. Your message will not be returned. Please contact your vaccination provider. The contents of this email is confidential and intended for the recipient specified in the message only. It is strictly forbidden to share any part of this message with any third party. If you are not the intended recipient, you are hereby notified that you have received this communication in error and that any review, disclosure, dissemination, distribution or copying of it or its contents is strictly prohibited. If you received this message, please reply to this message and follow with its deletion, so that we can ensure such a mistake does not occur in the future. For more information about COVID-19 vaccine in Rhode Island, visit www.C19vaccineRI.org";
//			}
			System.out.println("ExpectedBody - "+expectedBody);
			System.out.println("ActualBody - "+body);
			if(body.contains(expectedBody))
			{
				if(subject.equals(map.get("Subject")))
				{
					if(sender.equals("Vaccination Clinics <no-reply@multistatep4p.com>"))
					{
						flag = true;
					}
				}
			}
			return flag;
		}
		else if(map.get("Subject").equals("Appointment allocated") && map.containsKey("ClinicType"))
		{
			if(map.get("State").equals("Generic"))
			{
				expectedBody = "Your Testing Registration Dear "+map.get("Name")+", A testing appointment has become available. You have an appointment on "+date1+" Venue/Location: "+base.mspp.get("Venue")+" Address: "+map.get("Address")+" Time: "+map.get("Time")+" To cancel the appointment click: Cancel To reschedule the appointment click: Reschedule Thank you";
			}
//			else if(map.get("State").equals("Rhode Island"))
//			{
//				expectedBody = "Your Vaccination Registration Dear "+map.get("Name")+", A vaccination appointment has become available. You have an appointment on "+date1+" Venue/Location: "+base.mspp.get("Venue")+" Address: "+map.get("Address")+" Time: "+map.get("Time")+" To cancel the appointment click: Cancel To reschedule the appointment click: Reschedule Thank you, " +base.mspp.get("Organization")+ " Please DO NOT REPLY TO OR SEND email to this address. Your message will not be returned. Please contact your vaccination provider. The contents of this email is confidential and intended for the recipient specified in the message only. It is strictly forbidden to share any part of this message with any third party. If you are not the intended recipient, you are hereby notified that you have received this communication in error and that any review, disclosure, dissemination, distribution or copying of it or its contents is strictly prohibited. If you received this message, please reply to this message and follow with its deletion, so that we can ensure such a mistake does not occur in the future. For more information about COVID-19 vaccine in Rhode Island, visit www.C19vaccineRI.org";
//			}
			System.out.println("ExpectedBody - "+expectedBody);
			System.out.println("ActualBody - "+body);
			if(body.contains(expectedBody))
			{
				if(subject.equals(map.get("Subject")))
				{
					if(sender.equals("Vaccination Clinics <no-reply@multistatep4p.com>"))
					{
						flag = true;
					}
				}
			}
			return flag;
		}
		else if(map.get("Subject").equals("Appointment allocated"))
		{
			if(map.get("State").equals("Generic"))
			{
				expectedBody = "Your Vaccination Registration Dear "+map.get("Name")+", A vaccination appointment has become available. You have an appointment on "+date1+" Venue/Location: "+base.mspp.get("Venue")+" Address: "+map.get("Address")+" Time: "+map.get("Time")+" To cancel the appointment click: Cancel To reschedule the appointment click: Reschedule Thank you";
			}
//			else if(map.get("State").equals("Rhode Island"))
//			{
//				expectedBody = "Your Vaccination Registration Dear "+map.get("Name")+", A vaccination appointment has become available. You have an appointment on "+date1+" Venue/Location: "+base.mspp.get("Venue")+" Address: "+map.get("Address")+" Time: "+map.get("Time")+" To cancel the appointment click: Cancel To reschedule the appointment click: Reschedule Thank you, " +base.mspp.get("Organization")+ " Please DO NOT REPLY TO OR SEND email to this address. Your message will not be returned. Please contact your vaccination provider. The contents of this email is confidential and intended for the recipient specified in the message only. It is strictly forbidden to share any part of this message with any third party. If you are not the intended recipient, you are hereby notified that you have received this communication in error and that any review, disclosure, dissemination, distribution or copying of it or its contents is strictly prohibited. If you received this message, please reply to this message and follow with its deletion, so that we can ensure such a mistake does not occur in the future. For more information about COVID-19 vaccine in Rhode Island, visit www.C19vaccineRI.org";
//			}
			System.out.println("ExpectedBody - "+expectedBody);
			System.out.println("ActualBody - "+body);
			if(body.contains(expectedBody))
			{
				if(subject.equals(map.get("Subject")))
				{
					if(sender.equals("Vaccination Clinics <no-reply@multistatep4p.com>"))
					{
						flag = true;
					}
				}
			}
			return flag;
		}
		else if(map.get("Subject").equals("Your Vaccination Appointment"))
		{
			if(map.get("State").equals("Generic"))
			{
				expectedBody = "Dear "+map.get("Name")+": Congratulations! You've been vaccinated! Over the next few days, you might experience mild side effects. If you have severe symptoms, please contact your healthcare provider.";
			}
			else if(map.get("State").equals("Maryland"))
			{
				expectedBody = "Dear "+map.get("Name")+": Congratulations! You've been vaccinated! Over the next few days, you might experience mild side effects. If you have severe symptoms, please contact your healthcare provider. Click below to access your vaccination record. Please keep this file for your records. Download Patient Record Sincerely,";
			}
			else if(map.get("State").equals("North Dakota"))
			{
				expectedBody = "Dear "+map.get("Name")+": Congratulations! You've been vaccinated!";
			}
			else if(map.get("State").equals("Rhode Island"))
			{
				expectedBody = "Dear "+map.get("Name")+": Congratulations — you’ve been vaccinated against COVID-19! The vaccine card you got at the time of your vaccination is the best proof of your immunization status. Please keep it in a safe place for your records. You can find information about your most recent vaccination here: Download Patient Record Vaccines often cause our immune systems to respond, and that shows us the vaccine is working. This is healthy, normal, and expected. Over the next few days, you may experience a sore arm, headache, fever, or body aches, but they should go away within a few days. If you have any symptoms of COVID-19 following vaccination, stay home, call your healthcare provider, and get tested. If you have severe symptoms, please contact your healthcare provider. What to expect after you get your vaccine (English only): https://youtu.be/EILCpte7GSw (CDC video) Please continue to wear a mask, watch your distance, and wash your hands after you are vaccinated. Thank you for helping us Crush COVID in Rhode Island! Sincerely,";
			}
//			else if(map.get("State").equals("Washington"))
//			{
//				expectedBody = "Dear "+map.get("Name")+": Congratulations! You've been vaccinated! Please keep this for your records. Over the next few days, you might experience mild side effects. If you have severe symptoms, please contact your healthcare provider. Download Patient Record Sincerely, Your Vaccination Provider. Please DO NOT REPLY TO OR SEND email to this address. Your message will not be returned. Please contact your vaccination provider. The contents of this email is confidential and intended for the recipient specified in the message only. It is strictly forbidden to share any part of this message with any third party. If you are not the intended recipient, you are hereby notified that you have received this communication in error and that any review, disclosure, dissemination, distribution or copying of it or its contents is strictly prohibited. If you received this message, please reply to this message and follow with its deletion, so that we can ensure such a mistake does not occur in the future.";
//			}
			System.out.println("ExpectedBody - "+expectedBody);
			System.out.println("ActualBody - "+body);
			if(body.contains(expectedBody))
			{
				if(subject.equals(map.get("Subject")))
				{
					if(sender.equals("Vaccination Clinics <no-reply@multistatep4p.com>"))
					{
						flag = true;
					}
				}
			}
			return flag;
		}
		
		else if(map.get("Subject").equals("Your Appointment has been Rescheduled!"))
		{
			if(map.get("State").equals("Generic"))
			{
				expectedBody = "Dear "+map.get("Name")+", Your appointment has been rescheduled to "+date1+" at "+map.get("Time")+". Thank you";
			}
//			else if(map.get("State").equals("Rhode Island"))
//			{
//				expectedBody = "Dear "+map.get("Name")+", Your appointment has been rescheduled to "+date1+" at "+map.get("Time")+". Thank you, "+base.mspp.get("Organization")+" Please DO NOT REPLY TO OR SEND email to this address. Your message will not be returned. Please contact your vaccination provider. The contents of this email is confidential and intended for the recipient specified in the message only. It is strictly forbidden to share any part of this message with any third party. If you are not the intended recipient, you are hereby notified that you have received this communication in error and that any review, disclosure, dissemination, distribution or copying of it or its contents is strictly prohibited. If you received this message, please reply to this message and follow with its deletion, so that we can ensure such a mistake does not occur in the future. For more information about COVID-19 vaccine in Rhode Island, visit www.C19vaccineRI.org";
//			}
			System.out.println("ExpectedBody - "+expectedBody);
			System.out.println("ActualBody - "+body);
			if(body.contains(expectedBody))
			{
				if(subject.equals(map.get("Subject")))
				{
					if(sender.equals("Vaccination Clinics <no-reply@multistatep4p.com>"))
					{
						flag = true;
					}
				}
			}
			return flag;
		}
		
		else if(map.get("Subject").equals("Your Appointment has been Canceled"))
		{
			if(map.get("State").equals("Generic"))
			{
				expectedBody = "Dear "+map.get("Name")+", Your "+map.get("Time")+" appointment located at "+base.mspp.get("Venue")+" on "+date1+", "+map.get("Address")+" has been canceled. Sincerely";
			}
//			else if(map.get("State").equals("Anoka"))
//			{
//				expectedBody = "Dear "+map.get("Name")+", Your "+map.get("Time")+" appointment located at "+base.mspp.get("Venue")+" on "+date1+", "+map.get("Address")+" has been canceled. Sincerely, Anoka County Public Health The contents of this email is confidential and intended for the recipient specified in the message only. It is strictly forbidden to share any part of this message with any third party. If you are not the intended recipient, you are hereby notified that you have received this communication in error and that any review, disclosure, dissemination, distribution or copying of it or its contents is strictly prohibited. If you received this message, please reply to this message and follow with its deletion, so that we can ensure such a mistake does not occur in the future.";
//			}
//			else if(map.get("State").equals("Rhode Island"))
//			{
//				expectedBody = "Dear "+map.get("Name")+", Your "+map.get("Time")+" appointment located at "+base.mspp.get("Venue")+" on "+date1+", "+map.get("Address")+" has been canceled. Sincerely, "+base.mspp.get("Organization")+ " The contents of this email is confidential and intended for the recipient specified in the message only. It is strictly forbidden to share any part of this message with any third party. If you are not the intended recipient, you are hereby notified that you have received this communication in error and that any review, disclosure, dissemination, distribution or copying of it or its contents is strictly prohibited. If you received this message, please reply to this message and follow with its deletion, so that we can ensure such a mistake does not occur in the future. For more information about COVID-19 vaccine in Rhode Island, visit www.C19vaccineRI.org";
//			}
			System.out.println("ExpectedBody - "+expectedBody);
			System.out.println("ActualBody - "+body);
			if(body.contains(expectedBody))
			{
				if(subject.equals(map.get("Subject")))
				{
					if(sender.equals("Vaccination Clinics <no-reply@multistatep4p.com>"))
					{
						flag = true;
					}
				}
			}
			return flag;
		}
		else if(map.get("Subject").equals("Upcoming appointment reminder"))
		{
			if(map.get("State").equals("Generic"))
			{
				expectedBody = "Upcoming appointment reminder We look forward to seeing you on "+date1+", "+map.get("Time")+" at "+map.get("Address")+". If you cannot make this appointment, please alert us using these links: To cancel the appointment click: Cancel To reschedule the appointment click: Reschedule Thank you";
			}
			else if(map.get("State").equals("Polk County"))
			{
				expectedBody = "Upcoming appointment reminder We look forward to seeing you on "+date1+", "+map.get("Time")+" at "+map.get("Address")+". If you cannot make this appointment, please alert us using these links: To cancel the appointment click: Cancel Thank you";
			}
//			else if(map.get("State").equals("Rhode Island"))
//			{
//				expectedBody = "Upcoming appointment reminder We look forward to seeing you on "+date1+", "+map.get("Time")+" at "+map.get("Address")+". If you cannot make this appointment, please alert us using these links: To cancel the appointment click: Cancel To reschedule the appointment click: Reschedule Thank you, "+base.mspp.get("Organization")+" Please DO NOT REPLY TO OR SEND email to this address. Your message will not be returned. Please contact your vaccination provider. The contents of this email is confidential and intended for the recipient specified in the message only. It is strictly forbidden to share any part of this message with any third party. If you are not the intended recipient, you are hereby notified that you have received this communication in error and that any review, disclosure, dissemination, distribution or copying of it or its contents is strictly prohibited. If you received this message, please reply to this message and follow with its deletion, so that we can ensure such a mistake does not occur in the future.  For more information about COVID-19 vaccine in Rhode Island, visit www.C19vaccineRI.org";
//			}
			System.out.println("ExpectedBody - "+expectedBody);
			System.out.println("ActualBody - "+body);
			if(body.contains(expectedBody))
			{
				if(subject.equals(map.get("Subject")))
				{
					if(sender.equals("Vaccination Clinics <no-reply@multistatep4p.com>"))
					{
						flag = true;
					}
				}
			}
			return flag;
		}
		else if(map.get("Subject").equals("An appointment for your SECOND DOSE has been scheduled"))
		{
			if(map.get("State").equals("Generic"))
			{
				expectedBody = "Dear "+map.get("Name")+", An appointment for your SECOND DOSE has been scheduled for: Venue/Location: "+base.mspp.get("Venue")+" Address: "+map.get("Address")+" Date: "+date1+" Time: "+map.get("Time");
			}
			System.out.println("ExpectedBody - "+expectedBody);
			System.out.println("ActualBody - "+body);
			if(body.contains(expectedBody))
			{
				if(subject.equals(map.get("Subject")))
				{
					if(sender.equals("Vaccination Clinics <no-reply@multistatep4p.com>"))
					{
						flag = true;
					}
				}
			}
			return flag;
		}
		else if(map.get("Subject").equals("Moved to another clinic"))
		{
			if(map.get("State").equals("Generic"))
			{
				expectedBody = "Your Vaccination Registration Dear "+map.get("Name")+", Your vaccination appointment has been updated. You now have an appointment on "+date1+" Venue/Location: "+base.mspp.get("Venue")+" Address: "+map.get("Address")+" Time: "+map.get("Time");
			}
			System.out.println("ExpectedBody - "+expectedBody);
			System.out.println("ActualBody - "+body);
			if(body.contains(expectedBody))
			{
				if(subject.equals(map.get("Subject")))
				{
					if(sender.equals("Vaccination Clinics <no-reply@multistatep4p.com>"))
					{
						flag = true;
					}
				}
			}
			return flag;
		}
		return flag;
	} 
		
	public void selectDate(String date) throws ParseException, InterruptedException 
	{
		String[] arrMonth = new String[] { "January", "February", "March", "April", "May", "June", "July", "August","September", "October", "November", "December" };
		Date date1 = new SimpleDateFormat("yyyy/MM/dd").parse(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date1);
		int year = cal.get(Calendar.YEAR);
		int iMonth = cal.get(Calendar.MONTH);
		String month = arrMonth[iMonth];
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String displayMonth = driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-day') and not(contains(@class,'prevMonthDay'))]")).getAttribute("aria-label").toString().trim().substring(0, driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-day') and not(contains(@class,'prevMonthDay'))]")).getAttribute("aria-label").toString().trim().indexOf(" "));
		String displayYear = driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-day') and not(contains(@class,'prevMonthDay'))]")).getAttribute("aria-label").toString().trim().substring(driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-day') and not(contains(@class,'prevMonthDay'))]")).getAttribute("aria-label").toString().trim().lastIndexOf(" ")+1);

		if (displayYear.contains(Integer.toString(year))) 
		{
			if (displayMonth.contains(month)) 
			{
				Thread.sleep(3000);
				driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-day') and text()='"+day+"' and not(contains(@class,'flatpickr-disabled')) and not(contains(@class,'prevMonthDay'))]")).click();
			}
			else if ((Arrays.asList(arrMonth).indexOf(displayMonth)) > iMonth) 
			{
				while (!(driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-day') and not(contains(@class,'prevMonthDay')) and not(contains(@class,'nextMonthDay '))]")).getAttribute("aria-label").toString().trim().contains(month))) 
				{
					driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-prev-month')]")).click();
				}
				driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-day') and text()='"+day+"' and not(contains(@class,'flatpickr-disabled')) and not(contains(@class,'prevMonthDay'))]")).click();
			} 
			else if ((Arrays.asList(arrMonth).indexOf(displayMonth)) < iMonth) 
			{
				while (!(driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-day') and not(contains(@class,'prevMonthDay')) and not(contains(@class,'nextMonthDay '))]")).getAttribute("aria-label").toString().trim().contains(month))) 
				{
					driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-next-month')]")).click();
				}
				driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-day') and text()='"+day+"' and not(contains(@class,'flatpickr-disabled')) and not(contains(@class,'prevMonthDay'))]")).click();
			}
		} 
		else if (Integer.parseInt(displayYear) > year) 
		{
			while (!(driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-day') and not(contains(@class,'prevMonthDay')) and not(contains(@class,'nextMonthDay '))]")).getAttribute("aria-label").toString().trim().contains(Integer.toString(year)))) 
			{
				driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-prev-month')]")).click();
			}
			displayMonth = driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-day')]")).getAttribute("aria-label").toString().trim().substring(0, driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[@class='flatpickr-day ']")).getAttribute("aria-label").toString().trim().indexOf(" "));
			displayYear = driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-day')]")).getAttribute("aria-label").toString().trim().substring(driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[@class='flatpickr-day ']")).getAttribute("aria-label").toString().trim().lastIndexOf(" ")+1);
			if (displayMonth.contains(month)) 
			{
				driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-day') and text()='"+day+"' and not(contains(@class,'flatpickr-disabled')) and not(contains(@class,'prevMonthDay'))]")).click();
			} 
			else if ((Arrays.asList(arrMonth).indexOf(displayMonth)) > iMonth) 
			{
				while (!(driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-day') and not(contains(@class,'prevMonthDay')) and not(contains(@class,'nextMonthDay '))]")).getAttribute("aria-label").toString().trim().contains(month))) 
				{
					driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-prev-month')]")).click();
				}
				driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-day') and text()='"+day+"' and not(contains(@class,'flatpickr-disabled')) and not(contains(@class,'prevMonthDay'))]")).click();
			}
			else if ((Arrays.asList(arrMonth).indexOf(displayMonth)) < iMonth) 
			{
				while (!(driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-day') and not(contains(@class,'prevMonthDay')) and not(contains(@class,'nextMonthDay '))]")).getAttribute("aria-label").toString().trim().contains(month))) 
				{
					driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-next-month')]")).click();
				}
				driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-day') and text()='"+day+"' and not(contains(@class,'flatpickr-disabled')) and not(contains(@class,'prevMonthDay'))]")).click();
			}
		} 
		else if (Integer.parseInt(displayYear) < year) 
		{
			while (!(driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-day') and not(contains(@class,'prevMonthDay')) and not(contains(@class,'nextMonthDay '))]")).getAttribute("aria-label").toString().trim().contains(Integer.toString(year)))) 
			{
				driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-next-month')]")).click();
			}
			displayMonth = driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-day')]")).getAttribute("aria-label").toString().trim().substring(0, driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[@class='flatpickr-day ']")).getAttribute("aria-label").toString().trim().indexOf(" "));
			displayYear = driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-day')]")).getAttribute("aria-label").toString().trim().substring(driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[@class='flatpickr-day ']")).getAttribute("aria-label").toString().trim().lastIndexOf(" ")+1);
			if (displayMonth.contains(month)) 
			{
				driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-day') and text()='"+day+"' and not(contains(@class,'flatpickr-disabled')) and not(contains(@class,'prevMonthDay'))]")).click();
			} 
			else if ((Arrays.asList(arrMonth).indexOf(displayMonth)) > iMonth) 
			{
				while (!(driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-day') and not(contains(@class,'prevMonthDay')) and not(contains(@class,'nextMonthDay '))]")).getAttribute("aria-label").toString().trim().contains(month))) 
				{
					driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-prev-month')]")).click();
				}
				driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-day') and text()='"+day+"' and not(contains(@class,'flatpickr-disabled')) and not(contains(@class,'prevMonthDay'))]")).click();
			} 
			else if ((Arrays.asList(arrMonth).indexOf(displayMonth)) < iMonth) 
			{
				while (!(driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-day') and not(contains(@class,'prevMonthDay')) and not(contains(@class,'nextMonthDay '))]")).getAttribute("aria-label").toString().trim().contains(month))) 
				{
					driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-next-month')]")).click();
				}
				driver.findElement(By.xpath("//div[contains(@class,'flatpickr-calendar animate') and contains(@class,'open')]//span[contains(@class,'flatpickr-day') and text()='"+day+"' and not(contains(@class,'flatpickr-disabled')) and not(contains(@class,'prevMonthDay'))]")).click();
			}
		}
	}
	
	public void closeClinics(String param)
	{
		ArrayList al = new ArrayList<>();
		List<WebElement> elements = driver.findElements(By.xpath("//div[@id='flashMessage']//a")); 
		System.out.println("Start of Close clinics for - "+param);
		if(elements.size()>0)
		{
			for(int i=0;i<elements.size();i++)
			{
				al.add(elements.get(i).getAttribute("href"));
			}
			Set<String> set = new HashSet<>(al);
			al.clear();
			al.addAll(set);
			System.out.println("Total Clinics to be Closed - "+al.size());
			for(int j=0;j<al.size();j++)
			{
//				String appURL = configurations.get(param.substring(0,param.indexOf("FromConfig")))+al.get(j);
				driver.get(al.get(j).toString());
				driver.findElement(By.xpath("//button[contains(text(),'Save And Submit')]")).click();
//				if(param.contains("RhodeIsland"))
//				{
					if(driver.findElements(By.xpath("//a[contains(text(),'Continue to Save and Submit')]")).size()>0)
					{
						driver.findElement(By.xpath("//a[contains(text(),'Continue to Save and Submit')]")).click();
					}
					else
					{
						driver.findElement(By.xpath("//p[text()='Are you sure you want to Save and Submit? This will close the clinics for additional information.']/..//a[text()='Yes']")).click();
					}
//				}
////				else
//				{
//					driver.findElement(By.xpath("//p[text()='Are you sure you want to Save and Submit? This will close the clinics for additional information.']/..//a[text()='Yes']")).click();
//				}
				driver.findElement(By.xpath("//p[contains(text(),'Clinic was updated successfully.')]")).isDisplayed();
				driver.findElement(By.xpath("//button[contains(text(),'Okay')]")).click();
			}
		}
		else
		{
			System.out.println(driver.findElement(By.xpath("//p[@id='the-message']")).getText());
			System.out.println("Total Clinics to be Closed - 0");
		}
		System.out.println("End of Close Clinics for - "+param );
	}
}
