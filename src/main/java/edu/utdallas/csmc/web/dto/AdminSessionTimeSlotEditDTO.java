package edu.utdallas.csmc.web.dto;

import lombok.Data;
import edu.utdallas.csmc.web.model.misc.Room;
import edu.utdallas.csmc.web.model.session.ScheduledSession;
import edu.utdallas.csmc.web.model.session.SessionTimeSlot;

@Data
public class AdminSessionTimeSlotEditDTO {
    SessionTimeSlot id;
    String start;
    String end;
    Room location;
    int capacity;
    ScheduledSession session;
}
