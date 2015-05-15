package realtime;

import perspectives.base.Viewer;
import perspectives.base.ViewerFactory;

public class StreamDataViewerFactory extends ViewerFactory {

	@Override
	public Viewer create(String name) {
		// TODO Auto-generated method stub
		return new EyeTrackDataStreamViewer(name);
	}

	@Override
	public String creatorType() {
		// TODO Auto-generated method stub
		return "StreamViewer";
	}

	@Override
	public RequiredData requiredData() {
		// TODO Auto-generated method stub
		return null;
	}

}
