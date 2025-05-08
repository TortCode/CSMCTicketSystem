package edu.utdallas.csmc.web.controller;

import edu.utdallas.csmc.web.dto.*;
import edu.utdallas.csmc.web.helper.TicketHelper;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.service.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Optional;
import java.util.UUID;

/**
 * This Class will have the path for all the APIs for the Student.
 */
@Controller
@EnableAutoConfiguration
@CrossOrigin
@Log4j2
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentCourseService studentCourseService;

    @Autowired
    private StudentRegisterService studentRegisterService;

    @Autowired
    private StudentGradesService studentGradesService;

    @Autowired
    StudentTicketService studentTicketService;

    @Autowired
    private DefaultUsernameService defaultUsernameService;



    /*********** Student Courses View ***********/

    /**
     * This API returns the list of Courses for the Sections of the student.
     */
    @RequestMapping("/course/section")
    public String getCourses(ModelMap model) {

        List<StudentCourseResultDTO> result = studentCourseService.getCourseDetails();
        model.addAttribute("sections", result);
        return "role/student/course/courses.html";
    }

    /*********** Student Register View ***********/

    /**
     * This API returns the list of Scheduled Sessions and Quizzes for the Sections of the student.
     */
    @RequestMapping("/session/schedule")
    public String getScheduledSessionsAndQuizzes(ModelMap model) {

        List<StudentScheduleResultDTO> studentScheduleResultDTOList = studentRegisterService.getScheduleDetails();
        model.addAttribute("courses", studentScheduleResultDTOList);
        return "role/student/session/schedule_by_course.html";
    }

    /**
     * This API returns the list of time slots available for the selected session.
     */
    @RequestMapping("/session/schedule/sessions/{sessionId}")
    public String sessionRegisterTimeslotAction(ModelMap model, @PathVariable("sessionId") String sessionId) {
        model = studentRegisterService.getScheduledSessionDetails(model, UUID.fromString(sessionId));
        return "role/student/session/register/timeslots.html";
    }

    /**
     * This API unregisters the student from the selected scheduled session.
     * After unregistering the student is redirected to the Session Schedule page listing all the Scheduled Sessions
     * and Quizzes for the Sections of the Student.
     */
    @RequestMapping("/session/schedule/unregister/{sessionId}")
    public String unregisterSession(ModelMap model, @PathVariable("sessionId") String sessionId) {
        studentRegisterService.removeRegisteredSession(sessionId);
        return "redirect:/student/session/schedule";
    }

    /**
     * This API takes the student to a confirmation page which shows the details of the time slot that the Student
     * selects.
     */
    @RequestMapping("/session/register/timeslot/{tid}")
    public String sessionRegisterForm(ModelMap model, @PathVariable("tid") String timeslotId) {
        StudentSessionTimeSlotResultDTO studentSessionTimeSlotResultDTO = studentRegisterService.getSessionTimeslotDetails(UUID.fromString(timeslotId));
        model.addAttribute("session", studentSessionTimeSlotResultDTO);
        return "role/student/session/register/register.html";
    }

    /**
     * This API registers the student to the time slot that the student selects for a particular session.
     */
    @RequestMapping("/session/register/timeslot/confirm/{tid}")
    public String sessionRegisterConfirm(ModelMap model, @PathVariable("tid") String timeslotId) {
        studentRegisterService.confirmTimeslotRegistration(UUID.fromString(timeslotId));
        return "redirect:/student/session/schedule";
    }

    /*********** Student Grades View ***********/

    /**
     * This API lists the Grades of a student for Scheduled Sessions, Quizzes and Walk-in Sessions.
     */
    @RequestMapping("/session/history")
    public String getGrades(ModelMap model) {

        List<StudentGradesSessionResultDTO> scheduledSessionsAttendance = studentGradesService.getScheduledSessions();
        List<StudentGradesQuizResultDTO> quizAttendance = studentGradesService.getQuizAttendance();
        List<StudentGradesWalkInResultDTO> walkInAttendance = studentGradesService.getWalkinAttendance();

        model.addAttribute("sessions", scheduledSessionsAttendance);
        model.addAttribute("quizzes", quizAttendance);
        model.addAttribute("walkins", walkInAttendance);

        return "role/student/session/history_by_time.html";
    }

    /*********** Student Tickets View ***********/

    // ticket form
    @RequestMapping("/ticket/create")
    public String studentTicketForm(ModelMap model, @RequestParam("tableNo") Optional<Integer> tableNo) {
        TicketDTO ticketDTO = studentTicketService.getTicket();
        if (ticketDTO != null) {
            return "redirect:/student/ticket/progress";
        }
        TicketDTO autofillDTO = studentTicketService.getAutofillTicket();
        tableNo.ifPresent((tno) -> {
            autofillDTO.setTableNo(tno);
        });
        model.addAttribute("autofill", autofillDTO);
        model.addAttribute("courses", studentCourseService.getCourseDetails());
        model.addAttribute("activities", studentTicketService.getWalkInActivities());
        return "role/student/ticket/form.html";
    }

    // ticket in progress page
    @RequestMapping("/ticket/progress")
    public String studentTicketProgress(ModelMap model) {
        TicketDTO ticketDTO = studentTicketService.getTicket();
        if (ticketDTO == null) {
            return "redirect:/student/ticket/create";
        }
        model.addAttribute("ticket", ticketDTO);
        return "role/student/ticket/view.html";
    }

    // form submission endpoint
    @RequestMapping("/ticket/submit")
    public String studentTicketSubmit(
            @RequestParam String course,
            @RequestParam(required = false) String topic,
            @RequestParam String ticketType,
            @RequestParam(required = false) String additionalInfo,
            @RequestParam int tableNo
    ) {
        studentTicketService.buildTicket(course, topic, ticketType, additionalInfo, tableNo);
        return "redirect:/student/ticket/progress";
    }

    // ticket withdraw endpoint
    @RequestMapping("/ticket/withdraw")
    public String studentTicketWithdraw() {
        boolean b = studentTicketService.withdraw();
        if (b) {
            return "redirect:/student/ticket/create";
        }
        return "redirect:/student/ticket/progress";
    }
}
