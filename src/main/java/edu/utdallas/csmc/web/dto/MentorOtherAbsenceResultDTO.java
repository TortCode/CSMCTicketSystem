package edu.utdallas.csmc.web.dto;

import java.util.Date;
import java.util.UUID;

import lombok.Data;
@Data
public class MentorOtherAbsenceResultDTO {

    UUID id;
    Date shiftDate;
    Date startTime;
    String substitute;
    String mentor;
    String topic;

//    boolean substituteFlag = false;
//    boolean futureDateFlag = false;
//    boolean currentDateFlag = false;
//    boolean futureTimeFlag = false;
//    boolean futureFlag = false;

}
