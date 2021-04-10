# random-Testing-Randoop
Esecuzione di testing causuale mediante Randoop con valutazione della copertura raggiunta e test eseguiti

Prima di poter eseguire l’applicazione bisogna assicurarsi che ci sia, tra i vari file, una cartella con lo stesso nome dell’applicazione che si intende
testare, all'interno della quale devono essere presenti tutti i file jar da cui l’applicazione testata dipende. Per gli esempi riportati è stata effettuata questa operazione per l'applicazione "jipa". 

All’interno della cartella “src” è presente il package runTests all’interno del quale si trovano i file .java dell’applicazione, mentre nel package runTests
della cartella “bin” sono presenti i file .class.
Per eseguire l’applicazione ci sono tre possibilità:

1.	Importare l’intera cartella come progetto in Eclipse, aggiungendo i file jar “jcommon-1.0.23” e “jfreechart-1.0.19” che si trovano nella cartella
 	“external_jars” all’interno del Build Path di progetto. Dopodichè si può eseguire il tutto.
2.	Lanciare l’applicazione da linea di comando, dopo essersi posizionati all’interno della cartella generale che contiene tutti i file, 
	mediante il comando: java -cp .\external_jars\jcommon-1.0.23.jar;.\external_jars\jfreechart-1.0.19.jar;.\bin runTests.Run
3.	Lanciare l’applicazione mediante eseguibile jar, dopo essersi posizionati all’interno della cartella generale, mediante il comando: 
	java -jar run.jar


Il programma è stato sviluppato in un ambiante di esecuzione "JavaSE-13" e JDK 13, ma è stato testato anche per la versione 16.

Per una documentazione dettagliata consultare il file "Random Testing Tufano.pdf"

