package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.sql.Time;

@Data
public class OperationHoursResultDTO {
    String day;
    Time startTime;
    Time endTime;
}
