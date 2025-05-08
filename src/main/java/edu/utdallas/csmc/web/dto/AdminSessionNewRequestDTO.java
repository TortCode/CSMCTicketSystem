package edu.utdallas.csmc.web.dto;

import lombok.Data;

@Data
public class AdminSessionNewRequestDTO {
    String topic;
    String sections;
    String instructors;
    String type;
    String dates;
    String materials;
    String timeRequested;
    String id;
    int sessionType; // Session can be ScheduledSession (=0) as default
    // or Quiz (=1)
}
