package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.model.course.Section;
import edu.utdallas.csmc.web.model.misc.Semester;
import edu.utdallas.csmc.web.model.session.ScheduledSession;
import edu.utdallas.csmc.web.model.session.Session;
import edu.utdallas.csmc.web.model.session.SessionAttendance;
import edu.utdallas.csmc.web.model.session.WalkInAttendance;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.repository.*;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdminReportService {

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    ScheduledSessionRepository scheduledSessionRepository;

    @Autowired
    WalkInAttendanceRepository walkInAttendanceRepository;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    public List<WalkInAttendance> getWalkInAttendanceBySection(Section section){
        List<WalkInAttendance> potentialStudents = walkInAttendanceRepository.getAllByCourse(section.getCourse());
        List<WalkInAttendance> walkInAttendancesReport = new ArrayList<>();
        Optional<Semester> semester = semesterRepository.findSemesterByActive(true);
        for (WalkInAttendance potentialStudent : potentialStudents) {
            for ( User student : section.getStudents()) {
                if (student.getUsername().equals(potentialStudent.getUser().getUsername()) &&  potentialStudent.getTimeIn().compareTo(semester.get().getStartDate()) > 0) {
                    walkInAttendancesReport.add(potentialStudent);
                }
            }
        }
        return walkInAttendancesReport;
    }

    public List<Pair<User, SessionAttendance>> getReportForSession (Section section, ScheduledSession session) {
        List<Pair<User, SessionAttendance>> report = new ArrayList<>();
        if (session != null) {
            for (User student : section.getStudents()) {
                Pair<User, SessionAttendance> studentAttendance = new Pair<>(student, session.getAttendance(student));
                report.add(studentAttendance);
            }
        }
        return report;
    }

    public List<Section> getAllSectionsCurrentSemester (){
        return sectionRepository.findAllSectionsActiveSemester();
    }

    public List<ScheduledSession> getAllSessionsCurrentSemester(){
        return scheduledSessionRepository.findAllSessionJoinSectionActiveSemester();
    }

    public List<Session> getAllSessionsForCurrentSemester(UUID sectionId) {
        return sessionRepository.findAllSessionsForActiveSemesterForSelectedSection(sectionId);
    }

    public List<Session> getAllSessionsForCurrentSemester() {
        return sessionRepository.findAllSessionsForActiveSemester();
    }

    public Section getSectionWithSectionId(UUID sectionId) {
        return sectionRepository.getSectionFromId(sectionId);
    }
}
