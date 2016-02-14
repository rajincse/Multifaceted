package architecture;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import perspectives.base.Property;
import perspectives.base.Viewer;
import perspectives.properties.PFileInput;
import perspectives.two_d.JavaAwtRenderer;

public class DataFileCreator extends Viewer implements JavaAwtRenderer{

	public DataFileCreator(String name) {
		super(name);
		
//		String path = "C:/work/r.txt";
		String path = "C:/work/rajin.txt";
//		String path = "C:/work/Alexia/Alexia00";
		Property<PFileInput> pLoad = new Property<PFileInput>("Load User", new PFileInput()){

			@Override
			protected boolean updating(PFileInput newvalue) {
				loadFile(newvalue.path);
				return super.updating(newvalue);
			}
			
		};
		addProperty(pLoad);
		
		
		
		PFileInput loadFile = new PFileInput(path);
		pLoad.setValue(loadFile);
	}

	private void loadFile(String path)
	{
		long time = System.currentTimeMillis();
		System.out.println("Reading file:"+path);
		loadBinaryFile(path);
		time = System.currentTimeMillis() -time;
		System.out.println("Loaded "+path+", time required "+time+" ms");
	}
	private void loadTextFile(String path)
	{
		
		try {
			FileReader fr = new FileReader(new File(path));
			BufferedReader br = new BufferedReader(fr);
			
			String line = br.readLine();
			while(line!= null)
			{
//				System.out.println(line);
				line = br.readLine();
			}
			
			br.close();
			fr.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void loadBinaryFile(String path)
	{
		File file = new File(path);
		byte[] result = new byte[(int)file.length()];
	    
		try {
	        int totalBytesRead = 0;
	        InputStream input = new BufferedInputStream(new FileInputStream(file));
	        while(totalBytesRead < result.length){
	          int bytesRemaining = result.length - totalBytesRead;
	          //input.read() returns -1, 0, or more :
	          int bytesRead = input.read(result, totalBytesRead, bytesRemaining); 
	          if (bytesRead > 0){
	            totalBytesRead = totalBytesRead + bytesRead;
	          }
	        }
	        input.close();
	        
	       
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String msg  = new String(result);
		
		String[] lines = msg.split(System.getProperty("line.separator"));
		
		saveAsEyeData(path, lines);
		
		
	}
	private void saveAsEyeData(String path, String[] dataLines )
	{
		ArrayList<EyeEvent> events = new ArrayList<EyeEvent>();
		ArrayList<DataObject> dataObjects = new ArrayList<DataObject>();
		long startTime = -1;
		for(int i=0;i<dataLines.length;i++)
		{
			String[] lineSplit = dataLines[i].split("\t");
			try
			{
				String id =lineSplit[2];
				long time =Long.parseLong(lineSplit[1]);
				if (startTime < 0)
	        		startTime =  time;
				
				int type = Integer.parseInt(lineSplit[4]);
				DataObject obj = new DataObject(id, type);
				
				if(!dataObjects.contains(obj))
				{
					dataObjects.add(obj);
				}
				EyeEvent e = new EyeEvent(time-startTime,obj, Double.parseDouble(lineSplit[5]), 1);
        		events.add(e);
				
			}catch(ArrayIndexOutOfBoundsException ex)
			{
				System.err.println("Problem for("+i+") "+dataLines[i]);
			}
						
		}
		EyeData eyeData = new EyeData();
		eyeData.setDataObjects(dataObjects);
		eyeData.setEvents(events);
		saveAsEyeDataFile(path+"_out.eye", eyeData);
	}
	private void saveAsEyeDataFile(String path, EyeData eyeData)
	{
		 try {
			FileOutputStream fos = new FileOutputStream(path);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
	     
			oos.writeObject(eyeData);
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	     
	}
	private void saveAsText(String path, String[] dataLines )
	{
		StringBuffer outputLines = new StringBuffer();
		
		for(int i=0;i<dataLines.length;i++)
		{
			String[] lineSplit = dataLines[i].split("\t");
			try
			{
				outputLines.append("E\t"
						+lineSplit[1]+"\t" //time
						+lineSplit[2]+"\t" //id
						+lineSplit[4]+"\t" //type
						+lineSplit[5] //score
								+"\r\n");

			}catch(ArrayIndexOutOfBoundsException ex)
			{
				System.err.println("Problem for("+i+") "+dataLines[i]);
			}
						
		}
		saveToTextFile(path+"_out.txt", outputLines);
	}
	
	private void saveToTextFile(String path, StringBuffer output)
	{
		try {
			

			FileWriter fstream = new FileWriter(new File(path), false);
			BufferedWriter br = new BufferedWriter(fstream);

			br.write(output.toString());

			br.close();
			
			
			System.out.println("File saved:"+path);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public Color getBackgroundColor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void keyPressed(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean mousedragged(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mousemoved(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mousepressed(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mousereleased(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void render(Graphics2D arg0) {
		// TODO Auto-generated method stub
		
	}

}
