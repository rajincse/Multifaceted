package multifaceted.layout;


import java.awt.Font;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import perspectives.util.Label;

public class PivotLabel extends Label{
	private static final double INCREASING_FACTOR_FONT=1.3; 
	
	private int edgeCount;
	private boolean isChangeable;
	

	public PivotLabel( String label, boolean isChangeable) {
		super(0, 0, label);
		this.edgeCount =0;
		this.isChangeable= isChangeable;
	}
	
	public void setEdgeCount(int edgeCount)
	{
		
		this.edgeCount = edgeCount;
		changeView();
		
	}
	private void changeView()
	{
		if(isChangeable)
		{
			int fontSize = this.getFont().getSize();
			fontSize = (int) (fontSize* INCREASING_FACTOR_FONT);
			Font font = this.getFont();
			Font newFont = new Font(font.getFontName(), font.getStyle(), fontSize);
			this.setFont(newFont);
		}
		
	}
	public void increaseEdgeCount()
	{
		this.edgeCount++;
		changeView();
	}

	public int getEdgeCount() {
		return edgeCount;
	}
	public void setChangeable(boolean isChangeable) {
		this.isChangeable = isChangeable;
	}
	
	public double distance(Point p)
	{
		double d=0;
		
		AffineTransform at = new AffineTransform();
		
		
		at.translate(w/2, h/2);
		at.rotate(- getRotationAngle());
		at.translate(-x, -y);
		
		Point2D.Double transformedPoint = new Point2D.Double();
		at.transform(p, transformedPoint);
		
		if(transformedPoint.x < 0 && transformedPoint.y < 0)			
		{
			d= transformedPoint.distance(0,0);
		}
		else if(transformedPoint.x >= 0 && transformedPoint.x <=w && transformedPoint.y < 0 )
		{
			d = Line2D.ptLineDist(0, 0, w, 0, transformedPoint.x, transformedPoint.y);
		}
		else if(transformedPoint.x > w && transformedPoint.y < 0)
		{
			d = transformedPoint.distance(w,0);
		}
		else if(transformedPoint.x >w && transformedPoint.y>=0 && transformedPoint.y <=h)
		{
			d = Line2D.ptLineDist(w, 0, w, h, transformedPoint.x, transformedPoint.y);
		}
		else if(transformedPoint.x > w && transformedPoint.y > h)
		{
			d = transformedPoint.distance(w,h);
		}
		else if(transformedPoint.x >=0 && transformedPoint.x <=w && transformedPoint.y > h)
		{
			d = Line2D.ptLineDist(0, h, w, h, transformedPoint.x, transformedPoint.y);
		}
		else if(transformedPoint.x < 0 && transformedPoint.y >h)
		{
			d = transformedPoint.distance(0,h);
		}
		else if(transformedPoint.x < 0 && transformedPoint.y >=0 && transformedPoint.y <=h)
		{
			d = Line2D.ptLineDist(0, 0, 0, h, transformedPoint.x, transformedPoint.y);
		}
		else
		{
			d =0;
		}


		return d;
	}
}
