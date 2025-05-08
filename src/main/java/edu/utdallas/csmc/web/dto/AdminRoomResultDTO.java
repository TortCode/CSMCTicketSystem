package edu.utdallas.csmc.web.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.UUID;

@Data
public class AdminRoomResultDTO {
    UUID roomId;
    String building;
    String floor;
    String classRoomNumber;
    String description;
    int capacity;
    @Setter(AccessLevel.NONE)
    boolean active;

    public void setActive(String active){
        this.active = active != null ? true : false;
    }

    public void setActive(boolean active){
        this.active = active;
    }
}
