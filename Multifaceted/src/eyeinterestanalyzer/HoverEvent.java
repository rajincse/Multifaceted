package eyeinterestanalyzer;

import eyeinterestanalyzer.DataObject;

public class HoverEvent extends Event{
	DataObject target;
	boolean in;
	
	public HoverEvent(long time, boolean inOrOut, DataObject target) {
		super("hover", time);
		this.target = target;		
		this.in = inOrOut;
	}	
}
