package eyerecommend.item;

public class RectangleItem implements RecommendItem{
	private double score;
	private int index;
	public RectangleItem(int index, double score)
	{
		this.index = index;
		this.score = score;
	}
	@Override
	public int compareTo(RecommendItem other) {
		// TODO Auto-generated method stub
		return Double.compare(this.getPosteriorProbability()* this.getPriorProbability(), other.getPosteriorProbability()* other.getPriorProbability());
	}

	@Override
	public int getIndex() {
		// TODO Auto-generated method stub
		return this.index;
	}

	@Override
	public double getPosteriorProbability() {
		// TODO Auto-generated method stub
		return this.score;
	}

	@Override
	public double getPriorProbability() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{index:"+this.index+", score:"+score+"}";
	}
}
