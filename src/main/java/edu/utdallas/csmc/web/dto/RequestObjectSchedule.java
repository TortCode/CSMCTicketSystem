package edu.utdallas.csmc.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RequestObjectSchedule {
    UUID mentorID;
    UUID shiftID;

    public RequestObjectSchedule(String mentorID, String shiftID) {
        this.mentorID = UUID.fromString(mentorID);
        this.shiftID = UUID.fromString(shiftID);
    }

    public RequestObjectSchedule(){

    }

}
