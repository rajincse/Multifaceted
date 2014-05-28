package imdb.analysis;

import perspectives.base.ObjectInteraction;
import perspectives.base.ObjectInteraction.RectangleItem;
import perspectives.util.Rectangle;

public class HeatMapCell extends RectangleItem{
	private AnalysisItem item;

	private double score;
	
	private ScreenShot screenShot;
	
	private int count;
	
	public HeatMapCell(AnalysisItem item, double score, ScreenShot screenShot)
	{
		new ObjectInteraction().super(new Rectangle(0, 0, 0, 0));
		this.item = item;
		this.score = score;
		this.screenShot = screenShot;
		this.count =1;
		this.r.setColor(HeatMapAnalysisViewer.getHeatMapcolor(this.score));
	}
	public AnalysisItem getItem() {
		return item;
	}

	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
		this.r.setColor(HeatMapAnalysisViewer.getHeatMapcolor(this.score));
	}
	public ScreenShot getScreenShot() {
		return screenShot;
	}
	public void setScreenShot(ScreenShot screenShot) {
		this.screenShot = screenShot;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public void setRectangleInfo(double x, double y, double w, double h)
	{
		this.r.x = x;
		this.r.y = y;
		this.r.w = w;
		this.r.h = h;
	}
	public static HeatMapCell createInstance(String id, String name, double score, String imageName, int x, int y)
	{
		ScreenShot screenShot  = new ScreenShot(imageName, x, y);
		AnalysisItem item = new AnalysisItem(id,name);
		HeatMapCell cell = new HeatMapCell(item, score, screenShot);
		return cell;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{ item:"+item.toString()+", score: "+String.format("%.2f", score)+", count:"+count+", screenShot:"+screenShot.toString()+"}";
	}
}
