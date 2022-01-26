package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.yaml.snakeyaml.Yaml;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
 
@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"src/test/FeatureFiles/"},
		glue = {"actions","utilities","stepDefinitions","resources"},
		tags = {},	
        plugin = {"html:target/cucumber-results", "usage:target/cucumber-results/cucumber-usage.json",
                "junit:target/cucumber-results/cucumber-results.xml", "json:target/cucumber-results/cucumber.json",
                "com.vimalselvam.cucumber.listener.ExtentCucumberFormatter:target/Extent-Report/report.html"}
        )
public class TestRunner{
	
	public static HashMap<String,String> configurations;
	public static HashMap<String,String> objectRepository;
	 
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUp() throws IOException, InterruptedException 
    {
		Properties prop = new Properties();
 		String configFilePath = System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"java"+File.separator+"resources"+File.separator+"Configuration.properties";
 		prop.load(new FileInputStream(configFilePath));
 		configurations = new HashMap(prop);
 		String objectRepositoryFilePath = System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"java"+File.separator+"Resources"+File.separator+"ObjectRepository.yaml";
 		Yaml yaml = new Yaml();
 		objectRepository = (HashMap<String, String>) yaml.load(new FileReader(objectRepositoryFilePath));
    }	
} 