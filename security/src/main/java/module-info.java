module security {
    requires core;
    requires common;

    requires static lombok;
    requires commons.beanutils;
    requires java.persistence;
    requires java.validation;
    requires slf4j.api;
    requires spring.beans;
    requires spring.boot;
    requires spring.context;
    requires spring.context.support;
    requires spring.core;
    requires spring.data.commons;
    requires spring.data.jpa;
    requires spring.security.config;
    requires spring.security.core;
    requires spring.security.web;
    requires spring.tx;
    requires spring.web;
    requires tomcat.embed.core;
}