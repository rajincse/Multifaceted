package realtime;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import multifaceted.FileLineReader;

import perspectives.base.Property;
import perspectives.base.Viewer;
import perspectives.properties.PFileInput;
import perspectives.properties.PInteger;
import perspectives.two_d.JavaAwtRenderer;

public class EyeTrackDataStreamViewer extends Viewer implements JavaAwtRenderer {

	public static final int CELL_WIDTH =1;
	public static final int CELL_HEIGHT =12;
	public static final int MAX_X =1200;
	public static final int MAX_Y = 1000;
	public static final int TIME_RATIO =500;
	
	public static final int HEIGHT_PER_SUBJECT =25;
	
	public static final String PROPERTY_LOAD_SEQUENCE ="Load";
	
	public static final String PROPERTY_TIME ="Time";
	
	
	private ArrayList<TestSubject> testSubjectList = new ArrayList<TestSubject>();
	public EyeTrackDataStreamViewer(String name) {
		super(name);
		try
		{
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
			
			Property<PInteger> pTime = new Property<PInteger>(PROPERTY_TIME, new PInteger(0))
					{
						@Override
						protected boolean updating(PInteger newvalue) {
						
							requestRender();
							return super.updating(newvalue);
						}
					};
			addProperty(pTime);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
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
		System.out.println(subject);
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

	private void renderTimeLine(Graphics2D g)
	{
		g.setColor(Color.gray);
		g.drawLine(0, 0, 0, MAX_Y);
		g.setColor(Color.black);
		
		int time = getCurrentTime();
		g.drawLine(time, 0, time, MAX_Y);
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
			g.translate(100, 0);
			
			renderTimeLine(g);
			int yTranslate =50;
			g.translate(0, yTranslate);
			
			int time = getCurrentTime();
			
			for(TestSubject subject: testSubjectList)
			{
				
				subject.render(g, time * TIME_RATIO);
				g.translate(0, HEIGHT_PER_SUBJECT);
				yTranslate+= HEIGHT_PER_SUBJECT;
				
			}
			
			g.translate(0, -yTranslate);
			g.translate(-100, 0);
		}
	}

}
