package edu.utdallas.csmc.web.dto;

import edu.utdallas.csmc.web.model.session.WalkInActivity;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class MentorSwipeAjaxReturnMessageDTO {
    String successMessage;
    String errorMessage;
    String preferredName;
    String firstName;
    String attendance;
    String swipe;
    List<WalkInActivity> activity;
    List<MentorSwipeEntryCourseResultDTO> supportedCourses;
    List<MentorSwipeEntryCourseResultDTO> unsupportedCourses;
    List<MentorSwipeEntryCourseResultDTO> quizzes;
    List<MentorSwipeEntryCourseResultDTO> mentors;
    UUID userId;
    String timeIn;
    String timeOut;
}
