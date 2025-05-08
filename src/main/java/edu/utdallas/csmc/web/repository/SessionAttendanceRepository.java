package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.session.SessionAttendance;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SessionAttendanceRepository<T extends SessionAttendance> extends AttendanceRepository<T>{

}
