package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.Date;

@Data
public class StudentScheduleResultDTO {
    String courseName;
    String sectionNumber;
    String topic;
    String type;
    String room;
    String description;
    String sectionId;
    Date startDate;
    Date endDate;
    boolean registered;
    boolean attended;
}
