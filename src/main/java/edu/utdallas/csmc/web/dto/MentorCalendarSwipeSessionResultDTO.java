package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.sql.Time;
import java.util.List;
import java.util.UUID;

@Data
public class MentorCalendarSwipeSessionResultDTO {
    UUID timeSlotId;
    String topic;
    List<String> mentorPreferredName;
    Time actualStartTime;
    Integer registeredStudents;
    Integer capacity;
    List<MentorCalendarSwipeUserDetailsResultDTO> registrations;
    List<MentorCalendarSwipeAttendanceDetailsResultDTO> attendances;
    String message="";
}
