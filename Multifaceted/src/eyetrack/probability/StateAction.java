package eyetrack.probability;

import eyetrack.EyeTrackerItem;

public class StateAction {
	public static int STATE_INVALID =-1;
	public static int STATE_INIT =0;
	public static int STATE_ACTOR = 1;
	public static int STATE_MOVIE =2;
	public static int STATE_DIRECTOR =3;
	public static int STATE_GENRE =4;
	
	public static int ACTION_INVALID =-1;
	public static int ACTION_ACTOR_CONNECTED =0;
	public static int ACTION_ACTOR_DISCONNECTED =1;
	public static int ACTION_MOVIE_CONNECTED =2;
	public static int ACTION_MOVIE_DISCONNECTED =3;
	public static int ACTION_DIRECTOR_CONNECTED =4;
	public static int ACTION_DIRECTOR_DISCONNECTED =5;
	public static int ACTION_GENRE_CONNECTED =6;
	public static int ACTION_GENRE_DISCONNECTED =7;
	
	public static double[][] PROBABILITY_TABLE={
		{0, 0.63, 0, 0.2,0, 0.07,0,0.1},
		{0, 0.92, 0.043, 0.007,0, 0.01,0,0.01},
		{0.148, 0.022, 0, 0.77,0.024, 0.016,0.019,0.001},
		{0, 0.1, 0.081, 0.009,0, 0.74,0,0.007},
		{0, 0.1, 0.0056, 0.004,0, 0.02,0,0.81}
	};
	
	public static int getAction(EyeTrackerItem  currentElement, boolean connected) {
		
		if(connected)
		{
			if(currentElement.getType() == EyeTrackerItem.TYPE_ACTOR)
			{
				return ACTION_ACTOR_CONNECTED;
			}
			else if(currentElement.getType() == EyeTrackerItem.TYPE_MOVIE)
			{
				return ACTION_MOVIE_CONNECTED;
			}
			else if(currentElement.getType() == EyeTrackerItem.TYPE_DIRECTOR)
			{
				return ACTION_DIRECTOR_CONNECTED;
			}
			else if(currentElement.getType() == EyeTrackerItem.TYPE_GENRE)
			{
				return ACTION_GENRE_CONNECTED;
			}
		}
		else
		{
			if(currentElement.getType() == EyeTrackerItem.TYPE_ACTOR)
			{
				return ACTION_ACTOR_DISCONNECTED;
			}
			else if(currentElement.getType() == EyeTrackerItem.TYPE_MOVIE)
			{
				return ACTION_MOVIE_DISCONNECTED;
			}
			else if(currentElement.getType() == EyeTrackerItem.TYPE_DIRECTOR)
			{
				return ACTION_DIRECTOR_DISCONNECTED;
			}
			else if(currentElement.getType() == EyeTrackerItem.TYPE_GENRE)
			{
				return ACTION_GENRE_DISCONNECTED;
			}
		}
		
		return ACTION_INVALID;
		
	}
	
	private EyeTrackerItem previousItem;
	private int action = ACTION_INVALID;
	private double normalizedProbability = 0;
	
	public StateAction(EyeTrackerItem previousItem)
	{
		this.previousItem = previousItem;
	}
	public StateAction(EyeTrackerItem previousItem, double normalizaedProbability)
	{
		this.previousItem = previousItem;
		this.normalizedProbability = normalizaedProbability;
	}
	public EyeTrackerItem getPreviousItem() {
		return previousItem;
	}
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public double getNormalizedProbability() {
		return normalizedProbability;
	}
	public void setNormalizedProbability(double normalizedProbability) {
		this.normalizedProbability = normalizedProbability;
	}
	
	
}
