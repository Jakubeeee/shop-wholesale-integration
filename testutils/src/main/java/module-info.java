module testutils {
    requires static lombok;
    requires static junit;
    requires dom4j;
    requires java.persistence;
    requires spring.boot.test.autoconfigure;
    requires spring.core;

    exports com.jakubeeee.testutils.model;
}