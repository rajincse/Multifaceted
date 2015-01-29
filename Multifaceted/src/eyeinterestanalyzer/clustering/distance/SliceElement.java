package eyeinterestanalyzer.clustering.distance;

import eyeinterestanalyzer.DataObject;

public class SliceElement {
	private DataObject object;
	private double value;
	private int type;
	public SliceElement(DataObject object, double value, int type)
	{
		this.object = object;
		this.value = value;
		this.type = type;
	}
	public DataObject getObject() {
		return object;
	}
	public void setObject(DataObject object) {
		this.object = object;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{obj:"+this.object+", val:"+String.format("%.2f", this.value)+"}";
	}
}
