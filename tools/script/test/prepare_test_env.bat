@echo off
setlocal ENABLEDELAYEDEXPANSION

for /F "eol=# delims=" %%j in ('type "..\local.properties"') do set %%j

set testcore.resources.dir=%project.root%\testcore\src\main\resources
set generated.properties.file=%testcore.resources.dir%\generated\testdatabase-generated.properties

if exist %generated.properties.file% del %generated.properties.file%

set JAVA_HOME=%java.home%

start %groovy.home%\bin\groovy.bat %testcore.resources.dir%\launch_test_postgresql12_container.groovy

:checkGenerated
if exist %generated.properties.file% goto rebuildTestcore

timeout /T 5 & goto checkGenerated

:rebuildTestcore
call %maven.home%\bin\mvn.cmd -f %project.root%/testcore/pom.xml clean install -DskipTests