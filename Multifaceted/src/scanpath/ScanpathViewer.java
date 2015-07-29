package scanpath;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import multifaceted.FileLineReader;
import perspectives.base.Property;
import perspectives.base.Viewer;
import perspectives.properties.PFileInput;
import perspectives.two_d.JavaAwtRenderer;

public class ScanpathViewer extends Viewer implements JavaAwtRenderer{

	
	public static final String PROPERTY_LOAD_FILE ="Load";	
	
	public static final int TIME_STEP = 500;
	public static final int TIME_LINE_GAP =20;
	public static final int SCANPATH_DIAGRAM_GAP =150;
	public static final int TIME_CELL_WIDTH =5;
	
	
	
	private ArrayList<Scanpath> scanpathList = new ArrayList<Scanpath>();
	public ScanpathViewer(String name) {
		super(name);
		String path ="E:\\Graph\\UserStudy\\IEEEVIS_Poster\\catData\\ScanPath\\";
		PFileInput dirInput = new PFileInput();
		dirInput.onlyDirectories = true;
		Property<PFileInput> pLoad = new Property<PFileInput>(PROPERTY_LOAD_FILE, new PFileInput())
				{
					@Override
					protected boolean updating(PFileInput newvalue) {
						
						readSequenceDirectory(newvalue.path);						
						
						return super.updating(newvalue);
					}
				};
		addProperty(pLoad);
	
		
		PFileInput loadFile = new PFileInput(path);
		pLoad.setValue(loadFile);
	}
	
	private void readSequenceDirectory(String path)
	{
		System.out.println(path);
		
		File dir = new File(path);
		
		File[] sequenceFiles = dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File file) {
				// TODO Auto-generated method stub
				
				return file.getName().toUpperCase().endsWith(".TXT");
			}
		});
		
		for(File file: sequenceFiles)
		{
			Scanpath scanpath = new Scanpath(file.getAbsolutePath());
			
			this.scanpathList.add(scanpath);
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
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		if(this.scanpathList != null && !this.scanpathList.isEmpty())
		{
			g.translate(50, 100);
			for(Scanpath scanpath: scanpathList)
			{
				
				scanpath.render(g);
				g.translate(0, SCANPATH_DIAGRAM_GAP);
			}
			g.translate(0, -this.scanpathList.size()* SCANPATH_DIAGRAM_GAP);
		}
	}

}
