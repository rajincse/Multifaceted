package imdb.analysis;

import java.awt.Color;
import java.awt.Graphics2D;

import perspectives.base.ObjectInteraction;
import perspectives.base.ObjectInteraction.RectangleItem;
import perspectives.util.Rectangle;


public class HeatMapVisualItem extends RectangleItem{
	private long startTime;
	private long endTime;
	private AnalysisItem item;
	private double totalScore;
	private int totalCount;
	public HeatMapVisualItem(AnalysisItem item, long time) {
		new ObjectInteraction().super(new Rectangle(0, 0, 0, 0));
		this.item = item;
		this.startTime = time;
		this.endTime = time;
		totalScore =0;
		totalCount=0;
	}
	public void setRectangleInfo(double x, double y, double w, double h)
	{
		this.r.x = x;
		this.r.y = y;
		this.r.w = w;
		this.r.h = h;
	}
	
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public AnalysisItem getItem() {
		return item;
	}
	public void setItem(AnalysisItem item) {
		this.item = item;
	}
	public double getAverageScore() {
		return totalScore/totalCount;		
	}
	public double getTotalScore()
	{
		return totalScore;
	}
	public void setTotalScore(double score)
	{
		this.totalScore  = score;
		updateColor();
	}
	
	public void addScore(double score)
	{
		totalScore+= score;
		totalCount++;
		updateColor();
		
	}
	private void updateColor() {
		if(totalScore >0)
		{
			this.r.setColor(HeatMapAnalysisViewer.getHeatMapcolor(getTotalScore()));
		}
	}
	
	public void render(Graphics2D g)
	{
		if(totalScore >0)
		{
			this.r.render(g);
		}
	}
	
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public String getDisplayString()
	{
		return item.getName()+", "+String.format("%.2f",getTotalScore());
	}
	public String getDisplayString(long fromTime)
	{
		long t1 = (startTime-fromTime)/1000;
		long t2 = (endTime - fromTime)/1000;
				
		return item.getName()+", "+String.format("%.2f",getTotalScore())+":["+t1+", "+t2+"]";
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return item.getName()+", "+String.format("%.2f",getTotalScore())+":["+startTime+", "+endTime+"]";
	}
	
}
