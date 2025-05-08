package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.AdminOperationHoursResultDTO;
import edu.utdallas.csmc.web.helper.AdminOperationHoursHelper;
import edu.utdallas.csmc.web.model.misc.OperationHours;
import edu.utdallas.csmc.web.repository.OperationHoursRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
public class AdminOperationHoursService {

    @Autowired
    private OperationHoursRepository operationHoursRepository;

    AdminOperationHoursHelper adminOperationHoursHelper = new AdminOperationHoursHelper();
    public List<AdminOperationHoursResultDTO> getOperationHours() {
        List<OperationHours> operationHours = operationHoursRepository.findAll();
        List<AdminOperationHoursResultDTO> adminOperationHoursResultDTOS = adminOperationHoursHelper.setOperationHours(operationHours);
        return adminOperationHoursResultDTOS;
    }

    public AdminOperationHoursResultDTO editOperationHours(UUID id) {
        Optional<OperationHours> operationHours = operationHoursRepository.findById(id);
        AdminOperationHoursResultDTO adminOperationHoursResultDTO = new AdminOperationHoursResultDTO();
        if (operationHours.isPresent()) {
            OperationHours optHours = operationHours.get();
            adminOperationHoursResultDTO = adminOperationHoursHelper.editOperationHours(optHours);
        } else {
            log.info("No Operation Hours found with id : " + id);
        }
        return adminOperationHoursResultDTO;
    }

    public void saveOperationHours(AdminOperationHoursResultDTO adminOperationHoursResultDTO) {
        Optional<OperationHours> operationHours = operationHoursRepository.findById(adminOperationHoursResultDTO.getOperationHoursId());
        if(operationHours.isPresent()) {
            OperationHours optHours = operationHours.get();
            optHours.setStartTime(adminOperationHoursResultDTO.getStartTime());
            optHours.setEndTime(adminOperationHoursResultDTO.getEndTime());
            operationHoursRepository.save(optHours);
        }
        else {
            log.info("No Operation Hours found with id : " + adminOperationHoursResultDTO.getOperationHoursId());
        }
    }
}
