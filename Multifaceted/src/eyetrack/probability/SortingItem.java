package eyetrack.probability;

import eyetrack.EyeTrackerItem;

public class SortingItem implements Comparable<SortingItem> {

	private EyeTrackerItem item;
	private double value;
	public SortingItem(EyeTrackerItem item)
	{
		this.item = item;
		this.value = item.getScore();
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
		return Double.compare(this.getValue(), o.getValue());
	}
}
