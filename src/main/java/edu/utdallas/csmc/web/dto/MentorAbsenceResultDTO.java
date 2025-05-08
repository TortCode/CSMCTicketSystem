package edu.utdallas.csmc.web.dto;

import java.util.Date;
import java.util.List;


import lombok.Data;
@Data
public class MentorAbsenceResultDTO {

    String current_mentor;
    List<MentorMyAbsenceResultDTO> myAbsenceDetails;
    List<MentorOtherAbsenceResultDTO> otherAbsenceDetails;

}
