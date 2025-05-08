package edu.utdallas.csmc.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.util.UUID;

@Getter
@Setter
public class AdminCreateShiftResultDTO {

    Time startTime;
    Time endTime;
    int day;
    UUID room;
}
