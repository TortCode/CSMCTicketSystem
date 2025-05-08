package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.TicketDTO;
import edu.utdallas.csmc.web.helper.TicketHelper;
import edu.utdallas.csmc.web.dto.TicketDTO;
import edu.utdallas.csmc.web.model.course.Course;
import edu.utdallas.csmc.web.model.misc.Ticket;
import edu.utdallas.csmc.web.model.misc.TicketStatus;
import edu.utdallas.csmc.web.model.session.Attendance;
import edu.utdallas.csmc.web.model.session.WalkInActivity;
import edu.utdallas.csmc.web.model.session.WalkInAttendance;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.repository.*;
import edu.utdallas.csmc.web.repository.CourseRepository;
import edu.utdallas.csmc.web.repository.TicketRepository;
import edu.utdallas.csmc.web.repository.WalkInAttendanceRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Service
@Log4j2
public class StudentTicketService {

    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private SemesterRepository semesterRepository;
    @Autowired
    private WalkInActivityRepository walkInActivityRepository;
    @Autowired
    private TicketStatusRepository ticketStatusRepository;
    @Autowired
    private DefaultUsernameService defaultUsernameService;
    @Autowired
    private WalkInAttendanceRepository walkInAttendanceRepository;

    public void buildTicket(String courseStr, String topic, String activityStr, String additionalInfo, int tableNo) {
        User u = defaultUsernameService.getUser();
        Ticket ticket = new Ticket();
        ticket.setStudent(u); // set student
        ticket.setTopic(topic);
        Course course = findCourse(courseStr);
        if (course == null) {
            throw new IllegalArgumentException("Invalid course name");
        }
        ticket.setCourse(course);
        WalkInActivity activity = walkInActivityRepository.findByName(activityStr);
        if (activity == null) {
            throw new IllegalArgumentException("Invalid activity type");
        }
        ticket.setActivity(activity);
        ticket.setAdditionalInfo(additionalInfo);
        ticket.setStatus(ticketStatusRepository.findByName("UNCLAIMED"));
        ticket.setSemester(semesterRepository.findByActive(true));
        ticket.setTableNo(tableNo);
        ticket.setCreatedAt(new Date());
        ticketRepository.save(ticket);
    }

    private Course findCourse(String course) {
        List<Course> courses = courseRepository.findCourseBySupported(true);
        for (Course c: courses) {
            if (c.getFullNumber().equals(course)) {
                return c;
            }
        }
        return null;
    }

    public TicketDTO getTicket() {
        User u = defaultUsernameService.getUser();
        Ticket t = ticketRepository.findUnfinishedTicketByStudent(u.getId());
        if (t == null) return null;
        return TicketHelper.ticket2DTO(t);
    }

    public boolean withdraw() {
        User u = defaultUsernameService.getUser();
        Ticket t = ticketRepository.findUnfinishedTicketByStudent(u.getId());
        if (t == null) return false;
        ticketRepository.delete(t);
        return true;
    }

    public List<WalkInActivity> getWalkInActivities() {
        return walkInActivityRepository.findAll();
    }

    public TicketDTO getAutofillTicket() {
        User user = defaultUsernameService.getUser();
        List<Ticket> t = ticketRepository.findRecentFromStudent(user.getId());
        Ticket latestTicket = null;
        if (t != null && !t.isEmpty()) {
            latestTicket = t.get(0);
        }
        List<WalkInAttendance> attendanceList = walkInAttendanceRepository.findByUserOrderByTimeInDesc(user);
        WalkInAttendance latestAttendance = null;
        if (!attendanceList.isEmpty()) {
            latestAttendance = attendanceList.get(0);
        }
        if (latestAttendance != null &&
                (latestTicket == null || latestAttendance.getTimeIn().after(latestTicket.getCreatedAt()))) {
            TicketDTO ticketData = new TicketDTO();
            ticketData.setId(user.getId());
            ticketData.setStudentNetId(user.getUsername());
            ticketData.setStudentName(user.getName());
            WalkInAttendance userAttendance = attendanceList.get(0); // most recent attendance survey
            if(userAttendance.getCourse() != null) {
                ticketData.setCourse(userAttendance.getCourse().getFullNumber());
            }
            ticketData.setTopic(userAttendance.getTopic());
            if(userAttendance.getActivity() != null) {
                ticketData.setType(userAttendance.getActivity().getName());
            }
            return ticketData;
            // mentorNetId, mentorName, info, status, createdAt, resolvedAt will be empty
        } else if (latestTicket != null) {
            return TicketHelper.ticket2DTO(latestTicket);
        }
        // empty dto
        return new TicketDTO();
    }
}
