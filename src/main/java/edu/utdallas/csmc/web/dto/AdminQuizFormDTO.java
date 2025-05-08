package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
public class AdminQuizFormDTO {
    String topic;
    String type;
    List<String> types;
    String studentInstructions;
    String mentorInstructions;
    String description;
    boolean numericGrade;
    String color;
    boolean graded;

    List<String> rooms;
    List<String> uploadedFiles;

    String room;
    String startDate;
    String endDate;
}
