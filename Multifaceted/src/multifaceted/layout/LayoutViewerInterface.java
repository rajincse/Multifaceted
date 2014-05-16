package multifaceted.layout;

public interface LayoutViewerInterface {
	public void callRequestRender();
	public void selectItem(String id, String name);
	public void callSetToolTipText(String text);
}
