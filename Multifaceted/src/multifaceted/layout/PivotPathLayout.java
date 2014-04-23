package multifaceted.layout;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import perspectives.base.ObjectInteraction;
import perspectives.util.Label;

public class PivotPathLayout {
	public static final int COEFF_COMPULSIVE_FORCE_WEAK=50;
	public static final int COEFF_COMPULSIVE_FORCE_STRONG=500;
	public static final int SPRING_LENGTH=750;
	public static final int COEFF_BOUNDARY_FORCE=10000;
	
	private LayoutViewerInterface viewer;
	public PivotPathLayout(LayoutViewerInterface viewer)
	{
		this.viewer = viewer;
	}
	
	ArrayList<PivotLabel> labels = new ArrayList<PivotLabel>();
	
	private ObjectInteraction objectInteraction = new ObjectInteraction()
	{

		@Override
		protected void mouseIn(int object) {
			if (objectInteraction.getItem(object).selected)
				labels.get(object).setColor(Color.red);
			else
				labels.get(object).setColor(Color.yellow);
			viewer.callRequestRender();
		}

		@Override
		protected void mouseOut(int object) {
			if (objectInteraction.getItem(object).selected)
				labels.get(object).setColor(Color.pink);
			else
				labels.get(object).setColor(Color.LIGHT_GRAY);
			viewer.callRequestRender();
		}

		@Override
		protected void itemsSelected(int[] objects) {
			for (int i=0; i<labels.size(); i++)
				labels.get(i).setColor(Color.LIGHT_GRAY);
			for (int i=0; i<objects.length; i++)
				labels.get(objects[i]).setColor(Color.pink);
			viewer.callRequestRender();
		}
		
	};
	public ObjectInteraction getObjectInteraction()
	{
		return this.objectInteraction;
	}
	
	public void addLabel(String s, boolean tilt, boolean isChangeable)
	{
		PivotLabel l = new PivotLabel(s, isChangeable);
		
		l.setColor(Color.LIGHT_GRAY);
		if (tilt)
		l.setRotationAngle(Math.PI / 3);
		labels.add(l);
		objectInteraction.addItem(objectInteraction.new RectangleItem(l));
	}

	ArrayList<String> elem = new ArrayList<String>();
	ArrayList<Point2D> elemPos = new ArrayList<Point2D>(); 
	
	ArrayList<Integer> layer = new ArrayList<Integer>();
	
	ArrayList<Integer> edges1 = new ArrayList<Integer>();
	ArrayList<Integer> edges2 = new ArrayList<Integer>();
	
	int cnt = 0;
	
	int middles = 0;
	
	public ArrayList<String> getElements()
	{
		return this.elem;
	}
	
	public int addTopElement(String label)
	{
		elem.add(label);
		elemPos.add(new Point2D.Double(0,0));
		layer.add(0);
		return cnt++;
	}
	
	public int addBottomElement(String label)
	{
		elem.add(label);
		elemPos.add(new Point2D.Double(0,900));	
		layer.add(2);
		return cnt++;
	}
	
	public int addMiddleElement(String label)
	{
		elem.add(label);
		elemPos.add(new Point2D.Double(middles*50,450));
		middles++;
		
		layer.add(1);
		return cnt++;
	}
	
	public void addEdge(int e1, int e2)
	{
		edges1.add(e1);
		edges2.add(e2);
		this.labels.get(e1).increaseEdgeCount();
		this.labels.get(e2).increaseEdgeCount();
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
				if (layer.get(i) == 1 || layer.get(j) == 1)
					continue;
				
				if (!layer.get(i).equals(layer.get(j)))
					continue;
				
//				double[] f = this.compRepulsion(elemPos.get(i), elemPos.get(j));
				double[] f= this.computeLabelRepulsion(i, j);
			
				fx[i] += f[0];
				fy[i] += f[1];
				
				fx[j] -= f[0];
				fy[j] -= f[1];
			}
		

		//edge forces
		for (int i=0; i<edges1.size(); i++)
		{
			double[] f = compAttraction(elemPos.get(edges1.get(i)), elemPos.get(edges2.get(i)), SPRING_LENGTH);
			fx[edges1.get(i)] += f[0];
			fy[edges1.get(i)] += f[1];
			
			fx[edges2.get(i)] -= f[0];
			fy[edges2.get(i)] -= f[1];
		}
		
	//boundary forces
		for (int i=0; i<elem.size(); i++)
		{
			if (layer.get(i) == 1) continue;
			
			double y = 300;
			if (layer.get(i) == 2) y = 600;
			
			double d = elemPos.get(i).getY() - y;
			
			double mag = COEFF_BOUNDARY_FORCE * 1/(d*d);
			
			if (layer.get(i) == 0)
				fy[i] -= mag;
			else fy[i] += mag;
			
		}
		
		for (int i=0; i<fx.length; i++)
		{
			if (layer.get(i) == 1)
				continue;
				
			double fl = Math.sqrt(fx[i]*fx[i] + fy[i]*fy[i]);
			if (fl > 5)
			{
				fx[i] = 5 * (fx[i]/fl);
				fy[i] = 5 * (fy[i]/fl);
			}
			
			double x =elemPos.get(i).getX();
			double y =elemPos.get(i).getY();
			
			x = x+ fx[i];
			y = y +fy[i];
			
			elemPos.remove(i);
			elemPos.add(i, new Point2D.Double(x,y));
		}
		
	}
	
	private double[] compAttraction(Point2D p1, Point2D p2, double springLength)
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
		
		return new double[]{vx*mag, vy*mag};			
	}
	private double[] computeLabelRepulsion(int index1, int index2)
	{
		Label label1 = this.labels.get(index1);
		Label label2 = this.labels.get(index2);
		
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
	
	private double[] compRepulsion(Point2D p1, Point2D p2, int forceCoefficient)
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
	private void renderDebug(Graphics2D g)
	{
		for (int i=0; i<elem.size(); i++)
		{
			int x = (int)elemPos.get(i).getX();
			int y = (int)elemPos.get(i).getY();
			Label label = labels.get(i);
			int lx = (int)label.x;
			int ly = (int)label.y;
			int lw = (int) label.w;
			int lh = (int) label.h;
			g.setColor(Color.blue);
			g.drawRect(lx-lw/2,ly-lh/2,lw,lh);
			int radius =5;
			g.setColor(Color.green);
			g.fillOval(x-radius, y-radius, 2*radius, 2*radius);
			
			g.setColor(Color.pink);
			g.fillOval(lx-lw/2-radius, ly-lh/2-radius, 2*radius, 2*radius);
			g.fillOval(lx-lw/2-radius, ly+lh/2-radius, 2*radius, 2*radius);
			g.fillOval(lx+lw/2-radius, ly-lh/2-radius, 2*radius, 2*radius);
			g.fillOval(lx+lw/2-radius, ly+lh/2-radius, 2*radius, 2*radius);
			
		}
		
	}
	public void render(Graphics2D g) {
		g.setColor(Color.black);
		
		for (int i=0; i<elem.size(); i++)
		{
			int x = (int)elemPos.get(i).getX();
			int y = (int)elemPos.get(i).getY();
			
			labels.get(i).x = x;
			labels.get(i).y = y;
			
			//g.drawString(elem.get(i), x, y);
			labels.get(i).render(g);
		}
		
		for (int i=0; i<edges1.size(); i++)
		{
			g.setColor(Color.lightGray);
			if (objectInteraction.getItem(edges1.get(i)).hovered || (objectInteraction.getItem(edges2.get(i)).hovered))
					g.setColor(Color.black);
			if (objectInteraction.getItem(edges1.get(i)).selected || (objectInteraction.getItem(edges2.get(i)).selected))
					g.setColor(Color.red);
			int x1 = (int)elemPos.get(edges1.get(i)).getX();
			int y1 = (int)elemPos.get(edges1.get(i)).getY();
			int x2 = (int)elemPos.get(edges2.get(i)).getX();
			int y2 = (int)elemPos.get(edges2.get(i)).getY();
			
			g.drawLine(x1, y1, x2, y2);
		}
//		this.renderDebug(g);
	}
}
