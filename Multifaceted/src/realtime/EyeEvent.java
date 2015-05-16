package realtime;




public class EyeEvent{

	private  DataObject target;
	private double score;
	private double probability;
	private long time;
	
	public EyeEvent(long time, DataObject target, double score, double prob) {
		this.time = time;
		this.target = target;
		this.score = score;	
		this.probability = prob;
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

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{target:"+target+", time:"+time+"}";
	}
}
