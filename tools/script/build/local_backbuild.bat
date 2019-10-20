@echo off
setlocal ENABLEDELAYEDEXPANSION

for /F "eol=# delims=" %%j in ('type "..\local.properties"') do set %%j

set JAVA_HOME=%java.home%

call %maven.home%\bin\mvn.cmd -f %project.root%/pom.xml ^
 clean install -P "local, back-build"