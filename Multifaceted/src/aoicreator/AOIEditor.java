package aoicreator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import perspectives.base.Property;
import perspectives.base.Viewer;
import perspectives.properties.PFileInput;
import perspectives.two_d.JavaAwtRenderer;

public class AOIEditor extends Viewer implements JavaAwtRenderer{

	public static final String PROPERTY_LOAD="Load";
	
	private BufferedImage currentImage = null;
	private String currentImagePath="";
	private AOIStimuliInfo currentStimuliInfo = null;
	public AOIEditor(String name) {
		super(name);
		// TODO Auto-generated constructor stub
		try
		{
			PFileInput fileInput = new PFileInput();
			fileInput.extensions = new String[]{"png","*"};
			Property<PFileInput> pLoad = new Property<PFileInput>(PROPERTY_LOAD, fileInput )
					{
						@Override
						protected boolean updating(PFileInput newvalue) {
							// TODO Auto-generated method stub
							readImage(newvalue.path);
							requestRender();
							return super.updating(newvalue);
						}
					};
		    addProperty(pLoad);
		}
		catch(Exception ex)
		{
			
		}
	}
	
	private String getDataFilePath(String imageFilePath)
	{
		String extension = imageFilePath.substring(imageFilePath.lastIndexOf("."));
		String fullPathName = imageFilePath.substring(0, imageFilePath.lastIndexOf(extension));
		String dataFilePath= fullPathName+".JSON";
		
		return dataFilePath;
	}
	private void readImage(String imageFilePath)
	{
		try {
			File imageFile = new File(imageFilePath);
		
			this.currentImage = ImageIO.read(imageFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	@Override
	public Color getBackgroundColor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void keyPressed(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean mousedragged(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mousemoved(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mousepressed(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mousereleased(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		if(currentImage != null)
		{
			g.drawImage(currentImage, 0, 0, currentImage.getWidth(),  currentImage.getHeight(),
					0, 0, currentImage.getWidth(), currentImage.getHeight(),null);
		}
	}

}
