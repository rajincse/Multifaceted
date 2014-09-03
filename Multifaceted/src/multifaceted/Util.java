package multifaceted;

import java.awt.Color;
import java.awt.Graphics2D;

public class Util {
	public static void drawCircle(int x, int y, Color c, Graphics2D g)
	{
		Color previousColor = g.getColor();
		g.setColor(c);
		int rad = 2;
		g.fillOval(x-rad, y-rad, 2*rad, 2*rad);
		g.setColor(previousColor);
	}
}
