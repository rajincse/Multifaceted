package realtime;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
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
	
	public void prepareRendering(int timeMarker)
	{
		long totalTimePreviousTasks =0;
		int currentTaskIndex =INVALID_INDEX;
		for(int i=0;i<taskList.size();i++)
		{
			Task task = taskList.get(i);
			
			if(totalTimePreviousTasks+ task.getEndTime() >= timeMarker)
			{
				currentTaskIndex =i;
				break;
			}
			totalTimePreviousTasks+= task.getEndTime();
		}
		
		if(currentTaskIndex> INVALID_INDEX)
		{
			Task currentTask = taskList.get(currentTaskIndex);
			long startTimeMarker =  timeMarker - totalTimePreviousTasks;
			qualifiedItems = currentTask.getQualifiedItems(startTimeMarker) ;
			System.out.println("name: "+this.name+", Task: "+currentTask+" Qualified:");
			heatmapCellPerItem= new double[qualifiedItems.size()][currentTaskIndex+1][];
			for(int objectIndex=0;objectIndex<qualifiedItems.size();objectIndex++)
			{
				DataObject qualifiedItem = qualifiedItems.get(objectIndex);
				for(int taskIndex =0;taskIndex<currentTaskIndex;taskIndex++)
				{
					Task task = taskList.get(taskIndex);
					heatmapCellPerItem[objectIndex][taskIndex] = task.getHeatmapCells(qualifiedItem);
				}
				heatmapCellPerItem[objectIndex][currentTaskIndex] = currentTask.getHeatmapCells(qualifiedItem, startTimeMarker);
			}

		}
	}
	
	public void render(Graphics2D g)
	{
		g.setColor(Color.BLUE);
		Font font = new Font("Helvetica", Font.ITALIC, 20);
		g.setFont(font);
		g.drawString(this.name, -EyeTrackDataStreamViewer.POSITION_X_USER_NAME, EyeTrackDataStreamViewer.POSITION_Y_USER_NAME);
		
		if(qualifiedItems!= null && !qualifiedItems.isEmpty())
		{
			double scoreSum =0;
			for(DataObject obj: qualifiedItems)
			{
				scoreSum+=obj.getSortingScore();
			}
			
			int x=0;
			int lastY =0;
			
			for(int i=0;i<qualifiedItems.size();i++)
			{
				DataObject obj = qualifiedItems.get(i);
				int allottedHeight =  (int) ( obj.getSortingScore()*qualifiedItems.size()* EyeTrackDataStreamViewer.CELL_HEIGHT / scoreSum);
				int height = (int)(0.8 * allottedHeight);
				
				
				
				g.setFont(g.getFont().deriveFont(0.75f * allottedHeight));
				int stringWidth = g.getFontMetrics().stringWidth(obj.getLabel());
				int allowedCharacters = obj.getLabel().length();
				if(stringWidth > 0)
				{
					allowedCharacters = Math.min(obj.getLabel().length(), (int)( obj.getLabel().length() * EyeTrackDataStreamViewer.LABEL_WIDTH / stringWidth));
				}
				 
				String label = obj.getLabel().substring(0, allowedCharacters);
				stringWidth = g.getFontMetrics().stringWidth(label);
				Color c = Util.getRelevanceChartColor(obj.getType());
				g.setColor(new Color(c.getRed(),c.getGreen(), c.getBlue(), 100));
				
				g.fillRect(x-stringWidth-5, lastY, stringWidth+5, allottedHeight);
				g.setColor(Color.black);
				g.drawString(label, x-stringWidth-5, lastY+height);
				
				for(int taskIndex=0;taskIndex<heatmapCellPerItem[i].length;taskIndex++)
				{
					for(int timeIndex=0;timeIndex<heatmapCellPerItem[i][taskIndex].length;timeIndex++)
					{
						Color[] colorScheme = ColorScheme.DEFAULT;
						double score = heatmapCellPerItem[i][taskIndex][timeIndex];
						int cellIndex = taskIndex* heatmapCellPerItem[i].length+timeIndex;
						Color heatmapColor = perspectives.util.Util.getColorFromRange(colorScheme, score);
						g.setColor(heatmapColor);
						g.fillRect(cellIndex*EyeTrackDataStreamViewer.CELL_WIDTH, lastY,EyeTrackDataStreamViewer.CELL_WIDTH, allottedHeight);
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



	@Override
	public String toString() {
		return "TestSubject [name=" + name + ", directoryPath=" + directoryPath
				+ ", fileNameList=" + fileNameList + "]";
	}


	
	
}
