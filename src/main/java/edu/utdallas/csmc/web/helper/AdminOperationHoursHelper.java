package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.dto.AdminOperationHoursResultDTO;
import edu.utdallas.csmc.web.model.misc.OperationHours;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class AdminOperationHoursHelper {

    public List<AdminOperationHoursResultDTO> setOperationHours(List<OperationHours> operationHours) {
        List<AdminOperationHoursResultDTO> adminOperationHoursResultDTOS = new ArrayList<>();

        HashMap<Integer, String> dayMap = new HashMap<>();
        dayMap.put(0, "Sunday");
        dayMap.put(1, "Monday");
        dayMap.put(2, "Tuesday");
        dayMap.put(3, "Wednesday");
        dayMap.put(4, "Thursday");
        dayMap.put(5, "Friday");
        dayMap.put(6, "Saturday");

        for(OperationHours hours : operationHours) {
            AdminOperationHoursResultDTO adminOperationHoursResultDTO = new AdminOperationHoursResultDTO();
            adminOperationHoursResultDTO.setOperationHoursId(hours.getId());
            adminOperationHoursResultDTO.setDay(dayMap.get(hours.getDay()));
//            operationHoursDTO.setDay(dayMap.get(operationHours.getDay()));
            adminOperationHoursResultDTO.setStartTime(hours.getStartTime());
            adminOperationHoursResultDTO.setEndTime(hours.getEndTime());

            adminOperationHoursResultDTOS.add(adminOperationHoursResultDTO);
        }
        return adminOperationHoursResultDTOS;
    }

    public AdminOperationHoursResultDTO editOperationHours(OperationHours operationHours) {

        HashMap<Integer, String> dayMap = new HashMap<>();
        dayMap.put(0, "Sunday");
        dayMap.put(1, "Monday");
        dayMap.put(2, "Tuesday");
        dayMap.put(3, "Wednesday");
        dayMap.put(4, "Thursday");
        dayMap.put(5, "Friday");
        dayMap.put(6, "Saturday");

        AdminOperationHoursResultDTO adminOperationHoursResultDTO = new AdminOperationHoursResultDTO();
        adminOperationHoursResultDTO.setOperationHoursId(operationHours.getId());
        adminOperationHoursResultDTO.setDay(dayMap.get(operationHours.getDay()));
        adminOperationHoursResultDTO.setStartTime(operationHours.getStartTime());
        adminOperationHoursResultDTO.setEndTime(operationHours.getEndTime());
        return adminOperationHoursResultDTO;
    }
}
