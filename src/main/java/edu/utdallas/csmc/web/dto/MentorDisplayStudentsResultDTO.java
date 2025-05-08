package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MentorDisplayStudentsResultDTO {
    String firstName;
    String courseAbbreviation;
    String courseNumber;
    String activityName;
    Date timeIn;
}
