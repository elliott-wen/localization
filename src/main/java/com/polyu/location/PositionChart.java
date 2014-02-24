package com.polyu.location;

import java.awt.Color;
import java.awt.Dimension;






import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;

import math.geom3d.Point3D;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;


public class PositionChart extends ApplicationFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7574554959001743481L;
	private XYSeries tagSeries=null;
	private XYSeries computerSeries=null;
	private XYSeries oldComputerSeries=null;
	
  public PositionChart(String paramString)
  {
    super(paramString);
  }

  private static JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createScatterPlot("Position", "X", "Y", paramXYDataset, PlotOrientation.VERTICAL, true, false, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    localXYPlot.setNoDataMessage("NO DATA");
    localXYPlot.setDomainZeroBaselineVisible(true);
    localXYPlot.setRangeZeroBaselineVisible(true);
    XYLineAndShapeRenderer localXYLineAndShapeRenderer = (XYLineAndShapeRenderer)localXYPlot.getRenderer();
    localXYLineAndShapeRenderer.setSeriesOutlinePaint(0, Color.black);
    localXYLineAndShapeRenderer.setUseOutlinePaint(true);
    NumberAxis localNumberAxis1 = (NumberAxis)localXYPlot.getDomainAxis();
    localNumberAxis1.setAutoRangeIncludesZero(true);
    localNumberAxis1.setRange(-20D, 40D);
    localNumberAxis1.setTickMarkInsideLength(2F);
    localNumberAxis1.setTickMarkOutsideLength(0F);
    NumberAxis localNumberAxis2 = (NumberAxis)localXYPlot.getRangeAxis();
    localNumberAxis2.setTickMarkInsideLength(2F);
    localNumberAxis2.setRange(-20D, 40D);
    localNumberAxis2.setTickMarkOutsideLength(0F);
    double size = 20.0;
    double delta = size / 2.0;
    Shape shape1 = new Rectangle2D.Double(-delta, -delta, size, size);
    Shape shape2 = new Rectangle2D.Double(-delta, -delta, size, size);
    Shape shape3 = new Rectangle2D.Double(-delta, -delta, size, size);
    Shape shape4 = new Rectangle2D.Double(-delta, -delta, size, size);
    Shape shape5 = new Ellipse2D.Double(-delta, -delta, size, size);
    
    localXYPlot.getRenderer().setSeriesShape(0, shape1);
    localXYPlot.getRenderer().setSeriesShape(1, shape2);
    localXYPlot.getRenderer().setSeriesShape(2, shape3);
    localXYPlot.getRenderer().setSeriesShape(3, shape4);
    localXYPlot.getRenderer().setSeriesShape(4, shape5);
    return localJFreeChart;
  }
  public void init()
  {
	  JFreeChart localJFreeChart = createChart(createDataset());
	    ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
	    localChartPanel.setVerticalAxisTrace(true);
	    localChartPanel.setHorizontalAxisTrace(true);
	    localChartPanel.setPopupMenu(null);
	    localChartPanel.setDomainZoomable(true);
	    localChartPanel.setRangeZoomable(true);
	    localChartPanel.setPreferredSize(new Dimension(800, 600));
	    setContentPane(localChartPanel);
	    RefineryUtilities.positionFrameRandomly(this);
	    
  }
  public void updatePlotData(final double computerX,final double computerY,final double tagX,final double tagY)
  {
	 try
	 {
			  //tagSeries.add(tagX, tagY);
			  if(computerSeries.getItemCount()>1)
			  {
				  for(int j=0;j<computerSeries.getItemCount();j++)
				  {
					  oldComputerSeries.add(computerSeries.getX(j),computerSeries.getY(j));
					 
					  computerSeries.clear();
				  }
			  }
			  computerSeries.add(computerX, computerY);
	 }
	 catch(Exception e)
	 {
		 
	 }
  }


    private XYDataset createDataset() 
    {
	  //tagSeries = new XYSeries("Position from Tag"); 
	  computerSeries = new XYSeries("New Position from Computer");
	  oldComputerSeries =new XYSeries("Histroy Position from Computer");
	  XYSeriesCollection xyseriescollection = new XYSeriesCollection();  
	  Iterator<Entry<Integer,Point3D>> iter=Config.anchorPositionMap.entrySet().iterator();
	  while(iter.hasNext())
	  {
		  Entry<Integer,Point3D> entry=iter.next();
		  XYSeries series=new XYSeries("Anchor "+entry.getKey());
		  series.add(entry.getValue().getX(), entry.getValue().getY());
		  xyseriescollection.addSeries(series);
	  }
	             
	  //xyseriescollection.addSeries(tagSeries);
	  xyseriescollection.addSeries(computerSeries); 
	  xyseriescollection.addSeries(oldComputerSeries); 
	  return xyseriescollection;
	 }
}