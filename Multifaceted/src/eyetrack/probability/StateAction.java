package eyetrack.probability;

import eyetrack.EyeTrackerItem;

public class StateAction {
	public static int STATE_INVALID =-1;
	public static int STATE_INIT =0;
	public static int STATE_ACTOR = 1;
	public static int STATE_MOVIE =2;
	public static int STATE_DIRECTOR =3;
	public static int STATE_GENRE =4;
	public static int STATE_MOVIE_STAR_RATING =5;
	
	public static int ACTION_INVALID =-1;
	public static int ACTION_ACTOR_CONNECTED =0;
	public static int ACTION_ACTOR_HOVERED =1;
	public static int ACTION_ACTOR_DISCONNECTED =2;
	public static int ACTION_MOVIE_CONNECTED =3;
	public static int ACTION_MOVIE_HOVERED =4;
	public static int ACTION_MOVIE_DISCONNECTED =5;
	public static int ACTION_STAR_RATING_CONNECTED =6;
	public static int ACTION_STAR_RATING_DISCONNECTED =7;
	public static int ACTION_DIRECTOR_CONNECTED =8;
	public static int ACTION_DIRECTOR_HOVERED =9;
	public static int ACTION_DIRECTOR_DISCONNECTED =10;
	public static int ACTION_GENRE_CONNECTED =11;
	public static int ACTION_GENRE_HOVERED =12;
	public static int ACTION_GENRE_DISCONNECTED =13;
	public static int ACTION_ACTOR_SELF=14;
	public static int ACTION_ACTOR_SAME_GROUP=15;
	public static int ACTION_ACTOR_OTHER=16;
	public static int ACTION_MOVIE_SELF=17;
	public static int ACTION_MOVIE_SAME_GROUP=18;
	public static int ACTION_MOVIE_OTHER=19;
	public static int ACTION_DIRECTOR_SELF=20;
	public static int ACTION_DIRECTOR_SAME_GROUP=21;
	public static int ACTION_DIRECTOR_OTHER=22;
	public static int ACTION_GENRE_SELF=23;
	public static int ACTION_GENRE_SAME_GROUP=24;
	public static int ACTION_GENRE_OTHER=25;
	
	public static int SAME_TYPE_RELATION_SELF =0;
	public static int SAME_TYPE_RELATION_SAME_GROUP =1;
	public static int SAME_TYPE_RELATION_OTHER =2;
	
	public static double[][] PROBABILITY_TABLE={
//		{0, 1, 0, 0.8,0, 0.2,0,0.411764706,0,0.588235294,0,0,0,0,0,0,0,0,0,0,0,0},
//		{0, 1, 0.688, 0.112,0.172, 0.028,0,0.5,0,0.5,1,0.75,0.2,0,0,0,0,0,0,0,0,0},
//		{0.870588235, 0.129411765, 0, 0.8,0, 0.2,0.4,0.266666667,0.316666667,0.016666667,0,0,0,1,0.75,0.2,0,0,0,0,0,0},
//		{0, 1, 0.72, 0.08,0.18, 0.02,0,0.990629183,0,0.009370817,0,0,0,0,0,0,0.99,0.74,0.19,0.01,0.007,0.002},
//		{0, 1, 0.466666666, 0.333333334,0.116666667, 0.083333333,0,0.024096386,0,0.975903614,0,0,0,0,0,0,0.03,0.022,0.006,0.97,0.72,0.19},
//		{0.870588235, 0.129411765, 0, 0.8,0, 0.2,0.4,0.266666667,0.316666667,0.016666667,0,0,0,1,0.75,0.2,0,0,0,0,0,0},
		{0, 0,1, 0, 0, 0.8,0, 0.2,0,0, 0.411764706,0,0, 0.588235294,0,0,0,0,0,0,0,0,0,0,0,0},
		{0, 0,1, 0.688, 1, 0.112,0.172, 0.028,0,0, 0.5,0,0, 0.5,1,0.75,0.2,0,0,0,0,0,0,0,0,0},
		{0.870588235, 1,0.129411765, 0, 0, 0.8,0, 0.2,0.4,0.5, 0.266666667,0.316666667,0.5, 0.016666667,0,0,0,1,0.75,0.2,0,0,0,0,0,0},
		{0, 0,1, 0.72, 1, 0.08,0.18, 0.02,0,0, 0.990629183,0,0, 0.009370817,0,0,0,0,0,0,0.99,0.74,0.19,0.01,0.007,0.002},
		{0, 0,1, 0.466666666, 1, 0.333333334,0.116666667, 0.083333333,0,0, 0.024096386,0,0, 0.975903614,0,0,0,0,0,0,0.03,0.022,0.006,0.97,0.72,0.19},
		{0, 0,0.129411765, 0, 0, 0.8,0, 0.2,0,0, 0.266666667,0,0, 0.016666667,0,0,0,1,0,0.2,0,0,0,0,0,0},

	};
	public static int getSameTypeAction(EyeTrackerItem currentElement, int typeOfRelation)
	{
		if(typeOfRelation == SAME_TYPE_RELATION_SELF)
		{
			if(currentElement.getType() == EyeTrackerItem.TYPE_ACTOR)
			{
				return ACTION_ACTOR_SELF;
			}
			else if(currentElement.getType() == EyeTrackerItem.TYPE_MOVIE)
			{
				return ACTION_MOVIE_SELF;
			}
			else if(currentElement.getType() == EyeTrackerItem.TYPE_DIRECTOR)
			{
				return ACTION_DIRECTOR_SELF;
			}
			else if(currentElement.getType() == EyeTrackerItem.TYPE_GENRE)
			{
				return ACTION_GENRE_SELF;
			}
			else if(currentElement.getType() == EyeTrackerItem.TYPE_MOVIE_STAR_RATING)
			{
				return ACTION_MOVIE_SELF;
			}
		}
		else if(typeOfRelation == SAME_TYPE_RELATION_SAME_GROUP)
		{
			if(currentElement.getType() == EyeTrackerItem.TYPE_ACTOR)
			{
				return ACTION_ACTOR_SAME_GROUP;
			}
			else if(currentElement.getType() == EyeTrackerItem.TYPE_MOVIE)
			{
				return ACTION_MOVIE_SAME_GROUP;
			}
			else if(currentElement.getType() == EyeTrackerItem.TYPE_DIRECTOR)
			{
				return ACTION_DIRECTOR_SAME_GROUP;
			}
			else if(currentElement.getType() == EyeTrackerItem.TYPE_GENRE)
			{
				return ACTION_GENRE_SAME_GROUP;
			}
			else if(currentElement.getType() == EyeTrackerItem.TYPE_MOVIE_STAR_RATING)
			{
				return ACTION_MOVIE_SAME_GROUP;
			}
		}
		else if(typeOfRelation == SAME_TYPE_RELATION_OTHER)
		{
			if(currentElement.getType() == EyeTrackerItem.TYPE_ACTOR)
			{
				return ACTION_ACTOR_OTHER;
			}
			else if(currentElement.getType() == EyeTrackerItem.TYPE_MOVIE)
			{
				return ACTION_MOVIE_OTHER;
			}
			else if(currentElement.getType() == EyeTrackerItem.TYPE_DIRECTOR)
			{
				return ACTION_DIRECTOR_OTHER;
			}
			else if(currentElement.getType() == EyeTrackerItem.TYPE_GENRE)
			{
				return ACTION_GENRE_OTHER;
			}
			else if(currentElement.getType() == EyeTrackerItem.TYPE_MOVIE_STAR_RATING)
			{
				return ACTION_MOVIE_OTHER;
			}
		}
		return ACTION_INVALID;
	}
	public static int getAction(EyeTrackerItem  currentElement, boolean connected, boolean previousElementHovered) {
		
		if(connected && previousElementHovered)
		{
			if(currentElement.getType() == EyeTrackerItem.TYPE_ACTOR)
			{
				return ACTION_ACTOR_HOVERED;
			}
			else if(currentElement.getType() == EyeTrackerItem.TYPE_MOVIE)
			{
				return ACTION_MOVIE_HOVERED;
			}
			else if(currentElement.getType() == EyeTrackerItem.TYPE_DIRECTOR)
			{
				return ACTION_DIRECTOR_HOVERED;
			}
			else if(currentElement.getType() == EyeTrackerItem.TYPE_GENRE)
			{
				return ACTION_GENRE_HOVERED;
			}
			else if(currentElement.getType() == EyeTrackerItem.TYPE_MOVIE_STAR_RATING)
			{
				return ACTION_MOVIE_HOVERED;
			}
		}
		else if(connected && !previousElementHovered)
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
			else if(currentElement.getType() == EyeTrackerItem.TYPE_MOVIE_STAR_RATING)
			{
				return ACTION_STAR_RATING_CONNECTED;
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
			else if(currentElement.getType() == EyeTrackerItem.TYPE_MOVIE_STAR_RATING)
			{
				return ACTION_STAR_RATING_DISCONNECTED;
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
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{"+previousItem+", "+action+", "+normalizedProbability+"}";
	}
}
