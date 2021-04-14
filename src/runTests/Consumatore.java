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

import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

public class Consumatore extends Thread{
	private ClasseCondivisa c;
	private int numSessioni;
	private String javaPath; //percorso della jre 1.8
	private ArrayList<Double> arrayLOCunion; //conterrà i valori di LOC della sessione
	private String currentDate;
	private String nomeApplicazione;
	
	
	public Consumatore(ClasseCondivisa c, int num, String javaPath, String currentDate, String nomeApplicazione) {
		this.c = c;
		this.numSessioni=num;
		this.arrayLOCunion = new ArrayList<>();
		this.javaPath = javaPath;
		this.currentDate = currentDate;
		this.nomeApplicazione = nomeApplicazione;
	}
	
	public ArrayList<Double> getarrayLOCunion(){
		return this.arrayLOCunion;
	}
	
	private boolean reachedCoverageAEQ(ArrayList<Double> array, int indice) {
		 boolean equal = true;
		 //se il numero di serie create è minore di num+1 non posso ancora fare il controllo
		 if(Run.getDatasetLOC().getSeriesCount() < (this.numSessioni+1)) {
			 return false;
		 }
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
			 	marker.setLabel(" T-AEQ");
			 
			 	marker.setLabelAnchor(RectangleAnchor.BOTTOM_LEFT);
			 	marker.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
			 	marker.setLabelPaint(Color.BLUE);
			 	DrawLOC.getPlot().addAnnotation(line);
			 	DrawLOC.getPlot().addDomainMarker(marker);
			 }
		 return equal;
	 }
	public void run() {
		String outputdir = this.nomeApplicazione+"-"+this.currentDate;
		try {
			Runtime.getRuntime().exec("cmd /c start \"\" create_coverageUnion.bat "+" "+this.numSessioni+" "+this.javaPath+" "+this.currentDate+" "+this.nomeApplicazione);
		} catch (IOException e) {
			System.out.println("ECCEZIONE SULLA ESECUZIONE DEL .bat");
			e.printStackTrace();
		}
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); //per convertire l'ultima modifica del file nel formato MM/dd/yyyy HH:mm:ss
		File f = new File(".\\"+outputdir+"\\sessioncoverageLOC_Union.txt");
		
		while(!f.exists());
		
		//creo la curva sul grafico corrispondente alla sessione 
		XYSeries series = new XYSeries("Unione_Sessioni");
		Run.getDatasetLOC().addSeries(series);
		
		String lastModify = sdf.format(f.lastModified());
		
		int elem = 0;
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
				
				ArrayList<Double> vettoreAppoggio = new ArrayList<>();
				Double locCoperti = 0.0;
				Double locTotali = 0.0; 
				String stringaAppoggio = "";
				
				while(linea == null) {
					try {
						linea = re.readLine();
						stringaAppoggio = linea;
						stringaAppoggio = stringaAppoggio.replace(",", ".");
						locCoperti = Double.parseDouble((String) stringaAppoggio.subSequence(stringaAppoggio.indexOf('=')+1, stringaAppoggio.indexOf('/')));
						locTotali = Double.parseDouble((String) stringaAppoggio.substring(stringaAppoggio.indexOf('/')+1));
						vettoreAppoggio.add((locCoperti/locTotali)*100);
						//System.out.println("coverage unione -> "+locCoperti+"/"+locTotali+"="+vettoreAppoggio.get(vettoreAppoggio.size()-1)+"%");
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
						if(linea!=null) {
							stringaAppoggio = linea;
							stringaAppoggio = stringaAppoggio.replace(",", ".");
							locCoperti = Double.parseDouble((String) stringaAppoggio.subSequence(stringaAppoggio.indexOf('=')+1, stringaAppoggio.indexOf('/')));
							locTotali = Double.parseDouble((String) stringaAppoggio.substring(stringaAppoggio.indexOf('/')+1));
							vettoreAppoggio.add((locCoperti/locTotali)*100);
							//System.out.println("coverage unione-> "+locCoperti+"/"+locTotali+"="+vettoreAppoggio.get(vettoreAppoggio.size()-1)+"%");
						}
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
		
				/*
				testo=testo.replace("%", "%%");
				Pattern p = Pattern.compile("\\d+");
				Matcher m = p.matcher(testo);*/
				
				elem = vettoreAppoggio.size();
				for (int i=0; i<elem; i++) {
					if(i >= cicli) {
						this.arrayLOCunion.add(vettoreAppoggio.get(i));
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
