module core {
    requires common;

    requires static lombok;
    requires org.aspectj.weaver;
    requires org.slf4j;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.context.support;
    requires spring.core;
    requires spring.data.jpa;
    requires spring.messaging;
    requires spring.tx;
    requires spring.web;
    requires spring.webmvc;
    requires spring.websocket;

    exports com.jakubeeee;
    exports com.jakubeeee.core;
}