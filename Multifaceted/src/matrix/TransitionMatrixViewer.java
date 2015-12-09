package matrix;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.imageio.ImageIO;

import multifaceted.FileLineReader;
import multifaceted.Util;


import perspectives.base.Property;
import perspectives.base.Viewer;
import perspectives.properties.PFileInput;
import perspectives.properties.PFileOutput;
import perspectives.two_d.JavaAwtRenderer;
import realtime.DataObject;
import realtime.EyeEvent;


public class TransitionMatrixViewer extends Viewer implements JavaAwtRenderer{
	public static final String PROPERTY_LOAD_FILE ="Load File";
	public static final String PROPERTY_SAVE_IMAGE="Save Image";

	private static final int TIME_STEP = 500;
	public static final int THRESHOLD =5;
	private static final int MAX_COUNT = 25;
	
	private ArrayList<EyeEvent> eyeEventList = new ArrayList<EyeEvent>();
	private HashMap<String, DataObject> dataObjectList = new HashMap<String, DataObject>();
	private long startTime =0;
	private int[][] transitionMatrix;
	private DataObject[] objectSequence;

	private ArrayList<DataObject> renderingObjectList = new ArrayList<DataObject>();
	
	public TransitionMatrixViewer(String name) {
		super(name);
		String path ="E:\\Graph\\UserStudy\\IEEEVIS_Poster\\catData\\Matrix\\4_1.txt";
		
		Property<PFileInput> pLoad = new Property<PFileInput>(PROPERTY_LOAD_FILE,new PFileInput())
				{
					@Override
					protected boolean updating(PFileInput newvalue) {
						loadFile(newvalue.path);
						prepareRender();
//						printData();
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

	private void loadFile(String path)
	{
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
		fileLineReader.read(path);
	}
	private void prepareRender()
	{
		prepareRenderingObjects();
		renderingObjectList = filterSequence(objectSequence, MAX_COUNT);
		
		this.transitionMatrix = new int[renderingObjectList.size()][renderingObjectList.size()];
		for(int i=0;i<transitionMatrix.length;i++)
		{
			for(int j=0;j<transitionMatrix[i].length;j++)
			{
				transitionMatrix[i][j]=0;
			}
		}
		
		DataObject lastObject =null;
		for(int i=0;i<objectSequence.length;i++)
		{
			DataObject currentObject = objectSequence[i];
			
			if(lastObject != null && currentObject != null && !currentObject.equals(lastObject)
					&& renderingObjectList.contains(currentObject) && renderingObjectList.contains(lastObject)
			)
			{
				int sourceIndex = renderingObjectList.indexOf(lastObject);
				int destination = renderingObjectList.indexOf(currentObject);
				
				int value = transitionMatrix[sourceIndex][destination];
				value++;
				transitionMatrix[sourceIndex][destination] = value;
				
				
			}
			lastObject = currentObject;
			
		}
		
	}
	private void printData()
	{
		for(int i=0;i<renderingObjectList.size();i++)
		{
			System.out.println(i+"\t"+renderingObjectList.get(i).getLabel()+"\t"+(int)renderingObjectList.get(i).getSortingScore());
		}
		String msg ="";
		for(int i=0;i<renderingObjectList.size();i++)
		{
			
			for(int j=0;j<renderingObjectList.size();j++)
			{
				
				int value = transitionMatrix[i][j];
				msg+=value+"\t";
			}
			msg+="\r\n";
		}
		System.out.println(msg);
	}
	private void prepareRenderingObjects()
	{
		int totalCells =(int)(eyeEventList.get(eyeEventList.size()-1).getTime()/ TIME_STEP)+1;
		objectSequence = new DataObject[totalCells];

		
		ArrayList<DataObject> dataObjectCollection = new ArrayList<DataObject>();
		int lastIndex =0;
		for(EyeEvent eye: eyeEventList)
		{
			
			int index = (int)(eye.getTime()/TIME_STEP);
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
				objectSequence[lastIndex] = bestDataObject;
				if(bestDataObject != null )
				{
					renderingObjectList.add(bestDataObject);
				}
				
				
				lastIndex = index;
				dataObjectCollection.clear();
			}
		}
	}
	private ArrayList<DataObject> filterSequence(DataObject[] objectSequence, int maxCount)
	{
		ArrayList<DataObject> sortingList = new ArrayList<DataObject>();
		for(int i=0;i<objectSequence.length;i++)
		{
			if(objectSequence[i] != null)
			{
				if(!sortingList.contains(objectSequence[i]))
				{
					objectSequence[i].setSortingScore(1);
					sortingList.add(objectSequence[i]);
				}
				else
				{
					double sortingScore = objectSequence[i].getSortingScore()+1;
					objectSequence[i].setSortingScore(sortingScore);
					int objectIndex = sortingList.indexOf(objectSequence[i]);
					sortingList.set(objectIndex, objectSequence[i]);
				}
			}
		}
		Collections.sort(sortingList, new Comparator<DataObject>() {

			@Override
			public int compare(DataObject o1, DataObject o2) {
				// TODO Auto-generated method stub
				return Double.compare(o2.getSortingScore(), o1.getSortingScore() );
			}
			
		});
		
		ArrayList<DataObject> filteringList = new ArrayList<DataObject>();
		for(int i=0;i<sortingList.size() && i < maxCount;i++)
		{
			filteringList.add(sortingList.get(i));
		}
		
		return filteringList;
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
	public static final int LABEL_WIDTH =300;
	public static final int LABEL_HEIGHT =20;
	
	public static final Color COLOR_LABEL_TEXT = Color.black;
	public static final Color COLOR_LABEL_BORDER = Color.BLUE;
	public static final Color COLOR_MATIRX_BORDER = Color.gray;
	public static final Color COLOR_CELL_BACKGROUND = Color.black;
	public static final Color COLOR_CELL_FOREGROUND = Color.white;

	private static final double SAVE_VIEW_ZOOM = 3.0;
	
	

	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		if(this.renderingObjectList != null && !this.renderingObjectList.isEmpty())
		{
			
			double theta =90 * Math.PI /180;
			//Draw Rotated Label
			g.rotate(-theta);
			g.translate(LABEL_WIDTH, 0);
			drawLabels(g);
			g.translate(-LABEL_WIDTH, 0);
			g.rotate(theta);
			//Draw Normal Label
			drawLabels(g);
			
			int cellSize = LABEL_HEIGHT;
			g.setColor(COLOR_MATIRX_BORDER);
			for(int i=0;i<=renderingObjectList.size();i++)
			{
				g.drawLine(i*cellSize, 0,i*cellSize, renderingObjectList.size()*cellSize);
				g.drawLine( 0,i*cellSize, renderingObjectList.size()*cellSize, i*cellSize);
			}
			
			for(int i=0;i<transitionMatrix.length;i++)
			{
				for(int j=0;j<transitionMatrix[i].length;j++)
				{
					if(transitionMatrix[i][j] >0)
					{
						DataObject source = renderingObjectList.get(i);
						
						DataObject destination = renderingObjectList.get(j);
						
						drawMatrixCell(g, i, j, transitionMatrix[i][j]);
						
					}
					
				}
			}
		}
	}
	
	public void drawMatrixCell(Graphics2D g, int i,int j, int value)
	{
		int cellSize = LABEL_HEIGHT;
		g.setColor(COLOR_CELL_BACKGROUND);
		Rectangle rect = new Rectangle(j*cellSize, i*cellSize, cellSize, cellSize);
		
		g.fillRect(rect.x, rect.y, rect.width, rect.height);
		
		Util.drawTextBox(g, COLOR_CELL_FOREGROUND, ""+value, rect);
	}
	
	public void drawLabels(Graphics2D g)
	{
		
		for(int i=0;i<renderingObjectList.size();i++)
		{
			DataObject dataObject = renderingObjectList.get(i);
			
			int y = i*LABEL_HEIGHT;
			
			Rectangle rect = new Rectangle(-LABEL_WIDTH, y, LABEL_WIDTH, LABEL_HEIGHT);
			Util.drawTextBox(g, COLOR_LABEL_TEXT, dataObject.getLabel(), rect);
			g.setColor(COLOR_LABEL_BORDER);
			g.drawRect(rect.x, rect.y, rect.width, rect.height);
		}
	}
	private void saveView(String filePath)
	{	
		
		// TODO Auto-generated method stub
		
		Dimension dimension =new Dimension(LABEL_WIDTH + renderingObjectList.size()* LABEL_HEIGHT, LABEL_WIDTH +  renderingObjectList.size()* LABEL_HEIGHT);
		if(dimension != null)
		{
			BufferedImage bim = new BufferedImage((int)((dimension.width)* SAVE_VIEW_ZOOM),(int)((dimension.height)*SAVE_VIEW_ZOOM), BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g = bim.createGraphics();
			
			
			g.scale(SAVE_VIEW_ZOOM, SAVE_VIEW_ZOOM);
			g.translate(LABEL_WIDTH, LABEL_WIDTH);
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
