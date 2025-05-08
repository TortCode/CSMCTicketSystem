package edu.utdallas.csmc.web.dto;

import java.util.Date;
import java.util.UUID;


import lombok.Data;
@Data
public class MentorMyAbsenceResultDTO {

    String absenceId;
    Date shiftDate;
    Date startTime;
    String reason;
    String substitute;
    boolean substituteFlag;
    boolean futureDateFlag;
    boolean currentDateFlag;
    boolean futureTimeFlag;
    boolean futureFlag;

}
