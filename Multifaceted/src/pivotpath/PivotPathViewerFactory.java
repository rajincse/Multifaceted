package pivotpath;

import imdb.IMDBDataSource;
import perspectives.base.Viewer;
import perspectives.base.ViewerFactory;

public class PivotPathViewerFactory extends ViewerFactory{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1643893574389340971L;

	@Override
	public Viewer create(String name) {
		if (this.isAllDataPresent())
			return new PivotPathViewer(name, (IMDBDataSource)this.getData().get(0));
		return null;
	}

	@Override
	public String creatorType() {
		// TODO Auto-generated method stub
		return "PivotPaths Viewer";
	}

	@Override
	public RequiredData requiredData() {
		// TODO Auto-generated method stub

		RequiredData rd = new RequiredData("IMDBDataSource","1");
		return rd;
	}

}

