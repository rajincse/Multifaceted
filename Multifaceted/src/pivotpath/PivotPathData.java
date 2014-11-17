package pivotpath;

import java.util.ArrayList;




public class PivotPathData {
	private ArrayList<String> dataList;
	private ArrayList<ArrayList<ArrayList<String>>> attributeList;
	public PivotPathData()
	{
		this.dataList = new ArrayList<String>();
		this.attributeList = new ArrayList<ArrayList<ArrayList<String>>>();
	}
	
	public int addData(String data)
	{
		this.dataList.add(data);
		
		return this.dataList.size()-1;
	}
	
	public int addData(ArrayList<String> data)
	{
		this.dataList.addAll(data);
		
		return this.dataList.size()-1;
	}
	public void addAttribute(int dataIndex, String[] attribute)
	{
		if(dataIndex >= this.attributeList.size())
		{
			this.attributeList.add(new ArrayList<ArrayList<String>>());
		}
		ArrayList<ArrayList<String>> dataAttribute = this.attributeList.get(dataIndex);
		if(dataAttribute == null)
		{
			
		}
		ArrayList<String> pivotAttribute  = new ArrayList<String>();
		for(String s: attribute)
		{
			pivotAttribute.add(s);
		}
		dataAttribute.add(pivotAttribute);
	
		
	}
	
	public void addAttribute(ArrayList<ArrayList<ArrayList<String>>> attribute)
	{
		this.attributeList.addAll(attribute);
	}
	public ArrayList<String> getDataList()
	{
		return this.dataList;
	}
	public String[] getData()
	{
		String[] data = new String[this.dataList.size()];
		for(int i=0;i<data.length;i++)
		{
			data[i] = this.dataList.get(i);
		}
		return data;
	}
	public ArrayList<ArrayList<ArrayList<String>>> getAttributeList()
	{
		return this.attributeList;
	}
	public String[][][] getAttribute()
	{
		String[][][] attribute = new String[this.attributeList.size()][][];
		for(int i=0;i<attribute.length;i++)
		{
			ArrayList<ArrayList<String>> dataAttribute = this.attributeList.get(i);
			attribute[i] = new String[dataAttribute.size()][];
			for(int j=0;j<attribute[i].length;j++)
			{
				ArrayList<String> pivotAttribute = dataAttribute.get(j);
				attribute[i][j] = new String[pivotAttribute.size()];
				for(int k=0;k<attribute[i][j].length;k++)
				{
					attribute[i][j][k] = pivotAttribute.get(k);
				}
			}
		}
		return attribute;
	}
	
}
