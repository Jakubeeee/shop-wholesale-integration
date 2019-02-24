module common {
    requires static lombok;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires dom4j;
    requires java.sql;
    requires java.persistence;
    requires org.apache.commons.lang3;
    requires org.apache.cxf.core;
    requires spring.web;
    requires spring.beans;
    requires spring.context;
    requires spring.context.support;
    requires spring.core;

    exports com.jakubeeee.common.annotations;
    exports com.jakubeeee.common.converters;
    exports com.jakubeeee.common.exceptions;
    exports com.jakubeeee.common.misc;
    exports com.jakubeeee.common.model;
    exports com.jakubeeee.common.serializers;
    exports com.jakubeeee.common.service;
    exports com.jakubeeee.common.utils;
}
