package scanpath;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import perspectives.base.Property;
import perspectives.base.Viewer;
import perspectives.properties.PColor;
import perspectives.properties.PDouble;
import perspectives.properties.PFileInput;
import perspectives.properties.PFileOutput;
import perspectives.properties.POptions;
import perspectives.two_d.JavaAwtRenderer;
import realtime.DataObject;

public class ScanpathViewer extends Viewer implements JavaAwtRenderer{

	
	public static final String PROPERTY_LOAD_FILE ="Load";	
	public static final String PROPERTY_DIAGRAM_TYPE = "Diagram Type";
	public static final String PROPERTY_COLOR_OBJECT ="Object Color";
	public static final String PROPERTY_COLOR_LINE_HORIZONTAL ="Horizontal Line Color";
	public static final String PROPERTY_COLOR_LINE_TRANSITION ="Transition Line Color";
	public static final String PROPERTY_RATIO_HEIGHT = "Height Ratio";	
	public static final String PROPERTY_SAVE_IMAGE="Save Image";
	
	
	public static final String[] TYPE_NAME_DIAGRAM= new String[]{"User-> DOI", "DOI -> User"};
	public static final int TYPE_USER_DOI =0;
	public static final int TYPE_DOI_USER =1;
	
	public static final int TIME_STEP = 500;
	
	public static final int SCANPATH_DIAGRAM_GAP =10;
	
	public static final int TIME_CELL_WIDTH =10;
	public static final int TIME_CELL_HEIGHT =5;
	
	public static final int INIT_TRANSLATE_X =50;
	public static final int INIT_TRANSLATE_Y =100;
	
	public static final Color COLOR_BACKGROUND_SELECTION = new Color(166, 170,171, 120);
	public static final Color COLOR_SELECTION = Color.red;
	
	public static final int INFINITY =10000;
	public static final int INVALID =-1;
	
	private ArrayList<Scanpath> scanpathList = new ArrayList<Scanpath>();
	private MultiScanpath multiscanpath = null;
	
	public ScanpathViewer(String name) {
		super(name);
		String path ="E:\\Graph\\UserStudy\\IEEEVIS_Poster\\catData\\ScanPath\\";
		PFileInput dirInput = new PFileInput();
		dirInput.onlyDirectories = true;
		Property<PFileInput> pLoad = new Property<PFileInput>(PROPERTY_LOAD_FILE,dirInput)
				{
					@Override
					protected boolean updating(PFileInput newvalue) {
						
						readSequenceDirectory(newvalue.path);						
						multiscanpath = new MultiScanpath(newvalue.path);
						requestRender();
						return super.updating(newvalue);
					}
				};
		addProperty(pLoad);
		
		POptions diagramNameOptions = new POptions(TYPE_NAME_DIAGRAM);
		diagramNameOptions.selectedIndex = TYPE_USER_DOI;
		Property<POptions> pDiagramType = new Property<POptions>(PROPERTY_DIAGRAM_TYPE, diagramNameOptions)
				{
						@Override
						protected boolean updating(POptions newvalue) {
							// TODO Auto-generated method stub
							requestRender();
							return super.updating(newvalue);
						}
				};
		addProperty(pDiagramType);
		
		Property<PColor> pColorObject = new Property<PColor>(PROPERTY_COLOR_OBJECT, new PColor(Color.blue))
				{
					@Override
					protected boolean updating(PColor newvalue) {
						// TODO Auto-generated method stub
						requestRender();
						return super.updating(newvalue);
					}
				};
		addProperty(pColorObject);
		
		Property<PColor> pColorLineHorizontal = new Property<PColor>(PROPERTY_COLOR_LINE_HORIZONTAL, new PColor(Color.black))
				{
					@Override
					protected boolean updating(PColor newvalue) {
						// TODO Auto-generated method stub
						requestRender();
						return super.updating(newvalue);
					}
				};
		addProperty(pColorLineHorizontal);
		
		Property<PColor> pColorLineTransition = new Property<PColor>(PROPERTY_COLOR_LINE_TRANSITION, new PColor(Color.black))
				{
					@Override
					protected boolean updating(PColor newvalue) {
						// TODO Auto-generated method stub
						requestRender();
						return super.updating(newvalue);
					}
				};
		addProperty(pColorLineTransition);
		
		Property<PDouble> pHeightRatio = new Property<PDouble>(PROPERTY_RATIO_HEIGHT, new PDouble(1.0))
				{
					@Override
					protected boolean updating(PDouble newvalue) {
						// TODO Auto-generated method stub
						requestRender();
						return super.updating(newvalue);
					}
				};
		addProperty(pHeightRatio);
		
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
	
	private int getSelectedIndexPOptions(String propertyName)
	{
		return ((POptions)this.getProperty(propertyName).getValue()).selectedIndex; 
	}
	private Color getColor(String propertyName)
	{
		return ((PColor)this.getProperty(propertyName).getValue()).colorValue();
	}
	
	private double getDouble(String propertyName)
	{
		return ((PDouble)this.getProperty(propertyName).getValue()).doubleValue();
	}
	private void readSequenceDirectory(String path)
	{
		
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
	public boolean mousedragged(int currentx, int currenty, int oldx, int oldy) {
		// TODO Auto-generated method stub
		int diagramType = getSelectedIndexPOptions(PROPERTY_DIAGRAM_TYPE);
		if(diagramType == TYPE_USER_DOI)
		{
			int selectedDiagramIndex = getSelectedDiagramIndex();
			if(selectedDiagramIndex != INVALID)
			{
				Point point = new Point(currentx,currenty);
				AffineTransform transform = new AffineTransform();
				transform.translate(-INIT_TRANSLATE_X, -INIT_TRANSLATE_Y);
				Point transformedPoint = new Point();
				transform.transform(point, transformedPoint);
				int scanpathY =0;
				int destinationIndex =selectedDiagramIndex;
				for(int i=0;i<scanpathList.size();i++)
				{
					Dimension d= scanpathList.get(i).getImageDimension();
					scanpathY += d.height;
					
					if(transformedPoint.y >= scanpathY-d.height && transformedPoint.y <= scanpathY)
					{
						destinationIndex = i;
						
						break;
					}
					else
					{
						scanpathY+= SCANPATH_DIAGRAM_GAP;
					}
				}
				if(destinationIndex <0 )
				{
					destinationIndex = 0;
					
				}
				else if(destinationIndex > scanpathY)
				{
					destinationIndex = scanpathList.size()-1;
				}
				if(selectedDiagramIndex != destinationIndex)
				{
					Scanpath scanpath = scanpathList.remove(selectedDiagramIndex);
					scanpathList.add(destinationIndex, scanpath);
				}
				return true;
			}
			
		}
		else if(diagramType == TYPE_DOI_USER)
		{
			return this.multiscanpath.mousedragged(currentx, currenty, oldx, oldy);
		}
		return false;
	}

	@Override
	public boolean mousemoved(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mousepressed(int x, int y, int button) {
		// TODO Auto-generated method stub
		int diagramType = getSelectedIndexPOptions(PROPERTY_DIAGRAM_TYPE);
		unselectAllDiagrams(diagramType);
		if(button == MouseEvent.BUTTON1)
		{
			
			if(diagramType == TYPE_USER_DOI)
			{
				Point point = new Point(x,y);
				AffineTransform transform = new AffineTransform();
				transform.translate(-INIT_TRANSLATE_X, -INIT_TRANSLATE_Y);
				Point transformedPoint = new Point();
				transform.transform(point, transformedPoint);
				int scanpathY =0;
				DataObject selectedObject = null;
				for(Scanpath scanpath: scanpathList)
				{
					Dimension d= scanpath.getImageDimension();
					scanpathY += d.height;
					
					if(transformedPoint.y >= scanpathY-d.height && transformedPoint.y <= scanpathY)
					{
						if(transformedPoint.x >= 0 && transformedPoint.x <= d.width)
						{
							int mouseX =transformedPoint.x ;
							int mouseY = transformedPoint.y - scanpathY+d.height;
							scanpath.setSelected(true);
							selectedObject = scanpath.getSelectedObject(mouseX, mouseY);							
						}
						
						break;
					}
					else
					{
						scanpathY+= SCANPATH_DIAGRAM_GAP;
					}
				}
				
				if(selectedObject != null)
				{
					for(Scanpath scanpath: scanpathList)
					{
						scanpath.setSelectedObject(selectedObject);
					}
				}
			}
			else if(diagramType == TYPE_DOI_USER)
			{
				return this.multiscanpath.mousepressed(x, y, button);
			}
		}
		
		return false;
	}
	private int getSelectedDiagramIndex()
	{
		
		for(int i=0;i<scanpathList.size();i++)
		{
			if(scanpathList.get(i).isSelected())
			{
				return i;
			}
		}
		return INVALID;
	}
	private void unselectAllDiagrams(int diagramType)
	{
		
		if(diagramType == TYPE_USER_DOI)
		{
			
			for(Scanpath scanpath: scanpathList)
			{
				scanpath.setSelected(false);
				scanpath.setSelectedObject(null);
			}
			
		}
		else if(diagramType == TYPE_DOI_USER)
		{
			this.multiscanpath.unselectAllDiagrams();
		}
	}

	@Override
	public boolean mousereleased(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		
		int diagramType = getSelectedIndexPOptions(PROPERTY_DIAGRAM_TYPE);
		Color objectColor = getColor(PROPERTY_COLOR_OBJECT);
		Color horizontalColor = getColor(PROPERTY_COLOR_LINE_HORIZONTAL);
		Color transitionColor = getColor(PROPERTY_COLOR_LINE_TRANSITION);
		double heightRatio = getDouble(PROPERTY_RATIO_HEIGHT);
		
		g.translate(INIT_TRANSLATE_X, INIT_TRANSLATE_Y);
		
		
		if(diagramType == TYPE_USER_DOI && this.scanpathList != null && !this.scanpathList.isEmpty())
		{
			
			
			
			int totalYTranslate=0;
			for(Scanpath scanpath: scanpathList)
			{	
				scanpath.render(g, objectColor, horizontalColor, transitionColor, heightRatio);
				int yTranslate = scanpath.getRowCount()*TIME_CELL_HEIGHT+SCANPATH_DIAGRAM_GAP; 
				g.translate(0, yTranslate);
				totalYTranslate+= yTranslate;
			}
			g.translate(0, -totalYTranslate);
		}
		else if(diagramType == TYPE_DOI_USER && multiscanpath != null)
		{
			this.multiscanpath.render(g, objectColor, horizontalColor, transitionColor, heightRatio);
		}
		g.translate(-INIT_TRANSLATE_X, -INIT_TRANSLATE_Y);
		
	}
	public static double SAVE_VIEW_ZOOM =3;
	private void saveView(String filePath)
	{	
		
		// TODO Auto-generated method stub
		int diagramType = getSelectedIndexPOptions(PROPERTY_DIAGRAM_TYPE);
		Dimension dimension =null;
		if(diagramType == TYPE_USER_DOI)
		{
			dimension = Scanpath.getImageDimension(scanpathList);
		}
		else if(diagramType == TYPE_DOI_USER)
		{
			dimension = this.multiscanpath.getImageDimension();
		}
		
		if(dimension != null)
		{
			BufferedImage bim = new BufferedImage((int)((dimension.width+INIT_TRANSLATE_X)* SAVE_VIEW_ZOOM),(int)((dimension.height+INIT_TRANSLATE_Y)*SAVE_VIEW_ZOOM), BufferedImage.TYPE_INT_ARGB);
			
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
