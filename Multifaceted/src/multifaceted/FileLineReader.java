package multifaceted;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public abstract class FileLineReader {
	
	public void read(String filePath) 
	{	
		try {
		File file = new File(filePath);
		FileReader fStream = new FileReader(file);
			
		BufferedReader bufferedReader = new BufferedReader(fStream);
		
		String fileLine = bufferedReader.readLine();
		
		while(fileLine != null)
		{	
			readLine(fileLine, file);
			fileLine = bufferedReader.readLine();
		}
		
		bufferedReader.close();
		fStream.close(); 
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public abstract void readLine(String fileLine, File currentFile);
	
}
