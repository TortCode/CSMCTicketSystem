package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class MentorGradesQuizEditDTO {
    UUID id;
    String name;
    String username;
    int grade;
}
