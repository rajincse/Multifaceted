package eyeinterestanalyzer.event;

import eyeinterestanalyzer.DataObject;

public class HoverEvent extends Event{
	public DataObject target;
	public boolean in;
	
	public HoverEvent(long time, boolean inOrOut, DataObject target) {
		super("hover", time);
		this.target = target;		
		this.in = inOrOut;
	}	
}
