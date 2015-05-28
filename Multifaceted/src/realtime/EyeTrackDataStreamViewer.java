package realtime;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import multifaceted.FileLineReader;

import perspectives.base.Property;
import perspectives.base.Viewer;
import perspectives.properties.PFileInput;
import perspectives.properties.PFileOutput;
import perspectives.properties.PInteger;
import perspectives.two_d.JavaAwtRenderer;

public class EyeTrackDataStreamViewer extends Viewer implements JavaAwtRenderer {

	public static final int CELL_WIDTH =5;
	public static final int CELL_HEIGHT =12;
	public static final int MAX_CELLS =10;
	public static final int MAX_X =1200;
	public static final int MAX_Y = 1000;
	public static final int TRANSLATE_X =100;
	public static final int TRANSLATE_Y =40;
	
	public static final int TIME_STEP =500;
	public static final int TIME_WINDOW = 20000;
	public static final int LABEL_WIDTH =200;
	public static final int POSITION_X_USER_NAME=300;
	public static final int POSITION_Y_USER_NAME=10;
	public static final double LABEL_WIDTH_SCORE_EXPONENT =0.4;
	
	public static final int HEIGHT_PER_SUBJECT =120;
	
	public static final String PROPERTY_LOAD_SEQUENCE ="Load";	
	public static final String PROPERTY_TIME ="Time";
	public static final String PROPERTY_SAVE_IMAGE="Save Image";
	
	
	private ArrayList<TestSubject> testSubjectList = new ArrayList<TestSubject>();
	public EyeTrackDataStreamViewer(String name) {
		super(name);
		try
		{
			String path ="E:\\Graph\\UserStudy\\IEEEVIS_Poster\\catData\\Sequence_small_2.txt";
			int initialTime =100;
			
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
							return super.updating(newvalue);
						}
					};
			addProperty(pLoad);
			
			PFileInput loadFile = new PFileInput(path);
			pLoad.setValue(loadFile);
			
			
			Property<PInteger> pTime = new Property<PInteger>(PROPERTY_TIME, new PInteger(0))
					{
						@Override
						protected boolean updating(PInteger newvalue) {
						
							for(TestSubject subject: testSubjectList)
							{
								subject.prepareRendering(newvalue.intValue() * EyeTrackDataStreamViewer.TIME_STEP);
							}
							requestRender();
							return super.updating(newvalue);
						}
					};
			addProperty(pTime);
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
			
			
			PInteger pSetTime = new PInteger(initialTime);
			pTime.setValue(pSetTime);
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private void saveView(String filePath)
	{	
		
		// TODO Auto-generated method stub
		
		int time = getCurrentTime();
		
		int width = time* CELL_WIDTH+POSITION_X_USER_NAME;
				
		BufferedImage bim = new BufferedImage(width+50,MAX_Y+50, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = bim.createGraphics();
		
		g.translate(POSITION_X_USER_NAME, 0);
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
	public boolean mousedragged(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mousemoved(int x, int y) {
		// TODO Auto-generated method stub
		
		int index = (y-TRANSLATE_Y)/ HEIGHT_PER_SUBJECT;
		if( y >= TRANSLATE_Y && index >=0 && index <this.testSubjectList.size())
		{
			TestSubject subject =this.testSubjectList.get(index);
			HashMap<DataObject, Double> itemLabelHeight = subject.getItemLabelHeight();
			ArrayList<DataObject> qualifiedItem = subject.getQualifiedItems();
			if(!itemLabelHeight.isEmpty() && !qualifiedItem.isEmpty())
			{
				int yInSubject = y- index * HEIGHT_PER_SUBJECT- TRANSLATE_Y;
				int heightIterator =0;
				for(DataObject obj : qualifiedItem)
				{
					int height = itemLabelHeight.get(obj).intValue();
					heightIterator+= height;
					if(yInSubject < heightIterator)
					{
						this.setToolTipText(obj.getLabel());
						break;
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
		g.drawLine(time*CELL_WIDTH, 0, time*CELL_WIDTH, MAX_Y);
	}
	
	private int getCurrentTime()
	{
		return ((PInteger)getProperty(PROPERTY_TIME).getValue()).intValue();
	}
	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		if(!testSubjectList.isEmpty())
		{
			g.translate(TRANSLATE_X, 0);
			
			
			int yTranslate =TRANSLATE_Y;
			g.translate(0, yTranslate);
			g.setColor(Color.black);
			g.drawLine(-LABEL_WIDTH, 0, MAX_X, 0);
			
			
			
			for(TestSubject subject: testSubjectList)
			{
				
				subject.render(g);
				g.translate(0, HEIGHT_PER_SUBJECT);
				g.setColor(Color.black);
				g.drawLine(-LABEL_WIDTH, 0, MAX_X, 0);
				yTranslate+= HEIGHT_PER_SUBJECT;
				
			}
			
			g.translate(0, -yTranslate);
			renderTimeLine(g);
			g.translate(-TRANSLATE_X, 0);
		}
	}

}
