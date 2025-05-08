package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class MentorCalendarSessionDetailsResultDTO {
    UUID timeSlotId;
    String topic;
    String description;
    String studentInstructions;
    String mentorInstructions;
    Boolean graded;
    Boolean numericGrade;
    List<MentorFileDetailsDTO> files;
    Date startTime;
    Date endTime;
    String location;
//    List<String> assignments;
    List<String> mentors;
    Integer registeredStudents;
    Integer capacity;
    Boolean started;
    Boolean ended;
    String message="";
}
