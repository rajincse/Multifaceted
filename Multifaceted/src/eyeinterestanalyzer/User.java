package eyeinterestanalyzer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import eyeinterestanalyzer.clustering.ClusteringStringItem;

import multifaceted.Util;

public class User implements ClusteringStringItem{
	String name;
	
	ArrayList<Event> events;
	ArrayList<DataObject> dataObjects;
	
	
	ArrayList<Point> gazes;
	ArrayList<Point> mouses;
	
	String hoverString = "";
	String eyeString = "";
	
	BufferedImage image = null;
	
	BufferedImage heatmap = null;
	ArrayList<DataObject> viewedObjects;
	
	BufferedImage scarfplot=null;
	
	double[][] heatmapVal;
	
	int cellWidth = 1;
	int cellHeight = 12;
	
	long currentTime=0;
	int timeStep=600;
	
	long timePeriodStart = 0;
	long timePeriodEnd = 0;
	
	boolean withProb = true;
	
	HashMap<DataObject, Integer> dataToIndex;
	
	ArrayList<ViewEvent> viewEvents;
	ArrayList<HoverEvent> hoverEvents;
	ArrayList<Event> mouseEvents;
	
	ArrayList<ArrayList<String>> vectorLabel;
	ArrayList<ArrayList<Double>> vectorLabelScores;
	
	int heatmapXOffset = 200;
	
	boolean isAggregated;
	
	ArrayList< ArrayList<String>> coding;
	
	double cellFilter = 1.;
	double rowFilter = 1.;
	
	String sort = "First Viewed";
	
	public User(String name, boolean ag){
		isAggregated = ag;
		
		this.name = name;
		
		gazes = new ArrayList<Point>();
		mouses = new ArrayList<Point>();
		
		events = new ArrayList<Event>();
		
		viewEvents = new ArrayList<ViewEvent>();
		hoverEvents = new ArrayList<HoverEvent>();
		mouseEvents = new ArrayList<Event>();
		
		dataObjects = new ArrayList<DataObject>();
		
		dataToIndex = new HashMap<DataObject, Integer>();
	}
	
	public User(File f){
		
		this(f.getName(), false);
		loadUser(f.getAbsolutePath());
	}
	
	public void loadUser(String path){
		
		try {		
		
		String folder = path.substring(0,path.lastIndexOf("\\")) + "\\";		
		BufferedReader br = new BufferedReader(new FileReader(path));
		String prevView = "";
		
		Point prevGazePos = null;
		long prevGazeTime = 0;
		double gazeSpeed = 0;
			
	    String line;
	    long startTime = -1;
	        while ((line = br.readLine()) != null) {
	            
	        	String[] split = line.split("\t");
	        	
	        	if (startTime < 0)
	        		startTime =  Long.parseLong(split[1]);
	        	
	        	if (split[0].equals("Eye")){
	        		
	        		//if (gazeSpeed > 70) 
	        		//	continue;
	        		
//	        		System.out.println(line);
	        		long t = Long.parseLong(split[1]) - startTime;
	        		double s = Double.parseDouble(split[5]);
	        		double p = Double.parseDouble(split[8]);
	        		
	        	//	if (p >= 0)
	        	//		s  = s / p;
	        	//	if (s < 0 || s > 1) System.out.println("score: " + s + " ; " + line);
	        		if (split[4].trim().equals("5")) continue;
	        	
	        		
	        		String objId = split[2];
	        		DataObject object = null;
	        		for (int i=0; i<dataObjects.size(); i++)
	        			if (dataObjects.get(i).id.equals(objId)){
	        				object = dataObjects.get(i);
	        				break;
	        			}
	        		if (object == null){
	        			object = new DataObject(objId, split[3], Integer.parseInt(split[4].trim()));
	        			dataObjects.add(object);
	        		}	        		
	        		
	        		Event e = new EyeEvent(t,object, s, p);
	        		events.add(e);
	        	}
	        	else if (split[0].equals("HoverIn") || split[0].equals("HoverOut")){
	        		long t = Long.parseLong(split[1]) - startTime;
	        		
	        		String objId = split[2];
	        		DataObject object = null;
	        		for (int i=0; i<dataObjects.size(); i++)
	        			if (dataObjects.get(i).id.equals(objId)){
	        				object = dataObjects.get(i);
	        				break;
	        			}
	        		if (object == null){
	        			object = new DataObject(objId, split[3], Integer.parseInt(split[4].trim()));
	        			dataObjects.add(object);
	        		}
	        		
	        		HoverEvent e = new HoverEvent(t, split[0].equals("HoverIn"), object);
	        		hoverEvents.add(e);
	        	}
	        	else if (split[0].equals("Gaze")){
	        		long t = Long.parseLong(split[1]) - startTime;
	        		int x = Integer.parseInt(split[2]);
	        		int y = Integer.parseInt(split[3]);
	        		Event e = new GazeEvent(t,x,y);
	        		events.add(e);
	        		
	        		if (prevGazePos != null){
	        			gazeSpeed = prevGazePos.distance(new Point(x,y));
	        			//System.out.println(gazeSpeed + " : "+ prevGazePos.x + " " + prevGazePos.y + " -> " + x + " " + y + "( " + (prevGazeTime-t) + ")");
	        		}
	        		prevGazePos = new Point(x,y);
	        		prevGazeTime = t;
	        	}
	        	else if (split[0].equals("MouseMove") || split[0].equals("MouseDrag")){
	        		long t = Long.parseLong(split[1]) - startTime;
	        		int x = Integer.parseInt(split[2]);
	        		int y = Integer.parseInt(split[3]);
	        		boolean drag = split[0].equals("MouseDrag");
	        		Event e = new MouseMoveEvent(t,drag,x,y);
	        		
	        		mouseEvents.add(e);
	        		
	        		events.add(e);
	        	}
	        	else if (split[0].equals("MouseUp") || split[0].equals("MouseDown")){
	        		long t = Long.parseLong(split[1])- startTime;
	        		int x = Integer.parseInt(split[2]);
	        		int y = Integer.parseInt(split[3]);
	        		boolean up = split[0].equals("MouseUp");
	        		Event e = new MouseButtonEvent(t,up,x,y);
	        		if (up) mouseEvents.add(e);
	        		
	        		events.add(e);
	        	}
	        	else if (split[0].equals("Image")){
	        		long t = Long.parseLong(split[1])- startTime;
	        		Event e = new ImageEvent(t,folder + split[2]);
	        		events.add(e);
	        		
	        		int secunderscore = split[2].indexOf("_", split[2].indexOf("_")+1);
	        		int lastunderscore = split[2].lastIndexOf("_");
	        		String view = split[2].substring(secunderscore+1, lastunderscore);
	        		int n = 0;
	        		for (; n<view.length(); n++)
	        			if (view.charAt(n) < '0' || view.charAt(n) > '9') break;
	        		
	        		view = view.substring(n, view.length());
	        		if (!view.equals(prevView)){
	        			ViewEvent ee = new ViewEvent(t, view);
	        			viewEvents.add(ee);
	        			prevView = view;
	        		}
	        	}	        	
	        	else     		System.out.println("Unrecognized event " + split[0]);
	        	
	        }
	        
			for (int i=0; i<dataObjects.size(); i++)
				dataToIndex.put(dataObjects.get(i), i);
	        
	        
	        br.close();
	        
	        timePeriodStart = 0;
	        timePeriodEnd = events.get(events.size()-1).time;
	        createHeatmap();
	        createScarfplot();
	    }
		catch(Exception e){
			e.printStackTrace();
	    }
	}
	private String stringValue="";
	private void calculateStringValue()
	{
		this.stringValue ="";
		long ts = timePeriodStart;
		long te = timePeriodEnd;
		
		System.out.println("creating scarfplot ..");
		
		long lastTime = events.get(events.size()-1).time;
		int totalTimeCells = (int)(lastTime/timeStep + 1);
		ArrayList[][] eventMap = new ArrayList[dataObjects.size()][];
		
		for (int i=0; i<eventMap.length; i++){
			eventMap[i] = new ArrayList[totalTimeCells];
			for (int j=0; j<eventMap[i].length; j++)
				eventMap[i][j] = new ArrayList<Event>();
		}		

		
		for (int i=0; i<events.size(); i++)
			if (events.get(i) instanceof EyeEvent){
				EyeEvent e = (EyeEvent)events.get(i);
				
				if (e.time < ts || e.time > te)
					continue;
				
				int dataIndex = dataToIndex.get(e.target);
				int timeIndex = (int)(e.time / timeStep);


				eventMap[dataIndex][timeIndex].add(e);
			}
		
		double[][] heatmap = new double[eventMap.length][];
		for (int i=0; i<heatmap.length; i++)
			heatmap[i] = new double[eventMap[i].length];
		
		double avgCell = 0;
		int nonZeroCellCount = 0;
		for (int i=0; i<heatmap.length; i++)
			for (int j=0; j<heatmap[i].length; j++){
				ArrayList<EyeEvent> l = eventMap[i][j];
				double sum = 0;
				if (l.size() > 0){
				for (int k=0; k<l.size(); k++){
					
					if (withProb)
						sum += l.get(k).score;
					else{
					if (l.get(k).prob >= 0)
						sum += l.get(k).score / l.get(k).prob;
					else
						sum += l.get(k).score;
					}
				}
				}
				heatmap[i][j] = sum/Math.max(l.size(),Math.sqrt(timeStep/1000.*60));
				
				if (heatmap[i][j] != 0){
					avgCell += heatmap[i][j];
					nonZeroCellCount++;
				}
			}
		avgCell /= nonZeroCellCount;
		
		for (int i=0; i<heatmap.length; i++)
			for (int j=0; j<heatmap[i].length; j++)
				if (heatmap[i][j] < cellFilter*avgCell)
					heatmap[i][j] = 0;
		
		
		
		int maxIndex =0;
		double maxVal =- 1;
		for(int j=0;j<totalTimeCells;j++)
		{
			for(int i=0;i<heatmap.length;i++)
			{
				if(heatmap[i][j] > maxVal)
				{
					maxVal = heatmap[i][j];
					maxIndex = i;
				}
			}
			if(maxVal > 0)
			{
				DataObject object = dataObjects.get(maxIndex);
				this.stringValue += object.getStringValue();
			}
			maxVal =- 1;
		}
	}
	public void createScarfplot()
	{
		long ts = timePeriodStart;
		long te = timePeriodEnd;
		
		System.out.println("creating scarfplot ..");
		
		long lastTime = events.get(events.size()-1).time;
		int totalTimeCells = (int)(lastTime/timeStep + 1);
		ArrayList[][] eventMap = new ArrayList[dataObjects.size()][];
		
		for (int i=0; i<eventMap.length; i++){
			eventMap[i] = new ArrayList[totalTimeCells];
			for (int j=0; j<eventMap[i].length; j++)
				eventMap[i][j] = new ArrayList<Event>();
		}		

		
		for (int i=0; i<events.size(); i++)
			if (events.get(i) instanceof EyeEvent){
				EyeEvent e = (EyeEvent)events.get(i);
				
				if (e.time < ts || e.time > te)
					continue;
				
				int dataIndex = dataToIndex.get(e.target);
				int timeIndex = (int)(e.time / timeStep);


				eventMap[dataIndex][timeIndex].add(e);
			}
		
		double[][] heatmap = new double[eventMap.length][];
		for (int i=0; i<heatmap.length; i++)
			heatmap[i] = new double[eventMap[i].length];
		
		double avgCell = 0;
		int nonZeroCellCount = 0;
		for (int i=0; i<heatmap.length; i++)
			for (int j=0; j<heatmap[i].length; j++){
				ArrayList<EyeEvent> l = eventMap[i][j];
				double sum = 0;
				if (l.size() > 0){
				for (int k=0; k<l.size(); k++){
					
					if (withProb)
						sum += l.get(k).score;
					else{
					if (l.get(k).prob >= 0)
						sum += l.get(k).score / l.get(k).prob;
					else
						sum += l.get(k).score;
					}
				}
				}
				heatmap[i][j] = sum/Math.max(l.size(),Math.sqrt(timeStep/1000.*60));
				
				if (heatmap[i][j] != 0){
					avgCell += heatmap[i][j];
					nonZeroCellCount++;
				}
			}
		avgCell /= nonZeroCellCount;
		
		for (int i=0; i<heatmap.length; i++)
			for (int j=0; j<heatmap[i].length; j++)
				if (heatmap[i][j] < cellFilter*avgCell)
					heatmap[i][j] = 0;
		
	
		
		
		
		System.out.println("Time step:"+timeStep);
		
		
		int imWidth = cellWidth * totalTimeCells;
		int imHeight = cellHeight ;
		BufferedImage bim = new BufferedImage(imWidth, imHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bim.createGraphics();
		
		double totalVal=0;
		double [] typeVal = new double[6];
		for(int j=0;j<totalTimeCells;j++)
		{
			totalVal=0;
			for(int i=0;i<typeVal.length;i++)
			{
				typeVal[i] = 0;
			}
			
			
			for(int i=0;i<heatmap.length;i++)
			{
				if(heatmap[i][j] > 0)
				{
					DataObject object = dataObjects.get(i);
					typeVal[object.type]+= heatmap[i][j];
				}
			}
			
			for(int i=0;i<typeVal.length;i++)
			{
				totalVal+= typeVal[i];
						
			}
			if(totalVal > 0)
			{
				int lastY=0;
				for(int i=0;i<typeVal.length;i++)
				{
					if(typeVal[i] > 0)
					{
						Color c =   Util.getColor(i);
						Color c1 = new Color(c.getRed(), c.getGreen(), c.getBlue());

						g.setColor(c1);
						
						int height =(int)( typeVal[i]* cellHeight/totalVal);
						g.fillRect(j*cellWidth, lastY, cellWidth, height);
						lastY+= height;
					}
					
				}
				
			}
			
		}

		
		System.out.println("done creating scarfplot ..");
		this.scarfplot = bim;
		
		
		
	}
	public void createHeatmap(){
		
		long ts = timePeriodStart;
		long te = timePeriodEnd;
		
		System.out.println("creating heatmap ..");
		
		long lastTime = events.get(events.size()-1).time;
		
		ArrayList[][] eventMap = new ArrayList[dataObjects.size()][];
		
		for (int i=0; i<eventMap.length; i++){
			eventMap[i] = new ArrayList[(int)(lastTime/timeStep + 1)];
			for (int j=0; j<eventMap[i].length; j++)
				eventMap[i][j] = new ArrayList<Event>();
		}		

		
		for (int i=0; i<events.size(); i++)
			if (events.get(i) instanceof EyeEvent){
				EyeEvent e = (EyeEvent)events.get(i);
				
				if (e.time < ts || e.time > te)
					continue;
				
				int dataIndex = dataToIndex.get(e.target);
				int timeIndex = (int)(e.time / timeStep);


				eventMap[dataIndex][timeIndex].add(e);
			}
		
		double[][] heatmap = new double[eventMap.length][];
		for (int i=0; i<heatmap.length; i++)
			heatmap[i] = new double[eventMap[i].length];
		
		double avgCell = 0;
		int nonZeroCellCount = 0;
		for (int i=0; i<heatmap.length; i++)
			for (int j=0; j<heatmap[i].length; j++){
				ArrayList<EyeEvent> l = eventMap[i][j];
				double sum = 0;
				if (l.size() > 0){
				for (int k=0; k<l.size(); k++){
					
					if (withProb)
						sum += l.get(k).score;
					else{
					if (l.get(k).prob >= 0)
						sum += l.get(k).score / l.get(k).prob;
					else
						sum += l.get(k).score;
					}
				}
				}
				heatmap[i][j] = sum/Math.max(l.size(),Math.sqrt(timeStep/1000.*60));
				
				if (heatmap[i][j] != 0){
					avgCell += heatmap[i][j];
					nonZeroCellCount++;
				}
			}
		avgCell /= nonZeroCellCount;
		
		for (int i=0; i<heatmap.length; i++)
			for (int j=0; j<heatmap[i].length; j++)
				if (heatmap[i][j] < cellFilter*avgCell)
					heatmap[i][j] = 0;
		
		
		
		double[] heatmapAvg = new double[heatmap.length];
		int[] firstIndex = new int[heatmap.length];
		int[] index = new int[heatmap.length];
		for (int i=0; i<heatmap.length; i++){
			heatmapAvg[i] = 0;
			firstIndex[i] = -1;
			index[i] = i;
			for (int j=0; j<heatmap[i].length; j++){
				heatmapAvg[i] += heatmap[i][j];
				if (heatmap[i][j] != 0 && firstIndex[i] < 0)
					firstIndex[i] = j;
			}
			heatmapAvg[i] /= heatmap[i].length;
		}
		
		double avg = 0;
		for (int i=0; i<heatmap.length; i++)
			if (heatmapAvg[i] != 0)
				avg+= heatmapAvg[i];
		avg /= heatmapAvg.length;
		
		for (int i=0; i<heatmap.length; i++)
			if (heatmapAvg[i] < rowFilter*avg){
				firstIndex[i] = -1;
				heatmapAvg[i] = 0;
			}
		
		
		if (sort.equals("First Viewed")){
		//sort by first index
		while (true){
			boolean sw = false;
			for (int i=0; i<firstIndex.length-1; i++)
				if (firstIndex[i] > firstIndex[i+1]){
					sw = true;
					int tmpi = firstIndex[i]; firstIndex[i] = firstIndex[i+1]; firstIndex[i+1] = tmpi;
					    tmpi = index[i];      index[i] = index[i+1];           index[i+1] = tmpi;
					double tmpd = heatmapAvg[i]; heatmapAvg[i] = heatmapAvg[i+1]; heatmapAvg[i+1] = tmpd;
				}
			if (!sw) break;
		}}
		
		if (sort.equals("Most Viewed") || sort.equals("Category")){	
		//sort by activity
		while (true){
			boolean sw = false;
			for (int i=0; i<firstIndex.length-1; i++)
				if (heatmapAvg[i] < heatmapAvg[i+1]){
					sw = true;
					int tmpi = firstIndex[i]; firstIndex[i] = firstIndex[i+1]; firstIndex[i+1] = tmpi;
					    tmpi = index[i];      index[i] = index[i+1];           index[i+1] = tmpi;
					double tmpd = heatmapAvg[i]; heatmapAvg[i] = heatmapAvg[i+1]; heatmapAvg[i+1] = tmpd;
				}
			if (!sw) break;
		}}
		
		if (sort.equals("Category")){		
		//sort by type
		while (true){
			boolean sw = false;
			for (int i=0; i<firstIndex.length-1; i++)
				if (dataObjects.get(index[i]).type < dataObjects.get(index[i+1]).type){
					sw = true;
					int tmpi = firstIndex[i]; firstIndex[i] = firstIndex[i+1]; firstIndex[i+1] = tmpi;
					    tmpi = index[i];      index[i] = index[i+1];           index[i+1] = tmpi;
					double tmpd = heatmapAvg[i]; heatmapAvg[i] = heatmapAvg[i+1]; heatmapAvg[i+1] = tmpd;
				}
			if (!sw) break;
		}}
		
		
		
		viewedObjects = new ArrayList<DataObject>();
		for (int i=0; i<index.length; i++){
			if (heatmapAvg[i] == 0)
				dataObjects.get(index[i]).hidden = true;
			else{
				dataObjects.get(index[i]).hidden = false;
				viewedObjects.add(dataObjects.get(index[i]));
			}
		}
		
		int imWidth = cellWidth * heatmap[0].length;
		int imHeight = cellHeight * viewedObjects.size();
		BufferedImage bim = new BufferedImage(imWidth, imHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bim.createGraphics();
		int v = -1;
		for (int i=0; i<index.length; i++){
			if (dataObjects.get(index[i]).hidden)
				continue;
			
			v++;
			for (int j=0; j<heatmap[index[i]].length; j++){
				
				Color c = perspectives.util.Util.getColorFromRange(new Color[]{Color.white,Color.green,Color.yellow,Color.red}, heatmap[index[i]][j]);
				g.setColor(c);
				g.fillRect(j*cellWidth, v*cellHeight, cellWidth, cellHeight);
			}
		}
		
		System.out.println("done creating heatmap ..");
		this.heatmap = bim;
		
		//create the vector
		vectorLabel = new ArrayList<ArrayList<String>>();
		vectorLabelScores = new ArrayList<ArrayList<Double>>();
		for (int i=0; i<heatmap[0].length; i++){
			vectorLabel.add(new ArrayList<String>());
			vectorLabelScores.add(new ArrayList<Double>());
		}
		
		for (int i=0; i< heatmap[0].length; i++){
			double mx = 0;
			int mxj = -1;
			for (int j=0; j<index.length; j++){
				if (dataObjects.get(index[j]).hidden || dataObjects.get(index[j]).type == 5)
					continue;
				
				double val = heatmap[index[j]][i];
				
				if (val == 0) continue;
				
				ArrayList<String> a = vectorLabel.get(i);
				ArrayList<Double> vv = vectorLabelScores.get(i);
				int insertIndex = 0;
				for (int k=0; k<=a.size(); k++)
					if (k == a.size() || val > vv.get(k)){
						insertIndex = k;
						break;
					}
				
				a.add(insertIndex, dataObjects.get(index[j]).label.toLowerCase().trim());
				vv.add(insertIndex,val);

			}
			
			if (vectorLabel.get(i).size() == 0){
				vectorLabel.remove(i);
				vectorLabel.add(i, null);
			}
		}
		
	}
	
	
	public void changeTime(long t){
		
		gazes.clear();
		mouses.clear();

		hoverString = "";
		eyeString = "";
		
		image = null;
		
		currentTime = t;
		
		String imagePath = "";
		
		for (int i=0; i<events.size(); i++){
			
			long tt = events.get(i).time;
			
			if (events.get(i) instanceof GazeEvent){
				GazeEvent ge = (GazeEvent)events.get(i);
				
				if (t > tt && t - tt <= 100){
					gazes.add(new Point(ge.x, ge.y));
				}
			}
			else if (events.get(i) instanceof MouseMoveEvent){
				MouseMoveEvent ge = (MouseMoveEvent)events.get(i);
				
				if (t > tt && t - tt <= 100){
					mouses.add(new Point(ge.x, ge.y));
				}
			}
			else if (events.get(i) instanceof HoverEvent){
				HoverEvent he = (HoverEvent)events.get(i);
				
				if (t > tt && t - tt <= 50)
					hoverString = he.target.label;
			}
			else if (events.get(i) instanceof EyeEvent){
				EyeEvent ee = (EyeEvent)events.get(i);
				
				if (t > tt && t - tt <= 50)
					eyeString = (ee.target.label + "," + ee.score + "," + ee.prob + ";    ") + eyeString;				
			}
			
			else if (events.get(i) instanceof ImageEvent && t > tt){
				imagePath = ((ImageEvent)events.get(i)).path;
				
			}
		}
		
		if (imagePath != "")
			try {
				System.out.println("loading image: " + imagePath);
				image = ImageIO.read(new File(imagePath));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		
			
		}		
	}
	
	
	
	public int timeToPixel(long t){
		return heatmapXOffset+(int)(t/timeStep) * cellWidth;
	}
	public long pixelToTime(int p){
		return (p - heatmapXOffset)/cellWidth * timeStep;
	}
	
	public void setPeriodStart(long ts){
		timePeriodStart = ts;
		if (timePeriodStart < 0) timePeriodStart = 0;
		if (timePeriodStart > timePeriodEnd-timeStep) timePeriodStart = timePeriodEnd - timeStep;
	}
	
	public void setPeriodEnd(long te){
		timePeriodEnd = te;
		if (timePeriodEnd > events.get(events.size()-1).time) timePeriodEnd = events.get(events.size()-1).time;
		if (timePeriodEnd < timePeriodStart + timeStep) timePeriodEnd = timePeriodStart + timeStep;	
	}
	
	public void setCellWidth(int cw){
		cellWidth = cw;
		createHeatmap();
		createScarfplot();
		

	}
	
	public void setTimeStep(int ts){
		timeStep = ts;
		createHeatmap();
		createScarfplot();
		
	/*	ArrayList<ArrayList<String>> c = this.changeCodingTimeBase(ts);
		
		int start = 0;
		for (int i=0; i<c.size(); i++)
			if (c.get(i) != null && c.get(i).size() > 0){
				start = i;
				break;
				}
		
		try{
		PrintWriter writer = new PrintWriter("c:/vectorcomp.txt", "UTF-8");
	    double cnt = 0;
	    int bothnull = 0;
	    for (int i=start; i<c.size(); i++){
	    	String lab1 = "";
	    	String lab2 = "";
	    	double same = 0;
	    	if (this.vectorLabel.get(i) == null && c.get(i) == null){
	    		same = 1;
	    		bothnull++;
	    	}
	    	else if (vectorLabel.get(i) != null && c.get(i)!= null)
	    		same = rankDistance(vectorLabel.get(i), vectorLabelScores.get(i), c.get(i));
	
	    	
	    	if (vectorLabel.get(i) != null)
	    		lab1 = vectorLabel.get(i).get(0);
	    	if (c.get(i) != null)
		    	for (int j=0; j<c.get(i).size(); j++)
		    		lab2 += c.get(i).get(j) + ","; 
	    	
	    	cnt += same;
	    	
	    	System.out.println(cnt + " " + (c.size()-start) + " " + (cnt / (c.size() - start)) + " " + ((double)(cnt-bothnull) / (c.size() - start - bothnull)));
	    	writer.println(i + "\t" + lab1 + "\t" + lab2 + "\t" + same);
	    }
	    writer.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}*/
	}
	
	public double rankDistance(ArrayList<String> v1, ArrayList<Double> v1score, ArrayList<String> v2){
		
		String method = "top";
		if (method.equals("top")){
			if (v2.indexOf(v1.get(0)) >= 0)
				return 1;
			return 0;
		}
		else{
			double bestOption = 999999;
			for (int i=0; i<v2.size(); i++){
				int index = v1.indexOf(v2.get(i));
				if (index < 0) continue;				
				
				double d = Math.abs(1. - v1score.get(index));
				if (d < bestOption)
					bestOption = d;
			}
			if (bestOption == 999999)
				return 0;
			return 1- bestOption;
		}
	}
	
	
	public void addToAggregate(User u){
		if (!this.isAggregated) return;
		
		for (int i=0; i<u.events.size(); i++){
			Event e = u.events.get(i);
			
			if (e instanceof EyeEvent){
				DataObject t = ((EyeEvent) e).target;
				DataObject tnew = null;
				for (int j=0; j<dataObjects.size(); j++)
					if (dataObjects.get(j).id.equals(t.id)){
						tnew = dataObjects.get(j);
						break;
					}
				
				if (tnew == null){
					tnew = new DataObject(t.id, t.label, t.type);
					dataObjects.add(tnew);
					dataToIndex.put(tnew, dataObjects.size()-1);
				}
				
				EyeEvent enew = new EyeEvent(e.time, tnew, ((EyeEvent) e).score, ((EyeEvent) e).prob);
				events.add(enew);
			}
		}
		
		timePeriodStart = 0;
		timePeriodEnd = events.get(events.size()-1).time;
		
		createHeatmap();
		createScarfplot();
	}
	
	public void loadCoding(String path){
		try{
	    BufferedReader br = new BufferedReader(new FileReader(path));
	    String line;
	    
	    coding = new ArrayList< ArrayList<String> >();
	    while ((line = br.readLine()) != null) {
	    	System.out.println(line);
	    	String[] split = line.split("\t");
	    	int tm1 = Integer.parseInt(split[1])-1;
	    	int tm2 = Integer.parseInt(split[2])-1;
	    	String[] l = split[0].split(",");
	    
	    	for (int i = tm1; i<=tm2; i++){
	    		if (i >= coding.size()){
	    			i--;
	    			coding.add(null);
	    		}
	    		else{
	    			if (coding.get(i) == null){
	    				coding.remove(i);
	    				coding.add(i, new ArrayList<String>());
	    			}
	    			
	    			for (int k=0; k<l.length; k++)
	    			coding.get(i).add(l[k].trim().toLowerCase());
	   
	    		}
	    	}
	    }
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	ArrayList<ArrayList<String>> changeCodingTimeBase(int base){
		ArrayList<ArrayList<String>> c = new ArrayList<ArrayList<String>>();
		
		int totalTime = coding.size() * 100;
		int newsize = totalTime / base;
		
		for (int i=0; i<newsize; i++){
			ArrayList<String> t = new ArrayList<String>();
			c.add(t);
			
			int t1 = base * i;
			int t2 = base * (i+1);
			
			t1 = t1 / 100;
			t2 = (int)Math.ceil(t2 / 100.);
			
			for (int j=t1; j< t2; j++)
				for (int k=0; coding.get(j) != null && k<coding.get(j).size(); k++)
					t.add(coding.get(j).get(k));
			
			if (t.size() == 0){
				c.remove(c.size()-1);
				c.add(null);
			}
		}
		
		return c;
	}

	public void setCellFilter(double cf) {
		this.cellFilter = cf;
		this.createHeatmap();
		createScarfplot();
		
	}

	public void setRowFilter(double rf) {
		this.rowFilter = rf;
		this.createHeatmap();
		createScarfplot();
	}

	public void setSort(String sort) {
		this.sort = sort;
		this.createHeatmap();
		createScarfplot();
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{ name:"+name+", string:"+this.getStringValue().subSequence(0, 10)+" }";
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public String getStringValue() {
		// TODO Auto-generated method stub
		if(this.stringValue.isEmpty())
		{
			this.calculateStringValue();
		}
		return this.stringValue;
	}

	@Override
	public int getDistance(ClusteringStringItem otherItem) {
		// TODO Auto-generated method stub
		return LevenshteinDistance.getLevenshteinDistance(this.stringValue, otherItem.getStringValue());
	}

}
