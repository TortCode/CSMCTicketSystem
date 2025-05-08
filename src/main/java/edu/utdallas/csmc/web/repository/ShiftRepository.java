package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.schedule.Schedule;
import edu.utdallas.csmc.web.model.schedule.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Transactional
public interface ShiftRepository extends JpaRepository<Shift, UUID> {

    List<Shift> findBySchedule(Schedule schedule);

    //Query for Mentor Display view to get shift leader
    @Query("select s from Shift s join s.schedule sc join sc.semester se where se.active = true and s.day = :todayWeekday and s.startTime <= :now and s.endTime > :now")
    Shift getShiftLeaderDisplayDetails(@Param("now") Date now, @Param("todayWeekday") Integer todayWeekday);

}
