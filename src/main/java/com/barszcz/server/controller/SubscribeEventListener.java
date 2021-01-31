//package com.barszcz.server.controller;
//
//import org.springframework.context.ApplicationEvent;
//import org.springframework.context.ApplicationListener;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.WebSocketMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.messaging.SessionConnectEvent;
//import org.springframework.web.socket.messaging.SessionDisconnectEvent;
//import org.springframework.web.socket.messaging.SessionSubscribeEvent;
//
//@Component
//public class SubscribeEventListener implements ApplicationListener<SessionSubscribeEvent> {
//
//
//    @Override
//    public void onApplicationEvent(SessionSubscribeEvent sessionSubscribeEvent) {
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(sessionSubscribeEvent.getMessage());
//        System.out.println(sessionSubscribeEvent.toString());
//    }
//}
//
//
