package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.sql.Time;
import java.util.UUID;

@Data
public class AdminOperationHoursResultDTO {
    UUID operationHoursId;
    Integer dayNumber;
    String day;
    Time startTime;
    Time endTime;
}
