package multifaceted.layout;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map.Entry;

import perspectives.util.Label;

public class PivotPathGroupLayout extends PivotPathLayout{
	public static final int COEFF_COMPULSIVE_FORCE_WEAK=50;
	public static final int COEFF_COMPULSIVE_FORCE_STRONG=150;
	public static final int COEFF_SPRING_LENGTH=100;
	public static final int COEFF_BOUNDARY_FORCE=10000;

	HashMap<String, GroupedPivotElement> topGroupedElements = new HashMap<String, GroupedPivotElement>();
	HashMap<String, GroupedPivotElement> bottomGroupedElements = new HashMap<String, GroupedPivotElement>();
	public PivotPathGroupLayout(LayoutViewerInterface viewer) {
		super(viewer);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		super.init();
		
		topGroupedElements.clear();
		bottomGroupedElements.clear();
	}
	
	@Override
	public int addTopElement(String id, String displayName, int sourceIndex) {
		double minX = this.elem.get(sourceIndex).getPosition().getX()- STEP_MIDDLE_ITEM;
		double maxX = this.elem.get(sourceIndex).getPosition().getX()+ STEP_MIDDLE_ITEM;
		Point2D.Double position = new Point2D.Double(getRandomPositionX(maxX, minX), TOP_Y);
		PivotElement element = new PivotElement(id, displayName, position, LAYER_TOP);
		this.elem.add(element);
		this.elementId.add(id);
		this.addLabel(element.getLabel(), false, true);		
		
		// Add to Grouped Element
		String sourceId = this.elem.get(sourceIndex).getId();
		if(!this.topGroupedElements.containsKey(sourceId))
		{
			GroupedPivotElement groupedPivotElement = new GroupedPivotElement(element);
			
			this.topGroupedElements.put(sourceId, groupedPivotElement);
		}
		else
		{
			GroupedPivotElement groupedPivotElement = this.topGroupedElements.get(sourceId);
			groupedPivotElement.addElement(element);
		}
		
		return cnt++;
	}
	
	@Override
	public int addBottomElement(String id, String displayName, int sourceIndex) {
		double minX = this.elem.get(sourceIndex).getPosition().getX()- STEP_MIDDLE_ITEM;
		double maxX = this.elem.get(sourceIndex).getPosition().getX()+ STEP_MIDDLE_ITEM;
		Point2D.Double position = new Point2D.Double(getRandomPositionX(maxX, minX), BOTTOM_Y);
		PivotElement element = new PivotElement(id, displayName, position, LAYER_BOTTOM);
		this.elem.add(element);
		this.elementId.add(id);
		this.addLabel(element.getLabel(), false, true);	
		// Add to Grouped Element
		String sourceId = this.elem.get(sourceIndex).getId();
		if(!this.bottomGroupedElements.containsKey(sourceId))
		{
			GroupedPivotElement groupedPivotElement = new GroupedPivotElement(element);
			
			this.bottomGroupedElements.put(sourceId, groupedPivotElement);
		}
		else
		{
			GroupedPivotElement groupedPivotElement = this.bottomGroupedElements.get(sourceId);
			groupedPivotElement.addElement(element);
		}
		return cnt++;
	}
	
	@Override
	public void iteration() {
		for(Entry<String, GroupedPivotElement> group1Entry: this.topGroupedElements.entrySet())
		{
			GroupedPivotElement group1 = group1Entry.getValue();
			for(Entry<String, GroupedPivotElement> group2Entry: this.topGroupedElements.entrySet())
			{
				if(group1Entry.getKey() != group2Entry.getKey())
				{
					
					GroupedPivotElement group2 = group2Entry.getValue();
					double[] f = this.computeGroupReplusion(group1, group2);
					
					group1.fx+= f[0];
					group1.fy+= f[1];
					
					group2.fx-= f[0];
					group2.fy-= f[1];
				}
			}
			
			for(PivotElement element : this.elem)
			{
				if(element.getLayer()== LAYER_MIDDLE)
				{
					double[] f= this.computeGroupToElementRepulsion(group1, element);
					group1.fx+= f[0];
					group1.fy+= f[1];
				}
			}
		}

		for (int i=0; i<edges.size(); i++)
		{
			PivotEdge edge = this.edges.get(i);
			String sourceId = edge.getSource().getId();
			String destinationId = edge.getDestination().getId();
			
			GroupedPivotElement group = null;
			if(this.topGroupedElements.containsKey(sourceId))
			{
				group = this.topGroupedElements.get(sourceId);
			}
			else if(this.topGroupedElements.containsKey(destinationId))
			{
				group = this.topGroupedElements.get(destinationId);
			}
//			else if(this.bottomGroupedElements.containsKey(sourceId))
//			{
//				group = this.bottomGroupedElements.get(sourceId);
//			}
//			else if(this.bottomGroupedElements.containsKey(destinationId))
//			{
//				group = this.bottomGroupedElements.get(destinationId);
//			}
			else
			{
				continue;
			}
					
			int springLength = edge.getSpringLength()* COEFF_SPRING_LENGTH;
			double[] f = compAttraction(edge.getSource().getPosition(), group.getCenterPosition(),springLength);

			group.fx-= f[0];
			group.fy-= f[1];
		}
		for(GroupedPivotElement group: this.topGroupedElements.values())
		{
			double y = 300;
			double d = group.getCenterPosition().getY() - y;
			
			double mag = COEFF_BOUNDARY_FORCE * 1/(d*d);
			group.fy-= mag;
		}
		
		for(GroupedPivotElement group: this.topGroupedElements.values())
		{
			group.applyForces();
		}
		
	}
	
	protected double[] computeGroupToElementRepulsion(GroupedPivotElement group, PivotElement pivotElement)
	{
		double fx=0;
		double fy=0;
		Point2D[] gravityPoints1 = group.getGravityPoints();
		
		Label label = pivotElement.getLabel();
		int totalPoints = 5;
		Point2D[] gravityPoints2 = new Point2D[totalPoints];
		
		gravityPoints2[0] = new Point((int)(label.x),(int)(label.y)) ;
		

		gravityPoints2[1] = new Point((int)(label.x-label.w/2),(int)(label.y - label.h/2)) ;
		
		gravityPoints2[2] = new Point((int)(label.x-label.w/2),(int)(label.y + label.h/2)) ;
		
		gravityPoints2[3] = new Point((int)(label.x+label.w/2),(int)(label.y - label.h/2)) ;
		
		gravityPoints2[4] = new Point((int)(label.x+label.w/2),(int)(label.y + label.h/2)) ;
		
		for(int i=0;i<gravityPoints1.length;i++)
		{
			Point2D p1 = gravityPoints1[i];
			for(int j=0;j<gravityPoints2.length;j++)
			{
				
				Point2D p2 = gravityPoints2[j];
				if(i==0 || j==0)
				{
					double[] f = this.compRepulsion(p1, p2, COEFF_COMPULSIVE_FORCE_STRONG);
					fx+=f[0];
					fy+=f[1];
				}
				else
				{
					double[] f = this.compRepulsion(p1, p2, COEFF_COMPULSIVE_FORCE_WEAK);
					fx+=f[0];
					fy+=f[1];
				}
				
			}
			
		}
		
		return new double[]{fx,fy}; 
	}
	protected double[] computeGroupReplusion(GroupedPivotElement group1, GroupedPivotElement group2)
	{
		double fx =0;
		double fy =0;
		Point2D[] gravityPoints1 = group1.getGravityPoints();
		Point2D[] gravityPoints2 = group2.getGravityPoints();

		for(int i=0;i<gravityPoints1.length;i++)
		{
			Point2D p1 = gravityPoints1[i];
			for(int j=0;j<gravityPoints2.length;j++)
			{
				
				Point2D p2 = gravityPoints2[j];
				if(i==0 || j==0)
				{
					double[] f = this.compRepulsion(p1, p2, COEFF_COMPULSIVE_FORCE_STRONG);
					fx+=f[0];
					fy+=f[1];
				}
				else
				{
					double[] f = this.compRepulsion(p1, p2, COEFF_COMPULSIVE_FORCE_WEAK);
					fx+=f[0];
					fy+=f[1];
				}
				
			}
			
		}
		
		return new double[]{fx,fy};
	}
	
	@Override
	public void render(Graphics2D g) {
		g.setColor(Color.black);

		for (PivotElement element: this.elem)
		{
			if(element.getLayer() == LAYER_TOP || element.getLayer() == LAYER_BOTTOM)
			{ 
				//Skipping group element
				continue;
			}
			element.render(g);
		}
		//Drawing Group Element
		for(GroupedPivotElement group:this.topGroupedElements.values())
		{	
			group.render(g);
		}
		
		for(GroupedPivotElement group:this.bottomGroupedElements.values())
		{
			group.render(g);
		}
		
		for (int i=0; i<edges.size(); i++)
		{
			PivotEdge edge = this.edges.get(i);
			
			int e1 = edge.getSourceIndex();
			int e2 = edge.getDestinationIndex();
			g.setColor(Color.lightGray);
			if (objectInteraction.getItem(e1).hovered || (objectInteraction.getItem(e2).hovered))
					g.setColor(Color.black);
			if (objectInteraction.getItem(e1).selected || (objectInteraction.getItem(e2).selected))
					g.setColor(Color.red);
			edge.render(g);
		}
	}

}
