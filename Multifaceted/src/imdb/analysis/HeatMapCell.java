package imdb.analysis;

public class HeatMapCell{
	private AnalysisItem item;

	private double score;
	
	private ScreenShot screenShot;
	
	
	public HeatMapCell(AnalysisItem item, double score, ScreenShot screenShot)
	{
		this.item = item;
		this.score = score;
		this.screenShot = screenShot;
	}
	public AnalysisItem getItem() {
		return item;
	}

	public double getScore() {
		return score;
	}
	
	public ScreenShot getScreenShot() {
		return screenShot;
	}
	public void setScreenShot(ScreenShot screenShot) {
		this.screenShot = screenShot;
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
		return "{ item:"+item.toString()+", score: "+String.format("%.2f", score)+", screenShot:"+screenShot.toString()+"}";
	}
}
