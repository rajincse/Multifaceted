package scanpath;

import realtime.DataObject;

public class MultiScanpathDiagram {
	public DataObject dataObject;
	public boolean isSelected;
	public MultiScanpathDiagram(DataObject dataObject, boolean isSelected) {
		this.dataObject = dataObject;
		this.isSelected = isSelected;
	}
	@Override
	public int hashCode() {		
		return dataObject.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MultiScanpathDiagram)
		{
			return ((MultiScanpathDiagram)obj).equals(this.dataObject);
		}
		else
		{
			return dataObject.equals(obj);
		}
		
	}
	
	
}
