package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class MentorCalendarSessionsResultDTO {
    UUID timeSlotId;
    List<String> sections;
    String topic;
    String startTime;
    List<String> mentors;
    String building;
    Integer floor;
    Integer roomNumber;
    List<String> instructors;
    String description;
    Boolean started;
    Boolean ended;
}