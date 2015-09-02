package doiriver;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import multifaceted.FileLineReader;
import multifaceted.Util;
import perspectives.properties.PString;
import perspectives.tree.Tree;
import perspectives.tree.TreeNode;
import perspectives.util.DistancedPoints;
import perspectives.util.HierarchicalClustering;
import realtime.DataObject;
import realtime.EyeEvent;
import scanpath.MultiScanpathDiagram;
import scanpath.ScanpathViewer;

public class DOIRiverDiagram {
	private String directoryPath;
	
	private ArrayList<String> userList = new ArrayList<String>();
	private ArrayList<ArrayList<EyeEvent>> eyeEventList = new ArrayList<ArrayList<EyeEvent>>();
	private ArrayList<HashMap<String, DataObject>> dataObjectList = new ArrayList<HashMap<String, DataObject>>();
	private long startTime =0;
	
	
	private ArrayList<DataObject> riverObjectList = new ArrayList<DataObject>();
	ArrayList<DOIRiver> riverList = new ArrayList<DOIRiver>();
	private DataObject[][] riverObjectPointArray;
	
	public DOIRiverDiagram(String directoryPath) {
		this.directoryPath = directoryPath;		
		process();
		computeDistance();
		computeRiverCurverPoints();
	}
	
	private ArrayList<Color> colorList = new ArrayList<Color>();
	private void computeRiverCurverPoints()
	{
		int maxTimeIndex =Integer.MIN_VALUE;
		for(int userIndex=0;userIndex< riverObjectPointArray.length;userIndex++)		
		{
			if(riverObjectPointArray[userIndex].length > maxTimeIndex)
			{
				maxTimeIndex = riverObjectPointArray[userIndex].length;
			}
			DOIRiver previousRiver = null;
			for(int timeIndex =0;timeIndex< riverObjectPointArray[userIndex].length;timeIndex++)
			{
				DataObject object = riverObjectPointArray[userIndex][timeIndex];
				if(object != null)
				{
					int objectIndex = riverObjectList.indexOf(object);
					DOIRiver river = new DOIRiver(object, objectIndex, timeIndex);
					int index = riverList.indexOf(river);
					if(index <0)
					{
						riverList.add(river);
						
					}
					else 
					{
						river = riverList.get(index);
						river.increaseHeight(timeIndex);					
					}
					
					if(previousRiver != null && !previousRiver.equals(river))
					{	
						previousRiver.addDestinationRiver(timeIndex, river);
						river.addSourceRiver(timeIndex, previousRiver);
					}
					previousRiver = river;
				}
				
			}
		}
		
		Random rand = new Random();
		for(int i=0;i< riverObjectList.size();i++)
		{
			float r = rand.nextFloat() / 2.0f + 0.5f;
			float g = rand.nextFloat() / 2.0f + 0.5f;
			float b = rand.nextFloat() / 2.0f + 0.5f;
			Color randomColor = new Color(r, g, b);
			while(colorList.contains(randomColor))
			{
				r = rand.nextFloat() / 2.0f + 0.5f;
				g = rand.nextFloat() / 2.0f + 0.5f;
				b = rand.nextFloat() / 2.0f + 0.5f;
				randomColor = new Color(r, g, b);
			}
			
			colorList.add(Util.getAlphaColor(randomColor, 220));
		}
		int heightPerObject = userList.size()* DOIRiverViewer.TIME_CELL_HEIGHT+DOIRiverViewer.DIAGRAM_GAP;
		
		for(DOIRiver river: riverList)
		{
			river.prepareRender(maxTimeIndex, heightPerObject, colorList.get(river.index));
		}
		
			
		
	}
	
	private void process()
	{
		File dir = new File(directoryPath);
		
		File[] sequenceFiles = dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File file) {
				// TODO Auto-generated method stub
				
				return file.getName().toUpperCase().endsWith(".TXT");
			}
		});
		
		FileLineReader fileLineReader = new FileLineReader() {
			
			@Override
			public void readLine(String fileLine, File currentFile) {
				// TODO Auto-generated method stub
				
				int index = getUserIndex(currentFile);
				
				ArrayList<EyeEvent> eyeEvents = eyeEventList.get(index);
				HashMap<String, DataObject> dataObjects = dataObjectList.get(index);
				
				String[] split = fileLine.split("\t");
				if (split[0].equals("Eye") && !split[4].trim().equals("5") ){
	        		
	        		long t = Long.parseLong(split[1]) ;
	        		double s = Double.parseDouble(split[5]);
	        		double p = Double.parseDouble(split[8]);
	        		
	        		String objId = split[2];
	        		DataObject object = new DataObject(objId, split[3], Integer.parseInt(split[4].trim()));
	        		if(dataObjects.containsKey(objId))
	        		{
	        			object = dataObjects.get(objId);
	        		}
	        		else
	        		{
	        			dataObjects.put(objId, object);
	        		}
	        		
	        		
	        		
	        		if(eyeEvents.isEmpty())
	        		{
	        			startTime = t;
	        		}
	        		EyeEvent e = new EyeEvent(t-startTime,object, s, p);
	        		eyeEvents.add(e);
	        	}
			}

			
		};
		
		riverObjectPointArray = new DataObject[sequenceFiles.length][];
		int index =0;
		for(File file: sequenceFiles)
		{
			String fileName = file.getName();
			int extensionIndex = fileName.toUpperCase().lastIndexOf(".TXT");
			String user = fileName.substring(0, extensionIndex);
			this.userList.add(user);
			
			ArrayList<EyeEvent> eyeEvents = new ArrayList<EyeEvent>();
			this.eyeEventList.add(eyeEvents);
			
			HashMap<String, DataObject> dataObjects = new HashMap<String, DataObject>();
			this.dataObjectList.add(dataObjects);
			
			
			fileLineReader.read(file.getAbsolutePath());
			
			riverObjectPointArray[index] = getRiverObject(index);
			index++;
		}
	}

	
	public DataObject[] getRiverObject(int userIndex)
	{
		ArrayList<EyeEvent> eyeEvents = eyeEventList.get(userIndex);
		
		int totalCells =(int)(eyeEvents.get(eyeEvents.size()-1).getTime()/ DOIRiverViewer.TIME_STEP)+1;
		DataObject[] scanpathPoints = new DataObject[totalCells];
		ArrayList<DataObject> dataObjectCollection = new ArrayList<DataObject>();
		int lastIndex =0;
		for(EyeEvent eye: eyeEvents)
		{
			
			int index = (int)(eye.getTime()/DOIRiverViewer.TIME_STEP);
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
				if(bestDataObject != null && !this.riverObjectList.contains(bestDataObject))
				{
					this.riverObjectList.add(bestDataObject);
				}
				
				
				lastIndex = index;
				dataObjectCollection.clear();
			}
		}
		
		return scanpathPoints;
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
	
	
	private int getUserIndex(File file)
	{
		String fileName = file.getName();
		int extensionIndex = fileName.toUpperCase().lastIndexOf(".TXT");
		String user = fileName.substring(0, extensionIndex);
		
		if(this.userList != null && !this.userList.isEmpty() && this.userList.contains(user))
		{
			return this.userList.indexOf(user);
		}
		else
		{
			return DOIRiverViewer.INVALID;
		}
			
	}

	public String getDirectoryPath() {
		return directoryPath;
	}

	public void setDirectoryPath(String directoryPath) {
		this.directoryPath = directoryPath;
	}

	


	
	public void render(Graphics2D g, Color objectColor, Color transitionLineColor, double heightRatio)
	{
		if(this.riverObjectList != null && !this.riverObjectList.isEmpty())
		{
			int heightPerObject = this.userList.size()*DOIRiverViewer.TIME_CELL_HEIGHT+DOIRiverViewer.DIAGRAM_GAP;
			//outline 
			for(int objectIndex=0;objectIndex<riverObjectList.size();objectIndex++)
			{
				DataObject object =riverObjectList.get(objectIndex); 

				
				
				int titleY = heightPerObject*objectIndex;
				g.translate(0,  titleY);
				
				Color objectBackColor = colorList.get(objectIndex);
				g.setColor(objectBackColor);
				g.fillRect(0, 0,DOIRiverViewer.WIDTH_TITLE, userList.size()*DOIRiverViewer.TIME_CELL_HEIGHT);
				
				Util.drawTextBox(g, Color.black, object.getLabel(), new Rectangle(0, 0, DOIRiverViewer.WIDTH_TITLE, userList.size()*DOIRiverViewer.TIME_CELL_HEIGHT/2));
				
				

				
				g.translate(0, -titleY);
				
			}
			
			g.translate(DOIRiverViewer.WIDTH_TITLE, 0);
			
			
			for(DOIRiver river: riverList)
			{
				g.setColor(river.color);
				g.fillPolygon(river.curveXPoints, river.curveYPoints, river.curveXPoints.length);
			}
			g.translate(-DOIRiverViewer.WIDTH_TITLE, 0);
			
			
			
			
			///*
			//Rendering objects and transitions
			Dimension imageDimension = getImageDimension();
			g.translate(imageDimension.width- DOIRiverViewer.WIDTH_TITLE, 0);
			for(int userIndex=0;userIndex<riverObjectPointArray.length;userIndex++)
			{
				Point lastPoint = null;
				for(int time=0;time<riverObjectPointArray[userIndex].length;time++)
				{
					DataObject object = riverObjectPointArray[userIndex][time];
					if(object != null)
					{
						int index = riverObjectList.indexOf(object);
						
						int x = time*ScanpathViewer.TIME_CELL_WIDTH+DOIRiverViewer.WIDTH_TITLE;
						int y = index* heightPerObject+userIndex*ScanpathViewer.TIME_CELL_HEIGHT;
						
						if(lastPoint != null)
						{							
							g.setColor(transitionLineColor);
							
							g.drawLine(lastPoint.x+ScanpathViewer.TIME_CELL_WIDTH, lastPoint.y, x, y);
						}
						lastPoint = new Point(x,y);
						g.setColor(objectColor);
						
						int cellHeight =(int) (ScanpathViewer.TIME_CELL_HEIGHT* heightRatio);
						g.fillRect(x, y-cellHeight/2, ScanpathViewer.TIME_CELL_WIDTH,cellHeight);
					}
					else if(lastPoint != null)
					{
						int x = time*ScanpathViewer.TIME_CELL_WIDTH+DOIRiverViewer.WIDTH_TITLE;
						int y = lastPoint.y;
						g.setColor(transitionLineColor);
					
						g.drawLine(lastPoint.x, lastPoint.y, x+ScanpathViewer.TIME_CELL_WIDTH, y);
						lastPoint = new Point(x,y);
					}
				}
			}
			g.translate(-imageDimension.width+ DOIRiverViewer.WIDTH_TITLE, 0);
			//*/
		}
		
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
				for(int i=0;i<riverObjectList.size();i++)
				{
					DataObject object =riverObjectList.get(i);
					String objectId = object.getId()+"-"+object.getType();
					if(objectId.equalsIgnoreCase(id))
					{
						return i;
					}
				}
				return DOIRiverViewer.INVALID;
			}
			
			@Override
			public String getPointId(int index) {
				// TODO Auto-generated method stub
				DataObject object =riverObjectList.get(index);
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
				return riverObjectList.size();
			}
		};
		private int transition[][];
		private float distance[][];
		
		public void computeDistance()
		{
			transition = new int [riverObjectList.size()][riverObjectList.size()];
			
			
			for(int userIndex=0;userIndex<riverObjectPointArray.length;userIndex++)
			{
				int sourceIndex =DOIRiverViewer.INVALID;
				for(int time=0;time<riverObjectPointArray[userIndex].length;time++)
				{
					DataObject object = riverObjectPointArray[userIndex][time];
					int destinationIndex = this.riverObjectList.indexOf(object);
					
					if(sourceIndex != destinationIndex && 
						sourceIndex > DOIRiverViewer.INVALID && 
						destinationIndex > DOIRiverViewer.INVALID)
					{
						transition[sourceIndex][destinationIndex]++;
						transition[destinationIndex][sourceIndex]++;
					}
					
					sourceIndex = destinationIndex;
				}
			}
			distance = new float [riverObjectList.size()][riverObjectList.size()];
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
						distance[i][j] = DOIRiverViewer.INFINITY;
					}
				}
			}

			
			Tree t = HierarchicalClustering.compute(this.clusteringPoints);
			traverseClusterNode(t.getRoot());
//			System.out.println(diagramList.size()+", "+clusteredSequence.size()+", "+clusteringPoints.getCount());
			riverObjectList = clusteredSequence;
	
			
		}
		private ArrayList<DataObject> clusteredSequence= new ArrayList<DataObject>(); 
		
		private void traverseClusterNode(TreeNode node)
		{
//			System.out.println("<Node>");
			
			if(node.isLeaf())
			{
				PString pId =(PString) node.getProperty("Id").getValue();
				int index = clusteringPoints.getPointIndex(pId.stringValue());
				if(index > DOIRiverViewer.INVALID)
				{
					DataObject object = riverObjectList.get(index);
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
					if(index > DOIRiverViewer.INVALID)
					{
						DataObject object = riverObjectList.get(index);
//						System.out.println("<Id>#"+diagram.dataObject.getLabel()+"</Id>");
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
		public Dimension getImageDimension()
		{
			int maxColumnCount =Integer.MIN_VALUE;
			for(int i=0;i<riverObjectPointArray.length;i++)
			{
				if(riverObjectPointArray[i].length > maxColumnCount)
				{
					maxColumnCount = riverObjectPointArray[i].length;
				}
			}
			int width = DOIRiverViewer.WIDTH_TITLE+maxColumnCount*DOIRiverViewer.TIME_CELL_WIDTH;
			int height = riverList.size()*(DOIRiverViewer.DIAGRAM_GAP+userList.size()*DOIRiverViewer.TIME_CELL_HEIGHT);
			
			Dimension imageDimension = new Dimension(width, height);
			
			return imageDimension;
		}
}
