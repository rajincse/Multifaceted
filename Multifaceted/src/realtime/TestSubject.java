package realtime;


import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;



public class TestSubject {
	private String name;
	private String directoryPath;
	private ArrayList<String> fileNameList;
	private ArrayList<Task> taskList = new ArrayList<Task>() ;
	private long startTime =0;
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
			System.out.println(task);
		}
	}
	
	public void render(Graphics2D g, long timeProgress)
	{
		g.setColor(Color.BLUE);
		
		g.drawString(this.name, -100, 10);
		
		long currentTime =0;
		
		
		for(int i=0;i<this.taskList.size() && currentTime < timeProgress;i++)
		{
			Task task = this.taskList.get(i);
			long totalTime = task.getEyeEventList().get(task.getEyeEventList().size()-1).getTime();
			
			if( timeProgress < currentTime+totalTime)
			{
				task.render(g, timeProgress ) ;
			}
			else
			{
				task.render(g);
			}
			currentTime += totalTime;
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
