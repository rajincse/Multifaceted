package realtime;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;

import multifaceted.FileLineReader;
import multifaceted.Util;

public class Task {
	private String filePath;
	private ArrayList<EyeEvent> eyeEventList = new ArrayList<EyeEvent>();
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
				if (split[0].equals("Eye")){
	        		
	        		long t = Long.parseLong(split[1]) ;
	        		double s = Double.parseDouble(split[5]);
	        		double p = Double.parseDouble(split[8]);
	        		
	        		String objId = split[2];
	        		DataObject object = new DataObject(objId, split[3], Integer.parseInt(split[4].trim()));
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
	public int getDominatingType(ArrayList<EyeEvent> cellEventList)
	{
		if(!cellEventList.isEmpty())
		{
			int types[] = new int[6];
			for(EyeEvent event:cellEventList)
			{
				types[event.getTarget().getType()]++;
			}
			
			int maxTypeScore =-1;
			int maxType =-1;
			for(int i=0;i<types.length;i++)
			{
				if(types[i] > maxTypeScore)
				{
					maxTypeScore = types[i];
					maxType = i;
				}
			}
			return maxType;
		}
		else
		{
			return -1;
		}
		
	}
	public void render(Graphics2D g)
	{
		long lastTime = getEyeEventList().get(getEyeEventList().size()-1).getTime() ;
		render(g, lastTime);
	}
	public void render(Graphics2D g, long lastTime)
	{
		int totalCells = (int )(lastTime/ EyeTrackDataStreamViewer.TIME_RATIO);
		int currentIndex =0;
		long currentTimeMarker =0;
		for(int i=0;i<totalCells;i++)
		{
			ArrayList<EyeEvent> cellEventList = new ArrayList<EyeEvent>();
			long tempTimeMarker =currentTimeMarker;
			while(currentTimeMarker - tempTimeMarker < EyeTrackDataStreamViewer.TIME_RATIO && currentIndex < this.eyeEventList.size())
			{
				EyeEvent event = this.eyeEventList.get(currentIndex);
				
				currentIndex++;
				currentTimeMarker= event.getTime();
				cellEventList.add(event);
			}
			
			int dominatingType = getDominatingType(cellEventList);
			if(dominatingType >=0)
			{
				Color c = Util.getRelevanceChartColor(dominatingType);
				
				g.setColor(c);
				
				g.drawRect(i, 0, EyeTrackDataStreamViewer.CELL_WIDTH, EyeTrackDataStreamViewer.CELL_HEIGHT);
			}
			
		}
	}
	public ArrayList<EyeEvent> getEyeEventList() {
		return eyeEventList;
	}

	public void setEyeEventList(ArrayList<EyeEvent> eyeEventList) {
		this.eyeEventList = eyeEventList;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String fileName) {
		this.filePath = fileName;
	}

	@Override
	public String toString() {
		return "Task [filePath=" + filePath +" EventSize= "+eyeEventList.size()+"]";
	}

}
