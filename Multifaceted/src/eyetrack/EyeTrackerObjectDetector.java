package eyetrack;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;


public class EyeTrackerObjectDetector implements EyeTrackerDataReceiver{
	
	Point[][] curves;
	Point[] points;
	
	private Point[][] curveControl;
	private double[][] curveControlScore;	
	private double[] curveScoresShort;
	private double[] curveScoresLong;
	private double[] curveLengths;
	
	private double[] pointControlScore;
	
	HashMap<String,Integer> curveIds;
	HashMap<String, Integer> pointIds;
	
	private Point offset;
	
	private boolean blocked = false;
	
	
	////params
	double curveSegmentDecay = 0.05;
	double curveSegmentInc = 0.1;
	double curveSegThreshold = 75;
	double maxCurveLength = 500;
	
	double curveShortInc = 10.; // s/curveShortInc get added to curveShort
	double curveShortDec = 0.004;
	
	double curveLongInc = 1600.; // curveShort/curveShortInc get added to curveShort
	double curveLongDec = 0.0005;
	
	EyeTrackServer g;
	
	
	
	/////debug info
	double[][] debugBarsL;
	double[][] debugBarsR;
	
	double[] debugCompBars;
	double[] debugD1;
	double[] debugD2;
	
	Point debugGazeLocation = new Point();
	
	
	public EyeTrackerObjectDetector()
	{
		curves = new Point[0][];
		points = new Point[0];
		
		curveControl = new Point[0][];
		curveControlScore = new double[0][];
		pointControlScore = new double[0];
		
		curveScoresShort = new double[0];
		curveScoresLong = new double[0];
		
		curveLengths = new double[0];
		
		curveIds = new HashMap<String,Integer>();
		pointIds = new HashMap<String,Integer>();
		
		offset = new Point(0,0);
		
		g = new EyeTrackServer(this);
		
		debugBarsL = new double[0][];
		debugBarsR = new double[0][];
		debugCompBars = new double[0];
		debugD1 = new double[0];
		debugD2 = new double[0];

		
	}
	
	public void setOffset(Point p)
	{
		offset = p;
	}
	
	public void block(boolean block)
	{
		synchronized(this)
		{
			this.blocked = block;
		}		
	}
	@Override
	public void processGaze(Point gazePoint, double pupilDiameter) {
		// TODO Auto-generated method stub
		this.processGaze(gazePoint);
	}
	public void processGaze(Point gaze)
	{
		synchronized(this)
		{
			if (blocked) return;
		}
		int scx = (int)this.offset.getX();
		int scy = (int)this.offset.getY();
		
		
		Point processingPoint = new Point((int)gaze.getX()-scx,(int)gaze.getY()-scy);
		
		gaze = processingPoint;
		
		debugGazeLocation = gaze;
		
		for (int i=0; i<curves.length; i++)
			if (curves[i] != null)
				processCurveGaze(i,gaze);
		
		for (int i=0; i<points.length; i++)
			if (points[i] != null)
				processPointGaze(i,gaze);
				
	}
	
	public double getCurveScore(String id)
	{
		int index = curveIds.get(id);
		return this.curveScoresLong[index];
	}
	
	
	private void processPointGaze(int curve, Point gaze)
	{
		
	}
		
	public void registerCurve(String id, Point[] curve)
	{
		Point[][] curves2 = new Point[curves.length+1][];
		Point[][] curveControl2 = new Point[curveControl.length+1][];
		double[][] curveControlScore2 = new double[curveControlScore.length+1][];
		double[] curveScoresShort2 = new double[curveScoresShort.length+1];
		double[] curveScoresLong2 = new double[curveScoresLong.length+1];
		double[] curveLengths2 = new double[this.curveLengths.length+1];
		
		double[][] debugBarsL2 = new double[debugBarsL.length+1][];
		double[][] debugBarsR2 = new double[debugBarsR.length+1][];
		double[] debugCompBars2 = new double[debugCompBars.length+1];
		double[] debugD12 = new double[debugD1.length+1];
		double[] debugD22 = new double[debugD2.length+1];
		
		for (int i=0; i<curves.length; i++)
		{
			curves2[i] = curves[i];
			curveControl2[i] = curveControl[i];
			curveControlScore2[i] = curveControlScore[i]; 
			curveScoresShort2[i] = curveScoresShort[i];
			curveScoresLong2[i] = curveScoresLong[i];
			curveLengths2[i] = curveLengths[i];
			
			debugBarsL2[i] = debugBarsL[i];
			debugBarsR2[i] = debugBarsR[i];
			debugCompBars2[i] = debugCompBars[i];
			debugD12[i] = debugD1[i];
			debugD22[i] = debugD2[i];
		}
		
		curves2[curves.length] = curve;
		curveScoresShort2[curveScoresShort.length] = 0;
		curveScoresLong2[curveScoresLong.length] = 0;
		
		
		Point[] cc = segmentCurve(curve);
		double[] ccs = new double[cc.length];
		for (int i=0; i<ccs.length; i++)
			ccs[i] = 0;
		
		curveControl2[curveControl.length] = cc;
		curveControlScore2[curveControlScore.length] = ccs;
		
		
		curves = curves2;
		curveScoresShort = curveScoresShort2;
		curveScoresLong = curveScoresLong2;
		curveControl = curveControl2;
		curveControlScore = curveControlScore2;
		curveLengths = curveLengths2;
		curveIds.put(id, curves.length-1);
		
		debugBarsL = debugBarsL2;
		debugBarsR = debugBarsR2;
		debugCompBars = debugCompBars2;
		debugD1 = debugD12;
		debugD2 = debugD22;
		
		double length = 0;		
		for (int i=0; i<cc.length-1; i++)
			length += cc[i].distance(cc[i+1]);
		curveLengths[curveLengths.length-1] = length;
	}
	
	public void deleteCurve(String id)
	{
		int index = curveIds.get(id);
		curves[index] = null;
	}
	
	public Point[][] getCurveControls()
	{
		return curveControl;
	}
	public double[][] getCurveControlScores()
	{
		return curveControlScore;
	}
	
	public void reshapeCurve(String id, Point[] curve)
	{		
		int index = curveIds.get(id);
		curves[index] = curve;
		
		Point[] cc = segmentCurve(curve);
		double[] ccs = new double[cc.length];
		for (int i=0; i<ccs.length; i++)
			ccs[i] = 0;
		
		curveControl[index] = cc;
		curveControlScore[index] = ccs;
		
		double length = 0;		
		for (int i=0; i<cc.length-1; i++)
			length += cc[i].distance(cc[i+1]);
		curveLengths[index] = length;
	}
	
	public void registerPoint(String id, Point p)
	{
		Point[] points2 = new Point[points.length+1];
		double[] pointControlScore2 = new double[pointControlScore.length+1];
		
		for (int i=0; i<points.length; i++)
		{
			points2[i] = points[i];
			pointControlScore2[i] = pointControlScore[i];
		}
		
		points2[points.length] = p;
		pointControlScore2[pointControlScore.length] = 0;
		
		points = points2;		
		pointControlScore = pointControlScore2;
		pointIds.put(id, points.length-1);
	}
	
	public void deletePoint(String id)
	{
		int index = pointIds.get(id);
		points[index] = null;
	}
	
	public void reshapePoint(String id, Point p)
	{
		int index = pointIds.get(id);
		points[index] = p;
	}
	
	public double getScore(String id)
	{
		return 0;
	}
	
	private Point[] segmentCurve(Point[] curve)
	{
		if (curve.length == 0)
			return new Point[]{};
		
		double length = 0;		
		for (int i=0; i<curve.length-1; i++)
			length += curve[i].distance(curve[i+1]);
		
		int nrSeg = (int)Math.ceil(length/40.);
		double segLength = length / (double)nrSeg;
		
		Point[] ret = new Point[nrSeg+1];
		
		ret[0] = curve[0];
		
		
		double l = segLength;
		int cindex = 0;
		int sindex = 1;
		double remainder = 0;
		while(cindex != curve.length-1)
		{			
			double d = curve[cindex].distance(curve[cindex+1]);
						
			while (l <= d)
			{
				double vx = curve[cindex+1].x - curve[cindex].x;
				double vy = curve[cindex+1].y - curve[cindex].y;
				
				double len = Math.sqrt(vx*vx + vy*vy);
				
				vx /= len;
				vy /= len;
				
				Point p = new Point((int)(curve[cindex].x + vx*l), (int)(curve[cindex].y + vy*l));				
			
				ret[sindex++] = p;
				
				l += segLength;
			}
			l = l - d;
			cindex++;
		}
		
		if (ret[ret.length-1] == null) ret[ret.length-1] = curve[curve.length-1];
		
		return ret;
	}
	
	
	
	private void processCurveGaze(int curve, Point gaze)
	{
		//decay
		for (int i=0; i<curveControlScore[curve].length; i++)
		{				
			curveControlScore[curve][i] -= (curveSegmentDecay / Math.sqrt(curveLengths[curve]));
				if (curveControlScore[curve][i] < 0) curveControlScore[curve][i] = 0;	
		}
		
		addGazeToEdge(gaze, curveControl[curve], this.curveControlScore[curve], (int)curveSegThreshold);
		
		double d = this.computeBars(curve,curveControl[curve], this.curveControlScore[curve]);
		
		debugCompBars[curve] = d;

		double d1 = Math.min(1,d/maxCurveLength);
		double d2 =  Math.min(1,d/curveLengths[curve]);
		
		debugD1[curve]= d1;
		debugD2[curve] = d2;
				
		double s = Math.min(d1,d2);
		
		if (s > curveScoresShort[curve])
			curveScoresShort[curve] = Math.min(1, curveScoresShort[curve]+s/curveShortInc);
		else 
			curveScoresShort[curve] = Math.max(0, curveScoresShort[curve]-curveShortDec) ;
		
		
			
			
		if (curveScoresShort[curve] > curveScoresLong[curve])
			curveScoresLong[curve] += curveScoresShort[curve]/curveLongInc;
			else
				curveScoresLong[curve] -= curveLongDec;
			
		if (curveScoresLong[curve] > 1) curveScoresLong[curve] = 1;
		if (curveScoresLong[curve] < 0) curveScoresLong[curve] = 0;
		
		
		if (Math.random() < 0.1)
		{
			System.out.println("Score("+curve+"):"+s + "\t" + curveScoresShort[curve] + "\t" + curveScoresLong[curve]);
		}
		
	}
	
	public void addGazeToEdge(Point gaze, Point[] seg, double[] segV, int threshold)
	{	
	
		double bestDist = Double.MAX_VALUE;
		int bestIndex = -1;
	
		for (int i=0; i<seg.length; i++)
		{
			double d = gaze.distance(seg[i]);
			if (d < bestDist)
			{
				bestDist = d;
				bestIndex = i;
			}
		}
		
		if (bestIndex >= 0 && bestDist < threshold)
		{
			double v = 1-bestDist/threshold;
			
			v = Math.sqrt(v);
			
	
			int index = bestIndex;
			double max = 0;
			for (int i=bestIndex-2; i<=bestIndex+2; i++)
			{
				if (i < 0 || i>=seg.length)
					continue;
	
				if (segV[i] > max)
				{
					index = i;
					max = segV[i];
				}
			}
	
			segV[index] = segV[index] + curveSegmentInc*v;
			if (segV[index] > 1) segV[index] = 1;
		}	
	}
	
	
	public double computeBars(int index, Point[] seg, double[] segV)
	{
		if (seg.length <= 1) return 0;	
		
		double[] barsR = new double[segV.length];
		double[] barsL = new double[segV.length];	
		
		double len = curveLengths[index];
		double segL = len/(seg.length-1);
	
		
		for (int i=0; i<seg.length; i++)
		{
			double val = 3*segV[i];
			if (val == 0) continue;
			
			barsL[i] = val; 
			barsR[i] = val;
	
			if (val > i)
				barsR[i] += (val-i);
			if (val+i > seg.length-1)
				barsL[i] += (val+i - seg.length+1);
	
			if (barsL[i] > i)
				barsL[i] = i;
			if (barsR[i] + i > seg.length-1)
				barsR[i] = seg.length-1 - i;	
		}
		
		debugBarsL[index] = barsL;
		debugBarsR[index] = barsR;
	
		double min = -1;
		double max = -1;
		double d = 0;
		for (int i=0; i<seg.length; i++)
		{
			if (segV[i] <= 0) continue;
	
			double l = i*segL - barsL[i]*segL;
			double r = i*segL + barsR[i]*segL;	
	
			if (min < 0)
			{
				min = l; 
				max = r; 	
			}
			else
			{
				if (l > max)
				{	
					d = d + (max-min);
					min = l;
					max = r;	
				}
				else
				{	
					if (l < min) min = l;
					if (r > max) max = r;
				}
			}	
		}
		if (max > 0 && min>=0)
			d = d + (max-min);
		
	return d;
	}
	
	public void renderDebug(Graphics2D g)
	{
		//render gaze
		g.setColor(Color.green);
		g.fillOval(debugGazeLocation.x-10, debugGazeLocation.y-10, 20,20);
		//render segments
		g.setColor(Color.gray);
		for (int i=0; i<this.curveControl.length; i++)
			for (int j=0; j<this.curveControl[i].length-1; j++)
			{
				g.drawLine(curveControl[i][j].x, curveControl[i][j].y, curveControl[i][j+1].x, curveControl[i][j+1].y);
				g.fillOval(curveControl[i][j].x-5, curveControl[i][j].y-5, 10, 10);
			}
		
		//render bars
		g.setStroke(new BasicStroke(3));
		g.setColor(new Color(0,0,250, 150));
		for (int i=0; i<this.curveControl.length; i++)
			for (int j=0; j<this.curveControl[i].length; j++)
			{
				double len = curveLengths[i];
				double segL = len/(curveControl[i].length-1);
				
				double v1x = 0;
				double v1y = 0;
				
				if (j > 0)
				{
					v1x = curveControl[i][j-1].x - curveControl[i][j].x;
					v1y = curveControl[i][j-1].y - curveControl[i][j].y;
				}
				else
				{
					v1x = curveControl[i][j].x - curveControl[i][j+1].x;
					v1y = curveControl[i][j].y - curveControl[i][j+1].y;
				}
				
				double v1l = Math.sqrt(v1x*v1x + v1y*v1y);
				v1x/=v1l;
				v1y/=v1l;
				
				
				double v2x = 0;
				double v2y = 0;
				
				if (j < curveControl[i].length -1)
				{
					v2x = curveControl[i][j+1].x - curveControl[i][j].x;
					v2y = curveControl[i][j+1].y - curveControl[i][j].y;
				}
				else
				{
					v2x = curveControl[i][j].x - curveControl[i][j-1].x;
					v2y = curveControl[i][j].y - curveControl[i][j-1].y;
				}
				
				double v2l = Math.sqrt(v2x*v2x + v2y*v2y);
				v2x/=v2l;
				v2y/=v2l;
				
				if (debugBarsL.length != curveControl.length || debugBarsR.length != curveControl.length || debugBarsL[i] == null || debugBarsR[i] == null)
					continue;
				
				g.drawLine(curveControl[i][j].x, curveControl[i][j].y, curveControl[i][j].x + (int)(segL*debugBarsL[i][j] * v1x), curveControl[i][j].y + (int)(segL*debugBarsL[i][j] * v1y));
				g.drawLine(curveControl[i][j].x, curveControl[i][j].y, curveControl[i][j].x + (int)(segL*debugBarsR[i][j] * v2x), curveControl[i][j].y + (int)(segL*debugBarsR[i][j] * v2y));
				
			}
		
		//renderScores
		for (int i=0; i<this.curveControl.length; i++)
		{
			Point p = curveControl[i][curveControl[i].length-1];
			
			g.setFont(g.getFont().deriveFont(20.f));
			g.drawString(String.format("%1$,.0f", debugCompBars[i])+ "," +
					String.format("%1$,.0f", this.curveLengths[i]) + "," +  
					String.format("%1$,.2f", debugD1[i]) + "," + 
					String.format("%1$,.2f", debugD2[i]), p.x, p.y);

		}

		
		
	}

	
	
}
