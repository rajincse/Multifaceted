package scanpath;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import multifaceted.ColorScheme;
import multifaceted.Util;

import realtime.DataObject;

public class MultiScanpathDiagram {
	public DataObject dataObject;
	public boolean isSelected;
	public MultiScanpathDiagram(DataObject dataObject, boolean isSelected) {
		this.dataObject = dataObject;
		this.isSelected = isSelected;
	}
	@Override
	public int hashCode() {		
		return dataObject.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MultiScanpathDiagram)
		{
			return ((MultiScanpathDiagram)obj).equals(this.dataObject);
		}
		else
		{
			return dataObject.equals(obj);
		}
		
	}
	
	// Draw just outline of the diagram
	public void render(Graphics2D g, ArrayList<String> userList, Dimension imageDimension, Color horizontalLineColor, int selectedUserIndex)
	{
		if(isSelected)
		{
			g.setColor(ScanpathViewer.COLOR_BACKGROUND_SELECTION);
			g.fillRect(0, 0, imageDimension.width, userList.size()*ScanpathViewer.TIME_CELL_HEIGHT);			
		}
		
		DataObject dataObject = this.dataObject;
		
		Util.drawTextBox(g, Color.black, dataObject.getLabel(), new Rectangle(0, 0, MultiScanpath.WIDTH_TITLE, userList.size()*ScanpathViewer.TIME_CELL_HEIGHT/2));
		
		
		
		
		g.translate(MultiScanpath.WIDTH_TITLE, 0);
		
		for(int userIndex=0;userIndex<userList.size();userIndex++)
		{
			String name = userList.get(userIndex);
			int lineY = userIndex* ScanpathViewer.TIME_CELL_HEIGHT;
			g.setColor(horizontalLineColor);
			g.drawLine(MultiScanpath.WIDTH_ANCHOR,lineY, imageDimension.width- MultiScanpath.WIDTH_TITLE , lineY);
			
			int textY = userIndex* ScanpathViewer.TIME_CELL_HEIGHT-ScanpathViewer.TIME_CELL_HEIGHT/2;
			Rectangle rect = new Rectangle(0,textY,MultiScanpath.WIDTH_ANCHOR,ScanpathViewer.TIME_CELL_HEIGHT);
			
			Color textBackColor = ColorScheme.ALTERNATE_COLOR_BLUE[userIndex%2];
			g.setColor(textBackColor);
			g.fillRect(rect.x, rect.y, rect.width, rect.height);
			if(userIndex == selectedUserIndex)
			{
				g.setColor(ScanpathViewer.COLOR_SELECTION);
				g.drawRect(rect.x, rect.y, rect.width, rect.height);
			}
			
			Util.drawTextBox(g, Color.black, " "+name, rect);
			
		}
		g.translate(-MultiScanpath.WIDTH_TITLE, 0);
	}
}
