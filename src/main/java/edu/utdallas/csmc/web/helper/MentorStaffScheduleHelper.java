package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.dto.MentorFileDetailsDTO;
import edu.utdallas.csmc.web.dto.MentorStaffScheduleQuizzesResultDTO;
import edu.utdallas.csmc.web.dto.MentorStaffScheduleSessionResultDTO;
import edu.utdallas.csmc.web.dto.MentorStaffScheduleSubjectDetailsDTO;
import edu.utdallas.csmc.web.model.file.File;
import edu.utdallas.csmc.web.model.misc.Subject;
import edu.utdallas.csmc.web.model.schedule.ShiftAssignment;
import edu.utdallas.csmc.web.model.session.Quiz;
import edu.utdallas.csmc.web.model.session.SessionTimeSlot;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.util.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * This class has all the helper functions that will convert Model objects into DTO objects with the required processing
 * for the Mentor Staff Schedule View.
 */
public class MentorStaffScheduleHelper {

    /**
     * This helper function receives a SessionTimeSlot Model that will be added to a List of
     * MentorStaffScheduleSessionResultDTO that will be sent back to the view.
     */
    public void setSessionDetails(final List<MentorStaffScheduleSessionResultDTO> mentorStaffScheduleSessionResultDTOList, List<SessionTimeSlot> sessionTimeSlotList, final HashMap<String, List<UUID>> mentorWithScheduledSessions) {
        for (SessionTimeSlot currentSessionTimeSlot : sessionTimeSlotList) {
            MentorStaffScheduleSessionResultDTO mentorStaffScheduleSessionResultDTO = new MentorStaffScheduleSessionResultDTO();
            mentorStaffScheduleSessionResultDTO.setStartTime(currentSessionTimeSlot.getStartTime());
            mentorStaffScheduleSessionResultDTO.setTopic(currentSessionTimeSlot.getSession().getTopic());

            // Adding room location
            StringBuilder location = new StringBuilder();
            location.append(currentSessionTimeSlot.getLocation().getBuilding()).append(" ")
                    .append(currentSessionTimeSlot.getLocation().getFloor()).append(".")
                    .append(currentSessionTimeSlot.getLocation().getNumber());
            mentorStaffScheduleSessionResultDTO.setLocation(location.toString());

            // Adding mentors to the DTO for display and to the mentorWithScheduledSessions Map to track mentors with scheduled sessions by start time
            List<ShiftAssignment> currentShiftAssignments = currentSessionTimeSlot.getAssignments();
            StringBuilder mentorName = new StringBuilder();
            for (int i = 0; i < currentShiftAssignments.size(); i++) {

                User currentMentor = currentShiftAssignments.get(i).getMentor();
                mentorName.append(currentMentor.getFirstName()).append(" ")
                        .append(currentMentor.getLastName()).append(", ");

                String currentStartTime = DateUtil.timeFormatTo12Hours.format(currentSessionTimeSlot.getStartTime());

                if (mentorWithScheduledSessions.containsKey(currentStartTime)) {
                    mentorWithScheduledSessions.get(currentStartTime).add(currentMentor.getId());
                } else {
                    List<UUID> newMentorList = new ArrayList<>();
                    newMentorList.add(currentMentor.getId());
                    mentorWithScheduledSessions.put(currentStartTime, newMentorList);
                }

            }
            // Substring to remove the ", " at the end of mentorName
            if (mentorName.length() > 0) {
                mentorStaffScheduleSessionResultDTO.setMentors(mentorName.toString().substring(0, mentorName.length() - 2));
            }

            mentorStaffScheduleSessionResultDTO.setSessionTimeSlotId(currentSessionTimeSlot.getId());

            if (!currentSessionTimeSlot.getSession().getFiles().isEmpty()) {
                List<MentorFileDetailsDTO> fileDetailsDTOList = new ArrayList<>();
                for (File currentFile : currentSessionTimeSlot.getSession().getFiles()) {
                    MentorFileDetailsDTO fileDetailsDTO = new MentorFileDetailsDTO();
                    fileDetailsDTO.setFileId(currentFile.getId());
                    fileDetailsDTO.setName(currentFile.getName());

                    fileDetailsDTOList.add(fileDetailsDTO);
                }
                mentorStaffScheduleSessionResultDTO.setFiles(fileDetailsDTOList);
            }

            mentorStaffScheduleSessionResultDTOList.add(mentorStaffScheduleSessionResultDTO);

        }
    }

    /**
     * This helper function receives a Quiz Model that will be added to a List of MentorStaffScheduleQuizzesResultDTO
     * that will be sent back to the view.
     */
    public void setQuizzDetails(final List<MentorStaffScheduleQuizzesResultDTO> mentorStaffScheduleQuizzesResultDTOList, List<Quiz> quizList) { //check parameters //check if returning list
        for (Quiz currentQuiz : quizList) {
            MentorStaffScheduleQuizzesResultDTO mentorStaffScheduleQuizzesResultDTO = new MentorStaffScheduleQuizzesResultDTO();
            mentorStaffScheduleQuizzesResultDTO.setDescription(currentQuiz.getDescription());
            mentorStaffScheduleQuizzesResultDTO.setTopic(currentQuiz.getTopic());

            // Adding room location
            StringBuilder location = new StringBuilder();
            location.append(currentQuiz.getTimeSlot().getLocation().getBuilding()).append(" ")
                    .append(currentQuiz.getTimeSlot().getLocation().getFloor()).append(".")
                    .append(currentQuiz.getTimeSlot().getLocation().getNumber());
            mentorStaffScheduleQuizzesResultDTO.setLocation(location.toString());

            if (!currentQuiz.getFiles().isEmpty()) {
                List<MentorFileDetailsDTO> fileDetailsDTOList = new ArrayList<>();
                for (File currentFile : currentQuiz.getFiles()) {
                    MentorFileDetailsDTO fileDetailsDTO = new MentorFileDetailsDTO();
                    fileDetailsDTO.setFileId(currentFile.getId());
                    fileDetailsDTO.setName(currentFile.getName());

                    fileDetailsDTOList.add(fileDetailsDTO);
                }
                mentorStaffScheduleQuizzesResultDTO.setFiles(fileDetailsDTOList);
            }

            mentorStaffScheduleQuizzesResultDTOList.add(mentorStaffScheduleQuizzesResultDTO);
        }
    }

    /**
     * This helper function receives a Subject Model that will be added to a List of MentorStaffScheduleSubjectDetailsDTO
     * that will be sent back to the view.
     */
    public void buildSubjectLegendDetails(final List<MentorStaffScheduleSubjectDetailsDTO> subjectLegendList, List<Subject> subjectList) {
        for (Subject currentSubject : subjectList) {
            MentorStaffScheduleSubjectDetailsDTO subjectDetailsDTO = new MentorStaffScheduleSubjectDetailsDTO();
            subjectDetailsDTO.setName(currentSubject.getName());
            subjectDetailsDTO.setColor(currentSubject.getColor());

            subjectLegendList.add(subjectDetailsDTO);
        }
    }

}
