package pivotpath;

import imdb.IMDBDataSource;
import perspectives.base.Viewer;
import perspectives.base.ViewerFactory;

public class PivotPathImageGeneratorViewerFactory extends ViewerFactory{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Viewer create(String name) {
		if (this.isAllDataPresent())
			return new PivotPathImageGenerator(name, (IMDBDataSource)this.getData().get(0));
		return null;
	}

	@Override
	public String creatorType() {
		// TODO Auto-generated method stub
		return "PivotPaths Image Generator";
	}

	@Override
	public RequiredData requiredData() {
		// TODO Auto-generated method stub

		RequiredData rd = new RequiredData("IMDBDataSource","1");
		return rd;
	}

}
