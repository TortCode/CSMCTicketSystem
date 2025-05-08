package edu.utdallas.csmc.web.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.UUID;

@Data
public class AdminIpResultDTO {
    UUID ipId;
    String ipAddress;
    String building;
    String floor;
    String classRoomNumber;
    @Setter(AccessLevel.NONE)
    boolean blocked;

    public void setBlocked(String blockedInput){
        this.blocked = blockedInput != null ? true : false;
    }

    public void setBlocked(boolean blockedInput){
        this.blocked = blockedInput;
    }
}
