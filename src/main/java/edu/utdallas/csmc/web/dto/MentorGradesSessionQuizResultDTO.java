package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class MentorGradesSessionQuizResultDTO {
    UUID scheduledSessionId;
    UUID quizId;
    List<String> section;
    String topic;
    String description;
}
