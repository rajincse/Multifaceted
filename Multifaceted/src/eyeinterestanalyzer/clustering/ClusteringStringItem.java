package eyeinterestanalyzer.clustering;

public interface ClusteringStringItem {
	public static String DELIMITER ="#";
	public String getId();
	public String getStringValue();
	public int getDistance(ClusteringStringItem otherItem);
}
