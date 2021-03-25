package runTests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClasseCondivisa{
	private boolean available = false;
	
	
	 public synchronized void leggi() {
	      while (available == false) {
	         try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println("ECCEZIONE SULLA WAIT DEL CONSUMATORE");
				e.printStackTrace();
			}
	      }
	    //********LETTURA  FILE********
		File f = new File("LetturaCoverage.txt");
		if ( !f.exists() )
		{ System.out.println("il txt non esiste!");
		return;
		}	
		FileReader fe = null;
		try {
			fe = new FileReader(f);
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			System.out.println("ECCEZIONE SULLA CREAZIONE DELL'OGGETTO FILEREADER");
			e2.printStackTrace();
		}
		BufferedReader re = new BufferedReader (fe);
		String linea = null;
		try {
			linea = re.readLine();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("ECCEZIONE SULLA READLINE DEL CONSUMATORE");
			e1.printStackTrace();
		}
		
		while (linea != null){ 
			try {
				System.out.println("lettura: "+linea);
				linea = re.readLine();
				

			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("ECCEZIONE SULLA SECONDA READLINE DEL CONSUMATORE");
				e.printStackTrace();
			}
		}
		
			
	    try {
			re.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ECCEZIONE SULLA CHIUSURA DELL'OGGETTO BUFFERREADER DEL CONSUMATORE");
			e.printStackTrace();
		}
	    available = false;
	    notifyAll(); //sveglio i produttori
	  }
	   
	   public synchronized void scritturaFileCondiviso(String s, String produttore){
		   while (available == true) {
		         try {
		            wait();
		         }
		         catch (InterruptedException e) { 
		        	 System.out.println("ECCEZIONE SULLA WAIT DEL PRODUTTORE");
		        	 e.printStackTrace();
		         } 
		      }
		   //*****SCRITTURA SU FILE*****
		   File f1 = new File("LetturaCoverage.txt");
		   
		   FileWriter fw = null;
		   try {
			   fw = new FileWriter(f1, false); //mettendo true consento la scrittura append
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("ECCEZIONE SULLA CREAZIONE DI FILE WRITER DEL PRODUTTORE");
				e.printStackTrace();
			}
			PrintWriter pw = new PrintWriter(fw);
			//System.out.println("vado a scrivere "+n+" sul file condiviso");
			pw.printf("Unione della sessione "+produttore+" al crescere dei cicli \n"+s+"\n");
			pw.close();
			available = true;
		    notifyAll(); //sveglio i consumatori
		    //return;
	   }
	
	
}
