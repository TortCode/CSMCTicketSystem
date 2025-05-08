package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.AdminTimesheetResultDTO;
import edu.utdallas.csmc.web.helper.AdminTimesheetHelper;
import edu.utdallas.csmc.web.model.schedule.Timesheet;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.repository.TimesheetRepository;
import edu.utdallas.csmc.web.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@Log4j2
public class AdminTimesheetService {

   @Autowired
    private UserRepository userRepository;

    @Autowired
    private TimesheetRepository timesheetRepository;

    AdminTimesheetHelper adminTimesheetHelper = new AdminTimesheetHelper();

    public List<AdminTimesheetResultDTO> getMentor() {
        List<AdminTimesheetResultDTO> mentors = new ArrayList<>();
        List<User> mentorList = userRepository.findUserByRolesName("mentor");
        adminTimesheetHelper.buildMentorsList(mentors, mentorList);
        return mentors;
    }

    public String mentorName(AdminTimesheetResultDTO adminTimesheetResultDTO){
        String mentorName = userRepository.getMentorName(UUID.fromString(adminTimesheetResultDTO.getUserId()));
        return mentorName;
    }

    public TreeMap<Date,HashMap<Date, Date>> getTimesheet(AdminTimesheetResultDTO adminTimesheetResultDTO) {
        TreeMap<Date, HashMap<Date,Date>> mentorTimeSheet = new TreeMap<Date, HashMap<Date, Date>>();
        Optional<User> mentor = userRepository.findById(UUID.fromString(adminTimesheetResultDTO.getUserId()));
        List<Timesheet> timeSheet= timesheetRepository.getTimesheetForMentor(mentor.get(),adminTimesheetResultDTO.getStartDate(),adminTimesheetResultDTO.getEndDate());

        mentorTimeSheet = adminTimesheetHelper.getTimesheetReport(timeSheet);
        return mentorTimeSheet;
    }

    public int getNoOfDays(AdminTimesheetResultDTO adminTimesheetResultDTO) {
        Date startDate = adminTimesheetResultDTO.getStartDate();
        Date endDate = adminTimesheetResultDTO.getEndDate();
        LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        end = end.plusDays(1);
        long difference = DAYS.between(start,end);
        return (int)difference;

    }

    public void getFinalTimesheet(AdminTimesheetResultDTO adminTimesheetResultDTO) {
        Date startDate = adminTimesheetResultDTO.getStartDate();
        Date endDate = adminTimesheetResultDTO.getEndDate();
        LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        end = end.plusDays(1);
        ZoneId defaultZoneId = ZoneId.systemDefault();
        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            if (! adminTimesheetResultDTO.getTimesheet().containsKey(Date.from(date.atStartOfDay(defaultZoneId).toInstant()))){
                HashMap<Date, Date> times = new HashMap<Date, Date>();
                times.put(null,null);
                adminTimesheetResultDTO.getTimesheet().put(Date.from(date.atStartOfDay(defaultZoneId).toInstant()), times);

            }
        }
       }
}
