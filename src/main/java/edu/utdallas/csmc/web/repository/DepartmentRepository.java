package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.course.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepartmentRepository extends JpaRepository<Department, UUID> {

    List<Department> findAllByOrderByName();

    Optional<Department> findDepartmentByName(String name);

}
