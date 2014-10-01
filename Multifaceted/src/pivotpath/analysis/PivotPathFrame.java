package pivotpath.analysis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import multifaceted.Util;

public class PivotPathFrame {
	private long timestamp;
	private BufferedImage image;
	private Point2D mousePosition;
	private Point2D gazePosition;
	private ArrayList<Double> elementScoreList;
	private ArrayList<Point2D> elementPositionList;
	public PivotPathFrame(long timestamp)
	{
		this.timestamp = timestamp;
		this.image = null;
		this.mousePosition = null;
		this.gazePosition = null;
		this.elementPositionList = null;
		this.elementScoreList = null;
	}
	public PivotPathFrame(	long timestamp,
							BufferedImage image,
							Point2D mousePosition,
							Point2D gazePosition,
							ArrayList<Double> elementScoreList,
							ArrayList<Point2D> elementPositionList)
	{
		this.timestamp = timestamp;
		this.image = image;
		this.mousePosition = mousePosition;
		this.gazePosition = gazePosition;
		this.elementScoreList = elementScoreList;
		this.elementPositionList = elementPositionList;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public Point2D getMousePosition() {
		return mousePosition;
	}

	public void setMousePosition(Point2D mousePosition) {
		this.mousePosition = mousePosition;
	}

	public Point2D getGazePosition() {
		return gazePosition;
	}

	public void setGazePosition(Point2D gazePosition) {
		this.gazePosition = gazePosition;
	}

	public ArrayList<Double> getElementScoreList() {
		return elementScoreList;
	}


	public ArrayList<Point2D> getElementPositionList() {
		return elementPositionList;
	}

	public void addElement(Point2D position, double score)
	{
		if(this.elementScoreList == null)
		{
			this.elementScoreList = new ArrayList<Double>();
		}
		
		if(this.elementPositionList == null)
		{
			this.elementPositionList = new ArrayList<Point2D>();
		}
		this.elementPositionList.add(position);
		this.elementScoreList.add(score);
	}
	
	public void render(Graphics2D g)
	{
		
		int originX =100;
		int originY = 100;
		g.translate(originX, originY);
		double scaleFactor = 1.5; 
		g.setColor(Color.black);
		g.drawString("Time: "+this.timestamp,-50, -50);
		if(this.image != null)
		{
			g.drawImage(this.image, 0,0,(int)( this.image.getWidth()/scaleFactor),(int) (this.image.getHeight()/scaleFactor), 0, 0, this.image.getWidth(), this.image.getHeight(), null);
		}
		if(this.mousePosition != null)
		{
			Util.drawCircle((int)this.mousePosition.getX(), (int)this.mousePosition.getY(), Color.red, g);
		}
		
		if(this.gazePosition != null)
		{
			Util.drawCircle((int)this.gazePosition.getX(), (int)this.gazePosition.getY(), Color.blue, g);
		}
	}
	
}
