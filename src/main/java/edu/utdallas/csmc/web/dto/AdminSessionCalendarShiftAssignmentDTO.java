package edu.utdallas.csmc.web.dto;

import java.util.List;
import edu.utdallas.csmc.web.model.misc.Room;
import edu.utdallas.csmc.web.model.schedule.ShiftAssignment;
import edu.utdallas.csmc.web.model.session.SessionTimeSlot;
import lombok.Data;

@Data
public class AdminSessionCalendarShiftAssignmentDTO {
    SessionTimeSlot id;
    String date;
    String startTime;
    String endTime;
    Room location;
    int capacity;
    List<ShiftAssignment> assignments;
}
