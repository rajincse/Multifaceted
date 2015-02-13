package eyeinterestanalyzer.feature;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import multifaceted.Util;

import eyeinterestanalyzer.DataObject;
import eyeinterestanalyzer.LevenshteinDistance;
import eyeinterestanalyzer.clustering.HierarchicalClustering;
import eyeinterestanalyzer.clustering.distance.SliceElement;
import eyeinterestanalyzer.clustering.distance.TimeSlice;

public class PercentileFeature extends Feature{

	@Override
	public double getFeatureDistance(ArrayList<TimeSlice> slice1,
			ArrayList<TimeSlice> slice2) {

		String string1 = this.getStripString(slice1, PRECISION);
		String string2 = this.getStripString(slice2, PRECISION);
		
		double distance = LevenshteinDistance.getLevenshteinDistance(string1, string2);
		return distance;
	}
	
	public String getStripString(ArrayList<TimeSlice> slices, int precision)
	{
		double totalValue =0;
		HashMap<Integer, Double> valueMap = new HashMap<Integer, Double>();
		for(TimeSlice slice: slices )
		{
			for(SliceElement elem: slice.getSliceElements())
			{
				DataObject obj = elem.getObject();
				
				if(valueMap.containsKey(obj.getType()))
				{
					double value =valueMap.get(obj.getType());
					value+= elem.getValue()*slice.getTotalValue();
					valueMap.put(obj.getType(), value);
					totalValue+= elem.getValue()*slice.getTotalValue();
					
				}
				else
				{
					totalValue+= elem.getValue()*slice.getTotalValue();
					valueMap.put(obj.getType(),elem.getValue()*slice.getTotalValue());
				}
			}
		}
		
		String string ="";
		for(int type: valueMap.keySet())
		{
			double value = valueMap.get(type);
			int numberOfCopies = (int)(value* precision/ totalValue );
			for(int i=0;i<numberOfCopies;i++)
			{
				string+=type;
			}
		}
		return string;
	}

	@Override
	public void render(Graphics2D g,double[][] heatmap, int totalTimeCells, 
			int imWidth, int imHeight , ArrayList<DataObject> dataObjects) {
	
		
		double totalVal=0;
		double [] typeVal = new double[6];
		DataObject[] typeObjects = new DataObject[6];
		for(int i=0;i<typeVal.length;i++)
		{
			typeVal[i] = 0;
		}
		for(int j=0;j<totalTimeCells;j++)
		{
			
			
			
			for(int i=0;i<heatmap.length;i++)
			{
				DataObject object = dataObjects.get(i);
				typeObjects[object.getType()] = object;
				if(heatmap[i][j] > 0)
				{
					
					typeVal[object.getType()]+= heatmap[i][j];
				}
			}
			
			
		
			
		}
		for(int i=0;i<typeVal.length;i++)
		{
			totalVal+= typeVal[i];
					
		}
		int lastX=0;
		for(int i=0;i<typeVal.length;i++)
		{
			int width =(int)( typeVal[i]*imWidth / totalVal);
			g.setColor(Util.getScarfplotColor(i));
			g.fillRect(lastX, 0, width, imHeight);
			g.setColor(Color.black);
			g.drawString(String.format("%.2f", typeVal[i]*100/totalVal), lastX, imHeight);
			lastX = lastX+width;
		}

		
	}

}

