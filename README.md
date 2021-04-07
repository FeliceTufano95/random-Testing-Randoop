# random-Testing-Randoop
Esecuzione di testing causuale mediante Randoop con valutazione della copertura raggiunta e test eseguiti

SCRIPT:
•	starter6Randoop: esso crea in modo iterativo dei cicli di test lanciando Randoop. Per ogni ciclo vengono prodotti, oltre agli eventuali  ErrorTest,  tutti i file relativi alla copertura della sessione al crescere dei cicli, e i file relativi all’unione delle coperture. In particolare il file “SessioncoverageLOC” che contiene le percentuali di LOC coperte dall’unione  dei cicli della sessione al crescere dei cicli, è quello che verrà utilizzato per ottenere una lettura in tempo reale dell’andamento. Questo script prende in ingresso i seguenti parametri: nome dell’applicazione da testare, quantità di tempo da dedicare ad ogni classe per la generazione dei test (ossia il time limit usato da Randoop per generare test), id del thread rappresentativo della sessione (che verrà utilizzato per distinguere le varie sessioni tra di loro). Questi parametri verranno passati allo script automaticamente dal programma scritto in Java; 
•	createCoverageUnion: calcola l’unione delle coperture raggiunte dalle varie sessioni al crescere dei cicli, attendendo ad  ogni ciclo la creazione degli n file “coverageUnion.es” da parte delle n sessioni e ne effettua il merge di volta in volta, i risultati prodotti verranno messi in una cartella chiamata “coverageunion”;
•	esecuzione_test: utilizzato per l’esecuzione dei test JUnit, crea un file txt per ogni esecuzione riportandone l’esito. In particolare il programma principale ad ogni ciclo controlla se è stato prodotto il relativo file “ErrorTest”, in termini pratici ciò equivale ad effettuare un controllo sulla data di modifica di questo file (questo perché ad ogni nuova creazione Randoop sovrascriverà il file ErrorTest.java), se la data è cambiata vuol dire che al ciclo corrente sono stati prodotti dei nuovi test e si può procedere con la loro esecuzione.



Il programma è stato sviluppato in un ambiante di esecuzione "JavaSE-13" e JDK 13.Per eseguirlo bisogna includere all'interno del build path del progetto i due
JAR "JCommon e JFreeChart" che servono per la generazione dei  grafici, entrambi situati all'interno della cartella "external_jars". 

Quando si testa un'applicazione bisogna assicurarsi è che ci sia una cartella con il suo stesso nome (nella folder generale), all'interno della quale ci siano tutti i file jar necessari all'esecuzione dei test JUnit. La classe che dovrà essere lanciata per l'esecuzione dell'applicativo è "Run.class", il sorgente è situato nella cartella src.

Per la documentazione dettagliata e un esempio di utilizzo completo, consultare il file "Random Testing Tufano.pdf".

