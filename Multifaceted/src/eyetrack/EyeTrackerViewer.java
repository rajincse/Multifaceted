package eyetrack;

import java.awt.Point;
import java.awt.geom.AffineTransform;

public interface EyeTrackerViewer {
	public void gazeDetected(int x, int y);
	public Point getEyeTrackOffset();
	public AffineTransform getTransform();
	public double getZoom();
}
