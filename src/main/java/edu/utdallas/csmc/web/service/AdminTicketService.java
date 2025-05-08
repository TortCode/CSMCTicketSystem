package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.AdminTicketStatsDetailsCourseDTO;
import edu.utdallas.csmc.web.dto.AdminTicketStatsDetailsDTO;
import edu.utdallas.csmc.web.dto.AdminTicketStatsDetailsDayDTO;
import edu.utdallas.csmc.web.model.misc.Semester;
import edu.utdallas.csmc.web.model.misc.Ticket;
import edu.utdallas.csmc.web.model.schedule.ScheduledShift;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.repository.SemesterRepository;
import edu.utdallas.csmc.web.repository.TicketRepository;
import edu.utdallas.csmc.web.repository.UserRepository;
import edu.utdallas.csmc.web.util.DateUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class AdminTicketService {
    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    SemesterRepository semesterRepository;
    @Autowired
    private UserRepository userRepository;

    public AdminTicketStatsDetailsDTO getTicketStatsDetails(UUID mentorId) {
        User mentor = userRepository.getUser(mentorId);
        AdminTicketStatsDetailsDTO details = new AdminTicketStatsDetailsDTO();

        Semester currentSemester = semesterRepository.findByActive(true);

        List<Ticket> tickets = ticketRepository.findBySemesterAndMentor(currentSemester.getId(), mentorId);
        List<Ticket> finishedTickets = tickets.stream()
                .filter((t) -> t.getStatus().getName().equals("FINISHED"))
                .collect(Collectors.toList());

        details.setName(mentor.getName());
        details.setDayDetails(this.getDayDetails(tickets));
        details.setCourseDetails(this.getCourseDetails(tickets, mentor));
        details.setAverageHelpMinutes(this.getMentorAverageHelpTime(finishedTickets));
        details.setAverageTicketsPerWeek(this.getMentorAverageTicketsPerWeek(finishedTickets));
        return details;
    }

    private List<AdminTicketStatsDetailsDayDTO> getDayDetails(List<Ticket> tickets) {
        Map<Date, AdminTicketStatsDetailsDayDTO> claimedByDay = new HashMap<>();

        // Fill claimedByDay with DTOs that have 0 claims
        List<Date> days = getDays();
        for (Date day : days) {
            AdminTicketStatsDetailsDayDTO dayDTO = new AdminTicketStatsDetailsDayDTO();
            dayDTO.setClaimed(0);
            claimedByDay.put(day, dayDTO);
        }

        // Update claims for all days with tickets
        for (Ticket ticket : tickets) {
            Date startOfDay = DateUtil.atStartOfDay(ticket.getCreatedAt());
            if (claimedByDay.containsKey(startOfDay)) {
                AdminTicketStatsDetailsDayDTO dayDTO = claimedByDay.get(startOfDay);
                dayDTO.setClaimed(dayDTO.getClaimed() + 1);
                claimedByDay.replace(startOfDay, dayDTO);
            } else {
                // Ticket exists outside of all days?
            }
        }

        List<AdminTicketStatsDetailsDayDTO> dayDTOs = new ArrayList<>();
        for (Map.Entry<Date, AdminTicketStatsDetailsDayDTO> entry : claimedByDay.entrySet()) {
            AdminTicketStatsDetailsDayDTO dayDTO = entry.getValue();
            dayDTO.setStartOfDay(entry.getKey());
            dayDTOs.add(dayDTO);
        }

        return dayDTOs.stream()
                .sorted(Comparator.comparing(AdminTicketStatsDetailsDayDTO::getStartOfDay))
                .collect(Collectors.toList());
    }

    private List<AdminTicketStatsDetailsCourseDTO> getCourseDetails(List<Ticket> tickets, User mentor) {
        Map<String, AdminTicketStatsDetailsCourseDTO> claimedByCourse = new HashMap<>();
        for (Ticket ticket : tickets) {
            // update claimed by course
            String courseName = ticket.getCourse().getFullNumber();
            AdminTicketStatsDetailsCourseDTO courseDTO = null;
            if (claimedByCourse.containsKey(courseName)) {
                courseDTO = claimedByCourse.get(courseName);
            } else {
                courseDTO = new AdminTicketStatsDetailsCourseDTO();
                courseDTO.setCompleted(0);
                courseDTO.setUnclaimed(0);
            }
            boolean hasLastClaim = ticket.getLastClaim().getUser().getId().equals(mentor.getId());
            String status = ticket.getStatus().getName();
            if (hasLastClaim && status.equals("FINISHED")) {
                courseDTO.setCompleted(courseDTO.getCompleted() + 1);
            } else if (!hasLastClaim || status.equals("UNCLAIMED")) {
                courseDTO.setUnclaimed(courseDTO.getUnclaimed() + 1);
            }
            claimedByCourse.put(courseName, courseDTO);
        }

        List<AdminTicketStatsDetailsCourseDTO> courseDTOs = new ArrayList<>();
        for (Map.Entry<String, AdminTicketStatsDetailsCourseDTO> entry : claimedByCourse.entrySet()) {
            AdminTicketStatsDetailsCourseDTO courseDTO = entry.getValue();
            courseDTO.setCourse(entry.getKey());
            courseDTOs.add(courseDTO);
            //System.out.println("Course " + courseDTO.getCourse() + " " + courseDTO.getUnclaimed() + " " + courseDTO.getCompleted());
        }

        return courseDTOs;
    }

    private int getMentorAverageWaitTime(List<Ticket> tickets) {
        if (tickets.size() == 0) {
            return 0;
        }

        Duration waitTime = Duration.ZERO;
        for (Ticket t : tickets) {
            waitTime.plus(Duration.between(t.getCreatedAt().toInstant(), t.getClaims().get(0).getTimeIn().toInstant()));
        }
        return (int) waitTime.dividedBy(tickets.size()).toMinutes();
    }

    public double getMentorAverageTicketsPerWeek(List<Ticket> tickets) {
        List<Date> days = getDays();
        double weeks = ((Duration.ofMillis(days.get(days.size() - 1).getTime() - days.get(0).getTime())).toDays() / 7);
        double value = tickets.size() / weeks;
        return Math.round(value * 100) / 100.0;
    }

    private List<Date> getDays() {
        List<Date> days = new ArrayList<>();
        List<ScheduledShift> scheduledShifts =
                semesterRepository.findSemesterByActive(true).get().getSchedule().getScheduleShifts();
        Date currentDate = DateUtil.atStartOfDay(new Date());
        for (ScheduledShift s : scheduledShifts) {
            if (DateUtil.atStartOfDay(s.getDate()).before(currentDate)) {
                days.add(s.getDate());
            }
        }
        return days.stream()
                .sorted(Date::compareTo)
                .collect(Collectors.toList());
    }

    private double getMentorAverageHelpTime(List<Ticket> tickets) {
        if (tickets.size() == 0) {
            return 0;
        }

        Duration helpTime = Duration.ZERO;
        for (Ticket t : tickets) {
            if (t.getClaims().size() != 0) {
                helpTime = helpTime.plus(Duration.between(
                        t.getClaims().get(0).getTimeIn().toInstant(),
                        t.getLastClaim().getTimeOut().toInstant()));
            }
        }
        double value = (double) helpTime.dividedBy(tickets.size()).toMinutes();
        return Math.round(value * 100) / 100.0;
    }
}