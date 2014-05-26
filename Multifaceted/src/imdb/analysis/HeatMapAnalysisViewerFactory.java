package imdb.analysis;

import perspectives.base.Viewer;
import perspectives.base.ViewerFactory;

public class HeatMapAnalysisViewerFactory extends ViewerFactory{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5062138460885607687L;

	@Override
	public Viewer create(String name) {
		if (this.isAllDataPresent())
			return new HeatMapAnalysisViewer(name);
		return null;
	}

	@Override
	public String creatorType() {
		// TODO Auto-generated method stub
		return "Heatmap Analysis Viewer";
	}

	@Override
	public RequiredData requiredData() {
		// TODO Auto-generated method stub

		return null;
	}

}
