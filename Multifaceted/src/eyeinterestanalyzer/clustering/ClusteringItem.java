package eyeinterestanalyzer.clustering;

public interface ClusteringItem {
	public static String DELIMITER ="#";
	public String getId();
	public String getStringValue();
	public double getDistance(ClusteringItem otherItem);
	public int getClusteringMethod();
}
