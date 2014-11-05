package pivotpath;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.ArrayList;

import multifaceted.Util;
import eyetrack.EyeTrackerItem;


class ActorInfoBit extends LabelInfoBit
{
	public ActorInfoBit(String label)
	{
		super(label);
	}
	public ActorInfoBit(String label, String id)
	{
		super(label, id);
	}
	@Override
	public Line2D[] getEdgeAnchors() {
		return new Line2D.Double[]{new Line2D.Double(getWidth()/2,0,getWidth()/2,-100),
				new Line2D.Double(getWidth()/2,getHeight(),getWidth()/2,getHeight()+100)};
		
	}
	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return EyeTrackerItem.TYPE_ACTOR;
	}
}
class StarRating extends LabelInfoBit
{
	private MovieInfoBit movie;
	private double rating;
	public StarRating(MovieInfoBit movie, double rating)
	{
		super(movie.label+":"+rating, "R"+movie.getId());
		this.movie = movie;
		this.rating = rating;
	}

	public MovieInfoBit getMovie() {
		return movie;
	}

	public double getRating() {
		return rating;
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return EyeTrackerItem.TYPE_MOVIE_STAR_RATING;
	}
	@Override
	public void render(Graphics2D g, boolean hovered) {
		// TODO Auto-generated method stub
		if (hovered)
			g.setColor(hoveredColor);
		else
			g.setColor(color);
		g.fillRect(0, 0, (int)getWidth(), (int)getHeight());
		
		g.setColor(Color.gray);
		int width =g.getFontMetrics().stringWidth(""+this.rating);
		
		g.drawString(""+this.rating, 0,(int)scale*10);
		
		int[] starx = new int[]{0,4,5,6,10,7,8,5,2,3};
		int[] stary = new int[]{4,4,0,4,4,6,10,8,10,6};
		for (int i=0; i<starx.length; i++)
		{
			starx[i] += width;
		}
		
		g.setColor(Color.gray);
		g.drawPolygon(starx, stary, 10);
		g.setColor(Color.yellow);
		g.fillPolygon(starx, stary, 10);
		
	}
	 @Override
	public double getWidth() {

			if (w < 0)
			{
				Canvas c = new Canvas();
				font = font.deriveFont((float)(scale*10.));
				w = c.getFontMetrics(font).stringWidth(""+this.rating)+17;	
				return w;
			}
			return w;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{Movie:"+movie+", star"+this.rating+"}";
	}
}
class MovieInfoBit extends LabelInfoBit
{
	public static final int MAX_LEN =32;
	private StarRating starRating;
	public MovieInfoBit(String label)
	{
		super(label.split("\t")[0], label.split("\t")[2]);
		double rating = java.lang.Double.parseDouble(label.split("\t")[1]);
		this.starRating = new StarRating(this, rating);
	
		scale = 1.5;
	}
	
	public StarRating getStarRating() {
		return starRating;
	}

	public void setStarRating(StarRating starRating) {
		this.starRating = starRating;
	}
	
	@Override
	public InfoBit[] getAdditionalInfoBit() {
		// TODO Auto-generated method stub
		return new InfoBit[]{this.starRating};
	}

	@Override
	public Line2D[] getEdgeAnchors() {
		return new Line2D.Double[]{new Line2D.Double(0,5,-100,5), 
				new Line2D.Double(getWidth(),5,getWidth()+100,5)};
		
	}
	
	@Override
	public void render(Graphics2D g, boolean hovered)
	{
		if (hovered || isConnectionHovered)
			g.setColor(hoveredColor);
		else
			g.setColor(color);
		
		g.setFont(font.deriveFont((float)(scale*10.)));
		w = g.getFontMetrics().stringWidth(label)+g.getFontMetrics().stringWidth("ww");		
		
		g.fillRect(0, 0, (int)getWidth(), (int)getHeight());
		
		g.setColor(new Color(0,0,0,200));
		drawString(g);

	}
	public void drawString(Graphics2D g)
	{
		String[] labelLines = getMultilineLabels();
		for(int i=0;i<labelLines.length;i++)
		{
			g.drawString(labelLines[i], g.getFontMetrics().stringWidth("w"), (int)(getHeight()*0.8* (i+1)/ labelLines.length));
		}
		
		
	}
	private String[] getMultilineLabels()
	{
		if(label.length()< MAX_LEN)
		{
			return new String[]{label};
		}
		else
		{
			String[] split = label.split(" ");
			ArrayList<String> lines = new ArrayList<String>();
			String line ="";
			for(String str: split)
			{
				if(line.length() + str.length() < MAX_LEN)
				{
					line+= " "+str;
				}
				else
				{
					lines.add(line);
					line = str;
				}
			}
			lines.add(line);
			String[] lineArray = new String[lines.size()];
			for(int i=0;i<lineArray.length;i++)
			{
				lineArray[i] = lines.get(i);
			}
			
			return lineArray;
		}
	}
	@Override
	public double getWidth() {
		
	
		Canvas c = new Canvas();
		font = font.deriveFont((float)(scale*10.));
		String[] labelLines = getMultilineLabels();
		String maxLengthLine="";
		for(String line: labelLines)
		{
			if(line.length()> maxLengthLine.length())
			{
				maxLengthLine = line;
			}
		}
		w = c.getFontMetrics(font).stringWidth(maxLengthLine)+c.getFontMetrics(font).stringWidth("ww");	
		return w;
		
	}

	@Override
	public double getHeight() {
		String[] labelLines = getMultilineLabels();
		return 10*scale*labelLines.length;
	}
	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return EyeTrackerItem.TYPE_MOVIE;
	}
}

class DirectorInfoBit extends LabelInfoBit
{
	public DirectorInfoBit(String label)
	{
		super(label);
	}
	public DirectorInfoBit(String label, String id)
	{
		super(label, id);
	}
	@Override
	public Line2D[] getEdgeAnchors() {
		return new Line2D.Double[]{new Line2D.Double(getWidth()/2,0,getWidth()/2,-100),
				new Line2D.Double(getWidth()/2,getHeight(),getWidth()/2,getHeight()+100)};
		
	}
	@Override
	public void setScale(double d) {
		// TODO Auto-generated method stub
		
		super.setScale(d*1.5);
	}
	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return EyeTrackerItem.TYPE_DIRECTOR;
	}
}

class GenreInfoBit extends LabelInfoBit
{
	public GenreInfoBit(String label)
	{
		super(label);
	}
	public GenreInfoBit(String label, String id)
	{
		super(label, id);
	}
	@Override
	public Line2D[] getEdgeAnchors() {
		return new Line2D.Double[]{new Line2D.Double(getWidth()/2,0,getWidth()/2,-100),
				new Line2D.Double(getWidth()/2,getHeight(),getWidth()/2,getHeight()+100)};
		
	}
	@Override
	public void setScale(double d) {
		// TODO Auto-generated method stub
		super.setScale(d*0.75);
	}
	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return EyeTrackerItem.TYPE_GENRE;
	}
}


public class MoviePivotPaths extends GeneralPivotPaths
{

	public MoviePivotPaths(String main, Double mainBounds) {
		super(main, mainBounds);
	}
	
	@Override
	protected InfoBit createInfoBit(String facetName, String value, String id)
	{
		InfoBit b = null;
		if (facetName.equals("actor"))
			b = new ActorInfoBit(value, id);
		else if (facetName.equals("director"))
			b = new DirectorInfoBit(value, id);
		else if (facetName.equals("genre"))
			b = new GenreInfoBit(value, id);
		else
			b = new LabelInfoBit(value, id);
		b.facetName = facetName;
		return b;
	}
	
	@Override
	protected InfoBit createDataInfoBit(String value)
	{
		InfoBit b = new MovieInfoBit(value);
		return b;		
	}
	
	@Override
	public Line2D[] getAnchors(InfoBit b1, InfoBit b2)
	{
		Line2D[] a1 = b1.getGroup().getItemEdgeAnchor(b1);
		Line2D[] a2 = b2.getGroup().getItemEdgeAnchor(b2);
		
		double mindd = 999999999;
		Line2D l1 = null,l2 = null;
		for (int l=0; l<a1.length; l++)
			for (int n=0; n<a2.length; n++)
			{
				double dd= Math.abs(a1[l].getY2() - a2[n].getY2());
				if (dd < mindd)
				{
					mindd = dd;
					l1 = a1[l];
					l2 = a2[n];
				}
			}		
		return new Line2D[]{l1,l2};
	}
}


