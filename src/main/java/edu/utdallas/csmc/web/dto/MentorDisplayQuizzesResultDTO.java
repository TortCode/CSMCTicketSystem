package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MentorDisplayQuizzesResultDTO {
    String topic;
    String location;
    Date startDate;
    Date endDate;
}
