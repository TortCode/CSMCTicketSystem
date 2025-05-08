package edu.utdallas.csmc.web.dto;

import lombok.Data;

@Data
public class RoomDetailsResultDTO {
    String building;
    String floor;
    String number;
    String description;
}
