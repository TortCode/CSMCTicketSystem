package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class AdminSemeterResultDTO {
    public UUID semesterID;
    public String season;
    public int year;
    public Date startDate;
    public Date endDate;
    public boolean active;
    public String semesterIDString;

}
