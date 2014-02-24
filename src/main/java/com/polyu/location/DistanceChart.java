package com.polyu.location;


import java.awt.BasicStroke;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;











import java.util.Map.Entry;

import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;


public class DistanceChart extends ApplicationFrame
{
 
  /**
	 * 
	 */
	
	private static final long serialVersionUID = -8381969478308372248L;
	private Map<Integer,TimeSeries> series=new HashMap<Integer,TimeSeries>();
  public DistanceChart(String paramString)
  {
    super(paramString);
  }
  public void init()
  {
	  setContentPane(createChartPanel());
	  RefineryUtilities.positionFrameRandomly(this);
  }
  public ChartPanel createChartPanel()
  {
	  Iterator<Integer> iter=Config.anchorPositionMap.keySet().iterator();
	  TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
	  while(iter.hasNext())
	  {
		  Integer id=iter.next();
		  TimeSeries tS=new TimeSeries("Anchor:"+id);
		  series.put(id, tS);
		  localTimeSeriesCollection.addSeries(tS);
	  }
	  
	  ChartPanel localChartPanel = new ChartPanel(createChart(localTimeSeriesCollection));
      localChartPanel.setPreferredSize(new Dimension(800, 600));
      return localChartPanel;
  }

  
  private JFreeChart createChart(XYDataset paramXYDataset)
  {
    JFreeChart localJFreeChart = ChartFactory.createTimeSeriesChart("Distance Data", "Time", "Value", paramXYDataset, true, true, false);
    XYPlot localXYPlot = (XYPlot)localJFreeChart.getPlot();
    int seriesCount = localXYPlot.getSeriesCount();
    for (int i = 0; i < seriesCount; i++) {
    	localXYPlot.getRenderer().setSeriesStroke(i, new BasicStroke(3));
    }
    ValueAxis localValueAxis = localXYPlot.getDomainAxis();
    localValueAxis.setAutoRange(true);
    localValueAxis.setFixedAutoRange(60000.0D);
    localValueAxis = localXYPlot.getRangeAxis();
    localValueAxis.setRange(0D, 20.0D);
    return localJFreeChart;
  }
  public void updatePlotData(final Map<Integer,Double> distanceData)
  {
	  Runnable updateTask=new Runnable()
	  {
		  public void run()
		  {
			  Iterator<Entry<Integer,Double>> iter=distanceData.entrySet().iterator();
			  while(iter.hasNext())
			  {
				  Entry<Integer,Double> singleData=iter.next();
				  series.get(singleData.getKey()).add(new Millisecond(),singleData.getValue());
			  }
		  }
	  };
	  SwingUtilities.invokeLater(updateTask);
  }
  


 
}
