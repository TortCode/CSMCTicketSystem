package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class AdminSessionUpdatedDTO {
    String sessionId;
    String sessionType;
    String topic;
    String type;
    String description;
    String studentInstruction;
    String mentorInstruction;
    String[] sections;
    String room;

    boolean graded;
    boolean numericgrade;
    String color;
    int repeats;
    int capacity;
    int durationH;
    int durationM;
    String startDate;
    String endDate;
}
