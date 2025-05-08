package edu.utdallas.csmc.web.dto;

import lombok.Data;

@Data
public class RequestSwipeWalkInObject {
    String scancode;

    public RequestSwipeWalkInObject(String scancode) {
        this.scancode = scancode;
    }

    public RequestSwipeWalkInObject() {

    }
}
