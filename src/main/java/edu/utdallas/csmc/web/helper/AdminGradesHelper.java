package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.dto.*;
import edu.utdallas.csmc.web.model.session.*;
import edu.utdallas.csmc.web.model.user.User;

import java.util.*;
import java.util.stream.Collectors;

public class AdminGradesHelper {
    /**
     * This function sets the scheduled session attendance grades.
     * Returns attendance grades for the view
     */
    public AdminAttendanceGradesResultDTO setScheduledSessionAttendanceGrades(ScheduledSession attendance) {

            List<TimeSlot> timeslot = new ArrayList<>();
            AdminAttendanceGradesResultDTO mentorAttendanceGradesResultDTO = new AdminAttendanceGradesResultDTO();

            UUID session = attendance.getId();
            mentorAttendanceGradesResultDTO.setSessionId(session);

            for(int i=0; i<attendance.getTimeslots().size();i++){
                timeslot.add(attendance.getTimeslots().get(i));
            }
            mentorAttendanceGradesResultDTO.setTimeSlot(timeslot);

            List<User> students = new ArrayList<>();
            for(int i = 0; i < attendance.getSections().size(); i++) {
                for (int j = 0; j < attendance.getSections().get(i).getStudents().size(); j++) {
                    students.add(attendance.getSections().get(i).getStudents().get(j));
                }
            }
            List<User> sortedUsers = students.stream()
                .sorted(Comparator.comparing(User::getFirstName))
                .collect(Collectors.toList());

            mentorAttendanceGradesResultDTO.setUsers(sortedUsers);

        return mentorAttendanceGradesResultDTO;
    }


    /**
     * This function sets attendance for quiz.
     * Returns a lists of quiz grades for view
     */
    public List<AdminGradesQuizEditDTO> setQuizAttendance(List<QuizAttendance> quizAttendances, Optional<Quiz> session)
    {
        List<AdminGradesQuizEditDTO> mentorGradesQuizEditDTOList = new ArrayList<>();

        for(int i=0;i<quizAttendances.size();i++)
        {
            AdminGradesQuizEditDTO mentorGradesQuizEditDTO = new AdminGradesQuizEditDTO();

            StringBuilder sb = new StringBuilder();
            sb.append(quizAttendances.get(i).getUser().getFirstName());
            sb.append(" ");
            sb.append(quizAttendances.get(i).getUser().getLastName());

            mentorGradesQuizEditDTO.setName(sb.toString());
            mentorGradesQuizEditDTO.setUsername(quizAttendances.get(i).getUser().getUsername());
            mentorGradesQuizEditDTO.setId(quizAttendances.get(i).getId());

            if(session.get().isGraded()) {
                if(session.get().isNumericGrade() && quizAttendances.get(i).getGrade() != null) {
                    mentorGradesQuizEditDTO.setGrade(quizAttendances.get(i).getGrade());
                }
                else if (quizAttendances.get(i).getGrade() != null){
                    if(quizAttendances.get(i).getGrade() == 0) {
                        mentorGradesQuizEditDTO.setGrade(-2); //-2 means fail (considering fail means 0 in db)
                    }
                    else {
                        mentorGradesQuizEditDTO.setGrade(-1); //-1 means pass (considering pass means 1 in db)
                    }
                }
            }

            mentorGradesQuizEditDTOList.add(mentorGradesQuizEditDTO);
        }

        return mentorGradesQuizEditDTOList;
    }

    /**
     * This function sets information for scheduled session
     * Returns a lists of scheduled session grades for view
     */
    public AdminGradesSessionResultDTO setSessionDetailsSS(Optional<ScheduledSession> session) {

        AdminGradesSessionResultDTO mentorGradesSessionResultDTO = new AdminGradesSessionResultDTO();

        mentorGradesSessionResultDTO.setSessionId(session.get().getId());
        mentorGradesSessionResultDTO.setTopic(session.get().getTopic());
        mentorGradesSessionResultDTO.setGraded(session.get().isGraded());
        mentorGradesSessionResultDTO.setNumericGrade(session.get().isNumericGrade());

        return mentorGradesSessionResultDTO;
    }

    /**
     * This function sets information for quiz
     * Returns a quiz grade view
     */
    public AdminGradesSessionResultDTO setSessionDetailsQuiz(Optional<Quiz> session) {

        AdminGradesSessionResultDTO mentorGradesSessionResultDTO = new AdminGradesSessionResultDTO();

        mentorGradesSessionResultDTO.setSessionId(session.get().getId());
        mentorGradesSessionResultDTO.setTopic(session.get().getTopic());
        mentorGradesSessionResultDTO.setGraded(session.get().isGraded());
        mentorGradesSessionResultDTO.setNumericGrade(session.get().isNumericGrade());

        return mentorGradesSessionResultDTO;
    }

    /**
     * This function sets information for session time slot and return the list of grading updated scheduled session
     * Returns a list of scheduled session grades
     */
    public List<AdminGradesScheduledSessionEditDTO> setSessionTimeSlotDetails(List<SessionTimeSlot> sessionTimeSlotList, Optional<ScheduledSession> session) {
        List<AdminGradesScheduledSessionEditDTO> mentorGradesScheduledSessionEditDTOList = new ArrayList<>();

        for(int i=0;i<sessionTimeSlotList.size();i++) {
            AdminGradesScheduledSessionEditDTO mentorGradesScheduledSessionEditDTO = new AdminGradesScheduledSessionEditDTO();

            mentorGradesScheduledSessionEditDTO.setStartTime(sessionTimeSlotList.get(i).getStartTime());
            mentorGradesScheduledSessionEditDTO.setEndTime(sessionTimeSlotList.get(i).getEndTime());

            List<AdminGradesQuizEditDTO> mentorGradesQuizEditDTOList = new ArrayList<>();
            for(int j=0;j<sessionTimeSlotList.get(i).getAttendances().size();j++){
                AdminGradesQuizEditDTO mentorGradesQuizEditDTO = new AdminGradesQuizEditDTO();

                mentorGradesQuizEditDTO.setUsername(sessionTimeSlotList.get(i).getAttendances().get(j).getUser().getUsername());
                mentorGradesQuizEditDTO.setId(sessionTimeSlotList.get(i).getAttendances().get(j).getId());

                if(session.get().isGraded()) {
                    if (session.get().isNumericGrade() && sessionTimeSlotList.get(i).getAttendances().get(j).getGrade() != null) {
                        mentorGradesQuizEditDTO.setGrade(sessionTimeSlotList.get(i).getAttendances().get(j).getGrade());
                    } else if(sessionTimeSlotList.get(i).getAttendances().get(j).getGrade() != null){
                        if (sessionTimeSlotList.get(i).getAttendances().get(j).getGrade() == 0) {
                            mentorGradesQuizEditDTO.setGrade(-2); //-2 means fail (considering fail means 0 in db)
                        } else {
                            mentorGradesQuizEditDTO.setGrade(-1); //-1 means pass (considering pass means 1 in db)
                        }
                    }
                }

                StringBuilder sb = new StringBuilder();
                sb.append(sessionTimeSlotList.get(i).getAttendances().get(j).getUser().getFirstName());
                sb.append(" ");
                sb.append(sessionTimeSlotList.get(i).getAttendances().get(j).getUser().getLastName());

                mentorGradesQuizEditDTO.setName(sb.toString());

                mentorGradesQuizEditDTOList.add(mentorGradesQuizEditDTO);
            }
            mentorGradesScheduledSessionEditDTO.setAttendees(mentorGradesQuizEditDTOList);

            mentorGradesScheduledSessionEditDTOList.add(mentorGradesScheduledSessionEditDTO);
        }

        return mentorGradesScheduledSessionEditDTOList;

    }

    /**
     * This function sets the quiz attendance grades.
     * Returns attendance grades for the view
     */
    public AdminAttendanceGradesResultDTO setQuizAttendanceGrades(Quiz attendance) {

        AdminAttendanceGradesResultDTO mentorAttendanceGradesResultDTO = new AdminAttendanceGradesResultDTO();

        UUID session = attendance.getId();
        mentorAttendanceGradesResultDTO.setSessionId(session);
        mentorAttendanceGradesResultDTO.setSessionType("quiz");

        List<User> students = new ArrayList<>();
        for(int i = 0; i < attendance.getSections().size(); i++) {
            for (int j = 0; j < attendance.getSections().get(i).getStudents().size(); j++) {
                students.add(attendance.getSections().get(i).getStudents().get(j));
            }
        }
        List<User> sortedUsers = students.stream()
                .sorted(Comparator.comparing(User::getFirstName))
                .collect(Collectors.toList());

        mentorAttendanceGradesResultDTO.setUsers(sortedUsers);

        List<TimeSlot> ts = new ArrayList<>();
        ts.add(attendance.getTimeSlot());
        mentorAttendanceGradesResultDTO.setTimeSlot(ts);

        return mentorAttendanceGradesResultDTO;
    }
}
