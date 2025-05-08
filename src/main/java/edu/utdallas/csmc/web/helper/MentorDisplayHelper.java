package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.dto.*;
import edu.utdallas.csmc.web.model.schedule.Shift;
import edu.utdallas.csmc.web.model.schedule.ShiftAssignment;
import edu.utdallas.csmc.web.model.schedule.Timesheet;
import edu.utdallas.csmc.web.model.session.Quiz;
import edu.utdallas.csmc.web.model.session.SessionTimeSlot;
import edu.utdallas.csmc.web.model.session.WalkInAttendance;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.model.user.info.Specialty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MentorDisplayHelper {

    //Helper for mentors
    public List<MentorDisplayMentorsResultDTO> setMentorDetails(List<Timesheet> sessions, Map<User,List<Specialty>> map) {
        List<MentorDisplayMentorsResultDTO> mentorDisplayMentorsResultDTOList = new ArrayList<>();

        for(Timesheet session: sessions) {
            MentorDisplayMentorsResultDTO mentorDisplayMentorsResultDTO = new MentorDisplayMentorsResultDTO();
            mentorDisplayMentorsResultDTO.setMentorId(session.getUser().getId());
            if(session.getUser().getProfile()!=null && session.getUser().getProfile().getPreferredName()!=null ){
                mentorDisplayMentorsResultDTO.setPreferredName(session.getUser().getProfile().getPreferredName());
            }
            else
                mentorDisplayMentorsResultDTO.setPreferredName(session.getUser().getFirstName());
            mentorDisplayMentorsResultDTO.setUserName(session.getUser().getUsername());
            List<MentorDisplaySpecialtyResutltDTO> mentorDisplaySpecialityResutltDTOList = new ArrayList<>();
            for(Specialty specialty: map.get(session.getUser())) {
                MentorDisplaySpecialtyResutltDTO mentorDisplaySpecialityResutltDTO = new MentorDisplaySpecialtyResutltDTO();
                mentorDisplaySpecialityResutltDTO.setSpecialtyRating(specialty.getRating());
                mentorDisplaySpecialityResutltDTO.setShowOnCalendar(specialty.getTopic().isShowOnCalendar());
                mentorDisplaySpecialityResutltDTO.setSubjectName(specialty.getTopic().getName());
                mentorDisplaySpecialityResutltDTO.setSubjectColor(specialty.getTopic().getColor());
                mentorDisplaySpecialityResutltDTO.setSubjectAbbreviation(specialty.getTopic().getAbbreviation());
                mentorDisplaySpecialityResutltDTOList.add(mentorDisplaySpecialityResutltDTO);
            }
            mentorDisplayMentorsResultDTO.setSpecialties(mentorDisplaySpecialityResutltDTOList);
            mentorDisplayMentorsResultDTOList.add(mentorDisplayMentorsResultDTO);
        }
        return mentorDisplayMentorsResultDTOList;
    }

    //Helper for students
    public List<MentorDisplayStudentsResultDTO> setStudentDetails(List<WalkInAttendance> students) {
        List<MentorDisplayStudentsResultDTO> mentorDisplayStudentsResultDTOList = new ArrayList<>();
        for(WalkInAttendance student: students) {
            MentorDisplayStudentsResultDTO mentorDisplayStudentsResultDTO = new MentorDisplayStudentsResultDTO();
            mentorDisplayStudentsResultDTO.setFirstName(student.getUser().getFirstName());
            mentorDisplayStudentsResultDTO.setCourseAbbreviation(student.getCourse().getDepartment().getAbbreviation());
            mentorDisplayStudentsResultDTO.setCourseNumber(student.getCourse().getNumber());
            mentorDisplayStudentsResultDTO.setActivityName(student.getActivity().getName());
            mentorDisplayStudentsResultDTO.setTimeIn(student.getTimeIn());
            mentorDisplayStudentsResultDTOList.add(mentorDisplayStudentsResultDTO);
        }
        return mentorDisplayStudentsResultDTOList;
    }

    //Helper for sessions
    public List<MentorDisplaySessionsResultDTO> setSessionDetails(List<SessionTimeSlot> sessions){
        List<MentorDisplaySessionsResultDTO> mentorDisplaySessionsResultDTOList = new ArrayList<>();
        for(SessionTimeSlot session: sessions) {
            MentorDisplaySessionsResultDTO mentorDisplaySessionsResultDTO= new MentorDisplaySessionsResultDTO();
            mentorDisplaySessionsResultDTO.setTopicName(session.getSession().getTopic());
            mentorDisplaySessionsResultDTO.setStartTime(session.getStartTime());
            mentorDisplaySessionsResultDTO.setLocation(session.getLocation().getBuilding() + " " + session.getLocation().getFloor() + "." + session.getLocation().getNumber());
            mentorDisplaySessionsResultDTO.setCapacity(session.getCapacity());
            mentorDisplaySessionsResultDTO.setRemainingSeats(session.getRemainingSeats());
            List<String> mentors = new ArrayList<>();
            for(ShiftAssignment assignment: session.getAssignments()) {
                if(assignment.getMentor().getProfile()!=null && assignment.getMentor().getProfile().getPreferredName()!=null){
                    mentors.add(assignment.getMentor().getProfile().getPreferredName());
                } else{
                    mentors.add(assignment.getMentor().getFirstName());
                }
            }
            mentorDisplaySessionsResultDTO.setMentorPreferredName(mentors);

            mentorDisplaySessionsResultDTOList.add(mentorDisplaySessionsResultDTO);
        }
        return mentorDisplaySessionsResultDTOList;
    }

    //Helper for quizzes
    public List<MentorDisplayQuizzesResultDTO> setQuizzDetails(List<Quiz> quizzes){
        List<MentorDisplayQuizzesResultDTO> mentorDisplayQuizzesResultDTOList = new ArrayList<>();
        for(Quiz quiz: quizzes) {
            MentorDisplayQuizzesResultDTO mentorDisplayQuizzesResultDTO = new MentorDisplayQuizzesResultDTO();
            mentorDisplayQuizzesResultDTO.setTopic(quiz.getTopic());

            StringBuilder location = new StringBuilder();
            location.append(quiz.getTimeSlot().getLocation().getBuilding() + " " + quiz.getTimeSlot().getLocation().getFloor() + "." + quiz.getTimeSlot().getLocation().getNumber());
            mentorDisplayQuizzesResultDTO.setLocation(location.toString());

            mentorDisplayQuizzesResultDTO.setStartDate(quiz.getTimeSlot().getStartTime());
            mentorDisplayQuizzesResultDTO.setEndDate(quiz.getTimeSlot().getEndTime());

            mentorDisplayQuizzesResultDTOList.add(mentorDisplayQuizzesResultDTO);
        }
        return mentorDisplayQuizzesResultDTOList;
    }

    //Helper for shift leader
    public MentorDisplayShiftLeaderResultDTO setShiftLeaderDetails(Shift shiftLeader){
        MentorDisplayShiftLeaderResultDTO mentorDisplayShiftLeaderResultDTO = new MentorDisplayShiftLeaderResultDTO();
        if(shiftLeader != null)
            mentorDisplayShiftLeaderResultDTO.setId(shiftLeader.getShiftLeader().getId());
        else
            mentorDisplayShiftLeaderResultDTO.setId(null);
        return mentorDisplayShiftLeaderResultDTO;
    }
}
