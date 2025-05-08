package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.*;
import edu.utdallas.csmc.web.helper.AdminStaffScheduleHelper;
import edu.utdallas.csmc.web.model.misc.OperationHours;
import edu.utdallas.csmc.web.model.misc.Room;
import edu.utdallas.csmc.web.model.misc.Semester;
import edu.utdallas.csmc.web.model.misc.Subject;
import edu.utdallas.csmc.web.model.schedule.*;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.repository.*;
import edu.utdallas.csmc.web.util.DateUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class AdminScheduleCalendarService {

    @Autowired
    OperationHoursRepository operationHoursRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    ScheduledShiftRepository scheduleShiftRepository;

    @Autowired
    ShiftRepository shiftRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    ShiftSubjectRepository shiftSubjectRepository;

    @Autowired
    AbsenceRepository absenceRepository;

    @Autowired
    ShiftAssignmentRepository shiftAssignmentRepository;

    AdminStaffScheduleHelper adminStaffScheduleHelper = new AdminStaffScheduleHelper();

    public AdminScheduleCalendarResultDTO getCalendarScheduleDetails() {
        List<OperationHours> hours = operationHoursRepository.findAll();

        List<User> mentors = userRepository.findUserByRolesName("mentor");
        mentors.sort(Comparator.comparing(User::getFirstName));
        //List<User> sortedmentors = mentors.stream().sorted(Comparator.comparing(User::getFirstName)).collect(Collectors.toList());
        Semester semester = semesterRepository.findByActive(true);
        Schedule schedule = semester.getSchedule();
        List<Subject> subjects = subjectRepository.findByShowOnCalendar(true);
        AdminScheduleCalendarResultDTO adminScheduleCalendarResultDTO = new AdminScheduleCalendarResultDTO();
        adminScheduleCalendarResultDTO.setHours(hours);
        adminScheduleCalendarResultDTO.setMentors(mentors);
        adminScheduleCalendarResultDTO.setSchedule(schedule);
        adminScheduleCalendarResultDTO.setSubjects(subjects);
        return adminScheduleCalendarResultDTO;
    }

    public void updateShiftLeader(RequestObjectSchedule requestObjectSchedule) {
        Optional<Shift> shift = shiftRepository.findById(requestObjectSchedule.getShiftID());
        Optional<User> mentor = userRepository.findById(requestObjectSchedule.getMentorID());

        shift.get().setShiftLeader(mentor.get());
        shiftRepository.saveAndFlush(shift.get());
    }

    public void removeMentorFromShift(RequestObjectShift requestObjectShift) {
        Optional<Shift> shift = shiftRepository.findById(requestObjectShift.getShiftID());
        Optional<User> mentor = userRepository.findById(requestObjectShift.getMentorID());
        Optional<Subject> subject = subjectRepository.findById(requestObjectShift.getSubjectID());

        shift.get().removeMentor(subject.get(), mentor.get());
        shiftRepository.saveAndFlush(shift.get());
    }

    public void updateShift(RequestObjectShift requestObjectShift) {
        Optional<Shift> shift = shiftRepository.findById(requestObjectShift.getShiftID());
        Optional<User> mentor = userRepository.findById(requestObjectShift.getMentorID());
        Optional<Subject> subject = subjectRepository.findById(requestObjectShift.getSubjectID());

        shift.get().addMentor(subject.get(), mentor.get());
        shiftRepository.saveAndFlush(shift.get());
    }

    public void updateSubjectColor(RequestObjectColor requestObject) {
        Optional<Subject> subject = subjectRepository.findById(UUID.fromString(requestObject.getSubjectID()));
        if (subject.isPresent()) {
            subject.get().setColor(requestObject.getSubjectColor());
            subjectRepository.saveAndFlush(subject.get());
        } else {
            log.info("Subject not found!");
        }
    }

    public List<AdminScheduleTimesResultDTO> getScheduleTimes() {

        List<AdminScheduleTimesResultDTO> listScheduleTimes = new ArrayList<>();
        Schedule schedule = semesterRepository.findByActive(true).getSchedule();
        List<Shift> shifts = shiftRepository.findBySchedule(schedule);


        adminStaffScheduleHelper.schduleTimesHelper(listScheduleTimes, shifts);

        return listScheduleTimes;
    }

    public List<Room> getScheduleRooms() {
        return roomRepository.findByActive(true);
    }

    public void updateIncMaxMentors(RequestObjectMaxMentor requestObjectMaxMentors) {

        Optional<ShiftSubject> shift = shiftSubjectRepository.findById(requestObjectMaxMentors.getShiftSubjectID());
        Integer incMentor = new Integer(shift.get().getMaxMentors().intValue() + 1);
        shift.get().setMaxMentors(incMentor);
        shiftSubjectRepository.saveAndFlush(shift.get());
    }

    public void updateDecMaxMentors(RequestObjectMaxMentor requestObjectMaxMentors) {

        Optional<ShiftSubject> shift = shiftSubjectRepository.findById(requestObjectMaxMentors.getShiftSubjectID());
        Integer incMentor = new Integer(shift.get().getMaxMentors().intValue() - 1);
        shift.get().setMaxMentors(incMentor);
        shiftSubjectRepository.saveAndFlush(shift.get());
    }

    public void createShift(AdminCreateShiftResultDTO adminCreateShiftResultDTO) {
        Schedule schedule = semesterRepository.findByActive(true).getSchedule();
        Date start = adminCreateShiftResultDTO.getStartTime();
        Date end = adminCreateShiftResultDTO.getEndTime();
        Optional<Room> room = roomRepository.findById(adminCreateShiftResultDTO.getRoom());
        int day = adminCreateShiftResultDTO.getDay();

        Shift newShift = new Shift();
        newShift.setDay(day);
        newShift.setStartTime(new java.sql.Time(start.getTime()));
        newShift.setEndTime(new java.sql.Time(end.getTime()));
        if (room.isPresent()) {
            newShift.setRoom(room.get());
        }
        newShift.setSubjects(new ArrayList<>());
        newShift.setSchedule(schedule);

        List<Subject> subjects = subjectRepository.findByShowOnCalendar(true);
        for (Subject subject : subjects) {
            newShift.addSubject(subject, 0);
        }
        shiftRepository.saveAndFlush(newShift);
    }

    public void getUpdateDetails() {
        Semester semester = semesterRepository.findByActive(true);
        Schedule schedule = semester.getSchedule();
        Date startDate = new Date();
        Date semEndDate = semester.getEndDate();

        // Loop over all the shifts in the schedule and create a map for Day of Week -> Shift
        // This will store a list of all the Shifts for a particular day of week in a single loop that will be fetched
        Map<Integer,List<Shift>> dayToShiftsMap = new HashMap<>();
        for(Shift currentShift : schedule.getShifts()){
            if(dayToShiftsMap.containsKey(currentShift.getDay())){
                List<Shift> dayShiftList = dayToShiftsMap.get(currentShift.getDay());
                dayShiftList.add(currentShift);
                dayToShiftsMap.replace(currentShift.getDay(), dayShiftList);
            } else {
                List<Shift> newDayShiftList = new ArrayList<>();
                newDayShiftList.add(currentShift);
                dayToShiftsMap.put(currentShift.getDay(),newDayShiftList);
            }
        }

        Calendar c = Calendar.getInstance();
        List<Date> period = DateUtil.listOfDatesBetweenGivenDates(DateUtil.subtractNDays(startDate, 1), semEndDate);//Add the interval
        for (Date date : period) {
            if (date.compareTo(DateUtil.atStartOfDay(new Date())) < 0) { //date is less than new Date()
                continue;
            }
            c.setTime(date);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;

            if(dayToShiftsMap.containsKey(dayOfWeek)){

                List<Shift> shiftsForCurrentDayOfWeek = dayToShiftsMap.get(dayOfWeek);

                for (Shift shift : shiftsForCurrentDayOfWeek) {

                    // If there is no scheduled shift for that day it will create one
                    ScheduledShift scheduleShift = scheduleShiftRepository.findByScheduleAndShiftAndDate(schedule, shift, DateUtil.atStartOfDay(date));
                    if (scheduleShift == null) {
                        ScheduledShift newScheduledShift = new ScheduledShift();
                        newScheduledShift.setSchedule(schedule);
                        newScheduledShift.setShift(shift);
                        newScheduledShift.setDate(DateUtil.atStartOfDay(date));
                        ScheduledShift createdScheduledShift = scheduleShiftRepository.save(newScheduledShift);
                        createAssignment(createdScheduledShift);
                    } else {
                        // If there is a scheduled shift then that may have to be updated according to the shift data

                        // updateAssignments will change any existing assignments and return a list of assignments to
                        // delete if it was present in the scheduled shift but it is not present in the shift anymore
                        List<ShiftAssignment> needDeleted = updateAssignment(scheduleShift);
                        for (ShiftAssignment currentAssignment : needDeleted) {

                            if (currentAssignment.getAbsence() != null) {
                                continue;
                            }

                            if (absenceRepository.findBySubstitute(currentAssignment).isPresent()) {
                                continue;
                            }

                            currentAssignment.setSession(null);

                            shiftAssignmentRepository.deleteSingleQuery(currentAssignment.getId());
                        }
                    }
                }
            }
        }
    }

    public List<ShiftAssignment> updateAssignment(ScheduledShift scheduleShift) {

        HashMap<UUID, MentorDTO> mentorDetailsMap = new HashMap<>();

        // Get shift's mentors into a hash map with the subject
        for (ShiftSubject subject : scheduleShift.getShift().getSubjects()) {
            for (User mentor : subject.getMentors()) {
                MentorDTO currentMentor = new MentorDTO();
                currentMentor.setMentor(mentor);
                currentMentor.setSubject(subject.getSubject());
                currentMentor.setAssigned(false);

                mentorDetailsMap.put(mentor.getId(), currentMentor);
            }
        }
        // Add an entry of the Shift Leader into MentorDetailsMap
        if (scheduleShift.getShift().getShiftLeader() != null) {
            MentorDTO shiftLeader = new MentorDTO();
            shiftLeader.setMentor(scheduleShift.getShift().getShiftLeader());
            shiftLeader.setSubject(null);
            shiftLeader.setAssigned(false);

            mentorDetailsMap.put(scheduleShift.getShift().getShiftLeader().getId(), shiftLeader);
        }

        // List of existing ShiftAssignments that need to be deleted
        List<ShiftAssignment> assignmentsToDelete = new ArrayList<>();

        // Check if assignments still exist in the shift's mentors and update subject
        for (ShiftAssignment assignment : scheduleShift.getAssignments()) {

            if (mentorDetailsMap.containsKey(assignment.getMentor().getId())) {
                MentorDTO currentMentorDetails = mentorDetailsMap.get(assignment.getMentor().getId());
                assignment.setSubject(currentMentorDetails.getSubject());
                shiftAssignmentRepository.saveAndFlush(assignment);

                currentMentorDetails.setAssigned(true);
                mentorDetailsMap.replace(assignment.getMentor().getId(), currentMentorDetails);
            } else {
                assignmentsToDelete.add(assignment);
            }

        }

        // Create new assignments for mentors not currently assigned
        List<MentorDTO> mentorDTOList = new ArrayList<>(mentorDetailsMap.values());
        for (MentorDTO currentMentorDetails : mentorDTOList) {
            if (!currentMentorDetails.isAssigned()) {
                ShiftAssignment newAssignment = new ShiftAssignment();
                newAssignment.setScheduledShift(scheduleShift);
                newAssignment.setSubject(currentMentorDetails.getSubject());
                newAssignment.setMentor(currentMentorDetails.getMentor());
                ShiftAssignment savedAssignment = shiftAssignmentRepository.saveAndFlush(newAssignment);

                scheduleShift.getAssignments().add(savedAssignment);

                currentMentorDetails.setAssigned(true);
            }
        }

        scheduleShiftRepository.saveAndFlush(scheduleShift);

        return assignmentsToDelete;
    }

    public void createAssignment(ScheduledShift createdScheduledShift) {

        HashMap<UUID, MentorDTO> mentorDetailsMap = new HashMap<>();
        List<ShiftAssignment> newShiftAssignments = new ArrayList<>();

        createdScheduledShift.setAssignments(newShiftAssignments);

        // Get shift's mentors into a hash map with the subject
        for (ShiftSubject subject : createdScheduledShift.getShift().getSubjects()) {
            for (User mentor : subject.getMentors()) {
                MentorDTO currentMentor = new MentorDTO();
                currentMentor.setMentor(mentor);
                currentMentor.setSubject(subject.getSubject());
                currentMentor.setAssigned(false);

                mentorDetailsMap.put(mentor.getId(), currentMentor);
            }
        }

        // Add an entry of the Shift Leader into MentorDetailsMap
        if (createdScheduledShift.getShift().getShiftLeader() != null) {
            MentorDTO shiftLeader = new MentorDTO();
            shiftLeader.setMentor(createdScheduledShift.getShift().getShiftLeader());
            shiftLeader.setSubject(null);
            shiftLeader.setAssigned(false);

            mentorDetailsMap.put(createdScheduledShift.getShift().getShiftLeader().getId(), shiftLeader);
        }

        // Create new assignments for mentors not currently assigned
        List<MentorDTO> mentorDTOList = new ArrayList<>(mentorDetailsMap.values());
        for (MentorDTO currentMentorDetails : mentorDTOList) {
            if (!currentMentorDetails.isAssigned()) {
                ShiftAssignment newAssignment = new ShiftAssignment();
                newAssignment.setScheduledShift(createdScheduledShift);
                newAssignment.setSubject(currentMentorDetails.getSubject());
                newAssignment.setMentor(currentMentorDetails.getMentor());
                ShiftAssignment savedAssignment = shiftAssignmentRepository.saveAndFlush(newAssignment);

                createdScheduledShift.getAssignments().add(savedAssignment);

                currentMentorDetails.setAssigned(true);
            }
        }

        scheduleShiftRepository.saveAndFlush(createdScheduledShift);

    }

}
