package eyerecommend.utility;

public class Util {
	public static double gaussianDistribution(double value, double mean, double deviation)
	{	
		return Math.exp(-1.0* (value - mean) * (value - mean) / (2 * deviation * deviation) ) / (deviation * Math.sqrt(2 * Math.PI));
	}
}
