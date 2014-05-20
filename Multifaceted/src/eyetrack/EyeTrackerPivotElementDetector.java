package eyetrack;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;

import multifaceted.layout.PivotElement;

public class EyeTrackerPivotElementDetector implements EyeTrackerDataReceiver{

	private EyeTrackServer g;
	double[] nodeScores;
	double[] nodeScores2;
	public static final int EDGETHRESHOLD = 50;	
	
	private ArrayList<PivotElement> elements=null;
	private boolean blocked = false;
	private EyeTrackerViewer viewer = null;
	public EyeTrackerPivotElementDetector(EyeTrackerViewer viewer)
	{
		this.viewer = viewer;
		g = new EyeTrackServer(this);
	}
	public void registerElements(ArrayList<PivotElement> elements)
	{
		this.elements = elements;
		nodeScores = new double[this.elements.size()];
		nodeScores2 = new double[this.elements.size()];
	}
	@Override
	public void processGaze(Point gazePoint, double pupilDiameter) {
		// TODO Auto-generated method stub
		
		processGaze(gazePoint);
	}
	public double[] getNodeScore()
	{
		return nodeScores;
	}
	public double[] getNodeScore2()
	{
		return nodeScores2;
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
			if(this.elements != null && !this.elements.isEmpty())
			{
				this.processElementGaze(screenPoint);
			}
			viewer.gazeDetected(screenPoint.x, screenPoint.y);
		} catch (NoninvertibleTransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		if (nodeScores == null)
			return;
		double zoom =viewer.getZoom();
		int et = (int)(EDGETHRESHOLD / zoom);
		
		for (int i=0; i<elements.size(); i++)	//for all nodes
		{
			PivotElement element = this.elements.get(i);
			int nx = (int)element.getPosition().getX();
			int ny = (int)element.getPosition().getY();
			
			nodeScores[i] = Math.max(0, nodeScores[i]-0.02);	//node scores decay over time
			if (nodeScores[i] < 0) nodeScores[i] = 0;
				
			double d = Math.sqrt((gazePoint.x-nx) * (gazePoint.x-nx) + (gazePoint.y-ny)*(gazePoint.y-ny));
												
			if (d <= et)
			{				
				//the score is inversely proportional to the distance (small distance -> high score)
				double dbl = (et-d)/et;
				
				nodeScores[i] += dbl/10;
			}	
			if (nodeScores[i] > nodeScores2[i]) nodeScores2[i] += 0.0005;
			else nodeScores2[i] -= 0.0001;
			if (nodeScores2[i] > 1) nodeScores2[i] = 1;
			else if (nodeScores2[i] <0) nodeScores2[i] = 0;
		}
	
	}
	
}
