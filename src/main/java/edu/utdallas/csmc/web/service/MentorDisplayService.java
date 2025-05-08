package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.*;
import edu.utdallas.csmc.web.helper.MentorDisplayHelper;
import edu.utdallas.csmc.web.model.schedule.Shift;
import edu.utdallas.csmc.web.model.schedule.Timesheet;
import edu.utdallas.csmc.web.model.session.Quiz;
import edu.utdallas.csmc.web.model.session.SessionTimeSlot;
import edu.utdallas.csmc.web.model.session.WalkInAttendance;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.model.user.info.Specialty;
import edu.utdallas.csmc.web.repository.*;
import edu.utdallas.csmc.web.util.DateUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.*;

@Service
@Log4j2
public class MentorDisplayService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private WalkInAttendanceRepository walkInAttendanceRepository;

    @Autowired
    private TimesheetRepository timesheetRepository;

    @Autowired
    private SessionTimeSlotRepository sessionTimeSlotRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    MentorDisplayHelper mentorDisplayHelper = new MentorDisplayHelper();

    //Service to get Mentors
    public List<MentorDisplayMentorsResultDTO> getMentorDetails() {
        List<Timesheet> sessions = timesheetRepository.getSessionDisplayDetails(DateUtil.atStartOfDay(new Date()));
        Map<User,List<Specialty>> map = new HashMap<>();
        User user;
        List<Specialty> specialties;
        for(Timesheet session: sessions) {
            user = session.getUser();
            specialties = specialtyRepository.getSpecialtiesDisplayDetails(user);
            map.put(user,specialties);
        }
        List<MentorDisplayMentorsResultDTO> mentorDisplayMentorsResultDTO = mentorDisplayHelper.setMentorDetails(sessions,map);
        return mentorDisplayMentorsResultDTO;
    }

    //Service to get students
    public List<MentorDisplayStudentsResultDTO> getStudentDetails() {
        log.info(DateUtil.atStartOfDay(new Date()));
        List<WalkInAttendance> students = walkInAttendanceRepository.getStudentDisplayDetails(DateUtil.atStartOfDay(new Date()));
        List<MentorDisplayStudentsResultDTO> mentorDisplayStudentsResultDTO = mentorDisplayHelper.setStudentDetails(students);
        return mentorDisplayStudentsResultDTO;
    }

    //Service to get Sessions
    public List<MentorDisplaySessionsResultDTO> getSessionDetails() {
        Date firstDay = DateUtil.atStartOfDay(new Date());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, 72);
        Date lastDay = DateUtil.atEndOfDay(cal.getTime());
        List<SessionTimeSlot> sessions = sessionTimeSlotRepository.getSessionDisplayDetails(firstDay, lastDay);
        List<MentorDisplaySessionsResultDTO> mentorDisplaySessionsResultDTO = mentorDisplayHelper.setSessionDetails(sessions);
        return mentorDisplaySessionsResultDTO;
    }

    //Service to get Quizzes
    public List<MentorDisplayQuizzesResultDTO> getQuizzDetails() {
        List<Quiz> quizzes = quizRepository.getQuizDisplayDetails(new Date());
        List<MentorDisplayQuizzesResultDTO> mentorDisplayQuizzesResultDTO = mentorDisplayHelper.setQuizzDetails(quizzes);
        return mentorDisplayQuizzesResultDTO;
    }

    //Service to get ShiftLeader
    public MentorDisplayShiftLeaderResultDTO getShiftLeaderDetails() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        Shift shiftLeader = shiftRepository.getShiftLeaderDisplayDetails(new Date(), weekDay);
        MentorDisplayShiftLeaderResultDTO mentorDisplayShiftLeaderResultDTO = mentorDisplayHelper.setShiftLeaderDetails(shiftLeader);
        return mentorDisplayShiftLeaderResultDTO;
    }
}
