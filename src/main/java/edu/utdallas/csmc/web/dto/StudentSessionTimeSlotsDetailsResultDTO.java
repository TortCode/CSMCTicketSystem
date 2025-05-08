package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class StudentSessionTimeSlotsDetailsResultDTO {

    UUID id;
    String location;
    int remainingSeats;
    int capacity;
    Date startTime;
}
