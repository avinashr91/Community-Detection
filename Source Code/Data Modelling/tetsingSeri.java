import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;


/**
 Uses buffering, and abstract base classes. 
 JDK 7+. 
*/
public class tetsingSeri {
	
  private static final String COMMA_DELIMITER = ",";
  private static final String NEW_LINE_SEPARATOR = "\n";
  private static final String FILE_HEADER = "node1,node2,weight";

/*
 * Helper method to write a csv file
 */

public static void writeToCsv(List<String> writeData,String topic) throws IOException {
	// TODO Auto-generated method stub
	 FileWriter fileWriter = null;
	 String fileName = "D:/";
	 fileName += topic;
	 fileName += ".csv";
	
	 try 
	 {
		 fileWriter = new FileWriter(fileName);
		 fileWriter.append(FILE_HEADER.toString());
		 fileWriter.append(NEW_LINE_SEPARATOR);
		 for(int listInd = 0 ; listInd < writeData.size(); listInd ++)
		 {
			 fileWriter.append(String.valueOf(writeData.get(listInd)));
			 fileWriter.append(NEW_LINE_SEPARATOR);
		 }
	 }
	 catch(Exception e)
	 {
		 e.printStackTrace();
	 }
	 finally
	 {
		 fileWriter.flush();
		 fileWriter.close();
	 }

}
  

  // PRIVATE
  }

