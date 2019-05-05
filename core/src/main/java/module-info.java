module core {
    requires common;

    requires static lombok;
    requires java.annotation;
    requires org.aspectj.weaver;
    requires org.apache.cxf.core;
    requires slf4j.api;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.context.support;
    requires spring.data.jpa;
    requires spring.messaging;
    requires spring.tx;
    requires spring.web;
    requires spring.webmvc;
    requires spring.websocket;

    exports com.jakubeeee.core.exception;
    exports com.jakubeeee.core.mixin;
    exports com.jakubeeee.core.service;
    exports com.jakubeeee.core.util;
}