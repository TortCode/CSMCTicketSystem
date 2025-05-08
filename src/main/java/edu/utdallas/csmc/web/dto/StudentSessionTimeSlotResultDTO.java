package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.Date;

@Data
public class StudentSessionTimeSlotResultDTO {

    Date startTime;
    String location;
    String description;
    String studentInstructions;
    int remainingSeats;
    int capacity;
    String topic;
    String timeSlotId;
    String message ="";
}
