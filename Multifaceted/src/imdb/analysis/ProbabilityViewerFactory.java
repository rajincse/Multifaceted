package imdb.analysis;

import perspectives.base.Viewer;
import perspectives.base.ViewerFactory;

public class ProbabilityViewerFactory extends ViewerFactory{

	private static final long serialVersionUID = 5062138460885607687L;

	@Override
	public Viewer create(String name) {
		if (this.isAllDataPresent())
			return new ProbabilityViewer(name);
		return null;
	}

	@Override
	public String creatorType() {
		// TODO Auto-generated method stub
		return "Probability Viewer";
	}

	@Override
	public RequiredData requiredData() {
		// TODO Auto-generated method stub

		return null;
	}

}
