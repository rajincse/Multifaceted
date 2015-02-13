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
import eyeinterestanalyzer.clustering.distance.SliceElement;
import eyeinterestanalyzer.clustering.distance.TimeSlice;

public class ParticularPercentile extends Feature{

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
		HashMap<String, Double> valueMap = new HashMap<String, Double>();
		for(TimeSlice slice: slices )
		{
			for(SliceElement elem: slice.getSliceElements())
			{
				DataObject obj = elem.getObject();
				
				if(valueMap.containsKey(obj.getId()))
				{
					double value =valueMap.get(obj.getId());
					value+= elem.getValue()*slice.getTotalValue();
					valueMap.put(obj.getId(), value);
					totalValue+= elem.getValue()*slice.getTotalValue();
					
				}
				else
				{
					totalValue+= elem.getValue()*slice.getTotalValue();
					valueMap.put(obj.getId(), elem.getValue()*slice.getTotalValue());
				}
			}
		}
		
		String string ="";
		for(String id: valueMap.keySet())
		{
			double value = valueMap.get(id);
			int numberOfCopies = (int)(value* precision/ totalValue );
			for(int i=0;i<numberOfCopies;i++)
			{
				string+="#"+id+"#";
			}
		}
		
		return string;
	}
	@Override
	public void render(Graphics2D g,double[][] heatmap, int totalTimeCells, 
			int imWidth, int imHeight , ArrayList<DataObject> dataObjects)
	{
		
		double totalValue =0;
		HashMap<String, Double> valueMap = new HashMap<String, Double>();
		
		for(int j=0;j<totalTimeCells;j++)
		{
			
			for(int i=0;i<heatmap.length;i++)
			{
				DataObject object = dataObjects.get(i);
				if(valueMap.containsKey(object.getId()))
				{
					double value =valueMap.get(object.getId());
					value+= heatmap[i][j];
					valueMap.put(object.getId(), value);
					totalValue+= heatmap[i][j];
					
				}
				else
				{
					totalValue+= heatmap[i][j];
					valueMap.put(object.getId(), heatmap[i][j]);
				}

			}
			
			
		
			
		}
		HashMap<String, Color> colorStore = new HashMap<String, Color>();
		
		int lastX=0;
		for(String id: valueMap.keySet())
		{
			
			int width =(int)(valueMap.get(id)*imWidth / totalValue);
			
			Color color = Color.black;
			if(colorStore.containsKey(id))
			{
				color = colorStore.get(id);							
			}
			else
			{
				Random rand = new Random();
				int red = rand.nextInt(255);
				int green = rand.nextInt(255);
				int blue = rand.nextInt(255);
				color = new Color(red, green, blue);
				while(colorStore.containsValue(color))
				{
					red = rand.nextInt(255);
					green = rand.nextInt(255);
					blue = rand.nextInt(255);
					color = new Color(red, green, blue);
				}
				colorStore.put(id, color);
			}
			g.setColor(color);
			
			g.fillRect(lastX, 0, width, imHeight);
			lastX = lastX+width;
		}

		
	}

}
