package stat;

import perspectives.base.Viewer;
import perspectives.base.ViewerFactory;

public class TransitionProbabilityViewerFactory extends ViewerFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7150967973758828370L;

	@Override
	public Viewer create(String name) {
		// TODO Auto-generated method stub
		return new TransitionProbabilityViewer(name);
	}

	@Override
	public String creatorType() {
		// TODO Auto-generated method stub
		return "Transition Probability";
	}

	@Override
	public RequiredData requiredData() {
		// TODO Auto-generated method stub
		return null;
	}

}
