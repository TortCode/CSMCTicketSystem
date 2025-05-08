package edu.utdallas.csmc.web.dto;

import lombok.Data;

/**
 * This class will have the Data Transfer Objects (DTO) or the Plain Old Java Objects (POJO).
 * These will be sent back and forth from View and Controllers.
 */
@Data
public class UserDTO {
    String netId;
}
