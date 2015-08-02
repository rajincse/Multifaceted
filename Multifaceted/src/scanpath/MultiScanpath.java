package scanpath;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import multifaceted.ColorScheme;
import multifaceted.FileLineReader;
import multifaceted.Util;
import realtime.DataObject;
import realtime.EyeEvent;

public class MultiScanpath {
	public static final int INVALID =-1;
	
	private String directoryPath;
	
	private ArrayList<String> userList = new ArrayList<String>();
	private ArrayList<ArrayList<EyeEvent>> eyeEventList = new ArrayList<ArrayList<EyeEvent>>();
	private ArrayList<HashMap<String, DataObject>> dataObjectList = new ArrayList<HashMap<String, DataObject>>();
	private long startTime =0;
	private DataObject[][] scanpathPointArray;
	private ArrayList<DataObject> renderingObjectList = new ArrayList<DataObject>();
	
	public MultiScanpath(String directoryPath) {
		this.directoryPath = directoryPath;		
		process();
	}
	
	private void process()
	{
		File dir = new File(directoryPath);
		
		File[] sequenceFiles = dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File file) {
				// TODO Auto-generated method stub
				
				return file.getName().toUpperCase().endsWith(".TXT");
			}
		});
		
		FileLineReader fileLineReader = new FileLineReader() {
			
			@Override
			public void readLine(String fileLine, File currentFile) {
				// TODO Auto-generated method stub
				
				int index = getUserIndex(currentFile);
				
				ArrayList<EyeEvent> eyeEvents = eyeEventList.get(index);
				HashMap<String, DataObject> dataObjects = dataObjectList.get(index);
				
				String[] split = fileLine.split("\t");
				if (split[0].equals("Eye") && !split[4].trim().equals("5") ){
	        		
	        		long t = Long.parseLong(split[1]) ;
	        		double s = Double.parseDouble(split[5]);
	        		double p = Double.parseDouble(split[8]);
	        		
	        		String objId = split[2];
	        		DataObject object = new DataObject(objId, split[3], Integer.parseInt(split[4].trim()));
	        		if(dataObjects.containsKey(objId))
	        		{
	        			object = dataObjects.get(objId);
	        		}
	        		else
	        		{
	        			dataObjects.put(objId, object);
	        		}
	        		
	        		
	        		
	        		if(eyeEvents.isEmpty())
	        		{
	        			startTime = t;
	        		}
	        		EyeEvent e = new EyeEvent(t-startTime,object, s, p);
	        		eyeEvents.add(e);
	        	}
			}

			
		};
		
		scanpathPointArray = new DataObject[sequenceFiles.length][];
		int index =0;
		for(File file: sequenceFiles)
		{
			String fileName = file.getName();
			int extensionIndex = fileName.toUpperCase().lastIndexOf(".TXT");
			String user = fileName.substring(0, extensionIndex);
			this.userList.add(user);
			
			ArrayList<EyeEvent> eyeEvents = new ArrayList<EyeEvent>();
			this.eyeEventList.add(eyeEvents);
			
			HashMap<String, DataObject> dataObjects = new HashMap<String, DataObject>();
			this.dataObjectList.add(dataObjects);
			
			
			fileLineReader.read(file.getAbsolutePath());
			
			scanpathPointArray[index] = getScanpathpoint(index);
			index++;
		}
	}
	public DataObject[] getScanpathpoint(int userIndex)
	{
		ArrayList<EyeEvent> eyeEvents = eyeEventList.get(userIndex);
		HashMap<String, DataObject> dataObjects = dataObjectList.get(userIndex);
		
		int totalCells =(int)(eyeEvents.get(eyeEvents.size()-1).getTime()/ ScanpathViewer.TIME_STEP)+1;
		DataObject[] scanpathPoints = new DataObject[totalCells];
		ArrayList<DataObject> dataObjectCollection = new ArrayList<DataObject>();
		int lastIndex =0;
		for(EyeEvent eye: eyeEvents)
		{
			
			int index = (int)(eye.getTime()/ScanpathViewer.TIME_STEP);
			if(index == lastIndex)
			{
				DataObject object = eye.getTarget();
				if(dataObjectCollection.contains(object))
				{
					DataObject savedObject = dataObjectCollection.get(dataObjectCollection.indexOf(object));
					savedObject.setSortingScore(savedObject.getSortingScore()+eye.getScore());
				}
				else
				{
					object.setSortingScore(eye.getScore());
					dataObjectCollection.add(eye.getTarget());
				}
				
			}
			else
			{
				DataObject bestDataObject = getBestDataObject(dataObjectCollection);
				scanpathPoints[lastIndex] = bestDataObject;
				if(bestDataObject != null && !this.renderingObjectList.contains(bestDataObject))
				{
					this.renderingObjectList.add(bestDataObject);
				}
				
				
				lastIndex = index;
				dataObjectCollection.clear();
			}
		}
		
		return scanpathPoints;
	}
	
	public DataObject getBestDataObject(ArrayList<DataObject> dataObjectCollection)
	{
		if(dataObjectCollection != null && !dataObjectCollection.isEmpty())
		{
			Collections.sort(dataObjectCollection);
			
			return dataObjectCollection.get(0);
		}
		else
		{
			return null;
		}
	}
	
	
	private int getUserIndex(File file)
	{
		String fileName = file.getName();
		int extensionIndex = fileName.toUpperCase().lastIndexOf(".TXT");
		String user = fileName.substring(0, extensionIndex);
		
		if(this.userList != null && !this.userList.isEmpty() && this.userList.contains(user))
		{
			return this.userList.indexOf(user);
		}
		else
		{
			return INVALID;
		}
			
	}

	public String getDirectoryPath() {
		return directoryPath;
	}

	public void setDirectoryPath(String directoryPath) {
		this.directoryPath = directoryPath;
	}

	public static final int WIDTH_TITLE = 100;
	public static final int WIDTH_ANCHOR = 25;
	
	public void render(Graphics2D g, Color objectColor, Color horizontalLineColor, Color transitionLineColor, double heightRatio)
	{
		if(this.renderingObjectList != null && !this.renderingObjectList.isEmpty())
		{
			int heightPerObject = this.userList.size()*ScanpathViewer.TIME_CELL_HEIGHT+ScanpathViewer.SCANPATH_DIAGRAM_GAP;
			for(int objectIndex=0;objectIndex<renderingObjectList.size();objectIndex++)
			{
				DataObject dataObject = renderingObjectList.get(objectIndex);
				int titleX = 0;
				int titleY = heightPerObject*objectIndex;
				Util.drawTextBox(g, Color.black, dataObject.getLabel(), new Rectangle(titleX, titleY, WIDTH_TITLE, heightPerObject/2));
				
				g.translate(WIDTH_TITLE, heightPerObject*objectIndex);
				
				for(int userIndex=0;userIndex<userList.size();userIndex++)
				{
					String name = userList.get(userIndex);
					int lineY = userIndex* ScanpathViewer.TIME_CELL_HEIGHT;
					g.setColor(horizontalLineColor);
					g.drawLine(WIDTH_ANCHOR,lineY, WIDTH_ANCHOR+scanpathPointArray[userIndex].length*ScanpathViewer.TIME_CELL_WIDTH, lineY);
					
					int textY = userIndex* ScanpathViewer.TIME_CELL_HEIGHT-ScanpathViewer.TIME_CELL_HEIGHT/2;
					Rectangle rect = new Rectangle(0,textY,WIDTH_ANCHOR,ScanpathViewer.TIME_CELL_HEIGHT);
					Color textBackColor = ColorScheme.ALTERNATE_COLOR_BLUE[userIndex%2];
					g.setColor(textBackColor);
					g.fillRect(rect.x, rect.y, rect.width, rect.height);
					
					Util.drawTextBox(g, Color.black, " "+name, rect);
				}
				g.translate(-WIDTH_TITLE, -heightPerObject*objectIndex);
			}
			
			//Rendering objects and transitions
			
			for(int userIndex=0;userIndex<scanpathPointArray.length;userIndex++)
			{
				Point lastPoint = null;
				for(int time=0;time<scanpathPointArray[userIndex].length;time++)
				{
					DataObject object = scanpathPointArray[userIndex][time];
					if(object != null)
					{
						int index = renderingObjectList.indexOf(object);
						
						int x = time*ScanpathViewer.TIME_CELL_WIDTH+WIDTH_TITLE+WIDTH_ANCHOR;
						int y = index* heightPerObject+userIndex*ScanpathViewer.TIME_CELL_HEIGHT;
						
						if(lastPoint != null)
						{
							g.setColor(transitionLineColor);
//							g.setColor(ColorScheme.LINE_COLOR[userIndex]);
							g.drawLine(lastPoint.x+ScanpathViewer.TIME_CELL_WIDTH/2, lastPoint.y, x+ScanpathViewer.TIME_CELL_WIDTH/2, y);
						}
						lastPoint = new Point(x,y);
						g.setColor(objectColor);
						int cellHeight =(int) (ScanpathViewer.TIME_CELL_HEIGHT* heightRatio);
						g.fillRect(x, y-cellHeight/2, ScanpathViewer.TIME_CELL_WIDTH,cellHeight);
					}
					else if(lastPoint != null)
					{
						int x = time*ScanpathViewer.TIME_CELL_WIDTH+WIDTH_TITLE+WIDTH_ANCHOR+  ScanpathViewer.TIME_CELL_WIDTH/2;
						int y = lastPoint.y;
						g.setColor(transitionLineColor);
//						g.setColor(ColorScheme.LINE_COLOR[userIndex]);
						g.drawLine(lastPoint.x+ScanpathViewer.TIME_CELL_WIDTH/2, lastPoint.y, x+ScanpathViewer.TIME_CELL_WIDTH/2, y);
						lastPoint = new Point(x,y);
					}
				}
			}
		}
		
	}
	
	public Dimension getImageDimension()
	{
		int maxColumnCount =Integer.MIN_VALUE;
		for(int i=0;i<scanpathPointArray.length;i++)
		{
			if(scanpathPointArray[i].length > maxColumnCount)
			{
				maxColumnCount = scanpathPointArray[i].length;
			}
		}
		int width = WIDTH_TITLE+WIDTH_ANCHOR+maxColumnCount*ScanpathViewer.TIME_CELL_WIDTH;
		int height = renderingObjectList.size()*(ScanpathViewer.SCANPATH_DIAGRAM_GAP+userList.size()*ScanpathViewer.TIME_CELL_HEIGHT);
		
		Dimension imageDimension = new Dimension(width, height);
		
		return imageDimension;
	}
	
	
}
