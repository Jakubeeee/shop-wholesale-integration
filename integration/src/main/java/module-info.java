module integration {
    requires common;
    requires core;
    requires tasks;

    requires static lombok;
    requires com.fasterxml.jackson.annotation;
    requires dom4j;
    requires durian;
    requires java.annotation;
    requires org.slf4j;
    requires spring.beans;
    requires spring.boot;
    requires spring.context;
    requires spring.core;
    requires spring.web;

    exports com.jakubeeee.integration;
    exports com.jakubeeee.integration.auth;
    exports com.jakubeeee.integration.datasource;
    exports com.jakubeeee.integration.product;
}