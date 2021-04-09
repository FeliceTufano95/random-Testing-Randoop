@echo off
setlocal EnableDelayedExpansion
REM numero del ciclo corrente
set cycle=%1


javac -cp ..\external_jars\junit-4.13.2.jar;. ErrorTest.java
java -cp ..\external_jars\junit-4.13.2.jar;..\external_jars\hamcrest-core-1.3.jar;. org.junit.runner.JUnitCore ErrorTest >> EsecuzioneTestCiclo%cycle%.txt

exit
