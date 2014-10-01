package pivotpath.analysis;


import perspectives.base.Viewer;
import perspectives.base.ViewerFactory;

public class PivotPathSimulationViewerFactory extends ViewerFactory{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5178108044438420306L;

	@Override
	public Viewer create(String name) {
		// TODO Auto-generated method stub
		if(isAllDataPresent())
		{
			return new PivotPathSimulationViewer(name);
		}
		return null;
	}

	@Override
	public String creatorType() {
		// TODO Auto-generated method stub
		return "PivotPathSimulationViewer";
	}

	@Override
	public RequiredData requiredData() {
		// TODO Auto-generated method stub
		return null;
	}

}
