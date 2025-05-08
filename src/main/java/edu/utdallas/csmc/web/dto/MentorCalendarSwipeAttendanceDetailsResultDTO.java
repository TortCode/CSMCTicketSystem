package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class MentorCalendarSwipeAttendanceDetailsResultDTO {
    UUID attendanceStudentId;
    String firstName;
    String userName;
    Date timeIn;
    Date timeOut;
}
