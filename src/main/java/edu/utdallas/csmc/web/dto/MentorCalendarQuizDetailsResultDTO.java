package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MentorCalendarQuizDetailsResultDTO {
    String topic;
    String description;
    String studentInstructions;
    String mentorInstructions;
    Boolean graded;
    Boolean numericGrade;
    List<MentorFileDetailsDTO> files;
    Date startDate;
    Date endDate;
}
