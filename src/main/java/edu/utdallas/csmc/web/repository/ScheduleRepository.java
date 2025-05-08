package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {

}
