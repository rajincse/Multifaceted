package realtime;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import multifaceted.FileLineReader;
import multifaceted.Util;
import perspectives.base.Property;
import perspectives.base.Viewer;
import perspectives.properties.PFileInput;
import perspectives.properties.PFileOutput;
import perspectives.properties.PInteger;
import perspectives.properties.PList;
import perspectives.properties.PSignal;
import perspectives.two_d.JavaAwtRenderer;
import perspectives.two_d.ViewerContainer2D;

public class EyeTrackDataStreamViewer extends Viewer implements JavaAwtRenderer {

	public static final int CELL_WIDTH =5;
	public static final int CELL_HEIGHT =12;
	public static final int MAX_CELLS =10;
	public static final int MAX_CELL_COLUMNS = 300;
	public static final int MAX_X =1200;
	public static final int MAX_Y = 1000;
	public static final int TRANSLATE_X =100;
	public static final int TRANSLATE_Y =40;
	public static final int TIME_GUIDELINE_UNIT =5000;
	
	public static final int LABEL_WIDTH =200;
	public static final int POSITION_X_USER_NAME=300;
	public static final int POSITION_Y_USER_NAME=10;
	public static final double LABEL_WIDTH_SCORE_EXPONENT =0.4;
	
	public static final int HEIGHT_PER_SUBJECT =120;

	public static final long TIME_LAPSE = 1000;
	public static final double ZOOM_THRESHOLD =0.6;
	
	public static final String TEXT_ALL_SUBJECT ="All"; 
	
	public static final String PROPERTY_LOAD_SEQUENCE ="Load";	
	public static final String PROPERTY_TIME ="Time";
	public static final String PROPERTY_TIME_WINDOW= "Time Window (s)";
	public static final String PROPERTY_TIME_STEP= "Time Step (ms)";
	
	public static final String PROPERTY_SAVE_IMAGE="Save Image";
	public static final String PROPERTY_SUBJECT_LIST = "Subject List";
	private static final String PROPERTY_START ="Play";
	private static final String PROPERTY_STOP ="Pause";
	
	
	private ArrayList<TestSubject> testSubjectList = new ArrayList<TestSubject>();

	private Timer timer =new Timer();
	
	private long maxTime =0;
	
	public EyeTrackDataStreamViewer(String name) {
		super(name);
		try
		{
			String path ="E:\\Graph\\UserStudy\\IEEEVIS_Poster\\catData\\StreamViewer\\Task3.txt";
			int initialTime =0;
			int intialTimeWindow =90;
			int initialTimeStep =1000;
			Property<PFileInput> pLoad = new Property<PFileInput>(PROPERTY_LOAD_SEQUENCE, new PFileInput())
					{
						@Override
						protected boolean updating(PFileInput newvalue) {
							// TODO Auto-generated method stub
							String filePath = newvalue.path;
							
							FileLineReader fileLineReader = new FileLineReader() {
								
								@Override
								public void readLine(String fileLine, File currentFile) {
									// TODO Auto-generated method stub
									readSequenceFileLine(fileLine,currentFile);
								}
							};
							fileLineReader.read(filePath);
							maxTime =0;
							String[] listItems = new String[testSubjectList.size()+1];
							listItems[testSubjectList.size()] =TEXT_ALL_SUBJECT;
							for( int i=0;i<testSubjectList.size();i++)
							{
								TestSubject subject = testSubjectList.get(i);
								listItems[i] = subject.getName();
								if(subject.getTaskList()!= null && !subject.getTaskList().isEmpty())
								{
									long subjectMaxTime = 0;
									for(Task task : subject.getTaskList())
									{
										if(task.getEyeEventList() != null && !task.getEyeEventList().isEmpty())
										{
											subjectMaxTime+= task.getEyeEventList().get(task.getEyeEventList().size()-1).getTime();
										}
									}
									
									if(subjectMaxTime > maxTime)
									{
										maxTime = subjectMaxTime;
									}
								}
								
							}
							
							PList pList = new PList(listItems);
							
							pList.selectedIndeces =new int[]{ listItems.length-1};
							getProperty(PROPERTY_SUBJECT_LIST).setValue(pList);
							
							
							System.out.println("maxTime:"+maxTime);
							
							timeStateChanged(getCurrentTime(), getCurrentTimeWindow(), getCurrentTimeStep());
							
							
							
							return super.updating(newvalue);
						}
					};
			addProperty(pLoad);
			
			
			
			Property<PInteger> pTime = new Property<PInteger>(PROPERTY_TIME, new PInteger(initialTime))
					{
						@Override
						protected boolean updating(PInteger newvalue) {
						
							timeStateChanged(newvalue.intValue(), getCurrentTimeWindow(), getCurrentTimeStep());
							return super.updating(newvalue);
						}
					};
			addProperty(pTime);
			
			Property<PInteger> pTimeWindow = new Property<PInteger>(PROPERTY_TIME_WINDOW, new PInteger(intialTimeWindow))
					{
						@Override
						protected boolean updating(PInteger newvalue) {
							// TODO Auto-generated method stub
							timeStateChanged(getCurrentTime(), newvalue.intValue(), getCurrentTimeStep());
							return super.updating(newvalue);
						}
					};
			addProperty(pTimeWindow);	
			
			Property<PInteger> pTimeStep = new Property<PInteger>(PROPERTY_TIME_STEP, new PInteger(initialTimeStep))
					{
						@Override
						protected boolean updating(PInteger newvalue) {
							// TODO Auto-generated method stub
							timeStateChanged(getCurrentTime(), getCurrentTimeWindow(), newvalue.intValue());
							return super.updating(newvalue);
						}
					};
			addProperty(pTimeStep);	
			
			Property<PSignal> pStart = new Property<PSignal>(PROPERTY_START,new PSignal())
					{
						@Override
						protected boolean updating(PSignal newvalue) {
							// TODO Auto-generated method stub
							startTimer();
							return super.updating(newvalue);
						}
					};
			addProperty(pStart);
			
			Property<PSignal> pStop = new Property<PSignal>(PROPERTY_STOP,new PSignal())
					{
						@Override
						protected boolean updating(PSignal newvalue) {
							// TODO Auto-generated method stub
							stopTimer();
							return super.updating(newvalue);
						}
					};
			addProperty(pStop);
			Property<PList> pSubjectList = new Property<PList>(PROPERTY_SUBJECT_LIST,new PList(new String[]{TEXT_ALL_SUBJECT}))
					{
						@Override
						protected boolean updating(PList newvalue) {
							// TODO Auto-generated method stub
							requestRender();
							return super.updating(newvalue);
						}
					};
			addProperty(pSubjectList);
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
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private void startTimer()
	{
		this.timer = new Timer();
		this.timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				int currentTime = getCurrentTime();
				int maxSubjectTime =(int) Math.ceil(1.0* maxTime/getCurrentTimeStep());
				if(testSubjectList != null && !testSubjectList.isEmpty() && currentTime <maxSubjectTime)
				{
					currentTime++;
					PInteger pTime = new PInteger(currentTime);
					getProperty(PROPERTY_TIME).setValue(pTime);
				}
				else
				{
					stopTimer();
				}
			}
		}
		, 0, TIME_LAPSE);
	}
	
	private void stopTimer()
	{
		this.timer.cancel();
	}
	
	private void timeStateChanged(int timeMarker, int timeWindow, int timeStep)
	{
		
		for(TestSubject subject: testSubjectList)
		{
			subject.prepareRendering(timeMarker *timeStep, timeWindow*1000, timeStep);
		}
		requestRender();
	}
	private void saveView(String filePath)
	{	
		
		// TODO Auto-generated method stub
		
				
		BufferedImage bim = new BufferedImage((int)(SAVE_VIEW_WIDTH* SAVE_VIEW_ZOOM),(int)(SAVE_VIEW_HEIGHT*SAVE_VIEW_ZOOM), BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = bim.createGraphics();
		
		g.scale(SAVE_VIEW_ZOOM, SAVE_VIEW_ZOOM);
		g.translate(SAVE_VIEW_X,SAVE_VIEW_Y);
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
			
	
	}
	
	private void readSequenceFileLine(String fileLine,  File currentFile)
	{
		String directory = currentFile.getParentFile().getAbsolutePath();
		String[] tokens = fileLine.split("\t");
		String subjectName = tokens[0];
		ArrayList<String> fileNameList = new ArrayList<String>();
		for(int i=1;i<tokens.length;i++)
		{
			fileNameList.add(tokens[i]);
		}
		TestSubject subject = new TestSubject(subjectName, directory, fileNameList);
		this.testSubjectList.add(subject);
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
	public boolean mousedragged(int currentx, int currenty, int oldx, int oldy) {
		// TODO Auto-generated method stub
		if(timeSliderHovered)
		{
			int time = (currentx - TRANSLATE_X) / CELL_WIDTH;
			PInteger pTime = new PInteger(time);
			getProperty(PROPERTY_TIME).setValue(pTime);
			return true;
		}
		
		else
		{
			ViewerContainer2D container = (ViewerContainer2D)this.getContainer();
			double zoom = container.getZoom();
			if(container.rightButtonDown && zoom < ZOOM_THRESHOLD)
			{
				container.setZoom(ZOOM_THRESHOLD);
				return true;
			}
			else
			{
				return false;
			}
			
		}
		
	}

	public static final int TIME_SLIDER_WIDTH =20;
	private boolean timeSliderHovered = false;
	
	@Override
	public boolean mousemoved(int x, int y) {
		
		//time dragging
		int time = getCurrentTime();
		int timeStartX = time* CELL_WIDTH+ TRANSLATE_X - TIME_SLIDER_WIDTH/2;
		int timeEndX = timeStartX+TIME_SLIDER_WIDTH;
		if(x >= timeStartX && x <= timeEndX)
		{
			timeSliderHovered = true;
		}
		else
		{
			timeSliderHovered = false;
		}
		
		// hover text
		int index = (y-TRANSLATE_Y)/ HEIGHT_PER_SUBJECT;
		if( y >= TRANSLATE_Y && index >=0 && !this.testSubjectList.isEmpty() &&  index <this.testSubjectList.size())
		{
			int[] selectedIndices = getCurrentSelectedIndices();
			boolean containsAll = false;
			for(int i:selectedIndices)
			{
				if(i == testSubjectList.size()) // 'All' item resides at the end
				{
					containsAll = true;
					break;
				}
			}
			TestSubject subject =this.testSubjectList.get(index);
			if(!containsAll && selectedIndices[index] < this.testSubjectList.size())
			{
				subject =this.testSubjectList.get(selectedIndices[index]);
			}
			
			HashMap<DataObject, Double> itemLabelHeight = subject.getItemLabelHeight();
			ArrayList<DataObject> qualifiedItem = subject.getQualifiedItems();
			if(!itemLabelHeight.isEmpty() && !qualifiedItem.isEmpty())
			{
				int yInSubject = y- index * HEIGHT_PER_SUBJECT- TRANSLATE_Y;
				int heightIterator =0;
				for(DataObject obj : qualifiedItem)
				{
					if(itemLabelHeight.containsKey(obj))
					{
						int height = itemLabelHeight.get(obj).intValue();
						heightIterator+= height;
						if(yInSubject < heightIterator)
						{
							this.setToolTipText(subject.getName()+":"+obj.getLabel()+"("+Util.getTypeName(obj.getType())+")");
							break;
						}
					}
				}
			}
		}
		else
		{
			this.setToolTipText("");
		}
		return false;
	}

	@Override
	public boolean mousepressed(int x, int y, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mousereleased(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	private void renderTimeLine(Graphics2D g)
	{
		g.setColor(Color.gray);
		g.drawLine(0, 0, 0, MAX_Y);
		g.setColor(Color.black);
		
		int time = getCurrentTime();
		int timeStep = getCurrentTimeStep();
		int sliderPosition = Math.min(time, MAX_CELL_COLUMNS);
		g.drawLine((sliderPosition)*CELL_WIDTH, 0, (sliderPosition)*CELL_WIDTH, MAX_Y);
		if(timeSliderHovered)
		{
			g.drawLine((sliderPosition)*CELL_WIDTH- TIME_SLIDER_WIDTH/2, 10, (sliderPosition)*CELL_WIDTH+ TIME_SLIDER_WIDTH/2, 10);
		}
		double timeInSeconds =1.0*time*timeStep/1000.0; 
		g.setFont(g.getFont().deriveFont(10.0f));
		g.drawString(String.format("%.2f",timeInSeconds)+" s", (sliderPosition)*CELL_WIDTH, 0);
//		g.drawString("0 s", 0, 0);
		
		
		if(timeStep < TIME_GUIDELINE_UNIT)
		{
			
			long starTime = (long)Math.max(0, (1.0 *time- EyeTrackDataStreamViewer.MAX_CELL_COLUMNS) * timeStep);
			long endTime = time * timeStep;
			
			long markIterator =(long) Math.ceil(1.0 * starTime /  TIME_GUIDELINE_UNIT) * TIME_GUIDELINE_UNIT;
			while(markIterator < endTime)
			{
				if(markIterator > starTime)
				{
					int position =(int)( 1.0 *(markIterator - starTime)*CELL_WIDTH / timeStep);
					g.drawLine(position, 10, position, MAX_Y);
					g.drawString(String.format("%.0f",1.0*markIterator/1000)+"s", position-3, 10);
					
				}
				markIterator += TIME_GUIDELINE_UNIT;
			}
			
			
		}
	}
	private int getCurrentTimeWindow()
	{
		return ((PInteger)getProperty(PROPERTY_TIME_WINDOW).getValue()).intValue();
	}
	
	private int getCurrentTimeStep()
	{
		return ((PInteger)getProperty(PROPERTY_TIME_STEP).getValue()).intValue();
	}
	private int getCurrentTime()
	{
		return ((PInteger)getProperty(PROPERTY_TIME).getValue()).intValue();
	}
	private int[] getCurrentSelectedIndices()
	{
		return ((PList)getProperty(PROPERTY_SUBJECT_LIST).getValue()).selectedIndeces;
	}
	public static final int SAVE_VIEW_X=205;
	public static final int SAVE_VIEW_Y=10;
	public static final int SAVE_VIEW_WIDTH=1850;
	public static final int SAVE_VIEW_HEIGHT=1050;
	public static final double SAVE_VIEW_ZOOM =2.0;
	
	private void showBoundingBox(Graphics2D g)
	{
		Color previousColor = g.getColor();
		g.setColor(Color.red);
		
		g.translate(-SAVE_VIEW_X, -SAVE_VIEW_Y);
		
		g.drawRect(0, 0, SAVE_VIEW_WIDTH, SAVE_VIEW_HEIGHT);
		
		g.translate(SAVE_VIEW_X, SAVE_VIEW_Y);
		
		
		g.setColor(previousColor);
	}
	
	private void overlayTimeWindow(Graphics2D g)
	{
		Color previousColor = g.getColor();
		g.setColor(new Color(120,120,120,75));
		
		int totalUsers = testSubjectList.size();
		int height = totalUsers * HEIGHT_PER_SUBJECT;
		int currentTime = getCurrentTime();
		int timeWindow = getCurrentTimeWindow();
		
		
		int width = (Math.min(MAX_CELL_COLUMNS,currentTime) - timeWindow)*CELL_WIDTH;
		
		g.translate(TRANSLATE_X, TRANSLATE_Y);
		//drawGray Area
		if(width > 0)
		{
			g.fillRect(0, -40, width, height+80);
		}
		
		//drawWindow
//		int x = Math.max(0, width);
//		width = Math.min(currentTime * CELL_WIDTH - x, timeWindow*CELL_WIDTH);
//		g.setColor(Color.black);
//		g.setStroke(new BasicStroke(5));
//		g.drawRect(x, -40, width, height+80);
		
		g.translate(-TRANSLATE_X, -TRANSLATE_Y);
	
		
		
		
		
		g.setColor(previousColor);
	}
	
	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		
		
		if(!testSubjectList.isEmpty())
		{
			// Check All index present or not
			int[] selectedIndices = getCurrentSelectedIndices();
			boolean containsAll = false;
			for(int index:selectedIndices)
			{
				if(index == testSubjectList.size()) // 'All' item resides at the end
				{
					containsAll = true;
					break;
				}
			}
			
			int currentTime = getCurrentTime();
			
			//rendering
			g.translate(TRANSLATE_X, 0);
			
			
			int yTranslate =TRANSLATE_Y;
			g.translate(0, yTranslate);
			
			float strokeWidth =4.0f;
			Stroke previousStroke = g.getStroke();
			g.setStroke(new BasicStroke(strokeWidth));
			g.setColor(Color.black);
//			int maxX =(int)( maxTime* CELL_WIDTH / getCurrentTimeStep());
			int maxX =MAX_CELL_COLUMNS* CELL_WIDTH ;
			g.drawLine(-LABEL_WIDTH, 0, maxX, 0);
			g.setStroke(previousStroke);
			
			
			if(containsAll)
			{
				for(TestSubject subject: testSubjectList)
				{
					
					subject.render(g, currentTime);
					g.translate(0, HEIGHT_PER_SUBJECT);
					g.setStroke(new BasicStroke(strokeWidth));
					g.setColor(Color.black);				
					g.drawLine(-LABEL_WIDTH, 0, maxX, 0);
					g.setStroke(previousStroke);
					yTranslate+= HEIGHT_PER_SUBJECT;
					
				}
			}
			else
			{
				for(int selectedIndex:selectedIndices)
				{
					if(selectedIndex >= 0 && selectedIndex < testSubjectList.size())
					{
						TestSubject subject = testSubjectList.get(selectedIndex);
						subject.render(g, currentTime);
						g.translate(0, HEIGHT_PER_SUBJECT);
						g.setStroke(new BasicStroke(strokeWidth));
						g.setColor(Color.black);				
						g.drawLine(-LABEL_WIDTH, 0, maxX, 0);
						g.setStroke(previousStroke);
						yTranslate+= HEIGHT_PER_SUBJECT;
					}
					
				}
				
			}
			
			g.translate(0, -yTranslate);
			renderTimeLine(g);
			g.translate(-TRANSLATE_X, 0);
			
//			showBoundingBox(g);
			overlayTimeWindow(g);
		}
	}

}
