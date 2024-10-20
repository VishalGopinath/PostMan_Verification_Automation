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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class massKatrom {

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "D:\\ChromeDriver\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(70, TimeUnit.SECONDS);

            String folder1 = "D:\\Request_Automation\\3. Request Comparation\\1. Response\\Folder 1\\";
            String folder2 = "D:\\Request_Automation\\3. Request Comparation\\1. Response\\Folder 2\\";
            String folder3 = "D:\\Request_Automation\\3. Request Comparation\\2. JsonDifferences";

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(folder1), "*res.json")) {
                for (Path entry : stream) {
                    String fileName = entry.getFileName().toString();
                    String jsonFilePath1 = entry.toString();
                    String jsonFilePath2 = Paths.get(folder2, fileName).toString();

                    if (!Files.exists(Paths.get(jsonFilePath2))) {
                        System.out.println("File not found in folder 2: " + fileName);
                        continue; // Skip to the next file if the corresponding file is not found
                    }

                    processFiles(driver, jsonFilePath1, jsonFilePath2, folder3, fileName);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit(); // Clean up
        }
    }

    private static void processFiles(WebDriver driver, String jsonFilePath1, String jsonFilePath2, String outputFolder, String fileName) {
        try {
            String jsonContent1 = new String(Files.readAllBytes(Paths.get(jsonFilePath1)));
            String jsonContent2 = new String(Files.readAllBytes(Paths.get(jsonFilePath2)));

            driver.get("https://extendsclass.com/json-diff.html#");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));

            WebElement codeMirror1 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//div[@class='CodeMirror-scroll'])[1]")));
            pasteContent(driver, jsonContent1, codeMirror1);

            WebElement codeMirror2 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//div[@class='CodeMirror-scroll'])[2]")));
            pasteContent(driver, jsonContent2, codeMirror2);

            // Copy the comparison result after pasting
            String outputJson = copyComparisonResult(driver, wait, fileName);
            String outputFilePath = Paths.get(outputFolder, "ComparisonResult_" + fileName).toString();
            saveComparisonResult(outputFilePath, outputJson);

            // Clear the content only after copying the result
            clearContent(driver, codeMirror1);
            clearContent(driver, codeMirror2);
        } catch (Exception e) {
            System.out.println("Error processing file " + fileName + ": " + e.getMessage());
        }
    }

    private static void pasteContent(WebDriver driver, String jsonContent, WebElement targetElement) throws Exception {
        StringSelection stringSelection = new StringSelection(jsonContent);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
        targetElement.click();
        Actions actions = new Actions(driver);
        actions.keyDown(Keys.CONTROL).sendKeys("v").keyUp(Keys.CONTROL).perform();
        Thread.sleep(1000); // Wait for paste to complete
    }

    private static void clearContent(WebDriver driver, WebElement targetElement) throws InterruptedException {
        targetElement.click();
        Actions actions = new Actions(driver);
        actions.keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).perform(); // Select all
        actions.sendKeys(Keys.BACK_SPACE).perform(); // Delete the selected content
        Thread.sleep(500); // Wait to ensure the action completes
    }

    private static String copyComparisonResult(WebDriver driver, WebDriverWait wait, String fileName) throws Exception {
        Actions actions = new Actions(driver);
        
        try {
            WebElement resultElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[@class='CodeMirror-scroll'])[5]")));
            resultElement.click();
            actions.keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).perform(); // Select all
            actions.keyDown(Keys.CONTROL).sendKeys("c").keyUp(Keys.CONTROL).perform(); // Copy
            Thread.sleep(1000);
            String result = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(java.awt.datatransfer.DataFlavor.stringFlavor);

            return "{ \"fileName\": \"" + fileName + "\", \"result\": \"" + result + "\" }";
        } catch (Exception e) {
            System.out.println("Failed to copy result for " + fileName + ": " + e.getMessage());
            return ""; // Return empty result in case of failure
        }
    }

    private static void saveComparisonResult(String filePath, String comparisonResult) {
        if (comparisonResult.isEmpty()) {
            System.out.println("No data to save for the file.");
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(comparisonResult);
            System.out.println("Comparison result saved to: " + filePath);
        } catch (IOException e) {
            System.out.println("Error while saving the file: " + e.getMessage());
        }
    }
}
