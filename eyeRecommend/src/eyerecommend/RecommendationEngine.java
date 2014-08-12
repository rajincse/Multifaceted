package eyerecommend;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import eyerecommend.item.RecommendItem;

public class RecommendationEngine implements EyeTrackerDataReceiver{
	protected EyeTrackerViewer viewer;
	protected EyeTrackServer server;
	protected boolean blocked = false;
	protected RecommendItem[] elements;
	
	public RecommendationEngine(EyeTrackerViewer viewer)
	{
		this.viewer = viewer;
		this.server = new EyeTrackServer(this);
	}
	
	public RecommendationEngine(EyeTrackerViewer viewer, int port)
	{
		this.viewer = viewer;
		this.server = new EyeTrackServer(this, port);
	}
	
	public void registerItems(RecommendItem[] elements)
	{
		synchronized(this)
		{
			if (blocked) return;
		}
		this.elements = elements;
	}
	public void block(boolean block)
	{
		synchronized(this)
		{
			this.blocked = block;
		}		
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
			e.printStackTrace();
		}
	}
	public void processScreenPoint(Point screenPoint)
	{		
		viewer.gazeDetected(screenPoint.x, screenPoint.y);
		int maxIndex =-1;
		double maxValue = Double.MIN_VALUE;
		for(RecommendItem item: elements)
		{
			double posterior = item.getPosteriorProbability(screenPoint);
			double prior = item.getPriorProbability();
			double score = posterior * prior;
			if(score > maxValue)
			{
				maxValue = score;
				maxIndex = item.getIndex();
			}
		}
		this.viewer.recommendationFound(maxIndex);
	}

	@Override
	public void processGaze(Point gazePoint, double pupilDiameter) {
		// TODO Auto-generated method stub
		processGaze(gazePoint);
	}
	

}
