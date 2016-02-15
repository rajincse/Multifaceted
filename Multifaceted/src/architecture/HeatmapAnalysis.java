package architecture;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import perspectives.base.Environment;
import perspectives.base.Property;
import perspectives.base.Viewer;
import perspectives.properties.PDouble;
import perspectives.properties.PFileInput;
import perspectives.properties.PInteger;
import perspectives.properties.PList;
import perspectives.properties.POptions;
import perspectives.two_d.JavaAwtRenderer;

public class HeatmapAnalysis extends Viewer implements JavaAwtRenderer {
	
	
	ArrayList<User> users;
	User currentUser;
	User aggUser = null;

	
	Point timeLineHovered = null;
	boolean timeLineDragging = false;
	
	Point startPeriodHovered = null;
	boolean startPeriodDragging = false;
	Point endPeriodHovered = null;
	boolean endPeriodDragging = false;
	
	int dataObjectHovered = -1;
	
	Point heatmapPicked = null;
	
	int heatmapYOffset = 0;
	
	
	
	

	public HeatmapAnalysis(String name) {
		super(name);
		
		users = new ArrayList<User>();
		aggUser = new User("Aggregate", true);
		users.add(aggUser);

		
		Property<PFileInput> pLoad = new Property<PFileInput>("Load User", new PFileInput()){

			@Override
			protected boolean updating(PFileInput newvalue) {
				User u = new User(new File(newvalue.path));
				users.add(u);
				
				aggUser.addToAggregate(u);		
				
				
				PList list = new PList();
				list.items = new String[users.size()];
				for (int i=0; i<users.size(); i++)
					list.items[i] = users.get(i).name;
				list.selectedIndeces = new int[]{list.items.length-1};
				getProperty("Users").setValue(list);
				return super.updating(newvalue);
			}
			
		};
		addProperty(pLoad);
		
		Property<PList> pUserList = new Property<PList>("Users", new PList()){
			@Override
			protected boolean updating(PList newvalue) {
				if (newvalue.selectedIndeces.length > 0)
					currentUser = users.get(newvalue.selectedIndeces[0]);
				
				requestRender();
				return super.updating(newvalue);
			}
		};
		addProperty(pUserList);
		
		Property<PInteger> pTime = new Property<PInteger>("Time", new PInteger(0)){
			@Override
			protected boolean updating(PInteger newvalue) {
				currentUser.changeTime(newvalue.intValue() * 50);
				requestRender();
				return super.updating(newvalue);
			}
		};
		addProperty(pTime);	
		
		Property<PInteger> pCellWidth = new Property<PInteger>("Cell Width", new PInteger(1)){
			@Override
			protected boolean updating(PInteger newvalue) {
				currentUser.setCellWidth(newvalue.intValue()); 
				return super.updating(newvalue);
			}
		};
		addProperty(pCellWidth);	
		
		Property<PInteger> pTimeStep = new Property<PInteger>("Time Step", new PInteger(600)){
			@Override
			protected boolean updating(PInteger newvalue) {
				currentUser.setTimeStep(newvalue.intValue());
				requestRender();
				return super.updating(newvalue);
			}
		};
		addProperty(pTimeStep);	
		
	
		/*Property<PFileInput> pLoadCode = new Property<PFileInput>("Load Coding", new PFileInput()){

			@Override
			protected boolean updating(PFileInput newvalue) {
				if (currentUser != null)
					currentUser.loadCoding(newvalue.path);
				return true;
			}
			
		};
		addProperty(pLoadCode);
		
		Property<PBoolean> withProb = new Property<PBoolean>("WithProb", new PBoolean(true)){
			@Override
			protected boolean updating(PBoolean newvalue){
				currentUser.withProb = newvalue.boolValue();
				return true;
			}
		};
		addProperty(withProb);*/
		
		
		Property<PDouble> pCellFilter = new Property<PDouble>("Cell Filter", new PDouble(1.)){
			@Override
			protected boolean updating(PDouble newvalue){
				currentUser.setCellFilter(newvalue.doubleValue());
				return true;
			}
		};
		addProperty(pCellFilter);

		
		Property<PDouble> pRowFilter = new Property<PDouble>("Row Filter", new PDouble(1.)){
			@Override
			protected boolean updating(PDouble newvalue){
				currentUser.setRowFilter(newvalue.doubleValue());
				return true;
			}
		};
		addProperty(pRowFilter);
		
		Property<POptions> pSort = new Property<POptions>("Sort by", new POptions(new String[]{"First Viewed", "Most Viewed", "Category"})){
			@Override
			protected boolean updating(POptions newvalue){
				currentUser.setSort(newvalue.options[newvalue.selectedIndex]);
				return true;
			}
		};
		addProperty(pSort);
	}
	


	@Override
	public void render(Graphics2D g) {		
		if (currentUser == null)
			return;
		
		g.setColor(Color.red);
		int width = currentUser.heatmap.getWidth()+ currentUser.heatmapXOffset;
		int height = currentUser.heatmap.getHeight();
		g.drawRect(0, 0, width, height);
		
		g.drawString("" + currentUser.currentTime/100, 0, -80);

		if (currentUser.image != null){
			g.drawImage(currentUser.image, 0, 0, null);
			g.setColor(Color.black);
			g.drawRect(-1,-1,currentUser.image.getWidth()+2, currentUser.image.getHeight()+2);
		}
		
		for (int i=0; i<currentUser.gazes.size(); i++){
			int p = (int)(255* ((i+1.)/currentUser.gazes.size()));
			g.setColor(new Color(255,255-p,255-p));
			g.fillOval(currentUser.gazes.get(i).x-10, currentUser.gazes.get(i).y + -10, 20,20);
			
			if (i == currentUser.gazes.size()-1){
				g.setColor(new Color(255,255,255,200));
				//g.fillRect(currentUser.gazes.get(i).x+5, currentUser.gazes.get(i).y-10, 20, 10);
				g.setColor(Color.red);
				g.setFont(g.getFont().deriveFont(15f));
				//g.drawString("" + currentUser.currentTime/100, currentUser.gazes.get(i).x+5, currentUser.gazes.get(i).y);
			}
			//g.drawString("" + currentUser.gazes.get(i).x + "," + currentUser.gazes.get(i).y,currentUser.gazes.get(i).x, currentUser.gazes.get(i).y);
		}
		
		/*for (int i=0; i<currentUser.mouses.size(); i++){		
			int p = (int)(255* ((i+1.)/currentUser.mouses.size()));
			g.setColor(new Color(255-p,255-p,255));
			g.fillOval(currentUser.mouses.get(i).x-5, currentUser.mouses.get(i).y + -5, 10,10);
			g.drawString("" + currentUser.mouses.get(i).x + "," + currentUser.mouses.get(i).y,currentUser.mouses.get(i).x, currentUser.mouses.get(i).y);
		}*/
		
		g.setColor(Color.black);
		g.drawString("" + currentUser.currentTime/100, 0, -10);
		
		if (currentUser.hoverString.length() > 0)
			g.drawString("Hovered: " + currentUser.hoverString, 100, -60);
		if (currentUser.eyeString.length() > 0)
			g.drawString("Eyed: " + currentUser.eyeString, 100, -30);
		
		if (currentUser.heatmap != null){
			
			g.drawImage(currentUser.heatmap,  currentUser.heatmapXOffset, heatmapYOffset,null);
			
			int timeLine = currentUser.timeToPixel(currentUser.currentTime);
			g.setColor(new Color(0,0,0,100));
			g.fillRect(timeLine,heatmapYOffset-50,currentUser.cellWidth,currentUser.heatmap.getHeight()+100);
			g.drawString(currentUser.currentTime+"ms", timeLine, heatmapYOffset-50);			
			if (timeLineHovered != null)
				g.drawLine(timeLine-5, timeLineHovered.y, timeLine+5, timeLineHovered.y);
			
			int sp = currentUser.timeToPixel(currentUser.timePeriodStart);
			g.setColor(new Color(0,150,0,100));
			g.drawLine(sp,heatmapYOffset-50,sp,heatmapYOffset-50+currentUser.heatmap.getHeight()+100);
			g.drawString(currentUser.timePeriodStart+"ms", sp, heatmapYOffset-50);
			if (startPeriodHovered != null)
				g.drawLine(sp-5, startPeriodHovered.y, sp+5, startPeriodHovered.y);
			
			int ep = currentUser.timeToPixel(currentUser.timePeriodEnd);
			g.setColor(new Color(150,0,0,100));
			g.drawLine(ep,heatmapYOffset-50,ep,heatmapYOffset-50+currentUser.heatmap.getHeight()+100);
			g.drawString(currentUser.timePeriodEnd+"ms", ep, heatmapYOffset-50);
			if (endPeriodHovered != null)
				g.drawLine(ep-5, endPeriodHovered.y, ep+5, endPeriodHovered.y);
			
			g.setColor(new Color(100,100,100,50));
			g.fillRect(currentUser.heatmapXOffset, heatmapYOffset, sp - currentUser.heatmapXOffset, currentUser.heatmap.getHeight());
			g.fillRect(ep, heatmapYOffset, currentUser.heatmapXOffset + currentUser.heatmap.getWidth() - ep, currentUser.heatmap.getHeight());
			
			
			
			for (int i=0; i<currentUser.viewedObjects.size(); i++){

				if (currentUser.viewedObjects.get(i).type == 1)
					g.setColor(new Color(250,200,200));
				else if (currentUser.viewedObjects.get(i).type == 2)
					g.setColor(new Color(200,250,200));
				else if (currentUser.viewedObjects.get(i).type == 3)
					g.setColor(new Color(200,200,250));
				else if (currentUser.viewedObjects.get(i).type == 4)
					g.setColor(new Color(250,250,150));
				else if (currentUser.viewedObjects.get(i).type == 5)
					g.setColor(new Color(250,150,250));
				g.fillRect(0, heatmapYOffset+currentUser.cellHeight*i, currentUser.heatmapXOffset,currentUser.cellHeight-1);
				g.setColor(Color.black);
//				String label = currentUser.viewedObjects.get(i).label.substring(0, Math.min(26, currentUser.viewedObjects.get(i).label.length()));
				String label = currentUser.viewedObjects.get(i).label;
				
				g.setFont(g.getFont().deriveFont(10f));
				g.drawString(label, 0, heatmapYOffset+currentUser.cellHeight*i+currentUser.cellHeight-2);
				

			}
			
			
			for (int i=0; i<currentUser.viewedObjects.size(); i++){
				if (dataObjectHovered == i)
					g.drawRect(0, heatmapYOffset+currentUser.cellHeight*i, currentUser.heatmapXOffset + currentUser.heatmap.getWidth(), currentUser.cellHeight);
			}
			
		}
	}

	@Override
	public Color getBackgroundColor() {	return null;}

	@Override
	public boolean mousepressed(int x, int y, int button) {
		if (timeLineHovered != null)
			timeLineDragging = true;
		else if (startPeriodHovered != null)
			startPeriodDragging = true;
		else if (endPeriodHovered != null)
			endPeriodDragging = true;
		else if (new Rectangle(currentUser.heatmapXOffset, heatmapYOffset, currentUser.heatmap.getWidth(), currentUser.heatmap.getHeight()).contains(new Point(x,y)))
			heatmapPicked = new Point(x-currentUser.heatmapXOffset,y);
		return false;
	}

	int cnt = 0;
	@Override
	public boolean mousereleased(int x, int y, int button) {
		
		if (currentUser == null)
			return false;
		
		if (timeLineDragging){
			getProperty("Time").setValue(new PInteger((int)(currentUser.pixelToTime(x) / 50)));
			timeLineDragging = false;
		}		
		
		if (startPeriodDragging || endPeriodDragging){
			currentUser.createHeatmap();
			requestRender();
		}
		startPeriodDragging = false;
		endPeriodDragging = false;
		heatmapPicked = null;
		return false;
	}
	
	@Override
	public boolean mousemoved(int x, int y) {
		
		if (currentUser == null || currentUser.heatmap == null)
			return false;
		
		int timeLine = currentUser.timeToPixel(currentUser.currentTime); 
		Rectangle r = new Rectangle(timeLine-3,heatmapYOffset-50,currentUser.cellWidth+3,heatmapYOffset+currentUser.heatmap.getHeight());
		
		int ps = currentUser.timeToPixel(currentUser.timePeriodStart);
		Rectangle rps = new Rectangle(ps-3,heatmapYOffset-50,currentUser.cellWidth+3,heatmapYOffset+currentUser.heatmap.getHeight());
		int pe = currentUser.timeToPixel(currentUser.timePeriodEnd);
		Rectangle rpe = new Rectangle(pe-3,heatmapYOffset-50,currentUser.cellWidth+3,heatmapYOffset+currentUser.heatmap.getHeight());
		
		if (r.contains(new Point(x,y)))
			timeLineHovered = new Point(x,y);	
		else{
			timeLineHovered = null;	
		
			if (rps.contains(new Point(x,y)))
				startPeriodHovered = new Point(x,y);	
			else
				startPeriodHovered = null;	
			
			if (rpe.contains(new Point(x,y)))
				endPeriodHovered = new Point(x,y);	
			else
				endPeriodHovered = null;
		}
		
		this.setToolTipText("");
		
		dataObjectHovered = -1;
		for (int i=0; currentUser.viewedObjects != null && i<currentUser.viewedObjects.size(); i++){
			r = new Rectangle(0, heatmapYOffset+currentUser.cellHeight*i, currentUser.heatmapXOffset+currentUser.heatmap.getWidth(),currentUser.cellHeight);
			if (r.contains(new Point(x,y))){
				dataObjectHovered = i;
				this.setToolTipText(currentUser.viewedObjects.get(i).label);
			}
		}		
		
		

		
		
		requestRender();
		return false;
	}

	@Override
	public boolean mousedragged(int currentx, int currenty, int oldx, int oldy) {

		if (timeLineDragging){
			currentUser.changeTime(currentUser.pixelToTime(currentx));
			this.requestRender();
			return true;
		}
		if (startPeriodDragging){
			currentUser.setPeriodStart(currentUser.pixelToTime(currentx));
			requestRender();
			return true;
		}
		if (endPeriodDragging){
			currentUser.setPeriodEnd(currentUser.pixelToTime(currentx));
			requestRender();
			return true;
		}
		
		if (heatmapPicked != null){
			 
			currentUser.heatmapXOffset = currentx - heatmapPicked.x;
			requestRender();
			return true;
		}
		return false;
	}

	@Override
	public void keyPressed(String key, String modifiers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(String key, String modifiers) {
		// TODO Auto-generated method stub
		
	}
	
	private static final int SAVE_VIEW_ZOOM = 3;
	public void saveView(String filePath)
	{	
		
		// TODO Auto-generated method stub
		int width = currentUser.heatmap.getWidth()+ currentUser.heatmapXOffset;
		int height = currentUser.heatmap.getHeight();
		Dimension dimension =new Dimension(width, height);
		
		
		if(dimension != null)
		{
			BufferedImage bim = new BufferedImage((int)((dimension.width)* SAVE_VIEW_ZOOM),(int)((dimension.height)*SAVE_VIEW_ZOOM), BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g = bim.createGraphics();
//			g.translate(-INIT_X-TITLE_X+300, -INIT_Y);
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

	public static void main(String[] args)
	{
		if(args.length >=2)
		{
			Environment e = new Environment(true);
			
			HeatmapAnalysis heatmap = new HeatmapAnalysis("Architecture");
			
			e.addViewer(heatmap);
			
			String path =args[0];
			PFileInput input = new PFileInput(path);
			
			heatmap.getProperty("Load User").setValue(input);
			
			heatmap.saveView(args[1]);
			
			System.exit(0);
		}
		
		
	}
	

	
	
	





}
