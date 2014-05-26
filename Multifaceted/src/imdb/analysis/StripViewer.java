package imdb.analysis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import perspectives.base.Property;
import perspectives.properties.PFileInput;
import perspectives.two_d.JavaAwtRenderer;
import perspectives.util.Label;

public class StripViewer extends AnalysisViewer implements JavaAwtRenderer{
	public static final String PROPERTY_OPEN_FILE = "Open";
	
	protected HashMap<String, AnalysisItem> analysisItemList = new HashMap<String, AnalysisItem>();
	protected LinkedList<TimeStampItem> timeStampList = new LinkedList<TimeStampItem>();
	protected ArrayList<Color> colorList = new ArrayList<Color>();
	protected ArrayList<Label> labelList = new ArrayList<Label>();
	
	private ArrayList<Integer> lineXList = new ArrayList<Integer>();
	private ArrayList<String> lineStringList = new ArrayList<String>();
	public StripViewer(String name) {
		super(name);
		try
		{
			Property<PFileInput> pOpenFile = new Property<PFileInput>(PROPERTY_OPEN_FILE, new PFileInput())
			{
				 @Override
				protected boolean updating(PFileInput newvalue) {
					// TODO Auto-generated method stub
					 processFile(newvalue.path);
					 createVisualItems();
					 printInfo();
					 requestRender();
					return super.updating(newvalue);
				}
			};
			addProperty(pOpenFile);
		}catch(Exception e)
		
		{
			
		}
	}

	private  Color getRandomColor()
	{
		Random rand = new Random();
		int r =((int) Math.abs(rand.nextInt()) )% 155 + 100;
		int g =((int) Math.abs(rand.nextInt()) )% 155 +100;
		int b =((int) Math.abs(rand.nextInt()) )% 155 +100;
		
		return new Color(r,g,b);
	}
	private Color getUniqueColor()
	{
		Color color = getRandomColor();
		while(colorList.contains(color))
		{	
			color = getRandomColor();
		}
		
		return color;
	}
	@Override
	protected void processFileLine(String line) 
	{
		String[] split = line.split("\t");
		if(split.length >= 3)
		{
			long timeStamp = Long.parseLong(split[0]);
			
			String id = split[1];
			String name = split[2];
			AnalysisItem item = new AnalysisItem(id, name);
			if(analysisItemList.containsKey(id))
			{
				item = analysisItemList.get(id);
			}
			else
			{	
				Color color = getUniqueColor();
				item.setColor(color);
				colorList.add(color);
				analysisItemList.put(id, item);
			}
			
			if(!this.timeStampList.isEmpty() && (timeStampList.peekLast().getTimeStamp() == timeStamp))
			{
				TimeStampItem timeStampItem = timeStampList.peekLast();
				timeStampItem.addItem(item);
			}
			else
			{
				
				TimeStampItem timeStampItem = new TimeStampItem(timeStamp);
				timeStampItem.addItem(item);
				this.timeStampList.add(timeStampItem);
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
		if(!timeStampList.isEmpty())
		{	
			for(TimeStampItem timestamp: timeStampList)
			{
				System.out.println(timestamp);
			}
		}
		if(!labelList.isEmpty())
		{
			for(Label label: labelList)
			{
				System.out.println("{ text:"+label.getText()+",  x:"+label.x+", y:"+label.y+", w:"+label.w+", h:"+label.h+"}");
			}
		}
	}

	@Override
	protected void createVisualItems() {
		if(!timeStampList.isEmpty())
		{
			long previousTime = timeStampList.peekFirst().getTimeStamp(); 
			int width =0;
			TimeStampItem previousItem = timeStampList.peekFirst();
			
			int x =100;
			int y =100;
			int h =50;
			for(TimeStampItem timestamp: timeStampList)
			{
				
				if(width > 5 && !previousItem.equals(timestamp))
				{
					
					for(int i=0;i<previousItem.getItems().size();i++)
					{
						AnalysisItem analysisItem = previousItem.getItems().get(i);
						Label label = new Label(x, y+i*h, analysisItem.getName());
						BufferedImage bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);			
						FontMetrics fm = bi.getGraphics().getFontMetrics(label.getFont());
						java.awt.geom.Rectangle2D rect = fm.getStringBounds(label.getText(), bi.getGraphics());
						int size =(int)( label.getFont().getSize() * width / rect.getWidth());
						Font font = new Font("Sans-Serif",Font.PLAIN,size);
						label.setFont(font);
						label.setColor(analysisItem.getColor());
						label.x = label.x+ label.w/2;
						
						labelList.add(label);
						
						if(label.w > width)
						{
							width = (int)label.w;
						}
					}
					x=x+ width;
					previousTime = timestamp.getTimeStamp(); 
					width =0;
					previousItem = timestamp;
					
				}
				else
				{
					width =(int)( timestamp.getTimeStamp() - previousTime);
				}
			}
		}
		
		TimeStampItem first = timeStampList.peekFirst();
		TimeStampItem last = timeStampList.peekLast();
		long timeRange = last.getTimeStamp() - first.getTimeStamp();
		int minX =100;
		int step =1000;
		Label lastLabel = labelList.get(labelList.size()-1);
		int maxX =(int)( lastLabel.x+ lastLabel.w/2);
		int xRange = maxX- minX;
		for(long t =0;t < timeRange;t+=step)
		{
			int x =(int)( minX + t* xRange / timeRange);
			lineXList.add(x);
			lineStringList.add("t = "+t/step);
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
		if(!labelList.isEmpty())
		{	
			for(int i=0;i<labelList.size();i++)
			{
				Label label = labelList.get(i);
				
				label.render(g);
			}
			for(int i=0;i<lineXList.size();i++)
			{
				g.setColor(Color.black);
				g.setStroke(new BasicStroke(6));
				int x = lineXList.get(i);
				g.drawLine(x, -1700, x,2000);
				Font font = new Font("Sans-Serif",Font.PLAIN,50);
				g.setFont(font);
				g.drawString(lineStringList.get(i), x-5,-1705);
			}
		}
	}
}
