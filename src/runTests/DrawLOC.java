package runTests;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


import javax.swing.BorderFactory;
import javax.swing.JFrame;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.util.Random;
import java.util.Scanner;

public class DrawLOC extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int numeroSessioni;
	private static XYPlot plot;
	
	
	public DrawLOC(int num) {
		this.numeroSessioni = num;
    }

    public void initUI(XYDataset dataset) {

       // XYDataset dataset = createDataset(i, j, k);
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);
        
        add(chartPanel);

        pack();
        setTitle("Grafico LOC Coverage");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

       private JFreeChart createChart(final XYDataset dataset) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Testing copertura",
                "Numero di Cicli di Test",
                "LOC coverage %",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        DrawLOC.plot = chart.getXYPlot();

        var renderer = new XYLineAndShapeRenderer();
        
        //creo i colori random per le varie sessioni
        Random rand = new Random();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        for(int i=0; i<(this.numeroSessioni+1); i++) {
        	Color randomColor = new Color(r, g, b);
        	renderer.setSeriesPaint(i, randomColor);
            renderer.setSeriesStroke(i, new BasicStroke(2.0f));
            r = rand.nextFloat();
            g = rand.nextFloat();
            b = rand.nextFloat();
        }
    

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinesVisible(false);
        plot.setDomainGridlinesVisible(false);

        chart.getLegend().setFrame(BlockBorder.NONE);

        chart.setTitle(new TextTitle("Grafico Testing",
                        new Font("Serif", Font.BOLD, 18)
                )
        );

        return chart;
    }
    
    public static XYPlot getPlot() {
		return plot;
	}

	public static void setPlot(XYPlot plot) {
		DrawLOC.plot = plot;
	}

}