package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MentorGradesScheduledSessionEditDTO {
    Date startTime;
    Date endTime;
    List<MentorGradesQuizEditDTO> attendees;
}
