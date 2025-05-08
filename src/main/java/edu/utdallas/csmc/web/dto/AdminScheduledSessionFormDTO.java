package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class AdminScheduledSessionFormDTO {
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

    int repeats;
    String location;
    int capacity;
    int durationH;
    int durationM;
}
