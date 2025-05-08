package edu.utdallas.csmc.web.dto;

import lombok.Data;

@Data
public class AdminSessionCalendarEventsQuizDTO {
    String topic;

    public AdminSessionCalendarEventsQuizDTO() {
        this.topic = "";
    }
}
