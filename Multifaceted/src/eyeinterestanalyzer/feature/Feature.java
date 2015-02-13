package eyeinterestanalyzer.feature;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import eyeinterestanalyzer.DataObject;
import eyeinterestanalyzer.clustering.distance.TimeSlice;

public abstract class Feature {
	public static final int PRECISION = 20;
	
	public abstract double getFeatureDistance(ArrayList<TimeSlice> slice1, ArrayList<TimeSlice> slice2);
	public abstract void render(Graphics2D g,double[][] heatmap, int totalTimeCells, 
			int imageWidth, int imageHeight , ArrayList<DataObject> dataObjects);
}
