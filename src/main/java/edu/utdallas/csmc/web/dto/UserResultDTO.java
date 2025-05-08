package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserResultDTO {

    UUID userId;
    String userName;
    String firstName;
    String lastName;
    String roles;
    String message = "";
}
