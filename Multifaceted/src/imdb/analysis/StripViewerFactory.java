package imdb.analysis;

import perspectives.base.Viewer;
import perspectives.base.ViewerFactory;

public class StripViewerFactory extends ViewerFactory{

	private static final long serialVersionUID = 7665877958735358381L;

	@Override
	public Viewer create(String name) {
		if (this.isAllDataPresent())
			return new StripViewer(name);
		return null;
	}

	@Override
	public String creatorType() {
		// TODO Auto-generated method stub
		return "Strip Viewer";
	}

	@Override
	public RequiredData requiredData() {
		// TODO Auto-generated method stub

		return null; 
	}

}
