package eyetracker.instrument;

import java.awt.Point;

public interface EyeFixationDataReceiver {	
	public static final String METHOD_GAZE = "Gaze";
    public static final String METHOD_FIXATION = "Fixation";
	
	public void processGaze(Point gazePoint, double pupilDiameter);
	public void processFixation(Point gazePoint, double pupilDiameter, String type);
}
