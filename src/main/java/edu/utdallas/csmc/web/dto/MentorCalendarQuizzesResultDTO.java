package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class MentorCalendarQuizzesResultDTO {
    UUID quizId;
    String topic;
    Date startDate;
    Date endDate;
    String building;
    Integer floor;
    Integer roomNumber;
    String description;
}