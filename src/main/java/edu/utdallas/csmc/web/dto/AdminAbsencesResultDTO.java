package edu.utdallas.csmc.web.dto;

import edu.utdallas.csmc.web.model.user.User;
import lombok.Data;

import java.sql.Time;
import java.util.Date;

@Data
public class AdminAbsencesResultDTO {
    User mentor;
    Date date;
    Time startTime;
    String reason;
    Date timeSubmitted;
    String session;
    User substituteMentor;
}
