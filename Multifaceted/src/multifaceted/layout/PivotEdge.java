package multifaceted.layout;

import java.awt.Color;
import java.awt.Graphics2D;

public class PivotEdge {
	private PivotElement source;

	private PivotElement destination;
	private boolean isDirected;
	
	private int sourceIndex;
	private int destinationIndex;
	
	public PivotEdge(PivotElement source, int sourceIndex, PivotElement destination, int destinationIndex, boolean isDirected)
	{
		this.source = source;
		this.sourceIndex = sourceIndex;
		this.destination = destination;
		this.destinationIndex = destinationIndex;
		this.isDirected = isDirected;
		
		this.source.getLabel().increaseEdgeCount();
		this.destination.getLabel().increaseEdgeCount();
				
	}
	public PivotElement getSource() {
		return source;
	}
	public PivotElement getDestination() {
		return destination;
	}
	public boolean isDirected() {
		return isDirected;
	}
	
	public int getSourceIndex() {
		return sourceIndex;
	}
	public int getDestinationIndex() {
		return destinationIndex;
	}
	public int getSpringLength()
	{
		return Math.min(this.source.getLabel().getEdgeCount()+1, this.destination.getLabel().getEdgeCount()+1);
	}
	
	public void render(Graphics2D g)
	{
		int x1 = (int)this.getSource().getPosition().getX();
		int y1 = (int)this.getSource().getPosition().getY();
		int x2 = (int)this.getDestination().getPosition().getX();
		int y2 = (int)this.getDestination().getPosition().getY();
		
		g.drawLine(x1, y1, x2, y2);
		
//		renderDebug(g);
		
	}
	private void renderDebug(Graphics2D g)
	{
		int x1 = (int)this.getSource().getPosition().getX();
		int y1 = (int)this.getSource().getPosition().getY();
		int x2 = (int)this.getDestination().getPosition().getX();
		int y2 = (int)this.getDestination().getPosition().getY();
		double d = this.getSource().getPosition().distance(this.getDestination().getPosition());
		g.setColor(Color.black);
		g.drawString(getSpringLength()+", "+String.format("%.2f", d), (x1+x2)/2, (y1+y2)/2);
		
	}
}
