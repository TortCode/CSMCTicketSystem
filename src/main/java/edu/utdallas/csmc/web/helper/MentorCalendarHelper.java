package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.dto.*;
import edu.utdallas.csmc.web.model.file.File;
import edu.utdallas.csmc.web.model.schedule.ShiftAssignment;
import edu.utdallas.csmc.web.model.session.Quiz;
import edu.utdallas.csmc.web.model.session.SessionTimeSlot;
import edu.utdallas.csmc.web.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class MentorCalendarHelper {

    //Helper function to set information of Sessions to be displayed in Sessions table of Mentor Calendar view
    public List<MentorCalendarSessionsResultDTO> setSessionInformation(List<SessionTimeSlot> sessions) {
        List<MentorCalendarSessionsResultDTO> mentorCalendarSessionsResultDTOS = new ArrayList<>();
        for(SessionTimeSlot session: sessions) {
            MentorCalendarSessionsResultDTO mentorCalendarSessionsResultDTO = new MentorCalendarSessionsResultDTO();

            mentorCalendarSessionsResultDTO.setTimeSlotId(session.getId());
            mentorCalendarSessionsResultDTO.setTopic(session.getSession().getTopic());
            if(session.getStartTime() != null)
                mentorCalendarSessionsResultDTO.setStartTime(DateUtil.dateFormatTo12Hours.format(session.getStartTime()));  //TODO convert through twig
            mentorCalendarSessionsResultDTO.setBuilding(session.getLocation().getBuilding());
            mentorCalendarSessionsResultDTO.setFloor(session.getLocation().getFloor());
            mentorCalendarSessionsResultDTO.setRoomNumber(session.getLocation().getNumber());
            mentorCalendarSessionsResultDTO.setDescription(session.getSession().getDescription());
            mentorCalendarSessionsResultDTO.setStarted(session.hasStarted());
            mentorCalendarSessionsResultDTO.setEnded(session.hasEnded());

            //List of sections
            List<String> sections = new ArrayList<>();
            for(int i=0; i<session.getSession().getSections().size();i++){
                StringBuilder section = new StringBuilder();
                section.append(session.getSession().getSections().get(i).getCourse().getDepartment().getAbbreviation() + " " + session.getSession().getSections().get(i).getCourse().getNumber() + "." + session.getSession().getSections().get(i).getNumber());
                if(i < session.getSession().getSections().size()-1) {
                    section.append(", ");
                }
                sections.add(section.toString());
            }
            mentorCalendarSessionsResultDTO.setSections(sections);

            //List of instructors
            List<String> instructors = new ArrayList<>();
            for(int i=0; i<session.getSession().getSections().size(); i++){
                for(int j=0; j<session.getSession().getSections().get(i).getInstructors().size(); j++) {
                    StringBuilder instructor = new StringBuilder();
                    instructor.append(session.getSession().getSections().get(i).getInstructors().get(j).getFirstName() + " " + session.getSession().getSections().get(i).getInstructors().get(j).getLastName());
                    if (j < session.getSession().getSections().get(i).getInstructors().size()-1 || i < session.getSession().getSections().size()-1) {
                        instructor.append(", ");
                    }
                    instructors.add(instructor.toString());
                }
            }
            mentorCalendarSessionsResultDTO.setInstructors(instructors);

            //List of mentors
            List<String> mentors = new ArrayList<>();
            for(int i=0; i<session.getAssignments().size(); i++){
                StringBuilder mentor = new StringBuilder();
                mentor.append(session.getAssignments().get(i).getMentor().getFirstName() + " " + session.getAssignments().get(i).getMentor().getLastName());
                if(i < session.getAssignments().size()-1)
                    mentor.append(", ");
                mentors.add(mentor.toString());
            }
            mentorCalendarSessionsResultDTO.setMentors(mentors);

            mentorCalendarSessionsResultDTOS.add(mentorCalendarSessionsResultDTO);
        }
        return  mentorCalendarSessionsResultDTOS;
    }

    //Helper function to set information of Quizzes to be displayed in Quizzes table of Mentor Calendar view
    public List<MentorCalendarQuizzesResultDTO> setQuizInformation(List<Quiz> quizzes){
        List<MentorCalendarQuizzesResultDTO> mentorCalendarQuizzesResultDTOS = new ArrayList<>();
        for(Quiz quiz: quizzes){
            MentorCalendarQuizzesResultDTO mentorCalendarQuizzesResultDTO = new MentorCalendarQuizzesResultDTO();
            mentorCalendarQuizzesResultDTO.setQuizId(quiz.getId());
            mentorCalendarQuizzesResultDTO.setTopic(quiz.getTopic());
            mentorCalendarQuizzesResultDTO.setStartDate(quiz.getTimeSlot().getStartTime());
            mentorCalendarQuizzesResultDTO.setEndDate(quiz.getTimeSlot().getEndTime());
            mentorCalendarQuizzesResultDTO.setBuilding(quiz.getTimeSlot().getLocation().getBuilding());
            mentorCalendarQuizzesResultDTO.setFloor(quiz.getTimeSlot().getLocation().getFloor());
            mentorCalendarQuizzesResultDTO.setRoomNumber(quiz.getTimeSlot().getLocation().getNumber());
            mentorCalendarQuizzesResultDTO.setDescription(quiz.getDescription());

            mentorCalendarQuizzesResultDTOS.add(mentorCalendarQuizzesResultDTO);
        }

        return mentorCalendarQuizzesResultDTOS;
    }

    //Helper function to set details of one particular quiz to be displayed on screen when Details button of Quizzes table is clicked in Mentor Calendar view
    public MentorCalendarQuizDetailsResultDTO setQuizDetails(Quiz quizDetails) {
        MentorCalendarQuizDetailsResultDTO mentorCalendarQuizDetailsResultDTO = new MentorCalendarQuizDetailsResultDTO();

        mentorCalendarQuizDetailsResultDTO.setTopic(quizDetails.getTopic());
        mentorCalendarQuizDetailsResultDTO.setDescription(quizDetails.getDescription());
        mentorCalendarQuizDetailsResultDTO.setStudentInstructions(quizDetails.getStudentInstructions());
        mentorCalendarQuizDetailsResultDTO.setMentorInstructions(quizDetails.getMentorInstructions());
        mentorCalendarQuizDetailsResultDTO.setGraded(quizDetails.isGraded());
        mentorCalendarQuizDetailsResultDTO.setNumericGrade(quizDetails.isNumericGrade());

        if (!quizDetails.getFiles().isEmpty()) {
            List<MentorFileDetailsDTO> fileDetailsDTOList = new ArrayList<>();
            for (File currentFile : quizDetails.getFiles()) {
                MentorFileDetailsDTO fileDetailsDTO = new MentorFileDetailsDTO();
                fileDetailsDTO.setFileId(currentFile.getId());
                fileDetailsDTO.setName(currentFile.getName());

                fileDetailsDTOList.add(fileDetailsDTO);
            }
            mentorCalendarQuizDetailsResultDTO.setFiles(fileDetailsDTOList);
        }

        mentorCalendarQuizDetailsResultDTO.setStartDate(quizDetails.getTimeSlot().getStartTime());
        mentorCalendarQuizDetailsResultDTO.setEndDate(quizDetails.getTimeSlot().getEndTime());

        return mentorCalendarQuizDetailsResultDTO;
    }

    //Helper function to set details of one particular session to be displayed on screen when Details button of Sessions table is clicked in Mentor Calendar view
    public MentorCalendarSessionDetailsResultDTO setSessionDetails(SessionTimeSlot sessionDetails) {
        MentorCalendarSessionDetailsResultDTO mentorCalendarSessionDetailsResultDTO = new MentorCalendarSessionDetailsResultDTO();

        mentorCalendarSessionDetailsResultDTO.setTimeSlotId(sessionDetails.getId());
        mentorCalendarSessionDetailsResultDTO.setTopic(sessionDetails.getSession().getTopic());
        mentorCalendarSessionDetailsResultDTO.setDescription(sessionDetails.getSession().getDescription());
        mentorCalendarSessionDetailsResultDTO.setStudentInstructions(sessionDetails.getSession().getStudentInstructions());
        mentorCalendarSessionDetailsResultDTO.setMentorInstructions(sessionDetails.getSession().getMentorInstructions());
        mentorCalendarSessionDetailsResultDTO.setGraded(sessionDetails.getSession().isGraded());
        mentorCalendarSessionDetailsResultDTO.setNumericGrade(sessionDetails.getSession().isNumericGrade());

        if (!sessionDetails.getSession().getFiles().isEmpty()) {
            List<MentorFileDetailsDTO> fileDetailsDTOList = new ArrayList<>();
            for (File currentFile : sessionDetails.getSession().getFiles()) {
                MentorFileDetailsDTO fileDetailsDTO = new MentorFileDetailsDTO();
                fileDetailsDTO.setFileId(currentFile.getId());
                fileDetailsDTO.setName(currentFile.getName());

                fileDetailsDTOList.add(fileDetailsDTO);
            }
            mentorCalendarSessionDetailsResultDTO.setFiles(fileDetailsDTOList);
        }

        mentorCalendarSessionDetailsResultDTO.setStartTime(sessionDetails.getStartTime());
        mentorCalendarSessionDetailsResultDTO.setEndTime(sessionDetails.getEndTime());
        mentorCalendarSessionDetailsResultDTO.setLocation(sessionDetails.getLocation().getBuilding() + " " + sessionDetails.getLocation().getFloor() + "." + sessionDetails.getLocation().getNumber());
        mentorCalendarSessionDetailsResultDTO.setStarted(sessionDetails.hasStarted());
        mentorCalendarSessionDetailsResultDTO.setEnded(sessionDetails.hasEnded());

        // adding list of mentor names in the DTO
        List<String> mentorsNames = new ArrayList<>();
        for(ShiftAssignment currentShiftAssignment : sessionDetails.getAssignments()){
            mentorsNames.add(currentShiftAssignment.getMentor().getFirstName() + " " + currentShiftAssignment.getMentor().getLastName());
        }
        mentorCalendarSessionDetailsResultDTO.setMentors(mentorsNames);

        mentorCalendarSessionDetailsResultDTO.setRegisteredStudents(sessionDetails.getRegistrations().size());
        mentorCalendarSessionDetailsResultDTO.setCapacity(sessionDetails.getCapacity());
        mentorCalendarSessionDetailsResultDTO.setStarted(sessionDetails.hasStarted());
        mentorCalendarSessionDetailsResultDTO.setEnded(sessionDetails.hasEnded());

        return mentorCalendarSessionDetailsResultDTO;
    }

    public MentorCalendarSwipeSessionResultDTO setSwipeSessionDetails(SessionTimeSlot sessionTimeSlot){
        MentorCalendarSwipeSessionResultDTO mentorCalendarSwipeSessionResultDTO = new MentorCalendarSwipeSessionResultDTO();
        mentorCalendarSwipeSessionResultDTO.setTimeSlotId(sessionTimeSlot.getId());
        mentorCalendarSwipeSessionResultDTO.setTopic(sessionTimeSlot.getSession().getTopic());

        List<String> mentors = new ArrayList<>();
        for(int i=0; i<sessionTimeSlot.getAssignments().size(); i++){
            StringBuilder mentor = new StringBuilder();

            if(sessionTimeSlot.getAssignments().get(i).getMentor().getProfile()!=null &&
                    sessionTimeSlot.getAssignments().get(i).getMentor().getProfile().getPreferredName()!=null){
                mentor.append(sessionTimeSlot.getAssignments().get(i).getMentor().getFirstName());
            } else{
                mentor.append(sessionTimeSlot.getAssignments().get(i).getMentor().getFirstName());
            }

            if(i < sessionTimeSlot.getAssignments().size()-1)
                mentor.append(", ");
            mentors.add(mentor.toString());
        }
        mentorCalendarSwipeSessionResultDTO.setMentorPreferredName(mentors);

        mentorCalendarSwipeSessionResultDTO.setActualStartTime(sessionTimeSlot.getActualStartTime());
        mentorCalendarSwipeSessionResultDTO.setRegisteredStudents(sessionTimeSlot.getRegistrations().size());
        mentorCalendarSwipeSessionResultDTO.setCapacity(sessionTimeSlot.getCapacity());

        List<MentorCalendarSwipeUserDetailsResultDTO> mentorCalendarSwipeUserDetailsResultDTOS = new ArrayList<>();
        for(int i=0;i<sessionTimeSlot.getRegistrations().size();i++) {
            MentorCalendarSwipeUserDetailsResultDTO mentorCalendarSwipeUserDetailsResultDTO = new MentorCalendarSwipeUserDetailsResultDTO();
            mentorCalendarSwipeUserDetailsResultDTO.setStudentId(sessionTimeSlot.getRegistrations().get(i).getUser().getId());
            mentorCalendarSwipeUserDetailsResultDTO.setFirstName(sessionTimeSlot.getRegistrations().get(i).getUser().getFirstName());
            mentorCalendarSwipeUserDetailsResultDTO.setUserName(sessionTimeSlot.getRegistrations().get(i).getUser().getUsername());
            mentorCalendarSwipeUserDetailsResultDTOS.add(mentorCalendarSwipeUserDetailsResultDTO);
        }
        mentorCalendarSwipeSessionResultDTO.setRegistrations(mentorCalendarSwipeUserDetailsResultDTOS);

        List<MentorCalendarSwipeAttendanceDetailsResultDTO> mentorCalendarSwipeAttendanceDetailsResultDTOS = new ArrayList<>();
        for(int i=0;i<sessionTimeSlot.getAttendances().size();i++) {
            MentorCalendarSwipeAttendanceDetailsResultDTO mentorCalendarSwipeAttendanceDetailsResultDTO = new MentorCalendarSwipeAttendanceDetailsResultDTO();
            mentorCalendarSwipeAttendanceDetailsResultDTO.setAttendanceStudentId(sessionTimeSlot.getAttendances().get(i).getUser().getId());
            mentorCalendarSwipeAttendanceDetailsResultDTO.setTimeIn(sessionTimeSlot.getAttendances().get(i).getTimeIn());
            mentorCalendarSwipeAttendanceDetailsResultDTO.setTimeOut(sessionTimeSlot.getAttendances().get(i).getTimeOut());
            mentorCalendarSwipeAttendanceDetailsResultDTO.setFirstName(sessionTimeSlot.getAttendances().get(i).getUser().getFirstName());
            mentorCalendarSwipeAttendanceDetailsResultDTO.setUserName(sessionTimeSlot.getAttendances().get(i).getUser().getUsername());
            mentorCalendarSwipeAttendanceDetailsResultDTOS.add(mentorCalendarSwipeAttendanceDetailsResultDTO);
        }
        mentorCalendarSwipeSessionResultDTO.setAttendances(mentorCalendarSwipeAttendanceDetailsResultDTOS);

        return mentorCalendarSwipeSessionResultDTO;
    }
}
