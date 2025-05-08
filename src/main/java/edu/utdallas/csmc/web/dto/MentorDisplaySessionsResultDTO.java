package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MentorDisplaySessionsResultDTO {
    String topicName;
    Date startTime;
    String location;
    Integer capacity;
    Integer remainingSeats;
    List<String> mentorPreferredName;
}
