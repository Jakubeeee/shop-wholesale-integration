module common {
    requires static lombok;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires dom4j;
    requires java.sql;
    requires java.persistence;
    requires org.apache.commons.lang3;
    requires spring.web;
    requires spring.beans;
    requires spring.context;
    requires spring.core;

    exports com.jakubeeee.common.annotations;
    exports com.jakubeeee.common.converters;
    exports com.jakubeeee.common.exceptions;
    exports com.jakubeeee.common.mixins;
    exports com.jakubeeee.common.model;
    exports com.jakubeeee.common.serializers;
    exports com.jakubeeee.common.utils;
}
