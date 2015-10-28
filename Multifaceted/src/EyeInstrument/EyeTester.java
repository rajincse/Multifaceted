package EyeInstrument;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.Executors;

import perspectives.base.Viewer;
import perspectives.two_d.JavaAwtRenderer;
import eyetrack.*;

import com.sun.net.httpserver.*;

class Elem{
	int x, y, w, h;
	String id;
	
	ArrayList<String> categories;
	HashMap<String, String> properties ;
	public Elem(String id, int x, int y, int w, int h){
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.id = id;
		categories = new ArrayList<String>();
		properties = new HashMap<String, String>();
	}
	
	public HashMap<String, String> getProperties()	
	{
		return properties;
	}
	
	public void addProperty(String key, String value)
	{
		properties.put(key, value);
	}
	
	public String getProperty(String key)
	{
		if(properties.containsKey(key))
		{
			return properties.get(key);
		}
		else
		{
			return "";
		}
	}
	public Point getCenter(){
		return new Point(x + w/2, y+w/2);
	}
	
	public void addCategory(String cat){
		categories.add(cat);
	}
	public void removeCategory(String cat){
		int index = categories.indexOf(cat);
		if (index  < 0) categories.remove(index);
	}

	@Override
	public String toString() {
		return "Elem [x=" + x + ", y=" + y + ", w=" + w + ", h=" + h + ", id="
				+ id + ", categories=" + categories +"properties="+properties+ "]";
	}
	
}

class Transition{
	String from, to;
	double prob = 1;
	public Transition(String f, String t, double prob){
		from = f;
		to = t;
		this.prob = prob;
	}
}


public class EyeTester extends Viewer implements JavaAwtRenderer, EyeTrackerDataReceiver{
	
	 boolean debug = true;
	 
	 Rectangle window = null;
	 AffineTransform tranform = new AffineTransform();
	 
	 ArrayList<Point2D> gazes = new ArrayList<Point2D>();
	 
	 boolean hasFocus = false;
	
	
	//a gaze score history (one array for each element)
	ArrayList<ArrayList<Double>> gs = new ArrayList<ArrayList<Double>>();
	//a probability score history (maybe don't need a whole history)
	ArrayList<ArrayList<Double>> ps = new ArrayList<ArrayList<Double>>();
	//and a final score history
	ArrayList<ArrayList<Double>> fs = new ArrayList<ArrayList<Double>>();
	
	//Point gaze = null;
	int radius = 200;
	
	
	ArrayList<Elem> elems = new ArrayList<Elem>();
	ArrayList<Transition> trans = new ArrayList<Transition>();
	
	double originX = 0;
	double originY = 0;
	
	double scale=1;

	public EyeTester(String name) {
		super(name);
		
		/*addElement("e1", 100, 100, 20, 20);
		addElement("e2", 100, 130, 20, 20);
		addElement("e3", 300, 100, 20, 20);
		addElement("e4", 300, 130, 20, 20);
		addElement("e5", 100, 500, 20, 20);
		addElement("e6", 300, 500, 20, 20);
		
		getElem("e1").addCategory("red");
		getElem("e3").addCategory("red");
		
		setTrans("red", "red", 5);*/
		
		try {
			System.out.println("bla");
			HttpServer server = HttpServer.create(new InetSocketAddress(9999), 1);
			
			
			
			 server.createContext("/apa", new HttpHandler(){

				@Override
				public void handle(HttpExchange arg0) throws IOException {
					
					
					String s=arg0.getRequestURI().getQuery();
					
					System.out.println(s); 
					processCommands(s);
										
					 String response = "This is the response";
			          arg0.sendResponseHeaders(200, response.length());
			           OutputStream os = arg0.getResponseBody();
			           os.write(response.getBytes());
			           os.close();
					
				}
				 
			 });
			 server.setExecutor(Executors.newCachedThreadPool()); // creates a default executor
			 server.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		EyeTrackServer s = new EyeTrackServer(this);
		
		
	}
	
	
	public void addElement(String id, int x, int y, int w, int h){
		synchronized(this){
			elems.add(new Elem(id, x,y,w,h));
			gs.add(new ArrayList<Double>());
			ps.add(new ArrayList<Double>());
			fs.add(new ArrayList<Double>());
			
		}
	}
	public void addElemProperty(String id, String propertyKey, String propertyValue)
	{
		Elem elem = getElem(id);
	}
	public Elem getElem(String id){
		for (int i=0; i<elems.size(); i++)
			if (elems.get(i).id.equals(id)) return elems.get(i);
		return null;
	}
	
	public void reshapeElem(String id, int x, int y, int w, int h){
		Elem e = getElem(id);
		if (e != null){
			e.x = x; e.y = y; e.w = w; e.h = h;
		}
		requestRender();
	}
	
	public Transition getTransition(String from, String to){
		for (int i=0; i<trans.size(); i++)
			if (trans.get(i).from.equals(from) && trans.get(i).to.equals(to))
				return trans.get(i);
		return null;
	}
	
	public void setTrans(String from, String to, double p){
		Transition t = getTransition(from, to);
		if (t == null){
			t = new Transition(from, to, p);
			trans.add(t);
		}
		else t.prob = p;
	}
	
	public double getTransition(Elem e1, Elem e2){
		double prob = 1;
		for (int i=0; i<e1.categories.size(); i++)
			for (int j=0; j<e2.categories.size(); j++){
				Transition t = getTransition(e1.categories.get(i), e2.categories.get(j));
				if (t != null) prob *= t.prob;
			}
		return prob;
	}
	
	private void removeAllElements()
	{
		synchronized (this) {
			this.elems.clear();
			this.gs.clear();
			this.ps.clear();
			this.fs.clear();
		}
		
	}

	@Override
	public void render(Graphics2D g) {
//		g.setTransform(this.tranform);
		g.setColor(Color.LIGHT_GRAY);
		g.drawLine(200, 50, 200, 250);
		
	//	AffineTransform savedt = g.getTransform();
		//g.setTransform(tranform);
		for (int i=0; i<elems.size(); i++){
			g.setColor(Color.LIGHT_GRAY);
			if(elems.get(i) != null)
			{
				g.drawRect(elems.get(i).x, elems.get(i).y, elems.get(i).w, elems.get(i).h);
				g.setColor(Color.black);
				Elem elem = elems.get(i);
				if(elem.properties.containsKey("type"))
				{
					String type = elem.getProperty("type");
					if(type.equalsIgnoreCase("text"))
					{
						g.setColor(Color.pink);
					}
					else if(type.equalsIgnoreCase("image"))
					{
						g.setColor(Color.magenta);
					}
					else if(type.equalsIgnoreCase("button"))
					{
						g.setColor(Color.orange);
					}
				}
				g.drawString(elems.get(i).id, elems.get(i).x, elems.get(i).y + elems.get(i).h - 5);
			}
			
			
			///*
			if (
					(gs.get(i) != null && gs.get(i).size() == 0)
				|| (ps.get(i) != null && gs.get(i).size() == 0)
				|| (fs.get(i) != null && gs.get(i).size() == 0)
				)
			{
				continue;
			}

			double gg = (Double)gs.get(i).get(gs.get(i).size()-1);
			double p = (Double)ps.get(i).get(ps.get(i).size()-1);
			double f = (Double)fs.get(i).get(fs.get(i).size()-1);
			
			//Sayeed, please print your debug numbers like this.
			String s = (new DecimalFormat("##.00")).format(gg) + "; " + 
			(new DecimalFormat("##.00")).format(p) + " (" + (new DecimalFormat("##.00")).format(transfp(p)) +  "); " + 
					(new DecimalFormat("##.00")).format(f);
			g.drawString(s, elems.get(i).x+10, elems.get(i).y);
			//*/
		}
		
		//AffineTransform
		/*
		if (gaze != null){
			g.fillOval(gaze.x-5, gaze.y-5, 10,10);
			g.drawOval(gaze.x-radius, gaze.y-radius, 2*radius, 2*radius);
		}
		//*/
		
		for (int i=0; i<gazes.size(); i++){
			g.setColor(new Color(0,0,250,50));
			g.fillOval((int)gazes.get(i).getX()-2, (int)gazes.get(i).getY()-2, 4, 4);
		}
		
		g.setColor(Color.red);
		g.drawLine(0, 0, 1280, 0);
		g.drawLine(0, 0, 0, 720);
		g.translate(-originX, -originY);
	}

	@Override
	public Color getBackgroundColor() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public void processGaze(double x, double y){
		if (window == null || !hasFocus || gs.isEmpty() || gs.size() != elems.size())
			return;
		
		x = x - window.x;
		y = y - window.y;
		
		if (x > window.width || y > window.height) return;
		
		try{
			Point2D p = this.tranform.inverseTransform(new Point2D.Double(x,y),null);
			x = p.getX();
			y = p.getY();
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		
		gazes.add(new Point2D.Double(x,y));
		
		int t = gs.get(0).size()-1;	
		
		Point2D gaze = new Point2D.Double(x,y);
		
		//find non-null gaze scores for all elements
		for (int i=0; i<elems.size(); i++){
			double d = gaze.distance(elems.get(i).getCenter());
			
			if (d > radius) 
				gs.get(i).add(new Double(0));
			else
				gs.get(i).add(new Double((1.- d/radius)));
		}
		
	
		if (t < 0){ //first time we initialize everything
			for (int i=0; i<elems.size(); i++){
				ps.get(i).add(new Double(1));
				fs.get(i).add(gs.get(i).get(t+1));
			}
			return;
		}
		
		
		//find previously viewed candidates (see avScore method)
		double[] prevScores = new double[elems.size()];
		for (int i=0; i<elems.size(); i++)
			prevScores[i] = avScore(i);

			
		//we will compute transition probabilities for all elements with non-zero gaze scores
		double[] transProbs = new double[elems.size()];
		for (int i=0; i<elems.size(); i++){
			
			if ((Double)gs.get(i).get(t+1) <= 0 ){
				ps.get(i).add(new Double(0));
				fs.get(i).add(new Double(0));
				continue;
			}
			
			//from here on, this is a potentially viewed element; we will use transition probabilities to dicreminate between other potentially viewed elements
			
			double sumProb = 0;
			double sumCnt = 0;
			for (int j=0; j<elems.size(); j++){
				if (prevScores[j] == 0 || i==j) continue;
								
				//if object i is in competition with object j, then j cannot be used to discreminate!!!!				
				if ((Double)gs.get(j).get(t+1) != 0) continue;
								
				sumProb += prevScores[j] * getTransition(elems.get(j), elems.get(i));
				sumCnt += prevScores[j];
			}
			

			if (sumCnt != 0)
				transProbs[i] = sumProb / sumCnt;
			else
				transProbs[i] = 0;
			
		}
		
		//transProbs now contains probabilities for all elements with non-zero gazes;
		double sum = 0;
		double mx = 0;
		for (int i=0; i<transProbs.length; i++)
			sum += transProbs[i];
		
		for (int i=0; i<transProbs.length; i++){
			transProbs[i] /= sum;
			mx = Math.max(mx, transProbs[i]);
		}
		
		for (int i=0; i<elems.size(); i++){
			double g = (Double)gs.get(i).get(t+1);
			
			if (g <= 0) continue;
			
			double p = transProbs[i] / mx;
			if (sum == 0) p = 1;
		
			double f = g*transfp(p);
			
			ps.get(i).add(new Double(p));
			fs.get(i).add(new Double(f));
		}
	}
	
	
	//looks back in the final score history, through the most recent 'recentSize' number of samples, and returns the 'window' with the highest average score 
	public double avScore(int e){
		int windowSize = 5;
		int recentSize = 20;
		
		//if we don't have enough samples for a window, return;
		if (fs.get(e).size() < windowSize) return 0;
		
		//otherwise, slide the window backgrounds a compute average scores within the window; record the maximum average
		double maxAv = 0;
		for (int k=0; k<recentSize; k++){
			if (fs.get(e).size()-k-windowSize < 0)
				break;
			
			double av = 0;
			for (int i=fs.get(e).size()-k-windowSize; i<fs.get(e).size()-k; i++)
				av += (Double)fs.get(e).get(i);
			av /= windowSize;
			
			if (av > maxAv)
				maxAv = av;
		}
		
		return maxAv;
	}

	@Override
	public boolean mousepressed(int x, int y, int button) {
		if (debug)
			processGaze(x,y);		
		
		
		return true;
	}
	
	private double transfp(double d){
		return 0.1 + 0.9*d;
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

	
	public void processCommands(String command){
		try
		{
			String[] split1 = command.split("command=");
			
			for (int i=0; i<split1.length; i++)
			{
				split1[i] = split1[i].trim();
				if (split1[i].length() == 0) continue;
			
				String[] split = split1[i].split("_");
			
				if (split[0].equals("window"))
					window = new Rectangle(Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]));
				else if (split[0].equals("translate"))
				{	
					
					double newOriginX= Double.parseDouble(split[1]);
					double newOriginY= Double.parseDouble(split[2]);
					
					double translateX = newOriginX - originX;
					double translateY = newOriginY - originY;
					
					originX = newOriginX;
					originY = newOriginY;
					
					this.tranform.translate(translateX, translateY);
					requestRender();
				}	
				else if (split[0].equals("scale"))
				{
					double newScale = Double.parseDouble(split[1]);
					
					double transformScale = newScale/ scale;
					
					scale = newScale;
					
					this.tranform.scale(transformScale,transformScale);
					requestRender();
				}
									
				else if (split[0].equals("addElem"))
				{	
					try
					{
					
						String id = split[1];
						
						int x = (int)Double.parseDouble(split[2]);
						int y = (int)Double.parseDouble(split[3]);
						int w = (int)Double.parseDouble(split[4]);
						int h = (int)Double.parseDouble(split[5]);
						this.addElement(id,x,y,w,h);
					
					
					}
					catch(NumberFormatException ex)
					{
						System.err.println(" Problem at :"+split1[i]);
					}
					
				}
				else if (split[0].equals("addProperty"))
				{	
					Elem elem =this.getElem(split[1]); 
					String propertyString = split[2];
					
					String[] properties = propertyString.split("&");
					for(String property: properties)
					{
						String[] keyValue = property.split("=");
						String key = keyValue[0];
						String value = keyValue[1];
						elem.addProperty(key, value);
					}
					System.out.println(elem);
				}
				else if (split[0].equals("addCategory"))
				{	
					Elem elem =this.getElem(split[1]); 
					elem.addCategory(split[1]);
				}
				else if (split[0].equals("removeAllElem"))
				{	
					this.removeAllElements();
					this.requestRender();
				} 
				else if (split[0].equals("reshapeElem"))
				{	
					String id = split[1];
					Elem elem =this.getElem(id);
					if(elem != null)
					{
						int x = elem.x;
						int y = elem.y;
						int w = elem.w;
						int h = elem.h;
						
						if(split.length >= 3)
						{
							x = (int)Double.parseDouble(split[2]);
						}
						if(split.length >= 4)
						{
							y = (int)Double.parseDouble(split[3]);
						}
						if(split.length >= 5)
						{
							w = (int)Double.parseDouble(split[4]);
						}
						if(split.length >= 6)
						{
							h =(int) Double.parseDouble(split[5]);
						}
						
						this.reshapeElem(id, x, y, w, h);
					}
				}
				else if (split[0].equals("setTransition"))
					this.setTrans(split[1], split[2], Double.parseDouble(split[3]));
				else if (split[0].equals("gainedfocus"))
					hasFocus = true;
				else if (split[0].equals("lostfocus"))
					hasFocus = false;
				//else if (split[0].equals("gaze"))
				//	this.processGaze(Integer.parseInt(split[1]), Integer.parseInt(split[2]));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}


	@Override
	public void processGaze(Point gazePoint, double pupilDiameter) {
		this.processGaze(gazePoint.x, gazePoint.y);
		
		requestRender();
		
		
		//System.out.println("received gaze");
		
	}

}
