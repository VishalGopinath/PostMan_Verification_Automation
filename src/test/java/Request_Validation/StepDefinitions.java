package Request_Validation;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
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

public class StepDefinitions {
    private WebDriver driver;
    private String folder1;
    private String folder2;
    private String folder3;

    @Given("I have JSON files in {string}")
    public void i_have_json_files_in(String folder) {
        folder1 = folder;
    }

    @Given("I have corresponding JSON files in {string}")
    public void i_have_corresponding_json_files_in(String folder) {
        folder2 = folder;
    }

    @When("I compare the files")
    public void i_compare_the_files() {
        System.setProperty("webdriver.chrome.driver", "D:/ChromeDriver/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(folder1), "*res.json")) {
            for (Path entry : stream) {
                String fileName = entry.getFileName().toString();
                String jsonFilePath1 = entry.toString();
                String jsonFilePath2 = Paths.get(folder2, fileName).toString();

                if (!Files.exists(Paths.get(jsonFilePath2))) {
                    System.out.println("File not found in folder 2: " + fileName);
                    continue;
                }

                try {
                    String jsonContent1 = new String(Files.readAllBytes(Paths.get(jsonFilePath1)));
                    String jsonContent2 = new String(Files.readAllBytes(Paths.get(jsonFilePath2)));

                    driver.get("https://extendsclass.com/json-diff.html#");
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

                    WebElement codeMirror1 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//div[@class='CodeMirror-scroll'])[1]")));
                    pasteContent(jsonContent1, codeMirror1);

                    WebElement codeMirror2 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//div[@class='CodeMirror-scroll'])[2]")));
                    pasteContent(jsonContent2, codeMirror2);

                    String outputJson = copyComparisonResult(driver, wait, fileName);
                    String outputFilePath = Paths.get(folder3, "ComparisonResult_" + fileName).toString();
                    saveComparisonResult(outputFilePath, outputJson);

                    clearContent(codeMirror1);
                    clearContent(codeMirror2);
                } catch (Exception e) {
                    System.out.println("Error processing file " + fileName + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    @Then("I should save the comparison results in {string}")
    public void i_should_save_the_comparison_results_in(String folder) {
        this.folder3 = folder; // This could also be set in the Given step if needed
    }

    private void pasteContent(String jsonContent, WebElement targetElement) throws Exception {
        StringSelection stringSelection = new StringSelection(jsonContent);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
        targetElement.click();
        Actions actions = new Actions(driver);
        actions.keyDown(Keys.CONTROL).sendKeys("v").keyUp(Keys.CONTROL).perform();
        Thread.sleep(1000);
    }

    private void clearContent(WebElement targetElement) throws InterruptedException {
        targetElement.click();
        Actions actions = new Actions(driver);
        actions.keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).perform();
        actions.sendKeys(Keys.BACK_SPACE).perform();
        Thread.sleep(500);
    }

    private String copyComparisonResult(WebDriver driver, WebDriverWait wait, String fileName) throws Exception {
        Actions actions = new Actions(driver);
        WebElement resultElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//div[@class='CodeMirror-scroll'])[5]")));
        resultElement.click();
        actions.keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).perform();
        actions.keyDown(Keys.CONTROL).sendKeys("c").keyUp(Keys.CONTROL).perform();
        Thread.sleep(1000);
        String result = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        return "{ \"fileName\": \"" + fileName + "\", \"result\": \"" + result + "\" }";
    }

    private void saveComparisonResult(String filePath, String comparisonResult) {
        if (comparisonResult.isEmpty()) {
            System.out.println("No data to save for comparison.");
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
