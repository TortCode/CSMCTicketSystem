package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.schedule.Schedule;
import edu.utdallas.csmc.web.model.schedule.ScheduledShift;
import edu.utdallas.csmc.web.model.schedule.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Transactional
public interface ScheduledShiftRepository extends JpaRepository<ScheduledShift, UUID> {

    ScheduledShift findByScheduleAndShiftAndDate(Schedule schedule, Shift shift, Date date);

}

