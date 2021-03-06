package multifaceted.layout;



import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import perspectives.base.ObjectInteraction;
import perspectives.util.Label;

public class PivotPathLayout {
	public static final int COEFF_COMPULSIVE_FORCE_WEAK=50;
	public static final int COEFF_COMPULSIVE_FORCE_STRONG=150;
	public static final int COEFF_SPRING_LENGTH=200;
	public static final int COEFF_BOUNDARY_FORCE=100;
	
	public static final int MAX_MIDDLE_ITEM =15;
	public static final int STEP_MIDDLE_ITEM =50;
	public static final int TOP_Y =0;
	public static final int BOTTOM_Y=700;
	public static final int MIDDLE_Y=450;
	
	public static final int LAYER_TOP=0;
	public static final int LAYER_MIDDLE=1;
	public static final int LAYER_BOTTOM=2;
	public static final int LAYER_RIGHT=3;
	
	public static final int BOUNDARY_RIGHT = 850;
	
	private LayoutViewerInterface viewer;
	
	protected ObjectInteraction objectInteraction = null;
	
	ArrayList<PivotElement> elem = new ArrayList<PivotElement>();
	ArrayList<String> elementId = new ArrayList<String>();

	ArrayList<PivotEdge> edges = new ArrayList<PivotEdge>();
	
	int cnt = 0;
	
	int middles = 0;
	
	public PivotPathLayout(LayoutViewerInterface viewer)
	{
		this.viewer = viewer;
		initObjectInteraction();
	}
	protected void initObjectInteraction()
	{
		objectInteraction = new ObjectInteraction()
		{

			@Override
			protected void mouseIn(int object) {
				if(!elem.isEmpty())
				{
					if (objectInteraction.getItem(object).selected)
						elem.get(object).getLabel().setColor(Color.red);
					else
					{
						elem.get(object).getLabel().setColor(Color.yellow);
						viewer.callSetToolTipText(elem.get(object).getLabel().getText());
					}
						
					
					viewer.callRequestRender();
				}
				
			}

			@Override
			protected void mouseOut(int object) {
				if(!elem.isEmpty())
				{
					if (objectInteraction.getItem(object).selected)
						elem.get(object).getLabel().setColor(Color.pink);
					else
						elem.get(object).getLabel().setColor(Color.LIGHT_GRAY);
					viewer.callSetToolTipText("");
					viewer.callRequestRender();
				}
			}

			@Override
			protected void itemsSelected(int[] objects) {
				if(!elem.isEmpty())
				{
					System.out.println("Selection");
					for (int i=0; i<elem.size(); i++)
						elem.get(i).getLabel().setColor(Color.LIGHT_GRAY);
					for (int i=0; i<objects.length; i++)
					{
						elem.get(objects[i]).getLabel().setColor(Color.pink);
						System.out.println(elem.get(objects[i]));
					}
						
					viewer.callRequestRender();
					
					if(objects.length ==1 && elem.get(objects[0]).getLayer() != LAYER_MIDDLE && elem.get(objects[0]).getLayer() != LAYER_RIGHT)
					{
						viewer.selectItem(elem.get(objects[0]).getId(), elem.get(objects[0]).getLabel().getText());
					}
				}
			}
			
		};
	}
	public void init()
	{
		elem.clear();
		elementId.clear();
		edges.clear();
		cnt =0;
		middles =0;
		initObjectInteraction();
	}
	
	public ObjectInteraction getObjectInteraction()
	{	
		return this.objectInteraction;
	}
	
	protected void addLabel(PivotLabel label , boolean tilt, boolean isChangeable)
	{
		label.setChangeable(isChangeable);
		label.setColor(Color.LIGHT_GRAY);
		if (tilt)
			label.setRotationAngle(Math.PI / 3);
		objectInteraction.addItem(objectInteraction.new RectangleItem(label));
	}

	
	
	public ArrayList<String> getElementIds()
	{
		return this.elementId;
	}
	
	public ArrayList<PivotElement> getElements()
	{
		return this.elem;
	}
	public double getRandomPositionX(double maxX, double minX)
	{	
		double range = maxX -minX;
		double x = Math.random() * range + minX;
		return x;
	}
	public int addTopElement(String id, String displayName, int sourceIndex)
	{			
		double minX = this.elem.get(sourceIndex).getPosition().getX()- STEP_MIDDLE_ITEM;
		double maxX = this.elem.get(sourceIndex).getPosition().getX()+ STEP_MIDDLE_ITEM;
		Point2D.Double position = new Point2D.Double(getRandomPositionX(maxX, minX), TOP_Y);
		PivotElement element = new PivotElement(id, displayName, position, LAYER_TOP);
		this.elem.add(element);
		this.elementId.add(id);
		this.addLabel(element.getLabel(), false, true);		
		
		
		
		return cnt++;
	}
	
	public int addBottomElement(String id, String displayName, int sourceIndex)
	{
		double minX = this.elem.get(sourceIndex).getPosition().getX()- STEP_MIDDLE_ITEM;
		double maxX = this.elem.get(sourceIndex).getPosition().getX()+ STEP_MIDDLE_ITEM;
		Point2D.Double position = new Point2D.Double(getRandomPositionX(maxX, minX), BOTTOM_Y);
		PivotElement element = new PivotElement(id, displayName, position, LAYER_BOTTOM);
		this.elem.add(element);
		this.elementId.add(id);
		this.addLabel(element.getLabel(), false, true);	
		return cnt++;
	}
	public int addMiddleElement(PivotElement element)
	{
		middles++;
		Point2D.Double position = new Point2D.Double(middles*STEP_MIDDLE_ITEM,MIDDLE_Y); 
		element.setPosition(position);
		element.setLayer(LAYER_MIDDLE);
		this.elem.add(element);
		this.elementId.add(element.getId());
		this.addLabel(element.getLabel(), true, false);
		return cnt++;
	}
	public int addMiddleElement(String id, String displayName)
	{
		middles++;
		Point2D.Double position = new Point2D.Double(middles*STEP_MIDDLE_ITEM,MIDDLE_Y); 
		PivotElement element = new PivotElement(id, displayName, position, LAYER_MIDDLE);
		this.elem.add(element);
		this.elementId.add(id);
		this.addLabel(element.getLabel(), true, false);
		return cnt++;
	}
	public int addRightElement(String id, String displayName, int sourceIndex)
	{
		double x= BOUNDARY_RIGHT;

		Point2D.Double position = new Point2D.Double(x,MIDDLE_Y); 
		PivotElement element = new PivotElement(id, displayName, position, LAYER_RIGHT);
		this.elem.add(element);
		this.elementId.add(id);
		this.addLabel(element.getLabel(), false, false);
		return cnt++;
	}
	
	public int addMainItem(String id, String displayName)
	{
		middles++;
		Point2D.Double position = new Point2D.Double(-4*STEP_MIDDLE_ITEM,MIDDLE_Y); 
		PivotElement element = new PivotElement(id, displayName, position, LAYER_MIDDLE);
		Font font = new Font("Sans-Serif",Font.PLAIN,30);
		element.getLabel().setFont(font);
		this.elem.add(element);
		this.elementId.add(id);
		this.addLabel(element.getLabel(), true, false);
		
		return cnt++;
	}
	public void addEdge(int e1, int e2)
	{
		PivotElement source = this.elem.get(e1);
		PivotElement destination = this.elem.get(e2);
		
		PivotEdge edge = new PivotEdge(source, e1, destination,e2, false);
		this.edges.add(edge);
	}
	
	public void iteration()
	{
		
		//init forces for top and bottom
		double[] fx = new double[elem.size()];
		double[] fy = new double[elem.size()];

		
		//repulsive forces between point
		for (int i=0; i<elem.size()-1; i++)
			for (int j=i+1; j<elem.size(); j++)
			{
				if (elem.get(i).getLayer() == LAYER_MIDDLE || elem.get(j).getLayer() == LAYER_MIDDLE)
					continue;
				
				if (elem.get(i).getLayer() != elem.get(j).getLayer())
					continue;
				
				double[] f= this.computeLabelRepulsion(i, j);
			
				fx[i] += f[0];
				fy[i] += f[1];
				
				fx[j] -= f[0];
				fy[j] -= f[1];
			}
		

		//edge forces
		for (int i=0; i<edges.size(); i++)
		{
			PivotEdge edge = this.edges.get(i);
					
			int e1 = edge.getSourceIndex();
			int e2 = edge.getDestinationIndex();
			
			int springLength = edge.getSpringLength()* COEFF_SPRING_LENGTH;
			double[] f = compAttraction(edge.getSource().getPosition(), edge.getDestination().getPosition(),springLength);
			fx[e1] += f[0];
			fy[e1] += f[1];
			
			fx[e2] -= f[0];
			fy[e2] -= f[1];
		}
		
	//boundary forces
		for (int i=0; i<elem.size(); i++)
		{
			
			
			if(this.elem.get(i).getLayer() == LAYER_RIGHT)
			{
				double x = BOUNDARY_RIGHT;
				double d = elem.get(i).getPosition().getX() - x;
				double mag = COEFF_BOUNDARY_FORCE * 100/(d*d);
				fx[i] += mag;

			}
			else
			{
				if (this.elem.get(i).getLayer() == LAYER_MIDDLE || this.elem.get(i).getLayer() == LAYER_RIGHT) continue;
				
				double y = 300;
				if (this.elem.get(i).getLayer() == LAYER_BOTTOM) y = 600;
				
				double d = elem.get(i).getPosition().getY() - y;
				
				double mag = COEFF_BOUNDARY_FORCE * 1/(d*d);
				
				if (this.elem.get(i).getLayer() == LAYER_TOP)
					fy[i] -= mag;
				else fy[i] += mag;
			}
		}
		
		for (int i=0; i<fx.length; i++)
		{
			if (this.elem.get(i).getLayer() == LAYER_MIDDLE)
				continue;
				
			double fl = Math.sqrt(fx[i]*fx[i] + fy[i]*fy[i]);
			if (fl > 5)
			{
				fx[i] = 5 * (fx[i]/fl);
				fy[i] = 5 * (fy[i]/fl);
			}
			
			double x =elem.get(i).getPosition().getX();
			double y =elem.get(i).getPosition().getY();
			
			x = x+ fx[i];
			y = y +fy[i];
			
			elem.get(i).setPosition(new Point2D.Double(x,y));			
		}
		
	}
	
	protected double[] compAttraction(Point2D p1, Point2D p2, double springLength)
	{
		double x1 = p1.getX();
		double y1 = p1.getY();	

		double x2 = p2.getX();
		double y2 = p2.getY();
		
		double d = p1.distance(p2);
		
		if (d == 0) return new double[]{0,0};
		
		double vx = x2-x1;
		double vy = y2-y1;
		double vl = Math.sqrt(vx*vx + vy*vy);
		vx = vx/vl;
		vy = vy/vl;
		
		double mag = d/springLength;
		
		if (d < springLength)
			return new double[]{0,0};
		
		return new double[]{vx*mag, vy*mag};			
	}
	private double[] computeLabelRepulsion(int index1, int index2)
	{
		Label label1 = this.elem.get(index1).getLabel();
		Label label2 = this.elem.get(index2).getLabel();
		
		double fx =0;
		double fy =0;
		int totalPoints = 5;
		Point2D[] label1Points = new Point2D[totalPoints];
		Point2D[] label2Points = new Point2D[totalPoints];
		
		label1Points[0] = new Point((int)(label1.x),(int)(label1.y)) ;
		label2Points[0] = new Point((int)(label2.x),(int)(label2.y)) ;
		

		label1Points[1] = new Point((int)(label1.x-label1.w/2),(int)(label1.y - label1.h/2)) ;
		label2Points[1] = new Point((int)(label2.x-label2.w/2),(int)(label2.y - label2.h/2)) ;
		
		label1Points[2] = new Point((int)(label1.x-label1.w/2),(int)(label1.y + label1.h/2)) ;
		label2Points[2] = new Point((int)(label2.x-label2.w/2),(int)(label2.y + label2.h/2)) ;
		
		label1Points[3] = new Point((int)(label1.x+label1.w/2),(int)(label1.y - label1.h/2)) ;
		label2Points[3] = new Point((int)(label2.x+label2.w/2),(int)(label2.y - label2.h/2)) ;
		
		label1Points[4] = new Point((int)(label1.x+label1.w/2),(int)(label1.y + label1.h/2)) ;
		label2Points[4] = new Point((int)(label2.x+label2.w/2),(int)(label2.y + label2.h/2)) ;
		
		for(int i=0;i<label1Points.length;i++)
		{
			Point2D p1 = label1Points[i];
			for(int j=0;j<label2Points.length;j++)
			{
				
				Point2D p2 = label2Points[j];
				if(i==0 || j==0)
				{
					double[] f = this.compRepulsion(p1, p2, COEFF_COMPULSIVE_FORCE_STRONG);
					fx+=f[0];
					fy+=f[1];
				}
				else
				{
					double[] f = this.compRepulsion(p1, p2, COEFF_COMPULSIVE_FORCE_WEAK);
					fx+=f[0];
					fy+=f[1];
				}
				
			}
			
		}
		
		return new double[]{fx,fy};
	}
	
	protected double[] compRepulsion(Point2D p1, Point2D p2, int forceCoefficient)
	{
		double d = p1.distance(p2);
		
		double x1 = p1.getX();
		double y1 = p1.getY();
		double x2 = p2.getX();
		double y2 = p2.getY();
		
		double vx = x2 - x1;
		double vy = y2 - y1;
		
		while (d < 0.1)
		{
			x2 = x2 + Math.random()/10;
			y2 = y2 + Math.random()/10;
			vx = x2-x1;
			vy = y2-y1;				
			d = vx*vx + vy*vy;
			d = Math.sqrt(d);
		}			
		vx /= d;
		vy /= d;
		
		double mag = forceCoefficient  * (-1.)/(d*d);
		
		return new double[]{vx*mag, vy*mag};
	}
	public static void drawPoint(double x, double y, Graphics2D g, Color c)
	{
		g.setColor(c);
		int radius = 5;
		g.fillOval((int)x-radius,(int) y-radius, 2*radius, 2*radius);
	}
	
	protected void renderMainItemEdge(Graphics2D g)
	{
		// main item edge;
		if( middles > 1)
		{
			float dash1[] = {10.0f};
		    BasicStroke dashed = new BasicStroke(1.0f,
		                        BasicStroke.CAP_BUTT,
		                        BasicStroke.JOIN_MITER,
		                        10.0f, dash1, 0.0f);
			int x1=-4*STEP_MIDDLE_ITEM;
			int y1=450;
			
			int x2= (middles-1)*STEP_MIDDLE_ITEM;
			int y2= 450;
			Stroke previousStroke = g.getStroke();
			g.setStroke(dashed);
			g.drawLine(x1, y1, x2, y2);
			g.setStroke(previousStroke);
		}
		
	}
	public void render(Graphics2D g) {
		g.setColor(Color.black);
		
		for (PivotElement element: this.elem)
		{	
			element.render(g);
		}
		
		for (int i=0; i<edges.size(); i++)
		{
			PivotEdge edge = this.edges.get(i);
			
			int e1 = edge.getSourceIndex();
			int e2 = edge.getDestinationIndex();
			g.setColor(Color.lightGray);
			if (objectInteraction.getItem(e1).hovered || (objectInteraction.getItem(e2).hovered))
					g.setColor(Color.black);
			if (objectInteraction.getItem(e1).selected || (objectInteraction.getItem(e2).selected))
					g.setColor(Color.red);
			edge.render(g);
		}
	}
}
