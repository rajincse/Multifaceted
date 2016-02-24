package architecture;

import java.lang.reflect.Type;
import java.util.ArrayList;
import com.google.gson.reflect.TypeToken;


public class HeatmapObject {
	protected ArrayList<HeatmapEntry> entryList;
	protected int totalCells;
	public HeatmapObject(ArrayList<HeatmapEntry> entry, int totalCells) {
		
		this.entryList = entry;
		this.totalCells = totalCells;
	}
	
	
	public ArrayList<HeatmapEntry> getEntryList() {
		return entryList;
	}


	public void setEntryList(ArrayList<HeatmapEntry> entryList) {
		this.entryList = entryList;
	}


	public int getTotalCells() {
		return totalCells;
	}


	public void setTotalCells(int totalCells) {
		this.totalCells = totalCells;
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
				+ ((entryList == null) ? 0 : entryList.hashCode());
		result = prime * result + totalCells;
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
		if (totalCells != other.totalCells)
			return false;
		return true;
	}
	
	
}
