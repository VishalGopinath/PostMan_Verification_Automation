package BOP;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class finalclass {

    public static void main(String[] args) {
        String folder1 = "D:\\Request_Automation\\3. Request Comparation\\1. Response\\Folder 1\\";
        String folder2 = "D:\\Request_Automation\\3. Request Comparation\\1. Response\\Folder 2\\";
        String outputFolder = "D:\\Request_Automation\\3. Request Comparation\\2. JsonDifferences\\";

        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(folder1), "*.{json,txt}");
            boolean processedFiles = false;

            for (Path entry : stream) {
                String fileName = entry.getFileName().toString();
                String jsonFilePath1 = entry.toString();
                String baseName = fileName.replace(".json", "").replace(".txt", "");

                // Split the base name into words for flexible matching
                String[] baseNameParts = baseName.split("_");
                boolean fileFound = false;

                // Check for a matching file in Folder 2
                try (DirectoryStream<Path> folder2Stream = Files.newDirectoryStream(Paths.get(folder2))) {
                    for (Path folder2Entry : folder2Stream) {
                        String folder2FileName = folder2Entry.getFileName().toString();
                        for (String part : baseNameParts) {
                            if (folder2FileName.contains(part)) {
                                fileFound = true;
                                String jsonFilePath2 = folder2Entry.toString();
                                processFiles(jsonFilePath1, jsonFilePath2, outputFolder, fileName);
                                processedFiles = true;
                                break; // Exit the loop once the file is found
                            }
                        }
                        if (fileFound) break; // Exit the outer loop if a match was found
                    }
                }

                if (!fileFound) {
                    String outputFilePath = Paths.get(outputFolder, "Differences_" + fileName.replace(".json", "").replace(".txt", "") + ".json").toString();
                    saveNoDifferences(outputFilePath, fileName);
                }
            }

            if (!processedFiles) {
                System.out.println("No files were processed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processFiles(String jsonFilePath1, String jsonFilePath2, String outputFolder, String fileName) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode json1 = objectMapper.readTree(Paths.get(jsonFilePath1).toFile());
            JsonNode json2 = objectMapper.readTree(Paths.get(jsonFilePath2).toFile());

            Map<String, Object> differences = findDifferences(json1, json2);
            String outputFilePath = Paths.get(outputFolder, "Differences_" + fileName.replace(".json", "").replace(".txt", "") + ".json").toString();
            saveDifferences(outputFilePath, differences);
            System.out.println("Processed file: " + fileName); // Display completion message
        } catch (IOException e) {
            System.out.println("Error processing file " + fileName + ": " + e.getMessage());
        }
    }

    private static Map<String, Object> findDifferences(JsonNode json1, JsonNode json2) {
        Map<String, Object> differencesMap = new HashMap<>();
        compareNodes(json1, json2, differencesMap, "");
        return differencesMap;
    }

    private static void compareNodes(JsonNode node1, JsonNode node2, Map<String, Object> differencesMap, String path) {
        if (node1.isObject() && node2.isObject()) {
            // Compare fields in the first JSON object
            node1.fieldNames().forEachRemaining(fieldName -> {
                if (node2.has(fieldName)) {
                    compareNodes(node1.get(fieldName), node2.get(fieldName), differencesMap, path + "/" + fieldName);
                } else {
                    differencesMap.put(path + "/" + fieldName, "Field missing in second JSON");
                }
            });
            // Compare fields in the second JSON object
            node2.fieldNames().forEachRemaining(fieldName -> {
                if (!node1.has(fieldName)) {
                    differencesMap.put(path + "/" + fieldName, "Field missing in first JSON");
                }
            });
        } else if (!node1.equals(node2)) {
            Map<String, String> differenceDetail = new HashMap<>();
            differenceDetail.put("Value1", node1.toString());
            differenceDetail.put("Value2", node2.toString());
            differencesMap.put(path, differenceDetail);
        }
    }

    private static void saveDifferences(String filePath, Map<String, Object> differences) {
        if (differences.isEmpty()) {
            System.out.println("No differences found for the file.");
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            ObjectMapper objectMapper = new ObjectMapper();
            writer.write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(differences)); // Pretty print JSON
        } catch (IOException e) {
            System.out.println("Error while saving the file: " + e.getMessage());
        }
    }

    private static void saveNoDifferences(String filePath, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("{\"message\": \"No matching file found in folder 2 for: " + fileName + "\", \"status\": \"No differences found.\"}");
        } catch (IOException e) {
            System.out.println("Error while saving the no differences file: " + e.getMessage());
        }
    }
}
