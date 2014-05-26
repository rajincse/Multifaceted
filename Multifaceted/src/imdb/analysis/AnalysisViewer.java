package imdb.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import perspectives.base.Viewer;
import perspectives.two_d.JavaAwtRenderer;

public abstract class AnalysisViewer extends Viewer implements JavaAwtRenderer{
	
	public AnalysisViewer(String name) {
		super(name);
		
		
	}

	protected abstract void processFileLine(String line);

	protected void processFile(String filePath)
	{		
		try {
			File file = new File(filePath);
			FileReader fStream;
			fStream = new FileReader(file);		
			BufferedReader bufferedReader = new BufferedReader(fStream);
			
			String line = bufferedReader.readLine();
			while(line != null)
			{
				
				processFileLine(line);
				line = bufferedReader.readLine();
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
	}
	
	protected abstract void printInfo();
	
	protected abstract void createVisualItems();
	
	

}
