package eyeinterestanalyzer.event;

import eyeinterestanalyzer.DataObject;



public class EyeEvent extends Event{

	public DataObject target;
	public double score;
	public double prob;
	
	public EyeEvent(long time, DataObject target, double score, double prob) {
		super("eye", time);
		this.target = target;
		this.score = score;	
		this.prob = prob;
	}	
}
