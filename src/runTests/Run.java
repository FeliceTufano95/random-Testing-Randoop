package runTests;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.IOException;
import java.util.Scanner;

import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

public class Run {
	private static Scanner input;
	private static XYSeriesCollection datasetLOC = new XYSeriesCollection();
	private static XYSeriesCollection datasetErrorTest = new XYSeriesCollection(); //errorTest

	public static void main(String[] args) throws Exception {
		//System.out.print(System.getProperty("user.dir"));
		//System.out.println(Thread.currentThread().getName());
		ClasseCondivisa cc = new ClasseCondivisa();
		int num = 0;
		input = new Scanner(System.in); 
		System.out.println("inserisci il numero di sessioni di test in parallelo: ");
		num = input.nextInt();
		System.out.println("inserisci il nome dell'applicazione da testare: ");
		String nomeApplicazione = input.next();
		System.out.println("inserisci il time limit (in secondi): ");
		int timeLimit = input.nextInt();
		System.out.println("inserisci il path dove è situato la jre 1.8 (es. \"C:\\Program Files (x86)\\Java\\jre1.8.0_281\\bin\\java.exe\"");
		String javaPath = input.next();
		Produttore[] p = new Produttore[num];
		Consumatore c = new Consumatore(cc,num, javaPath);
		var ex = new DrawLOC(num);
		var ex1 = new DrawErrorTest(num);
		
		for(int i=0; i<num; i++) {
			p[i] = new Produttore(cc, nomeApplicazione, timeLimit, javaPath);
			p[i].start();
		}
		c.start();
		
		ex.initUI(getDatasetLOC());
     	ex.setVisible(true);
     	ex1.initUI(getDatasetErrorTest());
     	ex1.setVisible(true);
     
	}
	public static XYSeriesCollection getDatasetLOC() {
		return datasetLOC;
	}

	public static void setDatasetLOC(XYSeriesCollection dataset) {
		Run.datasetLOC = dataset;
	}

	public static XYSeriesCollection getDatasetErrorTest() {
		return datasetErrorTest;
	}

	public static void setDatasetErrorTest(XYSeriesCollection dataset1) {
		Run.datasetErrorTest = dataset1;
	}
		
		
}

