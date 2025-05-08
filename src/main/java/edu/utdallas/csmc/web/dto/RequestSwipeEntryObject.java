package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class RequestSwipeEntryObject {
    String topic;
    String course;
    String activity;
    String quiz;
    String user;

    public RequestSwipeEntryObject(String topic, String course, String activity, String quiz, String user){
        this.topic = topic;
        this.course = course;
        this.activity = activity;
        this.quiz = quiz;
        this.user = user;
    }

    public RequestSwipeEntryObject(){

    }
}
