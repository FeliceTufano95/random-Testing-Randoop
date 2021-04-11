@echo off
REM preimpostazione per la generazione di numeri random
setlocal EnableDelayedExpansion
REM nome dell'applicazione da testare
set name=%1
REM tempo da dedicare ad ogni classe
set timelimit=%2
REM nome del thread
set thread=%3
REM numero di sequenze
set sequences=1
REM numero di iterazioni
set iterations=1
REM valore di seed fisso
set seedvalue=!RANDOM!

rem ******* CAMBIO MOMENTANEAMENTE LA JAVA VERSION PER FAR FUNZIONARE EMMA*******
java -version
set java1.8_path=%4
echo JAVA 1.8:
rem VERIFICO CHE SIA STATA TROVATA LA JAVA 1.8
echo Display java version 
%java1.8_path% -version
REM percorso della cartella dove è ubicato il JAR di EMMA
set emmapath=".\emma-2.0.5312\lib\emma.jar"

set RANDOOP_PATH="."

REM imposta denominazione cartella con data e ora di avvio del test
for /f "tokens=1-6 delims=.,:/ " %%a in ("%date% %time%") do set mydatetime=%name%-%%c-%%b-%%a_%%d-%%e-%%f_%thread%

REM instrumentazione dell'archivio jar e creazione della cartella per contenere gli output
%java1.8_path% -noverify -cp %emmapath% emma instr -m fullcopy -d %mydatetime% -cp ".\jars\%name%.jar;.\jars\lib\*" -ix @myfilters.txt -out %mydatetime%\coverage.em
pause

set /a cycle=0
rem flag che mi serve per copiare i dati nella cartella dei test per la loro esecuzione
set /a flag=0 

:START
set /a cycle+=1

REM **********creazione del file contenente tutti i dati di test ottenuti per le sessioni di ogni ciclo *******
rem echo CICLO DI TEST %cycle% PER %name% DAL %thread%>>RisultatiCopertura_%thread%.txt  

REM imposta un valore random tra 0 e 32767 per il seed (se non impostato)
		
for /L %%N in (1,1,%sequences%) do (
	

	REM esegui la sessione e salva le coperture in un file .ec
	REM java -noverify -cp "%mydatetime%\lib\%name%.jar";%emmapath%;ripper.jar ripper.RipperApplicationTest %mydatetime%\lib\%name%.jar 1 %iterations% 0 !seedvalue!
		
	%java1.8_path% -noverify -cp "%mydatetime%\lib\%name%.jar";%emmapath%;randoop-all-3.0.6.jar randoop.main.Main gentests --classlist=jars\%name%.txt --timelimit=%timelimit% --randomseed=!RANDOM! --no-regression-tests=true --junit-output-dir=ErrorTest_%thread%
	
	rem se la cartella per contenere i test è stata creata copia al suo interno i file che servono per la loro esecuzione
	if exist ErrorTest_%thread% (
		if !flag!==0 (
			xcopy %name% ErrorTest_%thread%/E
			xcopy "esecuzione_test.bat" ErrorTest_%thread%
			rem xcopy hamcrest-core-1.3.jar ErrorTest_%thread%
			rem xcopy junit-4.13.2.jar ErrorTest_%thread%
			set /a flag+=1	
		)
	)
	copy coverage.ec %mydatetime%\coverage%cycle%-%thread%-%%N.ec
	del coverage.ec
		
	REM genera il report testuale effettuato il merge con i metadati del file .em
	%java1.8_path% -cp %emmapath% emma report -r txt -Dreport.txt.out.file=%mydatetime%\coverage%cycle%-%thread%-%%N.txt -in %mydatetime%\coverage.em,%mydatetime%\coverage%cycle%-%thread%-%%N.ec
		

	REM fondi i file .em e .ec in un unico file .es
	%java1.8_path% -cp %emmapath% emma merge -in %mydatetime%\coverage.em,%mydatetime%\coverage%cycle%-%thread%-%%N.ec -out %mydatetime%\coverage%cycle%-%thread%-%%N.es
			
	REM fusione dei risultati ottenuti in un unico file per ottenere la copertura degli eventi per la sessione corrente
	if exist %mydatetime%\coverageunion%cycle%-%thread%.es 		(
		%java1.8_path% -cp %emmapath% emma merge -in %mydatetime%\coverage%cycle%-%thread%-%%N.es,%mydatetime%\coverageunion%cycle%-%thread%.es -out %mydatetime%\coverageunion%cycle%-%thread%.es
	) else 		(
		%java1.8_path% -cp %emmapath% emma merge -in %mydatetime%\coverage%cycle%-%thread%-%%N.es -out %mydatetime%\coverageunion%cycle%-%thread%.es
	)
		
		

)

REM coverageunion%thread% e' la copertura dell'unione delle sequenze sulla sessione fino all'ultimo ciclo
REM per non perdere questo valore (poichè ad ogni ciclo effettuo una nuova unione), ne faccio una copia in cumulativecoverageunion%cycle%-thread%
	
REM fusione dei risultati ottenuti in un unico file per ogni sessione per ottenere la copertura totale di ogni sessione
if exist %mydatetime%\coverageunion%thread%.es (
	%java1.8_path% -cp %emmapath% emma merge -in %mydatetime%\coverageunion%cycle%-%thread%.es,%mydatetime%\coverageunion%thread%.es -out %mydatetime%\coverageunion%thread%.es
	REM echo esiste
) else 		(
	%java1.8_path% -cp %emmapath% emma merge -in %mydatetime%\coverageunion%cycle%-%thread%.es -out %mydatetime%\coverageunion%thread%.es
	REM echo non esiste
)
copy %mydatetime%\coverageunion%thread%.es %mydatetime%\cumulativecoverageunion%cycle%-%thread%.es
		
REM genera il report della copertura degli eventi
REM per output in HTML: -r html -Dreport.html.out.file=%mydatetime%\coverageunion\index.html
%java1.8_path% -cp %emmapath% emma report -r txt -Dreport.txt.out.file=%mydatetime%\unioneSequenzeDellaSessione_%thread%_perCicliCrescenti.txt -in %mydatetime%\coverageunion%thread%.es
copy %mydatetime%\unioneSequenzeDellaSessione_%thread%_perCicliCrescenti.txt %mydatetime%\cumulativecoverageunion%cycle%-%thread%.txt
		
		
REM salva su file di testo il numero di LOC coperte nell'unione delle sessioni 
set /A count=0
for /F "skip=5 tokens=1-12 delims=(/)" %%a in (%mydatetime%\unioneSequenzeDellaSessione_%thread%_perCicliCrescenti.txt) do (
	set /A count+=1
	if !count!==1 (
	@echo ciclo= %cycle%; sessione = %thread%; block_coperti: %%h/%%i ;LOC: %%k/%%l>> %mydatetime%\sessioncoverage.txt
	@echo LOC= %%j>>sessioncoverageLOC_%thread%.txt
	)
	REM %%g e' il numero di block; %%k e' il numero di lock
)
	
	
REM genera il report della copertura degli eventi
REM per output in HTML: -r html -Dreport.html.out.file=%mydatetime%\coverageunion\index.html
%java1.8_path% -cp %emmapath% emma report -r txt -Dreport.txt.out.file=%mydatetime%\coverageunion%cycle%-%thread%.txt -in %mydatetime%\coverageunion%cycle%-%thread%.es


REM fusione dei risultati ottenuti in un unico file per ottenere la copertura totale sulle varie sessioni
if exist %mydatetime%\coverageunion.es (
	%java1.8_path% -cp %emmapath% emma merge -in %mydatetime%\coverageunion%cycle%-%thread%.es,%mydatetime%\coverageunion.es -out %mydatetime%\coverageunion.es
	REM echo esiste
) else 	(
	%java1.8_path% -cp %emmapath% emma merge -in %mydatetime%\coverageunion%cycle%-%thread%.es -out %mydatetime%\coverageunion.es
	REM echo non esiste
)

REM COPIO IL FILE coverageunion.es DELLA SESSIONE CORRENTE NELLA CARTELLA COVERAGE_UNION CHE CONTERRA' L'UNIONE DI TUTTE LE SESSIONI
if not exist coverageunion mkdir coverageunion
copy %mydatetime%\coverageunion.es coverageunion\coverageunion_%thread%_ciclo_%cycle%.es /Y
	


REM genera il report della copertura totale degli eventi coverageunion DOVREBBE ESSERE L'UNIONE TOTALE
REM per output in HTML: -r html -Dreport.html.out.file=%mydatetime%\coverageunion\index.html
%java1.8_path% -cp %emmapath% emma report -r txt -Dreport.txt.out.file=%mydatetime%\coverageunion.txt -in %mydatetime%\coverageunion.es

REM salva su file di testo il numero di LOC coperte nell'unione totale di tutte le varie sessioni MA in questo caso perde di significato
	set /A count=0
	
	
	for /F "skip=5 tokens=1-12 delims=(/)" %%a in (%mydatetime%\coverageunion.txt) do (
	set /A count+=1
    if !count!==1 @echo %cycle%;U;=%%h/%%i;=%%k/%%l >> %mydatetime%\unioncoverage.txt
	)

REM mostra il contenuto di loccoverage.txt
rem type %mydatetime%\loccoverage.txt

REM pause
goto START
