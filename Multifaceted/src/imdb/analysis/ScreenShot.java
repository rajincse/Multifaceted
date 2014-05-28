package imdb.analysis;

public class ScreenShot {
	private String imageName;
	private int x;
	private int y;
	
	public ScreenShot(String imageName, int x, int y)
	{
		this.imageName = imageName;
		this.x = x;
		this.y = y;
	}

	public String getImageName() {
		return imageName;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{ image:"+imageName+", x:"+x+", y:"+y+"}";
	}
}
