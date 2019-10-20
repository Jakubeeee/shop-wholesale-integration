module tasks {
    requires core;
    requires common;

    requires static lombok;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires durian;
    requires java.persistence;
    requires org.apache.commons.lang3;
    requires org.aspectj.weaver;
    requires org.slf4j;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
    requires spring.data.commons;
    requires spring.messaging;
    requires spring.tx;
    requires spring.web;

    exports com.jakubeeee.tasks;
    exports com.jakubeeee.tasks.logging;
    exports com.jakubeeee.tasks.pasttaskexecution;
    exports com.jakubeeee.tasks.progresstracking;
    exports com.jakubeeee.tasks.provider;
    exports com.jakubeeee.tasks.status;
    exports com.jakubeeee.tasks.validation;
}