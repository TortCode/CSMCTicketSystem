package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.StudentScheduleResultDTO;
import edu.utdallas.csmc.web.dto.StudentScheduledSessionDetailsResultDTO;
import edu.utdallas.csmc.web.dto.StudentSessionTimeSlotResultDTO;
import edu.utdallas.csmc.web.dto.StudentSessionTimeSlotsDetailsResultDTO;
import edu.utdallas.csmc.web.helper.StudentRegisterHelper;
import edu.utdallas.csmc.web.model.course.Section;
import edu.utdallas.csmc.web.model.session.Quiz;
import edu.utdallas.csmc.web.model.session.Registration;
import edu.utdallas.csmc.web.model.session.ScheduledSession;
import edu.utdallas.csmc.web.model.session.SessionTimeSlot;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.repository.*;
import edu.utdallas.csmc.web.tasks.Task;
import edu.utdallas.csmc.web.util.DateUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.*;

/**
 * This class defines all the service functions related to the Student Register View.
 */
@Service
@Log4j2
public class StudentRegisterService {

    @Autowired
    private DefaultUsernameService defaultUsernameService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScheduledSessionRepository scheduledSessionRepository;

    @Autowired
    private SessionTimeSlotRepository sessionTimeSlotRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private Task task;

    StudentRegisterHelper studentRegisterHelper = new StudentRegisterHelper();

    /**
     * This function fetches all the details related to scheduled sessions and quizzes for the sections
     * that the student is associated with.
     */
    public List<StudentScheduleResultDTO> getScheduleDetails() {
        List<Section> sectionAndCourseList = sectionRepository.findSectionAndCourseByStudentUsername(defaultUsernameService.getUsername());
        log.info("sectionAndCourseList - {}", sectionAndCourseList);
        List<StudentScheduleResultDTO> studentScheduleResultDTOList = new ArrayList<>();

        for (Section currentSectionAndCourse : sectionAndCourseList) {

            List<ScheduledSession> scheduledSessionList = scheduledSessionRepository.findScheduledSessionBySectionIdAndStartTimeAfterToday(currentSectionAndCourse.getId(), new Date());
            studentRegisterHelper.buildStudentScheduleResultFromScheduledSessions(defaultUsernameService.getUsername(), studentScheduleResultDTOList, currentSectionAndCourse, scheduledSessionList);
            log.info("scheduledSessionList - {}", scheduledSessionList);
            List<Quiz> quizList = quizRepository.getQuizDetails(currentSectionAndCourse.getId(), new Date());
            studentRegisterHelper.buildStudentScheduleResultFromQuizzes(studentScheduleResultDTOList, currentSectionAndCourse, quizList);
            log.info("quizList - {}", quizList);
        }
        return studentScheduleResultDTOList;
    }

    /**
     * This function returns the details related to the selected session and the timeslots associated with it that the
     * student can register for.
     */
    public ModelMap getScheduledSessionDetails(ModelMap model, UUID sessionId) {
        Optional<ScheduledSession> op = scheduledSessionRepository.findById(sessionId);
        ScheduledSession scheduledSession = op.get();
        if (scheduledSession == null) {
            log.info("No session with id: " + sessionId);
        } else if (checkExpireDate(this.getEndDate(scheduledSession))) {
            log.info("Registration Time Expired for session with id: " + sessionId);
        } else {
            StudentScheduledSessionDetailsResultDTO studentScheduledSessionDetailsResultDTO = studentRegisterHelper.buildScheduledSessionDetails(scheduledSession);

            List<StudentSessionTimeSlotsDetailsResultDTO> studentSessionTimeSlotsDetailsResultDTOList = new ArrayList<>();
            List<SessionTimeSlot> scheduledSessionTimeslots = scheduledSession.getTimeslots();
            Collections.sort(scheduledSessionTimeslots, new StartTimeComparator());
            studentRegisterHelper.buildScheduledSessionTimeSlotsDetails(studentSessionTimeSlotsDetailsResultDTOList, scheduledSessionTimeslots);

            model.addAttribute("session", studentScheduledSessionDetailsResultDTO);
            model.addAttribute("timeslots", studentSessionTimeSlotsDetailsResultDTOList);
        }
        return model;
    }

    /**
     * This function unregisters the student from the registered session.
     */
    public void removeRegisteredSession(String sessionID) {
        UUID registrationId = null;
        Optional<ScheduledSession> scheduledSession = scheduledSessionRepository.findById(UUID.fromString(sessionID));
        if (scheduledSession.isPresent()) {
            ScheduledSession currentScheduledSession = scheduledSession.get();
            List<SessionTimeSlot> sessionTimeSlotList = currentScheduledSession.getTimeslots();
            for (SessionTimeSlot currentTimeSlot : sessionTimeSlotList) {
                List<Registration> registrationList = currentTimeSlot.getRegistrations();
                for (Registration currentRegistration : registrationList) {
                    if (currentRegistration.getUser().getUsername().equals(defaultUsernameService.getUsername())) {
                        registrationId = currentRegistration.getId();
                        break;
                    }
                }
            }
            if (registrationId != null) {
                registrationRepository.deleteById(registrationId);
            }
        } else {
            log.info("ScheduledSession not found");
        }

    }

    /**
     * This function fetches the details related to the selected timeslot.
     */
    public StudentSessionTimeSlotResultDTO getSessionTimeslotDetails(UUID timeslotId) {
        Optional<SessionTimeSlot> sessionTimeSlot = sessionTimeSlotRepository.findById(timeslotId);
        StudentSessionTimeSlotResultDTO studentSessionTimeSlotResultDTO = new StudentSessionTimeSlotResultDTO();

        if (sessionTimeSlot.isPresent() && DateUtil.compareTimeWithCurrentTime(sessionTimeSlot.get().getStartTime()) < 0) {
            log.error("Registration time expired for timeslot with id - " + timeslotId.toString());

        } else if (sessionTimeSlot.isPresent() ) {
            studentSessionTimeSlotResultDTO = studentRegisterHelper.buildStudentSessionTimeSlotDetails(sessionTimeSlot.get());

        } else {
            log.error("No such timeslot found with id - " + timeslotId.toString());
        }

        return studentSessionTimeSlotResultDTO;
    }

    /**
     * This function registers the student to the selected timeslot of a particular scheduled session.
     */
    public void confirmTimeslotRegistration(UUID timeslotId) {
        Registration registration = new Registration();
        // get timeslot
        Optional<SessionTimeSlot> sessionTimeSlot = sessionTimeSlotRepository.findById(timeslotId);
        // get user
        Optional<User> user = userRepository.findByUsername(defaultUsernameService.getUsername());
        // add date
        if (sessionTimeSlot.isPresent() && user.isPresent()) {
            User currentUser = user.get();
            SessionTimeSlot slot = sessionTimeSlot.get();
            //check if the user is already registered
            boolean alreadyRegistered = registrationRepository.existsByUserAndTimeSlot(currentUser, slot);
            if(alreadyRegistered) {
                log.info("User {} is already registered for this timeslot", currentUser.getUsername());
                return;
            }

            if(sessionTimeSlot.get().getRemainingSeats() != 0){
                registration.setTime(new Date());
                registration.setUser(user.get());
                registration.setTimeSlot(sessionTimeSlot.get());
                registrationRepository.save(registration);
                String location = registration.getTimeSlot().getLocation().getBuilding()+" "+registration.getTimeSlot().getLocation().getFloor()+"."+registration.getTimeSlot().getLocation().getNumber()+"-"+registration.getTimeSlot().getLocation().getDescription();
                String studentEmail = registration.getUser().getUsername().trim()+"@utdallas.edu";
                task.taskWithConfirmationMailDetails(registration.getTimeSlot().getSession().getTopic()+"="+registration.getTimeSlot().getStartTime()+"="+registration.getTimeSlot().getEndTime()+"="+location+"="+studentEmail+"="+defaultUsernameService.getUsername()+"="+registration.getUser().getFirstName());
            } else {
                log.info("Session filled out. No Remaining Seats");
            }
        }

    }

    /**
     * Helper method to check if provided date is a future date.
     */
    private boolean checkExpireDate(Date endDate) {
        if (endDate == null) return false;

        Date today = new Date();
        Date today_date = DateUtil.trimDate(today);
        log.info(today_date);
        if(endDate.compareTo(today_date) == 0){
            return false;
        }else return endDate.before(today);
        //return endDate.before(today) || endDate.compareTo(today) == 0;
    }

    /**
     * Helper method to find the end date for a particular Scheduled Session.
     */
    public Date getEndDate(ScheduledSession scheduledSession) {
        List<SessionTimeSlot> timeSlots = scheduledSession.getTimeslots();
        if (timeSlots != null && !timeSlots.isEmpty()) {
            Date startTime = timeSlots.get(0).getStartTime();
            Date endDate = DateUtil.trimDate(startTime);
            for (int i = 1; i < timeSlots.size(); i++) {
                Date date = DateUtil.trimDate(timeSlots.get(i).getStartTime());
                if (date.after(endDate)) {
                    endDate = date;
                }
            }
            return endDate;
        }
        return null;
    }

    /**
     * This comparator implementation defines how the SessionTimeSlot should be sorted.
     */
    static class StartTimeComparator implements Comparator<SessionTimeSlot> {
        @Override
        public int compare(SessionTimeSlot o1, SessionTimeSlot o2) {
            if (o1.getStartTime().before(o2.getStartTime())
                    || o1.getStartTime().compareTo(o2.getStartTime()) == 0) {
                return -1;
            }
            return 1;
        }
    }
}

