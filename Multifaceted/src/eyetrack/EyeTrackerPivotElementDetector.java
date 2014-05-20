package eyetrack;

import java.awt.Point;
import java.util.ArrayList;

import multifaceted.layout.PivotElement;

public class EyeTrackerPivotElementDetector implements EyeTrackerDataReceiver{

	private EyeTrackServer g;
	private Point offset;
	double[] nodeScores;
	double[] nodeScores2;
	int EDGETHRESHOLD = 50;	
	
	private ArrayList<PivotElement> elements=null;
	private boolean blocked = false;
	
	public EyeTrackerPivotElementDetector()
	{
		offset = new Point(0,0);
		
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
		if(this.elements != null && !this.elements.isEmpty())
		{
			this.processElementGaze(gazePoint);
		}
		
	}
	public void block(boolean block)
	{
		synchronized(this)
		{
			this.blocked = block;
		}		
	}
	public void setOffset(Point p)
	{
		offset = p;
	}
	
	private void processElementGaze(Point gazePoint)
	{
		if (nodeScores == null)
			return;
		//int zoom = this.getZoom();
		int zoom =1;
		int et = (int)(EDGETHRESHOLD / 1);
		
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
