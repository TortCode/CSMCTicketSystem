package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class StudentGradesSessionResultDTO {
    List<String> sections;
    String topic;
    String checkInTime;
    String checkOutTime;
    Integer grade;
    Boolean graded;
    Boolean numericGrade;
}
