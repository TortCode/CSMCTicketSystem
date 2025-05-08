package edu.utdallas.csmc.web.dto;

import lombok.Data;

@Data
public class AdminSessionDeniedDTO {
    String topic;
    String sections;
    String instructors;
    String type;
    String dates;
    String id;
    String reason;
    int sessionType; // Session can be ScheduledSession (=0) as default
    // or Quiz (=1)
}
