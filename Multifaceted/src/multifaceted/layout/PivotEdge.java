package multifaceted.layout;

public class PivotEdge {
	private PivotElement source;

	private PivotElement destination;
	private boolean isDirected;
	
	public PivotEdge(PivotElement source, PivotElement destination, boolean isDirected)
	{
		this.source = source;
		this.destination = destination;
		this.isDirected = isDirected;
				
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
	
	public int getSpringLength()
	{
		return Math.min(this.source.getLabel().getEdgeCount()+1, this.destination.getLabel().getEdgeCount()+1);
	}
}
