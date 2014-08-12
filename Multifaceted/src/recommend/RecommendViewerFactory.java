package recommend;

import imdb.IMDBDataSource;
import perspectives.base.Viewer;
import perspectives.base.ViewerFactory;

public class RecommendViewerFactory extends ViewerFactory{

	private static final long serialVersionUID = -4427363420826763533L;

	@Override
	public Viewer create(String name) {
		if (this.isAllDataPresent())
			return new RecommendedMovieElementViewer(name, (IMDBDataSource)this.getData().get(0));
		return null;
	}

	@Override
	public String creatorType() {
		// TODO Auto-generated method stub
		return "Recommned Viewer";
	}

	@Override
	public RequiredData requiredData() {
		// TODO Auto-generated method stub

		RequiredData rd = new RequiredData("IMDBDataSource","1");
		return rd;
	}

}