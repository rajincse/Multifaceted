package eyetrack;

import java.awt.geom.Point2D;

public interface EyeTrackerItem {
	public void setScore(double score);
	public double getScore();
	public double computeScore(Point2D gazePosition, double deviation);
}
