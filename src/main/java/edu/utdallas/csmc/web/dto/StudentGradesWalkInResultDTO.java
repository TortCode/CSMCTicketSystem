package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class StudentGradesWalkInResultDTO {
    String courseNumber;
    String topic;
    String activityName;
    String mentors;
    String checkInTime;
    String checkOutTime;
}
