package com.polyu.location.argorithm;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.log4j.Logger;

import math.geom2d.Point2D;
import math.geom3d.Point3D;

public class LocationMathUtils {
	public static Logger logger = Logger.getLogger(LocationMathUtils.class);
	public static double calcDistanceBetweenTwoPoint3D(Point3D point1,Point3D point2)
	{
		return Math.sqrt(Math.pow(point1.getX()-point2.getX(),2)+Math.pow(point1.getY()-point2.getY(),2)+Math.pow(point1.getZ()-point2.getZ(),2));
	}
	public static double calcVariance(List<Double> value)
	{
		double avg=0;
		for(int i=0;i<value.size();i++)
			avg+=value.get(i);
		avg=avg/value.size();
		double variance =0;
		double temp;
		for(int i=0;i<value.size();i++)
		{
			temp = value.get(i);
			variance +=  temp*temp;
		}
		variance = variance/value.size()-avg*avg;
		return variance;
	}
	public static double calcAvergeExcludeMaxAndMin(List<Double> value)
	{
		int maxNo=findMax(value);
		int minNo=findMin(value);
		double temp=0;
		for(int i=0;i<value.size();i++)
		{
			if(i==maxNo)
			{
				continue;
			}
			if(i==minNo)
			{
				continue;
			}
			temp+=value.get(i);
		}
		return temp/(value.size()-2);
	}
	public static int findMax(List<Double> values)
	{
		int a=0;
		for(int i=0;i<values.size();i++)
		{
			if(values.get(i)>values.get(a))
			    {
					a=i;
				}
		}
		return a;
	}
	public static int findMin(List<Double> values)
	{
		int a=values.size()-1;
		for(int i=0;i<values.size();i++)
		{
			if(values.get(i)<values.get(a))
			{
					a=i;
			}
		}
		return a;
	}
	public static double calcDistanceBetweenTwoPoint2D(Point2D point1,Point2D point2)
	{
		return Math.sqrt(Math.pow(point1.x()-point2.x(),2)+Math.pow(point1.y()-point2.y(), 2));
	}
	public static double calcDistanceFor2DWithPythagorean(double distance,double anchorHigh)
	{
		double temp=Math.pow(distance, 2)-Math.pow(anchorHigh, 2);
		if(temp<0)
		{
			return 0;
		}
		return Math.sqrt(temp);
	}
	public static double[] calcLinearEquationGroupForLocation2D(double[][] martixA, double[] martixB)
	{
		if(martixA.length>2)
		{
			try
			{
			OLSMultipleLinearRegression regression2 = new OLSMultipleLinearRegression();
			regression2.setNoIntercept(true);
	        regression2.newSampleData(martixB, martixA);       
	        double[] result = regression2.estimateRegressionParameters();
	        return result;
			}
			catch(Exception e)
			{
				
				return null;
			}
        }
		else if(martixA.length==2)
		{
			double[] result = new double[2];
			if((martixA[0][0]*martixA[1][1]-martixA[0][1]*martixA[1][0])!=0)
			{
				result[0]=(martixB[0]*martixA[1][1]-martixB[1]*martixA[0][1])/(martixA[0][0]*martixA[1][1]-martixA[0][1]*martixA[1][0]);
				result[1]=(-martixB[0]*martixA[1][0]+martixB[1]*martixA[0][0])/(martixA[0][0]*martixA[1][1]-martixA[0][1]*martixA[1][0]);
		
			}
			return result;
		}
		return null;
		
	}
	public static void main(String args[])
	{
		double martixA[][]=new double[2][2];
		martixA[0][0]=1;
		martixA[0][1]=2;
		martixA[1][0]=3;
		martixA[1][1]=4;
		double martixB[]=new double[2];
		martixB[0]=3;
		martixB[1]=7;
		double[] result=calcLinearEquationGroupForLocation2D(martixA,martixB);
		logger.info("Test Calc Linear "+result[0]+" "+result[1]);
		List<Double> array=new ArrayList<Double>();
		array.add(1.0);
		array.add(1.0);
		logger.info("Test Variance "+LocationMathUtils.calcVariance(array));
	}
}
