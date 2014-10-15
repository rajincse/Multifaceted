package eyetrack.probability;

import java.util.ArrayList;

import eyetrack.EyeTrackerItem;
import eyetrack.probability.StateAction;

public class ProbabilityManager {
	public static final double TOP_PERCENTILE =10;
	public static final double LEVITATION_LOWER_BOUND =0.5; // to make probabilty range from 0.5 to 1
	public static final int SCORE_ORIGINAL =0;
	public static final int SCORE_GAZE =1;
	public static final int SCORE_PROBABILITY =2;
	
	private double[][] probabilityTable;
	
	private ArrayList<EyeTrackerItem> originalPreviousElements;
	private ArrayList<EyeTrackerItem> gazePreviousElements;
	private ArrayList<EyeTrackerItem> probabilityPreviousElements;
	
	public ProbabilityManager()
	{
		this.probabilityTable =  StateAction.PROBABILITY_TABLE;
		this.originalPreviousElements = null;
	}

	public ArrayList<EyeTrackerItem> getOriginalPreviousElements()
	{
		return this.originalPreviousElements;
	}
	public ArrayList<EyeTrackerItem> getPreviousElementList(int scoreType)
	{
		if(scoreType== SCORE_ORIGINAL)
		{
			return originalPreviousElements;
		}
		else if (scoreType == SCORE_GAZE )
		{
			return gazePreviousElements;
		}
		else if (scoreType == SCORE_PROBABILITY )
		{
			return probabilityPreviousElements;
		}
		else
		{
			return null;
		}
	}
	public ArrayList<StateAction> getPreviousStateActions(int scoreType) {
		ArrayList<EyeTrackerItem> previousElementList = getPreviousElementList(scoreType);
		if(previousElementList != null)
		{
			double totalScore = 0;
			for(EyeTrackerItem item: previousElementList)
			{
				double score =item.getScore(); 
				totalScore += score;
			}
			
			ArrayList<StateAction> previousStateActions = new ArrayList<StateAction>();
			for(EyeTrackerItem item: previousElementList)
			{
				double normalizedScore =item.getScore()/ totalScore; 
				StateAction stateAction = new StateAction(item, normalizedScore);
				previousStateActions.add(stateAction);
			}
			return previousStateActions;
		}
		else
		{
			return null;
		}
	}
	public void setOriginalPreviousElements( ArrayList<EyeTrackerItem> previousElements) {
		this.originalPreviousElements = previousElements;
	}
	
	public ArrayList<EyeTrackerItem> getGazePreviousElements() {
		return gazePreviousElements;
	}

	public void setGazePreviousElements(ArrayList<EyeTrackerItem> gazePreviousElements) {
		this.gazePreviousElements = gazePreviousElements;
	}

	public ArrayList<EyeTrackerItem> getProbabilityPreviousElements() {
		return probabilityPreviousElements;
	}

	public void setProbabilityPreviousElements(
			ArrayList<EyeTrackerItem> probabilityPreviousElements) {
		this.probabilityPreviousElements = probabilityPreviousElements;
	}

	public double getProbability(ArrayList<StateAction> actions)
	{
		double maxProbability =Double.MIN_VALUE;
		int state = StateAction.STATE_INVALID;
		for(StateAction stateAction : actions)
		{
			if(stateAction.getPreviousItem() == null)
			{
				state = StateAction.STATE_INIT;
			}
			else
			{
				state = stateAction.getPreviousItem().getType();
			}
			int action = stateAction.getAction();
			double probability = this.probabilityTable[state][action];
			double probabilityScore = probability * stateAction.getNormalizedProbability();
			if(probabilityScore > maxProbability)
			{
				maxProbability = probabilityScore;
			}
		}
		
		return maxProbability;
	}
	
}
