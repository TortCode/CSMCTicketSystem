package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class AdminTicketStatsDetailsDTO {
    String name;
    List<AdminTicketStatsDetailsDayDTO> dayDetails;
    List<AdminTicketStatsDetailsCourseDTO> courseDetails;
    double averageHelpMinutes;
    double averageTicketsPerWeek;
}
