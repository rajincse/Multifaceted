package eyeinterestanalyzer;

import perspectives.base.Viewer;
import perspectives.base.ViewerFactory;

public class EyeInterestAnalyzerViewerFactory extends ViewerFactory{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5017872009533608188L;

	@Override
	public Viewer create(String name) {
		// TODO Auto-generated method stub
		return new EyeInterestAnalyzer(name);
	}

	@Override
	public String creatorType() {
		// TODO Auto-generated method stub
		return "EyeInterestAnalyzer";
	}

	@Override
	public RequiredData requiredData() {
		// TODO Auto-generated method stub
		return null;
	}

}
