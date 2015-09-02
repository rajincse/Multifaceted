package doiriver;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import perspectives.base.Property;
import perspectives.base.Viewer;
import perspectives.properties.PFileInput;
import perspectives.properties.PFileOutput;
import perspectives.two_d.JavaAwtRenderer;


public class DOIRiverViewer extends Viewer implements JavaAwtRenderer{

	
	public static final String PROPERTY_LOAD_FILE ="Load";	
	public static final String PROPERTY_SAVE_IMAGE="Save Image";
	
	public static final Color COLOR_INITIAL_OBJECT = new Color(0,0,255,100);
	public static final Color COLOR_INITIAL_LINE_HORIZONTAL = new Color(0,0,0,75);
	public static final Color COLOR_INITIAL_LINE_TRANSITION = new Color(0,0,0,100);
	public static final Color COLOR_INITIAL_TIMELINE = new Color(0,0,0,75);

	public static final int TIME_STEP =5000;
	public static final int INVALID =-1;
	public static final int INFINITY =10000;
	
	public static final int TIME_CELL_WIDTH =20;
	public static final int TIME_CELL_HEIGHT =10;
	public static final int CURVE_WIDTH = 5;
	public static final int DIAGRAM_GAP =10;	
	public static final int WIDTH_TITLE = 300;
	public static final int WIDTH_ANCHOR = 25;
	
	
	private DOIRiverDiagram doiRiverDiagram = null;
	
	public DOIRiverViewer(String name) {
		super(name);
		String path ="E:\\Graph\\UserStudy\\IEEEVIS_Poster\\catData\\DOIRiver\\";
		PFileInput dirInput = new PFileInput();
		dirInput.onlyDirectories = true;
		Property<PFileInput> pLoad = new Property<PFileInput>(PROPERTY_LOAD_FILE,dirInput)
				{
					@Override
					protected boolean updating(PFileInput newvalue) {
											
						doiRiverDiagram = new DOIRiverDiagram(newvalue.path);
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
		if(doiRiverDiagram != null)
		{
			doiRiverDiagram.render(g, COLOR_INITIAL_OBJECT, COLOR_INITIAL_LINE_TRANSITION, 1.0);
		}
		
	}
	public static double SAVE_VIEW_ZOOM =2;
	private void saveView(String filePath)
	{	
		
		// TODO Auto-generated method stub
		
		Dimension dimension =doiRiverDiagram.getImageDimension();
		
		
		if(dimension != null)
		{
			BufferedImage bim = new BufferedImage((int)((dimension.width)* SAVE_VIEW_ZOOM),(int)((dimension.height)*SAVE_VIEW_ZOOM), BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g = bim.createGraphics();
			
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
