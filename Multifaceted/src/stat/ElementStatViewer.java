package stat;

import imdb.IMDBDataSource;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javax.imageio.ImageIO;

import multifaceted.Util;

import eyetrack.EyeTrackerItem;

import perspectives.base.Property;
import perspectives.base.Viewer;
import perspectives.properties.PFileInput;
import perspectives.properties.PFileOutput;
import perspectives.two_d.JavaAwtRenderer;

public class ElementStatViewer extends Viewer implements JavaAwtRenderer {

	public static final String PROPERTY_LOAD="Load";
	public static final String PROPERTY_LOAD_RELEVANT_LIST="Load Relevant List";
	public static final String PROPERTY_LOAD_IGNORE_LIST="Load Ignore List";
	public static final String PROPERTY_LOAD_TASK_LIST="Load Task List";
	public static final String PROPERTY_SAVE_DATA = "Save Data";
	public static final String PROPERTY_LOAD_SAVED_DATA = "Load Saved Data";
	public static final String PROPERTY_LOAD_SUBTASK_VIEW_NAME = "Load Subtask View Name";
	public static final String PROPERTY_LOAD_VIEWED_DATA = "Load Viewed Data";
	public static final String PROPERTY_SAVE_VIEW = "Save View";
	public static final String PROPERTY_GENERATE_LEGEND_DIAGRAM = "Generate Legends";
	
	
	private ArrayList<StatElement> elementNames = new ArrayList<StatElement>();
	private HashMap<Long, ArrayList<ViewItem>> relevantItemMap = new HashMap<Long, ArrayList<ViewItem>>(); 
	private ArrayList<String> ignoreList = new ArrayList<String>();
	private HashMap<String, Integer> taskList = new HashMap<String, Integer>();
	private HashMap<String, String> taskUserList = new HashMap<String, String>();
	
	private HashMap<String,  HashMap<Integer,ViewScore>> nameTaskViewingMap = new HashMap<String,  HashMap<Integer,ViewScore>>();
	private HashMap<String, HashMap<Integer, HashMap<String,ViewItem>>> nameTaskRelevanceMap = new HashMap<String, HashMap<Integer,HashMap<String,ViewItem>>>();
	private HashMap<Integer, HashMap<Integer, ArrayList<String>>> subtaskViewNameMap = new HashMap<Integer, HashMap<Integer,ArrayList<String>>>(); 
	private int currentTask;
	private IMDBDataSource data;
	public ElementStatViewer(String name,IMDBDataSource data) {
		super(name);
		this.data = data;
		this.elementNames = new ArrayList<StatElement>();
		
		
		try
		{
			Property<PFileInput> pLoadIgnore = new Property<PFileInput>(PROPERTY_LOAD_IGNORE_LIST, new PFileInput())
					{
						@Override
						protected boolean updating(PFileInput newvalue) {
							// TODO Auto-generated method stub
							readIgnoreList(newvalue.path);
							return super.updating(newvalue);
						}
					};
			addProperty(pLoadIgnore);
			
			Property<PFileInput> pLoadRelevantList = new Property<PFileInput>(PROPERTY_LOAD_RELEVANT_LIST, new PFileInput())
					{
						@Override
						protected boolean updating(PFileInput newvalue) {
							// TODO Auto-generated method stub
							readRelevantListFile(newvalue.path);
							return super.updating(newvalue);
						}
					};
			addProperty(pLoadRelevantList);
			
			Property<PFileInput> pLoadTaskList = new Property<PFileInput>(PROPERTY_LOAD_TASK_LIST, new PFileInput())
					{
						@Override
						protected boolean updating(PFileInput newvalue) {
							// TODO Auto-generated method stub
							readTaskListFile(newvalue.path);
							return super.updating(newvalue);
						}
					};
			addProperty(pLoadTaskList);
			
			
			
			PFileInput inputFile = new PFileInput();
			inputFile.onlyDirectories = true;
			Property<PFileInput> pLoad = new Property<PFileInput>(PROPERTY_LOAD, inputFile)
					{
						@Override
						protected boolean updating(PFileInput newvalue) {
							// TODO Auto-generated method stub
//							processFile(newvalue.path);
//							readSingleUser(newvalue.path);
							readUserDirectory(newvalue.path);
							return super.updating(newvalue);
						}
					};
			addProperty(pLoad);
			
			Property<PFileOutput> pSaveData = new Property<PFileOutput>(PROPERTY_SAVE_DATA, new PFileOutput())
					{
						@Override
						protected boolean updating(PFileOutput newvalue) {
							// TODO Auto-generated method stub
							saveData(newvalue.path);
							return super.updating(newvalue);
						}
					};
			addProperty(pSaveData);
			
			Property<PFileInput> pLoadSavedData = new Property<PFileInput>(PROPERTY_LOAD_SAVED_DATA, new PFileInput())
					{
						@Override
						protected boolean updating(PFileInput newvalue) {
							// TODO Auto-generated method stub
							loadSavedData(newvalue.path);
							processNameTaskRelevanceMap();
							return super.updating(newvalue);
						}
					};
			addProperty(pLoadSavedData);
			
			Property<PFileInput> pLoadSubtaskView = new Property<PFileInput>(PROPERTY_LOAD_SUBTASK_VIEW_NAME, new PFileInput())
					{
						@Override
						protected boolean updating(PFileInput newvalue) {
							// TODO Auto-generated method stub
							readSubtaskViewName(newvalue.path);
							return super.updating(newvalue);
						}
					};
			addProperty(pLoadSubtaskView);
			
			PFileInput inputViewedDir = new PFileInput();
			inputViewedDir.onlyDirectories = true;
			Property<PFileInput> pLoadViewedData = new Property<PFileInput>(PROPERTY_LOAD_VIEWED_DATA, inputViewedDir)
					{
						@Override
						protected boolean updating(PFileInput newvalue) {
							// TODO Auto-generated method stub
							readViewedDirectory(newvalue.path);
							calculateParameters();
							requestRender();
							return super.updating(newvalue);
						}
					};
			addProperty(pLoadViewedData);
			
			PFileOutput saveView = new PFileOutput();
			
			Property<PFileOutput> pSaveView = new Property<PFileOutput>(PROPERTY_SAVE_VIEW, saveView)
					{
							@Override
							protected boolean updating(PFileOutput newvalue) {
								// TODO Auto-generated method stub
								saveView(newvalue.path);
								return super.updating(newvalue);
							}
					};
			addProperty(pSaveView);
			
			Property<PFileOutput> pLegends = new Property<PFileOutput>(PROPERTY_GENERATE_LEGEND_DIAGRAM, new PFileOutput())
					{
							@Override
							protected boolean updating(PFileOutput newvalue) {
								generateLegendDiagram(newvalue.path);
								return super.updating(newvalue);
							}
					};
			addProperty(pLegends);
		}
		catch(Exception ex)
		{
			
		}
	}
	
	int saveViewTranslateX = 300;
	int saveViewTranslateY = 75;
	private Dimension getImageDimension()
	{
		return new Dimension(2350,1050);
	}
	private void saveView(String filePath)
	{	
		
		// TODO Auto-generated method stub
		BufferedImage bim = new BufferedImage(getImageDimension().width,getImageDimension().height, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = bim.createGraphics();
		
		g.translate(saveViewTranslateX, saveViewTranslateY);
		render(g);
		
		if(!filePath.contains(".PNG"))
		{
			filePath+=".PNG";
		}
		
		try {
			ImageIO.write(bim, "PNG", new File(filePath));
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	
	}
	
	private void generateLegendDiagram(String filePath)
	{
		// TODO Auto-generated method stub
				BufferedImage bim = new BufferedImage(975,65, BufferedImage.TYPE_INT_ARGB);
				
				Graphics2D g = bim.createGraphics();
				
				renderLegendLabels(g);
				
				g.dispose();
				
				if(!filePath.contains(".PNG"))
				{
					filePath+=".PNG";
				}
				
				try {
					ImageIO.write(bim, "PNG", new File(filePath));
				
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	
	private void renderLegendLabels(Graphics2D g)
	{
		String [] typeName = new String[]{"INIT", "Actor", "Movie", "Director", "Genre", "Star"};
		

		int startType =EyeTrackerItem.TYPE_ACTOR;
		int endType = EyeTrackerItem.TYPE_GENRE;
		
		Color previousColor = g.getColor();
		Font previousFont = g.getFont();
		g.translate(25, +35);
		g.setColor(Color.black);
		g.setFont(g.getFont().deriveFont(60f));
//		g.drawString("Legends:", 0, 15);
		for(int i=startType;i<= endType;i++)
		{
			int legendWidth =250;
			int x = legendWidth * (i-1);
			int y = 0; //- maxYLength/2 + i * 50;
			
			drawElement(i,x,y, 20, g);
			
			
			g.drawString(typeName[i], x+25, y+15);
		}
		g.translate(-25, -35);
		g.setColor(previousColor);
		g.setFont(previousFont);
//		Color previousColorR = g.getColor();
//		g.setColor(Color.red);
//		
//		g.drawRect(0, 0,975, 65);
//		
//		g.setColor(previousColorR);
		
	}
	
	private void readViewedDirectory(String path)
	{
		init();
		File rootDirectory = new File(path);
		String taskString = rootDirectory.getName().substring(rootDirectory.getName().length()-1);
		currentTask= Integer.parseInt(taskString);
		File[] dataFiles = rootDirectory.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return pathname.getName().toUpperCase().endsWith(".TXT");
			}
		});
		
		for(File dataFile: dataFiles)
		{
			
			readViewedFile(dataFile.getAbsolutePath());
		}
		System.out.println("Loaded:"+path);
	}
	
	private void readViewedFile(String filePath)
	{
		try {

			
			File file = new File(filePath);
			FileReader fStream;
			fStream = new FileReader(file);		
			BufferedReader bufferedReader = new BufferedReader(fStream);
			
			int subtask =1;
			try
			{
				int dotIndex =file.getName().lastIndexOf("."); 
				
				String subTaskString = file.getName().substring(dotIndex-1, dotIndex);
				 subtask =Integer.parseInt(subTaskString);
			}
			catch(NumberFormatException ex)
			{
				System.out.println("Reading subtask from :"+filePath);
			}
			
			if(subtask > maxSubtask)
			 {
				 maxSubtask = subtask;
			 }

			
			String fileline = bufferedReader.readLine();
			
			while(fileline != null)
			{	
				String[] data  = fileline.split("\t");
				if(data.length >=2)
				{
					String name = data[0];
					double viewingScore = Double.parseDouble(data[1]);
					
				
					if(!this.nameTaskViewingMap.containsKey(name))
					{
						
						ViewScore score = new ViewScore(viewingScore);
						
						HashMap<Integer,ViewScore> subtaskViewMap = new HashMap<Integer, ViewScore>();
						subtaskViewMap.put(subtask, score);
						
						
						this.nameTaskViewingMap.put(name, subtaskViewMap);
					}
					else
					{
						
						HashMap<Integer,ViewScore> subtaskViewMap =  this.nameTaskViewingMap.get(name);
						if(subtaskViewMap == null)
						{
							
							ViewScore score = new ViewScore(viewingScore);
							
							subtaskViewMap = new HashMap<Integer, ViewScore>();
							subtaskViewMap.put(subtask, score);
						}
						else
						{
							ViewScore score = subtaskViewMap.get(subtask);
							if(score != null)
							{
								score.addScore(viewingScore);
							}
							else
							{
								score = new ViewScore(viewingScore);
								subtaskViewMap.put(subtask, score);
							}
						}
					}
				}
				fileline = bufferedReader.readLine();
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
	
	private void readSubtaskViewName(String filePath)
	{
		try {

			
			File file = new File(filePath);
			FileReader fStream;
			fStream = new FileReader(file);		
			BufferedReader bufferedReader = new BufferedReader(fStream);
			
			
			
			String fileline = bufferedReader.readLine();
			
			while(fileline != null)
			{	
				String[] data = fileline.split("\t");
				
				if(data.length > 2)
				{
					String name = data[0];
					int task = Integer.parseInt(data[1]);
					int subtask = Integer.parseInt(data[2]);
					
					HashMap<Integer, ArrayList<String>> subtaskView = this.subtaskViewNameMap.get(task);
					if(subtaskView == null)
					{
						subtaskView = new HashMap<Integer, ArrayList<String>>();
						ArrayList<String> nameList = new ArrayList<String>();
						nameList.add(name);
						subtaskView.put(subtask, nameList);
						this.subtaskViewNameMap.put(task, subtaskView);
					}
					else
					{
						ArrayList<String> nameList = subtaskView.get(subtask);
						if(nameList == null)
						{
							nameList = new ArrayList<String>();
							nameList.add(name);
							subtaskView.put(subtask, nameList);
						}
						else
						{
							nameList.add(name);
						}
							
					}
					
				}
				fileline = bufferedReader.readLine();
			}
			
			//populate Special names:
			int task = 4;
			int subtask = 1;
			
			HashMap<Integer, ArrayList<String>> subtaskView = new HashMap<Integer, ArrayList<String>>();
			
			ArrayList<String> nameList = new ArrayList<String>();
			
			for(String elemName: this.nameTaskRelevanceMap.keySet())
			{
				if(this.nameTaskRelevanceMap.get(elemName).containsKey(task))
				{
					nameList.addAll(this.nameTaskRelevanceMap.get(elemName).get(task).keySet());
				}
				
			}
			subtaskView.put(subtask, nameList);
			this.subtaskViewNameMap.put(task, subtaskView);
			
			System.out.println("Loaded:"+filePath);
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
	private void processNameTaskRelevanceMap()
	{
		if(this.elementNames != null && !this.elementNames.isEmpty())
		{
			for(StatElement elem: this.elementNames)
			{
				for(ViewItem item: elem.getItems())
				{
					
					if(item.getType() != EyeTrackerItem.TYPE_MOVIE_STAR_RATING )
					{
						if(!this.nameTaskRelevanceMap.containsKey(item.getName()))
						{
							HashMap<Integer, HashMap<String,ViewItem>> taskRelevanceMap = new HashMap<Integer, HashMap<String,ViewItem>>();
							HashMap<String,ViewItem> viewItemMap =new HashMap<String, ViewItem>();
							viewItemMap.put(elem.getName(), item);
							taskRelevanceMap.put(elem.getTask(), viewItemMap);
							
							this.nameTaskRelevanceMap.put(item.getName(), taskRelevanceMap);
						}
						else
						{
							HashMap<Integer, HashMap<String,ViewItem>> taskRelevanceMap = this.nameTaskRelevanceMap.get(item.getName());
							if(taskRelevanceMap == null)
							{
								taskRelevanceMap = new HashMap<Integer, HashMap<String,ViewItem>>();
								HashMap<String,ViewItem> viewItemMap =new HashMap<String, ViewItem>();
								viewItemMap.put(elem.getName(), item);
								taskRelevanceMap.put(elem.getTask(), viewItemMap);
								
								this.nameTaskRelevanceMap.put(item.getName(), taskRelevanceMap);
							}
							else 
							{
								HashMap<String,ViewItem> viewItemMap =taskRelevanceMap.get(elem.getTask());
								if(viewItemMap == null)
								{
									viewItemMap =new HashMap<String, ViewItem>();
									viewItemMap.put(elem.getName(), item);
									taskRelevanceMap.put(elem.getTask(), viewItemMap);
								}
								else
								{
									ViewItem existingItem = viewItemMap.get(elem.getName());
									if(existingItem != null )
									{
										if(existingItem.getRelevance() > item.getRelevance())
										{
											viewItemMap.put(elem.getName(), item);
										}
										
									}
									else
									{
										viewItemMap.put(elem.getName(), item);
									}
								}
								
								
							}
						}
					}
					
				}
			}
			
		}
	}
	private void loadSavedData(String filePath)
	{
		try {
			File file =new File(filePath);
			FileInputStream fOutput = new FileInputStream(file);
		
			ObjectInputStream out = new ObjectInputStream(fOutput);
			
			RelevanceData data = (RelevanceData) out.readObject();
			this.elementNames = data.getElementNames();
			this.relevantItemMap = data.getRelevantItemMap();
			this.ignoreList = data.getIgnoreList();
			this.taskList = data.getTaskList();
			this.taskUserList = data.getTaskUserList();
			out.close();
			fOutput.close();
			System.out.println("Loaded:"+filePath);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void saveData(String filePath)
	{
		
		try {
			File file =new File(filePath);
			FileOutputStream fOutput = new FileOutputStream(file);
		
			ObjectOutputStream out = new ObjectOutputStream(fOutput);
			
			RelevanceData data = new RelevanceData(elementNames, relevantItemMap, ignoreList, taskList, taskUserList);
			out.writeObject(data);
			out.close();
			fOutput.close();
			
			System.out.println("Saved:"+filePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void processFile(String filePath)
	{
		System.out.println("Reading: "+filePath+"\r\n----------------------------");
		if(!isInIgnoreList(filePath))
		{
			readUserFile(filePath);
			fillupPresetElementNames();
			populateCount();
			for(StatElement elem: this.elementNames)
			{
				System.out.println("View\t"+elem.getName()+"("+elem.getTask()+")\t"+elem.getId()+"\t"+elem.getType()+"\t"+elem.getElementCount());
//				elem.printItems();
			}
		}		
//		this.elementNames.clear();
		
	}
	private boolean isInIgnoreList(String filePath)
	{
		String fileName = filePath.toUpperCase().substring(filePath.lastIndexOf("\\")+1);
		return this.ignoreList.contains(fileName);
	}
	private void readSingleUser(String path)
	{
		File rootDirectory = new File(path);
		File[] dataFiles = rootDirectory.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return pathname.getName().toUpperCase().endsWith(".TXT");
			}
		});
		
		for(File dataFile: dataFiles)
		{
			
			processFile(dataFile.getPath());
		}
		
	}
	private void readUserDirectory(String path)
	{
		File rootDirectory = new File(path);
		File[] userDirectories = rootDirectory.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File childFile) {
				// TODO Auto-generated method stub
				return childFile.isDirectory();
				
			}
		});
		
		for(File userDirectory: userDirectories)
		{
			System.out.println("User: "+userDirectory.getName()+"\r\n------------------------------");
			
			File[] dataFiles = userDirectory.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					// TODO Auto-generated method stub
					return pathname.getName().toUpperCase().endsWith(".TXT");
				}
			});
			
			for(File dataFile: dataFiles)
			{
				processFile(dataFile.getPath());
			}
			
			
		}
		System.out.println("Done reading:"+path);
	}
	private void fillupPresetElementNames()	
	{
		String[] elementNames = new String[]{
								"Ang Lee", 
								"Tim Burton",
								"James Cameron",
								"David Fincher",
								"Once in a Lifetime  The Extraordinary Story of the New York Cosmos",
								"Austin Powers  International Man of Mystery"
								,"Wayne s World"
								,"St. Elmo s Fire"
								,"Joe Strummer  The Future Is Unwritten"
								,"The Transformers  The Movie"
								,"Futbaal  The Price of Dreams"
								,"The Boondock Saints II  All Saints Day"
								};
		int [] tasks = new int[]
				{
				2,
				2,
				2,
				2,
				4,
				4,
				4,
				4,
				4,
				4,
				4,
				4
				};
		long[] ids = new long[]{
									984258, //"Ang Lee",
									237997,//	"Tim Burton",
									253636,//"James Cameron",
									541437,//"David Fincher"
									2446725//Once in a Lifetime: The Extraordinary Story of the New York Cosmos
									,1928710//Austin Powers: International Man of Mystery
									,2796141//"Wayne's World "
									,2595417//"St. Elmo's Fire"
									,2258108//"Joe Strummer: The Future Is Unwritten"
									,2719894//"The Transformers  The Movie"
									,2154141//"Futbaal  The Price of Dreams"
									,2642731//"The Boondock Saints II  All Saints Day"
								};
		int types[] = new int[]{
				3, //"Ang Lee",
				3,//	"Tim Burton",
				3,//"James Cameron",
				3//"David Fincher"
				,2//Once in a Lifetime: The Extraordinary Story of the New York Cosmos
				,2//Austin Powers: International Man of Mystery
				,2//"Wayne's World "
				,2//"St. Elmo's Fire"
				,2//"Joe Strummer: The Future Is Unwritten"
				,2//"The Transformers  The Movie"
				,2//"Futbaal  The Price of Dreams"
				,2//"The Boondock Saints II  All Saints Day"
								};
		
		for(int i=0;i<elementNames.length;i++)
		{
			String elemName = elementNames[i];
			int task = tasks[i];
			int index = this.elementNames.indexOf(new StatElement(elemName, task));
			if(index >=0)
			{
				StatElement elem = this.elementNames.get(index);
				if(elem.getId() == StatElement.INVALID)
				{
					elem.setId(ids[i]);
				}
				if(elem.getType() == StatElement.INVALID)
				{
					elem.setType(types[i]);
				}
				
			}
		}
	}
	private void readUserFile(String filePath)
	{
		try {

			
			File file = new File(filePath);
			FileReader fStream;
			fStream = new FileReader(file);		
			BufferedReader bufferedReader = new BufferedReader(fStream);
			
			String fileline = bufferedReader.readLine();
			ArrayList<String> fileLines = new ArrayList<String>();
			while(fileline != null)
			{	
				fileLines.add(fileline);
				fileline = bufferedReader.readLine();
			}
			
			int task = StatElement.INVALID;
			if(this.taskList.containsKey(file.getName().toUpperCase()))
			{
				task = this.taskList.get(file.getName().toUpperCase());
			}
			
			for(String line:fileLines)
			{
				String[] splits =line.split("\t");
				if(splits[0].equals("Image"))
				{
					String imageFileName = splits[2];
				
					String elementName = imageFileName.substring(16, imageFileName.length()-9);
					elementName = elementName.replaceAll("[0-9]", "");
					StatElement element = new StatElement(elementName, task);

					if(!this.elementNames.contains(element))
					{
						
						this.elementNames.add(element);
					}
				}
			}
				
			
			for(String line:fileLines)
			{
				String[] splits =line.split("\t");
				if(splits[0].equals("Eye"))
				{
					String elementName = splits[3].trim();	
					int index = this.elementNames.indexOf(new StatElement(elementName, task));
					if(index >= 0)
					{
						StatElement elem = this.elementNames.get(index);
						
						Long id = Long.parseLong(splits[2]);
						int type = Integer.parseInt(splits[4]);
						
						elem.setId(id);
						elem.setType(type);
					}
				}
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
	private void populateCount()
	{
		for(StatElement elem: this.elementNames)
		{
			ArrayList<ViewItem> relevantList = getInitialRelevantList(elem);
			elem.processItems(data, relevantList);
		}
	}
	private void readTaskListFile(String filePath)
	{
		try {

			
			File file = new File(filePath);
			FileReader fStream;
			fStream = new FileReader(file);		
			BufferedReader bufferedReader = new BufferedReader(fStream);
			
			String fileline = bufferedReader.readLine();
			
			while(fileline != null)
			{	
				String[] data  = fileline.split("\t");
				if(data.length >=3)
				{
					String userName = data[0].toUpperCase();
					String fileName = data[1].toUpperCase();
					int task = Integer.parseInt(data[2]);
					this.taskList.put(fileName, task);
					this.taskUserList.put(fileName, userName);
				}
				fileline = bufferedReader.readLine();
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
	private void readIgnoreList(String filePath)
	{
		try {

			
			File file = new File(filePath);
			FileReader fStream;
			fStream = new FileReader(file);		
			BufferedReader bufferedReader = new BufferedReader(fStream);
			
			String fileline = bufferedReader.readLine();
			
			while(fileline != null)
			{	
				this.ignoreList.add(fileline);
				fileline = bufferedReader.readLine();
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

	private void readRelevantListFile(String filePath)
	{
		try {

			
			File file = new File(filePath);
			FileReader fStream;
			fStream = new FileReader(file);		
			BufferedReader bufferedReader = new BufferedReader(fStream);
			
			String fileline = bufferedReader.readLine();
			
			while(fileline != null)
			{	
				String[] data  = fileline.split("\t");
				if(data.length >=3)
				{
					long elemId = Long.parseLong(data[0]);
					if(!this.relevantItemMap.containsKey(elemId))
					{
						ArrayList<ViewItem> itemList = new ArrayList<ViewItem>();
						this.relevantItemMap.put(elemId, itemList);
					}
					long id = Long.parseLong(data[1]);
					int type = Integer.parseInt(data[2]);
					ViewItem item = new ViewItem(id,type,"Item");
					this.relevantItemMap.get(elemId).add(item);
				}
				fileline = bufferedReader.readLine();
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
	private ArrayList<ViewItem> getInitialRelevantList(StatElement elem)
	{
		if(elem.getTask() == 4)
		{
			return getSpecialRelevantList();
		}
		else
		{
			ArrayList<ViewItem> relevantList = this.relevantItemMap.get(elem.getId());
			return relevantList;
		}
		
	}
	
	// Task 4
	private ArrayList<ViewItem> getSpecialRelevantList()
	{
		int types[] = new int[]{EyeTrackerItem.TYPE_ACTOR, 
								EyeTrackerItem.TYPE_ACTOR, 
								EyeTrackerItem.TYPE_ACTOR,
								EyeTrackerItem.TYPE_MOVIE,
								EyeTrackerItem.TYPE_MOVIE,
								EyeTrackerItem.TYPE_MOVIE
								};
		long ids[] = new long[]{1034789, 1242483, 441598,2643725, 2696365, 2595417};
		String names[] = new String[]{" Rob Lowe"," Judd Nelson"," Matt Dillon", "The Breakfast Club", "The Outsiders", "St. Elmo's Fire"};
		
		ArrayList<ViewItem> relevantList = new ArrayList<ViewItem>();
		
		for(int i=0;i<types.length;i++)			
		{
			ViewItem item = new ViewItem(ids[i],types[i], names[i]);
			relevantList.add(item);
		}
		
		return relevantList;
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

	private int maxSubtask =1;
	private int maxRelevance =0;
	private double maxScore =0;
	private double minScore =1;
	private void init()
	{
		this.nameTaskViewingMap.clear();
		maxSubtask =1;
		maxRelevance=0;
		maxScore =0;
		minScore =1;
	}
	private void calculateParameters()
	{
		if(this.nameTaskRelevanceMap != null && this.nameTaskViewingMap != null
				&& !this.nameTaskRelevanceMap.isEmpty()
				&& !this.nameTaskViewingMap.isEmpty()
		)
		{
			for(String elementName: this.nameTaskViewingMap.keySet())
			{
					
					if(this.nameTaskRelevanceMap.get(elementName) != null && this.nameTaskRelevanceMap.get(elementName).containsKey(currentTask))
					{
						HashMap<Integer, ViewScore> subtaskViewMap = this.nameTaskViewingMap.get(elementName); 
						
						for(Integer subTask: subtaskViewMap.keySet())
						{
							
							HashMap<String, ViewItem> itemRelevance =this.nameTaskRelevanceMap.get(elementName).get(currentTask);
							if(this.subtaskViewNameMap.containsKey(currentTask) && this.subtaskViewNameMap.get(currentTask).containsKey(subTask))
							{
								for(String viewName: this.subtaskViewNameMap.get(currentTask).get(subTask))
								{
									if(itemRelevance.containsKey(viewName))
									{
										ViewItem item = this.nameTaskRelevanceMap.get(elementName).get(currentTask).get(viewName);
										double score = subtaskViewMap.get(subTask).getAverage();
										
										if(item.getRelevance() > maxRelevance && item.getRelevance() != StatElement.INFINITY_RELEVANCE)
										{
											maxRelevance = item.getRelevance();
										}
										
										if(score > maxScore)
										{
											maxScore = score;
										}
										
										if(score < minScore)
										{
											minScore = score;
										}
									
										
										break;
									}
									
								}
							}
							
						}
					}
				}
			
		}

		System.out.println("MaxRelevance: "+maxRelevance+", max:"+maxScore+", minScore:"+minScore+", max subtask:"
		+maxSubtask+", factor:"+String.format("%.2f", 1.0 / maxScore));
	}
	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		g.setColor(Color.black);
		
		int originX =10;
		int originY = 800;
		
		
		int maxYLength =800;
		double factor =1.0 / maxScore;
		int maxRelevanceWidth =2000/(maxRelevance+1);
		int maxTaskWidth =(int)( 1.0 * maxRelevanceWidth/ maxSubtask) ;
		
		
		ArrayList<ErrorBarGlyph> errorBarGlyphList = new ArrayList<ErrorBarGlyph>();
		
		if(this.nameTaskRelevanceMap != null && this.nameTaskViewingMap != null
				&& !this.nameTaskRelevanceMap.isEmpty()
				&& !this.nameTaskViewingMap.isEmpty()
		)
		{
			for(String elementName: this.nameTaskViewingMap.keySet())
			{
					
					if(this.nameTaskRelevanceMap.get(elementName) != null && this.nameTaskRelevanceMap.get(elementName).containsKey(currentTask))
					{
						HashMap<Integer, ViewScore> subtaskViewMap = this.nameTaskViewingMap.get(elementName); 
						
						for(Integer subTask: subtaskViewMap.keySet())
						{
							
							HashMap<String, ViewItem> itemRelevance =this.nameTaskRelevanceMap.get(elementName).get(currentTask);
							if(this.subtaskViewNameMap.containsKey(currentTask) && this.subtaskViewNameMap.get(currentTask).containsKey(subTask))
							{
								for(String viewName: this.subtaskViewNameMap.get(currentTask).get(subTask))
								{
									if(itemRelevance.containsKey(viewName))
									{
										ViewItem item = this.nameTaskRelevanceMap.get(elementName).get(currentTask).get(viewName);
										double score = subtaskViewMap.get(subTask).getAverage();
										
										
										int radius = 15;
										int pointX = originX+  item.getRelevance()* maxRelevanceWidth+
												radius+
												(subTask-1)*maxTaskWidth+
												(int)(Math.random()*(maxTaskWidth-2*radius));
										int pointY = originY - (int) (score*factor * maxYLength);
										
										Color c = Util.getRelevanceChartColor(item.getType());
										
										drawElement(item.getType(),pointX, pointY,radius, g);
										
										int index = errorBarGlyphList.indexOf(ErrorBarGlyph.getInstance(item.getRelevance(), subTask));
										if(index >=0)
										{
											ErrorBarGlyph average = errorBarGlyphList.get(index);
											average.addScore(score);
										}
										else
										{
											ErrorBarGlyph average = new ErrorBarGlyph(item.getRelevance(), subTask);
											average.addScore(score);
											errorBarGlyphList.add(average);
										}
										
										break;
									}
									else
									{
//										System.out.println("View name doesn't match:"+viewName+",Element "+elementName+"("+task+"."+subTask+")");
									}
								}
							}
							
						}
						
						
					}
					else
					{
						//System.out.println("Element "+elementName+"("+task+") not found in relevance map");
					}
				}
			
		}
		
		g.setColor(Color.black);
		Stroke regularStroke = g.getStroke();
		Stroke wideStroke = new BasicStroke(6.0f);
		g.setStroke(wideStroke);
		//Draw Axis
		g.drawLine(originX,originY, originX, originY-maxYLength);
		g.drawLine(originX,originY, originX+(maxRelevance +1)* maxRelevanceWidth, originY);
		g.setStroke(regularStroke);
		// Draw Legends
		g.setFont(g.getFont().deriveFont(60f));
		int legendsY = originY+3*g.getFontMetrics().getHeight()-15;
		int legendsX =originX-200;
		g.translate(legendsX, legendsY);
		renderLegendLabels(g);
		g.translate(-legendsX, -legendsY);
		
		int relevanceY = originY+2*g.getFontMetrics().getHeight();
		String relevanceLabel ="Relevance:"; 
		
		g.drawString(relevanceLabel, originX- g.getFontMetrics().stringWidth(relevanceLabel), relevanceY);
		
		int subtaskY = originY+1*g.getFontMetrics().getHeight();
		String subtaskLabel = "Task:";
		g.drawString(subtaskLabel,  originX-g.getFontMetrics().stringWidth(subtaskLabel), subtaskY);
		//Draw Relevance Partitions
		for(int i=0;i<=maxRelevance;i++)
		{
			Stroke previousStroke = g.getStroke();
			
			g.setStroke(wideStroke);
			g.drawLine(originX+(i+1)*maxRelevanceWidth,originY, originX+(i+1)*maxRelevanceWidth, originY-maxYLength);
			
			//Printing relevance
			g.drawString(String.format("%.2f", 1.0 / ( 1+i)),(int)( originX+(i+0.25)*maxRelevanceWidth+35), relevanceY);
			
			Stroke dashed =  new BasicStroke(1.0f,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f, new float[]{2f}, 0.0f);
			g.setStroke(dashed);
			g.setFont(g.getFont().deriveFont(40f));
			
			for(int j=0;j<maxSubtask;j++)
			{	
				g.drawLine(originX+i*maxRelevanceWidth+j*maxTaskWidth,originY, originX+i*maxRelevanceWidth+j*maxTaskWidth, originY-maxYLength);
				g.drawString(""+currentTask+(char)('a'+j), originX+i*maxRelevanceWidth+j*maxTaskWidth+maxTaskWidth/2-20, subtaskY);
			}
			g.setStroke(previousStroke);
			g.setFont(g.getFont().deriveFont(60f));
		}
		
		
		int totalPartitions =4;
		for(int i=0;i<=totalPartitions;i++)
		{
			int y = originY -  i * maxYLength/totalPartitions;
			g.drawLine(originX-15,y, originX+15, y);
			
			double scoreValue = i * 1.0 / factor / totalPartitions;
			g.drawString(String.format("%.3f",scoreValue), originX-200, y+10);
		}
		
		//Error Bars
		Collections.sort(errorBarGlyphList);
		Point[] previousPointPerSubtask = new Point[maxSubtask];
		for(ErrorBarGlyph average: errorBarGlyphList)
		{
			if(average.getRelevance() != StatElement.INFINITY_RELEVANCE)
			{
				g.setStroke(new BasicStroke(1.5f));
				Point p =average.draw(g, originX, originY, maxYLength, maxRelevanceWidth, maxTaskWidth,factor);
				if(previousPointPerSubtask[average.getSubtask()-1] != null)
				{
					Stroke previousStroke = g.getStroke();
					g.setStroke(getStroke(average.getSubtask()));
					g.drawLine(previousPointPerSubtask[average.getSubtask()-1].x, previousPointPerSubtask[average.getSubtask()-1].y, p.x, p.y);
					g.setStroke(previousStroke);
				}
				
				previousPointPerSubtask[average.getSubtask()-1] = p;
			}
			
		}
		
		//Save View border
//		g.translate(-saveViewTranslateX,-saveViewTranslateY);
//		g.setColor(Color.red);
//		g.drawRect(0, 0, getImageDimension().width,getImageDimension().height);
//		g.translate(saveViewTranslateX,saveViewTranslateY);
	}
	
	private Stroke getStroke(int subtask)
	{
		Stroke[] dashed = { 
			new BasicStroke(2f,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,10.0f, new float[]{3f}, 0.0f),
			new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 10.0f, new float[]{6f}, 0.0f),
			new BasicStroke(1.5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 10.0f, new float[]{12f}, 0.0f),
			new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, new float[]{24f}, 0.0f),
			
		};
		
		if(subtask <=dashed.length )
		{
			return dashed[subtask-1];
		}
		
		return new BasicStroke();
	}
	
	private void drawElement(int type,int x, int y, int rad, Graphics2D g)
	{
		Color c = Util.getRelevanceChartColor(type);
		if(type== EyeTrackerItem.TYPE_ACTOR)
		{
			Util.drawSquare(x, y, rad, c, g);
		}
		else if(type== EyeTrackerItem.TYPE_MOVIE)
		{
			Util.drawEquilateralTriangle(x, y, rad, c, g);
		}
		else if(type== EyeTrackerItem.TYPE_DIRECTOR)
		{
			Util.drawStar(x, y, rad, c, g);
		}	
		else if(type== EyeTrackerItem.TYPE_GENRE)
		{
			Util.drawTiltedSquare(x, y, rad, c, g);
		}
	}

}
