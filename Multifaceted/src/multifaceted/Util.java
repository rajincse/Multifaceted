package multifaceted;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import eyetrack.EyeTrackerLabelDetector;

public class Util {
	public static final int RECT_SIDE_INSIDE =0;
	public static final int RECT_SIDE_TOP_LEFT =1;
	public static final int RECT_SIDE_TOP =2;
	public static final int RECT_SIDE_TOP_RIGHT =3;
	public static final int RECT_SIDE_RIGHT =4;
	public static final int RECT_SIDE_BOTTOM_RIGHT =5;
	public static final int RECT_SIDE_BOTTOM =6;
	public static final int RECT_SIDE_BOTTOM_LEFT =7;
	public static final int RECT_SIDE_LEFT =8;
	
	
	public static void drawCircle(int x, int y, Color c, Graphics2D g)
	{
		Color previousColor = g.getColor();
		g.setColor(c);
		int rad = 2;
		g.fillOval(x-rad, y-rad, 2*rad, 2*rad);
		g.setColor(previousColor);
	}
	public static Point2D getTransformedPoint(double x, double y, double theta, Point2D p)
	{
		AffineTransform at = new AffineTransform();
		at.translate(x, y);
		at.rotate(theta);
		Point2D transformedPoint = new Point2D.Double();
		at.transform(p, transformedPoint);
		
		return transformedPoint;
	}
	
	public static int getRectangleSide(Rectangle rect, Point2D p)
	{
		Point2D transformedPoint = getTransformedPoint(-rect.x, -rect.y, 0, p);
		if(transformedPoint.getX() < 0 && transformedPoint.getY() < 0)			
		{
			return RECT_SIDE_TOP_LEFT;
		}
		else if(transformedPoint.getX() >= 0 && transformedPoint.getX() <=rect.width && transformedPoint.getY() < 0 )
		{
			return RECT_SIDE_TOP;
		}
		else if(transformedPoint.getX() > rect.width && transformedPoint.getY() < 0)
		{
			return RECT_SIDE_TOP_RIGHT;
		}
		else if(transformedPoint.getX() >rect.width && transformedPoint.getY()>=0 && transformedPoint.getY() <=rect.height)
		{
			return RECT_SIDE_RIGHT;
		}
		else if(transformedPoint.getX() > rect.width && transformedPoint.getY() > rect.height)
		{
			return RECT_SIDE_BOTTOM_RIGHT;
		}
		else if(transformedPoint.getX() >=0 && transformedPoint.getX() <=rect.width && transformedPoint.getY() > rect.height)
		{
			return RECT_SIDE_BOTTOM;
		}
		else if(transformedPoint.getX() < 0 && transformedPoint.getY() >rect.height)
		{
			return RECT_SIDE_BOTTOM_LEFT;
		}
		else if(transformedPoint.getX() < 0 && transformedPoint.getY() >=0 && transformedPoint.getY() <=rect.height)
		{
			return RECT_SIDE_LEFT;
		}
		else
		{
			return RECT_SIDE_INSIDE;
		}
		
	}
	public static double distanceToRectangle(double x, double y, double w, double h, Point2D p)
	{
		double d=0;
		Point2D transformedPoint = getTransformedPoint(-x, -y, 0, p);
		Rectangle rect = new Rectangle(0,0, (int)w, (int)h);
		int rectangleSide = getRectangleSide(rect, transformedPoint);
		
		switch(rectangleSide)
		{
			case RECT_SIDE_INSIDE:
				d = 0;
				break;
			case RECT_SIDE_TOP_LEFT:
				d = p.distance(0, 0);
				break;
			case RECT_SIDE_TOP:
				d = Line2D.ptLineDist(0, 0, w, 0, transformedPoint.getX(), transformedPoint.getY());
				break;
			case RECT_SIDE_TOP_RIGHT:
				d = transformedPoint.distance(w,0);
				break;
			case RECT_SIDE_RIGHT:
				d = Line2D.ptLineDist(w, 0, w, h, transformedPoint.getX(), transformedPoint.getY());
				break;
			case RECT_SIDE_BOTTOM_RIGHT:
				d = transformedPoint.distance(w,h);
				break;
			case RECT_SIDE_BOTTOM:
				d = Line2D.ptLineDist(0, h, w, h, transformedPoint.getX(), transformedPoint.getY());
				break;
			case RECT_SIDE_BOTTOM_LEFT:
				d = transformedPoint.distance(0,h);
				break;
			case RECT_SIDE_LEFT:
				d = Line2D.ptLineDist(0, 0, 0, h, transformedPoint.getX(), transformedPoint.getY());
				break;
			default:
				d =0;
				
		}


		return d;
	}
	
	public static double getRectangleToGazeScore(double x, double y, double w, double h, Point2D p, double zoom)
	{
		double score=0;
		double deviation = EyeTrackerLabelDetector.EDGETHRESHOLD/zoom;
		double distance = distanceToRectangle(x, y, w, h, p)*zoom;
		score =gaussianDistribution(distance, 0, deviation)*  Math.sqrt(2 * Math.PI);
		return score;
	}
	public static double getGaussianScore(Point2D gazePosition, double meanX, double meanY, double deviationX, double deviationY)
	{
		return gaussianDistribution(gazePosition.getX(), meanX, deviationX)* gaussianDistribution(gazePosition.getX(), meanY, deviationY);
	}
	public static double gaussianDistribution(double value, double mean, double deviation)
	{	
		return Math.exp(-1.0* (value - mean) * (value - mean) / (2 * deviation * deviation) ) / (deviation * Math.sqrt(2 * Math.PI));
	}
}
