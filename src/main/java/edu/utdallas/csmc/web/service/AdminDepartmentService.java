package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.AdminDepartmentsResultDTO;
import edu.utdallas.csmc.web.helper.AdminDepartmentHelper;
import edu.utdallas.csmc.web.model.course.Department;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.repository.DepartmentRepository;
import edu.utdallas.csmc.web.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This class defines all the service functions related to the Admin Courses View.
 */
@Service
@Log4j2
public class AdminDepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    AdminDepartmentHelper adminDepartmentHelper = new AdminDepartmentHelper();

    /**
     * This function fetches all the departments with CSMC.
     */
    public List<AdminDepartmentsResultDTO> getDepartmentDetailsList() {
        List<AdminDepartmentsResultDTO> adminDepartmentsResultDTOList = new ArrayList<>();
        List<Department> departmentList = departmentRepository.findAll();
        adminDepartmentHelper.buildDepartmentDetailsList(adminDepartmentsResultDTOList, departmentList);


        return adminDepartmentsResultDTOList;
    }

    /**
     * This function fetches the details about the departments from the database to show to the admin for editing.
     */
    public void getDepartmentDetails(final AdminDepartmentsResultDTO adminDepartmentsResultDTO) {
        Optional<Department> department = departmentRepository.findById(UUID.fromString(adminDepartmentsResultDTO.getDepartmentId()));
        if (department.isPresent()) {
            Department currentDepartment = department.get();
            adminDepartmentHelper.buildDepartmentDetailsFromDepartment(adminDepartmentsResultDTO, currentDepartment);
        } else {
            log.info("Department not found!");
        }
    }

    /**
     * This function deletes the department based on the input id if it is not associated with any courses through Foreign Key.
     */
    public String deleteDepartment(UUID departmentId) {

        Optional<Department> department = departmentRepository.findById(departmentId);
        if (department.isPresent()) {
            try {
                departmentRepository.delete(department.get());
            } catch (ConstraintViolationException ex) {
                log.error(ex);
                // TODO: See how this can be shown on Front End
                log.info("The Department has associated data and cannot be deleted.");
                return "redirect:/admin/department/edit/" + departmentId;
            }
        } else {
            log.info("No Department found for id " + departmentId.toString());
        }

        return "redirect:/admin/department";
    }

    /**
     * This function updates the details about the department in the database.
     */
    public void updateDepartmentDetails(AdminDepartmentsResultDTO adminDepartmentsResultDTO) {

        Optional<Department> department = departmentRepository.findById(UUID.fromString(adminDepartmentsResultDTO.getDepartmentId()));
        if (department.isPresent()) {
            Department currentDepartment = department.get();
            Optional<User> user = userRepository.findByUsername(adminDepartmentsResultDTO.getUsername());

            adminDepartmentHelper.buildUpdatedDepartmentFromDTO(currentDepartment, adminDepartmentsResultDTO, user);

            // Save the updated model back to the database
            departmentRepository.save(currentDepartment);

        } else {
            log.info("Department not Found!");
        }
    }

    /**
     * This function stores the details about the new department in the database.
     */
    public void submitDepartmentDetails(AdminDepartmentsResultDTO adminDepartmentsResultDTO) {
        Optional<User> user = userRepository.findByUsername(adminDepartmentsResultDTO.getUsername());

        Department newDepartment = adminDepartmentHelper.buildNewDepartmentModel(adminDepartmentsResultDTO, user);

        departmentRepository.save(newDepartment);
    }

}
