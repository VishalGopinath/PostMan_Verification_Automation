package BOP;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class Parakurom {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "D:\\ChromeDriver\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();

        try {
            // Maximize the browser window
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);

            // Specify the folders containing the JSON files
            String folder1 = "D:\\File\\";
            String folder2 = "D:\\"; // Ensure this path exists
            String fileName = "RC_AU_MO_01012025_V01_V2_RES.json";

            // Full paths to the JSON files
            String jsonFilePath1 = Paths.get(folder1, fileName).toString();
            String jsonFilePath2 = Paths.get(folder2, fileName).toString();

            // Read the JSON file contents
            String jsonContent1 = new String(Files.readAllBytes(Paths.get(jsonFilePath1)));
            String jsonContent2 = new String(Files.readAllBytes(Paths.get(jsonFilePath2)));

            // Navigate to the target web application
            driver.get("https://extendsclass.com/json-diff.html#");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Wait for and paste the first JSON content
            WebElement codeMirror1 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//div[@class='CodeMirror-scroll'])[1]")));
            pasteContent(driver, jsonContent1, codeMirror1);

            // Wait for and paste the second JSON content
            WebElement codeMirror2 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//div[@class='CodeMirror-scroll'])[2]")));
            pasteContent(driver, jsonContent2, codeMirror2);

            // Trigger the comparison (ensure to uncomment if applicable)
            // WebElement compareButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Compare')]")));
            // compareButton.click();

            System.out.println("Comparison triggered successfully for: " + jsonFilePath1 + " and " + jsonFilePath2);

            // Wait for results to load or display
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[@class='CodeMirror-scroll'])[5]"))); // Ensure this points to the results area

            // Copy the comparison result to a string
            String comparisonResult = copyComparisonResult(driver);
           // System.out.println(comparisonResult);

            // Define output file path with the same file name in JSON format
            String outputFilePath = Paths.get(folder2, "ComparisonResult_" + fileName).toString();
String OutputJson = copyComparisonResult(driver);
            // Save the comparison result to a file
            saveComparisonResult(outputFilePath, OutputJson);

           // System.out.println("Comparison result saved to: " + outputFilePath);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit(); // Clean up
        }
    }

    private static void pasteContent(WebDriver driver, String jsonContent, WebElement targetElement) throws Exception {
        // Use Actions to copy the JSON content to the clipboard
        StringSelection stringSelection = new StringSelection(jsonContent);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);

        // Click on the target element for pasting the JSON
        targetElement.click();

        // Paste the content from the clipboard
        Actions actions = new Actions(driver);
        actions.keyDown(Keys.CONTROL).sendKeys("v").keyUp(Keys.CONTROL).perform();

        // Wait a moment for the paste operation to complete
        Thread.sleep(1000); // Adjust as necessary
    }

    private static String copyComparisonResult(WebDriver driver) throws Exception {
        Actions actions = new Actions(driver);
        WebElement resultElement = driver.findElement(By.xpath("(//div[@class='CodeMirror-scroll'])[5]")); // Ensure this is the correct index for the comparison result

        // Select and copy the content from the results area
        actions.moveToElement(resultElement)
               .click()
               .keyDown(Keys.CONTROL)
               .sendKeys("a")
               .keyUp(Keys.CONTROL)
               .sendKeys("c")
               .build()
               .perform();

        // Allow some time for the copy operation to complete
        Thread.sleep(3000);

        // Get the text from the clipboard
        String result = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(java.awt.datatransfer.DataFlavor.stringFlavor);
//System.out.println(result);
        // Check if the result is valid
        if (result == null || result.isEmpty()) {
            throw new Exception("No data copied from the comparison result area.");
        }

        return result; // Return the result to be saved later
    }

    private static void saveComparisonResult(String filePath, String comparisonResult) {
        if (comparisonResult.isEmpty()) {
            System.out.println("No data to save.");
            return;
        }

        // Save the comparison result to a file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(comparisonResult);
            writer.flush(); // Ensure all data is written
            System.out.println("Comparison result saved to: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
