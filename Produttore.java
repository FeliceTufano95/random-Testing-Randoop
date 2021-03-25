package runTests;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

public class Produttore  extends Thread{
	private ClasseCondivisa c;
	private ArrayList<Integer> arrayLOC; //conterrà i valori di LOC della sessione
	
	public Produttore(ClasseCondivisa c) {
		this.c = c;
		this.arrayLOC = new ArrayList<>();
	}
	public ArrayList<Integer> getarrayLOC(){
		return this.arrayLOC;
	}
	
	public void setarrayLOC(){
		this.arrayLOC.add(100, 100);
	}
	private void incrementValue(Map<String,Integer> map, String key)
    {
        Integer count = map.get(key);
        //se non c'è nessun errore nella lista lo inserico con conteggio 1
         if (count == null) {
            map.put(key, 1);
        }
        //altrimenti aggiungo 1 alla lista corrispondente
        else {
            map.put(key, count + 1);
        }
    }
	
	 //conta quanti file Esecuzione test ci sono nella cartella 
	 private  int counterFile(){
		Pattern p = Pattern.compile("EsecuzioneTestCiclo\\d+[.]txt");
		int counter =0;
        File dir = new File(".\\ErrorTest_"+getName());
        if(!dir.exists())
        	return counter;
        
        File[] files = dir.listFiles();
        Arrays.sort(files); //ordino i file in ordine alfabetico

       
        for (File f1 : files) {
            Matcher m = p.matcher(f1.getName());
            if (m.find()) {
               counter++;
            }
        }
        return counter;
	 }
	 
	 //per il risultato dell'esecuzione dei test mi prendo gli errori prodotti e aggiorno la lista
	 private int aggiornaMap(Map<String, Integer> map,int i, File f) {
		 //System.out.println("lancio il file Errotest "+i);
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
					//System.out.println(linea);
				} catch (IOException e1) {
					System.out.println("ECCEZIONE SULLA PRIMA READLINE DEL PRODUTTORE");
					e1.printStackTrace();
				}
			}
	
			boolean takeNext = false;
	
			String ultimaLinea = ""; //mi servirà per prendere l'ultima linea letta cioè quella con i test eseguiti
			//String testo = "";
			while (linea != null){ 
				try {
					if(linea.startsWith("Tests run:")||linea.startsWith("OK ("))
						ultimaLinea=linea;
					linea = re.readLine();					
					if(takeNext &&  linea!= null) {
						//System.out.println(linea);
						incrementValue(map, linea);
						takeNext = false;
					}
					//se la linea inizia con un numero seguito da ) vuol dire che è il fallimento di un test (suppongo non più di 99 test)
					if(linea!= null && linea.matches("^[0-9].*$") && ((linea.indexOf(")")==1||linea.indexOf(")")==2)))
						takeNext = true;
					
					//AGGIUNGIU OGNI MESSAGGIO DI ERRORE ALL'HASHMAP myMap.merge(key, 1, Integer::sum) GESTEDO GLI ERRORI UGUALI E NON 
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
			
			//dall'ultima linea mi prendo il numero di test eseguiti
			Pattern p = Pattern.compile("\\d+");
			Matcher m = p.matcher(ultimaLinea);
			int testEseguiti=0;
			if(m.find())
				testEseguiti=Integer.parseInt(m.group());
			
			return testEseguiti;
	 }
	 
	 //verrà chiamata quando verrà raggiunto il 60% di coverage
	 private void reachedCoverageT60(int indice, int coverage) {
		XYLineAnnotation line = new XYLineAnnotation(indice, 0, indice, coverage, new BasicStroke(1.0f), Color.BLACK);
 		
 		//etichetta della linea
 		ValueMarker marker = new ValueMarker(indice, Color.white, new BasicStroke(1.0f));  // position is the value on the axis
 		marker.setLabel(getName()+" t60%"); // see JavaDoc for labels, colors, strokes
 		marker.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
 		marker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
 		marker.setLabelPaint(Color.BLACK);
 		DrawLOC.getPlot().addAnnotation(line);
 		DrawLOC.getPlot().addDomainMarker(marker);
		 
	 }
	 
	 private boolean reachedCoverageT50(int indice, ArrayList<Integer> array) {
		 boolean equal = true;
		 for(int i=(indice/2)+1; i<indice && equal; i++) {
			 if(!array.get(i).equals(array.get(i+1))) {
				 equal = false;
			 }
		 }
		 if(equal) {
			XYLineAnnotation line = new XYLineAnnotation(indice, 0, indice, array.get(indice), new BasicStroke(1.0f), Color.BLACK);
			 		
		 	//etichetta della linea
		 	ValueMarker marker = new ValueMarker(indice, Color.white, new BasicStroke(1.0f));  // position is the value on the axis
		 	marker.setLabel(getName()+" t50%"); // see JavaDoc for labels, colors, strokes
		 	marker.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
		 	marker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
		 	marker.setLabelPaint(Color.BLACK);
		 	DrawLOC.getPlot().addAnnotation(line);
		 	DrawLOC.getPlot().addDomainMarker(marker);
		 }
		 return equal;
				 
	  }
	 
	 private int reachedCoverageAIO(ArrayList<Integer> array, int indice) {
		 //XYDataItem lastValue = new XYDataItem(0, 0);
		 //parto dal ciclo 1
		 int i=0;
		 for(; i<Run.getDatasetLOC().getSeries("Unione_Sessioni").getItemCount(); i++) {
			 if(Run.getDatasetLOC().getSeries(getName()).getDataItem(i).equals(Run.getDatasetLOC().getSeries("Unione_Sessioni").getDataItem(i))) {
				if(i>=indice) {
					 XYLineAnnotation line = new XYLineAnnotation(i, 0, i, array.get(i), new BasicStroke(1.0f), Color.BLACK);
			 		
					 //etichetta della linea
					 ValueMarker marker = new ValueMarker(i, Color.white, new BasicStroke(1.0f));  // position is the value on the axis
					 marker.setLabel(getName()+" T-AIO");
				 
					 marker.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
					 marker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
					 marker.setLabelPaint(Color.BLACK);
					 DrawLOC.getPlot().addAnnotation(line);
					 DrawLOC.getPlot().addDomainMarker(marker);
					 //lastValue = Run.getDatasetLOC().getSeries(getName()).getDataItem(i);
				}
			 }
				 
		 }
		 return i;
	 }
	 
	public void run() {
		try {
			Runtime.getRuntime().exec("cmd /c start \"\" starter6Randoop.bat "+" saxpath"+" 20"+" "+getName());
		} catch (IOException e) {
			System.out.println("ECCEZIONE SULLA ESECUZIONE DEL .bat");
			e.printStackTrace();
		}
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); //per convertire l'ultima modifica del file nel formato MM/dd/yyyy HH:mm:ss
		File f = new File("sessioncoverageLOC_"+getName()+".txt");
		File fileEsecuzioneTest = new File(".\\ErrorTest_"+getName()+"\\esecuzione_test.bat");
		
		//mi serve definire il file ErrorTest per controllare di non eseguire due volte lo stesso
		SimpleDateFormat sdfErrorTest = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); 
		File errorTestFile =  new File(".\\ErrorTest_"+getName()+"\\ErrorTest.java");
		
		//la prima volta attendo la creazione del file da parte dello script per le successive controllo che la data di modifica sia cambiata
		while(!f.exists());
		
		String lastModify = sdf.format(f.lastModified());
		String errorTestLastModify = sdfErrorTest.format(errorTestFile.lastModified()); //ultima modifica al file ErrorTest.java
		boolean primaEsecuzioneTest = true;
		
		int cicli = 0;
		
		//VARIABILI RELATIVE ALL'ESECUZIONE DEI TEST JUNIT
		 Map<String, Integer> mapErrori = new HashMap<String, Integer>(); //conterrà la lista degli errori: errore->occorrenza
         
         int fileEsecuzioneLetti=0; //contatore dei file contententi i risultati di una esecuizione di ErrorTest.java letti
         int cicloEsecuzioneTest=0; 
         int testEseguiti =0;
         int testFalliti = 0;
         
		//creo la curva sul grafico corrispondente alla sessione 
		XYSeries seriesLOC = new XYSeries(getName());
		XYSeries seriesError = new XYSeries(getName());
		
		Run.getDatasetLOC().addSeries(seriesLOC);
		Run.getDatasetErrorTest().addSeries(seriesError);
		
		boolean reachedT60 = false;
		boolean reachedT50 = false;
		int lastIndexAIO = 0;
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
				
				//attendo che il file venga scritto dallo script (la prima volta), considerarne l'utilizzo visto che viene confrontato l'orario
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
						//System.out.println("produttore, ho letto= "+linea+" e lo vado a scrivere");
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
				//dalla stringa in ingresso mi prendo tutte le percentuali numeriche e le plotto
				Pattern p = Pattern.compile("\\d+");
				Matcher m = p.matcher(testo);
				
				for (int i=0; m.find(); i++) {
					if(i >= cicli) {
						this.arrayLOC.add(Integer.parseInt(m.group()));
						//System.out.println(getName()+"array["+i+"]= "+arrayLOC.get(i));
						seriesLOC.add(i, this.arrayLOC.get(i));
						
						//T60
						if(this.arrayLOC.get(i)>=60 && !reachedT60) {
							reachedCoverageT60(i, this.arrayLOC.get(i));
		            		reachedT60 = true;
						}
						//T50
						if(!reachedT50 && cicli>4)
								reachedT50 = reachedCoverageT50(i, this.arrayLOC);
						
						//AIO
						//controllo bruttissimo per assicurarmi che sia stata creata la serie Unione_Sessioni
						if(cicli>4) {
							if(!Run.getDatasetLOC().getSeries("Unione_Sessioni").isEmpty()) {
								System.out.println("chiamo AIO su indice: "+lastIndexAIO);
								lastIndexAIO= reachedCoverageAIO(this.arrayLOC, lastIndexAIO);
								System.out.println("nuovo indice: "+lastIndexAIO);
							}
						}
					}
				}
				
				cicli = this.arrayLOC.size();
				
				//L'ASSUNZIONE CHE FACCIO E' CHE CI SIA CORRISPONDENZA TEMPORALE TRA GLI ERRORTEST PRODTTI I DATI DI COPERTURA
				//se esiste la cartella ErroTest corrispondente e se il file ErrorTest è stato modificato, vado a eseguire i test
				
				if(primaEsecuzioneTest) {
					if(fileEsecuzioneTest.exists()) {
	    				errorTestLastModify = sdfErrorTest.format(errorTestFile.lastModified());
						try {
		    				Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"cd ErrorTest_"+getName()+" && esecuzione_test.bat\""+" "+cicli);
		    				primaEsecuzioneTest = false;
		    			} catch (IOException e) {
		    				System.out.println("ECCEZIONE SULLA PRIMA ESECUZIONE DEL .bat di esecuzione dei test");
		    				e.printStackTrace();
		    			}
					}
				}else if(!(errorTestLastModify.equals(sdfErrorTest.format(errorTestFile.lastModified())))) {
    				errorTestLastModify = sdfErrorTest.format(errorTestFile.lastModified());
					try {
	    				Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"cd ErrorTest_"+getName()+" && esecuzione_test.bat\""+" "+cicli);
	    				
	    			} catch (IOException e) {
	    				System.out.println("ECCEZIONE SULLA ESECUZIONE DEL .bat di esecuzione dei test");
	    				e.printStackTrace();
	    			}
	            
	    		}
				
				int count = counterFile();
				if(fileEsecuzioneLetti<count) {
					while(fileEsecuzioneLetti<count) {
						File fileRisultatiEsecuzioneTest = new File(".\\ErrorTest_"+getName()+"\\EsecuzioneTestCiclo"+cicloEsecuzioneTest+".txt");
						if(fileRisultatiEsecuzioneTest.exists()) {
							testEseguiti+=aggiornaMap(mapErrori, cicloEsecuzioneTest, fileRisultatiEsecuzioneTest);
							fileEsecuzioneLetti++;
						}
						cicloEsecuzioneTest++;
					}
					//calcolo del numero di test falliti
					testFalliti =0;
					for (Map.Entry<String, Integer> entry : mapErrori.entrySet()) {
						testFalliti+= entry.getValue();
					}
					
					 File fileReport = new File(".\\ErrorTest_"+getName()+"\\reportErrori.txt");
					   
					 FileWriter fw = null;
					 try {
						  fw = new FileWriter(fileReport, false); //mettendo true consento la scrittura append
					} catch (IOException e) {
					// TODO Auto-generated catch block
						System.out.println("ECCEZIONE SULLA CREAZIONE DI FILE WRITER DEL PRODUTTORE");
						e.printStackTrace();
					}
					PrintWriter pw = new PrintWriter(fw);
					for (Map.Entry<String, Integer> entry : mapErrori.entrySet()) {
						pw.printf(entry.getKey() + "  OCCORRENZA -> " + entry.getValue()+"\n");	
					}
					
					pw.close();
				
					//DEVI AGGIORNARE IL GRAFICO SOLO SE C'è STATO UN CAMBIAMENTO ALTRIMETNI STAMPA SEMPRE LO STESSO
					seriesError.add(testEseguiti, testFalliti);
				}
				c.scritturaFileCondiviso(testo, getName());
				/*
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.out.println("ECCEZIONE SULLA SLEEP DEL PRODUTTORE");
					e.printStackTrace();
				}*/
				
			}
		}
	
	}
	
}
