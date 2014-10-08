package eyetrack;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

import eyetrack.probability.ProbabilityManager;
import eyetrack.probability.SortingItem;
import eyetrack.probability.StateAction;

public class EyeTrackerLabelDetector implements EyeTrackerDataReceiver{
	public static final int TIME_RESOLUTION=100;
	private EyeTrackServer g;
	public static final int EDGETHRESHOLD = 50;	
	public static final double SELECTION_THRESHOLD =0.009;
	
	private ArrayList<EyeTrackerItem> elements=null;
	private boolean blocked = false;
	private EyeTrackerViewer viewer = null;
	private ProbabilityManager probabilityManager;
	
	private ArrayList<Long> timeList = new ArrayList<Long>();
	private ArrayList<ArrayList<Double>> scoreHistory = new ArrayList<ArrayList<Double>>();
	
	public EyeTrackerLabelDetector(EyeTrackerViewer viewer)
	{
		this.viewer = viewer;
		this.probabilityManager = new ProbabilityManager();
		g = new EyeTrackServer(this);
	}
	public void registerElements(ArrayList<EyeTrackerItem> elements)
	{
		this.elements = elements;
		this.probabilityManager.setOriginalPreviousElements(null);
	}
	public ArrayList<EyeTrackerItem> getTopElements()
	{
		return this.probabilityManager.getOriginalPreviousElements();
	}
	@Override
	public void processGaze(Point gazePoint, double pupilDiameter) {
		// TODO Auto-generated method stub
		
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
		long time = System.currentTimeMillis();
		this.timeList.add(time);
		ArrayList<Double> scoreHistoryTime = new ArrayList<Double>();
		this.scoreHistory.add(scoreHistoryTime);
		
		double zoom =viewer.getZoom();
		
		for (int i=0; i<elements.size(); i++)
		{
			EyeTrackerItem element = this.elements.get(i);
			
			
			double gazeScore = element.getGazeScore(gazePoint, zoom);
			
			//original 
			ArrayList<StateAction> originalActions = element.getActions(probabilityManager.getPreviousStateActions(ProbabilityManager.SCORE_ORIGINAL));			
			double probability = probabilityManager.getProbability(originalActions);			
			//gazeScore
			ArrayList<StateAction> gazeActions = element.getActions(probabilityManager.getPreviousStateActions(ProbabilityManager.SCORE_GAZE));
			probability = Math.max(probability, probabilityManager.getProbability(gazeActions));
			//probabilityScore
			ArrayList<StateAction> probabilityActions = element.getActions(probabilityManager.getPreviousStateActions(ProbabilityManager.SCORE_PROBABILITY));
			probability = Math.max(probability, probabilityManager.getProbability(probabilityActions));
			
			double score = gazeScore* probability;
			element.setScore(score);
			element.setGazeScore(gazeScore);
			element.setProbability(probability);
			scoreHistoryTime.add(score);
		
		}
		
		computeStates();
	}
	
	private void computeStates()
	{
		long currentTime = System.currentTimeMillis();
		
		int toIndex = this.timeList.size()-1;
		int fromIndex = toIndex;
		for(int i=toIndex;i>0 ;i--)
		{
			if(currentTime - this.timeList.get(i) > TIME_RESOLUTION)
			{
				break;
			}
			fromIndex = i;
		}
		ArrayList<EyeTrackerItem> originalPreviousElements = this.getPreviousElementList(fromIndex, toIndex, ProbabilityManager.SCORE_ORIGINAL);
		this.probabilityManager.setOriginalPreviousElements(originalPreviousElements);
		
		ArrayList<EyeTrackerItem> gazePreviousElements = this.getPreviousElementList(fromIndex, toIndex, ProbabilityManager.SCORE_GAZE);
		this.probabilityManager.setGazePreviousElements(gazePreviousElements);
		
		ArrayList<EyeTrackerItem> probabilityPreviousElements = this.getPreviousElementList(fromIndex, toIndex, ProbabilityManager.SCORE_PROBABILITY);
		this.probabilityManager.setProbabilityPreviousElements(probabilityPreviousElements);
	}
	
	private ArrayList<EyeTrackerItem> getPreviousElementList(int fromIndex, int toIndex, int scoreType )
	{
		PriorityQueue<SortingItem> priorityQueue = new PriorityQueue<SortingItem>();
		//initial
		ArrayList<Double> scoreHistoryTime = this.scoreHistory.get(fromIndex);
		for(int j=0;j<scoreHistoryTime.size();j++)
		{
			EyeTrackerItem element = this.elements.get(j);
			double score = 0;
			if(scoreType== ProbabilityManager.SCORE_ORIGINAL)
			{
				score = element.getScore();
			}
			else if (scoreType == ProbabilityManager.SCORE_GAZE )
			{
				score = element.getStoredGazeScore();
			}
			else if (scoreType == ProbabilityManager.SCORE_PROBABILITY )
			{
				score = element.getProbabilityScore();
			}
			SortingItem sortingItem = new SortingItem(element, score);
			priorityQueue.add(sortingItem);
		}
		
		// Re adjust the values for other timeframes
		for(int i = fromIndex+1 ; i<=toIndex; i++)
		{
			scoreHistoryTime = this.scoreHistory.get(i);
			LinkedList<SortingItem> temporaryList = new LinkedList<SortingItem>();
			// remove all items and put it to a temp list
			while(!priorityQueue.isEmpty())
			{
				SortingItem item = priorityQueue.poll();
				int index = this.elements.indexOf(item.getItem());
				double val = scoreHistoryTime.get(index);
				double newVal = (item.getValue()* (i-fromIndex)+val)/(i-fromIndex+1);
				item.setValue(newVal);
				temporaryList.add(item);
			}
			// re insert in the priority queue
			while(!temporaryList.isEmpty())
			{
				SortingItem item = temporaryList.poll();
				priorityQueue.add(item);
			}
		}
		// take top 10% items as previous item
		ArrayList<EyeTrackerItem> previousElements = new ArrayList<EyeTrackerItem>();
		int maxCount = (int) Math.ceil(ProbabilityManager.TOP_PERCENTILE*priorityQueue.size()/100.0);
		int count =0;
		while(!priorityQueue.isEmpty())
		{
			SortingItem item = priorityQueue.poll();
			item.setValue(item.getValue());
			previousElements.add(item.getItem());
			count++;
			if(count >= maxCount)
			{
				break;
			}
		}
		return previousElements;
	}
}
