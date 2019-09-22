module testutils {
    requires static lombok;
    requires static junit;
    requires dom4j;
    requires java.persistence;
    requires java.sql;
    requires spring.boot.test.autoconfigure;
    requires spring.core;

    exports com.jakubeeee.testutils;
    exports com.jakubeeee.testutils.marker;
}