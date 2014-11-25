package eyetrack.probability;

import java.awt.Point;

import eyetrack.EyeTrackerViewer;

public interface EyeTrackerProbabilityViewer extends EyeTrackerViewer{
	public boolean isProbabilityDisabled();
	public boolean isWithinScreen(Point screenPoint);
}
