package eyeinterestanalyzer;

public class MouseButtonEvent extends Event{
	int x,y;
	boolean up;
	public MouseButtonEvent(long time, boolean up, int x, int y){
		super("mousebutton", time);
		this.x = x;
		this.y = y;
		this.up = up;
	}
}
