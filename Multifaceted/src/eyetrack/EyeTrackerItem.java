package eyetrack;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import eyetrack.probability.StateAction;

public interface EyeTrackerItem{
	public static int TYPE_INVALID =-1;
	public static int TYPE_ACTOR =StateAction.STATE_ACTOR;
	public static int TYPE_MOVIE =StateAction.STATE_MOVIE;
	public static int TYPE_DIRECTOR =StateAction.STATE_DIRECTOR;
	public static int TYPE_GENRE =StateAction.STATE_GENRE;
	public static int TYPE_MOVIE_STAR_RATING=StateAction.STATE_MOVIE_STAR_RATING;
	public int getType();
	public String getId ();
	public void setScore(double score);
	public double getScore();
	public double getGazeScore(Point2D gazePosition, double zoomFactor);
	public double getStoredGazeScore();
	public double getProbabilityScore();
	public ArrayList<StateAction> getActions(ArrayList<StateAction> stateActions);
	//Debug
	public void setGazeScore(double gazeScore);
	public void setProbability(double probability);
	public void setNextProbability(double probability);
	//Mouse Hovering
	public boolean isHovered();
}
