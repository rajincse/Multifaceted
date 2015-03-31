package multifaceted;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import eyetrack.EyeTrackerItem;
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
	
	public static Color getBorderColor()
	{
		return Color.gray;
	}
	public static void drawStar(int x, int y, int rad, Color c, Graphics2D g)
	{	
		Color previousColor = g.getColor();		
		g.translate(x, y);
		double a = 1.414* rad;
		
		g.translate(-a/2, -a/2);
		
		g.setColor(c);
		g.fillRect(0, 0, (int)a, (int)a);
		
		g.setColor(getBorderColor());
		g.drawRect(0, 0, (int)a, (int)a);
		
		g.translate(a/2, a/2);
		
		g.rotate(Math.PI/4);
		g.translate(-a/2, -a/2);
		
		g.setColor(c);
		g.fillRect(0, 0, (int)a, (int)a);
		
		g.setColor(getBorderColor());
		g.drawRect(0, 0, (int)a, (int)a);
		
		g.translate(a/2, a/2);		
		g.rotate(-Math.PI/4);
		
		
		g.translate(-x, -y);
		g.setColor(previousColor);
		
	}
	public static void drawEquilateralTriangle(int x, int y, int rad, Color c, Graphics2D g)
	{
		Color previousColor = g.getColor();
		
		g.translate(x, y);
		
		
		
		double a = 1.732* rad;
		int xPoints[] = new int[]{0, (int)(a), (int )(a/2)};
		int yPoints[] = new int[]{0, 0	,(int )(- 1.5 * rad) };
		g.translate(-(int )(a/2),(int) (0.5 * rad));
		
		g.setColor(c);
		g.fillPolygon(xPoints, yPoints, xPoints.length);
		
		g.setColor(getBorderColor());
		g.drawPolygon(xPoints, yPoints, xPoints.length);
		
		
		g.translate((int )(a/2),-(int) (0.5 * rad));
		
		g.translate(-x, -y);
		g.setColor(previousColor);
	}
	public static void drawTiltedSquare(int x, int y, int rad, Color c, Graphics2D g)
	{
		Color previousColor = g.getColor();
		
		g.translate(x, y);
		g.rotate(Math.PI/4);
		
		double a = 1.414* rad;
		g.translate(-a/2, -a/2);
		
		g.setColor(c);
		g.fillRect(0, 0, (int)a, (int)a);
		
		g.setColor(getBorderColor());
		g.drawRect(0, 0, (int)a, (int)a);
		
		g.translate(a/2, a/2);
		
		g.rotate(-Math.PI/4);
		g.translate(-x, -y);
		g.setColor(previousColor);
	}
	public static void drawSquare(int x, int y, int rad, Color c, Graphics2D g)
	{
		Color previousColor = g.getColor();
		
		g.translate(x, y);
		
		double a = 1.414* rad;
		g.translate(-a/2, -a/2);
		
		g.setColor(c);
		g.fillRect(0, 0, (int)a, (int)a);
		
		g.setColor(getBorderColor());
		g.drawRect(0, 0, (int)a, (int)a);
		
		g.translate(a/2, a/2);
		
		g.translate(-x, -y);
		g.setColor(previousColor);
	}
	public static void drawCircle(int x, int y, Color c, Graphics2D g)
	{
		Color previousColor = g.getColor();
		g.setColor(c);
		int rad = 2;
		g.fillOval(x-rad, y-rad, 2*rad, 2*rad);
		g.setColor(previousColor);
	}
	public static void drawCircle(int x, int y, int rad, Color c, Graphics2D g)
	{
		Color previousColor = g.getColor();
		g.setColor(c);
		g.fillOval(x-rad, y-rad, 2*rad, 2*rad);
		g.setColor(previousColor);
	}
	public static void drawAxis(int x, int y, Color cx, Color cy, Graphics2D g)
	{
		Color previousColor = g.getColor();
		int width =10;
		g.setColor(cx);
		g.drawLine(x, y	,x, y+width);
		g.setColor(cy);
		g.drawLine(x, y	,x+width, y);
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
	
	public static double getRectangleToGazeScoreGaussian(double x, double y, double w, double h, Point2D p, double zoom)
	{
		double score=0;
		double deviation = EyeTrackerLabelDetector.EDGETHRESHOLD/zoom;
		double distance = distanceToRectangle(x, y, w, h, p)*zoom;
		score =gaussianDistribution(distance, 0, deviation) * (deviation * Math.sqrt(2 * Math.PI));
		return score;
	}
	
	public static double getRectangleToGazeScoreNonGaussian(double x, double y, double w, double h, Point2D p, double zoom)
	{
		double score=0;
		int radius = (int)( EyeTrackerLabelDetector.EDGETHRESHOLD / zoom);
		double distance = distanceToRectangle(x, y, w, h, p);
		if (distance <= radius) 
		{
			score= 1.0 - distance/radius;
		}
			
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
	
	public static String formatNum(double number) {
		int MAX_LENGTH = 9;
		int digitsAvailable =2;
	    if (Math.abs(number) < Math.pow(10, digitsAvailable)
	            && Math.abs(number) > Math.pow(10, -digitsAvailable)) {
	        String format = "0.";
	        double temp = number;
	        for (int i = 0; i < digitsAvailable; i++) {
	            if ((temp /= 10) < 1) {
	                format += "#";
	            }
	        }
	        return new DecimalFormat(format).format(number);
	    }
	    String format = "0.";
	    for (int i = 0; i < digitsAvailable; i++) {
	            format += "#";
	    }
	    String r = new DecimalFormat(format + "E0").format(number);
	    int lastLength = r.length() + 1;
	    while (r.length() > MAX_LENGTH && lastLength > r.length()) {
	        lastLength = r.length();
	        r = r.replaceAll("\\.?[0-9]E", "E");
	    }
	    return r;
	}
	public static Color getScarfplotColor(int type)
	{
		if(type== EyeTrackerItem.TYPE_ACTOR)
		{
			return new Color(250,200,200);
		}
		else if(type== EyeTrackerItem.TYPE_MOVIE)
		{
			return  new Color(200,250,200);
		}
		else if(type== EyeTrackerItem.TYPE_DIRECTOR)
		{
			return new Color(200,200,250);			
		}	
		else if(type== EyeTrackerItem.TYPE_GENRE)
		{
			return  new Color(250,250,150);
		}	
		else if(type== EyeTrackerItem.TYPE_MOVIE_STAR_RATING)
		{
			return  new Color(250,150,250);
		}	
		else
		{
			return Color.black;
		}
	}
	
	public static Color getRelevanceChartColor(int type)
	{
		if(type== EyeTrackerItem.TYPE_ACTOR)
		{
			return new Color(230,97,1, 200);
		}
		else if(type== EyeTrackerItem.TYPE_MOVIE)
		{
			return  new Color(253,184,99,200);
		}
		else if(type== EyeTrackerItem.TYPE_DIRECTOR)
		{
			return new Color(178,171,210,200);			
		}	
		else if(type== EyeTrackerItem.TYPE_GENRE)
		{
			return  new Color(94,60,153,200);
		}	
		else if(type== EyeTrackerItem.TYPE_MOVIE_STAR_RATING)
		{
			return  new Color(212,0,250,200);
		}	
		else
		{
			return Color.black;
		}
	}
	
	public static Color getColor(int type)
	{
		if(type== EyeTrackerItem.TYPE_ACTOR)
		{
			return  new Color(230,230,230,100);
		}
		else if(type== EyeTrackerItem.TYPE_MOVIE)
		{
			return  new Color(250,150,150,100);
		}
		else if(type== EyeTrackerItem.TYPE_DIRECTOR)
		{
			return new Color(250,150,150,100);
		}	
		else if(type== EyeTrackerItem.TYPE_GENRE)
		{
			return  new Color(150,150,250,100);
		}	
		else if(type== EyeTrackerItem.TYPE_MOVIE_STAR_RATING)
		{
			return  new Color(255,255,255,0);
		}	
		else
		{
			return Color.black;
		}
			
		
	}
	
	public static Color getHoveredColor(int type)
	{
		if(type== EyeTrackerItem.TYPE_ACTOR)
		{
			return   new Color(180,180,180,150);
		}
		else if(type== EyeTrackerItem.TYPE_MOVIE)
		{
			return   new Color(200,100,100,150);
		}
		else if(type== EyeTrackerItem.TYPE_DIRECTOR)
		{
			return new Color(200,100,100,150);
		}	
		else if(type== EyeTrackerItem.TYPE_GENRE)
		{
			return  new Color(100,100,200,150);
		}	
		else if(type== EyeTrackerItem.TYPE_MOVIE_STAR_RATING)
		{
			return  new Color(200,100,100,150);
		}	
		else
		{
			return Color.black;
		}
	}
	// Get a score of 1/R to 1
	public static double getLevitatedScore(double value, double lowerBound)
	{
		return value * ( 1- lowerBound)+ lowerBound;
				
	}
}
