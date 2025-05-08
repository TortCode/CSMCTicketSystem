package edu.utdallas.csmc.web.dto;

import edu.utdallas.csmc.web.model.session.Registration;
import edu.utdallas.csmc.web.model.schedule.ShiftAssignment;

import lombok.Data;
import java.util.*;

@Data
public class AdminSessionCalendarEventsSessionDTO {
    String topic;
    List<Registration> registrations;

    int registeredStudents;
    int capacity;
    List<ShiftAssignment> assignments;

    public AdminSessionCalendarEventsSessionDTO() {
        this.topic = "";
        this.registrations = new ArrayList<>();
        this.capacity = 0;
        this.assignments = new ArrayList<>();
    }
}
