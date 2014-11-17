package eyetrack.probability;

import java.util.ArrayList;

import eyetrack.EyeTrackerItem;
import eyetrack.probability.StateAction;

public class ProbabilityManager {
	public static final double TOP_PERCENTILE =10;
	public static final double LEVITATION_LOWER_BOUND =0.1; // to make probabilty range from 0.5 to 1
	
	private double[][] probabilityTable;
	
	private ArrayList<EyeTrackerItem> originalPreviousElements;
	private ArrayList<StateAction> previousStateAction;
	
	public ProbabilityManager()
	{
		this.probabilityTable =  StateAction.PROBABILITY_TABLE;
		this.originalPreviousElements = null;
	}


	public ArrayList<EyeTrackerItem> getPreviousElementList()
	{
		return originalPreviousElements;
	}
	protected ArrayList<StateAction> getPreviousStateActions(ArrayList<EyeTrackerItem> previousElementList) {
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
	public ArrayList<StateAction> getPreviousStateActions() {
		
		return this.previousStateAction;
	}
	public void setOriginalPreviousElements( ArrayList<EyeTrackerItem> previousElements) {
		this.originalPreviousElements = previousElements;
		this.previousStateAction = getPreviousStateActions(previousElements);
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
