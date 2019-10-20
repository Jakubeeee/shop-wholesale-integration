import com.jakubeeee.testcore.CustomPostgreSQL12Container
import groovy.transform.Field
import groovy.transform.SourceURI
import org.springframework.util.DefaultPropertiesPersister
import org.testcontainers.containers.JdbcDatabaseContainer

import java.nio.file.Paths

@Grab(group = 'com.jakubeeee', module = 'testcore', version = '1.0')

@Field final JdbcDatabaseContainer container = CustomPostgreSQL12Container.instance
@Field final Properties propertiesToWrite = new Properties()
@Field final TEMPORARY_FILE_LOCATION = "generated/testdatabase-generated.properties"
@Field final TEMPORARY_FILE_HEADER = "This is a temporary file to store test database properties"

@SourceURI @Field final URI SOURCE_URI
@Field final String THIS_SCRIPT_LOCATION = Paths.get(SOURCE_URI).getParent().toString()

def startupDatabase = {
    println "Starting PostgreSQL 12 database"
    container.start()
}

def printContainerProperties = {
    println "Database url: $container.jdbcUrl"
    println "Database name: $container.databaseName"
    println "Database username: $container.username"
    println "Database password: $container.password"
    println "Database container id: $container.containerId"
    println "Database container ip address: $container.containerIpAddress"
}

def createTemporaryPropertiesFile = {
    prepareSystemPropertiesToWrite(
            "TEST_POSTGRESQL_URL", "TEST_POSTGRESQL_USERNAME", "TEST_POSTGRESQL_PASSWORD")
    new File(THIS_SCRIPT_LOCATION, TEMPORARY_FILE_LOCATION).withOutputStream {
        stream -> new DefaultPropertiesPersister().store(propertiesToWrite, stream, TEMPORARY_FILE_HEADER)
    }
}

def prepareSystemPropertiesToWrite(String... propertyNames) {
    propertyNames.each { propertyName -> prepareSystemPropertyToWrite(propertyName) }
}

def prepareSystemPropertyToWrite(String propertyName) {
    propertiesToWrite.setProperty(propertyName, System.getProperty(propertyName))
}

def waitForUserTermination() {
    println "Type 'Y' to terminate the database"
    def input = System.in.newReader().readLine()
    if (input.equalsIgnoreCase("Y"))
        println "Database terminated"
    else
        waitForUserTermination()
}

def removeTemporaryPropertiesFile = {
    new File(THIS_SCRIPT_LOCATION, TEMPORARY_FILE_LOCATION).delete()
}

addShutdownHook {
    removeTemporaryPropertiesFile()
}

startupDatabase()
printContainerProperties()
createTemporaryPropertiesFile()
waitForUserTermination()