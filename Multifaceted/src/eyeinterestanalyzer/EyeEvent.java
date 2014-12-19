package eyeinterestanalyzer;

import eyeinterestanalyzer.DataObject;



public class EyeEvent extends Event{

	DataObject target;
	double score;
	double prob;
	
	public EyeEvent(long time, DataObject target, double score, double prob) {
		super("eye", time);
		this.target = target;
		this.score = score;	
		this.prob = prob;
	}	
}
