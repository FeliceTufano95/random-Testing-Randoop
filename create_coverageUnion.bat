@echo off
setlocal EnableDelayedExpansion
rem SCRIPT CHE SERVE A CALCOLARE L'UNIONE DELLA COPERTURA DI TUTTE LE SESSIONI A PARTIRE DAI RISPETTIVI FILE coverageunion.es
set num=%1


rem java -version
rem echo Setting JAVA_HOME
rem set JAVA_HOME=java.exe
rem echo setting PATH
rem set PATH=java.exe;%PATH%"
rem echo Display java version
rem java -version
REM percorso della cartella dove è ubicato il JAR di EMMA
set emmapath=".\emma-2.0.5312\lib\emma.jar"
set /a cycle=0

:START
REM fusione dei risultati ottenuti in un unico file per ottenere la copertura totale sulle varie sessioni
set /a cycle+=1
echo verifico che ci siano %num% file per il ciclo %cycle%

:VERIFY
set /a counterFile=0

rem attendo la creazione di num file per poterne fare l'unione

for /L %%i in (1,1,%num%) do (
	if exist coverageunion\coverageunion_Thread-%%i_ciclo_%cycle%.es (
		set /a counterFile+=1
		rem echo file trovati per l'unione: %counterFile%
	)
)

if not %counterFile%==%num% goto VERIFY

for /L %%i in (1,1,%num%) do (
	if exist coverageunion\coverageunion.es (
		".\java-1.8\jre1.8.0_281\bin\java.exe" -cp %emmapath% emma merge -in coverageunion\coverageunion_Thread-%%i_ciclo_%cycle%.es,coverageunion\coverageunion.es -out coverageunion\coverageunion.es
		REM echo esiste
	) else 	(
		".\java-1.8\jre1.8.0_281\bin\java.exe" -cp %emmapath% emma merge -in coverageunion\coverageunion_Thread-%%i_ciclo_%cycle%.es -out coverageunion\coverageunion.es
		REM echo non esiste
	)
)

REM genera il report della copertura totale degli eventi coverageunion

".\java-1.8\jre1.8.0_281\bin\java.exe" -cp %emmapath% emma report -r txt -Dreport.txt.out.file=coverageunion\coverageunion.txt -in coverageunion\coverageunion.es

REM salva su file di testo il numero di LOC coperte nell'unione totale di tutte le varie sessioni 
set /A count=0

for /F "skip=5 tokens=1-12 delims=(/)" %%a in (coverageunion\coverageunion.txt) do (
	set /A count+=1
    	if !count!==1 (
		 echo block =%%h/%%i; line =%%k/%%l>> coverageunion\unioncoverage.txt
		 echo LOC= %%j>> sessioncoverageLOC_Union.txt
		
	)
)

goto START
