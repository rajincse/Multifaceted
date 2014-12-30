package eyeinterestanalyzer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;

import javax.jws.soap.SOAPBinding.Use;

import eyeinterestanalyzer.clustering.Cluster;
import eyeinterestanalyzer.clustering.ClusteringStringItem;
import eyeinterestanalyzer.clustering.HierarchicalClustering;

import perspectives.base.Property;
import perspectives.base.Viewer;
import perspectives.properties.PDouble;
import perspectives.properties.PFileInput;
import perspectives.properties.PInteger;
import perspectives.properties.PList;
import perspectives.properties.POptions;
import perspectives.two_d.JavaAwtRenderer;

public class ScarfplotViewer extends Viewer implements JavaAwtRenderer{
	public static final String PROPERTY_LOAD_USER = "Load User";
	public static final String PROPERTY_USERS = "Users";
	public static int CLUSTER_SHIFT_X = 30;
	
	public static int VALUE_CELL_WIDTH =1;
	public static int VALUE_TIME_STEP =600;
	
	ArrayList<User> users;
	User currentUser =null;

	
	Point timeLineHovered = null;
	boolean timeLineDragging = false;
	
	Point startPeriodHovered = null;
	boolean startPeriodDragging = false;
	Point endPeriodHovered = null;
	boolean endPeriodDragging = false;
	
	int dataObjectHovered = -1;
	
	Point heatmapPicked = null;
	
	int heatmapYOffset = 50;
	
	private Cluster clusterRoot =null;
	
	

	public ScarfplotViewer(String name) {
		super(name);
		
		users = new ArrayList<User>();

		
		Property<PFileInput> pLoad = new Property<PFileInput>(PROPERTY_LOAD_USER, new PFileInput()){

			@Override
			protected boolean updating(PFileInput newvalue) {
				File dir = new File(newvalue.path).getParentFile();
				File[] fileList = dir.listFiles();
				for(File f: fileList)
				{
					if(f.getName().toLowerCase().contains(".txt"))
					{
						User u = new User(f);
						users.add(u);
						
						
						
						PList list = new PList();
						list.items = new String[users.size()];
						for (int i=0; i<users.size(); i++)
							list.items[i] = users.get(i).name;
						list.selectedIndeces = new int[]{list.items.length-1};
						getProperty(PROPERTY_USERS).setValue(list);
					}
					
				}
				createCluster();
				
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
				for(int i=0;i<users.size();i++)
				{
					User user= users.get(i);
					user.changeTime(newvalue.intValue() * 50);
				}
				
				requestRender();
				return super.updating(newvalue);
			}
		};
		addProperty(pTime);	
		
		Property<PInteger> pCellWidth = new Property<PInteger>("Cell Width", new PInteger(VALUE_CELL_WIDTH)){
			@Override
			protected boolean updating(PInteger newvalue) {
				for(int i=0;i<users.size();i++)
				{
					User user= users.get(i);
					user.setCellWidth(newvalue.intValue()); 
				}
				return super.updating(newvalue);
			}
		};
		addProperty(pCellWidth);	
		
		Property<PInteger> pTimeStep = new Property<PInteger>("Time Step", new PInteger(VALUE_TIME_STEP)){
			@Override
			protected boolean updating(PInteger newvalue) {
				for(int i=0;i<users.size();i++)
				{
					User user= users.get(i);
					user.setTimeStep(newvalue.intValue());
				}
				requestRender();
				return super.updating(newvalue);
			}
		};
		addProperty(pTimeStep);	
		
		
		
		
//		PFileInput pFile = new  PFileInput("E:/Graph/UserStudy/ScarfplotFull/Ajay.txt");
//		PFileInput pFile = new  PFileInput("E:/Graph/UserStudy/Ajay/20141121_185223_RESULT.txt");
//		pLoad.setValue(pFile);
//		pCellWidth.setValue(new PInteger(VALUE_CELL_WIDTH));
//		pTimeStep.setValue(new PInteger(VALUE_TIME_STEP));
		
	}
	private void createCluster()
	{
		ArrayList<ClusteringStringItem> items = new ArrayList<ClusteringStringItem>();
		for(User u: users)
		{
			items.add(u);
		}
		HierarchicalClustering clustering = new HierarchicalClustering(items);
		this.clusterRoot = clustering.getRoot();
	}

	private void drawAnchor(Graphics2D g, int x, int y)
	{
		Color previousColor = g.getColor();
		
		g.setColor(Color.black);
		int rad = 5;
		g.fillOval(x-rad, y-rad, 2*rad, 2*rad);
		
		g.setColor(previousColor);
	}

	private void renderCluster(Graphics2D g, Cluster cluster)
	{
		if(cluster.getChildren()== null || cluster.getChildren().isEmpty())
		{
			g.setColor(Color.black);				
			g.drawLine(0,currentUser.cellHeight/2,cluster.getParent().getHeight() * CLUSTER_SHIFT_X, currentUser.cellHeight/2);
			
			g.translate(cluster.getParent().getHeight() * CLUSTER_SHIFT_X, 0);
			for(ClusteringStringItem item: cluster.getItems())
			{
				if(item instanceof User)					
				{
					User user = (User) item;
					boolean dataHovered = false;
					if(dataObjectHovered >=0 && user.name.equals(users.get(dataObjectHovered).name))						
					{
						dataHovered = true;
					}
					this.renderUser(g, user, dataHovered);
					g.translate(0, user.cellHeight);
					
				}
			}
			g.translate(0,  -  cluster.getItems().size()* currentUser.cellHeight);
			g.translate(-cluster.getParent().getHeight() * CLUSTER_SHIFT_X, 0);
		}
		else
		{
			int totalUsers =cluster.getItems().size();
			Point anchor = getAnchorPosition(0, 0, 0, totalUsers* currentUser.cellHeight);
			
//			this.drawAnchor(g, anchor.x, anchor.y);
			int lastMaxY =0;
			for(Cluster child: cluster.getChildren())
			{
				int maxY = lastMaxY+ child.getItems().size()* currentUser.cellHeight;
				Point childAnchor = getAnchorPosition(CLUSTER_SHIFT_X, lastMaxY, CLUSTER_SHIFT_X, maxY);
				g.setColor(Color.black);				
				g.drawLine(anchor.x, anchor.y, anchor.x, childAnchor.y);
				g.drawLine(anchor.x, childAnchor.y, childAnchor.x, childAnchor.y);
				
				g.translate(CLUSTER_SHIFT_X,lastMaxY);
				this.renderCluster(g, child);
				g.translate(-CLUSTER_SHIFT_X, -lastMaxY );
				
				lastMaxY = maxY;
			}
		}
		
	}
	private Point getAnchorPosition(int minX, int minY, int maxX, int maxY)
	{
		return new Point((minX+maxX)/2,(minY+maxY)/2);
	}
	
	private void renderUser(Graphics2D g, User user, boolean dataHovered)
	{
		g.setColor(Color.cyan);
		g.fillRect(0, 0, 200,user.cellHeight-1);
		g.setColor(Color.black);
		g.setFont(g.getFont().deriveFont(10f));
		g.drawString(user.name, 0, user.cellHeight-2);
		if(user.scarfplot != null)
		{
			g.drawImage(user.scarfplot,  user.heatmapXOffset, 0,null);
			
			if (dataHovered)
				g.drawRect(0, 0, user.scarfplot.getWidth(), user.cellHeight);
	
		}
	}
	
	@Override
	public void render(Graphics2D g) {		
		if (users == null || users.isEmpty())
			return;
		

		g.setColor(Color.black);
		g.translate(0,  heatmapYOffset);
		this.renderCluster(g, clusterRoot);
//		for(int ii=0;ii< users.size();ii++)
//		{
//			User user = users.get(ii);
//			
//			boolean dataHovered = false;
//			if(dataObjectHovered == ii)
//			{
//				dataHovered = true;
//			}
//			
//			this.renderUser(g, user, dataHovered);
//			
//			g.translate(0, user.cellHeight);
//			
//		}
//		g.translate(0,  - users.size()* currentUser.cellHeight);
		g.translate(0,  -heatmapYOffset);
		
		g.translate((this.clusterRoot.getHeight()+1)* CLUSTER_SHIFT_X, 0);
		if( currentUser.scarfplot!=null)
		{
			
			int sp = currentUser.timeToPixel(currentUser.timePeriodStart);
			g.setColor(new Color(0,150,0,100));
			g.drawLine(sp,heatmapYOffset-50,sp,heatmapYOffset-50+users.size() * currentUser.cellHeight+100);
			g.drawString(currentUser.timePeriodStart+"ms", sp, heatmapYOffset-50);
			if (startPeriodHovered != null)
				g.drawLine(sp-5, startPeriodHovered.y, sp+5, startPeriodHovered.y);
			
			int ep = currentUser.timeToPixel(currentUser.timePeriodEnd);
			g.setColor(new Color(150,0,0,100));
			g.drawLine(ep,heatmapYOffset-50,ep,heatmapYOffset-50+currentUser.scarfplot.getHeight()+100);
			g.drawString(currentUser.timePeriodEnd+"ms", ep, heatmapYOffset-50);
			if (endPeriodHovered != null)
				g.drawLine(ep-5, endPeriodHovered.y, ep+5, endPeriodHovered.y);
			
			g.setColor(new Color(100,100,100,50));
			g.fillRect(currentUser.heatmapXOffset, heatmapYOffset, sp - currentUser.heatmapXOffset, currentUser.scarfplot.getHeight());
			g.fillRect(ep, heatmapYOffset, currentUser.heatmapXOffset + currentUser.scarfplot.getWidth() - ep, currentUser.scarfplot.getHeight());
			
			Rectangle[] vr = getViewEventRectangles();
			for (int i=0; i<vr.length; i++){
				if (i % 2 == 0) g.setColor(Color.gray);
				else g.setColor(Color.black);
				g.fillRect(vr[i].x, vr[i].y, vr[i].width, vr[i].height);
			}
			
			Rectangle[] hr = getHoverEventRectangles();
			for (int i=0; i<hr.length; i++){
				g.setColor(Color.pink);
				g.fillRect(hr[i].x, hr[i].y, hr[i].width, hr[i].height);
			}
			
			Rectangle[] dr = this.getMouseDragRectangles();
			for (int i=0; i<dr.length; i++){
				g.setColor(Color.blue);
				g.fillRect(dr[i].x, dr[i].y, dr[i].width, dr[i].height);
			}
			
		}
		g.translate(-(this.clusterRoot.getHeight()+1)* CLUSTER_SHIFT_X, 0);
	}

	@Override
	public Color getBackgroundColor() {	return null;}

	@Override
	public boolean mousepressed(int x, int y, int button) {
		if (currentUser == null || currentUser.scarfplot == null)
			return false;
		
		if (timeLineHovered != null)
			timeLineDragging = true;
		else if (startPeriodHovered != null)
			startPeriodDragging = true;
		else if (endPeriodHovered != null)
			endPeriodDragging = true;
		else if (new Rectangle(currentUser.heatmapXOffset, heatmapYOffset, currentUser.scarfplot.getWidth(), currentUser.scarfplot.getHeight()).contains(new Point(x,y)))
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
			currentUser.createScarfplot();
			requestRender();
		}
		startPeriodDragging = false;
		endPeriodDragging = false;
		heatmapPicked = null;
		return false;
	}
	
	@Override
	public boolean mousemoved(int x, int y) {
		
		if (currentUser == null || currentUser.scarfplot == null)
			return false;
		
		int timeLine = currentUser.timeToPixel(currentUser.currentTime); 
		Rectangle r = new Rectangle(timeLine-3,heatmapYOffset-50,currentUser.cellWidth+3,heatmapYOffset+currentUser.scarfplot.getHeight());
		
		int ps = currentUser.timeToPixel(currentUser.timePeriodStart);
		Rectangle rps = new Rectangle(ps-3,heatmapYOffset-50,currentUser.cellWidth+3,heatmapYOffset+currentUser.scarfplot.getHeight());
		int pe = currentUser.timeToPixel(currentUser.timePeriodEnd);
		Rectangle rpe = new Rectangle(pe-3,heatmapYOffset-50,currentUser.cellWidth+3,heatmapYOffset+currentUser.scarfplot.getHeight());
		
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
		
//		dataObjectHovered = -1;
//		for (int i=0; currentUser.viewedObjects != null && i<currentUser.viewedObjects.size(); i++){
//			r = new Rectangle(0, heatmapYOffset+currentUser.cellHeight*i, currentUser.heatmapXOffset+currentUser.scarfplot.getWidth(),currentUser.cellHeight);
//			if (r.contains(new Point(x,y))){
//				dataObjectHovered = i;
////				this.setToolTipText(currentUser.viewedObjects.get(i).label);
//			}
//		}		
		
		dataObjectHovered = -1;
		for (int i=0; i< users.size(); i++){
			r = new Rectangle(0, heatmapYOffset+currentUser.cellHeight*i, currentUser.heatmapXOffset+currentUser.scarfplot.getWidth(),currentUser.cellHeight);
			if (r.contains(new Point(x,y))){
				dataObjectHovered = i;
				this.setToolTipText(users.get(i).name);
			}
		}	
//		
//		Rectangle[] vr = this.getViewEventRectangles();
//		for (int i=0; i<vr.length; i++)
//		{
//			if (vr[i].contains(new Point(x,y)))
//			{
//				this.setToolTipText(currentUser.viewEvents.get(i).view);
//			}
//				
//		}
//			
//		
//		Rectangle[] hr = this.getHoverEventRectangles();
//		for (int i=0; i<hr.length; i++)
//		{
//			if (hr[i].contains(new Point(x,y)))
//			{
//				this.setToolTipText(currentUser.hoverEvents.get(i).target.label);
//			}	
//		}
		

		
		
		requestRender();
		return false;
	}

	@Override
	public boolean mousedragged(int currentx, int currenty, int oldx, int oldy) {

//		if (timeLineDragging){
////			currentUser.changeTime(currentUser.pixelToTime(currentx));
//			this.requestRender();
//			return true;
//		}
//		if (startPeriodDragging){
//			currentUser.setPeriodStart(currentUser.pixelToTime(currentx));
//			requestRender();
//			return true;
//		}
//		if (endPeriodDragging){
//			currentUser.setPeriodEnd(currentUser.pixelToTime(currentx));
//			requestRender();
//			return true;
//		}
		
//		if (heatmapPicked != null){
//			 
//			currentUser.heatmapXOffset = currentx - heatmapPicked.x;
//			requestRender();
//			return true;
//		}
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
	

	

	
	
	

	
	
	private Rectangle[] getViewEventRectangles(){
		
		Rectangle[] r = new Rectangle[currentUser.viewEvents.size()];
		
		for (int i=0; i<currentUser.viewEvents.size(); i++){
			int p1 = currentUser.timeToPixel(currentUser.viewEvents.get(i).time);
			int p2 = currentUser.timeToPixel(currentUser.events.get(currentUser.events.size()-1).time);
			if (i < currentUser.viewEvents.size()-1)
				p2 = currentUser.timeToPixel(currentUser.viewEvents.get(i+1).time);
			
			r[i] = new Rectangle(p1, heatmapYOffset-20, p2-p1, 10);
		}
		return r;
	}

	private Rectangle[] getHoverEventRectangles(){
		ArrayList<Rectangle> r = new ArrayList<Rectangle>();
		int hs = -1;
		for (int i=0; i<currentUser.hoverEvents.size(); i++){
			int p = currentUser.timeToPixel(currentUser.hoverEvents.get(i).time);
			
			if (hs >=0)
				r.add(new Rectangle(hs, heatmapYOffset-30, p - hs, 5));
	
			if (currentUser.hoverEvents.get(i).in)	hs = p;
			else hs = -1;
		}
		
		Rectangle[] ra = new Rectangle[r.size()];
		for (int i=0; i<r.size(); i++)
			ra[i] = r.get(i);
		return ra;
	}

	private Rectangle[] getMouseDragRectangles(){
		ArrayList<Rectangle> r = new ArrayList<Rectangle>();
		int ds = -1;
		for (int i=0; i<currentUser.mouseEvents.size(); i++){
			Event e = currentUser.mouseEvents.get(i);
			int p = currentUser.timeToPixel(e.time);
			if (e instanceof MouseMoveEvent && ((MouseMoveEvent)e).drag && ds < 0)
				ds = p;
			else if ((e instanceof MouseButtonEvent && ds > 0) || (e instanceof MouseMoveEvent && !((MouseMoveEvent)e).drag && ds > 0)){
				if (ds < currentUser.heatmapXOffset)
					System.out.println("");
				r.add(new Rectangle(ds, heatmapYOffset-40, p - ds, 5));
				ds = -1;
			}
		}
		
		Rectangle[] ra = new Rectangle[r.size()];
		for (int i=0; i<r.size(); i++)
			ra[i] = r.get(i);
		return ra;
	}



}
