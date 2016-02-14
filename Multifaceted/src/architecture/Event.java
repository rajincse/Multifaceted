package architecture;

public class Event{
	protected String type;
	protected long time;
	
	public Event(String type, long time){
		this.type = type;
		this.time = time;		
	}
}
