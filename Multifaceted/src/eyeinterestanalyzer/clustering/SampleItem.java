package eyeinterestanalyzer.clustering;

import java.util.ArrayList;

import eyeinterestanalyzer.LevenshteinDistance;

public class SampleItem implements ClusteringStringItem {

	private String id ;
	private String value;
	public SampleItem(String id, String value)	
	{
		this.id = id;
		this.value = value;
	}
	
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public String getStringValue() {
		// TODO Auto-generated method stub
		return this.value;
	}

	@Override
	public int getDistance(ClusteringStringItem otherItem) {
		// TODO Auto-generated method stub
		return LevenshteinDistance.getLevenshteinDistance(getStringValue(), otherItem.getStringValue());
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{id:"+this.getId()+", value:"+this.getStringValue()+"}";
	}

	
	public static void main(String[] args)
	{
		SampleItem item1 = new SampleItem("1", "AMA");
		SampleItem item2 = new SampleItem("2", "ABA");
		
		SampleItem item3 = new SampleItem("3", "CDA");
		SampleItem item4 = new SampleItem("4", "CFA");
		
		SampleItem item0 = new SampleItem("0", "EFG");
		
		ArrayList<ClusteringStringItem> items = new ArrayList<ClusteringStringItem>();
		items.add(item0);
		items.add(item1);
		items.add(item2);
		items.add(item3);
		items.add(item4);
		
		
		HierarchicalClustering hierarchicalClustering = new HierarchicalClustering(items);
		System.out.println("Hierarchy:\r\n"+hierarchicalClustering.getRoot());
	}
}
