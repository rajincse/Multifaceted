package multifaceted.layout;


import java.awt.Font;

import perspectives.util.Label;

public class PivotLabel extends Label{
	private static final double INCREASING_FACTOR_FONT=1.7; 
	
	private int edgeCount;
	private boolean isChangeable;
	

	public PivotLabel( String label, boolean isChangeable) {
		super(0, 0, label);
		this.edgeCount =0;
		this.isChangeable= isChangeable;
	}
	
	public void setEdgeCount(int edgeCount)
	{
		
		this.edgeCount = edgeCount;
		changeView();
		
	}
	private void changeView()
	{
		if(isChangeable)
		{
			int fontSize = this.getFont().getSize();
			fontSize = (int) (fontSize* INCREASING_FACTOR_FONT);
			Font font = this.getFont();
			Font newFont = new Font(font.getFontName(), font.getStyle(), fontSize);
			this.setFont(newFont);
		}
		
	}
	public void increaseEdgeCount()
	{
		this.edgeCount++;
		changeView();
	}

	public int getEdgeCount() {
		return edgeCount;
	}
	public void setChangeable(boolean isChangeable) {
		this.isChangeable = isChangeable;
	}
	
}
