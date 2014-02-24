package com.polyu.location.argorithm;

import java.util.Map;
import java.util.Set;

import math.geom2d.Point2D;
import math.geom3d.Point3D;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.log4j.Logger;

public class Localization {
	private static Logger logger = Logger.getLogger(Localization.class);
	private Map<Integer,Point3D> anchorPositionMap;
	private RealMatrix matrixA=null;
	private RealMatrix matrixR=null;
	private double defaultR[]={2,2,2,2,10};
	private double updateFrequency=0.2;
	private RealMatrix matrixX=null;
	private RealMatrix matrixP=null;
	private RealMatrix matrixQ=null;
	private RealMatrix matrixH=null;
	private double THRESOLDDISTANCE=2.5;
	public Map<Integer, Point3D> getAnchorPositionMap() {
		return anchorPositionMap;
	}
	public void setAnchorPositionMap(Map<Integer, Point3D> anchorPositionMap) {
		this.anchorPositionMap = anchorPositionMap;
	}
	public Point2D calculate(Map<Integer,Double> distanceData,double angle)
	{
		Set<Integer> keySet=distanceData.keySet();
		matrixR=MatrixUtils.createRealDiagonalMatrix(defaultR);
		for(Integer i:keySet)
		{
			if(distanceData.get(i)<THRESOLDDISTANCE)
			{
				matrixR.setEntry(i-1, i-1, 0.2*distanceData.get(i)+0.01);
			}
		}
		this.matrixX=this.matrixA.multiply(this.matrixX);
		this.matrixA=this.matrixA.multiply(this.matrixP).multiply(this.matrixA.transpose()).add(this.matrixQ);
		for(Integer i:keySet)
		{
			Point3D lp=anchorPositionMap.get(i)
			double temp=matrixX.getEntry(0, 0)-anchorPositionMap
		}
		
	}
	
	
}
