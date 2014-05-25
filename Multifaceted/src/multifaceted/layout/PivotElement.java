package multifaceted.layout;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class PivotElement {
	protected PivotLabel label;
	protected Point2D position;
	protected String id;
	
	protected int layer;
	
	public PivotElement(String id, String displayString, Point2D position, int layer)
	{
		this.id = id;
		this.label = new PivotLabel(displayString,false);
		this.position = position;
		this.layer = layer;
	}

	public PivotLabel getLabel() {
		return label;
	}

	public void setLabel(PivotLabel label) {
		this.label = label;
	}

	public Point2D getPosition() {
		return position;
	}

	public void setPosition(Point2D position) {
		this.position = position;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public void render(Graphics2D g)
	{	
		this.label.x =(int) this.getPosition().getX();
		this.label.y = (int) this.getPosition().getY();
		
		this.label.render(g);
		
//		renderDebug(g);
	}
	private void renderDebug(Graphics2D g)
	{
			
			int lx = (int)label.x;
			int ly = (int)label.y;
			int lw = (int) label.w;
			int lh = (int) label.h;
			g.setColor(Color.blue);
			g.drawRect(lx-lw/2,ly-lh/2,lw,lh);
			int radius =5;
			Point2D[] gravityPoints = getGravityPoints();
			g.setColor(Color.green);
			g.fillOval((int)gravityPoints[0].getX()-radius, (int)gravityPoints[0].getY()-radius, 2*radius, 2*radius);
			
			g.setColor(Color.pink);
			for(int i=1;i<gravityPoints.length;i++)
			{
				g.fillOval((int)gravityPoints[i].getX()-radius, (int)gravityPoints[i].getY()-radius, 2*radius, 2*radius);
			}
			
			g.setColor(Color.black);
			g.drawString(this.label.getEdgeCount()+"", lx, ly);
		
	}
	public Point2D[] getGravityPoints()
	{
		int totalPoints = 5;
		Point2D[] gravityPoints = new Point2D[totalPoints];
		int lx = (int)label.x;
		int ly = (int)label.y;
		int lw = (int) label.w;
		int lh = (int) label.h;
		
		gravityPoints[0] = new Point2D.Double(lx, ly);
		if(layer == PivotPathLayout.LAYER_MIDDLE)
		{
			double theta = getLabel().getRotationAngle();
			//A
			double x = lx + Math.cos(theta)* lw/2 - Math.sin(theta)*lh/2;
			double y = ly + Math.sin(theta)* lw/2 + Math.cos(theta)*lh/2;
			gravityPoints[1] = new Point2D.Double(x,y);
			//B
			x = lx + Math.cos(theta)* lw/2 + Math.sin(theta)*lh/2;
			y = ly + Math.sin(theta)* lw/2 - Math.cos(theta)*lh/2;
			gravityPoints[2] = new Point2D.Double(x,y);
			//C
			x = lx - Math.cos(theta)* lw/2 + Math.sin(theta)*lh/2;
			y = ly - Math.sin(theta)* lw/2 - Math.cos(theta)*lh/2;
			gravityPoints[3] = new Point2D.Double(x,y);
			//D
			x = lx - Math.cos(theta)* lw/2 - Math.sin(theta)*lh/2;
			y = ly - Math.sin(theta)* lw/2 + Math.cos(theta)*lh/2;			
			gravityPoints[4] = new Point2D.Double(x,y);
		}
		else
		{
			// no rotation
			//A
			gravityPoints[1] = new Point2D.Double(lx+lw/2, ly+lh/2);
			//B
			gravityPoints[2] = new Point2D.Double(lx+lw/2, ly-lh/2);
			//C
			gravityPoints[3] = new Point2D.Double(lx-lw/2, ly-lh/2);
			//D
			gravityPoints[4] = new Point2D.Double(lx-lw/2, ly+lh/2);
		}
		
		return gravityPoints;
		
		
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.id+" "+this.label.getText()+" "+this.position;
	}
}
