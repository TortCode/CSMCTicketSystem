package edu.utdallas.csmc.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RequestObjectMaxMentor {
    UUID shiftSubjectID;

    public RequestObjectMaxMentor(String shiftSubjectID) {
        this.shiftSubjectID = UUID.fromString(shiftSubjectID);
    }

    public RequestObjectMaxMentor(){

    }
}
