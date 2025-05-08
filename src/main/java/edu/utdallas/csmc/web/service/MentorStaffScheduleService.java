package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.*;
import edu.utdallas.csmc.web.helper.MentorStaffScheduleHelper;
import edu.utdallas.csmc.web.model.misc.Semester;
import edu.utdallas.csmc.web.model.misc.Subject;
import edu.utdallas.csmc.web.model.schedule.Schedule;
import edu.utdallas.csmc.web.model.schedule.ScheduledShift;
import edu.utdallas.csmc.web.model.schedule.ShiftAssignment;
import edu.utdallas.csmc.web.model.session.Quiz;
import edu.utdallas.csmc.web.model.session.SessionTimeSlot;
import edu.utdallas.csmc.web.repository.QuizRepository;
import edu.utdallas.csmc.web.repository.SemesterRepository;
import edu.utdallas.csmc.web.repository.SessionTimeSlotRepository;
import edu.utdallas.csmc.web.repository.SubjectRepository;
import edu.utdallas.csmc.web.util.DateUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class defines all the service functions related to the Mentor Staff Schedule View.
 */
@Service
@Log4j2
public class MentorStaffScheduleService {

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private SessionTimeSlotRepository sessionTimeSlotRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    MentorStaffScheduleHelper mentorStaffScheduleHelper = new MentorStaffScheduleHelper();

    /**
     * This function fetches all the details related to mentors on duty, sessions and quizzes with time slots
     * for 7 days starting from the date provided.
     */
    public ModelMap scheduleWeeklyAction(ModelMap model, Date date) {
        String message="";
        Optional<Semester> currentSemester = semesterRepository.findSemesterByActive(true);
        if (!currentSemester.isPresent()) {
            return model;
        }

        List<MentorStaffScheduleResultDTO> mentorStaffScheduleResultDTOList = new ArrayList<>();

        // Get the schedule for the current semester
        Schedule schedule = currentSemester.get().getSchedule();

        //remove print
        System.out.print("schedule "+ schedule);

        Date startDate;
        Date endDate;
        if (schedule == null) {
            message = "No Schedule Found!";
        } else {
            // Learn more about whether to use LocalDate or (Date and Calendar).
            startDate = date;
            endDate = DateUtil.addNDays(date, 7);

            // Create a list of dates between start date and end date called date period
            List<Date> datePeriod = DateUtil.listOfDatesBetweenGivenDates(DateUtil.subtractNDays(startDate, 1), endDate);

            List<ScheduledShift> scheduledShifts = schedule.getScheduleShifts();

            System.out.print("scheduledShifts "+ scheduledShifts);

            for (Date currentDate : datePeriod) {

                MentorStaffScheduleResultDTO mentorStaffScheduleResultDTO = new MentorStaffScheduleResultDTO();
                List<MentorStaffScheduleShiftResultDTO> mentorStaffScheduleShiftResultDTOList = new ArrayList<>();
                List<MentorStaffScheduleSessionResultDTO> mentorStaffScheduleSessionResultDTOList = new ArrayList<>();
                List<MentorStaffScheduleQuizzesResultDTO> mentorStaffScheduleQuizzesResultDTOList = new ArrayList<>();
                // Map ScheduledSession Start Time -> Mentor with Scheduled Session Id
                HashMap<String, List<UUID>> mentorWithScheduledSessions = new HashMap<>();

                mentorStaffScheduleResultDTO.setCurrentDate(currentDate);

                Date startOfDay = DateUtil.atStartOfDay(currentDate);
                Date endOfDay = DateUtil.atEndOfDay(currentDate);

                // Prepare data for the Sessions Table
                List<SessionTimeSlot> sessionTimeSlotList = sessionTimeSlotRepository.findSessionTimeSlotsForEntireDayByDate(startOfDay, endOfDay);
                mentorStaffScheduleHelper.setSessionDetails(mentorStaffScheduleSessionResultDTOList, sessionTimeSlotList, mentorWithScheduledSessions);

                System.out.print("sessionTimeSlotList "+ sessionTimeSlotList);

                //Prepare data for the Quizzes Table
                List<Quiz> quizList = quizRepository.findQuizzesForEntireDayByDate(startOfDay, endOfDay);
                mentorStaffScheduleHelper.setQuizzDetails(mentorStaffScheduleQuizzesResultDTOList, quizList);

                // Prepare data for the Mentors on Duty Table

                // Filters the ScheduledShift list and only keeps the ones matching with the current date in the list
                List<ScheduledShift> scheduledShiftsForDay = scheduledShifts
                        .stream()
                        .filter(currentShift -> currentShift.getDate().compareTo(DateUtil.atStartOfDay(currentDate)) == 0)
                        .collect(Collectors.toList());

                System.out.print("startOfDay "+ startOfDay + " endOfDay " + endOfDay);

                System.out.print("scheduledShiftsForDay "+ scheduledShiftsForDay);

                for (ScheduledShift currentShift : scheduledShiftsForDay) {

                    System.out.print("currentShift "+ currentShift);

                    MentorStaffScheduleShiftResultDTO mentorStaffScheduleShiftResultDTO = new MentorStaffScheduleShiftResultDTO();

                    mentorStaffScheduleShiftResultDTO.setStartTime(currentShift.getShift().getStartTime());
                    mentorStaffScheduleShiftResultDTO.setEndTime(currentShift.getShift().getEndTime());

                    // Map Subject -> Mentors Names
                    Map<String, MentorStaffScheduleSubjectDetailsDTO> subjectMap = new HashMap<>();

                    String startTimeForMentors = DateUtil.timeFormatTo12Hours.format(mentorStaffScheduleShiftResultDTO.getStartTime());
                    List<UUID> mentorsWithSessions = new ArrayList<>();
                    if (mentorWithScheduledSessions.containsKey(startTimeForMentors)) {
                        mentorsWithSessions = mentorWithScheduledSessions.get(startTimeForMentors);
                    }

                    List<ShiftAssignment> shiftAssignments = currentShift.getAssignments();
                    for (ShiftAssignment currentShiftAssignment : shiftAssignments) {

                        System.out.print("currentShiftAssignment "+ currentShiftAssignment);

                        MentorStaffScheduleSubjectDetailsDTO subjectDetailsDTO = new MentorStaffScheduleSubjectDetailsDTO();
                        if (currentShiftAssignment.getSubject() != null) {

                            System.out.print("currentShiftAssignment.getSubject() "+ currentShiftAssignment.getSubject().getName());

                            if (currentShiftAssignment.getScheduledShift().getShift() != null && currentShiftAssignment.getScheduledShift().getShift().getShiftLeader() != null) {

                                System.out.print("currentShiftAssignment shiftleader " + currentShiftAssignment.getScheduledShift().getId() + "  " + currentShiftAssignment.getScheduledShift().getShift().getShiftLeader().getName());
                            }

                            // If the subject map already has the subject then just add the mentor name to it
                            if (subjectMap.containsKey(currentShiftAssignment.getSubject().getName())) {
                                subjectDetailsDTO = subjectMap.get(currentShiftAssignment.getSubject().getName());

                                StringBuilder mentorName = new StringBuilder();
                                mentorName.append(currentShiftAssignment.getMentor().getFirstName()).append(" ")
                                        .append(currentShiftAssignment.getMentor().getLastName());

                                // Print (A) if absence is not null so will return true
                                if (currentShiftAssignment.getAbsence() != null) {
                                    mentorName.append(" (A)");
                                } else if (mentorWithScheduledSessions.containsKey(startTimeForMentors)) {
                                    // Print (S) if mentor has a session scheduled based on its start time
                                    if (mentorsWithSessions.contains(currentShiftAssignment.getMentor().getId())) {
                                        mentorName.append(" (S)");
                                    }
                                }

                                subjectDetailsDTO.setMentors(subjectDetailsDTO.getMentors() + ", " + mentorName.toString());

                                subjectMap.replace(currentShiftAssignment.getSubject().getName(), subjectDetailsDTO);
                            } else {
                                // If the subject map does not have the subject then add the subject details to it
                                subjectDetailsDTO.setName(currentShiftAssignment.getSubject().getName());
                                subjectDetailsDTO.setColor(currentShiftAssignment.getSubject().getColor());

                                StringBuilder mentorName = new StringBuilder();
                                mentorName.append(currentShiftAssignment.getMentor().getFirstName()).append(" ")
                                        .append(currentShiftAssignment.getMentor().getLastName());
                                // Print (A) if absence is not null so will return true
                                if (currentShiftAssignment.getAbsence() != null) {
                                    mentorName.append(" (A)");
                                } else if (mentorWithScheduledSessions.containsKey(startTimeForMentors)) {
                                    // Print (S) if mentor has a session scheduled based on its start time
                                    if (mentorsWithSessions.contains(currentShiftAssignment.getMentor().getId())) {
                                        mentorName.append(" (S)");
                                    }
                                }
                                subjectDetailsDTO.setMentors(mentorName.toString());

                                subjectMap.put(currentShiftAssignment.getSubject().getName(), subjectDetailsDTO);
                            }

                        } else {
                            System.out.print("Adding shift leader separately");
                            // Add Shift leader to the subject List
                            StringBuilder mentorName = new StringBuilder();
                            mentorName.append(currentShiftAssignment.getMentor().getFirstName()).append(" ")
                                    .append(currentShiftAssignment.getMentor().getLastName());
                            // Print (S) if mentor has a session scheduled based on its start time
                            if (mentorsWithSessions.contains(currentShiftAssignment.getMentor().getId())) {
                                mentorName.append(" (S)");
                            }
                            mentorStaffScheduleShiftResultDTO.setShiftLeader(mentorName.toString());
                        }
                    }

                    System.out.print("mentorStaffScheduleShiftResultDTO" + mentorStaffScheduleShiftResultDTO);

                    List<MentorStaffScheduleSubjectDetailsDTO> subjectDetailsDTOList = new ArrayList<>(subjectMap.values());
                    mentorStaffScheduleShiftResultDTO.setSubjects(subjectDetailsDTOList);
                    mentorStaffScheduleShiftResultDTOList.add(mentorStaffScheduleShiftResultDTO);

                }
                //sort according to start time
                mentorStaffScheduleShiftResultDTOList.sort(Comparator.comparing(x -> x.getStartTime()));
                mentorStaffScheduleResultDTO.setScheduledShifts(mentorStaffScheduleShiftResultDTOList);
                mentorStaffScheduleResultDTO.setSessions(mentorStaffScheduleSessionResultDTOList);
                mentorStaffScheduleResultDTO.setQuizzes(mentorStaffScheduleQuizzesResultDTOList);

                mentorStaffScheduleResultDTOList.add(mentorStaffScheduleResultDTO);

            }

            List<Subject> subjectList = subjectRepository.findByShowOnCalendar(true);
            List<MentorStaffScheduleSubjectDetailsDTO> subjectLegendDetailsList = new ArrayList<>();
            mentorStaffScheduleHelper.buildSubjectLegendDetails(subjectLegendDetailsList, subjectList);

            model.addAttribute("start_date", startDate);
            model.addAttribute("end_date", endDate);
            model.addAttribute("shifts", mentorStaffScheduleResultDTOList);
            model.addAttribute("previous_date", DateUtil.subtractNDays(startDate, 7));
            model.addAttribute("next_date", DateUtil.addNDays(endDate, 1));
            model.addAttribute("subjects", subjectLegendDetailsList);
            if(message!=null)
                model.addAttribute("message",message);
        }

        return model;
    }

}
