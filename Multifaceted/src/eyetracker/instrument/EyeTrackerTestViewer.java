package eyetracker.instrument;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;

import multifaceted.Util;

import perspectives.base.Viewer;
import perspectives.two_d.JavaAwtRenderer;
import perspectives.two_d.ViewerContainer2D;

public class EyeTrackerTestViewer extends Viewer implements JavaAwtRenderer, EyeFixationDataReceiver{

	private ArrayList<Point> gazes = new ArrayList<Point>();
	private ArrayList<Point> fixations = new ArrayList<Point>();
	
	public EyeTrackerTestViewer(String name) {
		super(name);
		EyeTrackerFixationServer server = new EyeTrackerFixationServer(this);
		
	}

	@Override
	public Color getBackgroundColor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void keyPressed(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean mousedragged(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mousemoved(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mousepressed(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mousereleased(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		g.setColor(Color.red);
		g.drawLine(0, 0, 1000, 0);
		g.drawLine(0, 0,0, 1000);
		
		g.setColor(Util.getAlphaColor(Color.green, 125));		
		g.fillRect(100, 100, 200, 200);
		g.setColor(Color.black);
		g.drawString("Green Rectange", 200, 200);
		
		g.setColor(Util.getAlphaColor(Color.gray, 125));
		g.fillRect(500, 100, 200, 200);
		g.setColor(Color.black);
		g.drawString("Gray Rectange", 600, 200);
		
		g.setColor(Util.getAlphaColor(Color.magenta, 125));
		g.fillRect(100, 500, 200, 200);
		g.setColor(Color.black);
		g.drawString("Magenta Rectange", 200, 600);
		
		g.setColor(Util.getAlphaColor(Color.orange, 125));
		g.fillRect(500, 500, 200, 200);
		g.setColor(Color.black);
		g.drawString("Orange Rectange", 600, 600);
		
		for(int i=0;i<gazes.size();i++)
		{
			Point p = gazes.get(i);
			g.setColor(new Color(0,0,255,120));
			g.fillOval(p.x, p.y, 10,10);
		}
		
		for(int i=0;i<fixations.size();i++)
		{
			Point p = fixations.get(i);
			g.setColor(new Color(255,0,0,120));
			g.fillOval(p.x, p.y, 10,10);
		}
	}

	

	
	public void gazeDetected(int x, int y) {
		// TODO Auto-generated method stub
		gazes.add(new Point(x,y));
		requestRender();
	}

	
	public Point getEyeTrackOffset()
	{
		Point p = new Point(0,0);
		if (this.getContainer() != null && this.getContainer().getViewerWindow() != null)
			p = this.getContainer().getViewerWindow().getDrawArea().getLocationOnScreen();
		
		
		return p;
	}

	public AffineTransform getTransform() {
		// TODO Auto-generated method stub
		return ((ViewerContainer2D)this.getContainer()).transform;
	}

	
	public double getZoom() {
		// TODO Auto-generated method stub
		return ((ViewerContainer2D)this.getContainer()).getZoom();
	}

	@Override
	public void processGaze(Point gazePoint, double pupilDiameter) {
		// TODO Auto-generated method stub
		
		
		try {
			
			Point screenPoint = getScreenPosition(gazePoint);
			
			gazes.add(screenPoint);
			
			requestRender();
		} catch (NoninvertibleTransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public void processFixation(Point gazePoint, double pupilDiameter, String type) {
		// TODO Auto-generated method stub
		try {
			
			Point screenPoint = getScreenPosition(gazePoint);
			
			fixations.add(screenPoint);
			
			requestRender();
		} catch (NoninvertibleTransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Point getScreenPosition(Point gazePoint) throws NoninvertibleTransformException
	{
	
		Point offset = getEyeTrackOffset();
		Point processingPoint = new Point(gazePoint.x - offset.x, gazePoint.y - offset.y);
		
		Point screenPoint = new Point();
		
		AffineTransform transform;
	
		transform = getTransform().createInverse();

		transform.transform(processingPoint, screenPoint);
		
		return screenPoint;
		
	}

}
