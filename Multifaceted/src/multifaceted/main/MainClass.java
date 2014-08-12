package multifaceted.main;

import imdb.IMDBDataFactory;
import imdb.IMDBViewerFactory;
import imdb.analysis.HeatMapAnalysisViewerFactory;
import imdb.analysis.ProbabilityViewerFactory;
import imdb.analysis.StripViewerFactory;
import perspectives.base.Environment;
import recommend.RecommendViewerFactory;

public class MainClass {
	
	public static void main(String[] args)
	{
		Environment e = new Environment(false);
		e.registerDataSourceFactory(new IMDBDataFactory());
		e.registerViewerFactory(new IMDBViewerFactory());
		e.registerViewerFactory(new StripViewerFactory());
		e.registerViewerFactory(new HeatMapAnalysisViewerFactory());
		e.registerViewerFactory(new ProbabilityViewerFactory());
		e.registerViewerFactory(new RecommendViewerFactory());
	}
}
