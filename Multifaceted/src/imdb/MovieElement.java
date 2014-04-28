package imdb;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Point2D;

import multifaceted.layout.PivotElement;
import multifaceted.layout.PivotLabel;

public class MovieElement extends PivotElement{
	public static final int OFFSET_LABEL=10;
	
	private PivotLabel ratingLabel;
	private double rating;
	
	private PivotLabel yearLabel;
	private int year;
	public MovieElement(String id, String displayString,double rating,int year) {
		super(id, displayString, new Point2D.Double(0, 0));
		this.rating = rating;
		this.ratingLabel = new PivotLabel(String.format("%.2f", rating), false);
		
		this.year = year;
		this.yearLabel = new PivotLabel(this.year+"",false);
	}
	public double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	public PivotLabel getRatingLabel() {
		return ratingLabel;
	}
	public void setRatingLabel(PivotLabel ratingLabel) {
		this.ratingLabel = ratingLabel;
	}
	public PivotLabel getYearLabel() {
		return yearLabel;
	}
	public void setYearLabel(PivotLabel yearLabel) {
		this.yearLabel = yearLabel;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	private void renderExtralabel(Graphics2D g)
	{
		double theta = this.label.getRotationAngle();
		double x= this.label.x - (this.label.h /2)* Math.sin(theta)+(this.label.w/2) * Math.cos(theta);
		double y = this.label.y+(this.label.h/2)* Math.cos(theta)+(this.label.w/2)*Math.sin(theta)+OFFSET_LABEL;
		this.ratingLabel.x = x;
		this.ratingLabel.y = y;
		this.ratingLabel.setColor(Color.white);
		this.ratingLabel.render(g);
		
		Toolkit tool = Toolkit.getDefaultToolkit();		 
		Image icon = tool.getImage("images/star.png");
		int imageWidth = icon.getWidth(null);
		int imageHeight = icon.getHeight(null);
		
		int w =(int) (imageWidth * this.ratingLabel.h / imageHeight); 
				
		g.drawImage(icon,(int)( this.ratingLabel.x+this.ratingLabel.w/2),(int)( this.ratingLabel.y-this.ratingLabel.h/2)
				,w, (int) this.ratingLabel.h, null);
		
		this.yearLabel.x = this.ratingLabel.x;
		this.yearLabel.y = this.ratingLabel.y+this.ratingLabel.h+OFFSET_LABEL;
		
		this.yearLabel.render(g);
	}
	
	private void drawPoint(double x, double y, Graphics2D g, Color c)
	{
		g.setColor(c);
		int radius = 5;
		g.fillOval((int)x-radius,(int) y-radius, 2*radius, 2*radius);
	}
	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		super.render(g);

		renderExtralabel(g);
	}

}
