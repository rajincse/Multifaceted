package eyeinterestanalyzer.clustering;

import java.util.ArrayList;

public class HierarchicalClustering {

	private int[][] distanceMatrix;
	private ArrayList<ClusteringStringItem> items;
	public HierarchicalClustering(ArrayList<ClusteringStringItem> items)
	{
		this.distanceMatrix = new int[items.size()][items.size()];
		this.items = items;
		for(int i=0;i<this.distanceMatrix.length;i++)
		{
			for(int j=0;j<this.distanceMatrix[i].length;j++)
			{
				if(i==j)
				{
					this.distanceMatrix[i][j]=0;
				}
				else
				{
					ClusteringStringItem item1 = items.get(i);
					ClusteringStringItem item2 = items.get(j);
					this.distanceMatrix[i][j] = item1.getDistance(item2);
				}
			}
				
		}
	}
	
	public Cluster getRoot()
	{
		ArrayList<Cluster> clusters = new ArrayList<Cluster>();
		for(int i=0;i<this.items.size();i++)
		{
			clusters.add(new Cluster(this.items.get(i),i ));
		}
		
		while(clusters.size() != 1)
		{
			clusters = reduce(clusters);
		}
		
		return clusters.get(0);
	}
	
	public ArrayList<Cluster> reduce(ArrayList<Cluster> clusters)
	{
		int clusterIndex1 =-1;
		int clusterIndex2 =-1;
		
		int minDistance = Integer.MAX_VALUE;
		for(int i=0;i<clusters.size()-1;i++)
		{
			
			for(int j=i+1;j<clusters.size();j++)
			{
				Cluster cluster1 = clusters.get(i);
				Cluster cluster2 = clusters.get(j);
				int distance = cluster1.getMinDistance(cluster2, distanceMatrix);
				
				if(distance < minDistance)
				{
					minDistance = distance;
					clusterIndex1 = i;
					clusterIndex2 = j;
				}
			}
		}
		
		ArrayList<Cluster> reducedList = new ArrayList<Cluster>();
		for(int i=0;i<clusters.size();i++)
		{
			if(i!= clusterIndex1 && i != clusterIndex2)
			{
				Cluster cluster = clusters.get(i);
				reducedList.add(cluster);
			}
		}
		//New Cluster
		ArrayList<Cluster> children = new ArrayList<Cluster>();
		Cluster cluster1 = clusters.get(clusterIndex1);
		Cluster cluster2 = clusters.get(clusterIndex2);
		children.add(cluster1);
		children.add(cluster2);
		Cluster newCluster = new Cluster(children);
		reducedList.add(newCluster);
		
		return reducedList;
	}
}
