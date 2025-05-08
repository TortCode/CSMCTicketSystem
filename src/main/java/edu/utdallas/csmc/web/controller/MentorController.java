package edu.utdallas.csmc.web.controller;

import edu.utdallas.csmc.web.dto.*;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.service.*;
import edu.utdallas.csmc.web.util.DateUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.io.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletResponse;

/**
 * This Class will have the path for all the APIs for the Mentor.
 */
@Controller
@EnableAutoConfiguration
@CrossOrigin
@Log4j2
@RequestMapping("/mentor")
public class MentorController {

    @Autowired
    private MentorTimeSheetReportService mentorTimeSheetReportService;

    @Autowired
    private MentorStaffScheduleService mentorStaffScheduleService;

    @Autowired
    private MentorCalendarService mentorCalendarService;

    @Autowired
    private MentorAbsenceService mentorAbsenceService;

    @Autowired
    private MentorTicketService mentorTicketService;

    @Autowired
    MentorGradesService mentorGradesService;

    @Autowired
    MentorDisplayService mentorDisplayService;

    @Autowired
    FileService fileService;
    @Autowired
    private DefaultUsernameService defaultUsernameService;


    /*********** Mentor Staff Schedule View ***********/

    /**
     * This API returns the staff schedule for 7 days starting from the current
     * date.
     */
    @RequestMapping("/schedule/weekly")
    public String getStaffSchedule(ModelMap model) {

        Date date = DateUtil.atStartOfDay(new Date());
        model = mentorStaffScheduleService.scheduleWeeklyAction(model, date);
        return "role/mentor/schedule/weekly.html";

    }

    /**
     * This API returns the staff schedule for 7 days starting from the date
     * provided.
     */
    @RequestMapping("/schedule/weekly/{date}")
    public String getStaffSchedule(ModelMap model,
            @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {

        model = mentorStaffScheduleService.scheduleWeeklyAction(model, date);
        return "role/mentor/schedule/weekly.html";

    }

    /*********** Mentor Calendar View ***********/

    // API to handle Mentor Calendar View with screen having Quizzes and Sessions
    // tables
    @RequestMapping("/session/schedule")
    public String getCourses(ModelMap model) {
        List<MentorCalendarSessionsResultDTO> sessions = mentorCalendarService.getSessionInformation();
        List<MentorCalendarQuizzesResultDTO> quizzes = mentorCalendarService.getQuizInformation();
        model.addAttribute("timeslots", sessions);
        model.addAttribute("quizzes", quizzes);
        return "role/mentor/session/schedule_by_time.html";
    }

    // API to handle Mentor Calendar View after click on Details button of Quizzes
    // table
    @RequestMapping("/session/view/{quizId}")
    public String getQuizDetails(ModelMap model, @PathVariable("quizId") String quizId) {
        MentorCalendarQuizDetailsResultDTO quizDetails = mentorCalendarService.getQuizDetails(UUID.fromString(quizId));
        model.addAttribute("session", quizDetails);
        model.addAttribute("sessionId", quizId);
        return "role/mentor/session/session.html";
    }

    // API to handle Mentor Calendar View after click on Details button of Sessions
    // table
    @RequestMapping("/session/timeslot/view/{timeSlotId}")
    public String getSessionDetails(ModelMap model, @PathVariable("timeSlotId") String timeSlotId) {
        MentorCalendarSessionDetailsResultDTO sessionDetails = mentorCalendarService
                .getSessionDetails(UUID.fromString(timeSlotId));
        model.addAttribute("timeslot", sessionDetails);
        model.addAttribute("timeslotId", timeSlotId);
        return "role/mentor/session/timeslot.html";
    }

    @RequestMapping(path = "/session/file/download/{fileId}/timeSlot/{timeSlotId}")
    public void downloadSessionFilesForSession(HttpServletResponse response, @PathVariable("fileId") String fileId,
            @PathVariable("timeSlotId") String timeSlotId) {
        edu.utdallas.csmc.web.model.file.File fileObject = fileService.getFileById(UUID.fromString(fileId)).get();
        log.info(fileObject);
        String path = fileObject.getHash().getPath();
        log.info("FileName: " + path);
        String directoryLevelOne = path.substring(0, 2);
        String directoryLevelTwo = path.substring(2, 4);
        String fileName = fileObject.getName();
        log.info(fileName);
        java.io.File file = new java.io.File(
                "./src/main/resources/uploads/" + directoryLevelOne + "/" + directoryLevelTwo + "/" + path);
        String mimeType = fileService.getFileMetaDataByKey(fileObject, "mime");
        String extension = fileService.getFileMetaDataByKey(fileObject, "extension");
        log.info(mimeType);
        if (file.exists()) {
            try {
                response.setContentType(mimeType);
                response.addHeader("Content-Disposition", "attachment; filename=" + fileName + "." + extension);
                response.setContentLength((int) file.length());
                InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                FileCopyUtils.copy(inputStream, response.getOutputStream());
                response.getOutputStream().flush();
                response.addHeader("Refresh", "1; url = /mentor/session/timeslot/view/" + timeSlotId);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    @RequestMapping(path = "/session/file/download/{fileId}/quiz/{quizId}")
    public void downloadSessionFilesForQuiz(HttpServletResponse response, @PathVariable("fileId") String fileId,
            @PathVariable("quizId") String quizId) {
        edu.utdallas.csmc.web.model.file.File fileObject = fileService.getFileById(UUID.fromString(fileId)).get();
        log.info(fileObject);
        String path = fileObject.getHash().getPath();
        log.info("FileName: " + path);
        String directoryLevelOne = path.substring(0, 2);
        String directoryLevelTwo = path.substring(2, 4);
        String fileName = fileObject.getName();
        log.info(fileName);
        java.io.File file = new java.io.File("./src/main/resources/uploads/" + directoryLevelOne + "/" + directoryLevelTwo + "/" + path);
        String mimeType = fileService.getFileMetaDataByKey(fileObject, "mime");
        String extension = fileService.getFileMetaDataByKey(fileObject, "extension");
        log.info(mimeType);
        if (file.exists()) {
            try {
                response.setContentType(mimeType);
                response.addHeader("Content-Disposition", "attachment; filename=" + fileName + "." + extension);
                response.setContentLength((int) file.length());
                InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                FileCopyUtils.copy(inputStream, response.getOutputStream());
                response.getOutputStream().flush();
                response.addHeader("Refresh", "1; url = /mentor/session/view/" + quizId);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // API to handle Mentor Calendar View for Session Start/End/Continue
    @RequestMapping("/swipe/session/{timeSlotId}") // sessionTimeSlotId
    public String swipeSessionAction(ModelMap model, @PathVariable("timeSlotId") String timeSlotId) {
        MentorCalendarSwipeSessionResultDTO sessionDetails = mentorCalendarService
                .getSwipeSessionDetails(UUID.fromString(timeSlotId));
        model.addAttribute("session", sessionDetails);
        return "shared/swipe/session.html";
    }

    // API to handle Mentor Calendar Swipe Session View
    @RequestMapping(value = "/swipe/ajax/session", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<MentorSwipeAjaxReturnMessageDTO> swipeSessionAjaxAction(
            @RequestBody RequestSwipeSessionObject requestObject) throws UnknownHostException {
        MentorSwipeAjaxReturnMessageDTO returnMessageDTO = mentorCalendarService
                .getSessionTimeSlotDetails(requestObject.getTimeSlotId(), requestObject.getScancode());

        if (returnMessageDTO.getSuccessMessage() != null) {
            return ResponseEntity.ok(returnMessageDTO);
        } else {
            return ResponseEntity.badRequest().body(returnMessageDTO);
        }
    }

    // API to handle Mentor Swipe Screen
    @RequestMapping("/swipe/walk_in")
    public String swipeWalkinAction(ModelMap model) {
        return "shared/swipe/walk_in.html";
    }

    // APT to handle Mentor swipe screen after swipe
    @RequestMapping(value = "/swipe/ajax/walk_in", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<MentorSwipeAjaxReturnMessageDTO> swipeWalkInAjaxAction(
            @RequestBody RequestSwipeWalkInObject requestObject) throws UnknownHostException {
        MentorSwipeAjaxReturnMessageDTO returnMessageDTO = mentorCalendarService
                .walkInSwipe(requestObject.getScancode());
        if (returnMessageDTO.getSuccessMessage() != null) {
            return ResponseEntity.ok(returnMessageDTO);
        } else {
            return ResponseEntity.badRequest().body(returnMessageDTO);
        }
    }

    // APT to handle Mentor swipe screen entry form
    @RequestMapping(value = "/swipe/ajax/entry", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<MentorSwipeAjaxReturnMessageDTO> swipeAjaxWalkInEntryAction(
            @RequestBody RequestSwipeEntryObject requestObject) throws UnknownHostException {
        MentorSwipeAjaxReturnMessageDTO returnMessageDTO = mentorCalendarService.getSwipeEntryDetails(
                requestObject.getTopic(), requestObject.getCourse(), requestObject.getActivity(),
                requestObject.getQuiz(), requestObject.getUser());
        if (returnMessageDTO.getSuccessMessage() != null) {
            System.out.println(returnMessageDTO.getSuccessMessage());
            return ResponseEntity.ok(returnMessageDTO);
        } else {
            System.out.println(returnMessageDTO.getErrorMessage());
            return ResponseEntity.badRequest().body(returnMessageDTO);
        }
    }

    // API to handle Mentor swipe screen exit form
    @RequestMapping(value = "/swipe/ajax/exit", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<MentorSwipeAjaxReturnMessageDTO> swipeAjaxWalkInExitAction(
            @RequestBody RequestSwipeExitObject requestObject) throws UnknownHostException {
        MentorSwipeAjaxReturnMessageDTO returnMessageDTO = mentorCalendarService.getSwipeExitDetails(
                requestObject.getMentors(), requestObject.getFeedback(), requestObject.getUser(),
                requestObject.getAttendance());
        if (returnMessageDTO.getSuccessMessage() != null) {
            System.out.println(returnMessageDTO.getSuccessMessage());
            return ResponseEntity.ok(returnMessageDTO);
        } else {
            System.out.println(returnMessageDTO.getErrorMessage());
            return ResponseEntity.badRequest().body(returnMessageDTO);
        }
    }

    // API to handle Mentor swipe screen register form
    @RequestMapping(value = "/swipe/ajax/register", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<MentorSwipeAjaxReturnMessageDTO> swipeAjaxRegisterAction(
            @RequestBody RequestSwipeRegisterObject requestObject) throws UnknownHostException {
        MentorSwipeAjaxReturnMessageDTO returnMessageDTO = mentorCalendarService.getSwipeRegisterDetails(
                requestObject.getUsername(), requestObject.getPassword(), requestObject.getSwipe(),
                requestObject.getSession());
        if (returnMessageDTO.getSuccessMessage() != null) {
            System.out.println(returnMessageDTO.getSuccessMessage());
            return ResponseEntity.ok(returnMessageDTO);
        } else {
            System.out.println(returnMessageDTO.getErrorMessage());
            return ResponseEntity.badRequest().body(returnMessageDTO);
        }
    }

    /*********** Mentor Absences View ***********/

    @RequestMapping("/absence/market")
    public String getAbsences(ModelMap model) {

        MentorAbsenceResultDTO mentorAbsenceResultDTO = mentorAbsenceService.getAbsenceDetails();
        model.addAttribute("absences", mentorAbsenceResultDTO);
        return "role/mentor/absences/absence_market.html";
    }

    @RequestMapping("/absence/create")
    public String createAbsences(ModelMap model) {

        MentorAbsenceFormResultDTO form = mentorAbsenceService.getMentorAbsenceFormResultDTO();
        model.addAttribute("form", form);
        return "role/mentor/absences/absence_form.html";
    }

    @RequestMapping("/absence/create/submit")
    public String submitAbsence(@RequestParam String absenceId, @RequestParam String date,
            @RequestParam(required = false) String time, @RequestParam String reason) throws ParseException {

        mentorAbsenceService.submitAbsence(date, time, reason, absenceId);
        return "redirect:/mentor/absence/market";
    }

    @RequestMapping("/absence/cancel/{absenceId}")
    public String cancelAbsence(@PathVariable("absenceId") String absenceId) {

        mentorAbsenceService.cancelAbsence(UUID.fromString(absenceId));

        return "redirect:/mentor/absence/market";
    }

    @RequestMapping("/absence/update/{absenceId}")
    public String updatedAbsence(ModelMap model, @PathVariable("absenceId") String absenceId) {

        MentorAbsenceFormResultDTO form = mentorAbsenceService.updateAbsence(UUID.fromString(absenceId));
        model.addAttribute("form", form);
        return "role/mentor/absences/absence_form.html";
    }

    @RequestMapping("/absence/relieveClaim/{absenceId}")
    public String relieveShift(@PathVariable("absenceId") String absenceId){
        mentorAbsenceService.relieveShift(UUID.fromString(absenceId));
        return "redirect:/mentor/absence/market";
    }

    @RequestMapping("/absence/claim/{absenceId}")
    public String claimShift(@PathVariable("absenceId") String absenceId) {

        mentorAbsenceService.claimShift(UUID.fromString(absenceId));
        return "redirect:/mentor/absence/market";
    }

    @RequestMapping(value = "/absence/shift", method = RequestMethod.POST, consumes = "application/json", produces = "text/html")
    public ResponseEntity<String> getShiftsByDate(@RequestBody RequestShiftObject requestObject)
            throws UnknownHostException {
        System.out.println(requestObject.getDate());

        List<String> shiftList = mentorAbsenceService.getShiftsByDate(requestObject.getDate());
        if (shiftList != null && !shiftList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("<option value=\"\"> -- select one -- </option>\n");
            for (int i = 0; i < shiftList.size(); i++) {
                sb.append("<option value='" + shiftList.get(i) + "'>" + shiftList.get(i) + "</option>\n");
            }
            return ResponseEntity.ok(sb.toString());
        } else {
            return ResponseEntity.badRequest().body("There is no time slot for the date above");
        }
    }

    /************ Mentor Grades View **********/

    /**
     * This API will get all the Scheduled Session and Quiz for past 2 days for
     * mentor to edit the grades.
     */
    @RequestMapping("/session/history")
    public String getScheduledSessionAndQuiz(ModelMap model) {
        List<MentorGradesSessionQuizResultDTO> scheduledSession = mentorGradesService.getScheduledSession();
        List<MentorGradesSessionQuizResultDTO> quizzes = mentorGradesService.getQuizzes();
        model.addAttribute("sessions", scheduledSession);
        model.addAttribute("quizzes", quizzes);
        return "role/mentor/session/history_by_time.html";
    }

    /**
     * This API will get all the Attended Students and their grades for the selected
     * the Scheduled Session or Quiz.
     */
    @RequestMapping("/session/grades/{Id}") /* Id here is sessionId */
    public String getAttendanceForScheduledSessionOrQuiz(ModelMap model, @PathVariable("Id") String id) {
        model = mentorGradesService.getDetailsForScheduledSessionOrQuiz(model, UUID.fromString(id));
        return "role/mentor/session/grades/grades.html";
    }

    /**
     * This API will fetch all the details required to mark student attendance.
     */
    @RequestMapping("/attend/{Id}") /* Id here is sessionId */
    public String getDetailsToMarkStudentAttendance(ModelMap model, @PathVariable("Id") String id) {
        MentorAttendanceGradesResultDTO attendanceGrade = mentorGradesService
                .getDetailsForAttendance(UUID.fromString(id));
        model.addAttribute("attendanceGrade", attendanceGrade);
        return "role/mentor/session/grades/form.html";
    }

    /**
     * This API will change the grades of the student through Asynchronous AJAX
     * request.
     */
    @RequestMapping(value = "/ajax/session/grades", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<?> gradesEditAjaxAction(
            @RequestBody RequestGradeAttendanceObjectDTO requestGradeAttendanceObjectDTO) {
        mentorGradesService.updateGrade(requestGradeAttendanceObjectDTO.getGrade(),
                requestGradeAttendanceObjectDTO.getAttendance());
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * This API will mark the student attendance after the mentor clicks the submit
     * button.
     */
    @RequestMapping("/attend/submit/{Id}") /* Id here is sessionId */
    public String markStudentAttendance(ModelMap model, @PathVariable("Id") String sessionId,
            @RequestParam(value = "timeSlot") String timeslot, @RequestParam("users") String netId,
            @RequestParam(required = false, value = "grade") String grade) {
        MentorAttendanceGradesResultDTO studentAttendance = mentorGradesService
                .markStudentAttendance(UUID.fromString(sessionId), timeslot, netId, grade);
        model.addAttribute("markStudentAttendance", studentAttendance);
        // TODO: Add flash messages
        return "redirect:/mentor/session/grades/{Id}";
    }

    /*********** Mentor Timesheet Report View ***********/

    private HashMap<Date, HashMap<Date, Date>> usertimesheet;

    @RequestMapping(value = { "timesheet/{dt}", "timesheet" })
    public String getTimeSheet(ModelMap model, @PathVariable(value = "dt", required = false) String weekStartDate) {

        if (usertimesheet == null) {
            usertimesheet = new HashMap<>();
            usertimesheet = mentorTimeSheetReportService.getTimeSheetofUser();
        }
        HashMap<Date, HashMap<Date, Date>> timesheet = mentorTimeSheetReportService.getTimeSheetofUser();
        MentorTimeSheetDTO mentorTimeSheetDTO = new MentorTimeSheetDTO();
        mentorTimeSheetDTO.setTimesheet(timesheet);

        if (weekStartDate == null) {
            mentorTimeSheetDTO.setWeekStartDate(mentorTimeSheetReportService.getCurrentStartDateofTheWeek());
        } else {
            weekStartDate = weekStartDate + " 12:00";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime parsedStartDate = LocalDateTime.parse(weekStartDate, formatter);
            mentorTimeSheetDTO.setWeekStartDate(parsedStartDate);
        }
        model.addAttribute("timesheet", timesheet);
        model.addAttribute("mentorTimeSheetDTO", mentorTimeSheetDTO);
        return "role/mentor/schedule/timesheet.html";
    }

    // Mentor Display Controller
    @RequestMapping("/display")
    public String displayAction(ModelMap model) {
        List<MentorDisplayMentorsResultDTO> mentors = mentorDisplayService.getMentorDetails();
        List<MentorDisplayStudentsResultDTO> students = mentorDisplayService.getStudentDetails();
        List<MentorDisplaySessionsResultDTO> sessions = mentorDisplayService.getSessionDetails();
        List<MentorDisplayQuizzesResultDTO> quizzes = mentorDisplayService.getQuizzDetails();
//        MentorDisplayShiftLeaderResultDTO shift_leader = mentorDisplayService.getShiftLeaderDetails();
        model.addAttribute("mentors", mentors);
        model.addAttribute("students", students);
        model.addAttribute("sessions", sessions);
        model.addAttribute("quizzes", quizzes);
//        model.addAttribute("shift_leader", shift_leader);
        return "shared/display.html";
    }

    @RequestMapping("/ticket/queue")
    public String getTicketQueue(ModelMap model) {
        return "role/mentor/ticket/queue.html";
    }

    @RequestMapping(value = "/ticket/ajax/queue", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<TicketDTO>> getTicketQueueAjax() {
        List<TicketDTO> tickets = Stream.concat(
                mentorTicketService.getUnclaimedTickets().stream(),
                mentorTicketService.getClaimedTickets().stream()
        ).collect(Collectors.toList());
        return ResponseEntity.ok(tickets);
    }

    @RequestMapping("/ticket/view/{ticketId}")
    public String getTicketView(ModelMap model, @PathVariable("ticketId") String ticketId) {
        TicketDTO ticket = mentorTicketService.getMentorTicket(UUID.fromString(ticketId));
        model.addAttribute("ticket", ticket);
        model.addAttribute("mentors", mentorTicketService.getCurrentScheduledMentors());
        return "role/mentor/ticket/view.html";
    }

    @RequestMapping("/ticket/claim/{ticketID}/{mentorId}")
    public String mentorTicketClaim(@PathVariable("ticketID") String ticketID, @PathVariable("mentorId") String mentorId) {
        boolean claimed = mentorTicketService.claim(UUID.fromString(ticketID), UUID.fromString(mentorId));
        if (claimed) {
            return "redirect:/mentor/ticket/view/"+ticketID;
        } else {
            return "redirect:/mentor/ticket/queue";
        }
    }

    @RequestMapping("/ticket/unclaim/{ticketID}")
    public String mentorTicketUnclaim(@PathVariable("ticketID") String ticketID) {
        mentorTicketService.unclaim(UUID.fromString(ticketID));
        return "redirect:/mentor/ticket/queue";
    }

    @RequestMapping("/ticket/finish/{ticketID}")
    public String mentorTicketFinish(@PathVariable("ticketID") String ticketID) {
        boolean finished = mentorTicketService.finish(UUID.fromString(ticketID));
        if (finished) {
            return "redirect:/mentor/ticket/queue";
        } else {
            return "redirect:/mentor/ticket/view/"+ticketID;
        }
    }

    @RequestMapping("/ticket/kill/{ticketID}")
    public String mentorTicketKill(@PathVariable("ticketID") String ticketID) {
        boolean killed = mentorTicketService.kill(UUID.fromString(ticketID));
        if (killed) {
            return "redirect:/mentor/ticket/queue";
        } else {
            return "redirect:/mentor/ticket/view/"+ticketID;
        }
    }
}
