package eyeinterestanalyzer;

public class ImageEvent extends Event{
	String path;	
	public ImageEvent(long time, String image) {
		super("image", time);
		this.path = image;				
	}	
}
