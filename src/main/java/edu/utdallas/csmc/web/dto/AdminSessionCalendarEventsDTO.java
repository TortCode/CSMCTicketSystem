package edu.utdallas.csmc.web.dto;

import lombok.Data;
import java.util.*;

@Data
public class AdminSessionCalendarEventsDTO {

    UUID id;
    Date startTime;
    Date endTime;
    RoomDTO location;
    AdminSessionCalendarEventsSessionDTO session;
    AdminSessionCalendarEventsQuizDTO quiz;

    @Data
    public static class RoomDTO {
        public RoomDTO(UUID id, String room) {
            this.id = id;
            this.room = room;
        }

        UUID id;
        String room;
    }
}
