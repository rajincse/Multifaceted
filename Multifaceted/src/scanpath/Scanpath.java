package scanpath;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import realtime.DataObject;
import realtime.EyeEvent;

import multifaceted.ColorScheme;
import multifaceted.FileLineReader;
import multifaceted.Util;

public class Scanpath {
	
	
	private String filepath;
	private ArrayList<EyeEvent> eyeEventList = new ArrayList<EyeEvent>();
	private HashMap<String, DataObject> dataObjectList = new HashMap<String, DataObject>();
	private long startTime =0;
	
	private boolean isSelected=false;
	public Scanpath(String filepath)
	{
		this.filepath = filepath;
		FileLineReader fileLineReader = new FileLineReader() {
			
			@Override
			public void readLine(String fileLine, File currentFile) {
				// TODO Auto-generated method stub
				String[] split = fileLine.split("\t");
				if (split[0].equals("Eye") && !split[4].trim().equals("5") ){
	        		
	        		long t = Long.parseLong(split[1]) ;
	        		double s = Double.parseDouble(split[5]);
	        		double p = Double.parseDouble(split[8]);
	        		
	        		String objId = split[2];
	        		DataObject object = new DataObject(objId, split[3], Integer.parseInt(split[4].trim()));
	        		if(dataObjectList.containsKey(objId))
	        		{
	        			object = dataObjectList.get(objId);
	        		}
	        		else
	        		{
	        			dataObjectList.put(objId, object);
	        		}
	        		
	        		
	        		
	        		if(eyeEventList.isEmpty())
	        		{
	        			startTime = t;
	        		}
	        		EyeEvent e = new EyeEvent(t-startTime,object, s, p);
	        		eyeEventList.add(e);
	        	}
			}

			
		};
		fileLineReader.read(this.filepath);
		prepareRender();
		
	}
	
	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	private DataObject[] scanpathPoints;
	private ArrayList<DataObject> renderingObjectList = new ArrayList<DataObject>();
	private double maxScore=Double.MIN_VALUE;
	
	public int getRowCount()
	{
		return this.renderingObjectList.size();
	}
	public void prepareRender()
	{
		int totalCells =(int)(eyeEventList.get(eyeEventList.size()-1).getTime()/ ScanpathViewer.TIME_STEP)+1;
		scanpathPoints = new DataObject[totalCells];
		ArrayList<DataObject> dataObjectCollection = new ArrayList<DataObject>();
		int lastIndex =0;
		for(EyeEvent eye: eyeEventList)
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
				
				if(bestDataObject != null && bestDataObject.getSortingScore() > maxScore)
				{
					maxScore = bestDataObject.getSortingScore();
				}
				
				lastIndex = index;
				dataObjectCollection.clear();
			}
		}
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
	public static final int WIDTH_TITLE = 100;
	public static final int WIDTH_ANCHOR = 50;
	
	public void render(Graphics2D g, Color objectColor, Color horizontalLineColor, Color transitionLineColor, double heightRatio)
	{
		String fileName = new File(this.filepath).getName();
		int extensionIndex = fileName.toUpperCase().lastIndexOf(".TXT");
		fileName = fileName.substring(0, extensionIndex);
		Util.drawTextBox(g,Color.black, fileName, new Rectangle(0,renderingObjectList.size()*ScanpathViewer.TIME_CELL_HEIGHT/2,WIDTH_TITLE,renderingObjectList.size()*ScanpathViewer.TIME_CELL_HEIGHT/8));
		
		g.translate(WIDTH_TITLE, 0);
		
		// Rendering Anchors
		
		for(int i=0;i<renderingObjectList.size();i++)
		{
			String name = renderingObjectList.get(i).getLabel();
			int lineY = i* ScanpathViewer.TIME_CELL_HEIGHT;
			g.setColor(horizontalLineColor);
			g.drawLine(WIDTH_ANCHOR,lineY, WIDTH_ANCHOR+scanpathPoints.length*ScanpathViewer.TIME_CELL_WIDTH, lineY);
			
			int textY = i* ScanpathViewer.TIME_CELL_HEIGHT-ScanpathViewer.TIME_CELL_HEIGHT/2;
			Rectangle rect = new Rectangle(0,textY,WIDTH_ANCHOR,ScanpathViewer.TIME_CELL_HEIGHT);
			Color textBackColor = ColorScheme.ALTERNATE_COLOR_BLUE[i%2];
			g.setColor(textBackColor);
			g.fillRect(rect.x, rect.y, rect.width, rect.height);
			
			Util.drawTextBox(g, Color.black, name, rect);
		}
		
		//Rendering objects and transitions
		g.translate(WIDTH_ANCHOR, 0);
		Point lastPoint = null;
		for(int i=0;i <scanpathPoints.length;i++)
		{
			DataObject object = scanpathPoints[i];
			if(object != null)
			{
				int index = renderingObjectList.indexOf(object);
				int x = i*ScanpathViewer.TIME_CELL_WIDTH;
				int y = index* ScanpathViewer.TIME_CELL_HEIGHT;
				if(lastPoint != null)
				{
					g.setColor(transitionLineColor);
					g.drawLine(lastPoint.x+ScanpathViewer.TIME_CELL_WIDTH/2, lastPoint.y, x+ScanpathViewer.TIME_CELL_WIDTH/2, y);
				}
				lastPoint = new Point(x,y);
				g.setColor(objectColor);
				int cellHeight =(int) (ScanpathViewer.TIME_CELL_HEIGHT* heightRatio);
				g.fillRect(x, y-cellHeight/2, ScanpathViewer.TIME_CELL_WIDTH,cellHeight);
			}
			else if(lastPoint != null)
			{
				int x = i*ScanpathViewer.TIME_CELL_WIDTH+  ScanpathViewer.TIME_CELL_WIDTH/2;
				int y = lastPoint.y;
				g.setColor(transitionLineColor);
				g.drawLine(lastPoint.x+ScanpathViewer.TIME_CELL_WIDTH/2, lastPoint.y, x+ScanpathViewer.TIME_CELL_WIDTH/2, y);
				lastPoint = new Point(x,y);
			}
		}
		
		g.translate(-WIDTH_ANCHOR, 0);
		
		
		
		g.translate(-WIDTH_TITLE, 0);
		
		if(isSelected)
		{
			g.setColor(Color.red);
			Dimension d = getImageDimension();
			g.drawRect(0, 0, d.width, d.height);
		}
	}
	
	public Dimension getImageDimension()
	{
		int width =WIDTH_ANCHOR+WIDTH_TITLE+scanpathPoints.length*ScanpathViewer.TIME_CELL_WIDTH;
		int height = renderingObjectList.size()*ScanpathViewer.TIME_CELL_HEIGHT;
		
		Dimension imageDimension = new Dimension(width, height);
		
		return imageDimension;
	}
	
	public static Dimension getImageDimension(ArrayList<Scanpath> scanpathList)
	{
		int width =0;
		int height = 0;
		for(Scanpath scanpath:scanpathList)
		{
			Dimension dimension = scanpath.getImageDimension();
			if(dimension.width > width)
			{
				width = dimension.width;
			}
			height+= dimension.getHeight()+ScanpathViewer.SCANPATH_DIAGRAM_GAP;
		}
		
		Dimension imageDimension = new Dimension(width, height);
		
		return imageDimension;
	}
	
	
}
