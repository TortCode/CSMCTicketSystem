package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class RequestSwipeSessionObject {
    String scancode;
    UUID timeSlotId;

    public RequestSwipeSessionObject(String scancode, String timeSlotId) {
        this.scancode = scancode;
        this.timeSlotId = UUID.fromString(timeSlotId);
    }

    public RequestSwipeSessionObject() {

    }
}
