package eyerecommend;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.concurrent.PriorityBlockingQueue;

import eyerecommend.item.RecommendItem;

public class RecommendationEngine implements EyeTrackerDataReceiver{
	protected EyeTrackerViewer viewer;
	protected EyeTrackServer server;
	protected boolean blocked = false;
	protected PriorityBlockingQueue<RecommendItem> elements;
	
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
	
	public void registerItems(ArrayList<RecommendItem> elements)
	{
		synchronized(this)
		{
			if (blocked) return;
		}
		this.elements = new PriorityBlockingQueue<RecommendItem>(elements);
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
	}

	@Override
	public void processGaze(Point gazePoint, double pupilDiameter) {
		// TODO Auto-generated method stub
		processGaze(gazePoint);
	}
	

}
