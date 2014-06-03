package imdb.analysis;

import java.util.HashMap;

public class HeatMapTimeStamp {
	private long timeStamp;

	private HashMap<String, HeatMapCell> cellList ;
	
	public HeatMapTimeStamp(long timeStamp)
	{
		this.timeStamp = timeStamp;
		this.cellList = new HashMap<String, HeatMapCell>();
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public HashMap<String, HeatMapCell> getCellList() {
		return cellList;
	}
	
	public void addItem(HeatMapCell cell)
	{
		String id = cell.getItem().getId();
		this.cellList.put(id, cell);
		
	}
	
	public void addItem(String id, String name, double score, String imageName, int x, int y)
	{
		HeatMapCell cell = HeatMapCell.createInstance(id, name, score, imageName, x, y);
		this.addItem(cell);
	}
	
	@Override
	public String toString() {
		String msg="{ Timestamp:"+timeStamp+", list:[";
		String list ="";
		for(HeatMapCell cell: cellList.values())
		{
			list+=cell.toString()+", ";
		}
		if(!list.isEmpty())
		{
			list  = list.substring(0,list.length()-2);
		}
		msg+= list+"]}";
		return msg;
	}
}
