package imdb.analysis;


import java.awt.Color;
import java.awt.Graphics2D;

import perspectives.two_d.JavaAwtRenderer;

public class HeatMapAnalysisViewer extends AnalysisViewer implements JavaAwtRenderer{
	public static final int TOTAL_COLORS =10;
	public HeatMapAnalysisViewer(String name) {
		super(name);
		// TODO Auto-generated constructor stub
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
	public void render(Graphics2D arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void processFileLine(String line) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void printInfo() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createVisualItems() {
		// TODO Auto-generated method stub
		
	}

	public static Color[] getHeatMapColors()
	{
		int alpha = 200;
		Color[] color = new Color[TOTAL_COLORS];
		
		color[0] = new Color(0,252,67,alpha);
		
		color[1] = new Color(0,252,0,alpha);
		
		color[2] = new Color(93,252,0,alpha);
		
		color[3] = new Color(151,252,0,alpha);
		
		color[4] = new Color(240,252,0,alpha);
		
		color[5] = new Color(252,194,0,alpha);
		
		color[6] = new Color(252,143,0,alpha);
		
		color[7] = new Color(252,97,0,alpha);
		
		color[8] = new Color(252,46,0,alpha);
		
		color[9] = new Color(252,0,0,alpha);
		
		return color;
	}
	

}
