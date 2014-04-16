package imdb;

import perspectives.base.Viewer;
import perspectives.base.ViewerFactory;
import perspectives.base.ViewerFactory.RequiredData;
import perspectives.graph.GraphData;
import perspectives.graph.GraphViewer;

public class IMDBViewerFactory extends ViewerFactory{

	@Override
	public Viewer create(String name) {
		if (this.isAllDataPresent())
			return new IMDBViewer(name, (IMDBDataSource)this.getData().get(0));
		return null;
	}

	@Override
	public String creatorType() {
		// TODO Auto-generated method stub
		return "IMDB Viewer";
	}

	@Override
	public RequiredData requiredData() {
		// TODO Auto-generated method stub

		RequiredData rd = new RequiredData("IMDBDataSource","1");
		return rd;
	}

}
