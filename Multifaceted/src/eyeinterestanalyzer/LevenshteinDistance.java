package eyeinterestanalyzer;

import java.util.ArrayList;

import eyeinterestanalyzer.clustering.ClusteringItem;
import eyeinterestanalyzer.clustering.distance.SliceElement;
import eyeinterestanalyzer.clustering.distance.TimeSlice;

public class LevenshteinDistance {
	
	public static final int INFINITY =100;
	public static final int PRECISION =10;
	
	private static int minimum(int a, int b, int c) {                            
	        return Math.min(Math.min(a, b), c);                                      
	}
	public static double getDiscreteDistance(ArrayList<TimeSlice> slice1, ArrayList<TimeSlice> slice2)
	{
		double distance = Math.abs( slice1.size() - slice2.size());
		int minSize = Math.min(slice1.size(), slice2.size());
		for(int i=0;i<minSize;i++)
		{
			TimeSlice timeSlice1= slice1.get(i);
			TimeSlice timeSlice2= slice2.get(i);
			distance += getSliceDifference(timeSlice1, timeSlice2);
		}
		return distance;
	}
	public static double getSliceDifference(TimeSlice slice1, TimeSlice slice2)
	{
		String string1 =slice1.getSliceString(PRECISION);
		String string2 =slice2.getSliceString(PRECISION);
		
		double distance = LevenshteinDistance.getLevenshteinDistance(string1, string2);
		distance = distance / PRECISION;
		
		return distance;
	}
	
	
	public static int getLevenshteinDistanceDelimitedString(String str1,String str2) {
		String[] string1Array = str1.split(ClusteringItem.DELIMITER, str1.length());
		String[] string2Array = str2.split(ClusteringItem.DELIMITER, str2.length());
		
		int maxLength = Math.max(string1Array.length, string2Array.length);
		String modifiedStr1 ="";
		String modifiedStr2 ="";
		
		for(int i=0;i<maxLength;i++)
		{	
			if(i< string1Array.length && i< string2Array.length )
			{
				if(isSimilar(string1Array[i], string2Array[i]))
				{
					modifiedStr1+="1";
					modifiedStr2+="1";
				}
				else
				{
					modifiedStr1+="2";
					modifiedStr2+="3";
				}
				
			}
			else if(i< string1Array.length )
			{
				modifiedStr1+=ClusteringItem.DELIMITER;
			}
			else if(i< string2Array.length )
			{
				modifiedStr2+=ClusteringItem.DELIMITER;
			}
			
		}
		
		int distance = getLevenshteinDistance(modifiedStr1, modifiedStr2);
		
		return distance;
	}
	public static boolean isSimilar(String str1,String str2) {
		if(str1.equals(str2))
		{
			return true;
		}
		else 
		{
			for(int i=0;i<str1.length();i++)
			{
				char c = str1.charAt(i);
				if(str2.contains(""+c))
				{
					return true;
				}
			}
			return false;
		}
		
	}
	public static int getLevenshteinDistance(String str1,String str2) {      
        int[][] distance = new int[str1.length() + 1][str2.length() + 1];        
 
        for (int i = 0; i <= str1.length(); i++)                                 
            distance[i][0] = i;                                                  
        for (int j = 1; j <= str2.length(); j++)                                 
            distance[0][j] = j;                                                  
 
        for (int i = 1; i <= str1.length(); i++)                                 
            for (int j = 1; j <= str2.length(); j++)                             
                distance[i][j] = minimum(                                        
                        distance[i - 1][j] + 1,                                  
                        distance[i][j - 1] + 1,                                  
                        distance[i - 1][j - 1] + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1));
 
        return distance[str1.length()][str2.length()];                           
    }
}
