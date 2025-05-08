package edu.utdallas.csmc.web.dto;

import lombok.Data;
import java.util.Date;
import java.util.UUID;

@Data
public class TicketDTO {
    UUID id;
    String studentNetId;
    String studentName;
    String mentorNetId;
    String mentorName;
    String course;
    String topic;
    String type;
    String info;
    String status;
    int tableNo;
    Date createdAt;
    Date resolvedAt;
}
