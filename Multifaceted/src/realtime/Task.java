package realtime;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import multifaceted.FileLineReader;

public class Task {
	private String filePath;
	private ArrayList<EyeEvent> eyeEventList = new ArrayList<EyeEvent>();
	private HashMap<String, DataObject> dataObjectList = new HashMap<String, DataObject>();
	private long startTime =0;
	public Task(String filePath) {
		this.filePath = filePath;
	}
	
	public void load()
	{
		new FileLineReader() {
			
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
		}.read(filePath);
	}
	public double[] getHeatmapCells(DataObject qualifiedItem)
	{
		return getHeatmapCells(qualifiedItem, getEndTime());
	}
	public double[] getHeatmapCells(DataObject qualifiedItem, long endTime)
	{
		int totalCells =(int) (endTime/ EyeTrackDataStreamViewer.TIME_STEP)+1;
		double[] heatmapCells = new double[totalCells];
		int [] count =new int[totalCells];
		
		for(EyeEvent event: eyeEventList)
		{
			if(event.getTime() > endTime)
			{
				break;
			}
			if(event.getTarget().equals(qualifiedItem))
			{
				int index = (int) (event.getTime()/ EyeTrackDataStreamViewer.TIME_STEP);
				heatmapCells[index]+=event.getScore();
				count[index]++;
			}
		}
		
		for(int i=0;i<heatmapCells.length;i++)
		{
			double dividend = Math.max(count[i],Math.sqrt(EyeTrackDataStreamViewer.TIME_STEP/1000.*60));
			heatmapCells[i] = heatmapCells[i] / dividend; // find Average
		}
		
		return heatmapCells;
	}
	public long getEndTime()
	{
		if(!eyeEventList.isEmpty())	
		{
		
			long endTime = eyeEventList.get(eyeEventList.size()-1).getTime();
			return endTime;
		}
		else
		{
			return 0;
		}
		
	}
	
	private double getCoefficient(long currentTime, long startTime, long endTime, long timeStep)
	{
		double coefficient = 1 - 1.0* (endTime - currentTime) / (endTime - startTime);  
		return coefficient;
	}
	public ArrayList<DataObject> getQualifiedItems(long endTimeMarker, int timeWindow)
	{
		ArrayList<DataObject> qualifiedItems = new ArrayList<DataObject>();
		if(!eyeEventList.isEmpty())
		{
			long startTimeMarker = endTimeMarker -  timeWindow;
			if(startTimeMarker< 0)
			{
				startTimeMarker =0;
			}
			ArrayList<DataObject> allItems = new ArrayList<DataObject>();
			double totalCoefficient =0;
			for(EyeEvent event: eyeEventList)
			{
				if(event.getTime() >= startTimeMarker && event.getTime() <= endTimeMarker)
				{
					double coefficient = getCoefficient(event.getTime(), startTimeMarker, endTimeMarker, EyeTrackDataStreamViewer.TIME_STEP);
					totalCoefficient+=coefficient;
					if(allItems.contains(event.getTarget()))
					{
						int index = allItems.indexOf(event.getTarget());
						DataObject obj = allItems.get(index);
						double sortingScore = obj.getSortingScore();
						sortingScore+= event.getScore() * coefficient;
						obj.setSortingScore(sortingScore);
					}
					else
					{
						DataObject obj = event.getTarget();
						double sortingScore = event.getScore()* coefficient;
						obj.setSortingScore(sortingScore);
						allItems.add(obj);
					}
				}
			}
			for(DataObject obj: allItems)
			{
				obj.setSortingScore(obj.getSortingScore()/ totalCoefficient);
			}
			Collections.sort(allItems);
			int maxCells = Math.min(allItems.size(), EyeTrackDataStreamViewer.MAX_CELLS);
			qualifiedItems = new ArrayList<DataObject>(allItems.subList(0, maxCells));

			
		}
		
		return qualifiedItems;
	}
	
	public ArrayList<EyeEvent> getEyeEventList() {
		return eyeEventList;
	}

	public void setEyeEventList(ArrayList<EyeEvent> eyeEventList) {
		this.eyeEventList = eyeEventList;
	}



	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String fileName) {
		this.filePath = fileName;
	}

	@Override
	public String toString() {
		return "Task [filePath=" + filePath +" EndTime= "+getEndTime()+"]";
	}

}
