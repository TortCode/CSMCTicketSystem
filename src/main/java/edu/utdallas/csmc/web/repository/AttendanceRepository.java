package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.session.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

//@NoRepositoryBean
public interface AttendanceRepository<T extends Attendance> extends JpaRepository<T, UUID> {

}
