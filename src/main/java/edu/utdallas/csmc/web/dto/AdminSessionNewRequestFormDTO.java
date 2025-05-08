package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class AdminSessionNewRequestFormDTO {
    String topic;
    String type;
    List<String> types;
    String studentInstructions;
  //  String mentorInstructions;

    String startDate;
    String endDate;

    String sections;
    List<String> uploadedFiles;

    String id;
    int sessionType; // Session can be ScheduledSession (=0) as default
    // or Quiz (=1)
}
