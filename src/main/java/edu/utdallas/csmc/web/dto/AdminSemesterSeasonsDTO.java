package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AdminSemesterSeasonsDTO {
    String seasonName;
    UUID seasonID;
    int id;
}
