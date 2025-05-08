package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AdminGradesQuizEditDTO {
    UUID id;
    String name;
    String username;
    int grade;
}
