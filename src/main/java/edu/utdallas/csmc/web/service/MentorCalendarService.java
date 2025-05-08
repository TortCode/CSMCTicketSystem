package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.*;
import edu.utdallas.csmc.web.helper.MentorCalendarHelper;
import edu.utdallas.csmc.web.model.course.Course;
import edu.utdallas.csmc.web.model.course.Section;
import edu.utdallas.csmc.web.model.misc.IpAddress;
import edu.utdallas.csmc.web.model.misc.Swipe;
import edu.utdallas.csmc.web.model.schedule.ShiftAssignment;
import edu.utdallas.csmc.web.model.schedule.Timesheet;
import edu.utdallas.csmc.web.model.session.*;
import edu.utdallas.csmc.web.model.user.Role;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.repository.*;
import edu.utdallas.csmc.web.util.DateUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Time;
import java.util.*;

@Service
@Log4j2
public class MentorCalendarService {

    private static final String SUCCESS_ATTENDEE_IN = "attendee_in";
    private static final String SUCCESS_ATTENDEE_OUT = "attendee_out";
    private static final String SUCCESS_SESSION_START = "session_start";
    private static final String SUCCESS_SESSION_END = "session_end";
    private static final String SUCCESS_ENTRANCE = "entrance";
    private static final String SUCCESS_EXIT = "exit";
    private static final String SUCCESS_MENTOR_IN = "mentor_in";
    private static final String SUCCESS_MENTOR_OUT = "mentor_out";

    private static final String ERROR_SESSION_ENDED = "session_already_ended";
    private static final String ERROR_UNREGISTERED = "unregistered_user";
    private static final String ERROR_SESSION_NOT_STARTED = "session_needs_starting";
    private static final String ERROR_INELIGIBLE = "ineligible";
    private static final String ERROR_BAD_CREDENTIALS = "bad_credentials";
    private static final String ERROR_NO_USER = "no_user";
    private static final String ERROR_INVALID = "invalid";

    @Autowired
    private SessionTimeSlotRepository sessionTimeSlotRepository;

    @Autowired
    private ScheduledSessionAttendanceRepository scheduledSessionAttendanceRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private IpAddressRepository ipAddressRepository;

    @Autowired
    private SwipeRepository swipeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TimesheetRepository timesheetRepository;

    @Autowired
    private WalkInAttendanceRepository walkInAttendanceRepository;

    @Autowired
    private QuizAttendanceRepository quizAttendanceRepository;

    @Autowired
    private WalkInActivityRepository walkInActivityRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private RoleRepository roleRepository;

    MentorCalendarHelper mentorCalendarHelper = new MentorCalendarHelper();

    //Service function to get Session Information to display in Sessions table on Mentor Calendar view
    public List<MentorCalendarSessionsResultDTO> getSessionInformation() {
        List<SessionTimeSlot> sessions = sessionTimeSlotRepository.getSessionInformation(DateUtil.atStartOfDay(new Date()));

        List<MentorCalendarSessionsResultDTO> mentorCalendarSessionsResultDTOS = mentorCalendarHelper.setSessionInformation(sessions);

        return mentorCalendarSessionsResultDTOS;
    }

    //Service function to get Quiz Information to display in Quizzes table on Mentor Calendar view
    public List<MentorCalendarQuizzesResultDTO> getQuizInformation() {
        List<Quiz> quizzes = quizRepository.getQuizzesForCalendar(DateUtil.atStartOfDay(new Date()));

        List<MentorCalendarQuizzesResultDTO> mentorCalendarQuizzesResultDTOS = mentorCalendarHelper.setQuizInformation(quizzes);

        return mentorCalendarQuizzesResultDTOS;
    }

    //Service function to get one particular quiz details to display on Mentor Calendar view when Details button of Quizzes table is clicked
    public MentorCalendarQuizDetailsResultDTO getQuizDetails(UUID quizId) {
        Quiz quizDetails = quizRepository.getQuizDetails(quizId);

        MentorCalendarQuizDetailsResultDTO mentorCalendarQuizDetailsResultDTO = mentorCalendarHelper.setQuizDetails(quizDetails);

        return mentorCalendarQuizDetailsResultDTO;
    }

    //Service function to get one particular session details to display on Mentor Calendar view when Details button of Sessions table is clicked
    public MentorCalendarSessionDetailsResultDTO getSessionDetails(UUID timeSlotId) {
        MentorCalendarSessionDetailsResultDTO mentorCalendarSessionDetailsResultDTO = new MentorCalendarSessionDetailsResultDTO();
        Optional<SessionTimeSlot> timeSlot = sessionTimeSlotRepository.findById(timeSlotId);
        if (timeSlot.isPresent()) {
            SessionTimeSlot sessionDetails = timeSlot.get();
            mentorCalendarSessionDetailsResultDTO = mentorCalendarHelper.setSessionDetails(sessionDetails);

        } else {
            log.info("No Time slot found with id : " + timeSlotId);
        }

        return mentorCalendarSessionDetailsResultDTO;
    }

    public MentorCalendarSwipeSessionResultDTO getSwipeSessionDetails(UUID timeSlotId) {
        MentorCalendarSwipeSessionResultDTO mentorCalendarSwipeSessionResultDTO = new MentorCalendarSwipeSessionResultDTO();
        Optional<SessionTimeSlot> timeSlot = sessionTimeSlotRepository.findById(timeSlotId);
        if (timeSlot.isPresent()) {
            SessionTimeSlot sessionDetails = timeSlot.get();
            mentorCalendarSwipeSessionResultDTO = mentorCalendarHelper.setSwipeSessionDetails(sessionDetails);
        } else {
            log.info("No Time slot found with id : " + timeSlotId);
        }
        return mentorCalendarSwipeSessionResultDTO;
    }

    public MentorSwipeAjaxReturnMessageDTO getSessionTimeSlotDetails(UUID timeSlotId, String cardId) throws UnknownHostException {
        Optional<SessionTimeSlot> timeSlot = sessionTimeSlotRepository.findById(timeSlotId);
        if (timeSlot.isPresent()) {
            SessionTimeSlot sessionDetails = timeSlot.get();
            return sessionSwipe(sessionDetails, cardId);
        } else {
            log.info("No Time slot found with id : " + timeSlotId);
        }
        return null;
    }

    public MentorSwipeAjaxReturnMessageDTO sessionSwipe(SessionTimeSlot timeSlot, String cardId) throws UnknownHostException {
        MentorSwipeAjaxReturnMessageDTO mentorSwipeAjaxReturnMessageDTO = new MentorSwipeAjaxReturnMessageDTO();
        if (timeSlot.hasEnded()) {
            mentorSwipeAjaxReturnMessageDTO.setErrorMessage(ERROR_SESSION_ENDED);
            return mentorSwipeAjaxReturnMessageDTO;
        }
        User user;
        if(cardId.contains(":")){
            Optional<User> userInfo = userRepository.findByCardId(cardId);
            if (userInfo.isPresent()) {
                user = userInfo.get();
            } else {
                mentorSwipeAjaxReturnMessageDTO.setErrorMessage(ERROR_UNREGISTERED);
                mentorSwipeAjaxReturnMessageDTO.setSwipe(cardId);
                return mentorSwipeAjaxReturnMessageDTO;
            }
        }
        else{
            User user_Net_Id = userRepository.getUser(cardId);
            if(user_Net_Id != null){
                user = user_Net_Id;
            }
            else{
                mentorSwipeAjaxReturnMessageDTO.setErrorMessage(ERROR_UNREGISTERED);
                mentorSwipeAjaxReturnMessageDTO.setSwipe(cardId);
                return mentorSwipeAjaxReturnMessageDTO;
            }
        }

        setSwipeRecord(user);

        for (Role role : user.getRoles()) {
            if (role.getName().equals("mentor")) {
                Timesheet timesheet = timesheetRepository.findOnDuty(user, DateUtil.atStartOfDay(new Date()));
                if (timesheet != null) {
                    timesheet.setTimeOut(new Date());
                    timesheetRepository.save(timesheet);
                    log.info("success_mentor_timeOut");
                } else {
                    Timesheet timesheetMentor = new Timesheet();
                    timesheetMentor.setTimeIn(new Date());
                    timesheetMentor.setUser(user);
                    timesheetRepository.save(timesheetMentor);
                    log.info("success_mentor_timeIn");
                }

                if (timeSlot.hasStarted()) {
                    timeSlot.setActualEndTime(new Time(new Date().getTime()));
                    sessionTimeSlotRepository.save(timeSlot);
                    mentorSwipeAjaxReturnMessageDTO.setSuccessMessage(SUCCESS_SESSION_END);
                    return mentorSwipeAjaxReturnMessageDTO;
                }
                timeSlot.setActualStartTime(new Time(new Date().getTime()));
                sessionTimeSlotRepository.save(timeSlot);
                mentorSwipeAjaxReturnMessageDTO.setSuccessMessage(SUCCESS_SESSION_START);
                return mentorSwipeAjaxReturnMessageDTO;
            }
        }

        if (!timeSlot.hasStarted()) {
            mentorSwipeAjaxReturnMessageDTO.setErrorMessage(ERROR_SESSION_NOT_STARTED);
            return mentorSwipeAjaxReturnMessageDTO;
        }

        ScheduledSessionAttendance scheduledSessionAttendance = new ScheduledSessionAttendance();
        ScheduledSessionAttendance savedObject;
        if (!timeSlot.hasAttended(user.getUsername())) {
            if (!eligible(user, timeSlot)) {
                mentorSwipeAjaxReturnMessageDTO.setErrorMessage(ERROR_INELIGIBLE);
                return mentorSwipeAjaxReturnMessageDTO;
            }

            scheduledSessionAttendance.setUser(user);
            scheduledSessionAttendance.setTimeIn(new Date());
            scheduledSessionAttendance.setTimeSlot(timeSlot);
            savedObject = scheduledSessionAttendanceRepository.save(scheduledSessionAttendance);

            mentorSwipeAjaxReturnMessageDTO.setSuccessMessage(SUCCESS_ATTENDEE_IN);
            mentorSwipeAjaxReturnMessageDTO.setFirstName(savedObject.getUser().getFirstName());
            mentorSwipeAjaxReturnMessageDTO.setUserId(savedObject.getUser().getId());
            mentorSwipeAjaxReturnMessageDTO.setTimeIn(DateUtil.timeFormatTo12Hours.format(savedObject.getTimeIn()));
            return mentorSwipeAjaxReturnMessageDTO;
        } else {
            Optional<ScheduledSessionAttendance> schSesAttendance = scheduledSessionAttendanceRepository.findByUserAndTimeSlot(user, timeSlot);
            if (schSesAttendance.isPresent())
                scheduledSessionAttendance = schSesAttendance.get();

            scheduledSessionAttendance.setTimeOut(new Date());
            List<User> mentors = new ArrayList<>();
            for (ShiftAssignment assignment : timeSlot.getAssignments()) {
                mentors.add(assignment.getMentor());
            }
            scheduledSessionAttendance.setMentors(mentors);
            savedObject = scheduledSessionAttendanceRepository.save(scheduledSessionAttendance);

            mentorSwipeAjaxReturnMessageDTO.setSuccessMessage(SUCCESS_ATTENDEE_OUT);
            mentorSwipeAjaxReturnMessageDTO.setFirstName(savedObject.getUser().getFirstName());
            mentorSwipeAjaxReturnMessageDTO.setUserId(savedObject.getUser().getId());
            mentorSwipeAjaxReturnMessageDTO.setTimeOut(DateUtil.timeFormatTo12Hours.format(savedObject.getTimeOut()));
            return mentorSwipeAjaxReturnMessageDTO;

        }
    }

    public boolean eligible(User user, SessionTimeSlot timeSlot) {
        List<Section> sections = timeSlot.getSession().getSections();
        List<User> secUser = new ArrayList<>();
        for (Section section : sections) {
            secUser = section.getStudents();
            for (User u : secUser) {
                if (u.getUsername().equals(user.getUsername()))
                    return true;
            }
        }
        return false;
    }

    public MentorSwipeAjaxReturnMessageDTO walkInSwipe(String scancode) throws UnknownHostException {
        MentorSwipeAjaxReturnMessageDTO mentorSwipeAjaxReturnMessageDTO = new MentorSwipeAjaxReturnMessageDTO();

        User user;
        if(scancode.contains(":")){
            Optional<User> userInfo = userRepository.findByCardId(scancode);
            if (userInfo.isPresent()) {
                user = userInfo.get();
            } else {
                mentorSwipeAjaxReturnMessageDTO.setErrorMessage(ERROR_UNREGISTERED);
                mentorSwipeAjaxReturnMessageDTO.setSwipe(scancode);
                return mentorSwipeAjaxReturnMessageDTO;
            }
        }
        else{
            User user_Net_Id = userRepository.getUser(scancode);
            if(user_Net_Id != null){
                user = user_Net_Id;
            }
            else{
                mentorSwipeAjaxReturnMessageDTO.setErrorMessage(ERROR_UNREGISTERED);
                mentorSwipeAjaxReturnMessageDTO.setSwipe(scancode);
                return mentorSwipeAjaxReturnMessageDTO;
            }
        }

        setSwipeRecord(user);

        for (Role role : user.getRoles()) {
            if (role.getName().equals("mentor")) {
                Timesheet timesheet = timesheetRepository.findOnDuty(user, DateUtil.atStartOfDay(new Date()));
                Timesheet savedObject = new Timesheet();
                if (timesheet != null) {
                    timesheet.setTimeOut(new Date());
                    savedObject = timesheetRepository.save(timesheet);
                    log.info("success_mentor_timeOut");
                    mentorSwipeAjaxReturnMessageDTO.setSuccessMessage(SUCCESS_MENTOR_OUT);

                    if (savedObject.getUser().getProfile() != null && savedObject.getUser().getProfile().getPreferredName() != null)
                        mentorSwipeAjaxReturnMessageDTO.setPreferredName(savedObject.getUser().getProfile().getPreferredName());
                    else
                        mentorSwipeAjaxReturnMessageDTO.setPreferredName(savedObject.getUser().getFirstName());

                    return mentorSwipeAjaxReturnMessageDTO;
                }
                Timesheet timesheetMentor = new Timesheet();
                timesheetMentor.setTimeIn(new Date());
                timesheetMentor.setUser(user);
                savedObject = timesheetRepository.save(timesheetMentor);
                log.info("success_mentor_timeIn");
                mentorSwipeAjaxReturnMessageDTO.setSuccessMessage(SUCCESS_MENTOR_IN);

                if (savedObject.getUser().getProfile() != null && savedObject.getUser().getProfile().getPreferredName() != null)
                    mentorSwipeAjaxReturnMessageDTO.setPreferredName(savedObject.getUser().getProfile().getPreferredName());
                else
                    mentorSwipeAjaxReturnMessageDTO.setPreferredName(savedObject.getUser().getFirstName());

                return mentorSwipeAjaxReturnMessageDTO;
            }
        }

        WalkInAttendance walkInAttendance = walkInAttendanceRepository.findCurrentUser(user, DateUtil.atStartOfDay(new Date()));
        if (walkInAttendance != null) {
            mentorSwipeAjaxReturnMessageDTO.setSuccessMessage(SUCCESS_EXIT);
            mentorSwipeAjaxReturnMessageDTO.setUserId(user.getId());
            mentorSwipeAjaxReturnMessageDTO.setMentors(getMentors());
            mentorSwipeAjaxReturnMessageDTO.setAttendance("walkin");
            return mentorSwipeAjaxReturnMessageDTO;
        }

        QuizAttendance quizAttendance = quizAttendanceRepository.findCurrentUser(user, DateUtil.atStartOfDay(new Date()));
        if (quizAttendance != null) {
            mentorSwipeAjaxReturnMessageDTO.setSuccessMessage(SUCCESS_EXIT);
            mentorSwipeAjaxReturnMessageDTO.setUserId(user.getId());
            mentorSwipeAjaxReturnMessageDTO.setMentors(getMentors());
            mentorSwipeAjaxReturnMessageDTO.setAttendance("quiz");
            return mentorSwipeAjaxReturnMessageDTO;
        }

        log.info("SUCCESS_ENTRANCE");
        mentorSwipeAjaxReturnMessageDTO.setSuccessMessage(SUCCESS_ENTRANCE);
        mentorSwipeAjaxReturnMessageDTO.setUserId(user.getId());
        mentorSwipeAjaxReturnMessageDTO.setActivity(walkInActivityRepository.findAll());

        List<Course> courseList = courseRepository.findCourses();
        mentorSwipeAjaxReturnMessageDTO.setSupportedCourses(getSupportedCourses(courseList));
        mentorSwipeAjaxReturnMessageDTO.setUnsupportedCourses(getUnsupportedCourses(courseList));

        mentorSwipeAjaxReturnMessageDTO.setQuizzes(getQuizzes());

        return mentorSwipeAjaxReturnMessageDTO;
    }

    public List<MentorSwipeEntryCourseResultDTO> getMentors() {
        List<MentorSwipeEntryCourseResultDTO> mentors = new ArrayList<>();
        List<User> users = userRepository.getMentors();
        for (User user : users) {
            MentorSwipeEntryCourseResultDTO mentorSwipeEntryCourseResultDTO = new MentorSwipeEntryCourseResultDTO();
            mentorSwipeEntryCourseResultDTO.setId(user.getId());

            if (user.getProfile() != null && user.getProfile().getPreferredName() != null)
                mentorSwipeEntryCourseResultDTO.setName(user.getProfile().getPreferredName());
            else
                mentorSwipeEntryCourseResultDTO.setName(user.getFirstName());

            mentors.add(mentorSwipeEntryCourseResultDTO);
        }
        return mentors;
    }

    public List<MentorSwipeEntryCourseResultDTO> getQuizzes() {
        List<MentorSwipeEntryCourseResultDTO> quizzes = new ArrayList<>();
        List<Quiz> quiz = quizRepository.getQuizForEntry(DateUtil.atEndOfDay(new Date()), new Date());
        for (Quiz q : quiz) {
            MentorSwipeEntryCourseResultDTO quz = new MentorSwipeEntryCourseResultDTO();
            quz.setName(q.getTopic());
            quz.setId(q.getId());
            quizzes.add(quz);
        }
        return quizzes;
    }

    public List<MentorSwipeEntryCourseResultDTO> getSupportedCourses(List<Course> courseList) {
        List<MentorSwipeEntryCourseResultDTO> courses = new ArrayList<>();
        for (Course c : courseList) {
            MentorSwipeEntryCourseResultDTO course = new MentorSwipeEntryCourseResultDTO();
            if (c.isSupported()) {
                course.setName(c.getDepartment().getAbbreviation() + " " + c.getNumber());
                course.setId(c.getId());
                courses.add(course);
            } else
                break;
        }
        return courses;
    }

    public List<MentorSwipeEntryCourseResultDTO> getUnsupportedCourses(List<Course> courseList) {
        List<MentorSwipeEntryCourseResultDTO> courses = new ArrayList<>();
        for (Course c : courseList) {
            MentorSwipeEntryCourseResultDTO course = new MentorSwipeEntryCourseResultDTO();
            if (!c.isSupported()) {
                course.setName(c.getDepartment().getAbbreviation() + " " + c.getNumber());
                course.setId(c.getId());
                courses.add(course);
            }
        }
        return courses;
    }

    public MentorSwipeAjaxReturnMessageDTO getSwipeEntryDetails(String topic, String courseId, String activityId, String quizId, String userId) {
        Optional<User> users = userRepository.findById(UUID.fromString(userId));
        User user = null;
        if (users.isPresent()) {
            user = users.get();
        }

        Optional<WalkInActivity> walkInActivity = walkInActivityRepository.findById(UUID.fromString(activityId));
        WalkInActivity activity = null;
        if (walkInActivity.isPresent()) {
            activity = walkInActivity.get();
        }

        Optional<Course> courses = courseRepository.findById(UUID.fromString(courseId));
        Course course = null;
        if (courses.isPresent()) {
            course = courses.get();
        }

        Quiz quiz = null;
        if (activity.getName().equals("Take a Quiz")) {
            Optional<Quiz> quizzes = quizRepository.findById(UUID.fromString(quizId));
            if (quizzes.isPresent()) {
                quiz = quizzes.get();
            }
        }

        return entry(user, topic, activity, course, quiz);
    }

    public MentorSwipeAjaxReturnMessageDTO entry(User user, String topic, WalkInActivity activity, Course course, Quiz quiz) {
        MentorSwipeAjaxReturnMessageDTO mentorSwipeAjaxReturnMessageDTO = new MentorSwipeAjaxReturnMessageDTO();
        if (quiz != null) {
            QuizTimeSlot timeSlot = quiz.getTimeSlot();
            QuizAttendance quizAttendance = new QuizAttendance();
            quizAttendance.setQuiz(quiz);
            quizAttendance.getQuiz().setTimeSlot(timeSlot);
            quizAttendance.setUser(user);
            quizAttendance.setTimeIn(new Date());
            quizAttendanceRepository.save(quizAttendance);
        } else {
            WalkInAttendance walkInAttendance = new WalkInAttendance();
            walkInAttendance.setActivity(activity);
            walkInAttendance.setCourse(course);
            walkInAttendance.setTopic(topic);
            walkInAttendance.setUser(user);
            walkInAttendance.setTimeIn(new Date());
            walkInAttendanceRepository.save(walkInAttendance);
        }
        mentorSwipeAjaxReturnMessageDTO.setSuccessMessage(SUCCESS_ATTENDEE_IN);

        if (user.getProfile() != null && user.getProfile().getPreferredName() != null)
            mentorSwipeAjaxReturnMessageDTO.setPreferredName(user.getProfile().getPreferredName());
        else
            mentorSwipeAjaxReturnMessageDTO.setPreferredName(user.getFirstName());

        return mentorSwipeAjaxReturnMessageDTO;
    }

    public MentorSwipeAjaxReturnMessageDTO getSwipeExitDetails(List<String> mentorId, String feedback, String userId, String attendance) {
        Optional<User> users = userRepository.findById(UUID.fromString(userId));
        User user = null;
        if (users.isPresent()) {
            user = users.get();
        }

        List<User> mentors = new ArrayList<>();
        for (String m : mentorId) {
            Optional<User> mentor = userRepository.findById(UUID.fromString(m));
            if (mentor.isPresent()) {
                mentors.add(mentor.get());
            }
        }

        return exit(user, mentors, feedback, attendance);
    }

    public MentorSwipeAjaxReturnMessageDTO exit(User user, List<User> mentors, String feedback, String attendance) {
        MentorSwipeAjaxReturnMessageDTO mentorSwipeAjaxReturnMessageDTO = new MentorSwipeAjaxReturnMessageDTO();
        if (attendance.equals("walkin")) {
            WalkInAttendance walkInAttendance = walkInAttendanceRepository.findCurrentUser(user, DateUtil.atStartOfDay(new Date()));
            walkInAttendance.setFeedback(feedback);
            walkInAttendance.setMentors(mentors);
            walkInAttendance.setTimeOut(new Date());
            walkInAttendanceRepository.save(walkInAttendance);
        }
        if (attendance.equals("quiz")) {
            QuizAttendance quizAttendance = quizAttendanceRepository.findCurrentUser(user, DateUtil.atStartOfDay(new Date()));
            quizAttendance.setFeedback(feedback);
            quizAttendance.setMentors(mentors);
            quizAttendance.setTimeOut(new Date());
            quizAttendanceRepository.save(quizAttendance);
        }
        mentorSwipeAjaxReturnMessageDTO.setSuccessMessage(SUCCESS_ATTENDEE_OUT);

        if (user.getProfile() != null && user.getProfile().getPreferredName() != null)
            mentorSwipeAjaxReturnMessageDTO.setPreferredName(user.getProfile().getPreferredName());
        else
            mentorSwipeAjaxReturnMessageDTO.setPreferredName(user.getFirstName());

        return mentorSwipeAjaxReturnMessageDTO;
    }

    public MentorSwipeAjaxReturnMessageDTO getSwipeRegisterDetails(String username, String password, String cardId, String sessionTimeSlotId) throws UnknownHostException {
        MentorSwipeAjaxReturnMessageDTO mentorSwipeAjaxReturnMessageDTO = new MentorSwipeAjaxReturnMessageDTO();
        Boolean check = register(username, password, cardId, true);

        if (!check) {
            mentorSwipeAjaxReturnMessageDTO.setErrorMessage(ERROR_BAD_CREDENTIALS);
            return mentorSwipeAjaxReturnMessageDTO;
        } else if (check == null) {
            mentorSwipeAjaxReturnMessageDTO.setErrorMessage(ERROR_NO_USER);
            return mentorSwipeAjaxReturnMessageDTO;
        }

        if (sessionTimeSlotId != null) {
            Optional<SessionTimeSlot> sessionTimeSlot = sessionTimeSlotRepository.findById(UUID.fromString(sessionTimeSlotId));
            return sessionSwipe(sessionTimeSlot.get(), cardId);
        } else {
            return walkInSwipe(cardId);
        }
    }

    private Boolean register(String username, String password, String cardID, boolean create) throws UnknownHostException {
        RegisterLdapObject registerLdapObject = ldapQuery(username, password, create);

        if (!registerLdapObject.isValid()) {
            return false;
        } else if (registerLdapObject == null) {
            return null;
        }

        User user = registerLdapObject.getUser();
        setSwipeRecord(user);

        user.setCardId(cardID);
        userRepository.save(user);

        return true;
    }

    private RegisterLdapObject ldapQuery(String username, String password, boolean create) {
        RegisterLdapObject registerLdapObject = new RegisterLdapObject();
        LdapSSOObject ldapSSOObject = SSO(username, password);

        if (!ldapSSOObject.isValid()) {
            registerLdapObject.setValid(false);
            return registerLdapObject;
        }

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            registerLdapObject.setValid(true);
            registerLdapObject.setUser(user.get());
        } else if (!user.isPresent() && create) {
            String firstName = ldapSSOObject.getFirstName();
            String lastName = ldapSSOObject.getLastName();

            Role role = roleRepository.findByName("student").get();
            List<Role> roles = new ArrayList<>();
            roles.add(role);

            User toSave = new User();
            toSave.setFirstName(firstName);
            toSave.setLastName(lastName);
            toSave.setUsername(username);
            toSave.setRoles(roles);
            User savedObject = userRepository.save(toSave);

            registerLdapObject.setValid(true);
            registerLdapObject.setUser(savedObject);
        } else {
            return null;
        }
        return registerLdapObject;
    }

    private LdapSSOObject SSO(String username, String password) {
        //TODO: implement the Single Sign on (SSO) here for the register flow.
        LdapSSOObject ldapSSOObject = new LdapSSOObject();
        //getters and setters has already been added in LdapSSOObject class for all the variables
        //if the login is successful set the valid variable as true and set the first name and last name in ldapSSOObject and then return it.
        //if the login is not successful set the valid variable in ldapSSOObject as false and then return it.
        ldapSSOObject.setValid(true);
        ldapSSOObject.setFirstName("test_firstname");
        ldapSSOObject.setLastName("test_lastname");

        return ldapSSOObject;
    }


    private void setSwipeRecord(User user) throws UnknownHostException {
        //TODO: Use the method to convert IP to long in IP class in Util package after merging in into Admin Screen
        String ip = InetAddress.getLocalHost().getHostAddress();
        char arr[] = ip.toCharArray();
        Long address = 0l;
        for (char t : arr) {
            if (t != '.') {
                int temp = Integer.parseInt(String.valueOf(t));
                address = address * 10 + temp;
            }
        }
        Optional<IpAddress> object = ipAddressRepository.findByAddress(address);
        //log.info(address);
        IpAddress ipAddress;
        if (object.isPresent()) {
            ipAddress = object.get();
        } else {
            IpAddress tosave = new IpAddress();
            tosave.setAddress(address);
            tosave.setBlocked(false);
            ipAddress = ipAddressRepository.save(tosave);
        }
        Swipe swipe = new Swipe();
        swipe.setIp(ipAddress);
        swipe.setValid(true);
        swipe.setLegacy(false); //TODO: Check if its always false
        swipe.setUser(user);
        swipe.setTime(new Date());
        swipeRepository.save(swipe);
    }
}
