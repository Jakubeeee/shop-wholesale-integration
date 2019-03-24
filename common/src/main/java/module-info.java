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

    exports com.jakubeeee.common.annotation;
    exports com.jakubeeee.common.converter;
    exports com.jakubeeee.common.exception;
    exports com.jakubeeee.common.mixin;
    exports com.jakubeeee.common.model;
    exports com.jakubeeee.common.serializer;
    exports com.jakubeeee.common.util;
}
