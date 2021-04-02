package runTests;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

public class Consumatore extends Thread{
	private ClasseCondivisa c;
	private int numSessioni;
	private ArrayList<Integer> arrayLOCunion; //conterrà i valori di LOC della sessione
	
	
	public Consumatore(ClasseCondivisa c, int num) {
		this.c = c;
		this.numSessioni=num;
		this.arrayLOCunion = new ArrayList<>();
	}
	
	public ArrayList<Integer> getarrayLOCunion(){
		return this.arrayLOCunion;
	}
	
	private boolean reachedCoverageAEQ(ArrayList<Integer> array, int indice) {
		 boolean equal = true;
		 for(int i=1; i<=this.numSessioni; i++) {
			 if(Run.getDatasetLOC().getSeries("Thread-"+i).getItemCount()<(indice+1))
				 return false;
		 }
		 
		 for(int j=1; (j+1)<=this.numSessioni; j++) {
			 if(!(Run.getDatasetLOC().getSeries("Thread-"+j).getDataItem(indice).equals(Run.getDatasetLOC().getSeries("Thread-"+(j+1)).getDataItem(indice))))
				equal = false;
			 
		 }
		 if(equal) {
				XYLineAnnotation line = new XYLineAnnotation(indice, 0, indice, array.get(indice), new BasicStroke(1.0f), Color.BLACK);
			 		
			 	//etichetta della linea
			 	ValueMarker marker = new ValueMarker(indice, Color.white, new BasicStroke(1.0f));  // position is the value on the axis
			 	marker.setLabel("T-AEQ");
			 
			 	marker.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
			 	marker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
			 	marker.setLabelPaint(Color.BLACK);
			 	DrawLOC.getPlot().addAnnotation(line);
			 	DrawLOC.getPlot().addDomainMarker(marker);
			 }
		 return equal;
	 }
	public void run() {
		try {
			Runtime.getRuntime().exec("cmd /c start \"\" create_coverageUnion.bat "+" "+this.numSessioni);
		} catch (IOException e) {
			System.out.println("ECCEZIONE SULLA ESECUZIONE DEL .bat");
			e.printStackTrace();
		}
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); //per convertire l'ultima modifica del file nel formato MM/dd/yyyy HH:mm:ss
		File f = new File("sessioncoverageLOC_Union.txt");
		
		while(!f.exists());
		
		//creo la curva sul grafico corrispondente alla sessione 
		XYSeries series = new XYSeries("Unione_Sessioni");
		Run.getDatasetLOC().addSeries(series);
		
		String lastModify = sdf.format(f.lastModified());
		
		
		int cicli = 0;
		while(true) {
			if (!(lastModify.equals(sdf.format(f.lastModified())))) {
				
				lastModify = sdf.format(f.lastModified());
				FileReader fe = null;
				try {
					fe = new FileReader(f);
				} catch (FileNotFoundException e2) {
					System.out.println("ECCEZIONE SULLA ISTANZIAZIONE DELLA FILE READER DEL PRODUTTORE PER LEGGERE I FILE GENERATI DAGLI SCRIPT");
					e2.printStackTrace();
				}
				BufferedReader re = new BufferedReader (fe);
				String linea = null;
				
				while(linea == null) {
					try {
						linea = re.readLine();
					} catch (IOException e1) {
						System.out.println("ECCEZIONE SULLA PRIMA READLINE DEL PRODUTTORE");
						e1.printStackTrace();
					}
				}
		
		
				String testo = "";
				while (linea != null){ 
					try {
						testo+=linea+"\n";
						linea = re.readLine();
					} catch (IOException e) {
						System.out.println("ECCEZIONE SULLA SECONDA READLINE DEL PRODUTTORE");
						e.printStackTrace();
					}
				}
		
			
				try {
					re.close();
				} catch (IOException e) {
					System.out.println("ECCEZIONE SULLA CHIUSURA DELL'OGGETTO BUFFER READER DEL PRODUTTORE");
					e.printStackTrace();
				}
		
				testo=testo.replace("%", "%%");
				Pattern p = Pattern.compile("\\d+");
				Matcher m = p.matcher(testo);
				
				for (int i=0; m.find(); i++) {
					if(i >= cicli) {
						this.arrayLOCunion.add(Integer.parseInt(m.group()));
						series.add(i, this.arrayLOCunion.get(i));
					}
				}
				
				cicli = this.arrayLOCunion.size();
				reachedCoverageAEQ(this.arrayLOCunion, cicli-1);
			
			}
			
			
			c.letturaFileCondiviso();
		}
	
	}
}
