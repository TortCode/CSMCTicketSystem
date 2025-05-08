package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.InstructorSessionScheduleDTO;

import edu.utdallas.csmc.web.model.course.Section;
import edu.utdallas.csmc.web.model.session.ScheduledSession;

import edu.utdallas.csmc.web.repository.ScheduledSessionRepository;
import edu.utdallas.csmc.web.repository.SectionRepository;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
@Log4j2
public class InstructorSessionScheduleService {

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    ScheduledSessionRepository scheduledSessionRepository;


    public List<InstructorSessionScheduleDTO> getSessionSchedule(String userId){

        List<InstructorSessionScheduleDTO> courses = new ArrayList<>();
        List<Section> sections = sectionRepository.findSectionsByInstructorUsername(userId);

        for (Section s: sections) {
            InstructorSessionScheduleDTO newSession = new InstructorSessionScheduleDTO();
            List<ScheduledSession> quizzes = scheduledSessionRepository.findQuizSessionsBySectionId(s.getId());
            List<ScheduledSession> sessions = scheduledSessionRepository.findSessionExceptQuizzesBySectionId(s.getId());
            log.info("Quizzes List: " + quizzes);
            log.info("Quizzes List: " + sessions);
            log.info("\n");
            newSession.setSection(s);
            newSession.setQuizzes(quizzes);
            newSession.setSessions(sessions);
            courses.add(newSession);
        }

        return courses;
    }

    public Optional<ScheduledSession> getSession(UUID id){
        return scheduledSessionRepository.findById(id);
    }
}
