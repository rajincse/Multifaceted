package eyeinterestanalyzer;

public class GazeEvent extends Event{
	int x,y;
	public GazeEvent(long time,int x, int y){
		super("gaze", time);
		this.x = x;
		this.y = y;
	}
}