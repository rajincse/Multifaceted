package eyeinterestanalyzer;

import java.util.ArrayList;

import eyeinterestanalyzer.clustering.ClusteringStringItem;

public class LevenshteinDistance {
	 private static int minimum(int a, int b, int c) {                            
	        return Math.min(Math.min(a, b), c);                                      
	    }  
	 
	
	public static int getLevenshteinDistance(ArrayList<DataObject> user1, ArrayList<DataObject>user2)
	{
		 int[][] distance = new int[user1.size() + 1][user2.size() + 1];        
		 
	        for (int i = 0; i <= user1.size(); i++)                                 
	            distance[i][0] = i;                                                  
	        for (int j = 1; j <= user2.size(); j++)                                 
	            distance[0][j] = j;                                                  
	 
	        for (int i = 1; i <= user1.size(); i++)                                 
	            for (int j = 1; j <= user2.size(); j++)                             
	                distance[i][j] = minimum(                                        
	                        distance[i - 1][j] + 1,                                  
	                        distance[i][j - 1] + 1,                                  
	                        distance[i - 1][j - 1] + ((user1.get(i - 1).equals(user2.get(j - 1))) ? 0 : 1));
	 
	        return distance[user1.size()][user2.size()];    
	}
	public static int getLevenshteinDistanceDelimitedString(String str1,String str2) {
		String[] string1Array = str1.split(ClusteringStringItem.DELIMITER, str1.length());
		String[] string2Array = str2.split(ClusteringStringItem.DELIMITER, str2.length());
		
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
				modifiedStr1+=ClusteringStringItem.DELIMITER;
			}
			else if(i< string2Array.length )
			{
				modifiedStr2+=ClusteringStringItem.DELIMITER;
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
