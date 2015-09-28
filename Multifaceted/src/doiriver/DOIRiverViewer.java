package doiriver;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;

import multifaceted.FileLineReader;
import multifaceted.InsidePolygonTester;
import multifaceted.Util;

import perspectives.base.Property;
import perspectives.base.Viewer;
import perspectives.properties.PFileInput;
import perspectives.properties.PFileOutput;
import perspectives.two_d.JavaAwtRenderer;
import perspectives.util.SplineFactory;
import realtime.DataObject;
import realtime.EyeEvent;
import scanpath.MultiScanpath;
import scanpath.ScanpathViewer;


public class DOIRiverViewer extends Viewer implements JavaAwtRenderer{

	
	public static final String PROPERTY_LOAD_FILE ="Load";	
	public static final String PROPERTY_SAVE_IMAGE="Save Image";
	
	public static final Color COLOR_INITIAL_OBJECT = new Color(0,0,255,100);
	public static final Color COLOR_INITIAL_LINE_HORIZONTAL = new Color(0,0,0,75);
	public static final Color COLOR_INITIAL_LINE_TRANSITION = new Color(0,0,0,100);
	public static final Color COLOR_INITIAL_TIMELINE = new Color(0,0,0,75);

	public static final int TIME_STEP =2500;
	public static final int INVALID =-1;
	public static final int INFINITY =10000;
	
	public static final int INIT_X =100;
	public static final int INIT_Y =100;
	
	public static final int TIME_CELL_WIDTH =50;
	public static final int CURVE_WIDTH = 15;	
	public static final int WIDTH_TITLE = 300;
	public static final int TITLE_X = -300;
	
	
	
	private ArrayList<String> userList = new ArrayList<String>();
	private ArrayList<ArrayList<EyeEvent>> eyeEventList = new ArrayList<ArrayList<EyeEvent>>();
	private ArrayList<HashMap<String, DataObject>> dataObjectList = new ArrayList<HashMap<String, DataObject>>();
	private long startTime =0;
	private int maxTimeCell =0;
	
	private ArrayList<River> riverList = new ArrayList<River>();
	
	public DOIRiverViewer(String name) {
		super(name);
		String path ="E:\\Graph\\UserStudy\\IEEEVIS_Poster\\catData\\DOIRiver\\";
		PFileInput dirInput = new PFileInput();
		dirInput.onlyDirectories = true;
		Property<PFileInput> pLoad = new Property<PFileInput>(PROPERTY_LOAD_FILE,dirInput)
				{
					@Override
					protected boolean updating(PFileInput newvalue) {
						process(newvalue.path);
						requestRender();
						return super.updating(newvalue);
					}
				};
		addProperty(pLoad);
		
		Property<PFileOutput> pSaveImage = new Property<PFileOutput>(PROPERTY_SAVE_IMAGE, new PFileOutput())
				{
					@Override
					protected boolean updating(PFileOutput newvalue) {
						// TODO Auto-generated method stub
						
						saveView(newvalue.path);
						
						return super.updating(newvalue);
					}
				};
		addProperty(pSaveImage);
		
		PFileInput loadFile = new PFileInput(path);
		pLoad.setValue(loadFile);
		
	}

	private void process(String directoryPath)
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
		
		DataObject[][] dataObjectPerTime = new DataObject[sequenceFiles.length][];
		int index=0;
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
			
			dataObjectPerTime[index] = getObjectsPerTime(index);
			index++;
			
		}
		
		HashMap<DataObject, HashMap<Integer, Double>> timeScoreMap = new HashMap<DataObject, HashMap<Integer,Double>>();
		
		for(int userIndex=0;userIndex<dataObjectPerTime.length;userIndex++)
		{
			for(int timeIndex=0;timeIndex<dataObjectPerTime[userIndex].length;timeIndex++)
			{
				DataObject object = dataObjectPerTime[userIndex][timeIndex];
				if(object != null)
				{
					if(timeScoreMap.containsKey(object))
					{
						HashMap< Integer, Double> timeScore = timeScoreMap.get(object);
						Integer timeIndexObject = new Integer(timeIndex);
						if(timeScore.containsKey(timeIndexObject))
						{
							Double previousScore = timeScore.get(timeIndexObject);
							previousScore+= 1.0;
							timeScore.put(timeIndexObject, previousScore);
						}
						else
						{
							timeScore.put(timeIndex, 1.0);
						}
					}
					else
					{
						HashMap< Integer, Double> timeScore = new HashMap<Integer, Double>();
						timeScore.put(new Integer(timeIndex), 1.0);
						timeScoreMap.put(object, timeScore);
					}	
				}
				
			}
			if(dataObjectPerTime[userIndex].length > maxTimeCell)
			{
				maxTimeCell = dataObjectPerTime[userIndex].length;
			}
		}
		
		
		ArrayList<Color> colorList = new ArrayList<Color>();
		River previousRiver = null;
		for(DataObject obj :timeScoreMap.keySet())
		{
			double[] timeScore = new double[maxTimeCell];
			for(Integer timeIndex: timeScoreMap.get(obj).keySet())
			{
				timeScore[timeIndex] = timeScoreMap.get(obj).get(timeIndex);
			}
			Color randomColor = getRandomColor();
			while(colorList.contains(randomColor))
			{
				randomColor = getRandomColor();
			}
			River river = new River(obj, timeScore, randomColor);
			river.setPreviousRiver(previousRiver);
			previousRiver = river;
			river.prepareRendering();
			riverList.add(river);
		}
		printDebug();
	}
	
	private Color getRandomColor()
	{
		Random rand = new Random();
		float r = rand.nextFloat() / 2.0f + 0.5f;
		float g = rand.nextFloat() / 2.0f + 0.5f;
		float b = rand.nextFloat() / 2.0f + 0.5f;
		Color randomColor = new Color(r, g, b);
		
		return randomColor;
	}
	
	private void printDebug()
	{
		String msg="";
		for(int i=0;i<riverList.size();i++)
		{
			River river =riverList.get(i);
			
			msg +=river.getDataObject().getLabel()+"-> ";
			
			for(int j=0;j<river.getTimeScores().length;j++)
			{
				msg+=String.format("%.2f", river.getTimeScores()[j])+", ";
			}
			msg+="\r\n";
		}
		
		System.out.println(msg);
	}
	
	public DataObject[] getObjectsPerTime(int userIndex)
	{
		ArrayList<EyeEvent> eyeEvents = eyeEventList.get(userIndex);
		
		int totalCells =(int)(eyeEvents.get(eyeEvents.size()-1).getTime()/ DOIRiverViewer.TIME_STEP)+1;
		DataObject[] objects = new DataObject[totalCells];
		ArrayList<DataObject> dataObjectCollection = new ArrayList<DataObject>();
		int lastIndex =0;
		for(EyeEvent eye: eyeEvents)
		{
			
			int index = (int)(eye.getTime()/DOIRiverViewer.TIME_STEP);
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
				objects[lastIndex] = bestDataObject;
				
				lastIndex = index;
				dataObjectCollection.clear();
			}
		}
		return objects;
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
			return DOIRiverViewer.INVALID;
		}
			
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
	public boolean mousemoved(int x, int y) {
		// TODO Auto-generated method stub
		int transformedX = x -INIT_X;
		int transformedY = y -INIT_Y;
		 
		 setToolTipText("");
		 
		 for(int i=0;i<riverList.size() ;i++)
		 {
			 River river = riverList.get(i);
			 if(river.containsPointInside(transformedX, transformedY)){
				 setToolTipText(river.getDataObject().getLabel());
				 break;
			 }
		 }
		
		return false;
	}

	@Override
	public boolean mousepressed(int x, int y, int button) {
		// TODO Auto-generated method stub
		int transformedX = x -INIT_X;
		int transformedY = y -INIT_Y;
		 
		for(int i=0;i<riverList.size();i++)
		{
			 River river = riverList.get(i);
			 river.setSelected(false);
		}
		 
		 for(int i=0;i<riverList.size() ;i++)
		 {
			 River river = riverList.get(i);
			 if(river.containsPointInside(transformedX, transformedY)){
				 river.setSelected(true);
				 break;
			 }
		 }
		return false;
	}

	@Override
	public boolean mousereleased(int x, int y, int button) {
		// TODO Auto-generated method stub
		 
		return false;
	}

	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		g.translate(INIT_X, INIT_Y);
		
		
		g.setColor(Color.black);
		g.drawLine(0, 0, TIME_CELL_WIDTH*maxTimeCell, 0);
		
		for(int i=0;i<riverList.size()  ;i++)
		{
			River river = riverList.get(i);

			int alpha =130;
			if(river.isSelected())
			{
				alpha = 255;
				g.setColor(Color.black);
				g.drawPolygon(river.getCurvedPolygonX(), river.getCurvedPolygonY(), river.getCurvedPolygonX().length);
			}
			g.setColor(Util.getAlphaColor(river.getColor(),alpha));
			g.fillPolygon(river.getCurvedPolygonX(), river.getCurvedPolygonY(), river.getCurvedPolygonX().length);
			
			
		}
		
		g.translate(TITLE_X, 0);
		
		
		for(int i=0;i<riverList.size() ;i++)
		{
			River river = riverList.get(i);
			g.setColor(river.getColor());
			g.fillRect(0,i*CURVE_WIDTH, WIDTH_TITLE-30,CURVE_WIDTH );
			Util.drawTextBox(g, Color.black, river.getDataObject().getLabel(), new Rectangle(0,i*CURVE_WIDTH, WIDTH_TITLE-30,CURVE_WIDTH ));
			if(river.isSelected())
			{
				g.setColor(Color.red);
				g.drawRect(0,i*CURVE_WIDTH, WIDTH_TITLE-30,CURVE_WIDTH);
			}
		}
		
		
		g.translate(-TITLE_X, 0);
		
		g.translate(-INIT_X, -INIT_Y);
		
		
		
	}
	public static double SAVE_VIEW_ZOOM =2;
	private void saveView(String filePath)
	{	
		
		// TODO Auto-generated method stub
		
		Dimension dimension =new Dimension(INIT_X-TITLE_X+maxTimeCell*TIME_CELL_WIDTH+300,INIT_Y+riverList.size()*CURVE_WIDTH);
		
		
		if(dimension != null)
		{
			BufferedImage bim = new BufferedImage((int)((dimension.width)* SAVE_VIEW_ZOOM),(int)((dimension.height)*SAVE_VIEW_ZOOM), BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g = bim.createGraphics();
			g.translate(-INIT_X-TITLE_X+300, -INIT_Y);
			g.scale(SAVE_VIEW_ZOOM, SAVE_VIEW_ZOOM);
			
			render(g);
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
			System.out.println("Image Saved:"+filePath);
		}
	}

}
