package imdb.analysis;

public class SearchableTimeStamp{
	private long timeStamp;
	public SearchableTimeStamp(long timeStamp)
	{
		this.timeStamp = timeStamp;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj instanceof HeatMapTimeStamp)
		{
			HeatMapTimeStamp heatmapTimeStamp = (HeatMapTimeStamp) obj;
			if(heatmapTimeStamp.getTimeStamp() == this.getTimeStamp())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return super.equals(obj);
		}
		
	}
	
}
