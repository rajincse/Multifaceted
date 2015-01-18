package eyeinterestanalyzer.clustering;

import java.util.ArrayList;

import eyeinterestanalyzer.DataObject;
import eyeinterestanalyzer.LevenshteinDistance;
import eyeinterestanalyzer.clustering.distance.SliceElement;
import eyeinterestanalyzer.clustering.distance.TimeSlice;
import eyetrack.EyeTrackerItem;

public class SampleItem implements ClusteringItem {

	private String id ;
	private String value;
	public SampleItem(String id, String value)	
	{
		this.id = id;
		this.value = value;
	}
	
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public String getStringValue() {
		// TODO Auto-generated method stub
		return this.value;
	}

	@Override
	public double getDistance(ClusteringItem otherItem) {
		// TODO Auto-generated method stub
		return LevenshteinDistance.getLevenshteinDistance(getStringValue(), otherItem.getStringValue());
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{id:"+this.getId()+", value:"+this.getStringValue()+"}";
	}

	
	

	@Override
	public int getClusteringMethod() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ArrayList<TimeSlice> getTimeSlice() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String[] args)
	{
		TimeSlice slice1 = new TimeSlice();
		DataObject obj11 = new DataObject("Dicaprio","Leo", EyeTrackerItem.TYPE_ACTOR);
		SliceElement elem11 = new SliceElement(obj11, 0.6, EyeTrackerItem.TYPE_ACTOR);
		slice1.getSliceElements().add(elem11);
		
		DataObject obj12 = new DataObject("Movie1","Ghajini", EyeTrackerItem.TYPE_MOVIE);
		SliceElement elem12 = new SliceElement(obj12, 0.2, EyeTrackerItem.TYPE_MOVIE);
		slice1.getSliceElements().add(elem12);
		
		DataObject obj13 = new DataObject("Dir","Nolan", EyeTrackerItem.TYPE_DIRECTOR);
		SliceElement elem13 = new SliceElement(obj13, 0.2, EyeTrackerItem.TYPE_DIRECTOR);
		slice1.getSliceElements().add(elem13);
		
		TimeSlice slice2 = new TimeSlice();
		DataObject obj21 = new DataObject("Dicaprio","Leo", EyeTrackerItem.TYPE_ACTOR);
		SliceElement elem21 = new SliceElement(obj21, 0.7, EyeTrackerItem.TYPE_ACTOR);
		slice2.getSliceElements().add(elem21);
		
		DataObject obj22 = new DataObject("Movie1","Ghajini", EyeTrackerItem.TYPE_MOVIE);
		SliceElement elem22 = new SliceElement(obj22, 0.15, EyeTrackerItem.TYPE_MOVIE);
		slice2.getSliceElements().add(elem22);
		
		DataObject obj23 = new DataObject("Dir","Nolan", EyeTrackerItem.TYPE_DIRECTOR);
		SliceElement elem23 = new SliceElement(obj23, 0.15, EyeTrackerItem.TYPE_DIRECTOR);
		slice2.getSliceElements().add(elem23);
		
		double distance = LevenshteinDistance.getSliceDifference(slice1, slice2);
		
		System.out.println("Distance: "+distance);
	}
}
