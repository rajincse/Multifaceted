package pivotpath.analysis;

import imdb.analysis.HeatMapAnalysisViewer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class PivotPathFrame {
	private static final double SCALE =1.5;
	private long timestamp;
	private String imageName; 
	private BufferedImage image;
	private Point2D mousePosition;
	private Point2D gazePosition;
	private int gazeRadius;
	private ArrayList<Double> elementScoreList;
	private ArrayList<Point2D> elementPositionList;
	public PivotPathFrame(long timestamp)
	{
		this.timestamp = timestamp;
		this.imageName = "";
		this.image = null;
		this.mousePosition = null;
		this.gazePosition = null;
		this.elementPositionList = null;
		this.elementScoreList = null;
	}
	public PivotPathFrame(	long timestamp,
							String imageName,
							BufferedImage image,
							Point2D mousePosition,
							Point2D gazePosition,
							ArrayList<Double> elementScoreList,
							ArrayList<Point2D> elementPositionList)
	{
		this.timestamp = timestamp;
		this.imageName = imageName;
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
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public void setElementScoreList(ArrayList<Double> elementScoreList) {
		this.elementScoreList = elementScoreList;
	}
	public void setElementPositionList(ArrayList<Point2D> elementPositionList) {
		this.elementPositionList = elementPositionList;
	}
	public int getGazeRadius() {
		return gazeRadius;
	}
	public void setGazeRadius(int gazeRadius) {
		this.gazeRadius = gazeRadius;
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj instanceof PivotPathFrame)
		{
			PivotPathFrame frame = (PivotPathFrame) obj;
			if(frame.getTimestamp() == this.timestamp)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return super.equals(obj);
		}
		
	}
	public void render(Graphics2D g)
	{
		
		int originX =100;
		int originY = 100;
		g.translate(originX, originY);
		g.setColor(Color.black);
		g.drawString("Time: "+this.timestamp,-50, -50);
		if(this.image != null)
		{
			g.drawImage(this.image, 0,0,(int)( this.image.getWidth()/SCALE),(int) (this.image.getHeight()/SCALE), 0, 0, this.image.getWidth(), this.image.getHeight(), null);
		}
		if(this.mousePosition != null)
		{
			renderMouse(g, (int)(this.mousePosition.getX()/SCALE), (int)(this.mousePosition.getY()/SCALE));
		}
		
		if(this.gazePosition != null)
		{
			renderGaze(g, (int)(this.gazePosition.getX()/SCALE), (int)(this.gazePosition.getY()/SCALE));
		}
		
		if(this.elementPositionList != null && !this.elementPositionList.isEmpty() )
		{
			for(int i =0;i< this.elementPositionList.size();i++)
			{
				int colorLength = HeatMapAnalysisViewer.HEATMAP_COLOR.length;
				int colorIndex =colorLength -( i * (colorLength-1) /this.elementPositionList.size())-1;
				Color color = HeatMapAnalysisViewer.HEATMAP_COLOR[colorIndex];
				Point2D point = this.elementPositionList.get(i);
				renderElement(g, (int)(point.getX()/SCALE), (int) (point.getY()/SCALE), color);
			}
		}
	}
	private void renderElement(Graphics2D g, int x, int y, Color color)
	{
		g.translate(x, y);
//		int outerCircleRad = 15;
		
		g.translate(-13, -27);
		g.setColor(color);
		g.fillPolygon(	new int[]{11,11, 5,13,22,16,16,11},
						new int[]{ 6,16,16,27,16,16, 6, 6}, 
				8);
		g.translate(13, 27);
		g.translate(-x, -y);
	}
	private void renderMouse(Graphics2D g, int x, int y)
	{
		g.translate(x, y);
		g.setColor(Color.black);
		g.drawPolygon(	new int[]{0,1 ,4 ,9 ,11,7 ,12,0},
				new int[]{0,17,13,21,19,12,12,0}, 
				8);
		g.setColor(Color.white);
		g.fillPolygon(	new int[]{0,1 ,4 ,9 ,11,7 ,12,0},
						new int[]{0,17,13,21,19,12,12,0}, 
						8);
		g.translate(-x, -y);
	}
	
	private void renderGaze(Graphics2D g, int x, int y)
	{
		g.translate(x, y);
		int innerCircleRad = 5;
		int outerCircleRad = gazeRadius;
		g.setColor(Color.red);
		
		g.fillOval(-innerCircleRad,-innerCircleRad, 2*innerCircleRad, 2* innerCircleRad);
		g.drawOval(-outerCircleRad,-outerCircleRad, 2*outerCircleRad, 2* outerCircleRad);
		
		g.translate(-x, -y);
	}
	
}
