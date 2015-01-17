package eyeinterestanalyzer.clustering;

import java.util.ArrayList;

public class Cluster {
	protected ArrayList<Cluster> children;
	protected ArrayList<ClusteringItem> items;
	protected ArrayList<Integer> itemIndices;
	
	protected int height;
	protected int depth;
	
	protected Cluster parent;
	
	public Cluster(ClusteringItem item, int index)
	{
		this.children = null;
		this.items = new ArrayList<ClusteringItem>();
		items.add(item);
		
		this.itemIndices = new ArrayList<Integer>();
		this.itemIndices.add(index);
		this.height =0;
		this.parent = null;
		this.calculateDepth();
	}
	public Cluster(ArrayList<Cluster> children)
	{
		this.children = children;
		this.items = new ArrayList<ClusteringItem>();
		this.itemIndices = new ArrayList<Integer>();
		if(this.children != null)
		{
			int maxHeight =0;
			for(Cluster cluster: children)
			{
				if(cluster.getHeight() > maxHeight)
				{
					maxHeight = cluster.getHeight();
				}
				items.addAll(cluster.getItems());
				this.itemIndices.addAll(cluster.getIndices());
				cluster.setParent(this);
				cluster.calculateDepth();
			}
			
			this.height = maxHeight+1;
		}

	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public void calculateDepth() {
		int depth =0;
		Cluster parentCluster = this.getParent();
		while(parentCluster!= null)
		{
			depth++;
			parentCluster = parentCluster.getParent();
		}
		this.depth = depth;
		if(this.children != null)
		{	
			for(Cluster cluster: children)
			{	
				cluster.calculateDepth();
			}
		}
	}
	public int getDepth() {
		return depth;
	}
	public Cluster getParent() {
		return parent;
	}
	public void setParent(Cluster parent) {
		this.parent = parent;
	}
	public ArrayList<Cluster> getChildren() {
		return children;
	}
	public ArrayList<ClusteringItem> getItems()
	{
		return this.items;
	}
	
	public ArrayList<Integer> getIndices()
	{
		return this.itemIndices;
	}
	
	public double getMinDistance(Cluster otherCluster)
	{
		ArrayList<ClusteringItem> otherItems = otherCluster.getItems();
		double minDistance = Integer.MAX_VALUE;
		for(ClusteringItem item1: items)
		{
			for(ClusteringItem item2: otherItems)
			{
				if(item1.getId().equals(item2.getId()))
				{
					continue;
				}
				double distance = item1.getDistance(item2);
				if(distance < minDistance)
				{
					minDistance = distance;
				}
			}
		}
		return minDistance;
	}
	public double getMinDistance(Cluster otherCluster, double[][] distanceMatrix)
	{
		ArrayList<Integer> otherItemIndices = otherCluster.getIndices();
		double minDistance = Integer.MAX_VALUE;
		for(Integer item1index: itemIndices)
		{
			for(Integer item2index: otherItemIndices)
			{
				if(item1index == item2index)
				{
					continue;
				}
				double distance = distanceMatrix[item1index][item2index];
				if(distance < minDistance)
				{
					minDistance = distance;
				}
			}
		}
		return minDistance;
	}
	public boolean isSingleton()
	{
		return false;
	}
	
	@Override
	public String toString() {
		String str ="{ height:"+this.height+", depth:"+this.getDepth()+",";
		if(this.children != null)
		{	
			str+="\"children\":[";
			for(Cluster cluster: children)
			{
				str+=cluster+",";
			}
			str +="]";
		}
		else
		{
			str+="\"item\":";
			for(ClusteringItem item: items)
			{
				str+="{\"id\":"+item.getId()+", \"value\":\""+item.getStringValue().subSequence(0, 10)+"\"}";
			}
			
		}
		str+="}";
		return str;
	}
}
