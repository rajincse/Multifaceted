package architecture;

import java.io.Serializable;




public class EyeEvent implements Serializable{

	DataObject target;
	double score;
	double prob;
	long time;
	String type;

	public EyeEvent(long time, DataObject target, double score, double prob) {
		this.time = time;
		this.target = target;
		this.score = score;	
		this.prob = prob;
		type="eye";
	}
	public EyeEvent()
	{
	}
	public DataObject getTarget() {
		return target;
	}

	public void setTarget(DataObject target) {
		this.target = target;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getProb() {
		return prob;
	}

	public void setProb(double prob) {
		this.prob = prob;
	}
	
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{target:"+target+", type:"+type+", time:"+time+"}";
	}
}
