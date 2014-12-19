package eyeinterestanalyzer;

public class MouseMoveEvent extends Event{
	int x,y;
	boolean drag;
	public MouseMoveEvent(long time, boolean drag, int x, int y){
		super("mousemove", time);
		this.x = x;
		this.y = y;
		this.drag = drag;
	}
}
