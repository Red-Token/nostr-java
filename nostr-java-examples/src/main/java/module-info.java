
module nostr.examples {
    requires nostr.event;
    requires nostr.client;
    requires nostr.api;
    requires static lombok;
    requires nostr.ws;
    requires org.eclipse.jetty.websocket.jetty.client;
    requires org.eclipse.jetty.websocket.jetty.api;
    requires org.eclipse.jetty.websocket.jetty.common;
    requires org.eclipse.jetty.websocket.core.common;
    requires org.eclipse.jetty.websocket.core.client;
    requires org.eclipse.jetty.client;
    requires org.eclipse.jetty.http;
    requires org.eclipse.jetty.util;
    requires org.eclipse.jetty.io;
    requires org.slf4j;
    requires org.eclipse.jetty.http2.client;
    requires org.eclipse.jetty.http2.common;
    requires org.eclipse.jetty.http2.hpack;
    requires org.eclipse.jetty.alpn.client;
    requires org.eclipse.jetty.http2.http.client.transport;
    requires org.eclipse.jetty.alpn.java.client;
    requires nostr.util;
    requires nostr.base;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires nostr.crypto;
    requires nostr.id;
    requires org.bouncycastle.provider;
    requires java.logging;
    
    exports nostr.examples;
}
