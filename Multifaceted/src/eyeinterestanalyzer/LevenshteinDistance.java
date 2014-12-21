package eyeinterestanalyzer;

import java.util.ArrayList;

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
}
