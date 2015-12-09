package multifaceted.main;

import java.text.NumberFormat;

import matrix.TransitionMatrixViewer;

import aoicreator.AOIEditor;
import doiriver.DOIRiverViewer;
import eyeinterestanalyzer.EyeInterestAnalyzerViewerFactory;
import eyeinterestanalyzer.ScarfplotViewer;
import imdb.IMDBDataFactory;
import imdb.IMDBViewerFactory;
import imdb.analysis.HeatMapAnalysisViewerFactory;
import imdb.analysis.ProbabilityViewerFactory;
import imdb.analysis.StripViewerFactory;
import perspectives.base.Environment;
import perspectives.graph.GraphDataFactory;
import perspectives.graph.GraphViewerFactory;
import pivotpath.PivotPathViewerFactory;
import pivotpath.analysis.PivotPathSimulationViewerFactory;
import realtime.EyeTrackDataStreamViewer;
import realtime.StreamDataViewerFactory;
import recommend.RecommendViewerFactory;
import scanpath.ScanpathViewer;
import stat.ElementStatViewerFactory;
import stat.TransitionProbabilityViewerFactory;

public class MainClass {
	
	public static void main(String[] args)
	{
		Environment e = new Environment(false);
		e.registerDataSourceFactory(new IMDBDataFactory());
//		e.registerViewerFactory(new IMDBViewerFactory());
//		e.registerViewerFactory(new StripViewerFactory());
//		e.registerViewerFactory(new HeatMapAnalysisViewerFactory());
//		e.registerViewerFactory(new TransitionProbabilityViewerFactory());
//		e.registerViewerFactory(new RecommendViewerFactory());
//		e.registerViewerFactory(new PivotPathViewerFactory());
//		e.registerViewerFactory(new PivotPathSimulationViewerFactory());
//		e.registerViewerFactory(new EyeInterestAnalyzerViewerFactory());
//		e.registerViewerFactory(new ElementStatViewerFactory());
//		e.registerViewerFactory(new StreamDataViewerFactory());
//		e.addViewer(new EyeTrackDataStreamViewer("Rajin"));
//		e.addViewer(new ScarfplotViewer("Scarfplot"));
//		e.addViewer(new ScanpathViewer("Scanpath"));
//		e.addViewer(new DOIRiverViewer("DOI River"));
//		e.addViewer(new EyeInstrument.EyeTester("EyeTester"));
//		e.addViewer(new AOIEditor("AOI Editor"));
//		e.registerDataSourceFactory(new GraphDataFactory());
//		e.registerViewerFactory(new GraphViewerFactory());
//		e.addViewer(new TransitionMatrixViewer("Matrix"));
		
	}
}
