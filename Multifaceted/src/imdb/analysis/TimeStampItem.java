package imdb.analysis;

import java.util.ArrayList;

public class TimeStampItem {
	private long timeStamp;
	private ArrayList<AnalysisItem> items;
	
	public TimeStampItem(long timeStamp)
	{
		this.timeStamp = timeStamp;
		
		this.items = new ArrayList<AnalysisItem>();
	}
	
	public void addItem(AnalysisItem item)
	{
		if(!items.contains(item))
		{
			this.items.add(item);
		}
		
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public ArrayList<AnalysisItem> getItems() {
		return items;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String msg="{ timeStamp:"+timeStamp+", list:[";
		String list ="";
		for(AnalysisItem item: items)
		{
			list+= item.toString()+", ";
		}
		if(!list.isEmpty())
		{
			list  = list.substring(0,list.length()-2);
		}
		msg+= list+"]}";
		
		return msg;
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj instanceof TimeStampItem)
		{
			TimeStampItem item = (TimeStampItem) obj;
			return item.items.equals(this.items);
		}
		else
		{
			return super.equals(obj);
		}
		
	}
}
