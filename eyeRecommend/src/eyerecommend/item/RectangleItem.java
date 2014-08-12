package eyerecommend.item;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

import eyerecommend.utility.Util;

public class RectangleItem implements RecommendItem{
	
	protected int index;
	protected Rectangle rect;
	public RectangleItem(int index, int x, int y, int width, int height)
	{
		this(index, new Rectangle(x, y, width, height));
	}
	public RectangleItem(int index, Rectangle rect)
	{
		this.index = index;
		this.rect = rect;
	}

	@Override
	public int getIndex() {
		// TODO Auto-generated method stub
		return this.index;
	}

	public Rectangle getRect() {
		return rect;
	}
	@Override
	public double getPosteriorProbability(Point2D gazePosition) {
		// TODO Auto-generated method stub
		return Util.gaussianDistribution(gazePosition.getX(), this.rect.getCenterX(), this.rect.getWidth()/2)
				*
				Util.gaussianDistribution(gazePosition.getY(), this.rect.getCenterY(), this.rect.getHeight()/2);
	}

	@Override
	public double getPriorProbability() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{index:"+this.index+"}";
	}
}
