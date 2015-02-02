package eyeinterestanalyzer.clustering.distance;

import java.util.ArrayList;

public class TimeSlice {

	private ArrayList<SliceElement> sliceElements;
	public TimeSlice()
	{
		this.sliceElements = new ArrayList<SliceElement>();
	}
	public ArrayList<SliceElement> getSliceElements() {
		return sliceElements;
	}
	public void setSliceElements(ArrayList<SliceElement> sliceElements) {
		this.sliceElements = sliceElements;
	}
	public SliceElement getMaxValueElement()
	{
		double maxVal = Double.MIN_VALUE;
		int maxIndex =-1;
		for(int i=0;i<sliceElements.size();i++)
		{
			SliceElement elem = sliceElements.get(i);
			if(elem.getValue() > maxVal)
			{
				maxVal = elem.getValue();
				maxIndex = i;
			}
		}
		if(maxIndex >=0 && maxIndex <sliceElements.size())
		{
			return sliceElements.get(maxIndex);
		}
		else
		{
			return null;
		}
	}
	public  String getSliceString( int precision)
	{
		String string ="";
		for(SliceElement elem: this.getSliceElements())
		{
			int numberOfCopies = (int)(elem.getValue() * precision);
			for(int i=0;i<numberOfCopies;i++)
			{
				string+=elem.getType();
			}
		}
		
		return string;
	}
}
