package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class AdminSessionTableDTO {
    List<AdminSessionNewRequestDTO> newRequests;
    List<AdminSessionPendingDTO> pendingRequests;
    List<AdminSessionCompletedDTO> completedRequests;
    List<AdminSessionDeniedDTO> deniedRequests;
}
