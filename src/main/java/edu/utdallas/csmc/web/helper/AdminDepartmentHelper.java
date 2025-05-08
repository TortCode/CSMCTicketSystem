package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.dto.AdminDepartmentsResultDTO;
import edu.utdallas.csmc.web.model.course.Department;
import edu.utdallas.csmc.web.model.user.User;
import lombok.extern.log4j.Log4j2;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Log4j2
public class AdminDepartmentHelper {

    public void buildDepartmentDetailsList(final List<AdminDepartmentsResultDTO> adminDepartmentsResultDTOList, List<Department> departmentList) {
        for(Department currentDepartment : departmentList){
            AdminDepartmentsResultDTO adminDepartmentsResultDTO = new AdminDepartmentsResultDTO();
            adminDepartmentsResultDTO.setDepartmentId(currentDepartment.getId().toString());
            adminDepartmentsResultDTO.setName(currentDepartment.getName());
            adminDepartmentsResultDTO.setAbbreviation(currentDepartment.getAbbreviation());
            adminDepartmentsResultDTO.setAdminNotes(currentDepartment.getAdmin_notes());

            adminDepartmentsResultDTOList.add(adminDepartmentsResultDTO);
        }
    }

    public void buildDepartmentDetailsFromDepartment(final AdminDepartmentsResultDTO adminDepartmentsResultDTO, Department currentDepartment) {
        adminDepartmentsResultDTO.setName(currentDepartment.getName());
        adminDepartmentsResultDTO.setAbbreviation(currentDepartment.getAbbreviation());
        adminDepartmentsResultDTO.setAdminNotes(currentDepartment.getAdmin_notes());
    }

    public Department buildNewDepartmentModel(AdminDepartmentsResultDTO adminDepartmentsResultDTO, Optional<User> user) {
        Department newDepartment = new Department();
        newDepartment.setName(adminDepartmentsResultDTO.getName());
        newDepartment.setAbbreviation(adminDepartmentsResultDTO.getAbbreviation());
        // Add Admin Notes if present
        if(adminDepartmentsResultDTO.getAdminNotes() != null){
            newDepartment.setAdmin_notes(adminDepartmentsResultDTO.getAdminNotes());
        }

        newDepartment.setCreatedOn(new Date());
        newDepartment.setLastModifiedOn(new Date());
        // Add these fields if the user is present
        if (user.isPresent()) {
            newDepartment.setCreatedBy(user.get());
            newDepartment.setLastModifiedBy(user.get());
        }

        return newDepartment;
    }

    public void buildUpdatedDepartmentFromDTO(final Department currentDepartment, AdminDepartmentsResultDTO adminDepartmentsResultDTO, Optional<User> user) {
        currentDepartment.setName(adminDepartmentsResultDTO.getName());
        currentDepartment.setAbbreviation(adminDepartmentsResultDTO.getAbbreviation());
        // Add Admin Notes if present
        if (adminDepartmentsResultDTO.getAdminNotes() != null) {
            currentDepartment.setAdmin_notes(adminDepartmentsResultDTO.getAdminNotes());
        }
        currentDepartment.setLastModifiedOn(new Date());
        // Add this field if the user is present
        if (user.isPresent()) {
            currentDepartment.setLastModifiedBy(user.get());
        }
    }

}
