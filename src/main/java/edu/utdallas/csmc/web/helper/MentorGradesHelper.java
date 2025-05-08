package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.dto.*;
import edu.utdallas.csmc.web.model.session.*;
import edu.utdallas.csmc.web.model.user.User;

import java.util.*;
import java.util.stream.Collectors;

public class MentorGradesHelper {

    public List<MentorGradesSessionQuizResultDTO> setSchedulesSessionDetails(List<ScheduledSession> scheduledSessions)
    {
        List<MentorGradesSessionQuizResultDTO> mentorGradesSessionQuizResultDTOList = new ArrayList<>();

        for(int i=0;i<scheduledSessions.size();i++)
        {
            List<String> sections = new ArrayList<>();
            MentorGradesSessionQuizResultDTO sessionResultDTO = new MentorGradesSessionQuizResultDTO();

            sessionResultDTO.setDescription(scheduledSessions.get(i).getDescription());
            for(int j=0; j<scheduledSessions.get(i).getSections().size();j++) {
                StringBuilder sb = new StringBuilder();
                sb.append(scheduledSessions.get(i).getSections().get(j).getCourse().getDepartment().getAbbreviation());
                sb.append(" ");
                sb.append(scheduledSessions.get(i).getSections().get(j).getCourse().getNumber());
                sb.append(".");
                sb.append(scheduledSessions.get(i).getSections().get(j).getNumber());
                sections.add(sb.toString());
            }
            sessionResultDTO.setSection(sections);
            sessionResultDTO.setScheduledSessionId(scheduledSessions.get(i).getId());
            sessionResultDTO.setTopic(scheduledSessions.get(i).getTopic());

            mentorGradesSessionQuizResultDTOList.add(sessionResultDTO);
        }

        return mentorGradesSessionQuizResultDTOList;
    }

    public List<MentorGradesSessionQuizResultDTO> setQuizzes(List<Quiz> quiz) {
        List<MentorGradesSessionQuizResultDTO> mentorGradesQuizResultDTOList = new ArrayList<>();

        for(int i=0;i<quiz.size();i++)
        {
            List<String> sections = new ArrayList<>();
            MentorGradesSessionQuizResultDTO quizResultDTO = new MentorGradesSessionQuizResultDTO();

            quizResultDTO.setDescription(quiz.get(i).getDescription());
            for(int j=0; j<quiz.get(i).getSections().size();j++){
                StringBuilder sb = new StringBuilder();
                sb.append(quiz.get(i).getSections().get(j).getCourse().getDepartment().getAbbreviation());
                sb.append(" ");
                sb.append(quiz.get(i).getSections().get(j).getCourse().getNumber());
                sb.append(".");
                sb.append(quiz.get(i).getSections().get(j).getNumber());

                sections.add(sb.toString());
            }
            quizResultDTO.setSection(sections);
            quizResultDTO.setQuizId(quiz.get(i).getId());
            quizResultDTO.setTopic(quiz.get(i).getTopic());

            mentorGradesQuizResultDTOList.add(quizResultDTO);
        }

        return mentorGradesQuizResultDTOList;
    }

    public MentorAttendanceGradesResultDTO setScheduledSessionAttendanceGrades(ScheduledSession attendance) {

            List<TimeSlot> timeslot = new ArrayList<>();
            MentorAttendanceGradesResultDTO mentorAttendanceGradesResultDTO = new MentorAttendanceGradesResultDTO();

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


    public List<MentorGradesQuizEditDTO> setQuizAttendance(List<QuizAttendance> quizAttendances, Optional<Session> session)
    {
        List<MentorGradesQuizEditDTO> mentorGradesQuizEditDTOList = new ArrayList<>();

        for(int i=0;i<quizAttendances.size();i++)
        {
            MentorGradesQuizEditDTO mentorGradesQuizEditDTO = new MentorGradesQuizEditDTO();

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

    public MentorGradesSessionResultDTO setSessionDetails(Optional<Session> session) {

        MentorGradesSessionResultDTO mentorGradesSessionResultDTO = new MentorGradesSessionResultDTO();

        mentorGradesSessionResultDTO.setSessionId(session.get().getId());
        mentorGradesSessionResultDTO.setTopic(session.get().getTopic());
        mentorGradesSessionResultDTO.setGraded(session.get().isGraded());
        mentorGradesSessionResultDTO.setNumericGrade(session.get().isNumericGrade());

        return mentorGradesSessionResultDTO;
    }

    public List<MentorGradesScheduledSessionEditDTO> setSessionTimeSlotDetails(List<SessionTimeSlot> sessionTimeSlotList, Optional<Session> session) {
        List<MentorGradesScheduledSessionEditDTO> mentorGradesScheduledSessionEditDTOList = new ArrayList<>();

        for(int i=0;i<sessionTimeSlotList.size();i++) {
            MentorGradesScheduledSessionEditDTO mentorGradesScheduledSessionEditDTO = new MentorGradesScheduledSessionEditDTO();

            mentorGradesScheduledSessionEditDTO.setStartTime(sessionTimeSlotList.get(i).getStartTime());
            mentorGradesScheduledSessionEditDTO.setEndTime(sessionTimeSlotList.get(i).getEndTime());

            List<MentorGradesQuizEditDTO> mentorGradesQuizEditDTOList = new ArrayList<>();
            for(int j=0;j<sessionTimeSlotList.get(i).getAttendances().size();j++){
                MentorGradesQuizEditDTO mentorGradesQuizEditDTO = new MentorGradesQuizEditDTO();

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

    public MentorAttendanceGradesResultDTO setQuizAttendanceGrades(Quiz attendance) {

        MentorAttendanceGradesResultDTO mentorAttendanceGradesResultDTO = new MentorAttendanceGradesResultDTO();

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
