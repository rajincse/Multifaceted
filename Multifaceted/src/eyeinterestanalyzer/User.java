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

import eyeinterestanalyzer.event.Event;
import eyeinterestanalyzer.event.EyeEvent;
import eyeinterestanalyzer.event.GazeEvent;
import eyeinterestanalyzer.event.HoverEvent;
import eyeinterestanalyzer.event.ImageEvent;
import eyeinterestanalyzer.event.MouseButtonEvent;
import eyeinterestanalyzer.event.MouseMoveEvent;
import eyeinterestanalyzer.event.ViewEvent;

public class User{
	public String name;
	
	public ArrayList<Event> events;
	ArrayList<DataObject> dataObjects;
	
	
	public ArrayList<Point> gazes;
	public ArrayList<Point> mouses;
	
	public String hoverString = "";
	public String eyeString = "";
	
	public BufferedImage image = null;
	
	public BufferedImage heatmap = null;
	public ArrayList<DataObject> viewedObjects;
	
	public int cellWidth = 1;
	public int cellHeight = 10;
	
	public long currentTime=0;
	int timeStep=600;
	
	public long timePeriodStart = 0;
	public long timePeriodEnd = 0;
	
	HashMap<DataObject, Integer> dataToIndex;
	
	public ArrayList<ViewEvent> viewEvents;
	public ArrayList<HoverEvent> hoverEvents;
	public ArrayList<Event> mouseEvents;
	
	public int heatmapXOffset = 200;
	
	boolean isAggregated;
	
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
		this("dummy", false);
		loadUser(f.getAbsolutePath());
	}
	
	public void loadUser(String path){
		
		try {		
		
		String folder = path.substring(0,path.lastIndexOf("\\")) + "\\";		
		BufferedReader br = new BufferedReader(new FileReader(path));
		String prevView = "";
			
	    String line;
	    long startTime = -1;
	        while ((line = br.readLine()) != null) {
	            
	        	String[] split = line.split("\t");
	        	
	        	if (startTime < 0)
	        		startTime =  Long.parseLong(split[1]);
	        	
	        	if (split[0].equals("Eye")){
	        		long t = Long.parseLong(split[1]) - startTime;
	        		double s = Double.parseDouble(split[5]);
	        		double p = Double.parseDouble(split[6]);
	        		
	        		String objId = split[2];
	        		DataObject object = null;
	        		for (int i=0; i<dataObjects.size(); i++)
	        			if (dataObjects.get(i).id.equals(objId)){
	        				object = dataObjects.get(i);
	        				break;
	        			}
	        		if (object == null){
	        			object = new DataObject(objId, split[3], split[4]);
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
	        			object = new DataObject(objId, split[3], split[4]);
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
	        timePeriodEnd = events.get(events.size()-1).getTime();
	        createHeatmap();
	    }
		catch(Exception e){
			e.printStackTrace();
	    }
	}
	
	
	public void createHeatmap(){
		
		long ts = timePeriodStart;
		long te = timePeriodEnd;
		
		System.out.println("creating heatmap ..");
		
		long lastTime = events.get(events.size()-1).getTime();
		
		ArrayList[][] eventMap = new ArrayList[dataObjects.size()][];
		
		for (int i=0; i<eventMap.length; i++){
			eventMap[i] = new ArrayList[(int)(lastTime/timeStep + 1)];
			for (int j=0; j<eventMap[i].length; j++)
				eventMap[i][j] = new ArrayList<Event>();
		}		

		
		for (int i=0; i<events.size(); i++)
			if (events.get(i) instanceof EyeEvent){
				EyeEvent e = (EyeEvent)events.get(i);
				
				if (e.getTime() < ts || e.getTime() > te)
					continue;
				
				int dataIndex = dataToIndex.get(e.target);
				int timeIndex = (int)(e.getTime()/ timeStep);

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
				for (int k=0; k<l.size(); k++)
					sum += l.get(k).score;
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
				if (heatmap[i][j] < avgCell)
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
				if (heatmap[i][j] != 0)
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
			if (heatmapAvg[i] < avg){
				firstIndex[i] = -1;
				heatmapAvg[i] = 0;
			}
		
		
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
		}
		
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
			
			long tt = events.get(i).getTime();
			
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
		if (timePeriodEnd > events.get(events.size()-1).getTime()) timePeriodEnd = events.get(events.size()-1).getTime();
		if (timePeriodEnd < timePeriodStart + timeStep) timePeriodEnd = timePeriodStart + timeStep;	
	}
	
	public void setCellWidth(int cw){
		cellWidth = cw;
		createHeatmap();
	}
	
	public void setTimeStep(int ts){
		timeStep = ts;
		createHeatmap();
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
				
				EyeEvent enew = new EyeEvent(e.getTime(), tnew, ((EyeEvent) e).score, ((EyeEvent) e).prob);
				events.add(enew);
			}
		}
		
		timePeriodStart = 0;
		timePeriodEnd = events.get(events.size()-1).getTime();
		
		createHeatmap();
	}
}
