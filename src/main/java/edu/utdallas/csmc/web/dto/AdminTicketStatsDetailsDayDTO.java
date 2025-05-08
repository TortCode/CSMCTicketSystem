package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AdminTicketStatsDetailsDayDTO {
    Date startOfDay;
    int claimed;
}
