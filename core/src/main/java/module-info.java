module core {
    requires static lombok;
    requires org.aspectj.weaver;
    requires slf4j.api;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.data.jpa;
    requires spring.messaging;
    requires spring.tx;
    requires spring.web;
    requires spring.websocket;
}