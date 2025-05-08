package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.dto.AdminAbsencesResultDTO;
import edu.utdallas.csmc.web.model.schedule.Absence;

import java.util.ArrayList;
import java.util.List;


public class AdminAbsencesHelper {
    public List<AdminAbsencesResultDTO> setAbsenceDetails(List<Absence> absenceList) {
        List<AdminAbsencesResultDTO> adminAbsencesResultDTOS = new ArrayList<>();
        for (int i = 0; i < absenceList.size(); i++) {
            AdminAbsencesResultDTO adminAbsencesResultDTO = new AdminAbsencesResultDTO();

            adminAbsencesResultDTO.setMentor(absenceList.get(i).getAssignment().getMentor());
            adminAbsencesResultDTO.setDate(absenceList.get(i).getAssignment().getScheduledShift().getDate());
            adminAbsencesResultDTO.setStartTime(absenceList.get(i).getAssignment().getScheduledShift().getShift().getStartTime());
            adminAbsencesResultDTO.setReason(absenceList.get(i).getReason());
            adminAbsencesResultDTO.setTimeSubmitted(absenceList.get(i).getCreatedOn());
            String sessionTopic = "";
            if (absenceList.get(i).getAssignment().getSubject() != null) {
                sessionTopic += absenceList.get(i).getAssignment().getSubject().getName();
                sessionTopic += "  ";
            }
            if (absenceList.get(i).getAssignment().getSession() != null)
                sessionTopic += absenceList.get(i).getAssignment().getSession().getSession().getTopic();

            adminAbsencesResultDTO.setSession(sessionTopic);
            if (absenceList.get(i).getSubstitute() != null)
                adminAbsencesResultDTO.setSubstituteMentor(absenceList.get(i).getSubstitute().getMentor());

            adminAbsencesResultDTOS.add(adminAbsencesResultDTO);
        }
        return adminAbsencesResultDTOS;
    }
}
