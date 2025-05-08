package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.schedule.Timesheet;
import edu.utdallas.csmc.web.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Transactional
public interface TimesheetRepository extends JpaRepository<Timesheet, UUID> {

    @Query("Select t from Timesheet t where (t.user = :userId and (t.timeIn >= :startDate and t.timeOut <= :endDate)) ")
    List<Timesheet> getTimesheetForMentor(@Param("userId") User userId , @Param("startDate")Date startDate, @Param("endDate")Date endDate );

    @Query("select t from Timesheet t where t.timeIn >= :today and t.timeOut = null and t.user = :user")
    Timesheet findOnDuty(@Param("user") User user, @Param("today") Date today);

    @Query("Select t from Timesheet t join t.user u where u.username = :netid order by t.timeIn desc")
    List<Timesheet> getTimeSheetForUser(@Param("netid") String netid);

    //Query for Mentor Display view to get mentors details
    //TODO
    @Query("select t from Timesheet t where t.timeIn > :date and t.timeOut is null ")
    List<Timesheet> getSessionDisplayDetails(@Param("date") Date date);

    @Query("select distinct(u.username) from Timesheet t join t.user u \n" +
            "where (t.timeIn between :fsd and :fed) and (t.timeOut between :fsd and :fed)")
    List<String> getMentorsList(@Param("fsd") Date formattedStartDate, @Param("fed") Date formattedEndDate);

    @Query("select sum(TIMESTAMPDIFF(MINUTE, t.timeIn, t.timeOut)/60) from Timesheet t join t.user u \n" +
            "where u.username = :netId and (t.timeIn between :fsd and :fed) and (t.timeOut between :fsd and :fed)")
    Float getTimesheetHoursTotalByMentorUserName(@Param("netId") String netId, @Param("fsd") Date formattedStartDate, @Param("fed") Date formattedEndDate);
}
