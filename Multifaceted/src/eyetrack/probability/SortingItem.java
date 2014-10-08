package eyetrack.probability;

import eyetrack.EyeTrackerItem;

public class SortingItem implements Comparable<SortingItem> {

	private EyeTrackerItem item;
	private double value;
	public SortingItem(EyeTrackerItem item, double value)
	{
		this.item = item;
		this.value = value;
	}
	
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public EyeTrackerItem getItem() {
		return item;
	}

	@Override
	public int compareTo(SortingItem o) {
		// TODO Auto-generated method stub
		return Double.compare( o.getValue(), this.getValue());
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{"+this.item+", "+this.getValue()+"}";
	}
}
