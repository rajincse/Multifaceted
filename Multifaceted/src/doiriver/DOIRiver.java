package doiriver;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import perspectives.util.SplineFactory;



import realtime.DataObject;

public class DOIRiver implements Comparable<DOIRiver>{
	public int index;
	public DataObject dataObject;
	public HashMap<Integer, Integer> heightMap = new HashMap<Integer, Integer>();
	public HashMap<Integer, ArrayList<DOIRiver>> sourceRivers = new HashMap<Integer, ArrayList<DOIRiver>>();
	public HashMap<Integer, ArrayList<DOIRiver>> destinationRivers = new HashMap<Integer, ArrayList<DOIRiver>>();
	
	public int curveXPoints[] ;
	public int curveYPoints[] ;
	public Color color;
	
	public DOIRiver(DataObject dataObject, int index, int timeIndex) {
		this.dataObject = dataObject;
		this.index = index;
		this.increaseHeight(timeIndex);
	}
	
	public DOIRiver(DataObject dataObject, int index) {
		this.dataObject = dataObject;
		this.index = index;
	}
	
	public void addSourceRiver(int timeIndex, DOIRiver river)
	{
		if(sourceRivers.containsKey(timeIndex) && ! sourceRivers.get(timeIndex).contains(river))
		{
			sourceRivers.get(timeIndex).add(river);
		}
		else
		{
			ArrayList<DOIRiver> riverList = new ArrayList<DOIRiver>();
			riverList.add(river);
			sourceRivers.put(timeIndex, riverList);
		}
	}
	
	public void addDestinationRiver(int timeIndex, DOIRiver river)
	{
		if(destinationRivers.containsKey(timeIndex) && !destinationRivers.get(timeIndex).contains(river) )
		{
			destinationRivers.get(timeIndex).add(river);
		}
		else
		{
			ArrayList<DOIRiver> riverList = new ArrayList<DOIRiver>();
			riverList.add(river);
			destinationRivers.put(timeIndex, riverList);
		}
	}
	
	public void increaseHeight(int timeIndex)
	{
		if(this.heightMap.containsKey(timeIndex))
		{
			int height = this.heightMap.get(timeIndex);
			height++;
			this.heightMap.put(timeIndex, height);
		}
		else
		{
			this.heightMap.put(timeIndex, 1);
		}
		
	}
	
	public int getHeight(int timeIndex)
	{
		int height =0;
		if(heightMap.containsKey(timeIndex))
		{
			height = heightMap.get(timeIndex);
		}
		
		return height;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataObject == null) ? 0 : dataObject.hashCode());
		result = prime * result + index;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DOIRiver other = (DOIRiver) obj;
		if (dataObject == null) {
			if (other.dataObject != null)
				return false;
		} else if (!dataObject.equals(other.dataObject))
			return false;
		if (index != other.index)
			return false;
		return true;
	}
	ArrayList<Point> pointList = new ArrayList<Point>();

	public void prepareRender(int maxTimeIndex, int heightPerObject, Color color)
	{
		this.color = color;
		
		int originY = (int)((index+0.5) * heightPerObject);
		ArrayList<Point> forwardPoints = new ArrayList<Point>();
		ArrayList<Point> backwardPoints = new ArrayList<Point>();
		
		
		
		for(int timeIndex=0;timeIndex<maxTimeIndex;timeIndex++)
		{
			int x = timeIndex * DOIRiverViewer.TIME_CELL_WIDTH;
			int heightPixels =getHeight(timeIndex)* DOIRiverViewer.TIME_CELL_HEIGHT;
			
			
			if(heightPixels > 0)
			{
				forwardPoints.add(new Point(x+DOIRiverViewer.CURVE_WIDTH, originY +heightPixels/2));
				backwardPoints.add(0,new Point(x+DOIRiverViewer.CURVE_WIDTH, originY-heightPixels/2));
				
				forwardPoints.add(new Point(x+DOIRiverViewer.TIME_CELL_WIDTH-DOIRiverViewer.CURVE_WIDTH, originY +heightPixels/2));
				backwardPoints.add(0,new Point(x+DOIRiverViewer.TIME_CELL_WIDTH-DOIRiverViewer.CURVE_WIDTH, originY-heightPixels/2));
				
			}
			
			
			if(destinationRivers.containsKey(timeIndex))
			{
				
				ArrayList<DOIRiver> destinationRiverList = destinationRivers.get(timeIndex);
				Collections.sort(destinationRiverList);
				
				
				for(int i =0;i< destinationRiverList.size();i++)
				{
					
					DOIRiver destinationRiver = destinationRiverList.get(i);
					int y = (int)((destinationRiver.index+0.5) * heightPerObject);
					int destinationHeightPixel = destinationRiver.getHeight(timeIndex)* DOIRiverViewer.TIME_CELL_HEIGHT;
					
					forwardPoints.add(new Point(x+DOIRiverViewer.CURVE_WIDTH, y +destinationHeightPixel/2));
					forwardPoints.add(new Point(x+DOIRiverViewer.CURVE_WIDTH, y-destinationHeightPixel/2));
					
					int originHeightPixels =getHeight(timeIndex-1)* DOIRiverViewer.TIME_CELL_HEIGHT;
					if(destinationRiver.index > this.index)
					{
						y =  originY+originHeightPixels/2;
						forwardPoints.add(new Point(x-DOIRiverViewer.CURVE_WIDTH,y - (i+1)* DOIRiverViewer.CURVE_WIDTH));
						forwardPoints.add(new Point(x-DOIRiverViewer.CURVE_WIDTH, y- (i+2)* DOIRiverViewer.CURVE_WIDTH));
					}
					else
					{
						y =  originY-originHeightPixels/2;
						forwardPoints.add(new Point(x-DOIRiverViewer.CURVE_WIDTH,y + (i+1)* DOIRiverViewer.CURVE_WIDTH));
						forwardPoints.add(new Point(x-DOIRiverViewer.CURVE_WIDTH, y+ (i+2)* DOIRiverViewer.CURVE_WIDTH));
					}
					
				}
				
				
			}
			
			if(!destinationRivers.containsKey(timeIndex+1) && getHeight(timeIndex+1) == 0 && getHeight(timeIndex)>0)
			{
				forwardPoints.add(new Point(x+DOIRiverViewer.TIME_CELL_WIDTH+DOIRiverViewer.CURVE_WIDTH, originY));
			}

		}
		

		pointList.addAll(forwardPoints);
		pointList.addAll(backwardPoints);
		
		double[] xPoints = new double[pointList.size()];
		double[] yPoints = new double[pointList.size()];
		for(int i=0;i<pointList.size();i++)
		{
			xPoints[i] = pointList.get(i).x;
			yPoints[i] = pointList.get(i).y;
			
		}
		
		double[][] curvePoints =SplineFactory.createCubic(xPoints, yPoints, pointList.size());
		curveXPoints = new int[curvePoints.length];
		curveYPoints = new int[curvePoints.length];
		for(int i=0;i<curvePoints.length;i++)
		{
			curveXPoints[i] = (int)curvePoints[i][0];
			curveYPoints[i] = (int)curvePoints[i][1];
		}
		
//		curveXPoints = new int[pointList.size()];
//		curveYPoints = new int[pointList.size()];
//		for(int i=0;i<pointList.size();i++)
//		{
//			curveXPoints[i] = pointList.get(i).x;
//			curveYPoints[i] = pointList.get(i).y;
//		}
		
		
	}

	@Override
	public String toString() {
		return "DOIRiver [index=" + index + ", dataObject=" + dataObject + "]";
	}

	@Override
	public int compareTo(DOIRiver o) {
		// TODO Auto-generated method stub
		return o.index- this.index;
	}
	
	

}
