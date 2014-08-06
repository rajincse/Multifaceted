package eyerecommend.main;

import java.util.concurrent.PriorityBlockingQueue;

import eyerecommend.item.RectangleItem;

public class MainClass {
	public static void main(String[] args)
	{
		RectangleItem item1 = new RectangleItem(0, 10);
		RectangleItem item2 = new RectangleItem(1, 20);
		RectangleItem item3 = new RectangleItem(2, 5);
		
		PriorityBlockingQueue<RectangleItem> queue = new PriorityBlockingQueue<RectangleItem>();
		queue.add(item1);
		queue.add(item2);
		queue.add(item3);
		
		RectangleItem lowest= queue.peek();
		System.out.println(lowest);
		
	}
}
