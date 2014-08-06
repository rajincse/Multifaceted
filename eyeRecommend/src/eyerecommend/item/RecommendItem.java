package eyerecommend.item;


public interface RecommendItem extends Comparable<RecommendItem>{
	public int getIndex();
	
	public double getPosteriorProbability();
	
	public double getPriorProbability();
	
}
