package edu.utdallas.csmc.web.dto;

import lombok.Data;

@Data
public class RequestSwipeRegisterObject {
    String username;
    String password;
    String swipe;
    String session;

    public RequestSwipeRegisterObject(String username, String password, String swipe, String session) {
        this.username = username;
        this.password = password;
        this.swipe = swipe;
        this.session = session;
    }

    public RequestSwipeRegisterObject() {

    }
}
