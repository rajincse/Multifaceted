package eyetrack.radu;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.PriorityQueue;

import multifaceted.Util;
import eyetrack.EyeTrackServer;
import eyetrack.EyeTrackerDataReceiver;
import eyetrack.EyeTrackerItem;
import eyetrack.probability.EyeTrackerProbabilityViewer;
import eyetrack.probability.ProbabilityManager;
import eyetrack.probability.SortingItem;
import eyetrack.probability.StateAction;

public class EyeTrackerLabelDetectorRadu implements EyeTrackerDataReceiver{
	public static final long TIME_RESOLUTION=500;
	public static final long TIME_RESOLUTION_GAZE =100;
	private EyeTrackServer g;
	
	private ArrayList<EyeTrackerItem> elements=null;
	private boolean blocked = false;
	private EyeTrackerProbabilityViewer viewer = null;
	
	
	ArrayList<ArrayList<Double>> gs = new ArrayList<ArrayList<Double>>();
	//a probability score history (maybe don't need a whole history)
	ArrayList<ArrayList<Double>> ps = new ArrayList<ArrayList<Double>>();
	//and a final score history
	ArrayList<ArrayList<Double>> fs =new ArrayList<ArrayList<Double>>();
	
	
	
	public EyeTrackerLabelDetectorRadu(EyeTrackerProbabilityViewer viewer)
	{
		this.viewer = viewer;
		g = new EyeTrackServer(this);
	}
	public void registerElements(ArrayList<EyeTrackerItem> elements)
	{
		this.elements = elements;
		
		this.gs.clear();
		this.ps.clear();
		this.fs.clear();
		for(int i =0;i< elements.size();i++)
		{
			this.gs.add(new ArrayList<Double>());
			this.ps.add(new ArrayList<Double>());
			this.fs.add(new ArrayList<Double>());
		}
	}
	public ArrayList<EyeTrackerItem> getTopElements()
	{
		if(this.elements == null || this.elements.isEmpty())
		{
			return this.elements;
		}
		else
		{
			PriorityQueue<SortingItem> priorityQueue = new PriorityQueue<SortingItem>();
			double sum =0;
			for(EyeTrackerItem elem: this.elements)
			{
				SortingItem sortItem = new SortingItem(elem, elem.getScore());
				priorityQueue.add(sortItem);
				sum+= elem.getScore();
			}
			double mean = sum / elements.size();
			int totalCandidates = (int)(elements.size()* 0.1);
			ArrayList<EyeTrackerItem> topElements = new ArrayList<EyeTrackerItem>();
			int count =0;
			while(!priorityQueue.isEmpty() && count < totalCandidates )
			{
				SortingItem item = priorityQueue.poll();
			
				if(item.getValue() > 0)
				{
					topElements.add(item.getItem());
				}
				count++;
			}
			return topElements;
		}
		
	}
	@Override
	public void processGaze(Point gazePoint, double pupilDiameter) {
		// TODO Auto-generated method stub
//		System.out.println(System.currentTimeMillis()+"\t"+gazePoint.x+"\t"+gazePoint.y+"\t"+String.format("%.2f", pupilDiameter));
		
		processGaze(gazePoint);
	}
	public void processGaze(Point gazePoint) {
		// TODO Auto-generated method stub
		synchronized(this)
		{
			if (blocked) return;
		}
		try {
			Point offset = viewer.getEyeTrackOffset();
			Point processingPoint = new Point(gazePoint.x - offset.x, gazePoint.y - offset.y);
			
			Point screenPoint = new Point();
			
			AffineTransform transform = viewer.getTransform().createInverse();
			
			transform.transform(processingPoint, screenPoint);
			processScreenPoint(screenPoint);
		} catch (NoninvertibleTransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void processScreenPoint(Point screenPoint)
	{
		if(this.elements != null && !this.elements.isEmpty())
		{
			this.processElementGaze(screenPoint);
		}
		viewer.gazeDetected(screenPoint.x, screenPoint.y);
	}
	public void block(boolean block)
	{
		synchronized(this)
		{
			this.blocked = block;
		}		
	}
	
	
	private void processElementGaze(Point gazePoint)
	{
		if(this.elements.isEmpty())
		{
			return ;
		}
		double zoom =viewer.getZoom();
		
		int t = gs.get(0).size()-1;
		
		for(int i=0;i< this.elements.size();i++)
		{
			EyeTrackerItem element = this.elements.get(i);
			
			double gazeScore = element.getGazeScore(gazePoint, zoom);
			this.gs.get(i).add(gazeScore);
			
		}
		if (t < 0){ //first time we initialize everything
			for (int i=0; i<elements.size(); i++){
				ps.get(i).add(new Double(1));
				fs.get(i).add(gs.get(i).get(t+1));
			}
			return ;
		}
		
		double[] scores = new double[this.elements.size()];
		for (int i=0; i<this.elements.size(); i++)
		{
			scores[i] = avScore(i);
		}
		
		double[] transProbs = new double[this.elements.size()];
		for (int i=0; i<this.elements.size(); i++)
		{	
			if ((Double)gs.get(i).get(t+1) <= 0 ){
				ps.get(i).add(new Double(0));
				fs.get(i).add(new Double(0));
				continue;
			}
			
			//from here on, this is a potentially viewed element; we will use transition probabilities to dicreminate between other potentially viewed elements
			
			double sumProb = 0;
			double sumCnt = 0;
			for (int j=0; j<this.elements.size(); j++){
				if (scores[j] == 0 || i==j) continue;
								
				//if object i is in competition with object j, then j cannot be used to discreminate!!!!				
				if ((Double)gs.get(j).get(t+1) != 0) continue;
				double transition = this.getTransitionProbability(j, i);			
				sumProb += scores[j] * transition;
				sumCnt += scores[j];
			}
			

			if (sumCnt != 0)
				transProbs[i] =  sumProb / sumCnt;
			else
				transProbs[i] = 0;
			
			EyeTrackerItem item = this.elements.get(i);
			if (item.isHovered() || item.isIndirectlyHovered())
			{
				transProbs[i] = (transProbs[i] + 1)/2;
			}	
				
			
		}	
		double sum = 0;
		double mx = 0;
		for (int i=0; i<transProbs.length; i++)
			sum += transProbs[i];
		
		for (int i=0; i<transProbs.length; i++){
			transProbs[i] /= sum;
			mx = Math.max(mx, transProbs[i]);
		}
		
		for (int i=0; i<this.elements.size(); i++)
		{
			EyeTrackerItem item = this.elements.get(i);
			double g = (Double)gs.get(i).get(t+1);
			item.setGazeScore(g);
			if (g <= 0)
			{
				item.setProbability(0);
				item.setScore(0);
				continue;
			}
			if(viewer.isProbabilityDisabled())
			{
				item.setProbability(-1);
				item.setScore(g);
			}
			else
			{
				double p = transProbs[i] / mx;
				if (sum == 0) p = 1;
			
				
				double f = g*Util.getLevitatedScore(p, ProbabilityManager.LEVITATION_LOWER_BOUND);
				
				ps.get(i).add(new Double(p));
				fs.get(i).add(new Double(f));
				
				
				item.setProbability(p);
				item.setScore(f);
			}
		
		}

		
	}
	public double getTransitionProbability(int source, int destination)
	{
		EyeTrackerItem sourceElement = this.elements.get(source);
		EyeTrackerItem destinationElement = this.elements.get(destination);
		
		//Get state
		int state;
		int sourceType = sourceElement.getType();
		if(sourceType == EyeTrackerItem.TYPE_ACTOR)
		{
			state = StateAction.STATE_ACTOR;
		}
		else if(sourceType == EyeTrackerItem.TYPE_MOVIE)
		{ 
			state = StateAction.STATE_MOVIE;
		}
		else if(sourceType == EyeTrackerItem.TYPE_DIRECTOR)
		{ 
			state = StateAction.STATE_DIRECTOR;
		}
		else if(sourceType == EyeTrackerItem.TYPE_GENRE)
		{ 
			state = StateAction.STATE_GENRE;
		}
		else if(sourceType == EyeTrackerItem.TYPE_MOVIE_STAR_RATING)
		{ 
			state = StateAction.STATE_MOVIE_STAR_RATING;
		}
		else 
		{ 
			state = StateAction.STATE_INVALID;
		}
		
		//Get action
		int action;
		int destinationType = destinationElement.getType();
		if(destinationType == EyeTrackerItem.TYPE_ACTOR)
		{
			if(destinationElement.isConnected(sourceElement) && sourceElement.isHovered())
			{
				action = StateAction.ACTION_ACTOR_HOVERED;
			}
			else if(destinationElement.isConnected(sourceElement))
			{
				action = StateAction.ACTION_ACTOR_CONNECTED;
			}
			else if(destinationElement.isSameGroup(sourceElement))
			{
				action = StateAction.ACTION_ACTOR_SAME_GROUP;
			}
			else
			{
				action = StateAction.ACTION_ACTOR_DISCONNECTED;
			}
		}
		else if(destinationType == EyeTrackerItem.TYPE_MOVIE)
		{ 
			if(destinationElement.isConnected(sourceElement) && sourceElement.isHovered())
			{
				action = StateAction.ACTION_MOVIE_HOVERED;
			}
			else if(destinationElement.isConnected(sourceElement))
			{
				action = StateAction.ACTION_MOVIE_CONNECTED;
			}
			else if(destinationElement.isSameGroup(sourceElement))
			{
				action = StateAction.ACTION_MOVIE_SAME_GROUP;
			}
			else
			{
				action = StateAction.ACTION_MOVIE_DISCONNECTED;
			}
		}
		else if(destinationType == EyeTrackerItem.TYPE_DIRECTOR)
		{ 
			if(destinationElement.isConnected(sourceElement) && sourceElement.isHovered())
			{
				action = StateAction.ACTION_DIRECTOR_HOVERED;
			}
			else if(destinationElement.isConnected(sourceElement))
			{
				action = StateAction.ACTION_DIRECTOR_CONNECTED;
			}
			else if(destinationElement.isSameGroup(sourceElement))
			{
				action = StateAction.ACTION_DIRECTOR_SAME_GROUP;
			}
			else
			{
				action = StateAction.ACTION_DIRECTOR_DISCONNECTED;
			}
		}
		else if(destinationType == EyeTrackerItem.TYPE_GENRE)
		{ 
			if(destinationElement.isConnected(sourceElement) && sourceElement.isHovered())
			{
				action = StateAction.ACTION_GENRE_HOVERED;
			}
			else if(destinationElement.isConnected(sourceElement))
			{
				action = StateAction.ACTION_GENRE_CONNECTED;
			}
			else if(destinationElement.isSameGroup(sourceElement))
			{
				action = StateAction.ACTION_GENRE_SAME_GROUP;
			}
			else
			{
				action = StateAction.ACTION_GENRE_DISCONNECTED;
			}
		}
		else if(destinationType == EyeTrackerItem.TYPE_MOVIE_STAR_RATING)
		{ 
			if(destinationElement.isConnected(sourceElement))
			{
				action = StateAction.ACTION_STAR_RATING_CONNECTED;
			}
			else if(destinationElement.isSameGroup(sourceElement))
			{
				action = StateAction.ACTION_MOVIE_SAME_GROUP;
			}
			else
			{
				action = StateAction.ACTION_STAR_RATING_DISCONNECTED;
			}
		}
		else 
		{ 
			action = StateAction.ACTION_INVALID;
		}
		
		if(state != StateAction.STATE_INVALID && action != StateAction.ACTION_INVALID)
		{
			double probability =StateAction.PROBABILITY_TABLE[state][action]; 
			return probability;
		}
		else
		{
			return 0;
		}
		
		
	}
	private double avScore(int e) {
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
	
}
