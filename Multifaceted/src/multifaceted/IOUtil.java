package multifaceted;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class IOUtil {
	public static ArrayList<String> readTextFile(String filePath)
	{
		ArrayList<String> allLines = new ArrayList<String>();
		try {

			
			File file = new File(filePath);
			FileReader fStream;
			fStream = new FileReader(file);		
			BufferedReader bufferedReader = new BufferedReader(fStream);
			
			String fileline = bufferedReader.readLine();
			
			while(fileline != null)
			{	
				allLines.add(fileline);
				fileline = bufferedReader.readLine();
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return allLines;
	}
}
