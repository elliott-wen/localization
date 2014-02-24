package com.polyu.location.argorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;






import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.log4j.Logger;

import math.geom2d.Point2D;
import math.geom2d.conic.Circle2D;
import math.geom3d.Point3D;

public class LocationArgorithmFor2D {
	private Map<Integer,Point3D> anchorPositionMap;
	private List<Point2D> historyPoints=new ArrayList<Point2D>();
	
	private static final int MAXHISTORYPOINT=10;
	private static Logger logger = Logger.getLogger(LocationArgorithmFor2D.class);
	private RealMatrix statusVector=null;
	private RealMatrix systemMatrixA=null;
	private RealMatrix systemNoiseMatrix=null;
	private RealMatrix covarianceMatrix=null;
	private RealMatrix IMatrix=null;
	private RealMatrix systemObseverNoiseMatrixV=null;
	private RealMatrix systemObserveMatrixH=null;
	private RealMatrix observeVector=null;
	private RealMatrix gainMatrix=null;
	private final static double UPDATEFREQUENCY=0.2;
	public void initKalmanFilter()
	{
		double [][]raw={{1,0,UPDATEFREQUENCY,0},{0,1,0,UPDATEFREQUENCY},{0,0,1,0},{0,0,0,1}};
		this.systemMatrixA=new Array2DRowRealMatrix(raw);
		double [][]raw1={{0.01},{0.01},{0.01},{0.01}};
		this.statusVector=new Array2DRowRealMatrix(raw1);
		double []raw2={1,1,0.5,0.5};
		this.systemNoiseMatrix=MatrixUtils.createRealDiagonalMatrix(raw2);
		double []raw3={0.5,0.5,Math.PI/18};
		this.systemObseverNoiseMatrixV=MatrixUtils.createRealDiagonalMatrix(raw3);
		double []rawI={1,1,1,1};
		IMatrix=MatrixUtils.createRealDiagonalMatrix(rawI);
		double []raw4={1,1,1,1};
		this.covarianceMatrix=MatrixUtils.createRealDiagonalMatrix(raw4);
	}
	private void print(RealMatrix in)
	{
	
	}
	public Point2D kalmanFilter(Point2D obsLocation,double obsGyroZ)
	{
		double raw1[][]={{obsLocation.x()},{obsLocation.y()},{obsGyroZ}};
		
		this.observeVector=new Array2DRowRealMatrix(raw1);
		//System.out.println("Obs");
		print(observeVector);
		this.statusVector=this.systemMatrixA.multiply(this.statusVector);
		//System.out.println("Sta");
		print(statusVector);
		this.covarianceMatrix=this.systemMatrixA.multiply(this.covarianceMatrix).multiply(this.systemMatrixA.transpose()).add(this.systemNoiseMatrix);
		double lx=(this.statusVector.getColumn(0))[0];
		double ly=(this.statusVector.getColumn(0))[1];
		double vx=(this.statusVector.getColumn(0))[2];
		double vy=(this.statusVector.getColumn(0))[3];
		////System.out.println(lx+" "+ly+" "+vx+" "+vy+" ");
		//System.out.println("P");
		print(covarianceMatrix);
		double raw[][]={{1,0,0,0},{0,1,0,0},{0,0,-vy/(vx*vx+vy*vy),vx/(vx*vx+vy*vy)}};
		this.systemObserveMatrixH=new Array2DRowRealMatrix(raw);
		RealMatrix temp=this.systemObserveMatrixH.multiply(this.covarianceMatrix).multiply(this.systemObserveMatrixH.transpose())
				.add(this.systemObseverNoiseMatrixV);
		temp=new LUDecomposition(temp).getSolver().getInverse();
		this.gainMatrix=this.covarianceMatrix.multiply(this.systemObserveMatrixH.transpose()).multiply(temp);
		//System.out.println("Gain");
		print(gainMatrix);
		double raw2[][]={{lx},{ly},{Math.atan(vy/vx)}};
		RealMatrix h=new Array2DRowRealMatrix(raw2);
		print(h);
		this.statusVector=this.statusVector.add(this.gainMatrix.multiply(this.observeVector.subtract(h)));
		//System.out.println("Sta");
		print(statusVector);
		this.covarianceMatrix=this.IMatrix.subtract(this.gainMatrix.multiply(this.systemObserveMatrixH)).multiply(this.covarianceMatrix);
		//System.out.println("Cov");
		print(this.covarianceMatrix);
		Point2D p=new Point2D((this.statusVector.getColumn(0))[0],(this.statusVector.getColumn(0))[1]);
		return p;
	}
	private void addHistoryPoint(Point2D newPoint)
	{
		if(historyPoints.size()>MAXHISTORYPOINT)
		{
			historyPoints.remove(0);
		}
		historyPoints.add(newPoint);
	}
	private int compareTwoPointWithHistoryPoint(Point2D point1,Point2D point2)
	{
		if(historyPoints.size()==0)
		{
			return -1;
		}
		int pointC1=0;
		int pointC2=0;
		for(int i=0;i<historyPoints.size();i++)
		{
			Point2D tmp=historyPoints.get(i);
			if(LocationMathUtils.calcDistanceBetweenTwoPoint2D(tmp, point1)-LocationMathUtils.calcDistanceBetweenTwoPoint2D(tmp, point2)>0)
			{
				pointC1++;
			}
			else
			{
				pointC2++;
			}
		}
		if(pointC1>pointC2)
		{
			return 1;
		}
		else
		{
			return 2;
		}
	}
	/*private double preProcessDistanceData(List<Double> values)
	{
		
			double variance=LocationMathUtils.calcVariance(values);
			if(variance<cutLimit)
			{
				return LocationMathUtils.calcAvergeExcludeMaxAndMin(values);
			}
			else
			{
				return -1;
			}
		
	}*/
	
	
	public Point2D calulatePosition(Map<Integer,Double> distanceData)
	{
		//==========Delete unused data================
		List<Integer> availableID=new ArrayList<Integer>();
		Iterator<Entry<Integer,Double>> iter=distanceData.entrySet().iterator();
		while(iter.hasNext())
		{
			Entry<Integer,Double> entry=iter.next();
			if(entry.getValue()<0)
			{
				continue;
			}
			availableID.add(entry.getKey());
			logger.debug("Calc Process 1 "+entry.getKey());
		}
		
		//===============Calcualte 2D distance =============================
		Map<Integer,Double> distanceData2D=new HashMap<Integer,Double>();
		for(int i=0;i<availableID.size();i++)
		{
			int id=availableID.get(i);
			double anchorHeight=this.anchorPositionMap.get(id).getZ();
			double distance2D=LocationMathUtils.calcDistanceFor2DWithPythagorean(distanceData.get(id), anchorHeight);
			distanceData2D.put(id, distance2D);
			logger.debug("Calc Process 2:"+id+" "+distance2D);
		}
		
		//========================Handle If only anchor available=======================================================
		if(availableID.size()==2)
		{
			logger.debug("Calc Process 3");
			double firstX=this.anchorPositionMap.get(availableID.get(0)).getX();
			double firstY=this.anchorPositionMap.get(availableID.get(0)).getY();
			Circle2D firstCircle=new Circle2D(firstX,firstY,distanceData2D.get(availableID.get(0)));
			double secondX=this.anchorPositionMap.get(availableID.get(1)).getX();
			double secondY=this.anchorPositionMap.get(availableID.get(1)).getY();
			Circle2D secondCircle=new Circle2D(secondX,secondY,distanceData2D.get(availableID.get(1)));
			Collection<Point2D> intersectPoint=Circle2D.circlesIntersections(firstCircle, secondCircle);
			if(intersectPoint.size()==1)
			{
				Iterator<Point2D> it=intersectPoint.iterator();
				Point2D point=it.next();
				this.addHistoryPoint(point);
				return (point);
			}
			else if(intersectPoint.size()==2)
			{
				Iterator<Point2D> it=intersectPoint.iterator();
				Point2D point1=it.next();
				Point2D point2=it.next();
				int result=this.compareTwoPointWithHistoryPoint(point1, point2);
				if(result==1)
				{
					this.addHistoryPoint(point1);
					return point1;
				}
				else
				{
					this.addHistoryPoint(point2);
					return point2;
				}
			}
			else
			{
				return null;
			}
		}
		else if(availableID.size()>2)
		{
			logger.debug("Calc Process 4");
			double[][] matrixA=new double[availableID.size()-1][2];
			double[] matrixB=new double[availableID.size()-1];
			for(int i=1;i<availableID.size();i++)
			{
					matrixA[i-1][0]=anchorPositionMap.get(availableID.get(i)).getX()-anchorPositionMap.get(availableID.get(0)).getX();
					matrixA[i-1][1]=anchorPositionMap.get(availableID.get(i)).getY()-anchorPositionMap.get(availableID.get(0)).getY();
					matrixB[i-1]=0.5*(Math.pow(distanceData2D.get(availableID.get(0)),2)-Math.pow(distanceData2D.get(availableID.get(i)),2)
							+Math.pow(matrixA[i-1][0],2)+Math.pow(matrixA[i-1][1],2));
			}
			//Handle One Beacon in One Line
			int emptyrow1=0;
			int emptyrow2=0;
			for(int i=0;i<matrixA.length;i++)
			{
				if(matrixA[i][0]==0)
					emptyrow1++;
				if(matrixA[i][1]==0)
					emptyrow2++;
			}
			double result[]=new double[2];
			if(emptyrow1==matrixA.length)
			{
				logger.debug("Calc Process 5");
				result[1]=matrixB[1]/matrixA[0][1];
				double distance=distanceData2D.get(availableID.get(0));
				double xDistance=Math.sqrt(Math.pow(distance, 2)-Math.pow(result[1], 2));
				double xPossible1=anchorPositionMap.get(availableID.get(0)).getX()+xDistance;
				double xPossible2=anchorPositionMap.get(availableID.get(0)).getX()-xDistance;
				Point2D point1=new Point2D(xPossible1,result[1]);
				Point2D point2=new Point2D(xPossible2,result[1]);
				int r=this.compareTwoPointWithHistoryPoint(point1, point2);
				if(r==1)
				{
					this.addHistoryPoint(point1);
					return point1;
				}
				else
				{
					this.addHistoryPoint(point2);
					return point2;
				}
			}
			else if(emptyrow2==matrixA.length)
			{
				logger.debug("Calc Process 6");
				result[0]=matrixB[1]/matrixA[0][0];
				double distance=distanceData2D.get(availableID.get(0));
				double yDistance=Math.sqrt(Math.pow(distance, 2)-Math.pow(result[0], 2));
				double yPossible1=anchorPositionMap.get(availableID.get(0)).getY()+yDistance;
				double yPossible2=anchorPositionMap.get(availableID.get(0)).getY()-yDistance;
				Point2D point1=new Point2D(result[0],yPossible1);
				Point2D point2=new Point2D(result[0],yPossible2);
				int r=this.compareTwoPointWithHistoryPoint(point1, point2);
				if(r==1)
				{
					this.addHistoryPoint(point1);
					return point1;
				}
				else
				{
					this.addHistoryPoint(point2);
					return point2;
				}
			}
			result=LocationMathUtils.calcLinearEquationGroupForLocation2D(matrixA, matrixB);
			if(result==null)
			{
				return null;
			}
			else
			{
				Point2D returnPoint= new Point2D(anchorPositionMap.get(availableID.get(0)).getX()+result[0],anchorPositionMap.get(availableID.get(0)).getY()+result[1]);
				this.addHistoryPoint(returnPoint);
				return (returnPoint);
			}
			
		}
		//=========================================================================================================
		return null;
	}
	public Map<Integer, Point3D> getAnchorPositionMap() {
		return anchorPositionMap;
	}
	public void setAnchorPositionMap(Map<Integer, Point3D> anchorPositionMap) {
		this.anchorPositionMap = anchorPositionMap;
	}
	
	
	private static void normalTest()
	{
		logger.info("Normal Testing Program");
		Random random=new Random();
		LocationArgorithmFor2D argo=new LocationArgorithmFor2D();
		logger.info("Anchor 1 position:0 0 100, Anchor 2 position 100 0 100, Anchor 3 Position 0 100 100, Anchor 4 Position 100 100 100");
		Map<Integer,Point3D> anchorPositionMap=new HashMap<Integer,Point3D>();
		Point3D anchor1=new Point3D(0,0,0);
		Point3D anchor2=new Point3D(10,0,0);
		Point3D anchor3=new Point3D(0,10,0);
		Point3D anchor4=new Point3D(10,10,0);
		
		anchorPositionMap.put(1, anchor1);
		anchorPositionMap.put(2, anchor2);
		anchorPositionMap.put(3, anchor3);
		anchorPositionMap.put(4, anchor4);
		int testingTime=10000;
		argo.setAnchorPositionMap(anchorPositionMap);
		logger.info("Test will test "+testingTime+" testcases");
		int success=0;
		double maxError=0;
		for(int i=0;i<testingTime;i++)
		{
			double randomX=random.nextDouble()*10;
			double randomY=random.nextDouble()*10;
			double randomZ=0;
			Point3D randomPoint=new Point3D(randomX,randomY,randomZ);
			double distance1=LocationMathUtils.calcDistanceBetweenTwoPoint3D(anchor1, randomPoint)+2;
			double distance2=LocationMathUtils.calcDistanceBetweenTwoPoint3D(anchor2, randomPoint)-2;
			double distance3=LocationMathUtils.calcDistanceBetweenTwoPoint3D(anchor3, randomPoint)+2;
			double distance4=LocationMathUtils.calcDistanceBetweenTwoPoint3D(anchor4, randomPoint)+2;
		
			Map<Integer,Double> distanceData=new HashMap<Integer,Double>();
			distanceData.put(1, distance1);
			distanceData.put(2, distance2);
			distanceData.put(3, distance3);
			distanceData.put(4, distance4);
			logger.info("Generate Test Data X="+randomX+" Y="+randomY+" "+"Distance between anchor1:"+ distance1 +",Distance between anchor2:"+distance2+",Distance between anchor3:"+distance3+",Distance between anchor4:"+distance4);
			
			Point2D result=argo.calulatePosition(distanceData);
			logger.info("Output Result X="+result.x()+" Y="+result.y());
			double error=Math.sqrt((result.x()-randomX)*(result.x()-randomX)+(result.y()-randomY)*(result.y()-randomY));
			if(error>maxError)
			{
				maxError=error;
			}
			if(result.x()-randomX<1&&result.y()-randomY<1)
			{
				logger.info("Testcase Passed:"+i);
				success++;
			}
			
			else
			{
				logger.error("Testcase Failed"+i);
			}
		}
		logger.info("Success :"+success+" Failed:"+(testingTime-success)+" Error:"+maxError);
	}
	
	
	
}
