package architecture;

import imdb.entity.CompactMovie;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.reflect.TypeToken;


public class HeatmapObject {
	protected ArrayList<HeatmapEntry> entryList;
	public HeatmapObject(ArrayList<HeatmapEntry> entry) {
		
		this.entryList = entry;
	}
	public ArrayList<HeatmapEntry> getEntry() {
		return entryList;
	}
	public void setEntry(ArrayList<HeatmapEntry> entry) {
		this.entryList = entry;
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
		result = prime * result + ((entryList == null) ? 0 : entryList.hashCode());
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
		if (entryList == null) {
			if (other.entryList != null)
				return false;
		} else if (!entryList.equals(other.entryList))
			return false;
		return true;
	}
	
	
}
