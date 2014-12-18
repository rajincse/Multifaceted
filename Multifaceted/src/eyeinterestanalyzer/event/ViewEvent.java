package eyeinterestanalyzer.event;

public class ViewEvent extends Event{
	String view;	
	public ViewEvent(long time, String view) {
		super("view", time);
		this.view = view;				
	}	
}
