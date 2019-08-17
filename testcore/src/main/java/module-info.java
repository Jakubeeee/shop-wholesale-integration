module testcore {
    requires common;
    requires core;

    requires database.commons;
    requires junit;
    requires org.codehaus.groovy;
    requires spring.core;
    requires testcontainers.merged;

    exports com.jakubeeee.testcore.container;
}