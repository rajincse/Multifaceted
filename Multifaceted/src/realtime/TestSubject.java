package realtime;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;

import multifaceted.ColorScheme;
import multifaceted.Util;



public class TestSubject {
	public static final int INVALID_INDEX =-1;
	private String name;
	private String directoryPath;
	private ArrayList<String> fileNameList;
	private ArrayList<Task> taskList = new ArrayList<Task>() ;
	private long startTime =0;
	private double[][][] heatmapCellPerItem; 
	private ArrayList<DataObject> qualifiedItems;
	private HashMap<DataObject, Double> itemLabelHeight;
	
	private boolean isLocked = false;
	
	public TestSubject(String name, String directoryPath,
			ArrayList<String> fileNameList) {
		this.name = name;
		this.directoryPath = directoryPath;
		this.fileNameList = fileNameList;
		
		for(String fileName: fileNameList)
		{
			String filePath = directoryPath+"\\"+name+"\\"+fileName+".TXT";
			Task task = new Task(filePath);
			task.load();
			taskList.add(task);
			
		}
	}
	
	public void prepareRendering(int timeMarker, int timeWindow, int timeStep)
	{
		synchronized (this) {
			isLocked = true;
		}
		
		long startTimeMarker = (long)Math.max(0, (1.0 *timeMarker/timeStep - EyeTrackDataStreamViewer.MAX_CELL_COLUMNS) * timeStep);
		long[] startTimePerTask = new long[taskList.size()];
		
		long totalTimePreviousTasks =0;
		int currentTaskIndex =INVALID_INDEX;
		for(int i=0;i<taskList.size();i++)
		{
			Task task = taskList.get(i);
			long taskStart = totalTimePreviousTasks;
			long taskEnd =totalTimePreviousTasks+ task.getEndTime();
			if(startTimeMarker> taskEnd)
			{
				startTimePerTask[i] = INVALID_INDEX;
			}
			else if(startTimeMarker>= taskStart && startTimeMarker< taskEnd)
			{
				startTimePerTask[i] = startTimeMarker -taskStart;
			}
			
			
			if( taskEnd>= timeMarker)
			{	
				currentTaskIndex =i;
				break;
			}
			totalTimePreviousTasks+= task.getEndTime();
		}
		
		if(currentTaskIndex> INVALID_INDEX)
		{
			Task currentTask = taskList.get(currentTaskIndex);
			long endTimeMarker =  timeMarker - totalTimePreviousTasks;
			qualifiedItems = currentTask.getQualifiedItems(endTimeMarker, timeWindow, timeStep) ;
			heatmapCellPerItem= new double[qualifiedItems.size()][currentTaskIndex+1][];
			for(int objectIndex=0;objectIndex<qualifiedItems.size();objectIndex++)
			{
				DataObject qualifiedItem = qualifiedItems.get(objectIndex);
				for(int taskIndex =0;taskIndex<currentTaskIndex;taskIndex++)
				{
					Task task = taskList.get(taskIndex);
					long startTime = startTimePerTask[taskIndex];
					if(startTime == INVALID_INDEX)
					{
						heatmapCellPerItem[objectIndex][taskIndex] = null;
					}
					else
					{
						heatmapCellPerItem[objectIndex][taskIndex] = task.getHeatmapCells(qualifiedItem,startTime, task.getEndTime(), timeStep);
					}
					
				}
				long startTime = startTimePerTask[currentTaskIndex];
				heatmapCellPerItem[objectIndex][currentTaskIndex] = currentTask.getHeatmapCells(qualifiedItem, startTime, endTimeMarker, timeStep);
			}
			
			
			//Label Height
			this.itemLabelHeight = new HashMap<DataObject, Double>();
			double scoreSum =0;
			for(DataObject obj: qualifiedItems)
			{
				scoreSum+=getLabelScore(obj);
			}
			
			for(DataObject obj:qualifiedItems)
			{
				double allottedHeight =  getLabelScore(obj)*qualifiedItems.size()* EyeTrackDataStreamViewer.CELL_HEIGHT / scoreSum;
				itemLabelHeight.put(obj, allottedHeight);
			}
		}
		else if(totalTimePreviousTasks< timeMarker)
		{
			Task currentTask = taskList.get(taskList.size()-1);
			long endTimeMarker =  currentTask.getEndTime();
			qualifiedItems = currentTask.getQualifiedItems(endTimeMarker, timeWindow, timeStep) ;
			heatmapCellPerItem= new double[qualifiedItems.size()][taskList.size()][];
			for(int objectIndex=0;objectIndex<qualifiedItems.size();objectIndex++)
			{
				DataObject qualifiedItem = qualifiedItems.get(objectIndex);
				for(int taskIndex =0;taskIndex<taskList.size();taskIndex++)
				{
					Task task = taskList.get(taskIndex);
					long startTime = startTimePerTask[taskIndex];
					if(startTime == INVALID_INDEX)
					{
						heatmapCellPerItem[objectIndex][taskIndex] = null;
					}
					else
					{
						heatmapCellPerItem[objectIndex][taskIndex] = task.getHeatmapCells(qualifiedItem,startTime, task.getEndTime(), timeStep);
					}
					
				}
			}
			
			
			//Label Height
			this.itemLabelHeight = new HashMap<DataObject, Double>();
			double scoreSum =0;
			for(DataObject obj: qualifiedItems)
			{
				scoreSum+=getLabelScore(obj);
			}
			
			for(DataObject obj:qualifiedItems)
			{
				double allottedHeight =  getLabelScore(obj)*qualifiedItems.size()* EyeTrackDataStreamViewer.CELL_HEIGHT / scoreSum;
				itemLabelHeight.put(obj, allottedHeight);
			}
		}
		
		synchronized (this) {
			isLocked = false;
		}
	}
	public double getLabelScore(DataObject obj)
	{
		return Math.pow(obj.getSortingScore(), EyeTrackDataStreamViewer.LABEL_WIDTH_SCORE_EXPONENT);
	}
	public void render(Graphics2D g, int currentTime)
	{
		if(isLocked)
		{
			return;
		}
		
		g.setColor(Color.BLUE);
		Font font = new Font("Helvetica", Font.ITALIC, 20);
		g.setFont(font);
		g.drawString(this.name, -EyeTrackDataStreamViewer.POSITION_X_USER_NAME, EyeTrackDataStreamViewer.POSITION_Y_USER_NAME);
		
		if(qualifiedItems!= null && !qualifiedItems.isEmpty() )
		{
			int x=0;
			int lastY =0;
			
			for(int i=0;i<qualifiedItems.size();i++)
			{
				DataObject obj = qualifiedItems.get(i);
				if(!this.itemLabelHeight.containsKey(obj))
				{
//					System.out.println("Null:"+this.name+", "+currentTime+", "+itemLabelHeight+"#"+qualifiedItems+"#"+obj);
					continue;
				}
				int allottedHeight =   this.itemLabelHeight.get(obj).intValue();
				int height = (int)(0.8 * allottedHeight);
				
				
				
				g.setFont(g.getFont().deriveFont(0.75f * allottedHeight));
				int stringWidth = g.getFontMetrics().stringWidth(obj.getLabel());
				int allowedCharacters = obj.getLabel().length();
				if(stringWidth > 0)
				{
					allowedCharacters = Math.min(obj.getLabel().length(), (int)( obj.getLabel().length() * EyeTrackDataStreamViewer.LABEL_WIDTH / stringWidth));
				}
				String label = obj.getLabel();
				if(allowedCharacters < obj.getLabel().length())
				{
					label = label.substring(0, allowedCharacters-3)+"...";
				}
				
				stringWidth = g.getFontMetrics().stringWidth(label);
				Color c = Util.getRelevanceChartColor(obj.getType());
				g.setColor(new Color(c.getRed(),c.getGreen(), c.getBlue(), 100));
				
				g.fillRect(x-stringWidth-5, lastY, stringWidth+5, allottedHeight);
				
				int rightX =(Math.min(currentTime, EyeTrackDataStreamViewer.MAX_CELL_COLUMNS)+1) * EyeTrackDataStreamViewer.CELL_WIDTH; 
//				g.fillRect( rightX, lastY, stringWidth+10, allottedHeight);
				
				g.setColor(Color.black);
				g.drawString(label, x-stringWidth-5, lastY+height);
				
//				g.drawString(label, rightX, lastY+height);
				int cellIndex =0;
				for(int taskIndex=0;taskIndex<heatmapCellPerItem[i].length;taskIndex++)
				{	
					if(heatmapCellPerItem[i][taskIndex] != null)
					{
						for(int timeIndex=0; timeIndex<heatmapCellPerItem[i][taskIndex].length;timeIndex++)
						{
							Color[] colorScheme = ColorScheme.LINEAR_INVERTED_GRAY;
							double score = heatmapCellPerItem[i][taskIndex][timeIndex];
							
							Color heatmapColor = perspectives.util.Util.getColorFromRange(colorScheme, score);
							g.setColor(heatmapColor);
							g.fillRect(cellIndex*EyeTrackDataStreamViewer.CELL_WIDTH, lastY,EyeTrackDataStreamViewer.CELL_WIDTH, allottedHeight);
							cellIndex++;
						}
					}
					
				}
				
				lastY+=allottedHeight;
			}
		}
	}
	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDirectoryPath() {
		return directoryPath;
	}

	public void setDirectoryPath(String directoryPath) {
		this.directoryPath = directoryPath;
	}

	public ArrayList<String> getFileNameList() {
		return fileNameList;
	}

	public void setFileNameList(ArrayList<String> fileNameList) {
		this.fileNameList = fileNameList;
	}

	public long getInitialTime() {
		return startTime;
	}

	public void setInitialTime(long initialTime) {
		this.startTime = initialTime;
	}



	public ArrayList<DataObject> getQualifiedItems() {
		if(qualifiedItems == null)
		{
			qualifiedItems = new ArrayList<DataObject>();
		}
		return qualifiedItems;
	}

	public HashMap<DataObject, Double> getItemLabelHeight() {
		if(itemLabelHeight == null)
		{
			itemLabelHeight = new HashMap<DataObject, Double>();
		}
		return itemLabelHeight;
	}

	public ArrayList<Task> getTaskList() {
		return taskList;
	}

	@Override
	public String toString() {
		return "TestSubject [name=" + name + ", directoryPath=" + directoryPath
				+ ", fileNameList=" + fileNameList + "]";
	}


	
	
}
