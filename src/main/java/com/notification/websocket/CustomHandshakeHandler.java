package com.notification.websocket;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.member.model.MemberVO;

import org.springframework.http.server.ServerHttpRequest;

import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        Object loggedInMember = attributes.get("loggedInMember");
        if (loggedInMember != null && loggedInMember instanceof MemberVO) {
            MemberVO member = (MemberVO) loggedInMember;
            Integer memberId = member.getMemberId();
            if (memberId != null) {
                return new StompPrincipal(memberId.toString());
            }
        }
        return null;
    }
}
