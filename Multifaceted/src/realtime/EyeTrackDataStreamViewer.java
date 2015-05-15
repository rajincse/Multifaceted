package realtime;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import multifaceted.FileLineReader;

import perspectives.base.Property;
import perspectives.base.Viewer;
import perspectives.properties.PFileInput;
import perspectives.two_d.JavaAwtRenderer;

public class EyeTrackDataStreamViewer extends Viewer implements JavaAwtRenderer {

	public static final String PROPERTY_LOAD_SEQUENCE ="Load";
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
								public void readLine(String fileLine) {
									// TODO Auto-generated method stub
									readSequenceFileLine(fileLine);
								}
							};
							fileLineReader.read(filePath);
							return super.updating(newvalue);
						}
					};
			addProperty(pLoad);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	private void readSequenceFileLine(String fileLine)
	{
		System.out.println(fileLine);
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
