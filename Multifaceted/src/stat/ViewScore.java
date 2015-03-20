package stat;

public class ViewScore {

	private double value;
	private int count;
	
	public ViewScore(double score)
	{
		this.value = score;
		this.count =1;
	}
	public void addScore( double score)
	{
		this.value+= score;
		this.count++;
		
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	public double getAverage()
	{
		return this.value / this.count;
	}
}
