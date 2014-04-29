package multifaceted.layout;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import perspectives.util.Label;

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
		
			int x = (int)this.getPosition().getX();
			int y = (int)this.getPosition().getY();
			
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
			g.setColor(Color.black);
			g.drawString(this.label.getEdgeCount()+"", lx, ly);
		
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.id+" "+this.label.getText()+" "+this.position;
	}
}
