package eyeinterestanalyzer.event;

public class MouseMoveEvent extends Event{
	public int x,y;
	public boolean drag;
	public MouseMoveEvent(long time, boolean drag, int x, int y){
		super("mousemove", time);
		this.x = x;
		this.y = y;
		this.drag = drag;
	}
}
