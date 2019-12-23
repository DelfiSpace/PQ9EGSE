/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.delfispace.pq9debugger.PQ9DataSocket.testSuites;

    import javax.swing.JFrame;
    import javax.swing.SwingUtilities;
    import javax.swing.WindowConstants;
    import org.jfree.chart.ChartFactory;
    import org.jfree.chart.ChartPanel;
    import org.jfree.chart.JFreeChart;
    import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author LocalAdmin
 */
public class Grapher extends JFrame 
{
  private final long serialVersionUID = 1L;
  private DefaultCategoryDataset dataset;
  JFreeChart chart;

  public Grapher(String title) {
    super(title);
    // Create dataset
    dataset = createDataset();
    // Create chart
    chart = ChartFactory.createLineChart(
        "Current flows", // Chart title
        "Capacity or Voltage", // X-Axis Label
        "time", // Y-Axis Label
        dataset
        );
    ChartPanel panel = new ChartPanel(chart);
    setContentPane(panel);
  }

  private DefaultCategoryDataset createDataset() {

    String series1 = "Voltage";
    String series2 = "Charge [mAh] EPS";  
    String series3 = "Charge [mAh] Calculated";  
    DefaultCategoryDataset datasetinit = new DefaultCategoryDataset();

    datasetinit.addValue(3, series1, "0");

    datasetinit.addValue(0, "jip", "20");
  
    return datasetinit;
  }
  
  public void AddDataToSet(int series, double value, int time)
  {
        //String series1 = "Voltage";
        String series2 = "Charge [mAh] EPS";  
        String series3 = "Charge [mAh] Calculated";  
        String timeS = String.valueOf(time);
        if(series == 1)
        {
         //   dataset.addValue(value, series1, timeS);
        }
        if(series == 2)
        {
            dataset.addValue(value, series2, timeS);
        }
          if(series == 3)
        {
            dataset.addValue(value, series2, timeS);
        }
    }
    public void UpdateChart()
    {
        chart = ChartFactory.createLineChart(
        "Current flows", // Chart title
        "Capacity or Voltage", // X-Axis Label
        "time", // Y-Axis Label
        dataset
        );
        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);
    }
  
}