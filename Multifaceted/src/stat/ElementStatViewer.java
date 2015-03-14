package stat;

import imdb.IMDBDataSource;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import eyetrack.EyeTrackerItem;

import perspectives.base.Property;
import perspectives.base.Viewer;
import perspectives.properties.PFileInput;
import perspectives.two_d.JavaAwtRenderer;

public class ElementStatViewer extends Viewer implements JavaAwtRenderer {

	public static final String PROPERTY_LOAD="Load";
	public static final String PROPERTY_LOAD_RELEVANT_LIST="Load Relevant List";
	public static final String PROPERTY_LOAD_IGNORE_LIST="Load Ignore List";
	
	
	private ArrayList<StatElement> elementNames = new ArrayList<StatElement>();
	private HashMap<Long, ArrayList<ViewItem>> relevantItemMap = new HashMap<Long, ArrayList<ViewItem>>(); 
	private ArrayList<String> ignoreList = new ArrayList<String>();
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
			PFileInput inputFile = new PFileInput();
			inputFile.onlyDirectories = true;
			Property<PFileInput> pLoad = new Property<PFileInput>(PROPERTY_LOAD, inputFile)
					{
						@Override
						protected boolean updating(PFileInput newvalue) {
							// TODO Auto-generated method stub
//							processFile(newvalue.path);
							readSingleUser(newvalue.path);
							return super.updating(newvalue);
						}
					};
			addProperty(pLoad);
			
			
		}
		catch(Exception ex)
		{
			
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
				System.out.println("View\t"+elem.getName()+"\t"+elem.getId()+"\t"+elem.getType()+"\t"+elem.getElementCount());
				elem.printItems();
			}
		}		
		this.elementNames.clear();
		
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
			int index = this.elementNames.indexOf(new StatElement(elemName));
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
			
			for(String line:fileLines)
			{
				String[] splits =line.split("\t");
				if(splits[0].equals("Image"))
				{
					String imageFileName = splits[2];
				
					String elementName = imageFileName.substring(16, imageFileName.length()-9);
					elementName = elementName.replaceAll("[0-9]", "");
//					System.out.println("line=>"+line+", image=>"+imageFileName+", elem=>"+elementName);
					StatElement element = new StatElement(elementName);
					
					
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
					int index = this.elementNames.indexOf(new StatElement(elementName));
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
//			for(StatElement elem: this.elementNames)
//			{
//				System.out.println("Elem:"+elem);
//			}
			
			
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
		ArrayList<ViewItem> relevantList = this.relevantItemMap.get(elem.getId());
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

	@Override
	public void render(Graphics2D arg0) {
		// TODO Auto-generated method stub
		
	}

}
