package multifaceted;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Util {
	public static void drawCircle(int x, int y, Color c, Graphics2D g)
	{
		Color previousColor = g.getColor();
		g.setColor(c);
		int rad = 2;
		g.fillOval(x-rad, y-rad, 2*rad, 2*rad);
		g.setColor(previousColor);
	}
	
	public static double distanceToRectangle(double x, double y, double w, double h, Point2D p)
	{
		double d=0;
		
		AffineTransform at = new AffineTransform();
		
//		
//		at.translate(w/2, h/2);
//		at.rotate(- getRotationAngle());
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
	
	
	public static double gaussianDistribution(double value, double mean, double deviation)
	{	
		return Math.exp(-1.0* (value - mean) * (value - mean) / (2 * deviation * deviation) ) / (deviation * Math.sqrt(2 * Math.PI));
	}
}
