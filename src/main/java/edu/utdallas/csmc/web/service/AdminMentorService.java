package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.AdminMentorResultDTO;
import edu.utdallas.csmc.web.dto.AdminSessionCalendarEventsSessionDTO;
import edu.utdallas.csmc.web.model.misc.Subject;
import edu.utdallas.csmc.web.model.schedule.ScheduledShift;
import edu.utdallas.csmc.web.model.schedule.ShiftAssignment;
import edu.utdallas.csmc.web.model.schedule.ShiftSubject;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.model.user.info.Specialty;
import edu.utdallas.csmc.web.repository.*;
import edu.utdallas.csmc.web.util.DateUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

    @Service
    @Log4j2
    public class AdminMentorService {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private SubjectRepository subjectRepository;

        @Autowired
        private ShiftAssignmentRepository shiftAssignmentRepository;

        @Autowired
        private SpecialtyRepository specialtyRepository;

        @Autowired
        private TimesheetRepository timesheetRepository;

        @Autowired
        private SemesterRepository semesterRepository;

    public AdminMentorResultDTO getMentorDetails() throws ParseException {
        AdminMentorResultDTO adminMentorResultDTO = new AdminMentorResultDTO();
        List<User> mentorList = userRepository.findUserByRolesName("mentor");
        adminMentorResultDTO.setMentors(mentorList);
        TreeMap<UUID, TreeMap<String, Integer>> mentorSpecialization = new TreeMap<UUID, TreeMap<String, Integer>>();
        TreeMap<UUID, Integer> mentorSession = new TreeMap<UUID, Integer>();
        String date = "2023-08-21";
        String time = "00:00";
        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeParser = new SimpleDateFormat("HH:mm");

        Date d = new SimpleDateFormat("yyyy-MM-dd").parse(date);


        Date eventDate = dateParser.parse(date);
        Time startTime = new Time(timeParser.parse(time).getTime());

        List<ShiftAssignment> shiftAssignments = shiftAssignmentRepository
                .getShiftAssignmentByDateAndIntervalModified(eventDate, startTime);
        for (ShiftAssignment shiftAssignment : shiftAssignments){
                User mentor = shiftAssignment.getMentor();
                UUID id = mentor.getId();
                if(mentorSession.containsKey(id)) {
                    mentorSession.put(id, mentorSession.get(id) + 1);
                }
                else {
                    mentorSession.put(id, 1);
                }
        }

        for (User mentor: mentorList){
            UUID id = mentor.getId();
            String netId = mentor.getUsername();
            mentorSession.put(id, shiftAssignmentRepository.getSessionsCountByMentorUserName(netId));

            List<Specialty> specialtyList = specialtyRepository.findMentorSpeciality(id);
            for (Specialty specialty : specialtyList){

                try {
                    if (mentorSpecialization.containsKey(id)) {
                        mentorSpecialization.get(id).put(specialty.getTopic().getName(), specialty.getRating());
                    } else {
                        TreeMap<String, Integer> rating = new TreeMap<String, Integer>();
                        rating.put(specialty.getTopic().getName(), specialty.getRating());
                        mentorSpecialization.put(id, rating);

                    }
                }
                catch (Exception e) {

                }

            }
        }

        adminMentorResultDTO.setMentorSpecialization(mentorSpecialization);
        adminMentorResultDTO.setMentorSession(mentorSession);
        List<Subject> subjectList = subjectRepository.getSubjectsInSortedOrder();
        List<Subject> subjectFinalList = new ArrayList<>();
        subjectFinalList.add(subjectList.get(1));
        subjectFinalList.add(subjectList.get(5));
        subjectFinalList.add(subjectList.get(4));
        subjectFinalList.add(subjectList.get(2));
        subjectFinalList.add(subjectList.get(3));
        adminMentorResultDTO.setSubjects(subjectFinalList);

        return adminMentorResultDTO;
    }

        public Map<String, Float> getMentorHoursForSelectedSemester(Date formattedStartDate, Date formattedEndDate) {
            Map<String, Float> mentorHours = new LinkedHashMap<>();
            List<String> mentors = timesheetRepository.getMentorsList(formattedStartDate, formattedEndDate);
            for(String mentor : mentors) {
                User u = userRepository.getUser(mentor);
                Float hours = timesheetRepository.getTimesheetHoursTotalByMentorUserName(mentor, formattedStartDate, formattedEndDate);
                if(hours != 0.0) {
                    mentorHours.put(u.getFirstName(), hours);
                }
            }
            return mentorHours;
        }

    public List<User> getActiveMentors() {
        Set<User> mentors = new HashSet<>();
        List<ScheduledShift> scheduledShifts = semesterRepository.findSemesterByActive(true).get().getSchedule().getScheduleShifts();
        for (ScheduledShift shift : scheduledShifts) {
            if (shift.getShift().getShiftLeader() != null) {
                mentors.add(shift.getShift().getShiftLeader());
            }
            for (ShiftSubject sub : shift.getShift().getSubjects()) {
                mentors.addAll(sub.getMentors());
            }
        }

        return mentors.stream()
            .sorted(Comparator.comparing(User::getLastName).thenComparing(User::getFirstName))
            .collect(Collectors.toList());

    }
}
