package stat;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import multifaceted.Util;

public class ErrorBarGlyph implements Comparable<ErrorBarGlyph> {
	private ArrayList<Double> scoreList;
	private int relevance;
	private int subtask;
	
	public ErrorBarGlyph(int relevance, int subtask)
	{
		this.scoreList = new ArrayList<Double>();
		this.relevance = relevance;
		this.subtask = subtask;
	}
	public int getRelevance() {
		return relevance;
	}
	public void setRelevance(int relevance) {
		this.relevance = relevance;
	}
	public int getSubtask() {
		return subtask;
	}
	public void setSubtask(int subtask) {
		this.subtask = subtask;
	}
	public void addScore( double score)
	{
		scoreList.add(score);
	}
	
	
	public double getAverage()
	{
		double sum =0;
		for(Double value:this.scoreList)
		{
			sum+= value;
		}
		double average = sum / this.scoreList.size();
		return average;
	}
	
	public double getStandardDeviation()
	{
		int n = this.scoreList.size();
		if(n>1)
		{
			double average = getAverage();
			
			double varianceSum = 0;
			for(Double value:this.scoreList)
			{
				varianceSum+= (value - average) *(value - average) ;
			}
			double variance = varianceSum/(n-1); // Standard Deviation with Bassel's correction
			
			double standardDeviation = Math.sqrt(variance);
			return standardDeviation;
		}
		else
		{
			return 0;
		}
		
	}
	
	public double getStandardMeanError()
	{	
		return getStandardDeviation() / Math.sqrt((double) this.scoreList.size()); 
	}
	
	public static ErrorBarGlyph getInstance(int relevance, int subtask)
	{
		return new ErrorBarGlyph(relevance, subtask);
	}
	public Point draw(Graphics2D g, int originX, int originY, int maxYLength, int maxRelevanceWidth, int maxTaskWidth, int factor)
	{
		Color previousColor = g.getColor();
		int radius =5;
		
		int x = originX+ 
				getRelevance()*maxRelevanceWidth+ 
				(getSubtask()-1)* maxTaskWidth
				+ radius+(int)(Math.random()*(maxTaskWidth-2*radius));
		
		int y = originY - (int) (getAverage()*factor * maxYLength);
		
		g.setColor(Color.black);
		
		Util.drawCircle(x, y,radius, Color.black, g);
		
		double errorBar = getStandardMeanError()*factor*maxYLength;
		
		g.drawLine(x, y, x, y+(int) errorBar);		
		g.drawLine(x-radius, y+(int) errorBar, x+radius, y+(int) errorBar);
		
		g.drawLine(x, y, x, y-(int) errorBar);
		g.drawLine(x-radius, y-(int) errorBar, x+radius, y-(int) errorBar);
		
		g.setColor(previousColor);
		
		return new Point(x,y);
		
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{relevance: "+relevance+", subtask:"+subtask+", average:"+getAverage()+", sd:"+getStandardDeviation()+", error:"+getStandardMeanError()+"}";
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj instanceof ErrorBarGlyph)
		{
			ErrorBarGlyph otherObj = (ErrorBarGlyph) obj;
			return otherObj.getRelevance() == this.getRelevance() && otherObj.getSubtask() == this.getSubtask();
		}
		else
		{
			return super.equals(obj);
		}
		
	}
	@Override
	public int compareTo(ErrorBarGlyph o) {
		// TODO Auto-generated method stub
		int difference = this.getRelevance()- o.getRelevance();
		if(difference == 0)
		{
			return this.getSubtask() - o.getSubtask();
		}
		else
		{
			return difference;
		}
		
	}
}
