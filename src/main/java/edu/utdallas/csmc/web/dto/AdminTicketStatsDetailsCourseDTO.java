package edu.utdallas.csmc.web.dto;

import lombok.Data;

@Data
public class AdminTicketStatsDetailsCourseDTO {
    String course;
    int unclaimed;
    int completed;
}
