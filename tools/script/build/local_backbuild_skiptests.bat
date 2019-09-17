@echo off

rem change directory to the project root
cd ../../../

rem build project using maven
call mvn clean install -P "local, back-build" -DskipTests

rem get back to the script location
cd ../tools/script/test