package pivotpath;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import imdb.IMDBDataSource;
import multifaceted.Util;

public class PivotPathImageGenerator extends PivotPathViewer {

	public PivotPathImageGenerator(String name, IMDBDataSource data) {
		super(name, data);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		super.render(g);

		if (this.pivotPaths.groups != null && this.pivotPaths.dataGroups != null) {
			int alpha =80;
			Color fill = Util.getAlphaColor(Color.red, alpha);
			Color border = new Color(99,37,35);
			
			ArrayList<InfoBitGroup> groups = this.pivotPaths.groups;
			ArrayList<InfoBitGroup> dataGroups = this.pivotPaths.dataGroups;

			for (InfoBitGroup group : groups) {
				for (InfoBit infoBit : group.items) {
					int x = (int) group.getItemX(infoBit);
					int y = (int) group.getItemY(infoBit);

					int w = (int) infoBit.getWidth();
					int h = (int) infoBit.getHeight();

					g.setColor(border);
					g.drawRect(x, y, w, h);
					g.setColor(fill);
					g.fillRect(x, y, w, h);
				}
			}
			for (InfoBitGroup group : dataGroups) {
				System.out.println("Group : ("+group.x+", "+group.y+")");
				for (InfoBit infoBit : group.items) {
					int x = (int) group.getItemX(infoBit);
					int y = (int) group.getItemY(infoBit);
					System.out.println(infoBit+"=>("+x+","+y+")");
					
					int w = (int) infoBit.getWidth();
					int h = (int) infoBit.getHeight();
					double r = MainInfoBitGroup.TILT_ANGLE;
					g.translate(group.x, group.y);	
					g.rotate(r);
					
					double yDiff = y - group.y;
					double xDiff = x -group.x;	
					
					g.translate(xDiff, yDiff);
					
					g.setColor(border);
					g.drawRect(0, 0, w, h);
					
					g.setColor(fill);
					g.fillRect(0, 0, w, h);
					
					g.translate(-xDiff, -yDiff);
					g.rotate(-r);
					g.translate(-group.x, -group.y);
					
				}
			}
		}

	}
}
