package eyeinterestanalyzer.event;

public class ImageEvent extends Event{
	public String path;	
	public ImageEvent(long time, String image) {
		super("image", time);
		this.path = image;				
	}	
}
