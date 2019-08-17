module testutils {
    requires static lombok;
    requires static junit;
    requires dom4j;
    requires java.persistence;
    requires java.sql;
    requires spring.boot.test.autoconfigure;
    requires spring.core;

    exports com.jakubeeee.testutils.constant;
    exports com.jakubeeee.testutils.factory;
    exports com.jakubeeee.testutils.marker;
    exports com.jakubeeee.testutils.model;
    exports com.jakubeeee.testutils.util;
}