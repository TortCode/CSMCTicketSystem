package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class MentorGradesSessionResultDTO {
    UUID sessionId;
    String topic;
    boolean graded;
    boolean numericGrade;
}
