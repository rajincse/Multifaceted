package architecture;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.reflect.TypeToken;

public class HeatmapEntry {
	protected String imagePath;
	protected DataObject dataObject;
	public HeatmapEntry(String imagePath, DataObject dataObject) {
		super();
		this.imagePath = imagePath;
		this.dataObject = dataObject;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public DataObject getDataObject() {
		return dataObject;
	}
	public void setDataObject(DataObject dataObject) {
		this.dataObject = dataObject;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataObject == null) ? 0 : dataObject.hashCode());
		result = prime * result
				+ ((imagePath == null) ? 0 : imagePath.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HeatmapEntry other = (HeatmapEntry) obj;
		if (dataObject == null) {
			if (other.dataObject != null)
				return false;
		} else if (!dataObject.equals(other.dataObject))
			return false;
		if (imagePath == null) {
			if (other.imagePath != null)
				return false;
		} else if (!imagePath.equals(other.imagePath))
			return false;
		return true;
	}
	public static Type getType()
	{
		return new TypeToken<HeatmapEntry>(){}.getType();
	}
	public static Type getListType()
	{
		return new TypeToken<ArrayList<HeatmapEntry>>(){}.getType();
	}
}
