package multifaceted.main;

import eyeinterestanalyzer.EyeInterestAnalyzerViewerFactory;
import eyeinterestanalyzer.ScarfplotViewer;
import imdb.IMDBDataFactory;
import imdb.IMDBViewerFactory;
import imdb.analysis.HeatMapAnalysisViewerFactory;
import imdb.analysis.ProbabilityViewerFactory;
import imdb.analysis.StripViewerFactory;
import perspectives.base.Environment;
import pivotpath.PivotPathViewerFactory;
import pivotpath.analysis.PivotPathSimulationViewerFactory;
import recommend.RecommendViewerFactory;
import stat.ElementStatViewerFactory;

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
		e.registerViewerFactory(new PivotPathViewerFactory());
		e.registerViewerFactory(new PivotPathSimulationViewerFactory());
		e.registerViewerFactory(new EyeInterestAnalyzerViewerFactory());
		e.registerViewerFactory(new ElementStatViewerFactory());
//		e.addViewer(new ScarfplotViewer("Scarfplot"));
	}
}
