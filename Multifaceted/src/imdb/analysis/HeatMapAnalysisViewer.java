package imdb.analysis;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import perspectives.base.ObjectInteraction;
import perspectives.base.Property;
import perspectives.properties.PFileInput;
import perspectives.properties.PFileOutput;
import perspectives.two_d.JavaAwtRenderer;
import perspectives.util.Label;

public class HeatMapAnalysisViewer extends AnalysisViewer implements JavaAwtRenderer{
	public static final long CELL_RESOLUTION  = 10000;
	public static final String PROPERTY_OPEN_FILE = "Open";
	public static final String PROPERTY_SAVE_IMAGE = "Save Image";
	
	public static final Color[] HEATMAP_COLOR = new Color[]
			{
				new Color(0,252,67,200),
		
				new Color(0,252,0,200),
		
				new Color(93,252,0,200),
		
				new Color(151,252,0,200),
		
				new Color(240,252,0,200),
		
				new Color(252,194,0,200),
		
				new Color(252,143,0,200),
		
				new Color(252,97,0,200),
		
				new Color(252,46,0,200),
		
				new Color(252,0,0,200)
			};
	
	public LinkedList<HeatMapTimeStamp> timeStamps = new LinkedList<HeatMapTimeStamp>();
	protected HashMap<String, AnalysisItem> analysisItemList = new HashMap<String, AnalysisItem>();
	protected ObjectInteraction objectInteraction = null;
	public HeatMapAnalysisViewer(String name) {
		super(name);
		// TODO Auto-generated constructor stub
		try
		{
			Property<PFileInput> pOpenFile = new Property<PFileInput>(PROPERTY_OPEN_FILE, new PFileInput())
			{
				 @Override
				protected boolean updating(PFileInput newvalue) {
					// TODO Auto-generated method stub
					 processFile(newvalue.path);
//					 createVisualItems();
//					 printInfo();
					 requestRender();
					return super.updating(newvalue);
				}
			};
			addProperty(pOpenFile);
			
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
			initObjectInteraction();
		}catch(Exception e)
		
		{
			
		}
	}
	protected void initObjectInteraction()
	{
		objectInteraction = new ObjectInteraction()
		{

			@Override
			protected void mouseIn(int object) {
				VisualItem item = objectInteraction.getItem(object) ;
				if(item instanceof HeatMapCell)
				{
					HeatMapCell cell =(HeatMapCell) item;
					setToolTipText(cell.getItem().getName()+", "+String.format("%.2f",cell.getScore()));
					requestRender();
				}
				
				
				
			}

			@Override
			protected void mouseOut(int object) {
				setToolTipText("");
			}

			@Override
			protected void itemsSelected(int[] objects) {
				
			}
			
		};
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
	public boolean mousedragged(int x, int y, int oldX, int oldY) {
		// TODO Auto-generated method stub
		objectInteraction.mouseMove(x, y);
		return false;
	}

	@Override
	public boolean mousemoved(int x, int y) {
		// TODO Auto-generated method stub
		return objectInteraction.mouseMove(x, y);
	}

	@Override
	public boolean mousepressed(int x, int y, int button) {
		// TODO Auto-generated method stub
		objectInteraction.mousePress(x, y);
		return false;
	}

	@Override
	public boolean mousereleased(int x, int y, int button) {
		// TODO Auto-generated method stub
		objectInteraction.mouseRelease(x, y);
		return false;
	}

	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		int x =200;
		int y =100;
		int gap =3;
		
		int step = 12;
		int i=0;
		Color color1 = new Color(223, 218, 242);
		Color color2 = new Color(244, 220, 245);
		double maxWidth =-1;
		for(AnalysisItem item: analysisItemList.values())
		{
			Label label = new Label(x, y, item.getName());
			if(i%2== 0)
			{
				label.setColor(color1);
			}
			else
			{
				label.setColor(color2);
			}
			label.render(g);
			
			y+= step+gap;
			i++;
			
			if(label.w > maxWidth)
			{
				maxWidth = label.w;
			}
		}
		
		x+= maxWidth/2;
		for(HeatMapTimeStamp timeStamp: timeStamps)
		{
			x+= step+gap;
			y = 100;
			for(AnalysisItem item: analysisItemList.values())
			{
				if(timeStamp.getCellList().containsKey(item.getId()))
				{
					HeatMapCell cell = timeStamp.getCellList().get(item.getId());
					cell.setRectangleInfo((int)x, (int)y,(int) step,(int) step);
					cell.r.render(g);
				}
				y+= step+gap;
			}
		}
	}

	@Override
	protected void processFileLine(String line) {
		String[] split = line.split("\t");
		if(split.length >= 7)
		{
			long timeStamp = Long.parseLong(split[0]);
			
			String id = split[1];
			String name = split[2];
			int layer = Integer.parseInt(split[3]);
			double score = Double.parseDouble(split[4]);
			int x = Integer.parseInt(split[5]);
			int y = Integer.parseInt(split[6]);
			String image = "";
			
			if(split.length == 8)
			{
				image = split[7];
			}
			
			AnalysisItem item = new AnalysisItem(id, name);
			if(analysisItemList.containsKey(id))
			{
				item = analysisItemList.get(id);
			}
			else
			{	
				analysisItemList.put(id, item);
			}
			
			if(!this.timeStamps.isEmpty() && (timeStamp <= this.timeStamps.peekLast().getTimeStamp()+CELL_RESOLUTION))
			{
				HeatMapTimeStamp timeStampItem = timeStamps.peekLast();
				HeatMapCell cell = HeatMapCell.createInstance(id, name, score, image, x, y);
				
				timeStampItem.addItem(cell);
				objectInteraction.addItem(timeStampItem.getCellList().get(id));
			}
			else
			{
				
				HeatMapTimeStamp timeStampItem = new HeatMapTimeStamp(timeStamp);
				HeatMapCell cell = HeatMapCell.createInstance(id, name, score, image, x, y);
				timeStampItem.addItem(cell);
				objectInteraction.addItem(timeStampItem.getCellList().get(id));
				this.timeStamps.add(timeStampItem);
				
			}
		}
		
	}

	@Override
	protected void printInfo() {
		// TODO Auto-generated method stub
		System.out.println("items");
		if(!analysisItemList.isEmpty())
		{	
			for(AnalysisItem item: analysisItemList.values())
			{
				System.out.println(item);
			}
		}
		System.out.println("Timestamps:");
		if(!timeStamps.isEmpty())
		{	
			for(HeatMapTimeStamp timestamp: timeStamps)
			{
				System.out.println(timestamp);
			}
		}
		
		System.out.println("Visual Items:");
		int total =objectInteraction.getNumberOfItems();
		for(int i=0;i<total;i++)
		{
			System.out.println("{index:"+i+", item:"+(HeatMapCell)objectInteraction.getItem(i)+"}");
		}
		
	}

	public static Color getHeatMapcolor(double score)
	{
		double factor = 2.0;
		int colorIndex = (int )(score*factor);
		colorIndex = Math.min(colorIndex, HeatMapAnalysisViewer.HEATMAP_COLOR.length-1);
		
		return HeatMapAnalysisViewer.HEATMAP_COLOR[colorIndex];
	}
	@Override
	protected void createVisualItems() {
		// TODO Auto-generated method stub
		
	}

	private void saveView(String filePath)
	{	
		// TODO Auto-generated method stub
		
		int width =timeStamps.size()*15+300;
		int height = analysisItemList.size()*15 +100;
		BufferedImage bim = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = bim.createGraphics();
		
		render(g);
		
		
		
		String filename = filePath;
		if(!filename.toUpperCase().endsWith(".PNG"))
		{
			filename = filePath+ ".PNG";
		}
		
		try {
			ImageIO.write(bim, "PNG", new File(filename ));
			JOptionPane.showMessageDialog(null, "Image saved to "+filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
	}
	

}
