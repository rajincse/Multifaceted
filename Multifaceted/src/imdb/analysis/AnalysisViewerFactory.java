package imdb.analysis;

import imdb.IMDBDataSource;
import perspectives.base.Viewer;
import perspectives.base.ViewerFactory;

public class AnalysisViewerFactory extends ViewerFactory{

	private static final long serialVersionUID = 7665877958735358381L;

	@Override
	public Viewer create(String name) {
		if (this.isAllDataPresent())
			return new AnalysisViewer(name, (IMDBDataSource)this.getData().get(0));
		return null;
	}

	@Override
	public String creatorType() {
		// TODO Auto-generated method stub
		return "Analysis Viewer";
	}

	@Override
	public RequiredData requiredData() {
		// TODO Auto-generated method stub

		RequiredData rd = new RequiredData("IMDBDataSource","1");
		return rd;
	}

}
