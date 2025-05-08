package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class AdminSessionFormDTO {
    // sessionType=1 (quiz)
    // sessionType=0 (scheduledSession)
    int sessionType;
    String id;
    List<String> allSection;
    Set<String> sectionSelected;
    AdminScheduledSessionFormDTO scheduledSession;
    AdminQuizFormDTO quiz;
}
