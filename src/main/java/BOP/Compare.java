package BOP;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/*public class Compare {
    public static void main(String[] args) {
        Compare compare = new Compare();
        compare.comMethod(); // You can still keep this if needed for standalone testing
    }

    public String[] comMethod() {
        // Paths to the two files
       // String filePath1 = "D:\\File\\Destination\\RC_BP_FL_11012023_V01_Res.json"; // Left input
       //String filePath2 = "D:\\File\\Destination\\RC_BP_NC_06012023_V01_Res.json"; // Right input
    	
        
      

        // Variables to hold file contents
        String dataFromFile1 = readFile(filePath1);
        String dataFromFile2 = readFile(filePath2);

        // Print the contents of both files
        System.out.println("Contents of file 1:");
        System.out.println(dataFromFile1);
        System.out.println("\nContents of file 2:");
        System.out.println(dataFromFile2);

        return new String[] { dataFromFile1, dataFromFile2 }; // Return both contents as an array
    }

    private static String readFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + filePath);
            e.printStackTrace();
        }
        return content.toString();
    }
}*/
