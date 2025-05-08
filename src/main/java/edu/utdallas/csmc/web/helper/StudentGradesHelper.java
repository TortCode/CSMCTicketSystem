package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.dto.StudentGradesQuizResultDTO;
import edu.utdallas.csmc.web.dto.StudentGradesSessionResultDTO;
import edu.utdallas.csmc.web.dto.StudentGradesWalkInResultDTO;
import edu.utdallas.csmc.web.model.session.QuizAttendance;
import edu.utdallas.csmc.web.model.session.ScheduledSession;
import edu.utdallas.csmc.web.model.session.ScheduledSessionAttendance;
import edu.utdallas.csmc.web.model.session.WalkInAttendance;
import edu.utdallas.csmc.web.util.DateUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class has all the helper functions that will convert Model objects into DTO objects with the required processing
 * for the Student Grades View.
 */
public class StudentGradesHelper {

    /**
     * This helper function will take the Lists of ScheduledSession and ScheduledSessionAttendance Models to return a list of
     * StudentGradesSessionResultDTO that will be sent back to the view.
     */
    public List<StudentGradesSessionResultDTO> setScheduledSessionsDetails(List<ScheduledSessionAttendance> scheduledSessions, List<ScheduledSession> sessions) {

        List<StudentGradesSessionResultDTO> studentGradesSessionResultDTO = new ArrayList<>();

        for(int i=0; i<scheduledSessions.size();i++){
            StudentGradesSessionResultDTO studentGradesResult = new StudentGradesSessionResultDTO();

            studentGradesResult.setTopic(sessions.get(i).getTopic());
            if(scheduledSessions.get(i).getTimeIn() != null)
                studentGradesResult.setCheckInTime(DateUtil.dateFormatTo12Hours.format(scheduledSessions.get(i).getTimeIn()));
            if(scheduledSessions.get(i).getTimeOut() != null)
                studentGradesResult.setCheckOutTime(DateUtil.dateFormatTo12Hours.format(scheduledSessions.get(i).getTimeOut()));
            studentGradesResult.setGrade(scheduledSessions.get(i).getGrade());
            studentGradesResult.setGraded(sessions.get(i).isGraded());
            studentGradesResult.setNumericGrade(sessions.get(i).isNumericGrade());

            List<String> sections = new ArrayList<>();
            for(int j=0; j<sessions.get(i).getSections().size();j++) {
                StringBuilder section = new StringBuilder();
                section.append(sessions.get(i).getSections().get(j).getCourse().getDepartment().getAbbreviation() + " " + sessions.get(i).getSections().get(j).getCourse().getNumber()+"."+sessions.get(i).getSections().get(j).getNumber());
                if(j<sessions.get(i).getSections().size()-1)
                    section.append(", ");
                sections.add(section.toString());
            }
            studentGradesResult.setSections(sections);

            studentGradesSessionResultDTO.add(studentGradesResult);
        }
        return studentGradesSessionResultDTO;
    }

    /**
     * This helper function will take the List of QuizAttendance Model and return a list of StudentGradesQuizResultDTO that will
     * be sent back to the view.
     */
    public List<StudentGradesQuizResultDTO> setQuizzesDetails(List<QuizAttendance> quizAttendance) {
        List<StudentGradesQuizResultDTO> studentGradesResultQADTO = new ArrayList<>();
        for(int i=0; i<quizAttendance.size();i++){
            StudentGradesQuizResultDTO studentGradesResult = new StudentGradesQuizResultDTO();

            if(quizAttendance.get(i).getCourse() != null && quizAttendance.get(i).getSection() != null)
                studentGradesResult.setSectionNumber(quizAttendance.get(i).getCourse().getDepartment().getAbbreviation() + " " + quizAttendance.get(i).getCourse().getNumber() + "." + quizAttendance.get(i).getSection().getNumber());
            studentGradesResult.setTopic(quizAttendance.get(i).getQuiz().getTopic());
            if(quizAttendance.get(i).getTimeIn() != null)
                studentGradesResult.setCheckInTime(DateUtil.dateFormatTo12Hours.format(quizAttendance.get(i).getTimeIn()));
            if(quizAttendance.get(i).getTimeOut() != null)
                studentGradesResult.setCheckOutTime(DateUtil.dateFormatTo12Hours.format(quizAttendance.get(i).getTimeOut()));
            studentGradesResult.setGrade(quizAttendance.get(i).getGrade());
            studentGradesResult.setGraded(quizAttendance.get(i).getQuiz().isGraded());
            studentGradesResult.setNumericGrade(quizAttendance.get(i).getQuiz().isNumericGrade());

            studentGradesResultQADTO.add(studentGradesResult);
        }
        return  studentGradesResultQADTO;
    }

    /**
     * This helper function will take a List of WalkInAttendance Model and will return a List of StudentGradesWalkInResultDTO
     * that will be sent back to the view.
     */
    public List<StudentGradesWalkInResultDTO> setWalkInDetails(List<WalkInAttendance> walkInAttendance) {
        List<StudentGradesWalkInResultDTO> studentGradesResultWalkInDTO = new ArrayList<>();

        for(int i=0; i<walkInAttendance.size();i++){
            StudentGradesWalkInResultDTO studentGradesResult = new StudentGradesWalkInResultDTO();

            studentGradesResult.setCourseNumber(walkInAttendance.get(i).getCourse().getDepartment().getAbbreviation() + " " + walkInAttendance.get(i).getCourse().getNumber());
            studentGradesResult.setTopic(walkInAttendance.get(i).getTopic());
            studentGradesResult.setActivityName(walkInAttendance.get(i).getActivity().getName());

            StringBuilder name = new StringBuilder();
            for(int j=0; j<walkInAttendance.get(i).getMentors().size() ; j++){
                name.append(walkInAttendance.get(i).getMentors().get(j).getFirstName()+" "+ walkInAttendance.get(i).getMentors().get(j).getLastName());
                if(j<walkInAttendance.get(i).getMentors().size()-1){
                    name.append(", ");
                }
            }
            studentGradesResult.setMentors(name.toString());
            if(walkInAttendance.get(i).getTimeIn() != null)
                studentGradesResult.setCheckInTime(DateUtil.dateFormatTo12Hours.format(walkInAttendance.get(i).getTimeIn()));
            if(walkInAttendance.get(i).getTimeOut() != null)
                studentGradesResult.setCheckOutTime(DateUtil.dateFormatTo12Hours.format(walkInAttendance.get(i).getTimeOut()));

            studentGradesResultWalkInDTO.add(studentGradesResult);
        }
        return  studentGradesResultWalkInDTO;
    }
}
