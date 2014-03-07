@echo off
setlocal ENABLEDELAYEDEXPANSION

rem ***************************************************
rem See docs/Readme.txt for installation instructions.
rem Change this dir to be the location for J2SE edition
rem ***************************************************

@if defined JAVA_HOME goto javahomedefined
SET JAVA_HOME=C:/Program Files/Java/jdk1.7.0_45
:javahomedefined

rem set the path to include the bin dir
PATH=%JAVA_HOME%/bin;%PATH%

rem tools.jar is needed for the javac compiler
SET CLASSPATH=.;%JAVA_HOME%/jre/lib/rt.jar;%JAVA_HOME%/lib/tools.jar;ant/lib/*;

echo Path=%PATH%
echo ClassPath=%CLASSPATH%

rem If the config.properties does not exit in the app dir copy it from the config dir.
if not exist config.properties (
copy config\config.properties .
echo Using default config.properties from /config dir.)

SET ANT_BUILD_FILE=ant/build.xml
rem SET ANT_BUILD_FILE=ant/buildtest.xml

rem *******************************************************
rem After application install TARGET 1/ AND 2/ must be run.
rem *******************************************************

rem 1/ TARGET=all Build and compile the trademanager application.
rem 2/ TARGET=createDB Create the database user and default data. 
rem 3/ TARGET=upgradeDB Upgrade the database to the latest version see reamme.txt. 
rem 4/ TARGET=resetDefaultData Deletes all the data from the DB and reload the default data. 
rem 5/ TARGET=deleteTransactionData Deletes all the Contract/Candle/Tradestrategies from the database. Leaves configuration in tact. 
rem 6/ TARGET=deleteTradeOrderData Deletes all the orders from the database.
rem 7/ TARGET=deleteAccountRuleData Delete the accounts and rules from the database. These will reload on login.
rem 8/ TARGET=all ANT_BUILD_FILE=ant/buildtest.xml Build and compile all test cases. This depends on 1/  

SET TARGET=all

java -classpath "%CLASSPATH%"  org.apache.tools.ant.Main -buildfile "%ANT_BUILD_FILE%" "%TARGET%"

pause

