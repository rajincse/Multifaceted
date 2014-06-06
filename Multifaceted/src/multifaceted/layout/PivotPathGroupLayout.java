package multifaceted.layout;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class PivotPathGroupLayout extends PivotPathLayout{
	public static final int COEFF_COMPULSIVE_FORCE_WEAK=100;
	public static final int COEFF_COMPULSIVE_FORCE_STRONG=300;
	public static final int COEFF_SPRING_LENGTH=200;
	public static final int COEFF_BOUNDARY_FORCE=100;

	HashMap<String, GroupedPivotElement> topGroupedElements = new HashMap<String, GroupedPivotElement>();
	HashMap<String, GroupedPivotElement> bottomGroupedElements = new HashMap<String, GroupedPivotElement>();
	HashMap<String, GroupedPivotElement> rightGroupedElements = new HashMap<String, GroupedPivotElement>();
	
	HashMap<String, ArrayList<String>> edgeSourceIds = new HashMap<String, ArrayList<String>>();
	
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
		rightGroupedElements.clear();
		edgeSourceIds.clear();
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
	public int addRightElement(String id, String displayName, int sourceIndex) {
		double x= BOUNDARY_RIGHT;

		Point2D.Double position = new Point2D.Double(x,MIDDLE_Y); 
		PivotElement element = new PivotElement(id, displayName, position, LAYER_RIGHT);
		this.elem.add(element);
		this.elementId.add(id);
		element.getLabel().setFont(new Font("Sans-Serif",Font.PLAIN,20));
		this.addLabel(element.getLabel(), false, false);
		String sourceId = this.elem.get(sourceIndex).getId();
		if(!this.rightGroupedElements.containsKey(sourceId))
		{
			GroupedPivotElement groupedPivotElement = new GroupedPivotElement(element);
			
			this.rightGroupedElements.put(sourceId, groupedPivotElement);
		}
		else
		{
			GroupedPivotElement groupedPivotElement = this.rightGroupedElements.get(sourceId);
			groupedPivotElement.addElement(element);
		}
		return cnt++;
	}
	
	@Override
	public void addEdge(int e1, int e2) {
		// TODO Auto-generated method stub
		super.addEdge(e1, e2);
		
		PivotElement source = this.elem.get(e1);		
		PivotElement destination = this.elem.get(e2);
		if(this.edgeSourceIds.containsKey(destination.getId()))
		{
			ArrayList<String> sourceIdList = this.edgeSourceIds.get(destination.getId());
			if(sourceIdList != null && !sourceIdList.contains(source.getId()))
			{
				sourceIdList.add(source.getId());
			}
			else
			{
				sourceIdList = new ArrayList<String>();
				sourceIdList.add(source.getId());
			}
		}
		else
		{
			ArrayList<String> sourceIdList = new ArrayList<String>();
			sourceIdList.add(source.getId());
			this.edgeSourceIds.put(destination.getId(), sourceIdList);
		}
		
		if(destination.getLabel().getEdgeCount() > 1)
		{
			ArrayList<String> sourceIdList = this.edgeSourceIds.get(destination.getId());
			String sourceId ="";
			if(!sourceIdList.isEmpty())
			{
				sourceId = sourceIdList.get(0);
			}
			

			if(!sourceId.isEmpty())
			{
				if(this.topGroupedElements.containsKey(sourceId))
				{
					GroupedPivotElement group = this.topGroupedElements.get(sourceId);
					if(group.getElements().contains(destination))
					{
						group.removeElement(destination);
						
						GroupedPivotElement newGroup = new GroupedPivotElement(destination);
						this.topGroupedElements.put(destination.getId(), newGroup);		
					}
				}
				else if(this.bottomGroupedElements.containsKey(sourceId))
				{
					GroupedPivotElement group = this.bottomGroupedElements.get(sourceId);
					if(group.getElements().contains(destination))
					{
						group.removeElement(destination);
						
						GroupedPivotElement newGroup = new GroupedPivotElement(destination);
						this.bottomGroupedElements.put(destination.getId(), newGroup);		
					}
				}
				else if(this.rightGroupedElements.containsKey(sourceId))
				{
					GroupedPivotElement group = this.rightGroupedElements.get(sourceId);
					if(group.getElements().contains(destination))
					{
						group.removeElement(destination);
						
						GroupedPivotElement newGroup = new GroupedPivotElement(destination);
						this.rightGroupedElements.put(destination.getId(), newGroup);		
					}
				}
			}
		}
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
		
		for(Entry<String, GroupedPivotElement> group1Entry: this.bottomGroupedElements.entrySet())
		{
			GroupedPivotElement group1 = group1Entry.getValue();
			for(Entry<String, GroupedPivotElement> group2Entry: this.bottomGroupedElements.entrySet())
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

		for(Entry<String, GroupedPivotElement> group1Entry: this.rightGroupedElements.entrySet())
		{
			GroupedPivotElement group1 = group1Entry.getValue();
			for(Entry<String, GroupedPivotElement> group2Entry: this.rightGroupedElements.entrySet())
			{
				if(group1Entry.getKey() != group2Entry.getKey())
				{
					
					GroupedPivotElement group2 = group2Entry.getValue();
					double[] f = this.computeGroupReplusion(group1, group2);
					double factor =1;
					group1.fx+= f[0]*factor;
					group1.fy+= f[1]*factor;
					
					group2.fx-= f[0]*factor;
					group2.fy-= f[1]*factor;
				}
			}
			for(Entry<String, GroupedPivotElement> group2Entry: this.bottomGroupedElements.entrySet())
			{
				if(group1Entry.getKey() != group2Entry.getKey())
				{
					
					GroupedPivotElement group2 = group2Entry.getValue();
					double[] f = this.computeGroupReplusion(group1, group2);
					double factor =2;
					group1.fx+= f[0]*factor;
					group1.fy+= f[1]*factor;
					
					group2.fx-= f[0]*factor;
					group2.fy-= f[1]*factor;
				}
			}
			for(Entry<String, GroupedPivotElement> group2Entry: this.topGroupedElements.entrySet())
			{
				if(group1Entry.getKey() != group2Entry.getKey())
				{
					
					GroupedPivotElement group2 = group2Entry.getValue();
					double[] f = this.computeGroupReplusion(group1, group2);
					double factor =2;
					group1.fx+= f[0]*factor;
					group1.fy+= f[1]*factor;
					
					group2.fx-= f[0]*factor;
					group2.fy-= f[1]*factor;
				}
			}
			for(PivotElement element : this.elem)
			{
				if(element.getLayer()== LAYER_MIDDLE )
				{
					double factor =10;
					double[] f= this.computeGroupToElementRepulsion(group1, element);
					group1.fx+= f[0]*factor;
					group1.fy+= f[1]*factor;
				}
			}
		}
		for (int i=0; i<edges.size(); i++)
		{
			PivotEdge edge = this.edges.get(i);
			String sourceId = edge.getSource().getId();
			String destinationId = edge.getDestination().getId();
			int factor =1;
			GroupedPivotElement group = null;
			if(this.topGroupedElements.containsKey(sourceId))
			{
				group = this.topGroupedElements.get(sourceId);
			}
			else if(this.topGroupedElements.containsKey(destinationId))
			{
				group = this.topGroupedElements.get(destinationId);
			}
			else if(this.bottomGroupedElements.containsKey(sourceId))
			{
				group = this.bottomGroupedElements.get(sourceId);
			}
			else if(this.bottomGroupedElements.containsKey(destinationId))
			{
				group = this.bottomGroupedElements.get(destinationId);
			}
			else if(this.rightGroupedElements.containsKey(sourceId))
			{
				group = this.rightGroupedElements.get(sourceId);
				factor = 2;
			}
			else if(this.rightGroupedElements.containsKey(destinationId))
			{
				group = this.rightGroupedElements.get(destinationId);
				factor = 2;
			}
			else
			{
				continue;
			}
					
			int springLength =edge.getSpringLength()* COEFF_SPRING_LENGTH * factor;
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
		for(GroupedPivotElement group: this.bottomGroupedElements.values())
		{
			double y = 600;
			double d = group.getCenterPosition().getY() - y;
			
			double mag = COEFF_BOUNDARY_FORCE * 1/(d*d);
			group.fy+= mag;
		}
		for(GroupedPivotElement group: this.rightGroupedElements.values())
		{
			double x = BOUNDARY_RIGHT-STEP_MIDDLE_ITEM;
			double factor =10;
			double centerX = group.getCenterPosition().getX();
			double d = centerX - x;
			double mag = COEFF_BOUNDARY_FORCE * factor/(d*d);
			group.fx+= mag;
			
		}
		
		for(GroupedPivotElement group: this.topGroupedElements.values())
		{
			group.applyForces();
		}
		
		for(GroupedPivotElement group: this.bottomGroupedElements.values())
		{
			group.applyForces();
		}
		for(GroupedPivotElement group: this.rightGroupedElements.values())
		{
			group.applyForces();
		}
	}
	
	protected double[] computeGroupToElementRepulsion(GroupedPivotElement group, PivotElement pivotElement)
	{
		double fx=0;
		double fy=0;
		Point2D[] gravityPoints1 = group.getGravityPoints();
		
		Point2D[] gravityPoints2 = pivotElement.getGravityPoints();
		
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
		
		renderMainItemEdge(g);
		
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
		
		
	}

}
