rem change directory to the resources of testcore module
cd ../../../testcore/src/main/resources

rem make sure that previous temporary properties file is removed
if exist "generated\testdatabase-generated.properties" del "generated\testdatabase-generated.properties"

rem run groovy script responsible of starting the test database
start launch_test_postgresql12_container.groovy

rem check if new properties file was generated
:checkGenerated
if exist generated/testdatabase-generated.properties goto rebuildTestcore

rem wait for 5 seconds and check again
timeout /T 5 & goto checkGenerated

rem change directory to testcore module root and rebuild testcore module so the changes are visible
:rebuildTestcore
cd ../../../ & call mvn clean install -DskipTests

rem get back to the script location
cd ../tools/script/test