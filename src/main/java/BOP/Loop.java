package BOP;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
public class Loop {
	
	
		  public  String vish() throws IOException {
		 String directoryPath = "D:\\Vishal Raj\\22-07-2022\\CF_Batch 6_Onboarding_vishal (1)\\CF_Batch 6_Onboarding_vishal";
	     String fileNameToRead = "ERC_AU_AK_07012020_V01_REQ.txt";
	String str= "";
	     Path dirPath = Paths.get(directoryPath);
	     
	     if (!Files.isDirectory(dirPath)) {
	         System.err.println("The specified directory does not exist.");
	        
	     }
	     
	     try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
	         boolean fileFound = false;
	         for (Path entry : stream) {
	             if (Files.isRegularFile(entry) && entry.getFileName().toString().equals(fileNameToRead)) {
	                 java.util.List<String> lines = Files.readAllLines(entry);
	                 for (String line : lines) {
	                	 str = str+ line;
	                    System.out.println(str);
	                		
	                 }
	                 fileFound = true;
	                 break;
	 
	}
	         }
	     
	         }
		return str ;
		
	 
		  }
	}
	        
