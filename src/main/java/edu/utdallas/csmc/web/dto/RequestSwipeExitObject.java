package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class RequestSwipeExitObject {
    String feedback;
    List<String> mentors = new ArrayList<>();
    String user;
    String attendance;

    public RequestSwipeExitObject(String feedback, String user, String attendance, String[] mentors){
        this.feedback = feedback;
        this.attendance = attendance;
        for(int i=0;i<mentors.length;i++) {
            this.mentors.add(mentors[i]);
        }
        this.user = user;
    }

    public RequestSwipeExitObject(){

    }
}
