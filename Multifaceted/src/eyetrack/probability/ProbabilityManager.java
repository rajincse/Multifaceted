package eyetrack.probability;

import java.util.ArrayList;

import eyetrack.EyeTrackerItem;
import eyetrack.probability.StateAction;

public class ProbabilityManager {
	public static final double TOP_PERCENTILE =10;
	private double[][] probabilityTable;
	
	private ArrayList<EyeTrackerItem> previousElements;
	
	public ProbabilityManager()
	{
		this.probabilityTable =  StateAction.PROBABILITY_TABLE;
		this.previousElements = null;
	}

	public ArrayList<StateAction> getPreviousStateActions() {
		if(previousElements != null)
		{
			double totalScore = 0;
			for(EyeTrackerItem item: previousElements)
			{
				double score =item.getScore(); 
				totalScore += score;
			}
			
			ArrayList<StateAction> previousStateActions = new ArrayList<StateAction>();
			for(EyeTrackerItem item: previousElements)
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
	public void setPreviousElement( ArrayList<EyeTrackerItem> previousElements) {
		this.previousElements = previousElements;
	}
	
	public double getProbability(ArrayList<StateAction> actions)
	{
		double maxProbability =Double.MIN_VALUE;
		int state = StateAction.STATE_INVALID;
		for(StateAction stateAction : actions)
		{
			stateAction.toString();
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
