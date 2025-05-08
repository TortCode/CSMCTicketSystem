package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class MentorCalendarSwipeUserDetailsResultDTO {
    UUID studentId;
    String firstName;
    String userName;
}
