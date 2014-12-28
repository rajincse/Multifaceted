package eyeinterestanalyzer.clustering;

public interface ClusteringStringItem {
	public String getId();
	public String getStringValue();
	public int getDistance(ClusteringStringItem otherItem);
}
