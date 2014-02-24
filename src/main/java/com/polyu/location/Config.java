package com.polyu.location;

import java.io.File;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;


import math.geom3d.Point3D;

public class Config {
	public static Map<Integer, Point3D> anchorPositionMap=null;
	public static String PORTNAME=null;
	public static int PORTBAUDRATE=57600;
	public static int WINDOWSIZE=10;
	
	public static boolean isSimulation=false;
	private static Logger logger = Logger.getLogger(Config.class);
	public static boolean loadConfig(File xmlFile)
	{
		try
		{
		  SAXBuilder builder = new SAXBuilder();
		  InputStream fileInputStream = new FileInputStream(xmlFile);
		  Document document = builder.build(fileInputStream);//获得文档对象
		  Element root = document.getRootElement();//获得根节点
		  Element sim=root.getChild("Simulation");
		  isSimulation=Boolean.valueOf(sim.getChildText("Switch"));
		  if(isSimulation)
		  {
			  logger.info("The program is running in simulation mode");
			  
		  }
		  else
		  {
			  logger.info("Loading Communication Detail");
			  Element com=root.getChild("Communication");
			  PORTNAME=com.getChildText("Port");
			  logger.info("Use port:"+PORTNAME);
			  PORTBAUDRATE=Integer.valueOf(com.getChildText("BaudRate"));
			  logger.info("Port BaudRate:"+PORTBAUDRATE);
		  }
		  
		  logger.info("Loading Data Process Parameters");
		  Element process=root.getChild("Process");
		  WINDOWSIZE=Integer.valueOf(process.getChildText("WindowSize"));
		  logger.info("Window Length:"+WINDOWSIZE);
		  logger.info("Loading Anchor Details");
		  anchorPositionMap=new HashMap<Integer, Point3D>();
		  Element anchors=root.getChild("Anchors");
		  List<Element> anchor=anchors.getChildren();
		  for(Element e:anchor)
		  {
			  int id=Integer.valueOf(e.getChildText("id"));
			  double x=Double.valueOf(e.getChildText("X"));
			  double y=Double.valueOf(e.getChildText("Y"));
			  double z=Double.valueOf(e.getChildText("Z"));
			  anchorPositionMap.put(id, new Point3D(x,y,z));
			  logger.info("Found an anchor=》ID="+id+" X="+x+" Y="+y+" Z="+z);
		  }
		  return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.error("Error when loading config file", e);
			return false;
		}
	}
}
