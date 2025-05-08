package edu.utdallas.csmc.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RequestObjectShift {
    UUID mentorID;
    UUID shiftID;
    UUID subjectID;

    public RequestObjectShift(String mentorID, String shiftID,String subjectID) {
        this.mentorID = UUID.fromString(mentorID);
        this.shiftID = UUID.fromString(shiftID);
        this.subjectID = UUID.fromString(subjectID);
    }

    public RequestObjectShift(){

    }

}
