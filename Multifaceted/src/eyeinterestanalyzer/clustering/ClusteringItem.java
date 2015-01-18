package eyeinterestanalyzer.clustering;

import java.util.ArrayList;

import eyeinterestanalyzer.clustering.distance.TimeSlice;

public interface ClusteringItem {
	public static String DELIMITER ="#";
	public String getId();
	public String getStringValue();
	public ArrayList<TimeSlice> getTimeSlice();
	public double getDistance(ClusteringItem otherItem);
	public int getClusteringMethod();
}
