package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
public class MentorAbsenceFormResultDTO {

    UUID absenceId;
    String reason;
    Set<String> dateList;
    List<String> shiftList;
    List<String> shiftsID;
    String dateShift;
}
