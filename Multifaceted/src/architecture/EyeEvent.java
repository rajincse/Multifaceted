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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + (int) (time ^ (time >>> 32));
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EyeEvent other = (EyeEvent) obj;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (time != other.time)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
}
