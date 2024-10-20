package BOP;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.time.Duration;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WebApp {

    public static void main(String[] args) {
        // Create an instance of Compare to read file contents
       // Compare com = new Compare();
       // String[] contents = com.comMethod(); // Get both file contents
        //String leftcheck = contents[0]; // First file content for left input
        //String rightcheck = contents[1]; // Second file content for right input
        
        System.setProperty("webdriver.chrome.driver", "D:\\ChromeDriver\\chromedriver.exe");
        // Initialize ChromeDriver
        WebDriver driver = new ChromeDriver();
        driver.get("D:\\File\\Destination\\RC_BP_NC_06012023_V01_Res.json");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(05));
        try {
            // Open the URL and maximize the window
            driver.get("https://extendsclass.com/json-diff.html#");
            driver.manage().window().maximize();

            // Locate the left input area and input data
          //  WebElement left =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[@class='CodeMirror-scroll'])[1]")));
            //left.click();
            //left.sendKeys(leftcheck); // Send the left file contents
            
            // Locate the right input area and input data
           // WebElement right = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[@class='CodeMirror-scroll'])[2]")));
            //right.click();
           
        	
       
        WebElement targetElement = driver.findElement(By.xpath("(//div[@class='CodeMirror-scroll'])[1]"));
        targetElement.click();
       /* JSONObject json = new JSONObject(leftcheck);
        String js = json.toString(4);*/
       // targetElement.sendKeys(leftcheck);
    
        

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Optionally quit the driver
            // driver.quit();
        }
    }
}
