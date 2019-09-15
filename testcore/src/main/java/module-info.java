module testcore {
    requires common;
    requires core;
    requires static testutils;

    requires database.commons;
    requires junit;
    requires org.codehaus.groovy;
    requires spring.core;
    requires testcontainers.merged;
    requires static greenmail;
    requires static lombok;
    requires static spring.beans;
    requires static spring.context;
    requires static spring.test;
    requires static spring.boot.test.autoconfigure;
    requires static spring.boot.test;
    requires static spring.test.dbunit;

    exports com.jakubeeee.testcore.container;
    exports com.jakubeeee.testcore.test;
}