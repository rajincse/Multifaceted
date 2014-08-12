package eyerecommend.item;

import java.awt.geom.Point2D;


public interface RecommendItem{
	public int getIndex();
	
	public double getPosteriorProbability(Point2D gazePosition);
	
	public double getPriorProbability();
	
}
