package doiriver;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import multifaceted.InsidePolygonTester;
import multifaceted.Util;

import perspectives.util.SplineFactory;

import realtime.DataObject;

public class River {
	public DataObject dataObject;
	
	private  double[] timeScores;
	
	private Color color;

	private River previousRiver =null;
	
	private boolean isSelected =false;

	public River(DataObject dataObject, double[] timeScores, Color color)
	{
		this.dataObject = dataObject;
		this.timeScores = timeScores;
		this.color = color;
	}
	
	public Point getControlPoint(int timeIndex)
	{
		int x =timeIndex * DOIRiverViewer.TIME_CELL_WIDTH+DOIRiverViewer.TIME_CELL_WIDTH/2;
		int y =0;
		if(previousRiver != null)
		{
			y = previousRiver.getControlPoint(timeIndex).y+(int) this.timeScores[timeIndex] * DOIRiverViewer.CURVE_WIDTH;
		}
		else
		{
			y = (int) this.timeScores[timeIndex] * DOIRiverViewer.CURVE_WIDTH;
		}
		
		return new Point(x,y);
	}

	public int[] getOuterEdgePointsX()
	{
		int[] xPoints = new int[timeScores.length+2];
		xPoints[0] =0;
		for(int i=0;i<timeScores.length;i++)
		{
			xPoints[i+1] = getControlPoint(i).x;
		}
		
		xPoints[timeScores.length+1] =timeScores.length * DOIRiverViewer.TIME_CELL_WIDTH;
		return xPoints;
	}
	public int[] getOuterEdgePointsY()
	{
		int[] yPoints = new int[timeScores.length+2];
		yPoints[0] =0;
		for(int i=0;i<timeScores.length;i++)
		{
			yPoints[i+1] = getControlPoint(i).y;
		}
		
		yPoints[timeScores.length+1] =0;
		return yPoints;
	}
	
	public int[] getPolygonX()
	{
		int [] outerEdgeX = this.getOuterEdgePointsX();
		if(previousRiver != null)
		{
			 
			int[] xPoints = new int[outerEdgeX.length+previousRiver.getOuterEdgePointsX().length-1];
			int index=0;
			for(int i=0;i<outerEdgeX.length;i++)
			{
				xPoints[i] = outerEdgeX[i];
				index++;
			}
			int[] previousRiverOuterX = previousRiver.getOuterEdgePointsX();
			for(int i=previousRiverOuterX.length-2;i>0;i--)
			{
				xPoints[index] = previousRiverOuterX[i];
				index++;
			}
			
			return xPoints;
		}
		else
		{
			int[] xPoints = new int[outerEdgeX.length+1];
			for(int i=0;i<outerEdgeX.length;i++)
			{
				xPoints[i] = outerEdgeX[i];
			}
			xPoints[outerEdgeX.length] =0;
			
			return xPoints;
		}
	}
	
	public int[] getPolygonY()
	{
		int [] outerEdgeY = this.getOuterEdgePointsY();
		if(previousRiver != null)
		{
			 
			int[] yPoints = new int[outerEdgeY.length+previousRiver.getOuterEdgePointsY().length-1];
			int index=0;
			for(int i=0;i<outerEdgeY.length;i++)
			{
				yPoints[i] = outerEdgeY[i];
				index++;
			}
			int[] previousRiverOuterY = previousRiver.getOuterEdgePointsY();
			for(int i=previousRiverOuterY.length-2;i>0;i--)
			{
				yPoints[index] = previousRiverOuterY[i];
				index++;
			}
			
			return yPoints;
		}
		else
		{
			int[] yPoints = new int[outerEdgeY.length+1];
			for(int i=0;i<outerEdgeY.length;i++)
			{
				yPoints[i] = outerEdgeY[i];
			}
			yPoints[outerEdgeY.length] =0;
			
			return yPoints;
		}
	}
	
	public void prepareRendering()
	{
		int[] polygonX = getPolygonX();
		int[] polygonY = getPolygonY();
		
		double[] controlPoints = new double[polygonX.length*3];
		for(int i=0;i<polygonX.length;i++)
		{
			controlPoints[3*i] = polygonX[i];
			controlPoints[3*i+1] = polygonY[i];
			controlPoints[3*i+2] = 0;
		}
		double[] curvePoints = SplineFactory.createCatmullRom(controlPoints, polygonX.length);
		this.xPoints  = new int[ curvePoints.length/3];
		this.yPoints  = new int[ curvePoints.length/3];
		
		for(int i=0;i<xPoints.length;i++)
		{
			xPoints[i] = (int)curvePoints[3*i];
			yPoints[i] = (int)curvePoints[3*i+1];
		}
	}
	private int[] xPoints;
	private int[] yPoints;
	public int[] getCurvedPolygonX()
	{
		return xPoints;
	}
	
	public int[] getCurvedPolygonY()
	{
		return yPoints;
	}
	public Color getColor() {
		return color;
	}
	public double[] getTimeScores() {
		return timeScores;
	}

	public void setTimeScores(double[] timeScores) {
		this.timeScores = timeScores;
	}


	public DataObject getDataObject() {
		return dataObject;
	}


	@Override
	public String toString() {
		return "River [dataObject=" + dataObject + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataObject == null) ? 0 : dataObject.hashCode());
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
		River other = (River) obj;
		if (dataObject == null) {
			if (other.dataObject != null)
				return false;
		} else if (!dataObject.equals(other.dataObject))
			return false;
		return true;
	}


	public River getPreviousRiver() {
		return previousRiver;
	}


	public void setPreviousRiver(River previousRiver) {
		this.previousRiver = previousRiver;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	public boolean containsPointInside(int x, int y)
	{
		int [] polygonX = getPolygonX();
		int [] polygonY = getPolygonY();
		Point[] points = new Point[polygonX.length];

		for(int i=0;i<polygonX.length;i++)
		{
			points[i] = new Point(polygonX[i], polygonY[i]);
		}

		boolean contains = InsidePolygonTester.isInside(points, points.length, new Point(x,y));

		return contains;
	}
	
}
