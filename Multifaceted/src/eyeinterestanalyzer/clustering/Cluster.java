package eyeinterestanalyzer.clustering;

import java.util.ArrayList;

public class Cluster {
	protected ArrayList<Cluster> children;
	protected ArrayList<ClusteringStringItem> items;
	protected ArrayList<Integer> itemIndices;
	
	protected int height;
	protected Cluster parent;
	
	public Cluster(ClusteringStringItem item, int index)
	{
		this.children = null;
		this.items = new ArrayList<ClusteringStringItem>();
		items.add(item);
		
		this.itemIndices = new ArrayList<Integer>();
		this.itemIndices.add(index);
		this.height =0;
		this.parent = null;
	}
	public Cluster(ArrayList<Cluster> children)
	{
		this.children = children;
		this.items = new ArrayList<ClusteringStringItem>();
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
	public Cluster getParent() {
		return parent;
	}
	public void setParent(Cluster parent) {
		this.parent = parent;
	}
	public ArrayList<Cluster> getChildren() {
		return children;
	}
	public ArrayList<ClusteringStringItem> getItems()
	{
		return this.items;
	}
	
	public ArrayList<Integer> getIndices()
	{
		return this.itemIndices;
	}
	
	public int getMinDistance(Cluster otherCluster)
	{
		ArrayList<ClusteringStringItem> otherItems = otherCluster.getItems();
		int minDistance = Integer.MAX_VALUE;
		for(ClusteringStringItem item1: items)
		{
			for(ClusteringStringItem item2: otherItems)
			{
				if(item1.getId().equals(item2.getId()))
				{
					continue;
				}
				int distance = item1.getDistance(item2);
				if(distance < minDistance)
				{
					minDistance = distance;
				}
			}
		}
		return minDistance;
	}
	public int getMinDistance(Cluster otherCluster, int[][] distanceMatrix)
	{
		ArrayList<Integer> otherItemIndices = otherCluster.getIndices();
		int minDistance = Integer.MAX_VALUE;
		for(Integer item1index: itemIndices)
		{
			for(Integer item2index: otherItemIndices)
			{
				if(item1index == item2index)
				{
					continue;
				}
				int distance = distanceMatrix[item1index][item2index];
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
		String str ="{ height:"+this.height+", ";
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
			for(ClusteringStringItem item: items)
			{
				str+="{\"id\":"+item.getId()+", \"value\":\""+item.getStringValue().subSequence(0, 10)+"\"}";
			}
			
		}
		str+="}";
		return str;
	}
}
