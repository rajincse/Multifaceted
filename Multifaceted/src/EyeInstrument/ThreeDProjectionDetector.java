package EyeInstrument;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStreamImpl;

import perspectives.base.Property;
import perspectives.base.Viewer;
import perspectives.properties.PDouble;
import perspectives.properties.PInteger;
import perspectives.properties.PSignal;
import perspectives.two_d.JavaAwtRenderer;

public class ThreeDProjectionDetector extends Viewer implements JavaAwtRenderer {
	private static final String PROPERTY_ADVANCE = "Advance";
	private static final String PROPERTY_ADVANCE_ALL = "Advance All";
	private static final String PROPERTY_TIME="Time";
	boolean withCutouts = false;

	String[] imageNames;
	String[] maskNames;
	double[] imageTimes;
	double[] maskTimes;
	String[] propNames;
	double[] propTimes;
	
	int[] gazeX;
	int[] gazeY;
	double[] gazeT;
	
	int[][] com1;
	String[] com2;
	
	int currentImageIndex = -1; //current screen counter
	int currentMaskIndex = -1;
	int currentPropIndex = -1;
	int cgc = -1; //current gaze counter
	//BufferedImage cs = null;
	//BufferedImage cr = null;
	
	BufferedImage currentImage = null;
	BufferedImage currentMask = null;
	Hashtable<String,String> currentProps = null;
	
	int radius = 50;
	int xOffset = 8;
	int yOffset = 115;
	
	String folder = "";//"E:/Graph/Data/ConstructionData/EyeTrackData_Amirmasoud/";
	
	int cutoutIndex = 0;
	
	PrintStream outstream = null;
	
	public ThreeDProjectionDetector(String name, String dataInputDirectory) {
		super(name);
		this.folder = dataInputDirectory+"/";
		load(folder);
		String dataOutputFile = folder+"proc/props.txt";
		File outputFile = new File(dataOutputFile);
		try {
			if(!outputFile.getParentFile().exists())
			{
				outputFile.getParentFile().mkdirs();
				outputFile.createNewFile();
				System.out.println(outputFile.getPath()+" created!");
			}
			if(!outputFile.exists())
			{
				
					outputFile.createNewFile();
					System.out.println(outputFile.getPath()+" created!");
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			outstream = new PrintStream(outputFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	//	this.computeAllVisible();
		
		
		Property<PSignal> padv = new Property<PSignal>(PROPERTY_ADVANCE, new PSignal()){

			@Override
			protected boolean updating(PSignal newvalue) {
				advance();
				return super.updating(newvalue);
			}
			
		};
		this.addProperty(padv);
		
		Property<PSignal> padvall = new Property<PSignal>(PROPERTY_ADVANCE_ALL, new PSignal()){

			@Override
			protected boolean updating(PSignal newvalue) {
//				while(advance());
				advanceAll();
				return super.updating(newvalue);
			}
			
		};
		this.addProperty(padvall);
		
		Property<PDouble> pTime = new Property<PDouble>(PROPERTY_TIME, new PDouble(0)){
			
			@Override
			protected boolean updating(PDouble newvalue) {
				// TODO Auto-generated method stub
				double time = newvalue.doubleValue();
				cgc = getGazeIndex(time)-1;
				advance();
				return super.updating(newvalue);
			}
		};
		this.addProperty(pTime);
	}
	public void advanceAll()
	{
		boolean keepRunning = true;
		long time = System.currentTimeMillis();
		while(keepRunning)
		{
			time = System.currentTimeMillis();
			keepRunning = advance();
			time = System.currentTimeMillis() -time;
			System.out.println("Time :"+time);
		}
		System.out.println("Advancing all complete");
	}
	
	private double minGazeTime = Double.MAX_VALUE;
	private double maxGazeTime = Double.MIN_VALUE;
	private int getGazeIndex(double time)
	{
		if(time > maxGazeTime)
		{
			return gazeT.length-2;
		}
		else if(time < minGazeTime)
		{
			return -1;
		}
		else
		{
			return binaryGazeIndexSearch(0, gazeT.length-1, time);
		}
	
		
	}
	
	private int binaryGazeIndexSearch(int startIndex, int endIndex, double value)
	{
		if(startIndex == endIndex)
		{
			return startIndex;
		}
		else
		{
			int midIndex = (startIndex+endIndex)/2;
			double distanceLeft = Math.abs(gazeT[midIndex] -value);
			double distanceRight = Math.abs(gazeT[midIndex+1] -value);
			if(distanceLeft < distanceRight)
			{
				return binaryGazeIndexSearch(startIndex, midIndex, value);
			}
			else
			{
				return binaryGazeIndexSearch(midIndex+1, endIndex, value);
			}
		}
		
		
		
		
	}
	private void load(String folder){
		
		File f = new File(folder);
		
		File[] fs = f.listFiles();
		ArrayList<String> masks = new ArrayList<String>();
		ArrayList<String> ims = new ArrayList<String>();
		ArrayList<String> props = new ArrayList<String>();
		for (int i=0; i<fs.length; i++){
			if (fs[i].isDirectory())
				continue;
			
			if (fs[i].getName().startsWith("SavedScreen") && fs[i].getName().endsWith("Mask.png"))
				masks.add(fs[i].getName());
			else if (fs[i].getName().startsWith("SavedScreen") && fs[i].getName().endsWith("Prop.txt"))
				props.add(fs[i].getName());
			else if (fs[i].getName().startsWith("SavedScreen"))
				ims.add(fs[i].getName());
		}
		
		imageNames = new String[ims.size()];
		imageTimes = new double[ims.size()];
		maskNames = new String[masks.size()];
		maskTimes = new double[masks.size()];
		propNames = new String[props.size()];
		propTimes = new double[props.size()];
		
		for (int i=0; i<ims.size(); i++){			
			imageNames[i] = folder + ims.get(i);
			imageTimes[i] = Double.parseDouble(ims.get(i).split("_")[1].replaceAll(".png", ""));
		}
		for (int i=0; i<masks.size(); i++){			
			maskNames[i] = folder + masks.get(i);
			maskTimes[i] = Double.parseDouble(masks.get(i).split("_")[1]);
		}
		for (int i=0; i<props.size(); i++){			
			propNames[i] = folder + props.get(i);
			propTimes[i] = Double.parseDouble(props.get(i).split("_")[1]);
		}
		
		//sort the masks
		while(true){
			boolean sw = false;
			for (int i=0; i<maskNames.length-1; i++){
				if (maskTimes[i] > maskTimes[i+1]){
					sw = true;
					String aux1 = maskNames[i];
					double aux2 = maskTimes[i];
					maskNames[i] = maskNames[i+1];
					maskTimes[i] = maskTimes[i+1];
					maskNames[i+1] = aux1;
					maskTimes[i+1] = aux2;
				}
			}
			if (!sw) break;
		}
		
		/*try {
			currentMask = ImageIO.read(new File(maskNames[0]));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		
		try (BufferedReader br = new BufferedReader(new FileReader(folder + "gazelog.txt"))) {
			ArrayList<String> lines = new ArrayList<String>();
			String line;
		    while ((line = br.readLine()) != null) {
			      if (line.trim().length() == 0) continue;
			      lines.add(line.trim());
		    }
		    
		    gazeX = new int[lines.size()];
		    gazeY = new int[lines.size()];
		    gazeT = new double[lines.size()];
		    
		    for (int i=0; i<gazeX.length; i++){
		    	String[] split = lines.get(i).split(",");
		    	gazeX[i] = Integer.parseInt(split[4]) - xOffset;
		    	gazeY[i] = Integer.parseInt(split[5]) - yOffset;
		    	double gazeTime = Double.parseDouble(split[3]);
		    	gazeT[i] = gazeTime;
		    	
		    	minGazeTime = Math.min(gazeTime, minGazeTime);
		    	maxGazeTime = Math.max(gazeTime, maxGazeTime);
		    	
		    }
		    
		    //smooth out gazeT
		    int i=0;
		    while (i < gazeT.length){
		    	if (i==0 || gazeT[i] != gazeT[i-1]){
		    		int e = i+1;
		    		while (e < gazeT.length && gazeT[i] == gazeT[e]) e++;
		    		double endTime;
		    		if (e < gazeT.length) endTime = gazeT[e];
		    		else endTime = gazeT[i] +  0.02 * (e-i);
		    		double incr = (endTime - gazeT[i])/ (e-i);
		    		for (int k=0; k<e-i; k++)
		    			gazeT[i+k] = gazeT[i] + incr*k;
		    		i = e;
		    	}
		    }
		}
		catch(Exception e){
			
		}
		
		
		try (BufferedReader br = new BufferedReader(new FileReader(folder + "ColorObjectMapping.txt"))) {
			ArrayList<String> lines = new ArrayList<String>();
		    String line;
		    while ((line = br.readLine()) != null) {
		      if (line.trim().length() == 0) continue;
		      lines.add(line.trim());
		    }
		    
		    com1 = new int[lines.size()][];
		    com2 = new String[lines.size()];
		    for (int i=0; i<com1.length; i++){
		    	String[] split = lines.get(i).split(",");
		    	com1[i] = new int[]{(int)(255*Double.parseDouble(split[0])), (int)(255*Double.parseDouble(split[1])), (int)(255*Double.parseDouble(split[2]))};
		    	
		    	String tail = "";
		    	for (int j=3; j<split.length; j++)
		    		if (j == split.length-1) tail += split[j]; else tail += split[j] + ",";
		    	
		    	com2[i] = tail;
		    }
		}
		catch(Exception e){
			
		}
		System.out.println(" MaskTimes:"+maskTimes.length+", gazeT:"+gazeT.length);
	}
	
	int[] minx;
	int[] miny;
	int[] maxx;
	int[] maxy;
	boolean[] visible;
	String[] doiNames;
	String[] doiProps;
	
	private boolean advance(){
		cgc++;

		//if we need to advance the current mask we do the following:
		//advance the current mask
		//find the prop file that is closest (in terms of time) to the current mask
		//   -- from the prop file, load the properties of each DOI into doiProps (a string formatted as attr1=value1 | attr2=value2 etc.
		//find the image file that is closest (in terms of time) to the current mask
		//   -- for each DOI find its image cutout as well as the center and size of the cutout in relation to the image
		//   -- save the cutout with a unique file name and store that file name into doiNames
		if (currentMaskIndex < 0 || gazeT[cgc] > maskTimes[currentMaskIndex]){
			currentMaskIndex++;
			if (currentMaskIndex >= maskTimes.length-1)
				return false;
			
			//process new DOIs:
			//find the closest prop (in time) to the mask
			double minT = 9999999;
			for (int i=0; i<propTimes.length; i++)
				if (Math.abs(propTimes[i] - maskTimes[currentMaskIndex]) < minT){
					minT = Math.abs(propTimes[i] - maskTimes[currentMaskIndex]);
					currentPropIndex = i;
				}
			
			//find the closest image (in time) to the mask
			minT = 9999999;
			for (int i=0; i<imageTimes.length; i++)
				if (Math.abs(imageTimes[i] - maskTimes[currentMaskIndex]) < minT){
					minT = Math.abs(imageTimes[i] - maskTimes[currentMaskIndex]);
					currentImageIndex = i;
				}
			
			//load the props
			try (BufferedReader br = new BufferedReader(new FileReader(propNames[currentPropIndex]))) {
				ArrayList<String> lines = new ArrayList<String>();
			    String line;
			    while ((line = br.readLine()) != null) {
			      if (line.trim().length() == 0) continue;
			      lines.add(line.trim());
			    }
			    
			    doiProps = new String[com1.length];
			    for (int i=0; i<lines.size(); i++){
			    	String doi = lines.get(i).split("\t")[0];
			    	for (int j=0; j<com2.length; j++)
			    		if (com2[j].equals(doi)){
			    			//doiProps[j] = lines.get(i);
			    			
			    			//reformat props
			    			String[] ps = lines.get(i).split("\t");
			    			String newf = "";
			    			for (int k=1; k<ps.length; k+=2){
			    				if (newf.length() != 0) newf += " || ";
			    				newf += ps[k] + "=" + ps[k+1];
			    			}
			    			doiProps[j] = newf;
			    			break;
			    		}
			    }
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
			//load the two images (make and real)
			//then look for the DOIs in the mask
			//then extract the DOIs from the image
			try {
				System.out.println("load mask " + maskNames[currentMaskIndex]);
				currentMask = ImageIO.read(new File(maskNames[currentMaskIndex]));
				
				System.out.println("load image " + imageNames[currentImageIndex]);
				currentImage = ImageIO.read(new File(imageNames[currentImageIndex]));
				
				if (!withCutouts){
					cutoutIndex++;
					String filename = folder + "proc/frame" + cutoutIndex + ".png";
					ImageIO.write(currentImage, "PNG", new File(filename));
				}
				
				minx = new int[com1.length];
				miny = new int[com1.length];
				maxx = new int[com1.length];
				maxy = new int[com1.length];
				visible = new boolean[com1.length];
				doiNames = new String[com1.length];
				
				for (int i=0; i<com1.length; i++){
					minx[i] = 9999999;
					miny[i] = 9999999;
					maxx[i] = -9999999;
					maxy[i] = -9999999;
					visible[i] = false;
					doiNames[i] = "";
				}
				
				for (int i = 0; i < currentMask.getWidth(); i++)
					for (int j=0; j < currentMask.getHeight(); j++){
						
						//find if the pixel color matches an object color
						Color c = new Color(currentMask.getRGB(i, j));				
						for (int k=0; k<com1.length; k++)
							if (com1[k][0] == c.getRed() && com1[k][1] == c.getGreen() && com1[k][2] == c.getBlue()){
								visible[k] = true;
								if (i<minx[k]) minx[k] = i;
								if (j<miny[k]) miny[k] = j;
								if (i>maxx[k]) maxx[k] = i;
								if (j>maxy[k]) maxy[k] = j;
							}
					}
				
				for (int i=0; i<com1.length; i++){				
					if (visible[i]){
						
						double centerX = (maxx[i] + minx[i]) / 2.;
						double centerY = (maxy[i] + miny[i]) / 2.;
						int width = maxx[i] - minx[i] + 1;
						int height = maxy[i] - miny[i] + 1;
						
						if (this.withCutouts){
							BufferedImage cutout = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);
							cutout.createGraphics().drawImage(currentImage,0,0,width-1,height-1,minx[i],miny[i], maxx[i], maxy[i],null);
							for (int xx=0; xx<width; xx++)
								for (int yy=0; yy<height; yy++){
									Color c = new Color(currentMask.getRGB(xx+minx[i], yy+miny[i]));
									if (!(com1[i][0] == c.getRed() && com1[i][1] == c.getGreen() && com1[i][2] == c.getBlue())){
										cutout.setRGB(xx, yy, (0 << 24) | (0 << 16) | (0 << 8) | 0);
								}
								}
									
							try {
								String filename = folder + "/proc/cutout" + (cutoutIndex++) + ".png";
								ImageIO.write(cutout, "PNG", new File(filename));
								doiNames[i] = filename;
							} catch (IOException e) {
								e.printStackTrace();
							}
						}						

					}
				}

								
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
					
		
		//advance time
		outstream.println();
		if (withCutouts)
			outstream.print("T=" + gazeT[cgc]);
		else
			outstream.print("T=" + gazeT[cgc] + " || screenImage=" + ("frame" + cutoutIndex + ".png"));
		processTime(this.outstream, gazeX[cgc], gazeY[cgc], visible, minx, miny, maxx, maxy, doiNames, doiProps);

		
		requestRender();
		return true;
	}
	
	private void processTime(PrintStream out, int x, int y, boolean[] visible, int[] minx, int[] miny, int[] maxx, int[] maxy, String[] doiNames, String[] doiProps){
	
		//go through all objects, detect if they were viewed, print their properties, save their outlines
		for (int i=0; i<com1.length; i++){
			out.println();
			out.print(com2[i]);
			if (visible[i]) out.print(" || visible = true");
			else out.print(" || visible=false");
			
			if (visible[i]){
				
				double centerX = (maxx[i] + minx[i]) / 2.;
				double centerY = (maxy[i] + miny[i]) / 2.;
				int width = maxx[i] - minx[i] + 1;
				int height = maxy[i] - miny[i] + 1;
								
				double dToCenter = Math.sqrt((centerX - x)*(centerX-x) + (centerY-y)*(centerY-y));
				out.print(" || viewed=" + dToCenter);
				
				out.print(" || center=(" + centerX + "," + centerY + ")");
				out.print(" || size=(" + width + "," + height + ")");
					
				if (this.withCutouts)
					out.print(" || cutout=" + doiNames[i]);	
			}
			out.print(" || " + doiProps[i]);
		}


		


	}

	@Override
	public void render(Graphics2D g) {
		if (currentMask != null)
			g.drawImage(currentMask, 0,0,null);
		if (cgc > 0){
			g.setColor(new Color(200,200,100));
			g.fillOval(gazeX[cgc]-5, gazeY[cgc]-5, 10,10);
		
			//print time and index
			g.setColor(Color.magenta);			
			g.drawString("Time: "+this.gazeT[cgc]+", index:"+cgc,50, 600);
		}
		
	}
	
	public void computeAllVisible(){
		boolean[] allDet = null;
		for (int i=0; i<imageNames.length; i++){
			BufferedImage im;
			try {
				im = ImageIO.read(new File(imageNames[i]));
			
				boolean[] det = computeVisible(im);
				if (allDet == null) allDet = det;
				else{
					for (int j=0; j<det.length; j++)
						allDet[j] = allDet[j] || det[j];
				}
				
				int cnt = 0;
				for (int j=0; j<allDet.length; j++){
					System.out.println(" -------------------------------------------  " + j + " --- " + com2[j] + " --- " + allDet[j]);
					
					if (allDet[j])
						cnt++;
				}
				
				System.out.println("Processed image " + i + " of " + imageNames.length + ": " + cnt);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		for (int i=0; i<allDet.length; i++)
			System.out.println("Viewed\t" + com2[i] + "\t" + allDet[i]);
	}
	private boolean[] computeVisible(BufferedImage im){
		boolean[] detected = new boolean[com1.length];
		for (int i=0; i<detected.length; i++)
			detected[i] = false;
		
		for (int i=0; i<im.getWidth(); i++)
			for (int j=0; j<im.getHeight(); j++){				
				
				for (int k=0; k<com1.length; k++){			
					Color c = new Color(im.getRGB(i, j));	
					if (com1[k][0] == c.getRed() && com1[k][1] == c.getGreen() && com1[k][2] == c.getBlue()){
						detected[k] = true;
						break;
					}
				}
			}
		return detected;	
	}

	@Override
	public Color getBackgroundColor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean mousepressed(int x, int y, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mousereleased(int x, int y, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mousemoved(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mousedragged(int currentx, int currenty, int oldx, int oldy) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void keyPressed(String key, String modifiers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(String key, String modifiers) {
		// TODO Auto-generated method stub
		
	}
	
	

}