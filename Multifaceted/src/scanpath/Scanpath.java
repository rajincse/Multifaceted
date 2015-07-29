package scanpath;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import eyetrack.EyeTrackerItem;

import realtime.DataObject;
import realtime.EyeEvent;

import multifaceted.FileLineReader;
import multifaceted.Util;

public class Scanpath {
	
	
	private String filepath;
	private ArrayList<EyeEvent> eyeEventList = new ArrayList<EyeEvent>();
	private HashMap<String, DataObject> dataObjectList = new HashMap<String, DataObject>();
	private long startTime =0;
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
	
	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	private DataObject[] scanpathPoints;
	private double maxScore=Double.MIN_VALUE;
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
	
	
	public void render(Graphics2D g)
	{
		renderTimeline(g);
		g.setColor(Color.black);
		Point lastPoint = null;
		int cellCenter = ScanpathViewer.TIME_CELL_WIDTH/2+1;
		for(int i=0;i <scanpathPoints.length;i++)
		{
			if(scanpathPoints[i] != null)
			{
				
				int x = i*ScanpathViewer.TIME_CELL_WIDTH+ cellCenter;
				int y = (scanpathPoints[i].getType()-1)* ScanpathViewer.TIME_LINE_GAP;
				if(lastPoint != null)
				{
					g.drawLine(lastPoint.x, lastPoint.y, x, y);
					
				}
				lastPoint = new Point(x,y);
				int radius = (int)(scanpathPoints[i].getSortingScore()*cellCenter/ maxScore)+1;
				Util.drawCircle(x, y, radius, Color.black, g);
			}
			else if(lastPoint != null)
			{
				int x = i*ScanpathViewer.TIME_CELL_WIDTH+ cellCenter;
				int y = lastPoint.y;
				g.drawLine(lastPoint.x, lastPoint.y, x, y);
				lastPoint = new Point(x,y);
			}
		}
	}
	
	private void renderTimeline(Graphics2D g)
	{
		int y=0;
		for(int type=EyeTrackerItem.TYPE_ACTOR;type<EyeTrackerItem.TYPE_MOVIE_STAR_RATING;type++)
		{
			
			Color c = Util.getRelevanceChartColor(type);
			g.setColor(c);
			
			g.drawLine(0, 0, scanpathPoints.length*ScanpathViewer.TIME_CELL_WIDTH, 0);
			g.translate(0, ScanpathViewer.TIME_LINE_GAP);
			y+= ScanpathViewer.TIME_LINE_GAP;
		}
		g.translate(0, -y);
		
	}
	
}
