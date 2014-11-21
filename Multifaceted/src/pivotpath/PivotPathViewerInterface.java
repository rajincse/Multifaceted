package pivotpath;

import multifaceted.layout.LayoutViewerInterface;

public interface PivotPathViewerInterface extends LayoutViewerInterface{
	public static final int GROUP_DATA=0;
	public static final int GROUP_ATTRIBUTE=1;
	public void callSaveView();
	public void hoverDetected(int type,int groupIndex, int elementIndex);
	public void selectItem(String id, String name, int type);
	public void setLock(boolean lock);
}
