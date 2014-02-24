package com.polyu.location;


import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import math.geom2d.Point2D;

import org.apache.log4j.Logger;

import com.polyu.location.argorithm.LocationArgorithmFor2D;

/**
 * Hello world!
 *
 */
public class App implements SerialPortEventListener
{
	private static Logger logger = Logger.getLogger(App.class);
	private LocationArgorithmFor2D argorithm=new LocationArgorithmFor2D();
	private SerialPort serialPort=null;
	
	private DistanceChart distanceChart=null;
	private PositionChart positionChart=null;
	private DecimalFormat df = new DecimalFormat("#0.0000");
	private int lastSequence=-1;
	private double[] lastDistance={-1,-1,-1,-1};
	double P[][]={{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
	double Q[][]={{2,0,0,0},{0,2,0,0},{0,0,2,0},{0,0,0,2}};
	double R[][]={{2,0,0,0},{0,2,0,0},{0,0,2,0},{0,0,0,2}};
	double DistPre[]={0,0,0,0};
	double K[]={0,0,0,0};
	double KalmanDist[] ={0,0,0,0};
	void Kalmanfilter_Distance(double []Dist)
	{
		int i,j,k;
		double E[][]=new double[4][4];
		for(i=0;i<4;i++) 
		 {
			DistPre[i]=KalmanDist[i];
		 	E[i][i]=P[i][i]+Q[i][i];
		 	K[i]=E[i][i]/(E[i][i]+R[i][i]);
		 	KalmanDist[i]=DistPre[i]+K[i]*(Dist[i]-DistPre[i]);
		 	P[i][i]=P[i][i]-K[i]*P[i][i];
		 	Dist[i]=KalmanDist[i];
		 }
	 }
	public void initApp()
	{
		initArgorithm();
		initDistanceChart();
		initPositionChart();
		initCommunication();
	}
	public void initArgorithm()
	{
		argorithm.setAnchorPositionMap(Config.anchorPositionMap);
		argorithm.initKalmanFilter();
	}
	public void initPositionChart()
	{
		positionChart = new PositionChart("Position");
	    positionChart.init();
	    positionChart.pack();
	    positionChart.setVisible(true);
	}
	public void initDistanceChart()
	{
		distanceChart = new DistanceChart("Distance Chart");
		distanceChart.init();
		distanceChart.pack();
		distanceChart.setVisible(true);
	}
	public void initCommunication()
	{
	
			try
			{
				CommPortIdentifier port=CommPortIdentifier.getPortIdentifier(Config.PORTNAME);
	        	serialPort = (SerialPort) port.open("Location", Config.PORTBAUDRATE);
	        	//inputStream=serialPort.getInputStream();
	            serialPort.setSerialPortParams(Config.PORTBAUDRATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
	            serialPort.notifyOnDataAvailable(true);
	            serialPort.addEventListener(this);
	            logger.info("Open Serial Successful!");
			}
			catch(Exception e)
			{
				logger.error("Open Communication Failed",e);
				logger.error("Ready to quit");
				System.exit(-1);
			}
            
            /*Thread readerThread=new Thread()
    		{
    			public void run()
    			{
    				
    				while(true)
    				{
    					try
    					{
    						x
    						byte[] startHeader= new byte[1];
    						//logger.info("fuck");
    						inputStream.read(startHeader);
    						if(startHeader[0]!=-1)
    						{
    							logger.info("Failed to locate start header!"+formatHex(startHeader[0])+":"+startHeader[0]);
    							continue;
    						}
    						byte[] readBuffer = new byte[31];
    						int numBytesRead=0;
    						int tagID=0;
    						numBytesRead=inputStream.read(readBuffer);
    						if(numBytesRead!=31)
    						{
    							logger.error("Data From Serial Port Seems Bad! Give Up "+numBytesRead);
    							continue;
    						}
    						tagID=readBuffer[0]&0XFF;
    						Map<Integer,Double> distanceMap=new HashMap<Integer,Double>();
    						for(int t=0;t<4;t++)
    						{
    								int n=t*5+1;
    								int anchorID=readBuffer[n]&0XFF;
    								anchorID=anchorID%112;
    								int distance=0;
    					            distance = readBuffer[n + 4];
    					            distance &= 0xff;
    					            distance |= ((long) readBuffer[n + 3] << 8);
    					            distance &= 0xffff;
    					            distance |= ((long) readBuffer[n + 2] << 16);
    					            distance &= 0xffffff;
    					            distance |= ((long) readBuffer[n + 1] << 24);
    					            double distanceD=Float.intBitsToFloat(distance);
    					            logger.debug("Tag="+tagID+":Distance between anchor "+anchorID+" :"+format(distanceD));
    					            distanceMap.put(anchorID, distanceD);
    						}
    						int tempX=0;
    						tempX=readBuffer[24];
    						tempX &= 0xff;
    						tempX |= ((long) readBuffer[23] << 8);
    						tempX &= 0xffff;
    						tempX |= ((long) readBuffer[22] << 16);
    						tempX &= 0xffffff;
    						tempX |= ((long) readBuffer[21] << 24);
    						double positionFromTagX=Float.intBitsToFloat(tempX);
    						int tempY=0;
    						tempY=readBuffer[28];
    						tempY &= 0xff;
    						tempY |= ((long) readBuffer[27] << 8);
    						tempY &= 0xffff;
    						tempY |= ((long) readBuffer[26] << 16);
    						tempY &= 0xffffff;
    						tempY |= ((long) readBuffer[25] << 24);
    						double positionFromTagY=Float.intBitsToFloat(tempY);
    						int sequence=0;
    						sequence=readBuffer[30];
    						sequence &= 0xff;
    						sequence|=((long)readBuffer[29] << 8);
    						if(lastSequence!=sequence)
    						{
    						lastSequence=sequence;
    						handleDataFromSerialPort(tagID, distanceMap, positionFromTagX, positionFromTagY,sequence);
    						}
    					}
    					catch(Exception e)
        				{
    						logger.error(e);
        					return;
        				}
        				finally
        				{
        					try
        					{
        						inputStream.close();
        					}
        					catch(Exception ex){}
        				}
    				}
    				
    			}
    		};
    		readerThread.start();*/
		
	}
	private void handleDataFromSerialPort(int tagID,Map<Integer,Double> distanceData, double positionFromTagX, double positionFromTagY,double gyroX,double gyroY,double gyroZ,int sequence)
	{
		
		Point2D computerResult=argorithm.calulatePosition(distanceData);
		computerResult=argorithm.kalmanFilter(computerResult, gyroZ/57.6);
		//logger.info("Location For tag:"+tagID);
		if(computerResult==null)
		{
			logger.warn("Calc Position Failed");
			this.positionChart.updatePlotData(0,0,positionFromTagX,positionFromTagY);
		}
		else
		{
			logger.debug("Computer Location Result: X="+format(computerResult.x())+" Y="+format(computerResult.y()));
			this.positionChart.updatePlotData(computerResult.x(), computerResult.y(), positionFromTagX, positionFromTagY);
		}
		logger.debug("Tag Location Result: X="+format(positionFromTagX)+" Y="+format(positionFromTagY));
		this.distanceChart.updatePlotData(distanceData);
		logger.debug("=========================================");
		String log=""+sequence+" ";
		Iterator<Entry<Integer,Double>> iter=distanceData.entrySet().iterator();
		while(iter.hasNext())
		{
			Entry<Integer,Double> entry=iter.next();
			log+=entry.getKey()+","+format(entry.getValue())+",";
			
		}
		log+=format(computerResult.x())+","+format(computerResult.y())+","+format(gyroX)+","+format(gyroY)+","+format(gyroZ)+". ";
		logger.info(log);
	}
	
  
	public void serialEvent(SerialPortEvent arg0) 
	{
		/*try 
		{
			serialPort.setSerialPortParams(Config.PORTBAUDRATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        } 
		catch (Exception e) 
		{
		}*/
	    /*The Format of the Data
	     * FF 0A 01 00 00 00 00 02 00 00 00 00 03 00 00 00 00 04 00 00 00 00 xx xx xx xx yy yy yy yy
	     */
		    logger.debug("=========================================");
			InputStream inputStream=null;
			
			byte[] readBuffer = new byte[44];
			int numBytesRead=0;
			int tagID=0;
			try 
			{
				inputStream = serialPort.getInputStream();
				
				if (inputStream.available() >= 44) 
				{
					numBytesRead = inputStream.read(readBuffer);
		        } 
			}
			catch (Exception e) 
			{
				logger.error("Read Bytes From Serial Port Failed",e);
				return;
			}
			finally
			{
				try
				{
					inputStream.close();
					
				}
				catch(Exception e)
				{
					
				}
			}
			logger.debug("Read Bytes From Serial Port:"+numBytesRead);
			if(numBytesRead!=44)
			{
				logger.error("Data From Serial Port Seems Bad! Give Up");
				return;
			}
			tagID=readBuffer[1]&0XFF;
			Map<Integer,Double> distanceMap=new HashMap<Integer,Double>();
			for(int t=0;t<4;t++)
			{
					int n=t*5+2;
					int anchorID=readBuffer[n]&0XFF;
					anchorID=anchorID%112;
					int distance=0;
		            distance = readBuffer[n + 4];
		            distance &= 0xff;
		            distance |= ((long) readBuffer[n + 3] << 8);
		            distance &= 0xffff;
		            distance |= ((long) readBuffer[n + 2] << 16);
		            distance &= 0xffffff;
		            distance |= ((long) readBuffer[n + 1] << 24);
		            double distanceD=Float.intBitsToFloat(distance);
		            logger.debug("Tag="+tagID+":Distance between anchor "+anchorID+" :"+format(distanceD));
		            if(lastDistance[t]==-1) lastDistance[t]=distanceD;
		            else if(distanceD-lastDistance[t]>1) distanceD=lastDistance[t]+1;
		            else if(distanceD-lastDistance[t]<-1) distanceD=lastDistance[t]-1;
		            lastDistance[t]=distanceD;
		            
		            distanceMap.put(anchorID, distanceD);
			}
			Kalmanfilter_Distance(lastDistance);
			int tempX=0;
			tempX=readBuffer[25];
			tempX &= 0xff;
			tempX |= ((long) readBuffer[24] << 8);
			tempX &= 0xffff;
			tempX |= ((long) readBuffer[23] << 16);
			tempX &= 0xffffff;
			tempX |= ((long) readBuffer[22] << 24);
			double positionFromTagX=Float.intBitsToFloat(tempX);
			int tempY=0;
			tempY=readBuffer[29];
			tempY &= 0xff;
			tempY |= ((long) readBuffer[28] << 8);
			tempY &= 0xffff;
			tempY |= ((long) readBuffer[27] << 16);
			tempY &= 0xffffff;
			tempY |= ((long) readBuffer[26] << 24);
			double positionFromTagY=Float.intBitsToFloat(tempY);
			int gyro=0;
			gyro=readBuffer[33];
			gyro &= 0xff;
			gyro |= ((long) readBuffer[32] << 8);
			gyro &= 0xffff;
			gyro |= ((long) readBuffer[31] << 16);
			gyro &= 0xffffff;
			gyro |= ((long) readBuffer[30] << 24);
			double gyroX=Float.intBitsToFloat(gyro);
			gyro=0;
			gyro=readBuffer[37];
			gyro &= 0xff;
			gyro |= ((long) readBuffer[36] << 8);
			gyro &= 0xffff;
			gyro |= ((long) readBuffer[35] << 16);
			gyro &= 0xffffff;
			gyro |= ((long) readBuffer[34] << 24);
			double gyroY=Float.intBitsToFloat(gyro);
			gyro=0;
			gyro=readBuffer[41];
			gyro &= 0xff;
			gyro |= ((long) readBuffer[40] << 8);
			gyro &= 0xffff;
			gyro |= ((long) readBuffer[39] << 16);
			gyro &= 0xffffff;
			gyro |= ((long) readBuffer[38] << 24);
			double gyroZ=Float.intBitsToFloat(gyro);
			int sequence=0;
			sequence=readBuffer[43];
			sequence &= 0xff;
			sequence|=((long)readBuffer[42] << 8);
			if(lastSequence!=sequence)
			{
			lastSequence=sequence;
			this.handleDataFromSerialPort(tagID, distanceMap, positionFromTagX, positionFromTagY,gyroX,gyroY,gyroZ,sequence);
			}
	}
	private String format(double number)
	{
		
		return df.format(number);
	}
	
	public static String formatHex(byte c)
	{
		String buf = Integer.toHexString((c & 0x000000FF) | 0xFFFFFF00).substring(6);
		
		if (buf.length() < 2)
			buf = "0" + buf;

		return buf + " ";
	}
}
