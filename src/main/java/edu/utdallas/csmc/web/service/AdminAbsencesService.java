package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.AdminAbsencesResultDTO;
import edu.utdallas.csmc.web.helper.AdminAbsencesHelper;
import edu.utdallas.csmc.web.model.schedule.Absence;
import edu.utdallas.csmc.web.repository.AbsenceRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Log4j2
public class AdminAbsencesService {

    @Autowired
    private AbsenceRepository absenceRepository;

    AdminAbsencesHelper adminAbsencesHelper = new AdminAbsencesHelper();

    public List<AdminAbsencesResultDTO> getAbsencesDetails() {
        List<Absence> absenceList = absenceRepository.getAbsenceDetails();
        List<AdminAbsencesResultDTO> absenceDetails = adminAbsencesHelper.setAbsenceDetails(absenceList);

        return absenceDetails;
    }
}
