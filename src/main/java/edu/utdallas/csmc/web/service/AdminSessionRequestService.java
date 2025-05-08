package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.*;
import edu.utdallas.csmc.web.helper.AdminGradesHelper;
import edu.utdallas.csmc.web.model.course.Section;
import edu.utdallas.csmc.web.model.file.File;
import edu.utdallas.csmc.web.model.misc.Room;
import edu.utdallas.csmc.web.model.misc.Semester;
import edu.utdallas.csmc.web.model.session.*;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.repository.*;
import edu.utdallas.csmc.web.util.StringUtils;
import edu.utdallas.csmc.web.util.TimeUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Log4j2
public class AdminSessionRequestService {

    @Autowired
    private ScheduledSessionRepository scheduledSessionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private SessionTypeRepository sessionTypeRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private QuizAttendanceRepository quizAttendanceRepository;

    @Autowired
    private SessionTimeSlotRepository sessionTimeSlotRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private SessionAttendanceRepository sessionAttendanceRepository;

    @Autowired
    private QuizTimeSlotRepository quizTimeSlotRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private ScheduledSessionAttendanceRepository scheduledSessionAttendanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    AdminGradesHelper adminGradesHelper = new AdminGradesHelper();

    /**
     * This function fetches requests from the system.
     */
    public AdminSessionTableDTO getSessionTable() {
        AdminSessionTableDTO adminSessionTableDTO = new AdminSessionTableDTO();
        adminSessionTableDTO.setNewRequests(new ArrayList<>());
        adminSessionTableDTO.setPendingRequests(new ArrayList<>());
        adminSessionTableDTO.setCompletedRequests(new ArrayList<>());
        adminSessionTableDTO.setDeniedRequests(new ArrayList<>());

        List<Request> requestsNew = requestRepository.getRequestsByStatus2(Request.RequestStatus.NEW);
        Optional<Semester> semester = semesterRepository.findSemesterByActive(true);
        for (Request r : requestsNew) {
            if(semester!=null && semester.get()!=null && r.getEndDate().compareTo(semester.get().getStartDate()) < 0){
                continue;
            }
            String topic = r.getTopic();
            String sections = StringUtils.SectionToString(r.getSections());
            String instructors = r.getUser().getFirstName() + " " + r.getUser().getLastName();
            String type = r.getType().getName();
            String dates = StringUtils.StartDateAndEndDateToString(r.getStartDate(), r.getEndDate());
            String materials = StringUtils.FileListToFileNameAndUIString(r.getFiles());
            String timeRequested = r.getCreated().toString();

            String id = r.getId().toString();

            int sessionType = r.getType().getName().equals("quiz") ? 1 : 0;

            AdminSessionNewRequestDTO adminSessionNewRequestDTO = new AdminSessionNewRequestDTO();

            adminSessionNewRequestDTO.setTopic(topic);
            adminSessionNewRequestDTO.setSections(sections);
            adminSessionNewRequestDTO.setInstructors(instructors);
            adminSessionNewRequestDTO.setType(type);
            adminSessionNewRequestDTO.setDates(dates);
            adminSessionNewRequestDTO.setMaterials(materials);
            adminSessionNewRequestDTO.setTimeRequested(timeRequested);
            adminSessionNewRequestDTO.setId(id);
            adminSessionNewRequestDTO.setSessionType(sessionType);

            adminSessionTableDTO.getNewRequests().add(adminSessionNewRequestDTO);
        }


        List<Request> requestsDenied = requestRepository.getRequestsByStatus2(Request.RequestStatus.DENIED);
        for (Request r : requestsDenied) {
            if(semester!=null && semester.get()!=null && r.getEndDate().compareTo(semester.get().getStartDate()) < 0){
                continue;
            }
            String topic = r.getTopic();
            String sections = StringUtils.SectionToString(r.getSections());
            String instructors = r.getUser().getFirstName() + " " + r.getUser().getLastName();
            String type = r.getType().getName();
            String dates = StringUtils.StartDateAndEndDateToString(r.getStartDate(), r.getEndDate());
            String id = r.getId().toString();
            int sessionType = r.getType().getName().equals("quiz") ? 1 : 0;

            AdminSessionDeniedDTO adminSessionDeniedDTO = new AdminSessionDeniedDTO();

            adminSessionDeniedDTO.setTopic(topic);
            adminSessionDeniedDTO.setSections(sections);
            adminSessionDeniedDTO.setInstructors(instructors);
            adminSessionDeniedDTO.setType(type);
            adminSessionDeniedDTO.setDates(dates);
            adminSessionDeniedDTO.setId(id);
            adminSessionDeniedDTO.setSessionType(sessionType);
            adminSessionDeniedDTO.setReason("");

            adminSessionTableDTO.getDeniedRequests().add(adminSessionDeniedDTO);
        }


        List<Quiz> quizzes = quizRepository.findAll();
        List<ScheduledSession> scheduledSessions = scheduledSessionRepository.findAll();

        for (Quiz q : quizzes) {

            Request r = q.getRequest();
            if (r == null) continue;
            if(semester!=null && semester.get()!=null && r.getEndDate().compareTo(semester.get().getStartDate()) < 0){
                continue;
            }
            String topic = q.getTopic();
            String sections = StringUtils.SectionToString(q.getSections().size() > 0 ? q.getSections() : r.getSections());
            String instructors = "";
            if (r.getUser() != null)
                instructors = r.getUser().getFirstName() + " " + r.getUser().getLastName();
            else
                instructors = StringUtils.InstructorsToString(r.getSections());
            String type = q.getType().getName();
            String dates = StringUtils.QuiztimeSlotToString(q.getTimeSlot());
            String materials = StringUtils.FileListToFileNameAndUIString(q.getFiles().size() > 0 ? q.getFiles() : r.getFiles());
            String repeats = "";
            String id = q.getId().toString();
            int sessionType = 1;

            AdminSessionCompletedDTO adminSessionCompletedDTO = new AdminSessionCompletedDTO();

            adminSessionCompletedDTO.setTopic(topic);
            adminSessionCompletedDTO.setSections(sections);
            adminSessionCompletedDTO.setInstructors(instructors);
            adminSessionCompletedDTO.setType(type);
            adminSessionCompletedDTO.setDates(dates);
            adminSessionCompletedDTO.setMaterials(materials);
            adminSessionCompletedDTO.setRepeats(repeats);
            adminSessionCompletedDTO.setId(id);
            adminSessionCompletedDTO.setSessionType(sessionType);

            adminSessionTableDTO.getCompletedRequests().add(adminSessionCompletedDTO);
        }

        for (ScheduledSession ss : scheduledSessions) {
            Request r = ss.getRequest();
            if (r == null) continue;
            if(semester!=null && semester.get()!=null && r.getEndDate().compareTo(semester.get().getStartDate()) < 0){
                continue;
            }
            String status = r.getStatus().name();

            if (status.equals(Request.RequestStatus.PENDING.name()) && ss.getRepeats() <= ss.getTimeslots().size() ) {
                r.setStatus(Request.RequestStatus.COMPLETED);
                requestRepository.save(r);
                ss.setRequest(r);
                scheduledSessionRepository.save(ss);

                status = Request.RequestStatus.COMPLETED.name();
            }

            int table = status.equals(Request.RequestStatus.COMPLETED.name()) ? 3 :
                    status.equals(Request.RequestStatus.PENDING.name()) ? 2 : 4;


            String topic = ss.getTopic();
            String sections = StringUtils.SectionToString(ss.getSections().size() > 0 ? ss.getSections() : r.getSections());
            String instructors = "";
            if (r.getUser() != null)
                instructors = r.getUser().getFirstName() + " " + r.getUser().getLastName();
            else
                instructors = StringUtils.InstructorsToString(r.getSections());
            String type = ss.getType().getName();
            String dates = StringUtils.StartDateAndEndDateToString(r.getStartDate(), r.getEndDate());
            String materials = StringUtils.FileListToFileNameAndUIString(ss.getFiles().size() > 0 ? ss.getFiles(): r.getFiles());
            String repeats = ss.getTimeslots().size() + "/"+ss.getRepeats();
            String id = ss.getId().toString();

            int sessionType = 0;

            if (table == 2) {
                AdminSessionPendingDTO adminSessionPendingDTO = new AdminSessionPendingDTO();

                adminSessionPendingDTO.setTopic(topic);
                adminSessionPendingDTO.setSections(sections);
                adminSessionPendingDTO.setInstructors(instructors);
                adminSessionPendingDTO.setType(type);
                adminSessionPendingDTO.setDates(dates);
                adminSessionPendingDTO.setMaterials(materials);
                adminSessionPendingDTO.setRepeats(repeats);
                adminSessionPendingDTO.setId(id);
                adminSessionPendingDTO.setSessionType(sessionType);

                adminSessionTableDTO.getPendingRequests().add(adminSessionPendingDTO);

            } else if (table == 3) {
                AdminSessionCompletedDTO adminSessionCompletedDTO = new AdminSessionCompletedDTO();
                adminSessionCompletedDTO.setTopic(topic);
                adminSessionCompletedDTO.setSections(sections);
                adminSessionCompletedDTO.setInstructors(instructors);
                adminSessionCompletedDTO.setType(type);
                adminSessionCompletedDTO.setDates(dates);
                adminSessionCompletedDTO.setMaterials(materials);
                adminSessionCompletedDTO.setRepeats(repeats);
                adminSessionCompletedDTO.setId(id);
                adminSessionCompletedDTO.setSessionType(sessionType);

                adminSessionTableDTO.getCompletedRequests().add(adminSessionCompletedDTO);
            }
        }
        return adminSessionTableDTO;
    }

    /**
     * This function denies a new session request and updates its status.
     */
    public void denyRequestSession(UUID id, String type) {
        Request r = requestRepository.findById(id).get();
        r.setStatus(Request.RequestStatus.DENIED);
        requestRepository.save(r);
        log.info("Deny request!!!: " + r.getId().toString());
    }

    /**
     * This function approves a new session request and creates a new session for that request.
     */
    public UUID approveRequestSession(UUID id, String type) {
        Request r = requestRepository.findById(id).get();

        if (type.equals("0")) {
            r.setStatus(Request.RequestStatus.PENDING);
            ScheduledSession ss = new ScheduledSession();

            ss.setRequest(r);
            ss.setTopic(r.getTopic() != null ? r.getTopic() : "");
            ss.setType(r.getType());
            ss.setStudentInstructions(r.getStudentInstructions() != null ? r.getStudentInstructions() : "");

            ss.setFiles(new ArrayList<>());
            for (File f : r.getFiles()) {
                ss.getFiles().add(f);
            }

            ss.setSections(new ArrayList<>());
            for (Section sec : r.getSections()) {
                ss.getSections().add(sec);
            }


            ss.setTimeslots(new ArrayList<>());

            ss.setCreatedOn(new Date());
            ss.setRepeats(1);
            ss.setDefaultCapacity(10);
            ss.setDefaultDuration("+P00Y00M00DT01H15M00S");
            ss.setGraded(false);
            ss.setNumericGrade(false);
            ss.setLastModifiedOn(new Date());

            ss.setMentorInstructions("");
            ss.setDescription("");

            scheduledSessionRepository.save(ss);

            r.setSession(ss);
            requestRepository.save(r);

            log.info("Approved request: " + r.toString());
            return ss.getId();

        } else {
            r.setStatus(Request.RequestStatus.COMPLETED);
            Quiz q = new Quiz();

            q.setRequest(r);
            q.setTopic(r.getTopic() != null ? r.getTopic() : "");
            q.setType(r.getType());
            q.setStudentInstructions(r.getStudentInstructions() != null ? r.getStudentInstructions() : "");

            QuizTimeSlot qts = new QuizTimeSlot();
            qts.setStartTime(r.getStartDate());
            qts.setEndTime(r.getEndDate());
            q.setTimeSlot(qts);

            q.setFiles(new ArrayList<>());
            for (File f : r.getFiles()) {
                q.getFiles().add(f);
            }

            q.setSections(new ArrayList<>());
            for (Section sec : r.getSections()) {
                q.getSections().add(sec);
            }

            q.setCreatedOn(new Date());
            q.setGraded(false);
            q.setNumericGrade(false);
            q.setLastModifiedOn(new Date());

            q.setMentorInstructions("");
            q.setDescription("");

            quizTimeSlotRepository.save(qts);
            quizRepository.save(q);

            r.setSession(q);
            requestRepository.save(r);

            log.info("Approved request: " + r.toString());
            return q.getId();
        }


    }

    /**
     * This function saves a file for a session by its ID.
     */
    public void uploadFileToSession(File f, String sessionId, String sessionType) {
        if (sessionType.equals("0")) {
            Optional<ScheduledSession> scheduledSession = scheduledSessionRepository.findById(UUID.fromString(sessionId));
            if (scheduledSession.isPresent()) {
                scheduledSession.get().getFiles().add(f);
                scheduledSessionRepository.save(scheduledSession.get());
                log.info("Uploaded a file to Schedule Session");
                System.out.print("Upload file to session"+ scheduledSession);
            }
        } else {
            Optional<Quiz> quiz = quizRepository.findById(UUID.fromString(sessionId));
            if (quiz.isPresent()) {
                quiz.get().getFiles().add(f);
                quizRepository.save(quiz.get());
                log.info("Uploaded a file to Quiz");
            }
        }
    }

    /**
     * This function saves an update for a session.
     */
    public void updateSession(AdminSessionUpdatedDTO adminSessionUpdatedDTO, Map<String,String> allRequestParams) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        List<Section> sectionList = sectionRepository.findAll();
        if (adminSessionUpdatedDTO.getSessionType().equals("0")) {
            Optional<ScheduledSession> scheduledSession = scheduledSessionRepository.findById(UUID.fromString(adminSessionUpdatedDTO.getSessionId()));
            if (scheduledSession.isPresent()) {
                scheduledSession.get().setTopic(adminSessionUpdatedDTO.getTopic());

                List<SessionType> sst = sessionTypeRepository.findAll();
                for (SessionType t : sst){
                    if (t.getName().equals(adminSessionUpdatedDTO.getType())) {
                        scheduledSession.get().setType(t);
                        break;
                    }
                }
                scheduledSession.get().setDescription(adminSessionUpdatedDTO.getDescription());
                scheduledSession.get().setStudentInstructions(adminSessionUpdatedDTO.getStudentInstruction());
                scheduledSession.get().setMentorInstructions(adminSessionUpdatedDTO.getMentorInstruction());
                scheduledSession.get().setGraded(adminSessionUpdatedDTO.isGraded());
                scheduledSession.get().setNumericGrade(adminSessionUpdatedDTO.isNumericgrade());

                List<File> files = scheduledSession.get().getFiles();
                List<File> updatedFiles = new ArrayList<>();
                for (File file : files) {
                    if (allRequestParams.containsKey(file.getName()))
                        updatedFiles.add(file);
                }
                scheduledSession.get().setFiles(updatedFiles);
                scheduledSession.get().setColor(adminSessionUpdatedDTO.getColor());
                scheduledSession.get().setRepeats(adminSessionUpdatedDTO.getRepeats());

                List<Room> rooms = roomRepository.findAll();
                for (Room rm : rooms) {
                    if (rm.toString().equals(adminSessionUpdatedDTO.getRoom())) {
                        scheduledSession.get().setDefaultLocation(rm);
                        break;
                    }
                }
                scheduledSession.get().setDefaultCapacity(adminSessionUpdatedDTO.getCapacity());
                String duration = TimeUtils.convertHourMinuteToStringFormat(adminSessionUpdatedDTO.getDurationH(), adminSessionUpdatedDTO.getDurationM());
                scheduledSession.get().setDefaultDuration(duration);

                List<Section> sections = new ArrayList<>();
                Set<String> selectedSections = new HashSet<>(Arrays.asList(adminSessionUpdatedDTO.getSections()));
                for (Section sec : sectionList) {
                    if (selectedSections.contains(sec.toString()))
                        sections.add(sec);
                }
                scheduledSession.get().setSections(sections);

                scheduledSessionRepository.save(scheduledSession.get());
            }

        } else {
            Optional<Quiz> quiz = quizRepository.findById(UUID.fromString(adminSessionUpdatedDTO.getSessionId()));
            if (quiz.isPresent()) {
                quiz.get().setTopic(adminSessionUpdatedDTO.getTopic());

                List<SessionType> sst = sessionTypeRepository.findAll();
                for (SessionType t : sst){
                    if (t.getName().equals(adminSessionUpdatedDTO.getType())) {
                        quiz.get().setType(t);
                        break;
                    }
                }
                quiz.get().setDescription(adminSessionUpdatedDTO.getDescription());
                quiz.get().setStudentInstructions(adminSessionUpdatedDTO.getStudentInstruction());
                quiz.get().setMentorInstructions(adminSessionUpdatedDTO.getMentorInstruction());
                quiz.get().setGraded(adminSessionUpdatedDTO.isGraded());
                quiz.get().setNumericGrade(adminSessionUpdatedDTO.isNumericgrade());
                quiz.get().setColor(adminSessionUpdatedDTO.getColor());

                List<File> files = quiz.get().getFiles();
                List<File> updatedFiles = new ArrayList<>();
                for (File file : files) {
                    if (allRequestParams.containsKey(file.getName()))
                        updatedFiles.add(file);
                }
                quiz.get().setFiles(updatedFiles);
                QuizTimeSlot quizTimeSlot = quiz.get().getTimeSlot();
                if (quizTimeSlot == null) {
                    quizTimeSlot = new QuizTimeSlot();
                    quizTimeSlot.setStartTime(new Date());
                    quizTimeSlot.setEndTime(new Date());
                    quizTimeSlot.setQuiz(quiz.get());
                    quiz.get().setTimeSlot(quizTimeSlot);
                }

                try {
                    Date startDate = formatter.parse(adminSessionUpdatedDTO.getStartDate());
                    Date endDate = formatter.parse(adminSessionUpdatedDTO.getEndDate());
                    quiz.get().getTimeSlot().setStartTime(startDate);
                    quiz.get().getTimeSlot().setEndTime(endDate);
                } catch (Exception e) {
                    log.info(e.getMessage());
                }

                List<Room> rooms = roomRepository.findAll();
                for (Room rm : rooms) {
                    if (rm.toString().equals(adminSessionUpdatedDTO.getRoom())) {
                        quiz.get().getTimeSlot().setLocation(rm);
                        break;
                    }
                }

                List<Section> sections = new ArrayList<>();
                Set<String> selectedSections = new HashSet<>(Arrays.asList(adminSessionUpdatedDTO.getSections()));
                for (Section sec : sectionList) {
                    if (selectedSections.contains(sec.toString()))
                        sections.add(sec);
                }
                quiz.get().setSections(sections);
                quizRepository.save(quiz.get());
                quizTimeSlotRepository.save(quizTimeSlot);
            }
        }
    }

    /**
     * This function returns an information for creation or edition of a session request.
     */
    public AdminSessionNewRequestFormDTO getSessionNewRequestDTO(UUID sessionID, String type) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        AdminSessionNewRequestFormDTO res = new AdminSessionNewRequestFormDTO();
        res.setId(sessionID.toString());
        if (type.equals("0")) {
            res.setSessionType(0);
            Optional<ScheduledSession> scheduledSession = scheduledSessionRepository.findById(sessionID);
            if (scheduledSession.isPresent()) {
                res.setTopic(scheduledSession.get().getTopic());
                res.setType(scheduledSession.get().getType().getName());

                List<SessionType> sst = sessionTypeRepository.findAll();
                List<String> types = new ArrayList<>();
                for (SessionType sessionType : sst) {
                    types.add(sessionType.getName());
                }
                res.setTypes(types);
                res.setStudentInstructions(scheduledSession.get().getStudentInstructions());
             //   res.setMentorInstructions(scheduledSession.get().getMentorInstructions());
                res.setStartDate(formatter.format(scheduledSession.get().getStartDate()));
                res.setEndDate(formatter.format(scheduledSession.get().getEndDate()));

                List<File> files = scheduledSession.get().getFiles();
                List<String> fs = new ArrayList<>();
                for (File f : files) {
                    fs.add(f.getName());
                }
                res.setUploadedFiles(fs);

                res.setSections(scheduledSession.get().getSectionsAsString());
            }
        } else {
            res.setSessionType(1);
            Optional<Quiz> quiz = quizRepository.findById(sessionID);
            if (quiz.isPresent()) {
                res.setTopic(quiz.get().getTopic());
                res.setType(quiz.get().getType().getName());

                List<SessionType> sst = sessionTypeRepository.findAll();
                List<String> types = new ArrayList<>();
                for (SessionType sessionType : sst) {
                    types.add(sessionType.getName());
                }
                res.setTypes(types);
                res.setStudentInstructions(quiz.get().getStudentInstructions());
             //   res.setMentorInstructions(quiz.get().getMentorInstructions());
                res.setStartDate(formatter.format(quiz.get().getTimeSlot().getStartTime()));
                res.setEndDate(formatter.format(quiz.get().getTimeSlot().getEndTime()));

                List<File> files = quiz.get().getFiles();
                List<String> fs = new ArrayList<>();
                for (File f : files) {
                    fs.add(f.getName());
                }
                res.setUploadedFiles(fs);

                res.setSections(quiz.get().getSectionsAsString());
            }
        }
        return res;
    }

    /**
     * This function fetches information for a session by its ID.
     */
    public AdminSessionFormDTO getSessionForm(UUID sessionID, String type) {
        List<Section> sectionList = sectionRepository.findAll();
        List<String> allSections = new ArrayList<>();
        for (Section section : sectionList) {
            allSections.add(section.toString());
        }
        Collections.sort(allSections);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        AdminSessionFormDTO form = new AdminSessionFormDTO();
        form.setId(sessionID.toString());
        form.setAllSection(allSections);
        if (type.equals("0")) {
            Optional<ScheduledSession> scheduledSession = scheduledSessionRepository.findById(sessionID);

            form.setSessionType(0);
            AdminScheduledSessionFormDTO result = new AdminScheduledSessionFormDTO();

            if (scheduledSession.isPresent()) {
                Request r = scheduledSession.get().getRequest();
                result.setTopic(scheduledSession.get().getTopic());
                result.setType(scheduledSession.get().getType().getName());
                result.setDescription(scheduledSession.get().getDescription());
                result.setStudentInstructions(scheduledSession.get().getStudentInstructions());
                result.setMentorInstructions(scheduledSession.get().getMentorInstructions());
                result.setColor(scheduledSession.get().getColor());
                result.setGraded(scheduledSession.get().isGraded());
                result.setNumericGrade(scheduledSession.get().isNumericGrade());
                result.setRepeats(scheduledSession.get().getRepeats());
                result.setCapacity(scheduledSession.get().getDefaultCapacity());
                result.setLocation(scheduledSession.get().getDefaultLocation() != null ? scheduledSession.get().getDefaultLocation().toString() : "");
                List<SessionType> sst = sessionTypeRepository.findAll();
                List<String> types = new ArrayList<>();
                for (SessionType sessionType : sst) {
                    types.add(sessionType.getName());
                }
                result.setTypes(types);

                if (scheduledSession.get().getSections().size() > 0)
                    form.setSectionSelected(scheduledSession.get().getSectionSet());
                else
                    form.setSectionSelected(r.getSectionSet());

                List<Room> rooms = roomRepository.findAll();
                List<String> rms = new ArrayList<>();
                for (Room rm : rooms) {
                    rms.add(rm.toString());
                }
                result.setRooms(rms);

                List<File> files = scheduledSession.get().getFiles();
                if (files == null || files.size() == 0) {
                    files = r.getFiles();
                }
                List<String> fs = new ArrayList<>();
                String sessionId = String.valueOf(scheduledSession.get().getId());
                for (File f : files) {
                    fs.add(f.getName()+",/admin/session/request/view/"+sessionId+"/download/"+f.getId());
                }
                result.setUploadedFiles(fs);

                int[] time = TimeUtils.getHourMinuteFromString(scheduledSession.get().getDefaultDuration());
                result.setDurationH(time[0]);
                result.setDurationM(time[1]);

            }
            form.setScheduledSession(result);
        } else {
            Optional<Quiz> quiz = quizRepository.findById(sessionID);
            form.setSessionType(1);
            AdminQuizFormDTO result = new AdminQuizFormDTO();

            if (quiz.isPresent()) {
                Request r = quiz.get().getRequest();
                result.setTopic(quiz.get().getTopic());
                result.setType(quiz.get().getType().getName());
                result.setDescription(quiz.get().getDescription());
                result.setStudentInstructions(quiz.get().getStudentInstructions());
                result.setMentorInstructions(quiz.get().getMentorInstructions());
                result.setRoom(quiz.get().getTimeSlot().getLocation() != null ? quiz.get().getTimeSlot().getLocation().toString() : "");
                result.setColor(quiz.get().getColor());
                result.setGraded(quiz.get().isGraded());
                result.setNumericGrade(quiz.get().isNumericGrade());
                result.setStartDate(formatter.format(quiz.get().getTimeSlot().getStartTime()));
                result.setEndDate(formatter.format(quiz.get().getTimeSlot().getEndTime()));

                List<SessionType> sst = sessionTypeRepository.findAll();
                List<String> types = new ArrayList<>();
                for (SessionType sessionType : sst) {
                    types.add(sessionType.getName());
                }
                result.setTypes(types);
                if (quiz.get().getSections().size() > 0)
                    form.setSectionSelected(quiz.get().getSectionSet());
                else
                    form.setSectionSelected(r.getSectionSet());

                List<Room> rooms = roomRepository.findAll();
                List<String> rms = new ArrayList<>();
                for (Room rm : rooms) {
                    rms.add(rm.toString());
                }
                result.setRooms(rms);

                List<File> files = quiz.get().getFiles();
                if (files == null || files.size() == 0) {
                    files = r.getFiles();
                }
                List<String> fs = new ArrayList<>();
                for (File f : files) {
                    fs.add(f.getName());
                }
                result.setUploadedFiles(fs);
            }

            form.setQuiz(result);
        }
        return form;
    }

    /**
     * This function gets details for scheduled session or quiz.
     */
    public ModelMap getDetailsForScheduledSessionOrQuiz(ModelMap model, UUID id, String ssType) {
        if(ssType.equals("1")){
            Optional<Quiz> q = quizRepository.findById(id);
            if (q.isPresent()) {
                AdminGradesSessionResultDTO adminGradesSessionResultDTO = adminGradesHelper.setSessionDetailsQuiz(q);
                model.addAttribute("session",adminGradesSessionResultDTO);
                List<QuizAttendance> quizAttendances = quizAttendanceRepository.getQuizAttendanceForGrades(id);
                List<AdminGradesQuizEditDTO> adminGradesQuizEditDTO = adminGradesHelper.setQuizAttendance(quizAttendances, q);
                model.addAttribute("quiz", adminGradesQuizEditDTO);
                model.addAttribute("sessionType", "1");
            }
        }
        else{
            Optional<ScheduledSession> ss = scheduledSessionRepository.findById(id);
            if (ss.isPresent()) {
                AdminGradesSessionResultDTO adminGradesSessionResultDTO = adminGradesHelper.setSessionDetailsSS(ss);
                model.addAttribute("session",adminGradesSessionResultDTO);
                List<SessionTimeSlot> sessionTimeSlotList = ss.get().getTimeslots();//sessionTimeSlotRepository.findSessionTimeSlotBySessionId(id);
                List<AdminGradesScheduledSessionEditDTO> adminGradesScheduledSessionEditDTO = adminGradesHelper.setSessionTimeSlotDetails(sessionTimeSlotList, ss);
                model.addAttribute("scheduledsession", adminGradesScheduledSessionEditDTO);
                model.addAttribute("sessionType", "0");
            }
        }
        return model;
    }


    /**
     * This function will update the grades of the student
     */
    public void updateGrade(int grade, UUID attendance) {
        Optional<SessionAttendance> sattendance = sessionAttendanceRepository.findById(attendance);

        if(sattendance.isPresent()){
            SessionAttendance sessionAttendance = sattendance.get();
            if(grade>=0){
                sessionAttendance.setGrade(grade);
            }
            else if(grade==-1){
                sessionAttendance.setGrade(1);
            }
            else if(grade==-2){
                sessionAttendance.setGrade(0);
            }
            sessionAttendanceRepository.save(sessionAttendance);
        }
    }

    /**
     * This function will get the required details to mark student attendance.
     */
    public AdminAttendanceGradesResultDTO getDetailsForAttendance(UUID id, String ssType) {
        if(ssType.equals("1")){
//            Quiz attendance = quizRepository.getAttendanceGrade(id);
            Optional<Quiz> q = quizRepository.findById(id);
            if (q.isPresent()) {
                Quiz attendance = q.get();
                AdminAttendanceGradesResultDTO attendanceGrade = adminGradesHelper.setQuizAttendanceGrades(attendance);
                return attendanceGrade;
            } else return  null;
        }
        else {
            Optional<ScheduledSession> ss = scheduledSessionRepository.findById(id);
            if (ss.isPresent()) {
                ScheduledSession attendance = ss.get();
                AdminAttendanceGradesResultDTO attendanceGrade = adminGradesHelper.setScheduledSessionAttendanceGrades(attendance);
                return attendanceGrade;
            } else return null;
        }
    }

    /**
     * This function will mark student attendance for a Scheduled Session or Quiz.
     */
    public AdminAttendanceGradesResultDTO markStudentAttendance(UUID sessionId, String timeSlot, String netId, String grade, String ssType) {
        AdminAttendanceGradesResultDTO markStudentAttendance = new AdminAttendanceGradesResultDTO();

        if(ssType.equals("1")) {
            markStudentAttendance.setSessionType("quiz");

            QuizAttendance quizAttendance= new QuizAttendance();

            //Getting the Quiz Object by sessionId as Quiz extends Session
            Optional<Quiz> quiz = quizRepository.findById(sessionId);

            if(!quizTimeSlotRepository.findById(UUID.fromString(timeSlot)).get().hasAttended(netId)) {
                Optional<User> user = userRepository.findByUsername(netId);
                if(user.isPresent()) {
                    quizAttendance.setUser(user.get());
                    quizAttendance.setQuiz(quiz.get());
                    quizAttendance.getQuiz().setTimeSlot(quiz.get().getTimeSlot());
                    quizAttendance.setGrade(Integer.parseInt(grade));
                    quizAttendanceRepository.save(quizAttendance);
                }
            }else{
                log.info("Student has already attended the quiz");
            }
        }
        /* If session is not a quiz */
        else {
            /* Add record to ScheduledSessionAttendance */
            Optional<ScheduledSession> ss = scheduledSessionRepository.findById(sessionId);
            ScheduledSessionAttendance scheduledSessionAttendance = new ScheduledSessionAttendance();
            Optional<User> user = userRepository.findByUsername(netId);
            if(!ss.get().isGraded()) { //if session is not graded then student can attend that session for multiple timeslot.
                if (!sessionTimeSlotRepository.findById(UUID.fromString(timeSlot)).get().hasAttended(netId)) {
                    saveToScheduleSessionAttendance(timeSlot, scheduledSessionAttendance, user);
                }
                else {
                    log.info("Student has already attended the session for this time slot");
                }
            }
            else { //if session is graded then student can only attend that session once
                Optional<ScheduledSession> scheduledSession = scheduledSessionRepository.findById(sessionId);
                if(!scheduledSession.get().hasAttended(netId)) {
                    saveToScheduleSessionAttendance(timeSlot, scheduledSessionAttendance, user);
                }
                else {
                    log.info("Student cannot attend the graded session twice");
                }
            }
        }
        return markStudentAttendance;
    }

    private void saveToScheduleSessionAttendance(String timeSlot, ScheduledSessionAttendance scheduledSessionAttendance, Optional<User> user) {
        if (user.isPresent()) {
            scheduledSessionAttendance.setUser(user.get());
            Optional<TimeSlot> ts = timeSlotRepository.findById(UUID.fromString(timeSlot));
            scheduledSessionAttendance.setTimeSlot(ts.get());//SessionTimeSlot Type
            scheduledSessionAttendance.setTimeIn(new Date());//Condition that student did not bring card then set time to current time
            scheduledSessionAttendanceRepository.save(scheduledSessionAttendance);
        }
    }

    /**
     * This function will get all the details for the given Session Id.
     */
    public Optional<Session> checkSessionType(UUID id){
        Optional<Session> session = sessionRepository.findById(id);
        return session;
    }
}
