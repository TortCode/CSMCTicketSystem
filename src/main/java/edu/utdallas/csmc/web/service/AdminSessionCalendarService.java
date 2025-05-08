package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.AdminScheduledSessionFormDTO;
import edu.utdallas.csmc.web.dto.AdminSessionCalendarEventsDTO;
import edu.utdallas.csmc.web.dto.AdminSessionTimeSlotCreateDTO;
import edu.utdallas.csmc.web.dto.AdminSessionTimeSlotEditDTO;
import edu.utdallas.csmc.web.dto.AdminSessionCalendarEventsSessionDTO;
import edu.utdallas.csmc.web.dto.AdminSessionCalendarRoom;
import edu.utdallas.csmc.web.dto.AdminSessionCalendarShiftAssignmentDTO;
import edu.utdallas.csmc.web.dto.AdminSessionCalendarShiftAssignmentMentorsDTO;
import edu.utdallas.csmc.web.dto.AdminSessionCalendarEventsQuizDTO;
import edu.utdallas.csmc.web.dto.AdminUnScheduledSessionDTO;
import edu.utdallas.csmc.web.model.session.*;
import edu.utdallas.csmc.web.model.misc.Room;
import edu.utdallas.csmc.web.model.schedule.ShiftAssignment;
import edu.utdallas.csmc.web.repository.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.TimeZone;
import java.sql.Time;
import java.text.SimpleDateFormat;

@Service
@Log4j2
public class AdminSessionCalendarService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ScheduledSessionRepository scheduledSessionRepository;

    @Autowired
    private SessionTimeSlotRepository sessionTimeSlotRepository;

    @Autowired
    private QuizTimeSlotRepository quizTimeSlotRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ShiftAssignmentRepository shiftAssignmentRepository;

    @Autowired
    private RoomRepository roomRepository;

    /*
    public AdminScheduledSessionFormDTO getScheduledSessionDetails(String sessionId) {
        AdminScheduledSessionFormDTO adminScheduledSessionFormDTO = new AdminScheduledSessionFormDTO();
        ScheduledSession scheduledSession = new ScheduledSession();
        scheduledSession = scheduledSessionRepository.getScheduledSessionById(UUID.fromString(sessionId));
        if (scheduledSession != null) {
            adminScheduledSessionFormDTO.setType(scheduledSession.getType().getName());
            adminScheduledSessionFormDTO.setCapacity(scheduledSession.getDefaultCapacity());
            adminScheduledSessionFormDTO.setColor(scheduledSession.getColor());
            adminScheduledSessionFormDTO.setDescription(scheduledSession.getDescription());
            // to check defaul time
            adminScheduledSessionFormDTO.setDurationH(scheduledSession.getDefaultDuration().length());
            adminScheduledSessionFormDTO.setDurationM(scheduledSession.getDefaultDuration().length());
            adminScheduledSessionFormDTO.setGraded(scheduledSession.isGraded());
            adminScheduledSessionFormDTO.setLocation(scheduledSession.getDefaultLocation().getBuilding());
            adminScheduledSessionFormDTO.setMentorInstructions(scheduledSession.getMentorInstructions());
            adminScheduledSessionFormDTO.setNumericGrade(scheduledSession.isNumericGrade());
            adminScheduledSessionFormDTO.setRepeats(scheduledSession.getRepeats());
            adminScheduledSessionFormDTO.setStudentInstructions(scheduledSession.getStudentInstructions());
            adminScheduledSessionFormDTO.setTopic(scheduledSession.getTopic());
            // files not set

        }
        return adminScheduledSessionFormDTO;
    }
    */
    /*
    public void updateSessionSchedule(String sessionid) {
        ScheduledSession scheduledSession = new ScheduledSession();
        scheduledSession = scheduledSessionRepository.getScheduledSessionById(UUID.fromString(sessionid));
        List<SessionTimeSlot> sessionTimeSlots = new ArrayList<>();
        sessionTimeSlots = scheduledSession.getTimeslots();
    
        SessionTimeSlot sessionTimeSlot = new SessionTimeSlot();
    
    }
    */
    
    /*
    // the DTO is not different for uncheduled and scheduled sessions. Hence the
    // return type.
    public List<AdminUnScheduledSessionDTO> getScheduledSessions() {
        List<AdminUnScheduledSessionDTO> adminScheduledSessionDTOList = new ArrayList<>();
        AdminUnScheduledSessionDTO adminScheduledSessionDTO;
        List<ScheduledSession> scheduledSessions = scheduledSessionRepository
                .geScheduledSessionsForCalendar(new Date());
    
        // System.out.println("Pringting Scheduled Sessions size:");
        // for(ScheduledSession scheduledSession:scheduledSessions){
        // System.out.println(scheduledSession.getTimeslots().size()+"
        // "+scheduledSession.getRepeats());
        // }
        for (ScheduledSession scheduledSession : scheduledSessions) {
            adminScheduledSessionDTO = new AdminUnScheduledSessionDTO();
            adminScheduledSessionDTO.color = scheduledSession.getColor();
            adminScheduledSessionDTO.defaultCapacity = scheduledSession.getDefaultCapacity();
            adminScheduledSessionDTO.defaultDuration = scheduledSession.getDefaultDuration();
            adminScheduledSessionDTO.defaultLocation = scheduledSession.getDefaultLocation().getBuilding()
                    + scheduledSession.getDefaultLocation().getNumber();
            adminScheduledSessionDTO.repeats = scheduledSession.getRepeats();
            adminScheduledSessionDTO.topic = scheduledSession.getTopic();
            adminScheduledSessionDTO.timeslots = scheduledSession.getTimeslots();
            adminScheduledSessionDTO.id = scheduledSession.getId().toString();
            adminScheduledSessionDTO.roomId = scheduledSession.getDefaultLocation().getId().toString();
            adminScheduledSessionDTOList.add(adminScheduledSessionDTO);
    
        }
        return adminScheduledSessionDTOList;
    }
    */
    
    public List<AdminUnScheduledSessionDTO> getUnScheduledSessions() {
        List<AdminUnScheduledSessionDTO> adminUnScheduledSessionDTOList = new ArrayList<>();
        AdminUnScheduledSessionDTO adminUnScheduledSessionDTO;
        List<ScheduledSession> scheduledSessions = scheduledSessionRepository.getUnscheduledSessionsForCalendar();
        for (ScheduledSession scheduledSession : scheduledSessions) {
            adminUnScheduledSessionDTO = new AdminUnScheduledSessionDTO();
            adminUnScheduledSessionDTO.color = scheduledSession.getColor();
            adminUnScheduledSessionDTO.defaultCapacity = scheduledSession.getDefaultCapacity();
            adminUnScheduledSessionDTO.defaultDuration = scheduledSession.getDefaultDuration();

            if (scheduledSession.getDefaultLocation() != null) {
                Room r = scheduledSession.getDefaultLocation();
                adminUnScheduledSessionDTO.roomId = r.getId().toString();
                adminUnScheduledSessionDTO.defaultLocation = r.toString();
            }

            adminUnScheduledSessionDTO.repeats = scheduledSession.getRepeats();
            adminUnScheduledSessionDTO.topic = scheduledSession.getTopic();
            adminUnScheduledSessionDTO.timeslots = scheduledSession.getTimeslots();

            adminUnScheduledSessionDTO.id = scheduledSession.getId().toString();
            adminUnScheduledSessionDTOList.add(adminUnScheduledSessionDTO);

            // System.out.println(adminUnScheduledSessionDTO);
            // System.out.println(adminUnScheduledSessionDTO.color);
            // System.out.println(adminUnScheduledSessionDTO.defaultCapacity);
            // System.out.println(adminUnScheduledSessionDTO.defaultDuration);
            // System.out.println(adminUnScheduledSessionDTO.defaultLocation);
            // System.out.println(adminUnScheduledSessionDTO.repeats);
            // System.out.println(adminUnScheduledSessionDTO.topic);
            // System.out.println(adminUnScheduledSessionDTO.timeslots.get(0));
            // System.out.println(adminUnScheduledSessionDTO.id);
            // System.out.println(adminUnScheduledSessionDTO.roomId);
        }
        return adminUnScheduledSessionDTOList;
    }

    public List<AdminSessionCalendarEventsDTO> fetchEvents(String start, String end) throws Exception {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date startSlot = formatter.parse(start);
            Date endSlot = formatter.parse(end);

            List<AdminSessionCalendarEventsDTO> dto = new ArrayList<>();

            List<SessionTimeSlot> timeslots = sessionTimeSlotRepository
                    .findSessionTimeSlotsForEntireDayByDate(startSlot, endSlot);

            for (SessionTimeSlot x : timeslots) {
                AdminSessionCalendarEventsDTO event = new AdminSessionCalendarEventsDTO();
                AdminSessionCalendarEventsSessionDTO session = new AdminSessionCalendarEventsSessionDTO();
                event.setId(x.getId());
                event.setStartTime(x.getStartTime());
		        event.setEndTime(x.getEndTime());
                Room eventLocation = x.getLocation();
                if (eventLocation != null) {
                    String room = eventLocation.getBuilding() + " " + eventLocation.getFloor() + "."
                            + eventLocation.getNumber();
                    event.setLocation(new AdminSessionCalendarEventsDTO.RoomDTO(eventLocation.getId(), room));
                }
                session.setTopic(x.getSession().getTopic());
                session.setRegistrations(x.getRegistrations());
                session.setRegisteredStudents(x.getRegistrations().size());
                session.setCapacity(x.getCapacity());
                session.setAssignments(x.getAssignments());
                event.setSession(session);
                dto.add(event);
            }

            List<QuizTimeSlot> quizTimeslots = quizTimeSlotRepository.findQuizTimeSlotsForEntireDayByDate(startSlot,
                    endSlot);

            for (QuizTimeSlot y : quizTimeslots) {
                AdminSessionCalendarEventsDTO event = new AdminSessionCalendarEventsDTO();
                AdminSessionCalendarEventsQuizDTO quiz = new AdminSessionCalendarEventsQuizDTO();
                event.setId(y.getId());
                event.setStartTime(y.getStartTime());
                event.setEndTime(y.getEndTime());
                Room eventLocation = y.getLocation();
                if (eventLocation != null) {
                    String room = eventLocation.getBuilding() + " " + eventLocation.getFloor() + "."
                            + eventLocation.getNumber();
                    event.setLocation(new AdminSessionCalendarEventsDTO.RoomDTO(eventLocation.getId(), room));
                }

                Quiz quiz1 = y.getQuiz();
                if (quiz1 != null) {
                    String topic = quiz1.getTopic();
                    quiz.setTopic(topic);
                } else {
                    quiz.setTopic("");
                }
                //   quiz.setTopic(y.getQuiz().getTopic());
                event.setQuiz(quiz);
                dto.add(event);
            }
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UUID createSessionTimeSlot(AdminSessionTimeSlotCreateDTO timeSlot) throws Exception {
        try {
            SessionTimeSlot newTimeSlot = new SessionTimeSlot();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date startDate = sdf.parse(timeSlot.getStart());
            Date endDate = sdf.parse(timeSlot.getEnd());
            newTimeSlot.setStartTime(startDate);
            newTimeSlot.setEndTime(endDate);

            newTimeSlot.setSession(timeSlot.getSession());

            newTimeSlot.setCapacity(timeSlot.getCapacity());

            if (timeSlot.getLocation() != null) {
                newTimeSlot.setLocation(timeSlot.getLocation());
            }
            sessionTimeSlotRepository.save(newTimeSlot);
            sessionTimeSlotRepository.flush();
            UUID timeSlotID = newTimeSlot.getId();

            Request sessionRequest = newTimeSlot.getSession().getRequest();
            if (sessionRequest != null
                    && newTimeSlot.getSession().getRepeats() <= newTimeSlot.getSession().getTimeslots().size()) {
                sessionRequest.setStatus(Request.RequestStatus.COMPLETED);
                requestRepository.save(sessionRequest);
            }

            return timeSlotID;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean editSessionTimeSlot(AdminSessionTimeSlotEditDTO timeSlot) {

        try {
            SessionTimeSlot sessionTimeSlot = timeSlot.getId();
            boolean timeChanged = false;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date startDate = sdf.parse(timeSlot.getStart());
            Date endDate = sdf.parse(timeSlot.getEnd());

            if (sessionTimeSlot.getStartTime().compareTo(startDate) != 0
                    || sessionTimeSlot.getEndTime().compareTo(endDate) != 0) {
                timeChanged = true;
            }

            sessionTimeSlot.setStartTime(startDate);
            sessionTimeSlot.setEndTime(endDate);

            if (timeChanged) {
                // Unassign mentors
                sessionTimeSlot.setAssignments(new ArrayList<ShiftAssignment>());
            }

            if (timeSlot.getLocation() != null) {
                sessionTimeSlot.setLocation(timeSlot.getLocation());
            }

            sessionTimeSlot.setCapacity(timeSlot.getCapacity());

            sessionRepository.save(sessionTimeSlot);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<AdminSessionCalendarShiftAssignmentMentorsDTO> getMentorsForShiftAssignments(String date, String start)
            throws Exception {
        try {
            SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeParser = new SimpleDateFormat("HH:mm");

            Date eventDate = dateParser.parse(date);
            Time startTime = new Time(timeParser.parse(start).getTime());
            
            List<AdminSessionCalendarShiftAssignmentMentorsDTO> result = new ArrayList<>();
            List<ShiftAssignment> shiftAssignments = shiftAssignmentRepository
                    .getShiftAssignmentByDateAndInterval(eventDate, startTime);
            for (ShiftAssignment assignment : shiftAssignments) {
                AdminSessionCalendarShiftAssignmentMentorsDTO shift = new AdminSessionCalendarShiftAssignmentMentorsDTO();
                shift.setId(assignment.getId());
                shift.setMentor(assignment.getMentor().getFirstName() + " " + assignment.getMentor().getLastName().substring(0,1) + ".");
                result.add(shift);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public List<AdminSessionCalendarRoom> getLocations() {
        try {
            List<Room> rooms = roomRepository.findAll();
            List<AdminSessionCalendarRoom> result = new ArrayList<>();
            for (Room r : rooms) {
                AdminSessionCalendarRoom room = new AdminSessionCalendarRoom();
                room.setId(r.getId());
                room.setLocation(r.toString());
                result.add(room);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean assignShifts(AdminSessionCalendarShiftAssignmentDTO data) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date startTime = sdf.parse(data.getDate() + " " + data.getStartTime());
            Date endTime = sdf.parse(data.getDate() + " " + data.getEndTime());

            SessionTimeSlot timeSlot = data.getId();
            
            timeSlot.setStartTime(startTime);
            timeSlot.setEndTime(endTime);
            timeSlot.setLocation(data.getLocation());
            timeSlot.setCapacity(data.getCapacity());
            shiftAssignmentRepository.clearAllAssignments(timeSlot);
            timeSlot.setAssignments(new ArrayList<ShiftAssignment>(data.getAssignments()));
            sessionRepository.save(timeSlot);

            for(ShiftAssignment x : data.getAssignments()){
		        shiftAssignmentRepository.updateTimeSlot(x.getId(), timeSlot);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
