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
