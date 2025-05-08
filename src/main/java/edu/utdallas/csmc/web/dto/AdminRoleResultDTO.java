package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class AdminRoleResultDTO {
    List<AdminSingleRoleDTO> roles;
}
