package edu.utdallas.csmc.web.dto;

import lombok.Data;

@Data
public class StudentGradesQuizResultDTO {
    String sectionNumber;
    String topic;
    String checkInTime;
    String checkOutTime;
    Integer grade;
    Boolean graded;
    Boolean numericGrade;
}
