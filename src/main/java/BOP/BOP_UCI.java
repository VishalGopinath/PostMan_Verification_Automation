package BOP;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONObject;

public class BOP_UCI {

    public static void main(String[] args) {
        LocalDate today = LocalDate.now();
        AtomicInteger successCount = new AtomicInteger(0); // Use AtomicInteger for thread-safe increment

        Properties properties = new Properties();
        try (InputStream inputStream = BOP_UCI.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String folderPath = "D:\\File\\Source";

        try {
            Files.list(Paths.get(folderPath))
            .filter(path -> path.toString().endsWith(".json") || path.toString().endsWith(".txt"))
                .forEach(path -> {
                    try {
                        if (processRequest(path, properties, today)) {
                            successCount.incrementAndGet(); // Increment success count
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Display success message in red color
        System.out.println("\u001B[31mSuccessfully " + successCount.get() + " requests in source folder generated to destination folder.\u001B[0m");
    }

    private static boolean processRequest(Path jsonFilePath, Properties properties, LocalDate today) throws IOException {
        StringBuilder jsonInputString = new StringBuilder();
        String fileName = jsonFilePath.getFileName().toString();
        System.out.println("Reading from file: " + fileName);

        // Read the JSON file
        try (BufferedReader br = new BufferedReader(new FileReader(jsonFilePath.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                jsonInputString.append(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + fileName);
            e.printStackTrace();
            return false;
        }

        String requestString = jsonInputString.toString();
        JSONObject requestJson;
        try {
            requestJson = new JSONObject(requestString);
        } catch (Exception e) {
            System.out.println("Error parsing JSON from file: " + fileName);
            e.printStackTrace();
            return false;
        }

        if (!requestJson.has("ServiceRequestDetail")) {
            System.out.println("Missing 'ServiceRequestDetail' in JSON: " + fileName);
            return false;
        }

        String token = properties.getProperty("UCI_Token").trim();
        requestJson.getJSONObject("ServiceRequestDetail").put("Token", token);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = today.format(formatter);
        requestJson.put("LastRateLookUpDate", formattedDate);

        // Save new request in destination folder
        String requestDestinationPath = "D:\\File\\Destination\\" + fileName;
        try (FileWriter reqWriter = new FileWriter(requestDestinationPath)) {
            reqWriter.write(requestJson.toString(4));
            reqWriter.flush(); // Ensure data is written
            System.out.println("Request content successfully written to " + requestDestinationPath);
        } catch (IOException e) {
            System.out.println("Error writing request to destination: " + e.getMessage());
            return false;
        }

        // Sending request to API
        String apiUrl = "https://uciisopmtbopkb.solartis.net/KnowledgeEngineV6_10/KnowledgeBase/FireEventV3";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Token", token);
            connection.setRequestProperty("EventName", "ISO_BOPV8_MultiState_Rating_FormSelection_V1");
            connection.setRequestProperty("OwnerId", "1");
            connection.setRequestProperty("Mode", "LIVE");
            connection.setRequestProperty("Environment", "15");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(20000);
            connection.setReadTimeout(20000);

            // Send request
            try (java.io.OutputStream os = connection.getOutputStream()) {
                os.write(requestJson.toString().getBytes("utf-8"));
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.out.println("Received response code " + responseCode + " for file: " + fileName);
                return false;
            }

            // Read response
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            // Save response to file
            JSONObject res = new JSONObject(response.toString());
           // String destinationFileName = fileName.replaceAll("(?i)req\\.json", "Res.json");
            String destinationFileName = fileName.replaceAll("(?i)req\\.(json|txt)", "Res.$1");
            String destinationPath = "D:\\File\\Destination\\" + destinationFileName;
            try (FileWriter writer = new FileWriter(destinationPath)) {
                writer.write(res.toString(4));
                writer.flush(); // Ensure response is written
                System.out.println("Response content successfully copied to " + destinationPath);
            } catch (IOException e) {
                System.out.println("Error writing response to destination: " + e.getMessage());
                return false;
            }

            return true;
        } catch (Exception e) {
            System.out.println("Error processing request for file: " + fileName);
            e.printStackTrace();
            return false;
        }
    }
}
