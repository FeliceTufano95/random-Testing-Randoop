# random-Testing-Randoop
Esecuzione di testing causuale mediante Randoop con valutazione della copertura raggiunta e test eseguiti

SCRIPT:
•	starter6Randoop: crea in modo iterativo dei cicli di test lanciando Randoop. 
	Per ogni ciclo vengono prodotti, oltre agli eventuali  ErrorTest,  tutti i file relativi alla copertura della sessione al crescere dei cicli,
 	e i file relativi all’unione delle coperture. In particolare il file “SessioncoverageLOC” che contiene le percentuali di LOC coperte dall’unione  
	dei cicli della sessione al crescere dei cicli, è quello che verrà utilizzato per ottenere una lettura in tempo reale dell’andamento. 
	Questo script prende in ingresso i seguenti parametri: nome dell’applicazione da testare, quantità di tempo da dedicare ad ogni classe per la
	generazione dei test, id del thread rappresentativo della sessione;
	Una volta lanciato produrrà una cartella per ogni sessione di test identificata dal nome dell'applicazione, dalla data, e dall'id del thread. In questa		
	cartella verranno generati i file sopra descritti, fatta eccezione del file SessioncoverageLOC che si troverà nella cartella generale per una maggiore
	praticità. 
	Per ogni sessione crea una cartella all'interno della quale verranno riportato gli ErrorTest prodotti, le classi che servono per eseguire i test,
	e lo script "esecuzione_test" per la loro esecuzione. 
•	createCoverageUnion: calcola l’unione delle coperture raggiunte dalle varie sessioni al crescere dei cicli, attendendo ad  ogni ciclo la creazione
	degli n file “coverageUnion.es” da parte delle n sessioni e ne effettua il merge di volta in volta. In particolare ogni sessione copia ad ogni ciclo
	il file relativo alla coverage dell'unione (aggiornata al ciclo corrente) all'interno della cartella "coverageunion", lo script ne effettua il merge e 
	produce tutti i risultati all'interno di questa cartella. Il file "SessioncoverageLOC_Union" verrà creato invece nella cartella generale, anche in questo caso
	per rendere la lettura più pratica;
•	esecuzione_test: utilizzato per l’esecuzione dei test JUnit, crea un file txt per ogni esecuzione riportandone l’esito. Questi file verranno letti dal
	programma Java per graficare l'andamento dei test, e generare il file contenente il report di tutti gli errori.


Il programma è stato sviluppato in un ambiante di esecuzione "JavaSE-13" e JDK 13.Per eseguirlo bisogna includere all'interno del build path del progetto i due
JAR "JCommon e JFreeChart" che servono per la generazione dei  grafici, entrambi situati all'interno della cartella "external_jars". 

Quando si testa un'applicazione bisogna assicurarsi è che ci sia una cartella con il suo stesso nome (nella folder generale), all'interno della quale ci siano tutti i file jar necessari all'esecuzione dei test JUnit.
