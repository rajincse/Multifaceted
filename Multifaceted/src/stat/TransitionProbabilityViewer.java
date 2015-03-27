package stat;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import multifaceted.IOUtil;

import perspectives.base.Property;
import perspectives.base.Viewer;
import perspectives.properties.PFileInput;
import perspectives.two_d.JavaAwtRenderer;

public class TransitionProbabilityViewer extends Viewer implements JavaAwtRenderer {

	public static final String PROPERTY_LOAD = "Load";
	
	public TransitionProbabilityViewer(String name) {
		super(name);
		
		
		Property<PFileInput> pLoad = new Property<PFileInput>(PROPERTY_LOAD, new PFileInput())
				{
					@Override
					protected boolean updating(PFileInput newvalue) {
						// TODO Auto-generated method stub
						readTransProbFile(newvalue.path);
						return super.updating(newvalue);
					}
				};
	}
	
	private void readTransProbFile(String filePath)
	{
		ArrayList<String> fileTextLines = IOUtil.readTextFile(filePath);
		for(String fileLine: fileTextLines)
		{
			String[] data = fileLine.split("\t");
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

	@Override
	public void render(Graphics2D arg0) {
		// TODO Auto-generated method stub
		
	}

}
