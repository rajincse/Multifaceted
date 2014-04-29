package multifaceted.layout;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

public class GroupedPivotElement {
	public static final int GAP_Y=10;
	public static final int GAP_X=10;
	
	public double fx;
	public double fy;
	
	private ArrayList<PivotElement> elements;
	public GroupedPivotElement(PivotElement intialElement)
	{
		this.elements = new ArrayList<PivotElement>();
		this.elements.add(intialElement);
		
		fx=0;
		fy=0;
	}
	
	public Point2D getCenterPosition() {
		double x=0;
		double y =0;
		if(!this.elements.isEmpty())
		{
			for(PivotElement elem:this.elements)
			{
				x+= elem.getPosition().getX();
				y+=elem.getPosition().getY();
			}
			x= x/ this.elements.size();
			y = y / this.elements.size();
		}
		
		return new Point2D.Double(x,y);
	}
	

	public ArrayList<PivotElement> getElements() {
		return elements;
	}

	public void setElements(ArrayList<PivotElement> elements) {
		this.elements = elements;
	}
	
	public void addElement(PivotElement elem)
	{
		if(!this.elements.isEmpty())
		{
			PivotElement lastElement = this.elements.get(this.elements.size()-1);
			Point2D lastElementPosition = lastElement.getPosition();
			
			elem.setPosition(new Point2D.Double(lastElementPosition.getX(),
					lastElementPosition.getY()+lastElement.getLabel().h+GAP_Y));
		}
		
		this.elements.add(elem);
	}
	
	public void removeElement(PivotElement elem)
	{
		if(this.elements.contains(elem))
		{
			int index = this.elements.indexOf(elem);
			for(int i=index+1;i< this.elements.size();i++)
			{
				PivotElement residingElement = this.elements.get(i);
				residingElement.setPosition(new Point2D.Double( 
						residingElement.getPosition().getX(), residingElement.getPosition().getY()- elem.getLabel().h - GAP_Y));
			}
			this.elements.remove(elem);
		}
	}
	public double getWidth()
	{
		double maxWidth =java.lang.Double.MIN_VALUE;
		for(PivotElement elem: this.elements)
		{
			if(maxWidth < elem.getLabel().w)
			{
				maxWidth = elem.getLabel().w;
						
			}
		}
		double width =0;
		if(maxWidth > width)
		{
			width =GAP_X+maxWidth+GAP_X; 
		}
		return width;
	}
	public double getHeight()
	{
		double height =0;
		for(PivotElement elem: this.elements)
		{
			height+= elem.getLabel().h+GAP_Y;
		}
		return height;
	}
	public void render(Graphics2D g)
	{
		for(PivotElement elem: this.elements)
		{
			elem.render(g);
		}
		
//		renderDebug(g);
	}
	
	public void renderDebug(Graphics2D g)	
	{
		Point2D center = this.getCenterPosition();
		double w = this.getWidth();
		double h = this.getHeight();
		
		g.setColor(Color.black);
		g.drawRect((int)(center.getX()-w/2),(int) (center.getY()-h/2),(int) w,(int) h);
//		g.setColor(Color.black);
//		g.drawString("fx:"+String.format("%.2f",this.fx)+", fy:"+String.format("%.2f",this.fy),(int)center.getX(),(int) center.getY());
		
		Point2D[] gravityPoints = this.getGravityPoints();
		for(Point2D p: gravityPoints)
		{
			PivotPathLayout.drawPoint(p.getX(), p.getY(), g, Color.blue);
		}
	}
	public Point2D[] getGravityPoints()
	{
		int totalPoints = 5+3* this.elements.size();
		Point2D[] gravityPoints = new Point2D[totalPoints];
		
		Point2D center = this.getCenterPosition();
		double width = this.getWidth();
		double height = this.getHeight();
		//Center
		gravityPoints[0]= this.getCenterPosition();
		//Top Left
		gravityPoints[1] = new Point2D.Double(center.getX()-width/2, center.getY()- height/2);
		//Bottom left
		gravityPoints[2] = new Point2D.Double(center.getX()-width/2, center.getY()+ height/2);
		//Bottom Right
		gravityPoints[3] = new Point2D.Double(center.getX()+width/2, center.getY()+ height/2);
		//Top Right
		gravityPoints[4] = new Point2D.Double(center.getX()+width/2, center.getY()- height/2);
		
		int index =5;
		for(PivotElement elem: this.elements)
		{
			
			gravityPoints[index] = new Point2D.Double(center.getX()-width/2,elem.getPosition().getY() );
			index++;
			gravityPoints[index] = new Point2D.Double(center.getX(),elem.getPosition().getY() );
			index++;
			gravityPoints[index] = new Point2D.Double(center.getX()+width/2,elem.getPosition().getY() );
			index++;
		}
		return gravityPoints;
	}
	
	public void applyForces()
	{
		this.applyForces(this.fx, this.fy);
		this.fx =0;
		this.fy=0;
	}
	public void applyForces(double fx, double fy)
	{	
		double fl = Math.sqrt(fx*fx + fy*fy);
		if (fl > 5)
		{
			fx = 5 * (fx/fl);
			fy= 5 * (fy/fl);
		}
		for(PivotElement elem: this.elements)
		{
			double x = elem.getPosition().getX();
			double y = elem.getPosition().getY();
			x+= fx;
			y+= fy;
			elem.setPosition(new Point2D.Double(x,y));
		}
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String str ="items:\r\n";
		for(PivotElement elem:this.elements)
		{
			str+= elem.toString()+"\r\n";
		}
		return str;
	}
}
