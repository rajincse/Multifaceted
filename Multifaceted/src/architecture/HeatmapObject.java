package architecture;

import imdb.entity.CompactMovie;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.reflect.TypeToken;

public class HeatmapObject {
	protected double[][] heatmap;
	protected int[] index;
	protected ArrayList<DataObject> dataObjects;
	protected ArrayList<DataObject> viewedObjects;
	public HeatmapObject(double[][] heatmap, int[] index,
			ArrayList<DataObject> dataObjects,
			ArrayList<DataObject> viewedObjects) {
		super();
		this.heatmap = heatmap;
		this.index = index;
		this.dataObjects = dataObjects;
		this.viewedObjects = viewedObjects;
	}
	public double[][] getHeatmap() {
		return heatmap;
	}
	public void setHeatmap(double[][] heatmap) {
		this.heatmap = heatmap;
	}
	public int[] getIndex() {
		return index;
	}
	public void setIndex(int[] index) {
		this.index = index;
	}
	public ArrayList<DataObject> getDataObjects() {
		return dataObjects;
	}
	public void setDataObjects(ArrayList<DataObject> dataObjects) {
		this.dataObjects = dataObjects;
	}
	public ArrayList<DataObject> getViewedObjects() {
		return viewedObjects;
	}
	public void setViewedObjects(ArrayList<DataObject> viewedObjects) {
		this.viewedObjects = viewedObjects;
	}
	
	public static Type getType()
	{
		return new TypeToken<HeatmapObject>(){}.getType();
	}
	public static Type getListType()
	{
		return new TypeToken<ArrayList<HeatmapObject>>(){}.getType();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataObjects == null) ? 0 : dataObjects.hashCode());
		result = prime * result + Arrays.hashCode(heatmap);
		result = prime * result + Arrays.hashCode(index);
		result = prime * result
				+ ((viewedObjects == null) ? 0 : viewedObjects.hashCode());
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
		HeatmapObject other = (HeatmapObject) obj;
		if (dataObjects == null) {
			if (other.dataObjects != null)
				return false;
		} else if (!dataObjects.equals(other.dataObjects))
			return false;
		if (!Arrays.equals(heatmap, other.heatmap))
			return false;
		if (!Arrays.equals(index, other.index))
			return false;
		if (viewedObjects == null) {
			if (other.viewedObjects != null)
				return false;
		} else if (!viewedObjects.equals(other.viewedObjects))
			return false;
		return true;
	}
	
	
}
