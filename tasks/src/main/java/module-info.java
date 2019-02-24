module tasks {
    requires core;
    requires common;

    requires static lombok;
    requires com.fasterxml.jackson.databind;
    requires durian;
    requires jackson.annotations;
    requires java.persistence;
    requires org.apache.commons.lang3;
    requires org.aspectj.weaver;
    requires slf4j.api;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
    requires spring.data.commons;
    requires spring.messaging;
    requires spring.tx;
    requires spring.web;

    exports com.jakubeeee.tasks.annotations;
    exports com.jakubeeee.tasks.enums;
    exports com.jakubeeee.tasks.exceptions;
    exports com.jakubeeee.tasks.model;
    exports com.jakubeeee.tasks.service;
    exports com.jakubeeee.tasks.utils;
    exports com.jakubeeee.tasks.validators;
}