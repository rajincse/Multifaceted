package eyerecommend;

import java.awt.Point;

public interface EyeTrackerDataReceiver {
	public void processGaze(Point gazePoint, double pupilDiameter);
}
