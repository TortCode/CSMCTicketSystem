package edu.utdallas.csmc.web.controller;

import edu.utdallas.csmc.web.dto.*;
import edu.utdallas.csmc.web.helper.InstructorParseSectionHelper;
import edu.utdallas.csmc.web.helper.MultipartFileToFileObjectHelper;
import edu.utdallas.csmc.web.helper.ReportExportHelper;
import edu.utdallas.csmc.web.model.course.Section;
import edu.utdallas.csmc.web.model.session.*;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.service.*;
import edu.utdallas.csmc.web.util.FileUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@EnableAutoConfiguration
@CrossOrigin
@Log4j2
@RequestMapping("/instructor")
public class InstructorController {

    @Autowired
    InstructorCourseService instructorCourseService;

    @Autowired
    InstructorSessionScheduleService instructorSessionScheduleService;

    @Autowired
    InstructorAttendanceAndGradeService instructorAttendanceAndGradeService;

    @Autowired
    InstructorRequestSessionService requestSessionService;

    @Autowired
    DefaultUsernameService defaultUsernameService;

    @Autowired
    FileService fileService;

    @Autowired
    AdminReportService adminReportService;


    @RequestMapping(path = "/course/section")
    public String getCurrentSection(ModelMap model) {
        User instructor = defaultUsernameService.getUser();
        List<Section> instructorSections = instructorCourseService.getInstructorSections(instructor);
        List<InstructorCourseDTO> sections = new ArrayList<>();
        for(Section s : instructorSections){
            InstructorCourseDTO sec = new InstructorCourseDTO();
            sec.setId(s.getId());
            sec.setCourse(s.getCourse());
            sec.setNumber(s.getNumber());
            sec.setRosterLength(s.getStudents().size());
            sections.add(sec);
        }
        model.addAttribute("sections", sections);
        log.info("Data Passed to front end: " + sections);
        return "role/instructor/course/section.html";
    }

    @RequestMapping(path = "/course/section/view/{id}", method = RequestMethod.POST)
    public String uploadSectionRoster(ModelMap model, @PathVariable("id") String id, @RequestParam("file") MultipartFile file) throws IOException {

        try{
            InputStream inStream = file.getInputStream();
            byte[] buffer = new byte[(int) file.getSize()];
            inStream.read(buffer);
            java.io.File receivedFile = new java.io.File("./src/main/resources/uploads/temp/" + file.getOriginalFilename());
            OutputStream outStream = new FileOutputStream(receivedFile);
            outStream.write(buffer);

            Section section = instructorCourseService.getSectionById(id).get();
            List<String[]> parseList = InstructorParseSectionHelper.parseRoster(receivedFile);
            List<User> parsedStudents = new ArrayList<>();
            for(String[] x : parseList){
                parsedStudents.add(instructorCourseService.findUserByNetId(x));
            }
            List<User> currentList = section.getStudents();
            List<User> addUserList = new ArrayList<>();
            int addedUser = 0;

            for(User u : parsedStudents){
                if(!currentList.contains(u)) {
                    addUserList.add(u);
                    addedUser++;
                }
            }

            addUserList.addAll(currentList);
            instructorCourseService.enrollUserToSection(section, addUserList);
            receivedFile.delete();
            model.addAttribute("records", addedUser);

        } catch (IOException exp) {
            log.error("Unable to get Files");
            exp.printStackTrace();
            model.addAttribute("records", "-1");
        }

        return viewSectionRoster(model, id);
    }

    @RequestMapping(path = "/course/section/view/{id}", method = RequestMethod.GET)
    public String viewSectionRoster(ModelMap model, @PathVariable("id") String id) {
        List<User> students = instructorCourseService.getRoster(UUID.fromString(id));
        Section section = instructorCourseService.getSectionById(id).get();
        InstructorRosterViewDTO data = new InstructorRosterViewDTO();
        data.setRoster(students);
        data.setSection(section);
        model.addAttribute("data", data);
        if(!model.containsAttribute("records")){
            model.addAttribute("records", "-2");
        }
        log.info("Query from the database" + students);
        return "role/instructor/course/section_roster.html";
    }

    @RequestMapping(path = "/session/schedule")
    public String viewSessionSchedule(ModelMap model){
        try{
            String instructorNetID = defaultUsernameService.getUsername();
            List<InstructorSessionScheduleDTO> schedule = instructorSessionScheduleService.getSessionSchedule(instructorNetID);
            model.addAttribute("courses", schedule);
            log.info("Query from the database" + schedule);
            return "role/instructor/session/schedule_by_course.html";
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping(path = "/session/schedule/session/view/{id}")
    public String viewSessionDetails(ModelMap model, @PathVariable("id") String id){
        try{
            ScheduledSession requestedSessionInfo = instructorSessionScheduleService.getSession(UUID.fromString(id)).get();
            model.addAttribute("session", requestedSessionInfo);
            return "role/instructor/session/session.html";
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping(path = "/session/schedule/session/view/{sessionId}/download/{fileId}")
    public void downloadSessionFiles(HttpServletResponse response, @PathVariable("fileId") String fileId, @PathVariable("sessionId") String sessionId) {
        edu.utdallas.csmc.web.model.file.File fileObject = fileService.getFileById(UUID.fromString(fileId)).get();
        log.info(fileObject);
        String path = fileObject.getHash().getPath();
        log.info("FileName: " + path);
        String directoryLevelOne = path.substring(0,2);
        String directoryLevelTwo = path.substring(2,4);
        String fileName = fileObject.getName();
        log.info(fileName);
        java.io.File file = new java.io.File("./src/main/resources/uploads/" + directoryLevelOne + "/" + directoryLevelTwo + "/" + path);
        String mimeType = fileService.getFileMetaDataByKey(fileObject, "mime");
        String extension = fileService.getFileMetaDataByKey(fileObject, "extension");
        log.info(mimeType);
        if (file.exists())
        {
            try
            {
                response.setContentType(mimeType);
                response.addHeader("Content-Disposition", "attachment; filename="+ fileName + "." +extension);
                response.setContentLength((int) file.length());
                InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                FileCopyUtils.copy(inputStream, response.getOutputStream());
                response.getOutputStream().flush();
                response.addHeader("Refresh", "1; url = /instructor/session/schedule/session/view/" + sessionId);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            // Redirect to an error page
        }
    }

    @RequestMapping(path = "/session/request")
    public String requestSession(ModelMap model){
        User instructor = defaultUsernameService.getUser();
        List<Request> sessionRequests = requestSessionService.findSessionsByUser(instructor);
        model.addAttribute("requests", sessionRequests);
        return "role/instructor/session/request/requests.html";
    }

    @RequestMapping(path = "/session/request/create", method = RequestMethod.GET)
    public String createRequestFormForSession(ModelMap model){
        String netId = defaultUsernameService.getUsername();
        List<Section> sections = instructorCourseService.getInstructorCurrentSections(netId);
        model.addAttribute("sections", sections);
        return "role/instructor/session/request/form_create_session.html";
    }


    @RequestMapping(path = "/session/request/create", method = RequestMethod.POST)
    public String createRequestForSession(@ModelAttribute("request") InstructorRequestSessionDTO request){
        SessionType sessionType = requestSessionService.getSessionTypeByName(request.getType());
        requestSessionService.addSessionRequest(request, defaultUsernameService.getUser(), sessionType);
        return "redirect:/instructor/session/request";
    }

    @RequestMapping(path = "/session/request/view/{id}")
    public String viewSession(ModelMap model, @PathVariable("id") String sessionId){
        Request viewSession = requestSessionService.findByRequestId(UUID.fromString(sessionId));
        model.addAttribute("request", viewSession);
        return "role/instructor/session/request/request.html";
    }

    @RequestMapping(path = "/session/request/view/{requestId}/download/{fileId}")
    public void downloadSessionRequestFiles(HttpServletResponse response, @PathVariable("fileId") String fileId, @PathVariable("requestId") String requestId) {
        edu.utdallas.csmc.web.model.file.File fileObject = fileService.getFileById(UUID.fromString(fileId)).get();
        log.info(fileObject);
        String path = fileObject.getHash().getPath();
        log.info("FileName: " + path);
        String directoryLevelOne = path.substring(0,2);
        String directoryLevelTwo = path.substring(2,4);
        String fileName = fileObject.getName();
        log.info(fileName);
        java.io.File file = new java.io.File("./src/main/resources/uploads/" + directoryLevelOne + "/" + directoryLevelTwo + "/" + path);
        String mimeType = fileService.getFileMetaDataByKey(fileObject, "mime");
        String extension = fileService.getFileMetaDataByKey(fileObject, "extension");
        log.info(mimeType);
        if (file.exists())
        {
            try
            {
                response.setContentType(mimeType);
                response.addHeader("Content-Disposition", "attachment; filename="+ fileName + "." +extension);
                response.setContentLength((int) file.length());
                InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                FileCopyUtils.copy(inputStream, response.getOutputStream());
                response.getOutputStream().flush();
                response.addHeader("Refresh", "1; url = /instructor/session/schedule/session/view/" + requestId);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            // Redirect to an error page
        }
    }

    @RequestMapping(path = "/session/request/edit/{id}", method = RequestMethod.GET)
    public String editSessionRequest(ModelMap model, @PathVariable("id") String requestId){
        Request request = requestSessionService.findByRequestId(UUID.fromString(requestId));
        model.addAttribute("request", request);
       // model.addAttribute("mentorInstructions", request.getSession().getMentorInstructions());
        String netId = defaultUsernameService.getUsername();
        List<Section> sections = instructorCourseService.getInstructorCurrentSections(netId);
        model.addAttribute("sections", sections);
        return "role/instructor/session/request/form_edit_session.html";
    }

    @RequestMapping(path = "/session/request/edit/{id}", method = RequestMethod.POST)
    public String updateRequestForSession(@ModelAttribute("request") InstructorRequestSessionDTO requestData, @PathVariable("id") String requestId){
        try {
            User user = defaultUsernameService.getUser();
            Request request = requestSessionService.findByRequestId(UUID.fromString(requestId));
            request.setTopic(requestData.getTopic());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            request.setStartDate(dateFormat.parse(requestData.getStartDate()));
            request.setEndDate(dateFormat.parse(requestData.getEndDate()));
            request.setStudentInstructions(requestData.getStudentInstructions());
          //  request.getSession().setMentorInstructions(requestData.getMentorInstructions());

            SessionType sessionType = requestSessionService.findSessionTypeById(UUID.fromString(requestData.getType()));
            request.setType(sessionType);
            List<Section> sections = requestSessionService.getSectionsFromIdList(requestData.getSections());
            request.setSections(sections);
            List<MultipartFile> filesList = requestData.getFiles();

            if(filesList != null && !filesList.get(0).getOriginalFilename().equals("")){
                List<edu.utdallas.csmc.web.model.file.File> filesReferences = new ArrayList<>();
                MultipartFileToFileObjectHelper converter = new MultipartFileToFileObjectHelper();

                for(int i = 0; i < filesList.size(); i++){
                    filesReferences.add(converter.getFileObject(filesList.get(i), user));
                    log.info(filesReferences.get(i));
                }

                for(edu.utdallas.csmc.web.model.file.File x : filesReferences){
                    fileService.saveFile(x);
                }

                request.setFiles(filesReferences);
            }

            requestSessionService.updateSessionRequest(request);

        } catch (Exception e){
            e.printStackTrace();
        }
        return "redirect:/instructor/session/request";
    }

    @RequestMapping(path = "/session/history")
    public String viewAttendanceAndGradeInfo (ModelMap modelMap) {
        String instructorNetID = defaultUsernameService.getUsername();
        UserDTO userDTO = new UserDTO();
        userDTO.setNetId(instructorNetID);
        List<ScheduledSession> instructorScheduleSession = instructorAttendanceAndGradeService.getAllGradedSessionByInstructor(userDTO);
        List<Quiz> instructorQuiz = instructorAttendanceAndGradeService.getAllGradedQuizByInstructor(userDTO);
        List<Section> sections = instructorCourseService.getInstructorSectionsWithNetID(instructorNetID);
        modelMap.addAttribute("sessions", instructorScheduleSession);
        modelMap.addAttribute("quizzes", instructorQuiz);
        modelMap.addAttribute("sections", sections);
        return "role/instructor/session/history_by_time.html";
    }

    @RequestMapping(path = "/session/grades/walkIn/{sectionId}")
    public String viewWalkInReportForSection (ModelMap modelMap, @PathVariable String sectionId) {
        UUID sectionUUID = UUID.fromString(sectionId);
        Section section = adminReportService.getSectionWithSectionId(sectionUUID);
        List<? extends Attendance> walkInReport = adminReportService.getWalkInAttendanceBySection(section);
        modelMap.addAttribute("section", section);
        modelMap.addAttribute("report", walkInReport);
        return "role/instructor/session/grades/walkInReport.html";
    }

    @RequestMapping(path = "/session/grades/session/{scheduleSessionId}")
    public String viewAttendanceAndGradeForSession (ModelMap modelMap, @PathVariable String scheduleSessionId) {
        UUID sessionUUID = UUID.fromString(scheduleSessionId);
        ScheduledSession scheduledSession = instructorAttendanceAndGradeService.getSessionInfoById(sessionUUID);
        List<SessionAttendance> sessionAttendance = instructorAttendanceAndGradeService.getAllAttendanceBySession(scheduledSession);
        modelMap.addAttribute("session", scheduledSession);
        modelMap.addAttribute("attendees", sessionAttendance);
        return "role/instructor/session/grades/grades.html";
    }

    @RequestMapping(path = "/session/grades/quiz/{quizId}")
    public String viewAttendanceAndGradeForQuiz (ModelMap modelMap, @PathVariable String quizId) {
        UUID quizUUID = UUID.fromString(quizId);
        Quiz quizSession = instructorAttendanceAndGradeService.getQuizInfoById(quizUUID);
        List<QuizAttendance> quizAttendance = instructorAttendanceAndGradeService.getQuizAttendanceByQuiz(quizSession);
        modelMap.addAttribute("session", quizSession);
        modelMap.addAttribute("attendees", quizAttendance);
        return "role/instructor/session/grades/grades.html";
    }

    @RequestMapping(path = "/session/grades/walkIn/download/{sectionId}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadReportWalkIn(ModelMap model, @PathVariable String sectionId) {
        ReportExportHelper helper = new ReportExportHelper();
        Section sectionObj = adminReportService.getAllSectionsCurrentSemester().stream()
                .filter(section -> section.getId().equals(UUID.fromString(sectionId))).findFirst().orElse(null);
        assert sectionObj != null;
        List<WalkInAttendance> walkInReport = adminReportService.getWalkInAttendanceBySection(sectionObj);
        String content = helper.convertWalkInAttendanceToString(walkInReport);
        FileUtil fileUtil = new FileUtil();
        return fileUtil.writeToCSVFile(content);
    }

    @RequestMapping(path = "/session/grades/session/download/{sessionId}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadAttendanceAndGradeForScheduleSession (@PathVariable String sessionId) {
        UUID sessionUUID = UUID.fromString(sessionId);
        ScheduledSession scheduledSession = instructorAttendanceAndGradeService.getSessionInfoById(sessionUUID);
        List<SessionAttendance> sessionAttendance = instructorAttendanceAndGradeService.getAllAttendanceBySession(scheduledSession);

        ReportExportHelper helper = new ReportExportHelper();
        String content = helper.convertAttendanceToString(sessionAttendance, scheduledSession);
        FileUtil fileUtil = new FileUtil();
        return fileUtil.writeToCSVFile(content);
    }

    @RequestMapping(path = "/session/grades/quiz/download/{quizId}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadAttendanceAndGradeForQuiz (@PathVariable String quizId) {
        UUID quizUUID = UUID.fromString(quizId);
        Quiz quizSession = instructorAttendanceAndGradeService.getQuizInfoById(quizUUID);
        List<QuizAttendance> quizAttendance = instructorAttendanceAndGradeService.getQuizAttendanceByQuiz(quizSession);

        ReportExportHelper helper = new ReportExportHelper();
        String content = helper.convertAttendanceToString(quizAttendance, quizSession);
        FileUtil fileUtil = new FileUtil();
        return fileUtil.writeToCSVFile(content);
    }
}
