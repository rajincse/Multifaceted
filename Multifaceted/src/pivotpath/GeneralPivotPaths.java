package pivotpath;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import multifaceted.Util;

import eyetrack.EyeTrackerItem;
import eyetrack.probability.ProbabilityManager;
import eyetrack.probability.StateAction;

import perspectives.base.Animation;
import perspectives.base.Viewer;
import perspectives.util.SplineFactory;


class GeneralCurve
{
	Point2D[] ps;
	
	
	int[] xs;
	int[] ys;
	
	public GeneralCurve(Point2D[] points)
	{
		this.ps = ps;
		
		double[] cx = new double[points.length];
		double[] cy = new double[points.length];
		
		for (int i=0; i<points.length; i++)
		{
			cx[i] = points[i].getX();
			cy[i] = points[i].getY();
		}
		
		for (int i=2; i<cx.length-2; i++)
		{
			cx[i] = 0.5*cx[i] + (1-0.5)*(0.5*cx[i-1] + 0.5*cx[i+1]);
			
			cy[i] = 0.5*cy[i] + (1-0.5)*(0.5*cy[i-1] + 0.5*cy[i+1]);
		}
		
		
		double[][] ret = SplineFactory.createCubic(cx,cy, 10);

		
		xs = new int[ret.length];
		ys = new int[ret.length];
		//xs = new int[cx.length];
		//ys = new int[cy.length];
		for (int i=0; i<ys.length; i++)
		{
			xs[i] = (int)ret[i][0];
			ys[i] = (int)ret[i][1];
			//xs[i] = (int)cx[i];
			//ys[i] = (int)cy[i];
		}
	}
	
	public void render(Graphics2D g)
	{
		g.drawPolyline(xs, ys, xs.length);
	}
	

}


class FacetDef
{
	public FacetDef(String name, Color color, Point2D[] bounds)
	{
		this.facetName = name;
		this.color = color;
		this.bounds = bounds;
	}
	protected String facetName;	
	protected Color color;	
	protected Point2D[] bounds;
}

class Facet extends FacetDef
{
	ForceContainer forceContainer;
	
	public Facet(FacetDef f)
	{
		super(f.facetName, f.color, f.bounds);
		
		forceContainer = new ForceContainer(bounds);
	}
	
	public void render(Graphics2D g)
	{
		Path2D.Double path = new Path2D.Double();
		path.moveTo(bounds[0].getX(), bounds[0].getY()); 
		for(int i=1;i<bounds.length;i++)
			path.lineTo(bounds[i].getX(), bounds[i].getY());
		path.closePath();
		
		g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
		g.fill(path);
		
		forceContainer.render(g);
	}
}


class ForceContainer
{
	private Line2D.Double[] segments;
	private Point2D.Double[] normals;
	
	public Point2D center;
	
	public ForceContainer(Point2D[] segments)
	{
		this.segments = new Line2D.Double[segments.length-1];
		this.normals = new Point2D.Double[segments.length-1];
		
		double cx = segments[0].getX();
		double cy = segments[0].getY();
		
		for (int i=1; i<segments.length; i++)
		{
			this.segments[i-1] = new Line2D.Double(segments[i-1], segments[i]);
			
			double vx = segments[i].getX() - segments[i-1].getX();
			double vy = segments[i].getY() - segments[i-1].getY();
			double d = Math.sqrt(vx*vx + vy*vy);
			vx /= d;
			vy /= d;
			
			normals[i-1] = new Point2D.Double(vy,-vx);
			
			cx += segments[i].getX();
			cy += segments[i].getY();
		}
		cx /= segments.length;
		cy /= segments.length;
		center = new Point2D.Double(cx,cy);
	}	
	
	public double[] getForce(InfoBitGroup ib)
	{
		
		Point2D.Double p1 = getForce(new Point2D.Double(ib.x, ib.y));
		Point2D.Double p2 = getForce(new Point2D.Double(ib.x+ib.getWidth(), ib.y));
		Point2D.Double p3 = getForce(new Point2D.Double(ib.x+ib.getWidth(), ib.y+ib.getHeight()));
		Point2D.Double p4 = getForce(new Point2D.Double(ib.x, ib.y+ib.getHeight()));
		
		return new double[]{(p1.x + p2.x + p3.x + p4.x)/4, (p1.y + p2.y + p3.y + p4.y)/4};		
	}
	
	public Point2D.Double getForce(Point2D.Double p)
	{
		double vx = 0;
		double vy = 0;
		for (int i=0; i<segments.length; i++)
		{
			double d = segments[i].ptSegDist(p);
			
			double vvx = p.getX() - segments[i].getX1();
			double vvy = p.getY() - segments[i].getY1();
			double dvv = Math.sqrt(vvx*vvx + vvy*vvy);
			vvx /= dvv;
			vvy /= dvv;
			
			double cosa = vvx * normals[i].x + vvy * normals[i].y;
			
			
			
			double m = 2000000000 / (d*d*d*d);
			
			if (cosa < 0) m = 100* d*d;
			
			vx += normals[i].x * m;
			vy += normals[i].y * m;	
		}
		
		vx /= segments.length;
		vy /= segments.length;
		
		return new Point2D.Double(vx,vy);
	}
	
	public void render(Graphics2D g)
	{
		for (int i=0; i<segments.length ;i++)
		{
			g.setColor(Color.black);
			g.drawLine((int)segments[i].x1, (int)segments[i].y1, (int)segments[i].x2, (int)segments[i].y2);
			
			double mx = (segments[i].x1 + segments[i].x2)/2;
			double my = (segments[i].y1 + segments[i].y2)/2;
			
			g.setColor(Color.red);
			g.drawLine((int)mx, (int)my, (int)(mx+normals[i].x*10), (int)(my+normals[i].y*10));	
		}
	}
}

abstract class InfoBit implements EyeTrackerItem
{
	String facetName;
	
	ArrayList<InfoBit> connections;
	
	InfoBitGroup group;
	
	protected double scale = 1;
	
	public String id;
	
	public String value;
	public InfoBitGroup getGroup()
	{
		return group;
	}
	
	public void setScale(double d)
	{
		scale = d;
	}
	
	public double getScale()
	{
		return scale;
	}
	protected boolean isConnectionHovered=false;
	public boolean isConnectionHovered()
	{
		return this.isConnectionHovered;
	}
	public void setConnectionHovered(boolean isConnectionHovered )
	{
		this.isConnectionHovered = isConnectionHovered;
	}
	public void setAllConnectionHover()
	{
		for(int i=0;i<this.connections.size();i++)
		{
			this.connections.get(i).setConnectionHovered(true);
		}
	}
	public abstract void render(Graphics2D g, boolean hovered);
	public abstract void renderDebug(Graphics2D g);
	public abstract double getWidth();
	public abstract double getHeight();
	public abstract Line2D[] getEdgeAnchors();
	public abstract InfoBit[] getAdditionalInfoBit();
	
	
	public InfoBit(String id)
	{
		this.id = id;
		this.value = id;
		connections = new ArrayList<InfoBit>();
	}
	
	public InfoBit(String id, String value)
	{
		this.id = id;
		this.value = value;
		connections = new ArrayList<InfoBit>();
	}
	
	public void addConnection(InfoBit ib)
	{
		connections.add(ib);
	}
	
	public boolean sameConnections(InfoBit other)
	{
		if (connections.size() != other.connections.size())
			return false;
		
		for (int i=0; i<connections.size(); i++)
		{
			if (other.connections.indexOf(connections.get(i)) >= 0)
				continue;
			else return false;
		}
		return true;
	}
}

class LabelInfoBit extends InfoBit
{
	String label;
	double w,h;
	
	protected Color color = new Color(200,200,0,200);
	protected Color hoveredColor = new Color(200,200,0,100);
	
	Font font = new Font("Helvetica",Font.PLAIN,12);
	protected double score =0;
	public LabelInfoBit(String label,String id)
	{
		super(id, label);
		this.label = label;
		w = -1;
		h = -1;
		this.color = Util.getColor(getType());
		this.hoveredColor = Util.getHoveredColor(getType());
	}
	public LabelInfoBit(String label)
	{
		super(label);
		this.label = label;
		w = -1;
		h = -1;
		this.color = Util.getColor(getType());
		this.hoveredColor = Util.getHoveredColor(getType());
	}

	@Override
	public void render(Graphics2D g, boolean hovered) {	
		if (hovered || this.isConnectionHovered)
			g.setColor(hoveredColor);
		else
			g.setColor(color);
		
		g.setFont(font.deriveFont((float)(scale*10.)));
		w = g.getFontMetrics().stringWidth(label)+g.getFontMetrics().stringWidth("ww");		
		
		g.fillRect(0, 0, (int)getWidth(), (int)getHeight());
		
		g.setColor(new Color(0,0,0,200));
		g.drawString(label, g.getFontMetrics().stringWidth("w"), (int)(getHeight()*0.8));

	}

	@Override
	public void renderDebug(Graphics2D g)
	{
		Color previousColor = g.getColor();
		
		g.setFont(g.getFont().deriveFont(8.0f));
		g.setColor(Color.black);
		
		
		String gazeScoreString =  (new DecimalFormat("##.00")).format(this.gazeScore) ;
		String probabilityString = (new DecimalFormat("##.00")).format(this.probability);
		String levitatedProbabilityScoreString  = 
				(new DecimalFormat("##.00"))
				.format(Util.getLevitatedScore(this.probability, ProbabilityManager.LEVITATION_LOWER_BOUND));
		String scoreString = (new DecimalFormat("##.00")).format(this.score);
		String s =gazeScoreString+ "; " + 
		 probabilityString+ " (" + levitatedProbabilityScoreString +  "); " + scoreString
				;
		g.drawString(s, (int)getWidth(), (int)(getHeight()));
		
		g.setColor(previousColor);
	}

	@Override
	public double getWidth() {
		
		if (w < 0)
		{
			Canvas c = new Canvas();
			font = font.deriveFont((float)(scale*10.));
			w = c.getFontMetrics(font).stringWidth(label)+c.getFontMetrics(font).stringWidth("ww");	
			return w;
		}
		return w;
	}

	@Override
	public double getHeight() {
		return 10*scale;
	}

	@Override
	public Line2D[] getEdgeAnchors() {
		return new Line2D.Double[]{new Line2D.Double(0,5,-20,5), 
				new Line2D.Double(getWidth(),5,getWidth()+20,5),
				new Line2D.Double(getWidth()/2,0,getWidth()/2,-50),
				new Line2D.Double(getWidth()/2,getHeight(),getWidth()/2,getHeight()+50)};
		
	}
	@Override
	public void setScore(double score) {
		this.score = score;
		
	}
	@Override
	public double getScore() {
		// TODO Auto-generated method stub
		return score;
	}
	@Override
	public double getGazeScore(Point2D gazePosition, double zoom) {
		
		double x = this.group.getItemX(this);
		double y = this.group.getItemY(this);
		double width = getWidth();
		double height = getHeight();
		Point2D point = Util.getTransformedPoint(-x, -y, 0, gazePosition);
		if(this.group instanceof MainInfoBitGroup)
		{
			
			double groupX = this.group.x;
			double groupY = this.group.y;
			double yDiff = y - this.group.y;
			double xDiff = x - this.group.x;
			AffineTransform at = new AffineTransform();	

			double r = MainInfoBitGroup.TILT_ANGLE;
			at.translate(-xDiff, -yDiff);
			at.rotate(-r);			
			at.translate(-groupX, -groupY);
			
			point = at.transform(gazePosition, point);
		}
		
		double gazeScore =Util.getRectangleToGazeScoreNonGaussian(0, 0, width, height,point, zoom);
		
		return gazeScore;
	}
	
	
	
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return this.id;
	}
	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return EyeTrackerItem.TYPE_INVALID;
	}
	private int getSameTypeRelation(EyeTrackerItem previousElement)
	{
		if(previousElement.getId() == this.getId())
		{
			return StateAction.SAME_TYPE_RELATION_SELF;
		}
		else if(this.group.items.contains(previousElement))
		{
			return StateAction.SAME_TYPE_RELATION_SAME_GROUP;
		}
		else
		{
			return StateAction.SAME_TYPE_RELATION_OTHER;
		}
	}
	@Override
	public ArrayList<StateAction> getActions(ArrayList<StateAction> stateActions) {
		if(stateActions != null)
		{
			for(StateAction stateAction: stateActions)
			{
				EyeTrackerItem previousItem = stateAction.getPreviousItem();
				if(previousItem.getType() == this.getType())
				{
					int sameTypeRelation = this.getSameTypeRelation(previousItem);
					int action = StateAction.getSameTypeAction(this, sameTypeRelation);
					stateAction.setAction(action);
				}
				else
				{
					int action = StateAction.getAction(this, isConnected(previousItem), previousItem.isHovered());
					stateAction.setAction(action);
				}
				
			}
			return stateActions;
		}
		else
		{
			ArrayList<StateAction> emptyStateActions = new ArrayList<StateAction>();
			StateAction stateAction = new StateAction(null, 1);
			int action = StateAction.getAction(this, false, false);
			stateAction.setAction(action);
			emptyStateActions.add(stateAction);
			return emptyStateActions;
		}
	}
	@Override
	public boolean isConnected(EyeTrackerItem element)
	{
		boolean connected = false;
		if(element != null)
		{
			for(InfoBit infoBit : this.connections)
			{
				if(infoBit.id.equals(element.getId()))
				{
					connected = true;
					break;
				}
			}
		}
		return connected;
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj instanceof EyeTrackerItem)
		{
			EyeTrackerItem item = (EyeTrackerItem) obj;
			if(item.getId().equalsIgnoreCase(this.getId()))
			{
				return true;
			}
		}
		return super.equals(obj);
	}
	private double gazeScore;
	private double probability;
	@Override
	public void setGazeScore(double gazeScore) {
		// TODO Auto-generated method stub
		this.gazeScore = gazeScore;
	}
	@Override
	public void setProbability(double probability) {
		// TODO Auto-generated method stub
		this.probability = probability;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{"+this.getType()+this.label+","+Util.formatNum(this.score)+"}";
	}
	@Override
	public double getStoredGazeScore() {
		// TODO Auto-generated method stub
		return this.gazeScore;
	}
	@Override
	public double getProbabilityScore() {
		// TODO Auto-generated method stub
		return this.probability;
	}
	private double nextProbability;
	@Override
	public void setNextProbability(double probability) {
		// TODO Auto-generated method stub
		this.nextProbability = probability;
	}
	@Override
	public InfoBit[] getAdditionalInfoBit() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isHovered() {
		// TODO Auto-generated method stub
		int hovered = this.group.hovered;
		if(hovered > 0 && hovered < this.group.items.size())
		{
			if(this == this.group.items.get(hovered))
			{
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean isSameGroup(EyeTrackerItem element) {
		// TODO Auto-generated method stub
		return this.group.items.contains(element);
	}
	@Override
	public boolean isIndirectlyHovered() {
		// TODO Auto-generated method stub
		return this.isConnectionHovered();
	}
}

class InfoBitGroup
{
	double x;
	double y;
	private double width;
	private double height;
	
	int hovered = -1;
	
	protected ArrayList<InfoBit> items;
	
	
	public InfoBitGroup()
	{
		items = new ArrayList<InfoBit>();		
	}
	public void addItem(InfoBit d)
	{
		if (items.indexOf(d) < 0)
			items.add(d);
	}
	
	public double getWidth()
	{
		double mw = 0;
		for (int i=0; i<items.size(); i++)
			if (mw < items.get(i).getWidth())
				mw = items.get(i).getWidth();
		return mw;
	}
	
	public double getHeight()
	{
		double h = 0;
		for (int i=0; i<items.size(); i++)
			h += items.get(i).getHeight();				
		return h;
	}
	
	public void render(Graphics2D g)
	{
		for (int i=0; i<items.size(); i++)
		{
			int x = (int)getItemX(i);
			int y = (int)getItemY(i);
			g.translate(x,y);
			InfoBit bit =items.get(i);			
			bit.render(g, i == hovered);
			g.translate(-x,-y);
		}
	}
	public void renderDebug(Graphics2D g)
	{
		for (int i=0; i<items.size(); i++)
		{
			int x = (int)getItemX(i);
			int y = (int)getItemY(i);
			g.translate(x,y);
			InfoBit bit =items.get(i);			
			bit.renderDebug(g);
			g.translate(-x,-y);
		}
	}
	public double getItemX(int i)
	{
		return x;
	}
	
	public double getItemY(int index)
	{
		double ry = 0;
		for (int i=0; i<index; i++)
			ry += items.get(i).getHeight();
		return y+ry;
	}
	
	public Line2D[] getItemEdgeAnchor(int i)
	{
		Line2D[] anchors = items.get(i).getEdgeAnchors();
		Line2D[] ret = new Line2D[anchors.length];
		for (int j=0; j<ret.length; j++)
			ret[j] = new Line2D.Double(x+anchors[j].getX1(), y+i*10+anchors[j].getY1(), x+anchors[j].getX2(), y+i*10+anchors[j].getY2());
		return ret;
	}
	
	
	public ArrayList<InfoBit> getItems()
	{
		return this.items;
	}
	public double getItemX(InfoBit item)
	{
		int i = items.indexOf(item);
		return getItemX(i);
	}
	
	public double getItemY(InfoBit item)
	{
		int i = items.indexOf(item);
		return getItemY(i);
	}
	
	public Line2D[] getItemEdgeAnchor(InfoBit item)
	{
		int i = items.indexOf(item);
		return getItemEdgeAnchor(i);
	}	
	
	public int mouseHovered(int mx, int my)
	{
		hovered = -1;
		for (int i=0; i<items.size(); i++)
		{
			double x = (int)getItemX(i);
			double y = (int)getItemY(i);
			double w =  items.get(i).getWidth();
			double h =  items.get(i).getHeight();
			
			if (mx > x && mx < x + w && my > y && my < y+h)
				hovered = i;
		}
		
		return hovered;
	}
	public void setAllConnectionUnHovered()
	{
		for (int i=0; i<items.size(); i++)
		{
			InfoBit bit = items.get(i);
			bit.setConnectionHovered(false);
		}
	}

}

class MainInfoBitGroup extends InfoBitGroup
{
	public static final double TILT_ANGLE = Math.PI/4;
	public Line2D[] getItemEdgeAnchor(int i)
	{
		Line2D[] anchors = items.get(i).getEdgeAnchors();
		Line2D[] ret = new Line2D[anchors.length];
		
		AffineTransform a = new AffineTransform();
		int x = (int)getItemX(i);
		int y = (int)getItemY(i);
		double w = items.get(i).getWidth();
		double h = items.get(i).getHeight();
		double r = TILT_ANGLE;		
		
		a.translate(x, y);			
		a.rotate(r);		

		for (int j=0; j<ret.length; j++)
		{
			Point2D p1 = a.transform(anchors[j].getP1(), null);
			Point2D p2 = a.transform(anchors[j].getP2(), null);
			ret[j] = new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY());
		}
		return ret;
	}
	@Override
	public double getItemX(int i) {
		// TODO Auto-generated method stub
		if(i > 0 && i< this.items.size())
		{
			InfoBit firstBit = items.get(0);
			InfoBit currentBit = items.get(i);
			return this.x + firstBit.getWidth()-currentBit.getWidth();
		}
		else
		{
			return super.getItemX(i);
		}
		
	}
	public void render(Graphics2D g)
	{
		double r = TILT_ANGLE;
		g.translate(x, y);			
		g.rotate(r);
		for (int i=0; i<items.size(); i++)
		{
			double yDiff = getItemY(i) - y;
			double xDiff = getItemX(i) -x;	
			InfoBit bit =items.get(i); 	

			g.translate(xDiff, yDiff);

			bit.render(g, i == hovered);		
			
			g.translate(-xDiff, -yDiff);
				
		}
		g.rotate(-r);		
		g.translate(-x, -y);
	}
	
	@Override
	public void renderDebug(Graphics2D g) {
		// TODO Auto-generated method stub
		double r = TILT_ANGLE;
		g.translate(x, y);			
		g.rotate(r);
		for (int i=0; i<items.size(); i++)
		{
			double yDiff = getItemY(i) - y;
			double xDiff = getItemX(i) -x;	
			InfoBit bit =items.get(i); 	

			g.translate(xDiff, yDiff);

			items.get(i).renderDebug(g);		
			
			g.translate(-xDiff, -yDiff);
				
		}
		g.rotate(-r);		
		g.translate(-x, -y);
	}
	public int mouseHovered(int mx, int my)
	{		
		hovered = -1;
		for (int i=0; i<items.size(); i++)
		{			
			
			double yDiff = getItemY(i) - y;
			double xDiff = getItemX(i) -x;	
			double w =  items.get(i).getWidth();
			double h =  items.get(i).getHeight();			
			
			AffineTransform at = new AffineTransform();	

			double r = TILT_ANGLE;
			
			at.translate(-xDiff,-yDiff);
			at.rotate(-r);	
			at.translate(-this.x, -this.y);
			
			Point2D p = new Point2D.Double();
			p = at.transform(new Point2D.Double(mx,my), p);
			
			if (p.getX() > 0 && p.getX() < w && p.getY() > 0 && p.getY() < h)
				hovered = i;
		}
		
		return hovered;
	}
}





public class GeneralPivotPaths {
	
	ArrayList<InfoBitGroup> groups;
	ArrayList<InfoBitGroup> dataGroups;
	
	String[][] facetInfo;
	
	String main;
	
	String[][] edges;
		
	double mf;
	
	Rectangle2D mainBounds;
	
	ArrayList<Point2D[]> slotBounds;
	ArrayList<Color> slotColors;
	HashMap<String, Integer> facetMapping;
	int[][] slotContents;
	ArrayList<ForceContainer> forceContainers;
	
	HashMap<String,Integer> facetMap = new HashMap<String,Integer>();
	ArrayList< ArrayList<String> > facets = new ArrayList< ArrayList<String>>();
	ArrayList< ArrayList<InfoBit> > infoBits = new ArrayList< ArrayList< InfoBit>>();
	
	ArrayList<String> data = new ArrayList<String>();
	
	Viewer viewer;
	PivotPathViewerInterface pivotPathViewer= null;
	
	HashMap<String, InfoBitGroup> savedPositions;
	

	
	public GeneralPivotPaths(String main, Rectangle2D.Double mainBounds)
	{		
		slotBounds = new ArrayList<Point2D[]>();
		slotColors =  new ArrayList<Color>();
		forceContainers = new ArrayList<ForceContainer>();
		facetMapping = new HashMap<String,Integer>();
		
		this.main = main;
		this.mainBounds = mainBounds;
		
		facetMap = new HashMap<String,Integer>();
		facets = new ArrayList< ArrayList<String>>();
		infoBits = new ArrayList< ArrayList< InfoBit>>();
		data = new ArrayList<String>();
	}
	
	
	public int addSlot(Point2D[] bounds, Color c)
	{
		slotBounds.add(bounds);
		slotColors.add(c);
		forceContainers.add(new ForceContainer(bounds));
		
		return slotBounds.size()-1;
	}
	
	public void setFacetToSlotMapping(String[] facets, int[] slots)
	{
		facetMapping = new HashMap<String,Integer>();
		for (int i=0; i<facets.length; i++)
			facetMapping.put(facets[i], new Integer(slots[i]));
	}
	
	
	public void setData(String[] data, String[][][] attributes)
	{
		HashSet<String> newdata = new HashSet<String>();
		for (int i=0; i<data.length; i++)
		{
			newdata.add("\t"+data[i]);
			for (int j=0; j<attributes[i].length; j++)
				newdata.add(attributes[i][j][0] + "\t" + attributes[i][j][1]+"\t"+ attributes[i][j][2]);
		}
		
		
		
		//first save positions of existing data
		savedPositions = new HashMap<String, InfoBitGroup>();
		Object[] prevFacets = facetMap.keySet().toArray();
		for (int i=0; i<prevFacets.length; i++)
		{
			int index = facetMap.get(prevFacets[i]);
			ArrayList<String> elem1 = facets.get(index);
			ArrayList< InfoBit> elem2 = infoBits.get(index);
			for (int j=0; j<elem1.size(); j++)
			{
				if (!newdata.contains(prevFacets[i]+"\t"+elem1.get(j)))
					continue;
				
				Point2D pos = new Point2D.Double(elem2.get(j).getGroup().getItemX(elem2.get(j)), elem2.get(j).getGroup().getItemY(elem2.get(j)));
				
				//each element now becomes its own group so they can be moved independently
				InfoBitGroup g = new InfoBitGroup();
				g.addItem(elem2.get(j));
				elem2.get(j).group = g;
				g.x = pos.getX();
				g.y = pos.getY();
				savedPositions.put(prevFacets[i]+"\t"+elem1.get(j), g);
			}
		}
		for (int i=0; i<this.data.size(); i++)
		{
			if (!newdata.contains("\t"+this.data.get(i)))
				continue;
			savedPositions.put("\t"+this.data.get(i), dataGroups.get(i));
		}		
		
		
		dataGroups = new ArrayList<InfoBitGroup>();
		this.data = new ArrayList<String>();
		
		facetMap = new HashMap<String,Integer>();
		facets = new ArrayList< ArrayList<String>>();
		infoBits = new ArrayList< ArrayList< InfoBit>>();
		
		for (int i=0; i<data.length; i++)
		{
			InfoBit d = createDataInfoBit(data[i]);			
			InfoBitGroup g = createDataGroup();
			
			g.addItem(d);
			d.group = g;
			
			//Additional InfoBit
			InfoBit[] additionalInfoBits = d.getAdditionalInfoBit();
			if(additionalInfoBits != null && additionalInfoBits.length > 0 )
			{
				for(InfoBit additionalInfoBit: additionalInfoBits)
				{
					g.addItem(additionalInfoBit);
					
					additionalInfoBit.group = g;
				}
			}
			
			g.x = mainBounds.getMinX() + i*mainBounds.getWidth()/data.length;
			g.y = mainBounds.getCenterY();			
			dataGroups.add(g);
			this.data.add(data[i]);
			
			
			for (int j=0; j<attributes[i].length; j++)
			{
				String facetName = attributes[i][j][0];
				String value = attributes[i][j][1];
				String id = attributes[i][j][2];
				InfoBit infoBit = null;
				
				if (!facetMap.containsKey(facetName))
				{
					facetMap.put(facetName, facets.size());
					facets.add(new ArrayList<String>());
					infoBits.add(new ArrayList<InfoBit>());
				}
				int f = facetMap.get(facetName);
				ArrayList<String> facet = facets.get(f);

				for (int k=0; k<facet.size(); k++)
				{
					if (equalsValue(facet.get(k), value))
					{
						infoBit = infoBits.get(f).get(k);
						break;
					}
				}
				
				if (infoBit == null)
				{
					infoBit = createInfoBit(facetName, value, id);
					facet.add(value);
					infoBits.get(f).add(infoBit);
					
				}
				
				infoBit.addConnection(d);
				d.addConnection(infoBit);
				infoBit.setScale(Math.log(infoBit.connections.size()+1));
			}
			
			
		}
		
		mf = 5;		
		group(infoBits);
		
		for (int i=0; i<1000; i++)
			computeIteration();
		
		//animate from previous positions to current positions
		for (int i=0; i<facets.size(); i++)
		{
			ArrayList<String> elem1 = facets.get(i);
			ArrayList< InfoBit> elem2 = infoBits.get(i);
			for (int j=0; j<elem1.size(); j++)
			{
				final InfoBitGroup old = savedPositions.get(elem2.get(j).facetName + "\t" + elem1.get(j));
				if (old == null) continue;
				
				final HashMap<String, InfoBitGroup> savedPosFinal = savedPositions;
				final String keyFinal = elem2.get(j).facetName + "\t" + elem1.get(j);
				
				Point2D newPos = new Point2D.Double(elem2.get(j).getGroup().getItemX(elem2.get(j)), elem2.get(j).getGroup().getItemY(elem2.get(j)));
				double newScale = elem2.get(j).scale;
				
                viewer.createAnimation(new Animation.PositionAnimation(new Point2D.Double(old.x,old.y), newPos, 2000) {
                    public void step(Point2D p) {
                  
                    	old.x = p.getX();
                    	old.y = p.getY();
                           
                        viewer.requestRender();
                    }
                    
                    public void animationComplete()
                    {
                    	savedPosFinal.remove(keyFinal);
                    	viewer.requestRender();
                    }
                });
                
                viewer.createAnimation(new Animation.DoubleAnimation(old.items.get(0).scale, newScale, 2000) {
                    public void step(double s) {                  
                    	
                    	old.items.get(0).setScale(s);         	
                        viewer.requestRender();
                    }
                 
                });
			}
		}
		for (int i=0; i<data.length; i++)
		{
			final InfoBitGroup old = savedPositions.get("\t" + data[i]);
			if (old == null) continue;
			
			final HashMap<String, InfoBitGroup> savedPosFinal = savedPositions;
			final String keyFinal = "\t" + data[i];
			
			Point2D newPos = new Point2D.Double(dataGroups.get(i).x, dataGroups.get(i).y);
			
            viewer.createAnimation(new Animation.PositionAnimation(new Point2D.Double(old.x,old.y), newPos, 2000) {
                public void step(Point2D p) {
              
                	old.x = p.getX();
                	old.y = p.getY();
                       
                    viewer.requestRender();
                }
                
                public void animationComplete()
                {
                	savedPosFinal.remove(keyFinal);
                	viewer.requestRender();
                }
            });
		}
		
		
	}
	
	protected InfoBitGroup createDataGroup()
	{
		MainInfoBitGroup mg = new MainInfoBitGroup();
		return mg;
	}
	
	protected InfoBit createDataInfoBit(String value)
	{
		LabelInfoBit b = new LabelInfoBit(value);
		return b;		
	}
	
	protected InfoBitGroup createGroup()
	{
		return new InfoBitGroup();
	}
	
	protected InfoBit createInfoBit(String facetName, String value)
	{
		LabelInfoBit b = new LabelInfoBit(value);
		b.facetName = facetName;
		return b;
	}
	protected InfoBit createInfoBit(String facetName, String value, String id)
	{
		LabelInfoBit b = new LabelInfoBit(value, id);
		b.facetName = facetName;
		return b;
	}
	protected boolean equalsValue(String v1, String v2)
	{
		return v1.equals(v2);
	}
	
	

	
	public void computeIteration()
	{
		double[][] f = new double[groups.size()][];
		for (int i=0; i<f.length; i++)
		{
			f[i] = new double[2];
			f[i][0] = 0;
			f[i][1] = 0;
		}
		
		
		//repulsive
		for (int k=0; k<slotContents.length; k++)
		{
			for (int i=0; i<slotContents[k].length-1; i++)
			{
				InfoBitGroup g1 = groups.get(slotContents[k][i]);			
				
				for (int j=i+1; j<slotContents[k].length; j++)
				{
					InfoBitGroup g2 = groups.get(slotContents[k][j]);						
				
					double[] rf = rectForce(g1,g2);	
					
					double ff = .5;
					
					f[slotContents[k][i]][0] += rf[0]*ff;
					f[slotContents[k][i]][1] += rf[1]*ff;
					f[slotContents[k][j]][0] -= rf[0]*ff;
					f[slotContents[k][j]][1] -= rf[1]*ff;
					
				}
			}
		}
		
		//force container
		for (int k=0; k<slotContents.length; k++)
		{
			for (int i=0; i<slotContents[k].length; i++)
			{
				InfoBitGroup g = groups.get(slotContents[k][i]);			
				
				double[] rf = forceContainers.get(k).getForce(g);					
				f[slotContents[k][i]][0] += rf[0]*0.01;
				f[slotContents[k][i]][1] += rf[1]*0.01;
			
			}
		}
				
		//edges
		for (int k=0; k<groups.size(); k++)
		{
			for (int i=0; i<groups.get(k).items.size(); i++)
			{
				InfoBit data1 = groups.get(k).items.get(i);				
				
				double x1 = groups.get(k).getItemX(i);
				double y1 = groups.get(k).getItemY(i);
				for (int j=0; j<data1.connections.size(); j++)
				{
					InfoBit data2 = data1.connections.get(j);					
					double x2 = data2.getGroup().getItemX(data2);
					double y2 = data2.getGroup().getItemY(data2);
					
					double dx = x2 - x1;
					double dy = y2 - y1;
					
					double dl = Math.sqrt(dx*dx + dy*dy);
					dx /= dl; dy /= dl;
					
					f[k][0] += 0.01*dx * dl / (Math.pow(data1.connections.size(),3));
					f[k][1] += 0.01*dy * dl / (Math.pow(data1.connections.size(),3));				
				}
			}
		}
		
		//bubbling
		for (int k=0; k<slotContents.length; k++)
		{
			double vx = forceContainers.get(k).center.getX() - mainBounds.getCenterX();
			double vy = forceContainers.get(k).center.getY() - mainBounds.getCenterY();			
			double vl = Math.sqrt(vx*vx + vy*vy);
			vx/=vl; vy/=vl;		
			
			for (int l=0; l<slotContents[k].length; l++)
			{					
				double mx = 0;
				for (int j=0; j<groups.get(slotContents[k][l]).items.size(); j++)
					if (groups.get(slotContents[k][l]).items.get(j).connections.size() > mx)
						mx = groups.get(slotContents[k][l]).items.get(j).connections.size();
				
				mx = Math.log(mx+1);
				
				f[slotContents[k][l]][0] += 0.00005*mx*vx;
				f[slotContents[k][l]][1] += 0.00005*mx*vy;
			}
		}
		

		
		//apply
		for (int i=0; i<groups.size(); i++)
		{
			if (groups.get(i).items.get(0).facetName.equals(main))
				continue;
			
			double mag = Math.sqrt(f[i][0]*f[i][0] + f[i][1]*f[i][1]);
			if (mag > mf)
			{
				f[i][0] /= mag;
				f[i][1] /= mag;
				f[i][0] *= mf;
				f[i][1] *= mf;
			}
			
			if (Double.isInfinite(mag) || Double.isNaN(mag))
				continue;
			
			groups.get(i).x += f[i][0];
			groups.get(i).y += f[i][1];
		}
		
		mf *= 0.999;
		

		splineShapes = null;
	}
	

	//GeneralCurve[] splineShapes = null;
	CubicCurve2D[] splineShapes = null;
	Color[] splineColors = null;
	InfoBit[][] splineTargets = null;
	private void prepareSplines()
	{
		int howmany = 0;
		for (int k=0; k<groups.size(); k++)
			for (int i=0; i<groups.get(k).items.size(); i++)
			{
				InfoBit d = groups.get(k).items.get(i);
				howmany += d.connections.size();
			}
		
		//splineShapes = new GeneralCurve[howmany];
		splineShapes = new CubicCurve2D[howmany];
		splineColors = new Color[howmany];
		splineTargets = new InfoBit[howmany][];
		
		howmany = 0;
		for (int k=0; k<groups.size(); k++)
		{
			for (int i=0; i<groups.get(k).items.size(); i++)
			{
				InfoBit d = groups.get(k).items.get(i);
				for (int j=0; j<d.connections.size(); j++)
				{
					
					Line2D[] anchors = getAnchors(d, d.connections.get(j));
					Line2D l1 = anchors[0];
					Line2D l2 = anchors[1];
															
					splineTargets[howmany] = new InfoBit[]{d, d.connections.get(j)};				
					splineColors[howmany] = new Color(100,100,100,Math.min(255,(int)(10*d.connections.size())));					
					splineShapes[howmany] = new CubicCurve2D.Double(l1.getX1(), l1.getY1(), l1.getX2(), l1.getY2(), l2.getX2(), l2.getY2(), l2.getX1(), l2.getY1());
					
					howmany++;
					
					
					
					continue;
				
				}
			}
		}
	}
	
	public Line2D[] getAnchors(InfoBit b1, InfoBit b2)
	{
		Line2D[] a1 = b1.getGroup().getItemEdgeAnchor(b1);
		Line2D[] a2 = b2.getGroup().getItemEdgeAnchor(b2);
		
		double mindd = 999999999;
		Line2D l1 = null,l2 = null;
		for (int l=0; l<a1.length; l++)
			for (int n=0; n<a2.length; n++)
			{
				double dd= a1[l].getP2().distance(a2[n].getP2());
				if (dd < mindd)
				{
					mindd = dd;
					l1 = a1[l];
					l2 = a2[n];
				}
			}		
		return new Line2D[]{l1,l2};
	}
	
	public void render(Graphics2D g)
	{
		if (savedPositions != null && savedPositions.size() >0)
		{
			Object[] keys = savedPositions.keySet().toArray();
			for (int i=0; i<keys.length; i++)
				savedPositions.get(keys[i]).render(g);
			
			return;
		}
		
		g.setColor(new Color(100,100,100,100));
		
		if (splineShapes == null)
			prepareSplines();
		
		g.setStroke(new BasicStroke(1));
		for (int i=0; i<splineShapes.length; i++)
		{
			try
			{
				if(splineTargets[i] == null || splineShapes[i]==null)
				{
					continue;
				}
				InfoBitGroup g1 = splineTargets[i][0].getGroup();
				InfoBitGroup g2 = splineTargets[i][1].getGroup();
				if ( (g1.hovered >=0 && g1.items.get(g1.hovered) == splineTargets[i][0]) ||
						(g2.hovered >=0 && g2.items.get(g2.hovered) == splineTargets[i][1]))
					g.setColor(Color.red);
				else
					g.setColor(splineColors[i]);
				g.draw(splineShapes[i]);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			
			//splineShapes[i].render(g);
		}
		
	
		for (int i=0; i<groups.size(); i++)
			groups.get(i).render(g);
		
		for (int i=0; i<dataGroups.size(); i++)
			dataGroups.get(i).render(g);
	}
	public void renderDebug(Graphics2D g)
	{
		for (int i=0; i<forceContainers.size(); i++)
			forceContainers.get(i).render(g);
		
		for (int i=0; i<groups.size(); i++)
			groups.get(i).renderDebug(g);
		
		for (int i=0; i<dataGroups.size(); i++)
			dataGroups.get(i).renderDebug(g);
	}
	
	
	private double[] rectForce(InfoBitGroup ib1, InfoBitGroup ib2)
	{
	
		Rectangle2D.Double r1 = new Rectangle2D.Double(ib1.x, ib1.y, ib1.getWidth(), ib1.getHeight());
		Rectangle2D.Double r2 = new Rectangle2D.Double(ib2.x, ib2.y, ib2.getWidth(), ib2.getHeight());
		
		double f[] = new double[2];
		f[0] = 0; f[1] = 0;
		
		double x1 = r1.getCenterX();
		double y1 = r1.getCenterY();
		double x2 = r2.getCenterX();
		double y2 = r2.getCenterY();
			
		Line2D.Double l1 = new Line2D.Double(r1.getCenterX(), r1.getCenterY(), r2.getCenterX(), r2.getCenterY());
		Line2D.Double l2 = new Line2D.Double(r2.getCenterX(), r2.getCenterY(), r1.getCenterX(), r1.getCenterY());
		
		Point2D p1 = lineRectInteresect(r1, l1);
		Point2D p2 = lineRectInteresect(r2, l2);

		double dx = x1-x2;
		double dy = y1-y2;
		double dl = Math.sqrt(dx*dx + dy*dy);
		while (dl == 0)
		{
			dx = Math.random();
			dy = Math.random();
			dl =  Math.sqrt(dx*dx + dy*dy);
		}
		
		
		if (p1 != null && p2 != null)
			dl = dl - p1.distance(new Point2D.Double(r1.getCenterX(), r1.getCenterY())) - p2.distance(new Point2D.Double(r2.getCenterX(), r2.getCenterY()));
		if (dl < 0)
			dl = 1;			
		
		double fm = 1000/(dl*dl);
		
		f[0] = fm * dx / dl;
		f[1] = fm * dy / dl;	
			
		return f;
	}
	
	private Point2D lineRectInteresect(Rectangle2D.Double r, Line2D.Double l)
	{
		double m = (l.y2 - l.y1)/(l.x2 - l.x1);
		
		double x1 = l.x1 + r.width/2;
		double y1 = l.y1 + m * r.width/2;
		
		double x2 = l.x1 - r.width/2;
		double y2 = l.y1 - m * r.width/2;
		
		double x3 = l.x1 + (1./m) * r.height/2;
		double y3 = l.y1 + r.height/2;
		
		double x4 = l.x1 - (1./m) * r.height/2;
		double y4 = l.y1 - r.height/2;
		
		double x,y;
		
		if (y1 > r.getMinY() && y1 < r.getMaxY() && Math.signum(x1-l.x1) == Math.signum(l.x2-l.x1) && Math.signum(y1-l.y1) == Math.signum(l.y2-l.y1))
			return new Point2D.Double(x1,y1);
		
		if (y2 > r.getMinY() && y2 < r.getMaxY() && Math.signum(x2-l.x1) == Math.signum(l.x2-l.x1) && Math.signum(y2-l.y1) == Math.signum(l.y2-l.y1))
			return new Point2D.Double(x2,y2);
		
		if (x3 > r.getMinX() && x3 < r.getMaxX() && Math.signum(x3-l.x1) == Math.signum(l.x2-l.x1) && Math.signum(y3-l.y1) == Math.signum(l.y2-l.y1))
			return new Point2D.Double(x3,y3);
		
		if (x4 > r.getMinX() && x4 < r.getMaxX() && Math.signum(x4-l.x1) == Math.signum(l.x2-l.x1) && Math.signum(y4-l.y1) == Math.signum(l.y2-l.y1))
			return new Point2D.Double(x4,y4);
		
		return null;
	}
	
	
	private void group(ArrayList< ArrayList<InfoBit> > data)
	{
		groups = new ArrayList<InfoBitGroup>();
		
		HashMap< Integer, ArrayList<Integer> > fc = new HashMap< Integer, ArrayList<Integer> >();
		for (int i=0; i<slotBounds.size(); i++)
			fc.put(new Integer(i), new ArrayList<Integer>());
		
		for (int k=0; k<data.size(); k++)
		{
			for (int i=0; i<data.get(k).size()-1; i++)
				for (int j=0; j<data.get(k).size(); j++)
				{
					InfoBit d1 = data.get(k).get(i);
					InfoBit d2 = data.get(k).get(j);
					
					if (d1.sameConnections(d2))
					{
						InfoBitGroup g;
						if (d1.getGroup() != null)
						{
							g = d1.getGroup();
							g.addItem(d2);
							d2.group = g;
						}
						else if (d2.getGroup() != null)
						{
							g = d2.getGroup();
							g.addItem(d1);
							d1.group = g;
						}
						else
						{
							g = this.createGroup();
							g.addItem(d1);
							g.addItem(d2);
							d1.group = g;
							d2.group = g;
							
							groups.add(g);
							
							int slotIndex = this.facetMapping.get(d1.facetName);						
							fc.get(slotIndex).add(groups.size()-1);
						}
					}	
				}
		}
		

		
		for (int k=0; k<data.size(); k++)
		{	
			for (int i=0; i<data.get(k).size(); i++)
				if (data.get(k).get(i).getGroup() == null)
				{
					InfoBitGroup g = createGroup();
					g.addItem(data.get(k).get(i));				
					data.get(k).get(i).group = g;				
					groups.add(g);				
					
					int slotIndex = this.facetMapping.get(data.get(k).get(i).facetName);						
					fc.get(slotIndex).add(groups.size()-1);
				}
		}
		
		slotContents = new int[slotBounds.size()][];
		for (int i=0; i<slotBounds.size(); i++)
		{
			ArrayList<Integer> content = fc.get(i);
			slotContents[i] = new int[content.size()];
			for (int j=0; j<content.size(); j++)
			{
				slotContents[i][j] = content.get(j);
				
				InfoBitGroup g = groups.get(slotContents[i][j]);
				double x = 0;
				double y = 0;
				int cnt = 0;
				for (int k=0; k<g.items.size(); k++)
				{
					InfoBit d = g.items.get(k);
					for (int l=0; l<d.connections.size(); l++)
					{
						x += d.connections.get(l).getGroup().x;
						cnt++;
					}
					
					if (d.connections.size() > y) y = d.connections.size();
				}
				x /= cnt;
				if (cnt == 0) x = 0;
				if (y == 0) y = 10;
				
				double vx = forceContainers.get(i).center.getX() - mainBounds.getCenterY();
				double vy = forceContainers.get(i).center.getY() - mainBounds.getCenterY();
				double vl = Math.sqrt(vx*vx + vy*vy);
				
				x = forceContainers.get(i).center.getX() + x/10.;
				y = forceContainers.get(i).center.getY() + vy/vl*(y*10) ;
	
				
				g.x = x;
				g.y= y;
			}
		}

	}
	
	String hovered = null;
	public void mouseMoved(int x, int y)
	{
		hovered=null;
		int hoverType =-1;
		int hoverGroup=-1;
		int hoverIndex =-1;
		
		// Make all un hovered
		for (int i=0; i<groups.size(); i++)
		{
			groups.get(i).setAllConnectionUnHovered();
		}
		for (int i=0; i<dataGroups.size(); i++)
		{
			dataGroups.get(i).setAllConnectionUnHovered();
		}
		for (int i=0; i<groups.size(); i++)
		{
			
			int index = groups.get(i).mouseHovered(x, y);
			if (index >= 0)
			{
				hovered = groups.get(i).items.get(index).facetName +"\t" + groups.get(i).items.get(index).value+ "\t" + groups.get(i).items.get(index).id;
				hoverType = PivotPathViewerInterface.GROUP_ATTRIBUTE;
				hoverGroup =i;
				hoverIndex = index;
				groups.get(i).getItems().get(index).setAllConnectionHover();
			}
				
			
		}
		for (int i=0; i<dataGroups.size(); i++)
		{
			
			int index = dataGroups.get(i).mouseHovered(x, y);
			if (index >=0)
			{
				hovered = "\t" + dataGroups.get(i).items.get(index).value+ "\t" + dataGroups.get(i).items.get(index).id;
				hoverType = PivotPathViewerInterface.GROUP_DATA;
				hoverGroup =i;
				hoverIndex = index;
				dataGroups.get(i).getItems().get(index).setAllConnectionHover();
			}
		}
		
		
		viewer.requestRender();		
		
		if(pivotPathViewer != null)
		{
			pivotPathViewer.hoverDetected(hoverType,hoverGroup, hoverIndex);
		}
	}
	
	public void mouseClicked(int x, int y)
	{
		if (hovered != null)
			transition(hovered.split("\t")[0], hovered.split("\t")[1],hovered.split("\t")[2]);
	}
	
	public void transition(String facet, String value, String id)
	{
		
	}
	
	
	
}
