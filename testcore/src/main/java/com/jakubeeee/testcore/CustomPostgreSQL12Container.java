package com.jakubeeee.testcore;

import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Singleton class used for initialization of docker container able to run PostgreSQL database for testing purposes.
 * It specifies the database version, publishes database url, username and password as environmental variables and
 * makes sure only one instance of the database can be created at the same time.
 */
public class CustomPostgreSQL12Container extends PostgreSQLContainer<CustomPostgreSQL12Container> {

    private static final String IMAGE_TAG = "postgres:12";

    private static CustomPostgreSQL12Container container;

    private CustomPostgreSQL12Container() {
        super(IMAGE_TAG);
    }

    public static CustomPostgreSQL12Container getInstance() {
        if (container == null)
            container = new CustomPostgreSQL12Container();
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("TEST_POSTGRESQL_URL", container.getJdbcUrl());
        System.setProperty("TEST_POSTGRESQL_USERNAME", container.getUsername());
        System.setProperty("TEST_POSTGRESQL_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        // do nothing - the container will be destroyed along with jvm instance
    }

}