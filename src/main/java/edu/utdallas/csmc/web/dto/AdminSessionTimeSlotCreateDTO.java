package edu.utdallas.csmc.web.dto;

import lombok.Data;
import edu.utdallas.csmc.web.model.misc.Room;
import edu.utdallas.csmc.web.model.session.ScheduledSession;

@Data
public class AdminSessionTimeSlotCreateDTO {
    String start;
    String end;
    Room location;
    int capacity;
    ScheduledSession session;
}
