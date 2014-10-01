package pivotpath.analysis;

import imdb.analysis.AnalysisItem;
import imdb.analysis.HeatMapCell;
import imdb.analysis.HeatMapTimeStamp;
import imdb.analysis.SearchableTimeStamp;
import imdb.entity.CompactMovie;
import imdb.entity.CompactPerson;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import perspectives.base.Property;
import perspectives.base.Viewer;
import perspectives.properties.PFileInput;
import perspectives.properties.PSignal;
import perspectives.two_d.JavaAwtRenderer;

public class PivotPathSimulationViewer extends Viewer implements JavaAwtRenderer{
	public static final int INVALID =-1;
	private static final String PROPERTY_LOAD ="Load";
	private static final String PROPERTY_NEXT ="Next";
	private static final String PROPERTY_START ="Start";
	private static final String PROPERTY_STOP ="Stop";
	
	private static final long TIME_LAPSE = 10;
	
	private ArrayList<PivotPathFrame> frameList = null;
	private HashMap<String, BufferedImage> imageList=null;
	private String directory;
	private Timer timer =new Timer();
	
	public PivotPathSimulationViewer(String name) {
		super(name);
		Property<PFileInput> pLoad = new Property<PFileInput>(PROPERTY_LOAD, new PFileInput())
				{
					@Override
					protected boolean updating(PFileInput newvalue) {
						// TODO Auto-generated method stub
						processFile(newvalue.path);
						return super.updating(newvalue);
					}

				};
		addProperty(pLoad);
		
		Property<PSignal> pNext = new Property<PSignal>(PROPERTY_NEXT,new PSignal())
				{
					@Override
					protected boolean updating(PSignal newvalue) {
						// TODO Auto-generated method stub
						if(frameList != null && !frameList.isEmpty())
						{
							frameIndex = (frameIndex+1) % frameList.size();
							requestRender();
						}
						return super.updating(newvalue);
					}
				};
		addProperty(pNext);
		
		Property<PSignal> pStart = new Property<PSignal>(PROPERTY_START,new PSignal())
				{
					@Override
					protected boolean updating(PSignal newvalue) {
						// TODO Auto-generated method stub
						startTimer();
						return super.updating(newvalue);
					}
				};
		addProperty(pStart);
		
		Property<PSignal> pStop = new Property<PSignal>(PROPERTY_STOP,new PSignal())
				{
					@Override
					protected boolean updating(PSignal newvalue) {
						// TODO Auto-generated method stub
						stopTimer();
						return super.updating(newvalue);
					}
				};
		addProperty(pStop);
	}

	private void startTimer()
	{
		this.timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				if(frameList != null && !frameList.isEmpty() && frameIndex < frameList.size()-1)
				{
					frameIndex++;
					requestRender();
				}
				else
				{
					stopTimer();
				}
			}
		}
		, 0, TIME_LAPSE);
	}
	
	private void stopTimer()
	{
		this.timer.cancel();
	}

	private void processFile(String filePath)
	{		
		try {
			frameList = new ArrayList<PivotPathFrame>();
			imageList = new HashMap<String, BufferedImage>();
			
			File file = new File(filePath);
			this.directory = file.getParent();
			FileReader fStream;
			fStream = new FileReader(file);		
			BufferedReader bufferedReader = new BufferedReader(fStream);
			
			String line = bufferedReader.readLine();
			while(line != null)
			{
				
				processFileLine(line);
				line = bufferedReader.readLine();
			}
			if(!this.frameList.isEmpty())
			{
				this.frameIndex =0;
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	private BufferedImage getImage(String fileName)
	{
		if(imageList != null && !fileName.isEmpty())
		{
			if(imageList.containsKey(fileName))
			{
				return imageList.get(fileName);
			}
			else
			{
				
				try {
					BufferedImage image = ImageIO.read(new File(directory+"/"+fileName));
					imageList.put(fileName, image);
					return image;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		return null;
	}
	
	private PivotPathFrame currentFrame= null;;
	private void processFileLine(String line) {		
		String[] split = line.split("\t");
//		System.out.println(line);
		
		if(split.length > 0 && split[0].equals("Mouse"))
		{
			long timeStamp = Long.parseLong(split[1]);
			int x = Integer.parseInt(split[2]);
			int y = Integer.parseInt(split[3]);
			String imageFile ="";
			if(split.length == 5)
			{
				imageFile = split[4];
			}
			if(currentFrame!= null && currentFrame.getTimestamp() == timeStamp)
			{
				currentFrame.setMousePosition(new Point2D.Double(x,y));
				if(currentFrame.getImage() == null && !imageFile.isEmpty())
				{
					currentFrame.setImage(getImage(imageFile));
				}
			}
			else
			{
				currentFrame = new PivotPathFrame(timeStamp);
				currentFrame.setMousePosition(new Point2D.Double(x,y));
				if(currentFrame.getImage() == null && !imageFile.isEmpty())
				{
					currentFrame.setImage(getImage(imageFile));
				}
				this.frameList.add(currentFrame);
			}
		}
		else if(split.length > 0 && split[0].equals("Gaze"))
		{
			long timeStamp = Long.parseLong(split[1]);
			int x = Integer.parseInt(split[2]);
			int y = Integer.parseInt(split[3]);
			String imageFile ="";
			if(split.length == 5)
			{
				imageFile = split[4];
			}
			if(currentFrame!= null && currentFrame.getTimestamp() == timeStamp)
			{
				currentFrame.setGazePosition(new Point2D.Double(x,y));
				if(currentFrame.getImage() == null && !imageFile.isEmpty())
				{
					currentFrame.setImage(getImage(imageFile));
				}
			}
			else
			{
				currentFrame = new PivotPathFrame(timeStamp);
				currentFrame.setGazePosition(new Point(x,y));
				if(currentFrame.getImage() == null && !imageFile.isEmpty())
				{
					currentFrame.setImage(getImage(imageFile));
				}
				this.frameList.add(currentFrame);
			}
		}
		else if(split.length >= 7)
		{

			
			long timeStamp = Long.parseLong(split[0]);
			
			
			
			String id = split[1];
			String name = split[2];
			int type = Integer.parseInt(split[3]);
			double score = Double.parseDouble(split[4]);
			int x = Integer.parseInt(split[5]);
			int y = Integer.parseInt(split[6]);
			String image = "";
			
			if(split.length == 8)
			{
				image = split[7];
			}
			
			if(currentFrame!= null && currentFrame.getTimestamp() == timeStamp)
			{
				currentFrame.addElement(new Point(x,y), score);
				if(currentFrame.getImage() == null && !image.isEmpty())
				{
					currentFrame.setImage(getImage(image));
				}
			}
			else
			{
				currentFrame = new PivotPathFrame(timeStamp);
				currentFrame.addElement(new Point(x,y), score);
				if(currentFrame.getImage() == null && !image.isEmpty())
				{
					currentFrame.setImage(getImage(image));
				}
				this.frameList.add(currentFrame);
			}
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
	private int frameIndex = INVALID;
	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		if(frameIndex != INVALID && this.frameList != null && !this.frameList.isEmpty())
		{
			PivotPathFrame frame = this.frameList.get(frameIndex);
			frame.render(g);
		}
	}

}
