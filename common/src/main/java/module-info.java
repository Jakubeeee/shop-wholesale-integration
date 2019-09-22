module common {
    requires static lombok;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires dom4j;
    requires java.persistence;
    requires java.sql;
    requires org.apache.commons.lang3;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
    requires spring.web;

    exports com.jakubeeee.common;
    exports com.jakubeeee.common.persistence;
    exports com.jakubeeee.common.reflection;
    exports com.jakubeeee.common.serialization;
}
