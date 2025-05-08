package edu.utdallas.csmc.web.dto;

import edu.utdallas.csmc.web.model.session.TimeSlot;
import edu.utdallas.csmc.web.model.user.User;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AdminAttendanceGradesResultDTO {

    UUID sessionId;
    String sessionType;
    List<User> users;
    List<TimeSlot> timeSlot;
}
