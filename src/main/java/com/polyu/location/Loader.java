package com.polyu.location;

import java.io.File;

import org.apache.log4j.Logger;

public class Loader {
	
		 private static Logger logger = Logger.getLogger(Loader.class);
		 public static void main( String[] args )
		 {
		    	loadConfig(args);
		    	App app=new App();
		    	app.initApp();
		    	
		    	
		 }
		 private static void loadConfig(String []args)
			{
				if(args.length==0)
		        {
		        	logger.info("No config file specified, use default config file");
		        	Config.loadConfig(new File("src/main/resources/config.xml"));
		        }
		        else
		        {
		        	File configFile=new File(args[0]);
		        	if(configFile.exists())
		        	{
		        		if(Config.loadConfig(configFile))
		        		{
		        			logger.info("External Config File Loaded.");
		        		}
		        		else
		        		{
		        			logger.error("Bad config file! Please Check the configuration");
		        			System.exit(-1);
		        		}
		        	}
		        	else
		        	{
		        		logger.error("Config file don't exist! Please Check the configuration");
		        		System.exit(-1);
		        	}
		        }
			}
}
