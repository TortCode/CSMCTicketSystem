package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.dto.StudentScheduleResultDTO;
import edu.utdallas.csmc.web.dto.StudentScheduledSessionDetailsResultDTO;
import edu.utdallas.csmc.web.dto.StudentSessionTimeSlotResultDTO;
import edu.utdallas.csmc.web.dto.StudentSessionTimeSlotsDetailsResultDTO;
import edu.utdallas.csmc.web.model.course.Section;
import edu.utdallas.csmc.web.model.session.Quiz;
import edu.utdallas.csmc.web.model.session.ScheduledSession;
import edu.utdallas.csmc.web.model.session.SessionTimeSlot;
import edu.utdallas.csmc.web.util.DateUtil;

import java.util.List;

/**
 * This class has all the helper functions that will convert Model objects into DTO objects with the required processing
 * for the Student Register View.
 */
public class StudentRegisterHelper {

    /**
     * This helper function receives a Section Model and a List of ScheduledSession Model that will be added to the
     * List of StudentScheduleResultDTO that will be sent back to the view.
     */
    public void buildStudentScheduleResultFromScheduledSessions(String studentNetId, final List<StudentScheduleResultDTO> studentScheduleResultDTOList, Section currentSectionAndCourse, List<ScheduledSession> scheduledSessionList) {
        for (ScheduledSession currentScheduledSession : scheduledSessionList) {
            StudentScheduleResultDTO studentScheduleResult = new StudentScheduleResultDTO();

            StringBuilder sectionNumber = new StringBuilder();
            sectionNumber.append(currentSectionAndCourse.getCourse().getDepartment().getAbbreviation())
                    .append(" ").append(currentSectionAndCourse.getCourse().getNumber())
                    .append(".").append(currentSectionAndCourse.getNumber());
            studentScheduleResult.setSectionNumber(sectionNumber.toString());
            studentScheduleResult.setDescription(currentSectionAndCourse.getDescription());
            studentScheduleResult.setCourseName(currentSectionAndCourse.getCourse().getName());
            studentScheduleResult.setRoom(currentScheduledSession.getDefaultLocation().getBuilding() + " " + currentScheduledSession.getDefaultLocation().getFloor() + "." + currentScheduledSession.getDefaultLocation().getNumber());
            studentScheduleResult.setType(currentScheduledSession.getType().getName());
            studentScheduleResult.setTopic(currentScheduledSession.getTopic());
            studentScheduleResult.setSectionId(currentScheduledSession.getId().toString());

            // Check if already attended
            studentScheduleResult.setAttended(currentScheduledSession.hasAttended(studentNetId));

            // Check if registered
            boolean registered = false;
            for (SessionTimeSlot sts : currentScheduledSession.getTimeslots()) {
                if (sts.isRegistered(studentNetId)) {
                    registered = true;
                    studentScheduleResult.setStartDate(sts.getStartTime());
                    studentScheduleResult.setEndDate(sts.getEndTime());
                }
            }
            studentScheduleResult.setRegistered(registered);

            studentScheduleResultDTOList.add(studentScheduleResult);
        }
    }

    /**
     * This helper function receives a Section Model and a List of Quiz Model that will be added to the
     * List of StudentScheduleResultDTO that will be sent back to the view.
     */
    public void buildStudentScheduleResultFromQuizzes(final List<StudentScheduleResultDTO> studentScheduleResultDTOList, Section currentSectionAndCourse, List<Quiz> quizList) {
        for (Quiz currentQuiz : quizList) {
            StudentScheduleResultDTO studentScheduleResult = new StudentScheduleResultDTO();

            StringBuilder sectionNumber = new StringBuilder();
            sectionNumber.append(currentSectionAndCourse.getCourse().getDepartment().getAbbreviation())
                    .append(" ").append(currentSectionAndCourse.getCourse().getNumber())
                    .append(".").append(currentSectionAndCourse.getNumber());
            studentScheduleResult.setSectionNumber(sectionNumber.toString());
            studentScheduleResult.setDescription(currentSectionAndCourse.getDescription());
            studentScheduleResult.setCourseName(currentSectionAndCourse.getCourse().getName());
            studentScheduleResult.setTopic(currentQuiz.getTopic());
            studentScheduleResult.setType(currentQuiz.getType().getName());
            studentScheduleResult.setStartDate(currentQuiz.getTimeSlot().getStartTime());
            studentScheduleResult.setEndDate(currentQuiz.getTimeSlot().getEndTime());
            studentScheduleResult.setRoom(currentQuiz.getTimeSlot().getLocation().getBuilding() + " " + currentQuiz.getTimeSlot().getLocation().getFloor() + "." + currentQuiz.getTimeSlot().getLocation().getNumber());
            studentScheduleResult.setSectionId(currentQuiz.getId().toString());

            studentScheduleResultDTOList.add(studentScheduleResult);
        }
    }

    /**
     * This helper function converts the SessionTimeSlot Model to the required StudentSessionTimeSlotResultDTO that will
     * be sent back to the view.
     */
    public StudentSessionTimeSlotResultDTO buildStudentSessionTimeSlotDetails(SessionTimeSlot currentSessionTimeSlot) {
        StudentSessionTimeSlotResultDTO timeSlotResultDTO = new StudentSessionTimeSlotResultDTO();

        timeSlotResultDTO.setRemainingSeats(currentSessionTimeSlot.getRemainingSeats());
        timeSlotResultDTO.setStartTime(currentSessionTimeSlot.getStartTime());
        timeSlotResultDTO.setLocation(currentSessionTimeSlot.getLocation().getFloor() + "." + currentSessionTimeSlot.getLocation().getNumber());
        timeSlotResultDTO.setDescription(currentSessionTimeSlot.getSession().getDescription());
        timeSlotResultDTO.setStudentInstructions(currentSessionTimeSlot.getSession().getStudentInstructions());
        timeSlotResultDTO.setCapacity(currentSessionTimeSlot.getCapacity());
        timeSlotResultDTO.setTopic(currentSessionTimeSlot.getSession().getTopic());
        timeSlotResultDTO.setTimeSlotId(currentSessionTimeSlot.getId().toString());

        return timeSlotResultDTO;
    }

    /**
     * This helper function receives a List of SessionTimeSlot Model that will be added to the
     * List of StudentSessionTimeSlotsDetailsResultDTO that will be sent back to the view.
     */
    public void buildScheduledSessionTimeSlotsDetails(final List<StudentSessionTimeSlotsDetailsResultDTO> studentSessionTimeSlotsDetailsResultDTOList, List<SessionTimeSlot> scheduledSessionTimeslots) {
        for (SessionTimeSlot timeSlot : scheduledSessionTimeslots) {

            if(!DateUtil.isStartTimeBeforeCurrentTime(timeSlot.getStartTime())){
                StudentSessionTimeSlotsDetailsResultDTO studentSessionTimeSlotsResult = new StudentSessionTimeSlotsDetailsResultDTO();
                studentSessionTimeSlotsResult.setId(timeSlot.getId());
                studentSessionTimeSlotsResult.setStartTime(timeSlot.getStartTime());
                studentSessionTimeSlotsResult.setLocation(timeSlot.getLocation().getBuilding() + " " + timeSlot.getLocation().getFloor() + "." + timeSlot.getLocation().getNumber());
                studentSessionTimeSlotsResult.setRemainingSeats(timeSlot.getRemainingSeats());
                studentSessionTimeSlotsResult.setCapacity(timeSlot.getCapacity());

                studentSessionTimeSlotsDetailsResultDTOList.add(studentSessionTimeSlotsResult);
            }

        }
    }

    /**
     * This helper function converts the ScheduledSession Model to the required StudentScheduledSessionDetailsResultDTO
     * that will be sent back to the view.
     */
    public StudentScheduledSessionDetailsResultDTO buildScheduledSessionDetails(ScheduledSession scheduledSession) {
        StudentScheduledSessionDetailsResultDTO scheduledSessionDetailsResultDTO = new StudentScheduledSessionDetailsResultDTO();
        scheduledSessionDetailsResultDTO.setTopic(scheduledSession.getTopic());
        scheduledSessionDetailsResultDTO.setDescription(scheduledSession.getDescription());
        scheduledSessionDetailsResultDTO.setStudentInstructions(scheduledSession.getStudentInstructions());

        return scheduledSessionDetailsResultDTO;
    }
}
