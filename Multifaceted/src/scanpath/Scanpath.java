package scanpath;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import perspectives.properties.PString;
import perspectives.tree.Tree;
import perspectives.tree.TreeNode;
import perspectives.util.DistancedPoints;
import perspectives.util.HierarchicalClustering;

import realtime.DataObject;
import realtime.EyeEvent;

import multifaceted.ColorScheme;
import multifaceted.FileLineReader;
import multifaceted.Util;

public class Scanpath {
	
	
	private String filepath;
	private ArrayList<EyeEvent> eyeEventList = new ArrayList<EyeEvent>();
	private HashMap<String, DataObject> dataObjectList = new HashMap<String, DataObject>();
	private long startTime =0;
	
	private boolean isSelected=false;
	private DataObject selectedObject =null;
	public Scanpath(String filepath)
	{
		this.filepath = filepath;
		FileLineReader fileLineReader = new FileLineReader() {
			
			@Override
			public void readLine(String fileLine, File currentFile) {
				// TODO Auto-generated method stub
				String[] split = fileLine.split("\t");
				if (split[0].equals("Eye") && !split[4].trim().equals("5") ){
	        		
	        		long t = Long.parseLong(split[1]) ;
	        		double s = Double.parseDouble(split[5]);
	        		double p = Double.parseDouble(split[8]);
	        		
	        		String objId = split[2];
	        		DataObject object = new DataObject(objId, split[3], Integer.parseInt(split[4].trim()));
	        		if(dataObjectList.containsKey(objId))
	        		{
	        			object = dataObjectList.get(objId);
	        		}
	        		else
	        		{
	        			dataObjectList.put(objId, object);
	        		}
	        		
	        		
	        		
	        		if(eyeEventList.isEmpty())
	        		{
	        			startTime = t;
	        		}
	        		EyeEvent e = new EyeEvent(t-startTime,object, s, p);
	        		eyeEventList.add(e);
	        	}
			}

			
		};
		fileLineReader.read(this.filepath);
		prepareRender();
		computeDistance();
		
	}
	
	// Clustering Start
	
		DistancedPoints clusteringPoints = new DistancedPoints() {
			
			@Override
			public void normalize() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public int getPointIndex(String id) {
				// TODO Auto-generated method stub
				for(int i=0;i<renderingObjectList.size();i++)
				{
					DataObject object =renderingObjectList.get(i); 
					String objectId = object.getId()+"-"+object.getType();
					if(objectId.equalsIgnoreCase(id))
					{
						return i;
					}
				}
				return ScanpathViewer.INVALID;
			}
			
			@Override
			public String getPointId(int index) {
				// TODO Auto-generated method stub
				DataObject object =renderingObjectList.get(index); 
				return object.getId()+"-"+object.getType();
			}
			
			@Override
			public float getDistance(int index1, int index2) {
				// TODO Auto-generated method stub
				return distance[index1][index2];
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return renderingObjectList.size();
			}
		};
		private int transition[][];
		private float distance[][];
		
		public void computeDistance()
		{
			transition = new int [renderingObjectList.size()][renderingObjectList.size()];
			
			
			
			int sourceIndex =ScanpathViewer.INVALID;
			for(int time=0;time<scanpathPoints.length;time++)
			{
				DataObject object = scanpathPoints[time];
				int destinationIndex = this.renderingObjectList.indexOf(object);
				
				if(sourceIndex != destinationIndex && 
					sourceIndex > ScanpathViewer.INVALID && 
					destinationIndex > ScanpathViewer.INVALID)
				{
					transition[sourceIndex][destinationIndex]++;
					transition[destinationIndex][sourceIndex]++;
				}
				
				sourceIndex = destinationIndex;
			}
			
			distance = new float [renderingObjectList.size()][renderingObjectList.size()];
			for(int i=0;i<transition.length;i++)
			{
				for(int j=0;j<transition[i].length;j++)
				{
					if(transition[i][j]> 0)
					{
						distance[i][j] = 1.0f / transition[i][j];
					}
					else
					{
						distance[i][j] = ScanpathViewer.INFINITY;
					}
				}
			}

			
			Tree t = HierarchicalClustering.compute(this.clusteringPoints);
			traverseClusterNode(t.getRoot());
//			System.out.println(renderingObjectList.size()+", "+clusteredSequence.size()+", "+clusteringPoints.getCount());
			renderingObjectList = clusteredSequence;
			

			
		}
		private ArrayList<DataObject> clusteredSequence= new ArrayList<DataObject>(); 
		
		private void traverseClusterNode(TreeNode node)
		{
//			System.out.println("<Node>");
			
			if(node.isLeaf())
			{
				PString pId =(PString) node.getProperty("Id").getValue();
				int index = clusteringPoints.getPointIndex(pId.stringValue());
				if(index > ScanpathViewer.INVALID)
				{
					DataObject object = renderingObjectList.get(index);
//					System.out.println("<Id>"+diagram.dataObject.getLabel()+"</Id>");
					clusteredSequence.add(0,object);
				}
				else
				{
//					System.out.println("Invalid Id:"+pId.stringValue());
				}
				
				
			}
			else
			{
				
				if(node.getProperty("Id") != null)
				{
					PString pId =(PString) node.getProperty("Id").getValue();
					
					int index = clusteringPoints.getPointIndex(pId.stringValue());
					if(index > ScanpathViewer.INVALID)
					{
						DataObject object = renderingObjectList.get(index);
//						System.out.println("<Id>"+diagram.dataObject.getLabel()+"</Id>");
						clusteredSequence.add(0,object);
					}
					else
					{
//						System.out.println("Invalid Id:"+pId.stringValue());
					}
				}
				else
				{
//					System.out.println("<Id>Intermediate</Id>");
				}
				
//				System.out.println("<Children>");
				for(TreeNode child: node.getChildren())
				{
					traverseClusterNode(child);
				}
//				System.out.println("</Children>");
				
			}
//			System.out.println("</Node>");
		}
		// Clustering End
	
	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	private DataObject[] scanpathPoints;
	private ArrayList<DataObject> renderingObjectList = new ArrayList<DataObject>();
	private double maxScore=Double.MIN_VALUE;
	
	public int getRowCount()
	{
		return this.renderingObjectList.size();
	}
	public void prepareRender()
	{
		int totalCells =(int)(eyeEventList.get(eyeEventList.size()-1).getTime()/ ScanpathViewer.TIME_STEP)+1;
		scanpathPoints = new DataObject[totalCells];
		ArrayList<DataObject> dataObjectCollection = new ArrayList<DataObject>();
		int lastIndex =0;
		for(EyeEvent eye: eyeEventList)
		{
			
			int index = (int)(eye.getTime()/ScanpathViewer.TIME_STEP);
			if(index == lastIndex)
			{
				DataObject object = eye.getTarget();
				if(dataObjectCollection.contains(object))
				{
					DataObject savedObject = dataObjectCollection.get(dataObjectCollection.indexOf(object));
					savedObject.setSortingScore(savedObject.getSortingScore()+eye.getScore());
				}
				else
				{
					object.setSortingScore(eye.getScore());
					dataObjectCollection.add(eye.getTarget());
				}
				
			}
			else
			{
				DataObject bestDataObject = getBestDataObject(dataObjectCollection);
				scanpathPoints[lastIndex] = bestDataObject;
				if(bestDataObject != null && !this.renderingObjectList.contains(bestDataObject))
				{
					this.renderingObjectList.add(bestDataObject);
				}
				
				if(bestDataObject != null && bestDataObject.getSortingScore() > maxScore)
				{
					maxScore = bestDataObject.getSortingScore();
				}
				
				lastIndex = index;
				dataObjectCollection.clear();
			}
		}
	}
	
	public DataObject getBestDataObject(ArrayList<DataObject> dataObjectCollection)
	{
		if(dataObjectCollection != null && !dataObjectCollection.isEmpty())
		{
			Collections.sort(dataObjectCollection);
			
			return dataObjectCollection.get(0);
		}
		else
		{
			return null;
		}
	}
	public static final int WIDTH_TITLE = 100;
	public static final int WIDTH_ANCHOR = 50;
	
	public void render(Graphics2D g, Color objectColor, Color horizontalLineColor, Color transitionLineColor, double heightRatio)
	{
		if(isSelected)
		{
			Dimension d = getImageDimension();
			
			g.setColor(ScanpathViewer.COLOR_BACKGROUND_SELECTION);
			g.fillRect(0, 0, d.width, d.height);
		}
		
		String fileName = new File(this.filepath).getName();
		int extensionIndex = fileName.toUpperCase().lastIndexOf(".TXT");
		fileName = fileName.substring(0, extensionIndex);
		Util.drawTextBox(g,Color.black, fileName, new Rectangle(0,renderingObjectList.size()*ScanpathViewer.TIME_CELL_HEIGHT/2,WIDTH_TITLE,renderingObjectList.size()*ScanpathViewer.TIME_CELL_HEIGHT/8));
		
		g.translate(WIDTH_TITLE, 0);
		
		// Rendering Anchors
		
		for(int i=0;i<renderingObjectList.size();i++)
		{
			DataObject object = renderingObjectList.get(i); 
			String name = object.getLabel();
			int lineY = i* ScanpathViewer.TIME_CELL_HEIGHT;
			g.setColor(horizontalLineColor);
			g.drawLine(WIDTH_ANCHOR,lineY, WIDTH_ANCHOR+scanpathPoints.length*ScanpathViewer.TIME_CELL_WIDTH, lineY);
			
			int textY = i* ScanpathViewer.TIME_CELL_HEIGHT-ScanpathViewer.TIME_CELL_HEIGHT/2;
			Rectangle rect = new Rectangle(0,textY,WIDTH_ANCHOR,ScanpathViewer.TIME_CELL_HEIGHT);
			Color textBackColor = ColorScheme.ALTERNATE_COLOR_BLUE[i%2];
			g.setColor(textBackColor);
			g.fillRect(rect.x, rect.y, rect.width, rect.height);
			if(object.equals(selectedObject))
			{
				g.setColor(ScanpathViewer.COLOR_SELECTION);
				g.drawRect(rect.x, rect.y, rect.width, rect.height);
			}
			
			Util.drawTextBox(g, Color.black, name, rect);
		}
		
		//Rendering objects and transitions
		g.translate(WIDTH_ANCHOR, 0);
		Point lastPoint = null;
		for(int i=0;i <scanpathPoints.length;i++)
		{
			DataObject object = scanpathPoints[i];
			if(object != null)
			{
				int index = renderingObjectList.indexOf(object);
				int x = i*ScanpathViewer.TIME_CELL_WIDTH;
				int y = index* ScanpathViewer.TIME_CELL_HEIGHT;
				if(lastPoint != null)
				{
					g.setColor(transitionLineColor);
					g.drawLine(lastPoint.x+ScanpathViewer.TIME_CELL_WIDTH, lastPoint.y, x, y);
				}
				lastPoint = new Point(x,y);
				g.setColor(objectColor);
				if(object.equals(selectedObject))
				{
					g.setColor(ScanpathViewer.COLOR_SELECTION);
				}
				
				int cellHeight =(int) (ScanpathViewer.TIME_CELL_HEIGHT* heightRatio);
				g.fillRect(x, y-cellHeight/2, ScanpathViewer.TIME_CELL_WIDTH,cellHeight);
			}
			else if(lastPoint != null)
			{
				int x = i*ScanpathViewer.TIME_CELL_WIDTH;
				int y = lastPoint.y;
				g.setColor(transitionLineColor);
				g.drawLine(lastPoint.x, lastPoint.y, x+ScanpathViewer.TIME_CELL_WIDTH, y);
				lastPoint = new Point(x,y);
			}
		}
		
		g.translate(-WIDTH_ANCHOR, 0);
		
		
		
		g.translate(-WIDTH_TITLE, 0);
		
		
	}
	
	public Dimension getImageDimension()
	{
		int width =WIDTH_ANCHOR+WIDTH_TITLE+scanpathPoints.length*ScanpathViewer.TIME_CELL_WIDTH;
		int height = renderingObjectList.size()*ScanpathViewer.TIME_CELL_HEIGHT;
		
		Dimension imageDimension = new Dimension(width, height);
		
		return imageDimension;
	}
	
	public static Dimension getImageDimension(ArrayList<Scanpath> scanpathList)
	{
		int width =0;
		int height = 0;
		for(Scanpath scanpath:scanpathList)
		{
			Dimension dimension = scanpath.getImageDimension();
			if(dimension.width > width)
			{
				width = dimension.width;
			}
			height+= dimension.getHeight()+ScanpathViewer.SCANPATH_DIAGRAM_GAP;
		}
		
		Dimension imageDimension = new Dimension(width, height);
		
		return imageDimension;
	}
	
	public DataObject getSelectedObject() {
		return selectedObject;
	}

	public void setSelectedObject(DataObject selectedObject) {
		this.selectedObject = selectedObject;
	}

	public DataObject getSelectedObject(int x, int y)
	{
		Dimension imageDimension = getImageDimension();
		if(x >= WIDTH_TITLE && x <= imageDimension.width && y >= 0 && y<= imageDimension.height)
		{
			
			int objectIndex = y / ScanpathViewer.TIME_CELL_HEIGHT;
			if(objectIndex >= 0 && objectIndex < renderingObjectList.size())
			{
				selectedObject = renderingObjectList.get(objectIndex);
				return selectedObject;
			}
		}
		return selectedObject;
	}
}
