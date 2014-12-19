package eyeinterestanalyzer.event;

public class Event{
	protected String type;
	public long time;
	
	public Event(String type, long time){
		this.type = type;
		this.time = time;		
	}
	
	public long getTime()
	{
		return this.time;
	}
}
