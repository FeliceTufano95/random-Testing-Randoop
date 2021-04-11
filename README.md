# random-Testing-Randoop
Prima di poter eseguire l’applicazione bisogna assicurarsi che ci sia, tra i vari file, una cartella con lo stesso nome dell’applicazione che si intende testare, all'interno della quale devono essere presenti tutti i file jar da cui l’applicazione testata dipende. L'esistenza di tale cartella è necessaria per l'esecuzione dei test JUnit. Queste cartelle, nel caso delle due applicazioni testate "saxpath e jipa", sono già presenti. Per ogni applicazione in fase di test, nella cartella jars c'è anche un file di testo contenente l'elenco delle classi che devono essere prese in considerazione per la valutazione della copertura. 
Per l’esecuzione del programma è necessario avere sul proprio pc la jre 1.8, necessaria al corretto funzionamento di Emma. Per scaricarla è possibile utilizzare  il link: https://www.oracle.com/java/technologies/javase-jre8-downloads.html 

All’interno della cartella “src” è presente il package runTests all’interno del quale si trovano i file .java dell’applicazione, mentre nel package runTests
della cartella “bin” sono presenti i file .class.
Per eseguire l’applicazione ci sono due possibilità:

1.	Lanciare l’applicazione da linea di comando, dopo essersi posizionati all’interno della cartella generale che contiene tutti i file, 
	mediante il comando: java -cp .\external_jars\jcommon-1.0.23.jar;.\external_jars\jfreechart-1.0.19.jar;.\bin runTests.Run
2.	Lanciare l’applicazione mediante eseguibile jar, dopo essersi posizionati all’interno della cartella generale, mediante il comando: 
	java -jar run.jar

All’avvio verrà chiesto di inserire i seguenti parametri: percorso del file java.exe della jre 1.8  (esempio "C:\Program Files (x86)\Java\jre1.8.0_281\bin\java.exe"), numero di sessioni di test in parallelo, nome dell’applicazione testata e il time limit per la generazione dei test (cioè la quantità di tempo da dedicare ad ogni classe  dell’applicazione testata per la generazione dei test);

Il programma è stato sviluppato in un ambiante di esecuzione "JavaSE-13" e JDK 13, ma è stato testato anche per la versione 16.

Per una documentazione dettagliata e una spiegazione degli output ottenuti, consultare il file "Random Testing Tufano.pdf"

