package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.StudentGradesQuizResultDTO;
import edu.utdallas.csmc.web.dto.StudentGradesSessionResultDTO;
import edu.utdallas.csmc.web.dto.StudentGradesWalkInResultDTO;
import edu.utdallas.csmc.web.helper.StudentGradesHelper;
import edu.utdallas.csmc.web.model.session.QuizAttendance;
import edu.utdallas.csmc.web.model.session.ScheduledSession;
import edu.utdallas.csmc.web.model.session.ScheduledSessionAttendance;
import edu.utdallas.csmc.web.model.session.WalkInAttendance;
import edu.utdallas.csmc.web.repository.QuizAttendanceRepository;
import edu.utdallas.csmc.web.repository.ScheduledSessionAttendanceRepository;
import edu.utdallas.csmc.web.repository.ScheduledSessionRepository;
import edu.utdallas.csmc.web.repository.WalkInAttendanceRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class defines all the service functions related to the Student Grades View.
 */
@Service
@Log4j2
public class StudentGradesService {

    @Autowired
    private DefaultUsernameService defaultUsernameService;

    @Autowired
    private ScheduledSessionRepository scheduledSessionRepository;

    @Autowired
    private ScheduledSessionAttendanceRepository scheduledSessionAttendanceRepository;

    @Autowired
    private QuizAttendanceRepository quizAttendanceRepository;

    @Autowired
    private WalkInAttendanceRepository walkInAttendanceRepository;

    /**
     * This function fetches the grades of all the scheduled sessions attended by the student.
     */
    public List<StudentGradesSessionResultDTO> getScheduledSessions() {
        List<ScheduledSessionAttendance> scheduledSessions = scheduledSessionAttendanceRepository.getScheduledSessions(defaultUsernameService.getUsername());

        List<UUID> res = new ArrayList<>();
        log.info("scheduledSessions Count - {}",scheduledSessions.size());
        for (ScheduledSessionAttendance s : scheduledSessions) {
            log.info("s.getTimeSlot().getId() - {}",s.getTimeSlot().getId());
            res.add(s.getTimeSlot().getId());
        }

        List<ScheduledSession> sessions = scheduledSessionRepository.getSessions(res);
        log.info("sessions Count - {}",sessions.size());
        StudentGradesHelper studentCourseHelper = new StudentGradesHelper();
        List<StudentGradesSessionResultDTO> studentGradesSessionResultDTO = studentCourseHelper.setScheduledSessionsDetails(scheduledSessions, sessions);
        log.info("out.getSections() Count - {}",studentGradesSessionResultDTO.size());
        for(StudentGradesSessionResultDTO out:  studentGradesSessionResultDTO){
            log.info("out.getSections() - {}",out.getSections());
        }
        return studentGradesSessionResultDTO;
    }

    /**
     * This function fetches the grades of all the quizzes taken by the student.
     */
    public List<StudentGradesQuizResultDTO> getQuizAttendance() {
        List<QuizAttendance> quizAttendance = quizAttendanceRepository.getQuizAttendance(defaultUsernameService.getUsername());

        StudentGradesHelper studentCourseHelper = new StudentGradesHelper();

        List<StudentGradesQuizResultDTO> studentGradesResultQADTO = studentCourseHelper.setQuizzesDetails(quizAttendance);

        return studentGradesResultQADTO;
    }

    /**
     * This function fetches the grades of all the walk-in sessions associated to the student.
     */
    public List<StudentGradesWalkInResultDTO> getWalkinAttendance() {
        List<WalkInAttendance> walkInAttendance = walkInAttendanceRepository.getWalkInAttendance(defaultUsernameService.getUsername());

        StudentGradesHelper studentCourseHelper = new StudentGradesHelper();

        List<StudentGradesWalkInResultDTO> studentGradesResultWalkInDTO = studentCourseHelper.setWalkInDetails(walkInAttendance);

        return studentGradesResultWalkInDTO;
    }

}
