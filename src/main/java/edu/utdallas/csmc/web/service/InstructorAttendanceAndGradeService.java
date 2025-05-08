package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.UserDTO;
import edu.utdallas.csmc.web.model.session.*;
import edu.utdallas.csmc.web.repository.QuizAttendanceRepository;
import edu.utdallas.csmc.web.repository.QuizRepository;
import edu.utdallas.csmc.web.repository.ScheduledSessionRepository;
import edu.utdallas.csmc.web.repository.SessionTimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class InstructorAttendanceAndGradeService {

    @Autowired
    ScheduledSessionRepository scheduledSessionRepository;

    @Autowired
    QuizRepository quizRepository;

    @Autowired
    QuizAttendanceRepository quizAttendanceRepository;

    @Autowired
    SessionTimeSlotRepository sessionTimeSlotRepository;

    public List<ScheduledSession> getAllGradedSessionByInstructor(UserDTO userDTO) {
        return scheduledSessionRepository.findAllGradedSessionsByInstructorOrderByStartTime(userDTO.getNetId(), new Date());
    }

    public List<Quiz> getAllGradedQuizByInstructor(UserDTO userDTO) {
        return quizRepository.findAllGradedQuizByInstructorOrderByStartTime(userDTO.getNetId(), new Date());
    }

    public List<QuizAttendance> getQuizAttendanceByQuiz(Quiz quiz) {
        return quizAttendanceRepository.findAllAttendanceById(quiz);
    }

    public Quiz getQuizInfoById(UUID quizId) {
        return quizRepository.findAllById(quizId);
    }

    public ScheduledSession getSessionInfoById (UUID sessionId) {
        return scheduledSessionRepository.findAllById(sessionId);
    }

    public List<SessionAttendance> getAllAttendanceBySession(ScheduledSession session) {
        return getAllAttendanceFromTimeslots(sessionTimeSlotRepository.findAllBySession(session));
    }

    private List<SessionAttendance> getAllAttendanceFromTimeslots (List<SessionTimeSlot> timeSlotList) {
        List<SessionAttendance> attendances = new ArrayList<>();
        for (SessionTimeSlot sessionTimeSlot : timeSlotList) {
            attendances.addAll(sessionTimeSlot.getAttendances());
        }
        return attendances;
    }
}