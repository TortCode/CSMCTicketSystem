package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AnnouncementsResultDTO {
    Date postDate;
    String message;
}
