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
	public static int ACTION_ACTOR_DISCONNECTED =1;
	public static int ACTION_MOVIE_CONNECTED =2;
	public static int ACTION_MOVIE_DISCONNECTED =3;
	public static int ACTION_DIRECTOR_CONNECTED =4;
	public static int ACTION_DIRECTOR_DISCONNECTED =5;
	public static int ACTION_GENRE_CONNECTED =6;
	public static int ACTION_GENRE_DISCONNECTED =7;
	public static int ACTION_ACTOR_SELF=8;
	public static int ACTION_ACTOR_SAME_GROUP=9;
	public static int ACTION_ACTOR_OTHER=10;
	public static int ACTION_MOVIE_SELF=11;
	public static int ACTION_MOVIE_SAME_GROUP=12;
	public static int ACTION_MOVIE_OTHER=13;
	public static int ACTION_DIRECTOR_SELF=14;
	public static int ACTION_DIRECTOR_SAME_GROUP=15;
	public static int ACTION_DIRECTOR_OTHER=16;
	public static int ACTION_GENRE_SELF=17;
	public static int ACTION_GENRE_SAME_GROUP=18;
	public static int ACTION_GENRE_OTHER=19;
	
	public static int SAME_TYPE_RELATION_SELF =0;
	public static int SAME_TYPE_RELATION_SAME_GROUP =1;
	public static int SAME_TYPE_RELATION_OTHER =2;
	
	public static double[][] PROBABILITY_TABLE={
//		{0, 0.63, 0, 0.2,0, 0.07,0,0.1},
//		{0, 0.92, 0.043, 0.007,0, 0.01,0,0.01},
//		{0.148, 0.022, 0, 0.77,0.024, 0.016,0.019,0.001},
//		{0, 0.1, 0.081, 0.009,0, 0.74,0,0.007},
//		{0, 0.1, 0.0056, 0.004,0, 0.02,0,0.81}
//		{0, 1, 0, 1,0, 0.411764705882353,0,0.588235294117647},
//		{0, 1, 0.86, 0.14,0, 0.5,0,0.5},
//		{0.870588235294118, 0.129411764705882, 0, 1,0.4, 0.266666666666667,0.316666666666667,0.0166666666666667},
//		{0, 1, 0.9, 0.1,0, 0.990629183400268,0,0.00937081659973226},
//		{0, 1, 0.583333333333333, 0.416666666666667,0, 0.0240963855421687,0,0.975903614457831},
		{0, 1, 0, 1,0, 0.411764706,0,0.588235294,0,0,0,0,0,0,0,0,0,0,0,0},
		{0, 1, 0.86, 0.14,0, 0.5,0,0.5,1,0.75,0.2,0,0,0,0,0,0,0,0,0},
		{0.870588235, 0.129411765, 0, 1,0.4, 0.266666667,0.316666667,0.016666667,0,0,0,1,0.75,0.2,0,0,0,0,0,0},
		{0, 1, 0.9, 0.1,0, 0.990629183,0,0.009370817,0,0,0,0,0,0,0.99,0.74,0.19,0.01,0.007,0.002},
		{0, 1, 0.583333333, 0.416666667,0, 0.024096386,0,0.975903614,0,0,0,0,0,0,0.03,0.022,0.006,0.97,0.72,0.19},

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
		}
		return ACTION_INVALID;
	}
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
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{"+previousItem+", "+action+", "+normalizedProbability+"}";
	}
}
