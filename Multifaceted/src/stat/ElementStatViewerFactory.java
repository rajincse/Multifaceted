package stat;

import imdb.IMDBDataSource;
import perspectives.base.Viewer;
import perspectives.base.ViewerFactory;
import perspectives.base.ViewerFactory.RequiredData;
import pivotpath.PivotPathViewer;

public class ElementStatViewerFactory extends ViewerFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2884082373625257172L;

	@Override
	public Viewer create(String name) {
		// TODO Auto-generated method stub
		return new ElementStatViewer(name, (IMDBDataSource)this.getData().get(0));
	}

	@Override
	public String creatorType() {
		// TODO Auto-generated method stub
		return "ElementStatViewer";
	}

	@Override
	public RequiredData requiredData() {
		// TODO Auto-generated method stub
		RequiredData rd = new RequiredData("IMDBDataSource","1");
		return rd;
	}

}
