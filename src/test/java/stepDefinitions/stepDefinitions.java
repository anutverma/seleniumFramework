package stepDefinitions;

import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import actions.action;

public class stepDefinitions 
{
	action objActions = new action();

	@Given("I navigate to the \"([^\"]*)\" URL$")
	public void setupLogin(String URL) throws Throwable 
	{
		objActions.setup(URL);
	}
	
	@Given("I navigate to the \"([^\"]*)\" CloneURL$")
	public void setupLoginClone(String URL) throws Throwable 
	{
		objActions.CloneURL(URL);
	}

	@And("^I click on \"([^\"]*)\"$")
	public void clickOn(String xpath) throws Exception 
	{
		objActions.clickOn(xpath);
	}
	
	@And("^I click on \"([^\"]*)\" with parameter \"([^\"]*)\"$")
	public void clickWithParameter(String xpath, String parameter) throws Exception 
	{
		objActions.clickWithParameter(xpath,parameter);
	}

	@And("^I scroll to the \"([^\"]*)\" of the page$")
	public void scroll(String action) throws Exception 
	{
		objActions.scroll(action);
	}

	@And("^I select date \"([^\"]*)\"$")
	public void selectDate(String date) throws Exception 
	{
		objActions.selectDate(date);
	}
	
	@And("^I select all health questions$")
	public void selectAllHealthQuestions() throws Exception 
	{
		objActions.selectAllHealthQuestions();
	}
	
	@And("^I remove all the selected counties$")
	public void removeSelectedCounties() throws Exception 
	{
		objActions.removeSelectedCounties();
	}

	@And("^I mouse hover on \"([^\"]*)\"$")
	public void mouseHoverOn(String xpathKey) throws Exception 
	{
		objActions.mouseHoverOn(xpathKey);
	}

	@And("^I verify element \"([^\"]*)\" is \"([^\"]*)\"$")
	public void verifyObject(String xpathKey, String action) throws Exception 
	{
		objActions.verifyObject(xpathKey, action);
	}
	
	@And("^I verify value \"([^\"]*)\" in dropdown \"([^\"]*)\" is \"([^\"]*)\"$")
	public void verifyValueInDropdown(String text, String xpathKey, String action) throws Exception 
	{
		objActions.verifyValueInDropdown(text, xpathKey, action);
	}

	@And("^I wait for \"([^\"]*)\" seconds$")
	public void waitFor(String seconds) throws Exception 
	{
		objActions.waitForSeconds(seconds);
	}

	@And("^I enter \"([^\"]*)\" in \"([^\"]*)\"$")
	public void iEnterText(String text, String xpathKey) throws Exception 
	{
		objActions.enterText(text, xpathKey);
	}
	
	@And("^I press \"([^\"]*)\" in \"([^\"]*)\"$")
	public void keyPressEvent(String text, String xpathKey) throws Exception 
	{
		objActions.keyPressEvent(text, xpathKey);
	}

	@And("^I select \"([^\"]*)\" in \"([^\"]*)\"$")
	public void selectObject(String text, String xpathKey) throws Exception 
	{
		objActions.selectObject(text, xpathKey);
	}
	
	@And("^I select \"([^\"]*)\" in \"([^\"]*)\" with parameter \"([^\"]*)\"$")
	public void selectObjectWithParameter(String text, String xpathKey, String param) throws Exception 
	{
		objActions.selectObjectWithParameter(text, xpathKey, param);
	}

	@And("^I scroll element \"([^\"]*)\" into view$")
	public void scrollElement(String xpathKey) throws Exception 
	{
		objActions.scrollElement(xpathKey);
	}

	@Then("^I switch to \"([^\"]*)\" window$")
	public void moveWindow(String window) throws Exception 
	{
		objActions.switchToWindow(window);
	}
	
	@Then("^I refresh the screen$")
	public void refreshScreen() throws Exception 
	{
		objActions.refreshScreen();
	}

	@Then("^I verify \"([^\"]*)\" text on \"([^\"]*)\" is \"([^\"]*)\"$")
	public void i_Verify_Text_On_View_Profile_Page(String text, String xpathKey, String value) throws Exception 
	{
		objActions.verifyTextOnObject(text, xpathKey, value);
	}

	@Then("^I switch to \"([^\"]*)\"$")
	public void switchFrame(String frame) throws Exception 
	{
		objActions.switchFrame(frame);
	}

	@Then("^I \"([^\"]*)\" the alert$")
	public void actionOnAlert(String action) throws Exception 
	{
		objActions.actionOnAlert(action);
	}

	@Then("^I fetch \"([^\"]*)\" text from \"([^\"]*)\"$")
	public void fetchText(String textFor, String xpathKey) throws Exception 
	{
		objActions.fetchText(textFor, xpathKey);
	}
	
	@Then("^I fetch \"([^\"]*)\" attribute \"([^\"]*)\" from \"([^\"]*)\"$")
	public void fetchAttribute(String textFor, String attribute, String xpathKey) throws Exception 
	{
		objActions.fetchAttribute(textFor,attribute,xpathKey);
	}
	
	@Then("^I fetch URL from email$")
	public void fetchURL(DataTable dataSets) throws Exception 
	{
		objActions.fetchURLFromEmail(dataSets);
	}

	@And("^I upload file \"([^\"]*)\" in \"([^\"]*)\"$")
	public void uploadFile(String fileName, String xpathKey, DataTable dataSets) throws Exception 
	{
		objActions.uploadFile(fileName, xpathKey, dataSets);
	}
	
	@Then("^I clear the text \"([^\"]*)\"$")
	public void clearText(String xpathKey) throws Exception 
	{
		objActions.clearText(xpathKey);
	}
	
	@Given("^I validate the data from \"([^\"]*)\" excel file$")
	public void validateDataFromExcel(String fileName, DataTable dataSets) throws Exception 
	{
		objActions.validateData(fileName, dataSets);
	}
	
	@Then("^I compare \"([^\"]*)\" image on \"([^\"]*)\"$")
	public void compareImage(String fileName, String xpathKey) throws Exception 
	{
		objActions.compareImage(fileName, xpathKey);
	}
	
	@Then("^I verify email on successful vaccine signup$")
	public void verifyEmail(DataTable dataSets) throws Exception 
	{
		objActions.verifyEmail(dataSets);
	}
	
	@Then("^I verify user on successful signup$")
	public void verifyUser() throws Exception 
	{
		objActions.verifyUser();
	}
	
	@Then("^I close the clinics for \"([^\"]*)\"$")
	public void closeClinics(String param) throws Exception 
	{
		objActions.closeClinics(param);
	}
	
	@And ("^I perform \"([^\"]*)\" operation$")
    public void performAction(String arg) throws Throwable{
        objActions.performAction(arg);
	}
}
