package actions;

import java.awt.AWTException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.SearchTerm;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import cucumber.api.DataTable;
import utilities.TestRunner;
import utilities.base;

public class action 
{

	base objBase = new base();

	public void setup(String URL) throws Exception 
	{
		if (URL.contains("FromConfig")) 
		{
			String appURL = base.configurations.get(URL.substring(0,URL.indexOf("FromConfig")));
			objBase.navigateToURL(appURL, base.configurations.get("Browser"),Integer.parseInt(base.configurations.get("ImplicitWait")));
		} 
		else if (URL.contains("FromHashMap"))
		{
			if(URL.contains("https"))
			{
				String clinicID = base.mspp.get(URL.substring(URL.indexOf("=")+1, URL.indexOf("FromHashMap")));
				URL = URL.replace(URL.substring(URL.indexOf("=")+1, URL.length()),clinicID);
				String appURL = URL;
				objBase.navigateToURL(appURL, base.configurations.get("Browser"),Integer.parseInt(base.configurations.get("ImplicitWait")));
			}
			else
			{
				String appURL = base.mspp.get(URL.substring(0,URL.indexOf("FromHashMap")));
				objBase.navigateToURL(appURL, base.configurations.get("Browser"),Integer.parseInt(base.configurations.get("ImplicitWait")));
			}
		}
		else
		{
			objBase.navigateToURL(URL, base.configurations.get("Browser"),Integer.parseInt(base.configurations.get("ImplicitWait")));
		}
	}
	//Clone
	public void CloneURL(String URL) throws Exception 
	{
		objBase.readMails();	
	}

	public String getYamlValue(String key) 
	{
		Map<?, ?> map = null;
		map = objBase.objectRepository;
		StringTokenizer st = new java.util.StringTokenizer(key, ".");
		int tokenCount = st.countTokens();
		for (int i = 1; i < tokenCount; i++) 
		{
			String token = st.nextToken();
			map = (Map<?, ?>) map.get(token);
		}
		return map.get(st.nextToken()).toString();
	}

	public void clickOn(String xpath) throws Exception 
	{
		if (!xpath.equals("")) 
		{
			String locator = getYamlValue(xpath);
			objBase.clickElement(locator);
		}
	}
	
	public void refreshScreen() throws Exception 
	{
		objBase.refreshScreen();
	}
	
	public void clickWithParameter(String xpath, String parameter) throws Exception 
	{
		if (!parameter.equals("")) 
		{
			if(parameter.contains("FromHashMap"))
			{
				parameter = base.mspp.get(parameter.substring(0, parameter.indexOf("FromHashMap")));
			}
			else if(parameter.contains("FromConfig"))
			{
				parameter = base.configurations.get(parameter.substring(0,parameter.indexOf("FromConfig")));
			}
			if(parameter.equals("TodaysDate"))
			{
				Date todaysDate = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				Calendar c = Calendar.getInstance();
				c.setTime(todaysDate);
				parameter = sdf.format(c.getTime());
			}
			else if (parameter.contains("TodaysDate+")) 
			{
				Date todaysDate = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				Calendar c = Calendar.getInstance();
				c.setTime(todaysDate);
				c.add(Calendar.DATE, Integer.parseInt(parameter.substring(parameter.indexOf("+")+1)));
				parameter = sdf.format(c.getTime());
			}
			else if (parameter.contains("TodaysDate-")) 
			{
				Date todaysDate = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				Calendar c = Calendar.getInstance();
				c.setTime(todaysDate);
				c.add(Calendar.DATE, -Integer.parseInt(parameter.substring(parameter.indexOf("-")+1)));
				parameter = sdf.format(c.getTime());
			}
			String locator = getYamlValue(xpath);
			locator = locator.replace("VARIABLE", parameter);
			objBase.clickElement(locator);
		}
	}

	public void switchToWindow(String window) 
	{
		if (window.equalsIgnoreCase("new")) 
		{
			objBase.switchToNewWindow();
		} 
		else
		{
			objBase.switchToOldWindow();
		}
	}

	public void enterText(String text, String xpath) 
	{
		if (!xpath.equals("")) 
		{
			if(!text.equals(""))
			{
				String updatedText = null;
				if(text.contains("Random"))
				{
					updatedText = text.replace("Random", "_"+RandomStringUtils.randomAlphabetic(8).toLowerCase());
					base.mspp.put(text.substring(14,text.indexOf("Random")), updatedText);
					text = updatedText;
					System.out.println(base.mspp.toString());
				}
				else if(text.contains("FromHashMap"))
				{
					text = base.mspp.get(text.substring(0,text.indexOf("FromHashMap")));
				}
				else if(text.contains("FromConfig"))
				{
					text = base.configurations.get(text.substring(0,text.indexOf("FromConfig")));
				}
				else if(text.equalsIgnoreCase("msppautomation@gmail.com"))
				{
					text = "msppautomation+" + RandomStringUtils.randomAlphabetic(8).toLowerCase() + "@gmail.com";
					base.mspp.put("Email", text);
					base.emails.put("Email"+String.valueOf(base.counter), text);
					base.counter++;
					System.out.println(base.mspp.toString());
					System.out.println(base.emails.toString());
				}
				String locator = getYamlValue(xpath);
				objBase.enterTextInElement(text, locator);
			}
		}
	}
	
	public void keyPressEvent(String text, String xpath) throws AWTException
	{
		String locator = getYamlValue(xpath);
		objBase.keyPressEvent(text, locator);
	}

	public void fetchText(String textFor, String xpath) 
	{
		String locator = getYamlValue(xpath);
		String text = objBase.getTextFromElement(locator);
		base.mspp.put(textFor, text);
	}
	
	public void fetchAttribute(String textFor, String attribute, String xpath) 
	{
		String locator = getYamlValue(xpath);
		String text = objBase.getAttributeValueFromElement(locator,attribute);
		base.mspp.put(textFor, text);
	}
	
	public void fetchURLFromEmail(DataTable dt) throws MessagingException, IOException, InterruptedException
	{
		objBase.fetchURLFromEmail(dt);
	}
	
	public void uploadFile(String fileName, String xpath, DataTable dt) throws MessagingException, IOException, CsvException
	{
		Map<String,String> m =  dt.asMap(String.class,String.class);
		String rndm = RandomStringUtils.randomAlphabetic(8).toLowerCase();
		File file = new File(System.getProperty("user.dir")+File.separator+"target"+File.separator+fileName+rndm);
		FileWriter outputfile = new FileWriter(file);
		CSVWriter writer = new CSVWriter(outputfile,',',CSVWriter.NO_QUOTE_CHARACTER,' ',CSVWriter.DEFAULT_LINE_END);
		for(String key : m.keySet())
		{
			String [] data = new String[1];
			
			String a = m.get(key);
			if(a.contains("msppautomation@gmail.com"))
			{
				String b = "msppautomation+" + RandomStringUtils.randomAlphabetic(8).toLowerCase() + "@gmail.com";
				a = a.replace("msppautomation@gmail.com", b);
				base.mspp.put(key+"Email", b);
				System.out.println(base.mspp.toString());
			}
			data[0] = a;	
			writer.writeNext(data);
		}
		writer.close();
		String locator = getYamlValue(xpath);
		objBase.enterTextInElement(System.getProperty("user.dir")+File.separator+"target"+File.separator+fileName+rndm, locator);
	}

	public void clearText(String xpath) 
	{
		String locator = getYamlValue(xpath);
		objBase.clearTextFromElement(locator);
	}

	public void scroll(String action)
	{
		if (action.equals("top")) 
		{
			objBase.scrollToTopOfThePage();
		} 
		else if (action.equals("bottom")) 
		{
			objBase.scrollToBottomOfThePage();
		}
	}

	public void scrollElement(String xpath) 
	{
		String locator = getYamlValue(xpath);
		objBase.scrollIntoViewElement(locator);
	}

	public void mouseHoverOn(String xpath) throws Exception 
	{
		String locator = getYamlValue(xpath);
		objBase.mouseHover(locator);
	}
	
	public void selectAllHealthQuestions() throws Exception 
	{
		objBase.selectAllHealthQuestions();
	}

	public void selectDate(String date) throws ParseException, InterruptedException 
	{
		if (!(date.equals("")))
		{
			if(date.equals("TodaysDate"))
			{
				Date todaysDate = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				Calendar c = Calendar.getInstance();
				c.setTime(todaysDate);
				date = sdf.format(c.getTime());
			}
			else if (date.contains("TodaysDate+")) 
			{
				Date todaysDate = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				Calendar c = Calendar.getInstance();
				c.setTime(todaysDate);
				c.add(Calendar.DATE, Integer.parseInt(date.substring(date.indexOf("+")+1)));
				date = sdf.format(c.getTime());
			}
			else if (date.contains("TodaysDate-")) 
			{
				Date todaysDate = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				Calendar c = Calendar.getInstance();
				c.setTime(todaysDate);
				c.add(Calendar.DATE, -Integer.parseInt(date.substring(date.indexOf("-")+1)));
				date = sdf.format(c.getTime());
			}
			else if (date.equals("FutureDate")) 
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				Date futureDate = new Date();
				Calendar c = Calendar.getInstance();
				c.setTime(futureDate);
				c.add(Calendar.DATE, 365);
				date = sdf.format(c.getTime());
			}
			objBase.selectDate(date);
		}
	}
	
	public void performAction(String action)
    {
        if(action.equals("backward"))
        {
            objBase.navigateBackwards();
        }
        else if(action.equals("forward"))
        {
        	objBase.navigateForward();
        }
        else if(action.equals("refresh"))
        {
        	objBase.refresh();
        }
    }
	
	public void removeSelectedCounties() throws InterruptedException
	{
		objBase.removeSelectedCounties();
	}

	public void waitForSeconds(String seconds) throws NumberFormatException, InterruptedException 
	{
		objBase.delay(Long.parseLong(seconds));
	}

	public void selectObject(String visibleText, String xpath) 
	{
		if(!visibleText.equals(""))
		{
			if(visibleText.contains("FromHashMap"))
			{
				visibleText = base.mspp.get(visibleText.substring(0, visibleText.indexOf("FromHashMap")));
			}
			else if(visibleText.contains("FromConfig"))
			{
				visibleText = base.configurations.get(visibleText.substring(0,visibleText.indexOf("FromConfig")));
			}
			String locator = getYamlValue(xpath);
			objBase.selectByVisibleText(visibleText, locator);
		}
	}
	
	public void selectObjectWithParameter(String visibleText, String xpath, String param) 
	{
		String locator = getYamlValue(xpath);
		if(!visibleText.equals(""))
		{
			if(visibleText.contains("FromHashMap"))
			{
				visibleText = base.mspp.get(visibleText.substring(0, visibleText.indexOf("FromHashMap")));
			}
			else if(visibleText.contains("FromConfig"))
			{
				visibleText = base.configurations.get(visibleText.substring(0,visibleText.indexOf("FromConfig")));
			}
			else if(locator.contains("VARIABLE"))
			{
				locator = locator.replace("VARIABLE", param);
			}
			objBase.selectByVisibleText(visibleText, locator);
		}
	}

	public void verifyObject(String xpath, String action) 
	{
		if(!xpath.equals(""))
		{
			String locator = getYamlValue(xpath);
			if (action.equals("visible")) 
			{
				Assert.assertTrue(objBase.isElementPresentOnPage(locator,action));
			} 
			else if (action.equals("not visible")) 
			{
				Assert.assertFalse(objBase.isElementPresentOnPage(locator,action));
			}
		}
	}
	
	public void verifyValueInDropdown(String text, String xpath, String action) 
	{
		if(!xpath.equals(""))
		{
			String locator = getYamlValue(xpath);
			if (action.equals("visible")) 
			{
				Assert.assertTrue(objBase.isValuePresentInDropdown(text, locator));
			} 
			else if (action.equals("not visible")) 
			{
				Assert.assertFalse(objBase.isValuePresentInDropdown(text, locator));
			}
		}
	}

	public void verifyTextOnObject(String text, String xpath,String action) 
	{
		if(!text.equals(""))
		{
			String locator = getYamlValue(xpath);
			if(text.contains("FromHashMap"))
			{
				text = base.mspp.get(text.substring(0, text.indexOf("FromHashMap")));
			}
			else if(text.contains("FromConfig"))
			{
				text = base.configurations.get(text.substring(0,text.indexOf("FromConfig")));
			}
			else if(text.contains("TodaysDate"))
			{
				if(text.contains("-"))
				{
					Date todaysDate = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
					Calendar c = Calendar.getInstance();
					c.setTime(todaysDate);
					c.add(Calendar.DATE, -Integer.parseInt(text.substring(text.indexOf("-")+1)));
					text = sdf.format(c.getTime());
				}
				else if(text.contains("+"))
				{
					Date todaysDate = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
					Calendar c = Calendar.getInstance();
					c.setTime(todaysDate);
					c.add(Calendar.DATE, Integer.parseInt(text.substring(text.indexOf("+")+1)));
					text = sdf.format(c.getTime());
				}
				else
				{
					Date todaysDate = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
					sdf.setTimeZone(TimeZone.getTimeZone(text.substring(text.indexOf("TodaysDate")+10)));
					text = sdf.format(todaysDate);
				}
			}
			locator = locator.replace("VARIABLE", text);
			if(action.equalsIgnoreCase("visible"))
			{
				Assert.assertTrue(objBase.isElementPresentOnPage(locator,action));
			}
			else
			{
				Assert.assertFalse(objBase.isElementPresentOnPage(locator,action));
			}
		}
	}

	public void switchFrame(String frame) 
	{
		if (frame.equalsIgnoreCase("iFrame"))
		{
			objBase.switchToFrame();
		} 
		else 
		{
			objBase.switchToDefaultContent();
		}
	}

	public void actionOnAlert(String action) 
	{
		if (action.equals("dismiss"))
		{
			objBase.dismissAlert();
		} 
		else if (action.equals("accept")) 
		{
			objBase.acceptAlert();
		}
	}
	
	public void validateData(String fileName, DataTable dt)
	{
		Map<String,String> m =  dt.asMap(String.class,String.class);
		File file = null;;
		try  
		{  
			File dir = new File(System.getProperty("user.dir")+File.separator+"target"+File.separator+"Downloads");  
			File[] listOfFiles = dir.listFiles();
			for(int i=0;i<listOfFiles.length;i++)
			{
				if(listOfFiles[i].getName().contains(fileName))
				{
					file = new File(System.getProperty("user.dir")+File.separator+"target"+File.separator+"Downloads"+File.separator+listOfFiles[i].getName());
				}
			}
			FileInputStream fis = new FileInputStream(file);   //obtaining bytes from the file  
			XSSFWorkbook wb = new XSSFWorkbook(fis);   
			XSSFSheet sheet = wb.getSheetAt(0); 
			for(int i=0; i<m.size();i++)
			{
				String key = m.keySet().toArray()[i].toString();
				int rownum = Integer.parseInt(key.substring(key.indexOf("Row")+3));
				Row row =sheet.getRow(rownum-1);
				String rowValue = "";
				Iterator<Cell> cellIterator = row.cellIterator();  
				while (cellIterator.hasNext())
				{  
					Cell cell = cellIterator.next();
					
					switch (cell.getCellType()) 
					{ 
	                    case Cell.CELL_TYPE_NUMERIC: 
	                    	rowValue = rowValue+Double.toString(cell.getNumericCellValue());
	                    	rowValue = rowValue+",";
	                        break; 
	                    case Cell.CELL_TYPE_STRING: 
	                    	rowValue = rowValue+cell.getRichStringCellValue();
	                    	rowValue = rowValue+",";
	                        break; 
                    } 
				}
				rowValue = rowValue.substring(0, rowValue.lastIndexOf(","));
				Assert.assertEquals(m.get(key), rowValue);
			}
		}
		catch(Exception e)  
		{  
			e.printStackTrace();  
		}
		finally
		{
			file.delete();
		}
	}
	
	public void compareImage(String fileName, String xpath) throws IOException
	{
		String locator = getYamlValue(xpath);
		Assert.assertTrue(objBase.ocularImageComparison(fileName, locator));
	}
	
	public void verifyEmail(DataTable table) throws IOException, MessagingException, InterruptedException, ParseException
	{
		Assert.assertTrue(objBase.verifyEmailText(table));
	}
	
	public void verifyUser() throws IOException, MessagingException
	{
		objBase.verifyUser();
	}
	
	public void closeClinics(String param)
	{
		objBase.closeClinics(param);
	}
		
}
