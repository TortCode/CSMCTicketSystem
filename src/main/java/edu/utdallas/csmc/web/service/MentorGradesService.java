package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.*;
import edu.utdallas.csmc.web.helper.MentorGradesHelper;
import edu.utdallas.csmc.web.model.session.*;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.repository.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
public class MentorGradesService {

    @Autowired
    private ScheduledSessionRepository scheduledSessionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private QuizAttendanceRepository quizAttendanceRepository;

    @Autowired
    private SessionTimeSlotRepository sessionTimeSlotRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScheduledSessionAttendanceRepository scheduledSessionAttendanceRepository;

    @Autowired
    private SessionAttendanceRepository sessionAttendanceRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private QuizTimeSlotRepository quizTimeSlotRepository;

    MentorGradesHelper mentorGradesHelper = new MentorGradesHelper();

    /*
    ** This function will get the all the Scheduled Session for past 2 days.
     */
    public List<MentorGradesSessionQuizResultDTO> getScheduledSession()
    {
        Date d = new Date();
        Date beforeDate = new Date(d.getTime()-(2*24*3600*1000));
        List<ScheduledSession> scheduledSessions = scheduledSessionRepository.getScheduledSessions(d);

        MentorGradesHelper mentorGradesHelper = new MentorGradesHelper();
        List<MentorGradesSessionQuizResultDTO> mentorGradesSessionQuizResultDTO = mentorGradesHelper.setSchedulesSessionDetails(scheduledSessions);

        return mentorGradesSessionQuizResultDTO;
    }

    /*
    ** This function wll get all the Quizzes for past 2 days.
     */
    public List<MentorGradesSessionQuizResultDTO> getQuizzes() {
        Date d = new Date();
        Date beforeDate = new Date(d.getTime()-(2*24*3600*1000));
        List<Quiz> quiz = quizRepository.getQuizzes(d);

        List<MentorGradesSessionQuizResultDTO> mentorGradesSessionQuizResultDTO = mentorGradesHelper.setQuizzes(quiz);

        return mentorGradesSessionQuizResultDTO;
    }

    /*
    ** This function will get the required details to mark student attendance.
     */
    public MentorAttendanceGradesResultDTO getDetailsForAttendance(UUID id) {
        Optional<Session> session = checkSessionType(id);
        String sessionType = session.get().getType().getName();
        if(sessionType.equals("quiz")){
            Quiz attendance = quizRepository.getAttendanceGrade(id);
            MentorAttendanceGradesResultDTO attendanceGrade = mentorGradesHelper.setQuizAttendanceGrades(attendance);
            return attendanceGrade;
        }
        else {
            ScheduledSession attendance = scheduledSessionRepository.getAttendanceGrade(id);
            MentorAttendanceGradesResultDTO attendanceGrade = mentorGradesHelper.setScheduledSessionAttendanceGrades(attendance);
            return attendanceGrade;
        }
    }

    /*
    ** This function will the all the student attended for a particular Scheduled Session or Quiz.
     */
    public ModelMap getDetailsForScheduledSessionOrQuiz(ModelMap model, UUID id) {
        Optional<Session> session = checkSessionType(id);
        MentorGradesHelper mentorGradesHelper = new MentorGradesHelper();
        String sessionType = session.get().getType().getName();

        MentorGradesSessionResultDTO mentorGradesSessionResultDTO = mentorGradesHelper.setSessionDetails(session);
        model.addAttribute("session",mentorGradesSessionResultDTO);

        if(sessionType.equals("quiz")){
            List<QuizAttendance> quizAttendances = quizAttendanceRepository.getQuizAttendanceForGrades(id);
            List<MentorGradesQuizEditDTO> mentorGradesQuizEditDTO = mentorGradesHelper.setQuizAttendance(quizAttendances,session);
            model.addAttribute("quiz",mentorGradesQuizEditDTO);
        }
        else{
            List<SessionTimeSlot> sessionTimeSlotList = sessionTimeSlotRepository.findSessionTimeSlotBySessionId(id);
            List<MentorGradesScheduledSessionEditDTO> mentorGradesScheduledSessionEditDTO =  mentorGradesHelper.setSessionTimeSlotDetails(sessionTimeSlotList,session);
            model.addAttribute("scheduledsession",mentorGradesScheduledSessionEditDTO);
        }
        return model;
    }

    /*
    ** This function will mark student attendance for a Scheduled Session or Quiz.
     */
    public MentorAttendanceGradesResultDTO markStudentAttendance(UUID sessionId, String timeSlot, String netId, String grade) {
        MentorAttendanceGradesResultDTO markStudentAttendance = new MentorAttendanceGradesResultDTO();

        Optional<Session> session = checkSessionType(sessionId);
        String sessionType = session.get().getType().getName();
        markStudentAttendance.setSessionType(sessionType);

        if(sessionType.equals("quiz")) {
            QuizAttendance quizAttendance= new QuizAttendance();

            //Getting the Quiz Object by sessionId as Quiz extends Session
            Optional<Quiz> quiz = quizRepository.findById(sessionId);

            if(!quizTimeSlotRepository.findById(UUID.fromString(timeSlot)).get().hasAttended(netId)) {
                Optional<User> user = userRepository.findByUsername(netId);
                if(user.isPresent()) {
                    quizAttendance.setUser(user.get());
                    quizAttendance.setQuiz(quiz.get());
                    quizAttendance.getQuiz().setTimeSlot(quiz.get().getTimeSlot());
                    quizAttendance.setGrade(Integer.parseInt(grade));
                    quizAttendanceRepository.save(quizAttendance);
                }
            }else{
                markStudentAttendance.setMessage("Student has already attended the quiz");
            }
        }
        /* If session is not a quiz */
        else {
            /* Add record to ScheduledSessionAttendance */
            ScheduledSessionAttendance scheduledSessionAttendance = new ScheduledSessionAttendance();
            Optional<User> user = userRepository.findByUsername(netId);
            if(!session.get().isGraded()) { //if session is not graded then student can attend that session for multiple timeslot.
                if (!sessionTimeSlotRepository.findById(UUID.fromString(timeSlot)).get().hasAttended(netId)) {
                    saveToScheduleSessionAttendance(timeSlot, scheduledSessionAttendance, user);
                }
                else {
                    markStudentAttendance.setMessage("Student has already attended the session for this time slot");
                }
            }
            else { //if session is graded then student can only attend that session once
                Optional<ScheduledSession> scheduledSession = scheduledSessionRepository.findById(sessionId);
                if(!scheduledSession.get().hasAttended(netId)) {
                    saveToScheduleSessionAttendance(timeSlot, scheduledSessionAttendance, user);
                }
                else {
                    markStudentAttendance.setMessage("Student cannot attend graded session twice");
                }
            }
        }
        return markStudentAttendance;
    }

    private void saveToScheduleSessionAttendance(String timeSlot, ScheduledSessionAttendance scheduledSessionAttendance, Optional<User> user) {
        if (user.isPresent()) {
            scheduledSessionAttendance.setUser(user.get());
            Optional<TimeSlot> ts = timeSlotRepository.findById(UUID.fromString(timeSlot));
            scheduledSessionAttendance.setTimeSlot(ts.get());//SessionTimeSlot Type
            scheduledSessionAttendance.setTimeIn(new Date());//Condition that student did not bring card then set time to current time
            scheduledSessionAttendanceRepository.save(scheduledSessionAttendance);
        }
    }

    /*
    ** This function will update the grades of the student
     */
    public void updateGrade(int grade, UUID attendance) {
        Optional<SessionAttendance> sattendance = sessionAttendanceRepository.findById(attendance);

        if(sattendance.isPresent()){
            SessionAttendance sessionAttendance = sattendance.get();
            if(grade>=0){
                sessionAttendance.setGrade(grade);
            }
            else if(grade==-1){
                sessionAttendance.setGrade(1);
            }
            else if(grade==-2){
                sessionAttendance.setGrade(0);
            }
            sessionAttendanceRepository.save(sessionAttendance);
        }
    }

    /*
     ** This function will get all the details for the given Session Id.
     */
    public Optional<Session> checkSessionType(UUID id){
        Optional<Session> session = sessionRepository.findById(id);
        return session;
    }

}
