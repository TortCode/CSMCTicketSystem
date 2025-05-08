package edu.utdallas.csmc.web.controller.newSite;

import edu.utdallas.csmc.web.dto.*;
import edu.utdallas.csmc.web.helper.ReportExportHelper;
import edu.utdallas.csmc.web.model.course.Section;
import edu.utdallas.csmc.web.model.file.FileHash;
import edu.utdallas.csmc.web.model.misc.Announcement;
import edu.utdallas.csmc.web.model.misc.Room;
import edu.utdallas.csmc.web.model.session.*;
import edu.utdallas.csmc.web.model.user.Role;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.service.*;
import edu.utdallas.csmc.web.util.DateUtil;
import edu.utdallas.csmc.web.util.FileUtil;
import javafx.util.Pair;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.*;
import java.sql.Time;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This Class will have the path for all the APIs for the Admin.
 */
@Controller
@EnableAutoConfiguration
@CrossOrigin
@Log4j2
@RequestMapping("/admin/new")
public class NewAdminController {

    @Autowired
    private AdminHomeService adminHomeService;

    @Autowired
    private AdminAbsencesService adminAbsencesService;

    @Autowired
    private AdminCourseService adminCourseService;

    @Autowired
    private AdminOperationHoursService adminOperationHoursService;

    @Autowired
    private AdminUsersServices adminUsersServices;

    @Autowired
    private AdminDepartmentService adminDepartmentService;

    @Autowired
    private DefaultUsernameService defaultUsernameService;

    @Autowired
    private AdminSessionRequestService adminSessionRequestService;

    @Autowired
    private AdminSwipesService adminSwipesService;

    @Autowired
    private AdminRoomsService adminRoomsService;

    @Autowired
    private AdminReportService adminReportService;

    @Autowired
    private AdminTimesheetService adminTimesheetService;

    @Autowired
    private AdminMentorService adminMentorService;

    @Autowired
    private AdminAnnouncementService adminAnnouncementService;

    @Autowired
    private AdminIpService adminIpService;

    @Autowired
    private AdminScheduleCalendarService adminScheduleCalendarService;

    @Autowired
    private AdminSemesterService adminSemesterService;

    @Autowired
    private AdminSectionService adminSectionService;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private AdminSessionCalendarService adminSessionCalendarService;

    @RequestMapping("/home")
    public String adminHomePage(ModelMap model) {
        model = adminHomeService.getHomeScreenDetails(model, null);
        return "role/admin/home.html";
    }

    @RequestMapping("/course")
    public String getCourseList(ModelMap model) {

        List<AdminCoursesResultDTO> supportedAdminCourseResultDTOList = adminCourseService.getSupportedCourseDetails();
        List<AdminCoursesResultDTO> unsupportedAdminCourseResultDTOList = adminCourseService
                .getUnsupportedCourseDetails();

        model.addAttribute("supported_courses", supportedAdminCourseResultDTOList);
        model.addAttribute("unsupported_courses", unsupportedAdminCourseResultDTOList);

        return "role/admin/course/list.html";
    }

    @RequestMapping("/course/create")
    public String addNewCourse(ModelMap model) {

        List<String> departments = adminCourseService.getDepartmentsForNewCourse();

        model.addAttribute("departments", departments);

        return "role/admin/course/add.html";
    }

    @RequestMapping("/course/create/submit")
    public String submitNewCourse(@RequestParam("department") String departmentName,
            @RequestParam("name") String courseName, @RequestParam("number") String courseNumber,
            @RequestParam(required = false, value = "supported") String supported,
            @RequestParam(required = false, value = "description") String description) {

        AdminCourseDetailsResultDTO adminCourseDetailsResultDTO = new AdminCourseDetailsResultDTO();
        adminCourseDetailsResultDTO.setDepartmentName(departmentName);
        adminCourseDetailsResultDTO.setName(courseName);
        adminCourseDetailsResultDTO.setNumber(courseNumber);
        adminCourseDetailsResultDTO.setSupported(supported);
        adminCourseDetailsResultDTO.setDescription(description);
        adminCourseDetailsResultDTO.setUsername(defaultUsernameService.getUsername());

        adminCourseService.submitCourseDetails(adminCourseDetailsResultDTO);

        return "redirect:/admin/course";
    }

    @RequestMapping("/course/edit/{courseId}")
    public String editCourse(ModelMap model, @PathVariable("courseId") String courseId) {

        AdminCourseDetailsResultDTO adminCourseDetailsResultDTO = new AdminCourseDetailsResultDTO();
        adminCourseDetailsResultDTO.setCourseId(courseId);
        adminCourseService.getCourseDetails(adminCourseDetailsResultDTO);
        List<String> departments = adminCourseService.getDepartmentsForNewCourse();
        // Remove the department of the course from the list of all departments to show
        // separately on the front end.
        departments.remove(adminCourseDetailsResultDTO.getDepartmentName());

        model.addAttribute("course", adminCourseDetailsResultDTO);
        model.addAttribute("departments", departments);

        return "role/admin/course/edit.html";

    }

    @RequestMapping("/course/update/{courseId}")
    public String updateCourse(@PathVariable("courseId") String courseId,
            @RequestParam("department") String departmentName, @RequestParam("name") String courseName,
            @RequestParam("number") String courseNumber,
            @RequestParam(required = false, value = "supported") String supported,
            @RequestParam(required = false, value = "description") String description) {

        AdminCourseDetailsResultDTO adminCourseDetailsResultDTO = new AdminCourseDetailsResultDTO();
        adminCourseDetailsResultDTO.setCourseId(courseId);
        adminCourseDetailsResultDTO.setDepartmentName(departmentName);
        adminCourseDetailsResultDTO.setName(courseName);
        adminCourseDetailsResultDTO.setNumber(courseNumber);
        adminCourseDetailsResultDTO.setSupported(supported);
        adminCourseDetailsResultDTO.setDescription(description);
        adminCourseDetailsResultDTO.setUsername(defaultUsernameService.getUsername());

        adminCourseService.updateCourseDetails(adminCourseDetailsResultDTO);

        return "redirect:/admin/course";

    }

    @RequestMapping("/course/delete/{courseId}")
    public String deleteCourse(@PathVariable("courseId") String courseId) {

        String returnPath = adminCourseService.deleteCourse(UUID.fromString(courseId));

        return returnPath;
    }

    /*********** User_Role ***********/
    /**
     * This API returns all roles in the system
     */
    @RequestMapping("/user/role/")
    public String getRoles(ModelMap model) {
        AdminRoleResultDTO adminRoleResultDTO = adminUsersServices.getRoleResult();
        model.addAttribute("roles", adminRoleResultDTO);
        return "role/admin/role/list.html";
    }

    /**
     * This API submits a new role to the system
     */
    @RequestMapping("/user/role/create/submit/")
    public String createRolesSubmit(ModelMap model, @RequestParam String rolename, @RequestParam String description) {
        String message = adminUsersServices.createRole(model, rolename, description);
        model.addAttribute("message", message);
        return getRoles(model);
    }

    /**
     * This API submits a new role to the system
     */
    @RequestMapping("/user/role/edit/submit/{roleID}/")
    public String editRolesSubmit(ModelMap model, @PathVariable("roleID") String roleID, @RequestParam String rolename,
            @RequestParam String description) {
        String message = adminUsersServices.editRole(model, UUID.fromString(roleID), rolename, description);
        model.addAttribute("message", message);

        return getRoles(model);
    }

    /**
     * This API returns the information of a role for editing
     */
    @RequestMapping("/user/role/edit/{roleID}/")
    public String editRole(ModelMap model, @PathVariable("roleID") String roleID) {
        AdminSingleRoleDTO adminSingleRoleDTO = adminUsersServices.getRoleByID(UUID.fromString(roleID));
        model.addAttribute("role", adminSingleRoleDTO);
        return "role/admin/role/add.html";
    }

    /**
     * This API removes a role by its ID
     */
    @RequestMapping("/user/role/remove/{roleID}/")
    public String removeRole(ModelMap model, @PathVariable("roleID") String roleID) {
        String message = adminUsersServices.removeRole(UUID.fromString(roleID));
        model.addAttribute("message", message);

        return getRoles(model);
    }

    /**
     * This API generates a page for role creation
     */
    @RequestMapping("/user/role/create/")
    public String createRolesForm(ModelMap model) {
        return "role/admin/role/add.html";
    }

    /*********** End of User_Role ***********/

    @RequestMapping("/hours")
    public String getOperationHours(ModelMap model) {
        List<AdminOperationHoursResultDTO> operationHours = adminOperationHoursService.getOperationHours();
        model.addAttribute("operationHours", operationHours);
        return "role/admin/schedule/listOperationHours.html";
    }

    @RequestMapping("/edit/hours/{id}")
    public String editOperationHours(ModelMap model, @PathVariable("id") UUID id) {
        AdminOperationHoursResultDTO operationHours = adminOperationHoursService.editOperationHours(id);
        model.addAttribute("form", operationHours);
        return "role/admin/schedule/editOperationHours.html";
    }

    @RequestMapping("/edit/hours/submit/{hoursId}")
    public String saveOperationHours(ModelMap model, @PathVariable("hoursId") UUID hoursId, @RequestParam String day,
            @RequestParam String startTime, @RequestParam String endTime) {
        AdminOperationHoursResultDTO adminOperationHoursResultDTO = new AdminOperationHoursResultDTO();
        adminOperationHoursResultDTO.setOperationHoursId(hoursId);
        adminOperationHoursResultDTO.setDay(day);
        if (startTime.length() < 6)
            startTime = startTime + ":00";
        if (endTime.length() < 6)
            endTime = endTime + ":00";
        adminOperationHoursResultDTO.setStartTime(Time.valueOf(startTime));
        adminOperationHoursResultDTO.setEndTime(Time.valueOf(endTime));
        adminOperationHoursService.saveOperationHours(adminOperationHoursResultDTO);

        return "redirect:/admin/hours";
    }

    /*********** Session_Request ***********/
    /**
     * This API returns all session requests in the system
     */
    @RequestMapping("/session/request/")
    public String getSessionRequest(ModelMap model) {
        AdminSessionTableDTO adminSessionNewRequestTableDTO = adminSessionRequestService.getSessionTable();
        model.addAttribute("sessions", adminSessionNewRequestTableDTO);
        return "role/admin/session/requests.html";
    }

    /**
     * This API returns a page for editing a new session request by its ID
     */
    @RequestMapping("/session/request/edit/{sessionId}/{sessionType}")
    public String editRequestSession(ModelMap model, @PathVariable("sessionId") String sessionId,
            @PathVariable("sessionType") String sessionType) {
        AdminSessionNewRequestFormDTO adminSessionNewRequestFormDTO = adminSessionRequestService
                .getSessionNewRequestDTO(UUID.fromString(sessionId), sessionType);
        model.addAttribute("form", adminSessionNewRequestFormDTO);
        return "role/admin/session/request_form.html";
    }

    /**
     * This API returns a page for editing an requested session by its ID
     */
    @RequestMapping("/session/edit/{sessionId}/{sessionType}")
    public String editSession(ModelMap model, @PathVariable("sessionId") String sessionId,
            @PathVariable("sessionType") String sessionType) {
        AdminSessionFormDTO adminSessionFormDTO = adminSessionRequestService.getSessionForm(UUID.fromString(sessionId),
                sessionType);
        model.addAttribute("form", adminSessionFormDTO);
        return "role/admin/session/form.html";
    }

    /**
     * This API is for upload a file in a session
     */
    @RequestMapping(value = {
            "/session/edit/uploadFile/{sessionId}/{sessionType}" }, method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String uploadFile(@RequestParam Map<String, String> allRequestParams,
            @PathVariable("sessionId") String sessionId, @PathVariable("sessionType") String sessionType,
            @RequestParam(value = "file") MultipartFile file) {
        if (file != null) {
            try {
                FileHash fh = new FileHash();
                fh.setPath(FileUtil.createSha1(file));
                fh.setSize((int) file.getSize());

                String uploadLocation = "./src/main/resources/uploads/" + fh.getPath();

                InputStream inStream = file.getInputStream();
                byte[] buffer = new byte[(int) file.getSize()];
                inStream.read(buffer);

                File receivedFile = new File(uploadLocation);
                if (!receivedFile.exists()) {
                    // Save file to disk
                    OutputStream outStream = new FileOutputStream(receivedFile);
                    outStream.write(buffer);
                    log.info("Saved an uploaded file to: " + uploadLocation);

                    // update session files
                    edu.utdallas.csmc.web.model.file.File csmsFile = new edu.utdallas.csmc.web.model.file.File();
                    csmsFile.setName(file.getOriginalFilename());
                    csmsFile.setHash(fh);
                    adminSessionRequestService.uploadFileToSession(csmsFile, sessionId, sessionType);
                } else
                    log.info("File existed!!!");
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        return "redirect:/admin/session/edit/{sessionId}/{sessionType}";
    }

    /**
     * This API submits an edition for a session
     */
    @RequestMapping("/session/edit/submit/{sessionId}/{sessionType}")
    public String editSubmitSession(ModelMap model, @RequestParam Map<String, String> allRequestParams,
            @PathVariable("sessionId") String sessionId, @PathVariable("sessionType") String sessionType,
            @RequestParam("topic") String topic, @RequestParam("type") String type,
            @RequestParam("description") String description,
            @RequestParam("studentInstruction") String studentInstruction,
            @RequestParam("mentorInstruction") String mentorInstruction, @RequestParam("room") String room,
            @RequestParam("sections") String[] sections,

            @RequestParam(required = false, value = "graded") String graded,
            @RequestParam(required = false, value = "numericgrade") String numericgrade,
            @RequestParam(required = false, value = "color") String color,
            @RequestParam(required = false, value = "repeats") String repeats,
            @RequestParam(required = false, value = "capacity") String capacity,
            @RequestParam(required = false, value = "durationH") String durationH,
            @RequestParam(required = false, value = "durationM") String durationM,
            @RequestParam(required = false, value = "startDate") String startDate,
            @RequestParam(required = false, value = "endDate") String endDate) {

        AdminSessionUpdatedDTO adminSessionUpdatedDTO = new AdminSessionUpdatedDTO();
        adminSessionUpdatedDTO.setSessionId(sessionId);
        adminSessionUpdatedDTO.setSessionType(sessionType);
        adminSessionUpdatedDTO.setTopic(topic);
        adminSessionUpdatedDTO.setType(type);
        adminSessionUpdatedDTO.setDescription(description);
        adminSessionUpdatedDTO.setStudentInstruction(studentInstruction);
        adminSessionUpdatedDTO.setMentorInstruction(mentorInstruction);
        adminSessionUpdatedDTO.setRoom(room);
        adminSessionUpdatedDTO.setSections(sections);

        adminSessionUpdatedDTO.setGraded(graded != null);
        adminSessionUpdatedDTO.setNumericgrade(numericgrade != null);
        adminSessionUpdatedDTO.setColor(color != null ? color : "");
        adminSessionUpdatedDTO.setRepeats(repeats != null ? Integer.parseInt(repeats) : 0);
        adminSessionUpdatedDTO.setCapacity(capacity != null ? Integer.parseInt(capacity) : 0);
        adminSessionUpdatedDTO.setDurationH(durationH != null ? Integer.parseInt(durationH) : 0);
        adminSessionUpdatedDTO.setDurationM(durationM != null ? Integer.parseInt(durationM) : 0);
        adminSessionUpdatedDTO.setStartDate(startDate != null ? startDate : "");
        adminSessionUpdatedDTO.setEndDate(endDate != null ? endDate : "");

        adminSessionRequestService.updateSession(adminSessionUpdatedDTO, allRequestParams);

        log.info("Updated session : " + sessionId);
        return "redirect:/admin/session/request/";
    }

    /**
     * This API approves a new session request and creates a new session
     */
    @RequestMapping("/session/create/{requestId}/{sessionType}")
    public String approveSession(ModelMap model, @PathVariable("requestId") String requestId,
            @PathVariable("sessionType") String sessionType) {
        UUID sessionId = adminSessionRequestService.approveRequestSession(UUID.fromString(requestId), sessionType);

        AdminSessionFormDTO adminSessionFormDTO = adminSessionRequestService.getSessionForm(sessionId, sessionType);
        model.addAttribute("form", adminSessionFormDTO);
        return "role/admin/session/form.html";
    }

    /**
     * This API denies an new session request
     */
    @RequestMapping("/session/deny/{sessionId}/{sessionType}")
    public String denySession(ModelMap model, @PathVariable("sessionId") String sessionId,
            @PathVariable("sessionType") String sessionType) {

        adminSessionRequestService.denyRequestSession(UUID.fromString(sessionId), sessionType);
        return "redirect:/admin/session/request/";
    }

    /**
     * This API returns a page for grading an session request by its ID
     */
    @RequestMapping("/session/grades/{sessionId}/{sessionType}")
    public String gradesSession(ModelMap model, @PathVariable("sessionId") String sessionId,
            @PathVariable("sessionType") String sessionType) {
        model = adminSessionRequestService.getDetailsForScheduledSessionOrQuiz(model, UUID.fromString(sessionId),
                sessionType);
        return "role/admin/session/grade/grades.html";
    }

    /**
     * This API will change the grades of the student through Asynchronous AJAX
     * request.
     */
    @RequestMapping(value = "/ajax/session/grades", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<?> gradesEditAjaxAction(
            @RequestBody RequestGradeAttendanceObjectDTO requestGradeAttendanceObjectDTO) {
        int grade = requestGradeAttendanceObjectDTO.getGrade();
        UUID id = requestGradeAttendanceObjectDTO.getAttendance();
        adminSessionRequestService.updateGrade(grade, id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * This API will fetch all the details required to mark student attendance.
     */
    @RequestMapping("/session/attend/{Id}/{sessionType}")
    public String getDetailsToMarkStudentAttendance(ModelMap model, @PathVariable("Id") String id,
            @PathVariable("sessionType") String sessionType) {
        AdminAttendanceGradesResultDTO attendanceGrade = adminSessionRequestService
                .getDetailsForAttendance(UUID.fromString(id), sessionType);
        model.addAttribute("attendanceGrade", attendanceGrade);
        model.addAttribute("sessionType", sessionType);
        return "role/admin/session/grade/form.html";
    }

    /**
     * This API will mark the student attendance after the mentor clicks the submit
     * button.
     */
    @RequestMapping("/session/attend/submit/{Id}/{sessionType}")
    public String markStudentAttendance(ModelMap model, @PathVariable("Id") String sessionId,
            @PathVariable("sessionType") String sessionType, @RequestParam(value = "timeSlot") String timeslot,
            @RequestParam("users") String netId, @RequestParam(required = false, value = "grade") String grade) {
        AdminAttendanceGradesResultDTO studentAttendance = adminSessionRequestService
                .markStudentAttendance(UUID.fromString(sessionId), timeslot, netId, grade, sessionType);
        model.addAttribute("markStudentAttendance", studentAttendance);
        return "redirect:/admin/session/grades/{Id}/{sessionType}";
    }

    /*********** End of Session_Request ***********/

    @RequestMapping("/schedule/absences")
    public String getAttendanceList(ModelMap model) {
        List<AdminAbsencesResultDTO> adminAbsencesResultDTOList = adminAbsencesService.getAbsencesDetails();
        model.addAttribute("absences", adminAbsencesResultDTOList);

        return "role/admin/schedule/absences.html";
    }

    @RequestMapping("/swipes")
    public String swipes(ModelMap model) {
        return "role/admin/swipes/swipes.html";
    }

    @RequestMapping(value = "/swipes/queryByPage", method = RequestMethod.POST)
    public ResponseEntity<SwipesDataTableResultDTO> queryByPage(@Valid @RequestBody DataTablesInput input) {
        SwipesDataTableResultDTO swipeDataTableResultDTO = adminSwipesService.querySwipesByPage(input);
        return ResponseEntity.ok(swipeDataTableResultDTO);
    }

    @RequestMapping("/department")
    public String listDepartments(ModelMap model) {
        List<AdminDepartmentsResultDTO> adminDepartmentsResultDTOList = adminDepartmentService
                .getDepartmentDetailsList();
        model.addAttribute("departments", adminDepartmentsResultDTOList);
        return "role/admin/department/list.html";
    }

    @RequestMapping("/department/create")
    public String createDepartment(ModelMap model) {

        return "role/admin/department/add.html";
    }

    @RequestMapping("/department/create/submit")
    public String submitNewDepartment(@RequestParam("name") String departmentName,
            @RequestParam("abbreviation") String abbreviation,
            @RequestParam(required = false, value = "adminNotes") String adminNotes) {

        AdminDepartmentsResultDTO adminDepartmentsResultDTO = new AdminDepartmentsResultDTO();
        adminDepartmentsResultDTO.setName(departmentName);
        adminDepartmentsResultDTO.setAbbreviation(abbreviation);
        adminDepartmentsResultDTO.setAdminNotes(adminNotes);
        adminDepartmentsResultDTO.setUsername(defaultUsernameService.getUsername());

        adminDepartmentService.submitDepartmentDetails(adminDepartmentsResultDTO);

        return "redirect:/admin/department";
    }

    @RequestMapping("/department/edit/{departmentId}")
    public String editDepartment(ModelMap model, @PathVariable("departmentId") String departmentId) {
        AdminDepartmentsResultDTO adminDepartmentsResultDTO = new AdminDepartmentsResultDTO();
        adminDepartmentsResultDTO.setDepartmentId(departmentId);
        adminDepartmentService.getDepartmentDetails(adminDepartmentsResultDTO);
        model.addAttribute("department", adminDepartmentsResultDTO);

        return "role/admin/department/edit.html";
    }

    @RequestMapping("/department/update/{departmentId}")
    public String updateDepartment(@PathVariable("departmentId") String departmentId,
            @RequestParam("name") String departmentName, @RequestParam("abbreviation") String abbreviation,
            @RequestParam(required = false, value = "adminNotes") String adminNotes) {

        AdminDepartmentsResultDTO adminDepartmentsResultDTO = new AdminDepartmentsResultDTO();
        adminDepartmentsResultDTO.setDepartmentId(departmentId);
        adminDepartmentsResultDTO.setName(departmentName);
        adminDepartmentsResultDTO.setAbbreviation(abbreviation);
        adminDepartmentsResultDTO.setAdminNotes(adminNotes);
        adminDepartmentsResultDTO.setUsername(defaultUsernameService.getUsername());

        adminDepartmentService.updateDepartmentDetails(adminDepartmentsResultDTO);

        return "redirect:/admin/department";
    }

    @RequestMapping("/department/delete/{departmentId}")
    public String deleteDepartment(@PathVariable("departmentId") String departmentId) {
        String returnPath = adminDepartmentService.deleteDepartment(UUID.fromString(departmentId));

        return returnPath;
    }

    @RequestMapping("/ip")
    public String listIps(ModelMap model) {
        model = adminIpService.getIpDetails(model);
        return "role/admin/ip/list.html";
    }

    @RequestMapping("/ip/create")
    public String createIp(ModelMap model) {
        return "role/admin/ip/add.html";
    }

    @RequestMapping("/ip/create/submit")
    public String addIp(ModelMap model, @RequestParam("address") String ipAddress,
            @RequestParam(required = false, value = "building") String building,
            @RequestParam(required = false, value = "floor") String floor,
            @RequestParam(required = false, value = "number") String number,
            @RequestParam(required = false, value = "blocked") String blocked) {

        AdminIpResultDTO adminIpResultDTO = new AdminIpResultDTO();
        adminIpResultDTO.setIpAddress(ipAddress);
        if (!building.isEmpty() && !floor.isEmpty() && !number.isEmpty()) {
            adminIpResultDTO.setBuilding(building);
            adminIpResultDTO.setFloor(floor);
            adminIpResultDTO.setClassRoomNumber(number);
        }
        adminIpResultDTO.setBlocked(blocked);

        String message = adminIpService.submitIpDetails(adminIpResultDTO);
        model.addAttribute("message", message);
        model = adminIpService.getIpDetails(model);
        return "role/admin/ip/list.html";
    }

    @RequestMapping("/ip/edit/{ipId}")
    public String editIp(ModelMap model, @PathVariable("ipId") String ipId) {
        AdminIpResultDTO adminIpResultDTO = adminIpService.getIpAdressDetails(ipId);
        model.addAttribute("ip", adminIpResultDTO);
        return "role/admin/ip/edit.html";
    }

    @RequestMapping("/ip/update/{ipId}")
    public String updateIp(@PathVariable("ipId") String ipId, @RequestParam("address") String ipAdress,
            @RequestParam(required = false, value = "building") String building,
            @RequestParam(required = false, value = "floor") String floor,
            @RequestParam(required = false, value = "number") String number,
            @RequestParam(required = false, value = "blocked") String blocked) {

        AdminIpResultDTO adminIpResultDTO = new AdminIpResultDTO();
        adminIpResultDTO.setIpId(UUID.fromString(ipId));
        adminIpResultDTO.setIpAddress(ipAdress);
        adminIpResultDTO.setBuilding(building);
        adminIpResultDTO.setFloor(floor);
        adminIpResultDTO.setClassRoomNumber(number);
        adminIpResultDTO.setBlocked(blocked);

        adminIpService.updateIpDetails(adminIpResultDTO);

        return "redirect:/admin/ip";
    }

    @RequestMapping("/ip/delete/{ipId}")
    public String deleteIp(@PathVariable("ipId") String ipId) {
        String returnPath = adminIpService.deleteIp(UUID.fromString(ipId));
        return returnPath;
    }

    @RequestMapping("/room/list")
    public String listRooms(ModelMap model) {

        List<AdminRoomResultDTO> roomList = adminRoomsService.getListofRooms();
        model.addAttribute("roomList", roomList);
        return "role/admin/room/list.html";
    }

    @RequestMapping("/room/create")
    public String createRoom(ModelMap model) {
        return "role/admin/room/add.html";
    }

    @RequestMapping("/room/create/submit")
    public String addRoom(@RequestParam("building") String building, @RequestParam("floor") String floor,
            @RequestParam("number") String roomNumber,
            @RequestParam(required = false, value = "desc") String description,
            @RequestParam("capacity") String capacity,
            @RequestParam(required = false, value = "active") String active) {

        AdminRoomResultDTO adminRoomResultDTO = new AdminRoomResultDTO();
        adminRoomResultDTO.setBuilding(building);
        adminRoomResultDTO.setFloor(floor);
        adminRoomResultDTO.setClassRoomNumber(roomNumber);
        adminRoomResultDTO.setDescription(description);
        adminRoomResultDTO.setCapacity(Integer.parseInt(capacity));
        adminRoomResultDTO.setActive(active);

        adminRoomsService.submitRoomDetails(adminRoomResultDTO);

        return "redirect:/admin/room/list";
    }

    @RequestMapping("/room/edit/{roomId}")
    public String editRoom(ModelMap model, @PathVariable("roomId") String roomId) {
        AdminRoomResultDTO adminRoomResultDTO = new AdminRoomResultDTO();
        adminRoomResultDTO.setRoomId(UUID.fromString(roomId));
        adminRoomsService.getRoomDetails(adminRoomResultDTO);
        model.addAttribute("room", adminRoomResultDTO);

        return "role/admin/room/edit.html";
    }

    @RequestMapping("/room/update/{roomId}")
    public String updateRoom(@PathVariable("roomId") String roomId, @RequestParam("building") String building,
            @RequestParam("floor") String floor, @RequestParam("number") String roomNumber,
            @RequestParam(required = false, value = "desc") String description,
            @RequestParam("capacity") String capacity,
            @RequestParam(required = false, value = "active") String active) {

        AdminRoomResultDTO adminRoomResultDTO = new AdminRoomResultDTO();
        adminRoomResultDTO.setRoomId(UUID.fromString(roomId));
        adminRoomResultDTO.setBuilding(building);
        adminRoomResultDTO.setFloor(floor);
        adminRoomResultDTO.setClassRoomNumber(roomNumber);
        adminRoomResultDTO.setDescription(description);
        adminRoomResultDTO.setCapacity(Integer.parseInt(capacity));
        adminRoomResultDTO.setActive(active);

        adminRoomsService.updateRoomDetails(adminRoomResultDTO);

        return "redirect:/admin/room/list";
    }

    @RequestMapping("/room/delete/{roomId}")
    public String deleteRoom(@PathVariable("roomId") String roomId) {

        String returnPath = adminRoomsService.deleteRoom(UUID.fromString(roomId));

        return returnPath;
    }

    @RequestMapping(path = "/report/ajax/section/{section_id}", method = RequestMethod.POST)
    public String ajaxSelectedSectionAction( @PathVariable UUID section_id, ModelMap model) {
        log.info("Front end to back end passing data ===> "+section_id);
        return "redirect:/admin/report/{section_id}";
    }

    @RequestMapping(path = "/report/{section_id}", method = RequestMethod.GET)
    public String sectionSessionDependency( @PathVariable UUID section_id, ModelMap model) {
        log.info("Entered section session dependency method!!");
        log.info("Section Id ===> "+section_id);
        List<Session> sessions = adminReportService.getAllSessionsForCurrentSemester(section_id).stream().map(i->(Session)i).collect(Collectors.toList());
        log.info("Sessions size ==> {}",sessions.size());
        for(int i=0;i<sessions.size();i++) {
            log.info("session {} ==> {}",i, sessions.get(i));
        }
        Section section = adminReportService.getSectionWithSectionId(section_id);
        model.addAttribute("section", section);
        model.addAttribute("iFirstName",section.getInstructors().get(0).getFirstName());
        model.addAttribute("iLastName",section.getInstructors().get(0).getLastName());
        model.addAttribute("sessionsList",sessions);
        log.info("Loading report test page!!");
        return "role/admin/report/report_test.html";
    }

    @RequestMapping(path = "/report")
    public String generateReportForm(ModelMap model) {
        // populate and generate the section and session
        List<Section> allSectionsInCurrentSemester = adminReportService.getAllSectionsCurrentSemester();
        model.addAttribute("sections", allSectionsInCurrentSemester);
        List<Session> allSessionsInCurrentSemester = adminReportService.getAllSessionsForCurrentSemester();
        model.addAttribute("sessions", allSessionsInCurrentSemester);
        return "role/admin/report/report_home.html";
    }

    @RequestMapping(path = "/report/submit/", method = RequestMethod.POST)
    public String getReportBySectionAndSession(ModelMap model,
                                               @ModelAttribute("section") String sectionNameWithInstructor,
                                               @ModelAttribute("session") String sessionName) {
        String sectionNameOnly = sectionNameWithInstructor.split("\\|")[0].trim();
        String reportType;

        log.info(" section id in submit method ==> {}",sectionNameOnly);

        log.info(adminReportService.getAllSectionsCurrentSemester());

        log.info("sections size ==> {}", adminReportService.getAllSectionsCurrentSemester().size());

        /*Section targetSection = adminReportService.getAllSectionsCurrentSemester().stream()
                .filter(section -> String.format("%s %s.%s", section.getCourse().getDepartment().getAbbreviation(),
                        section.getCourse().getNumber(), section.getNumber()).equals(sectionNameOnly))
                .findFirst().orElse(null);
        log.info(targetSection);*/

        Section targetSection = adminReportService.getSectionWithSectionId(UUID.fromString(sectionNameOnly));

        // session
        if (!sessionName.equals("Walk In")) {
            reportType = "session";
            log.info(sessionName);
            log.info(adminReportService.getAllSessionsCurrentSemester().stream()
                    .filter(session -> session.getTopic().equals(sessionName)));

            ScheduledSession targetSession = adminReportService.getAllSessionsCurrentSemester().stream()
                    .filter(session -> session.getTopic().equals(sessionName)).findFirst().orElse(null);
            List<Pair<User, SessionAttendance>> sessionReport = adminReportService.getReportForSession(targetSection,
                    targetSession);
            log.info(targetSession);
            log.info(sessionReport);
            model.addAttribute("report", sessionReport);
            model.addAttribute("session", targetSession);
        } else {
            // walk in
            reportType = "walk_in";
            assert targetSection != null;
            List<? extends Attendance> walkInReport = adminReportService.getWalkInAttendanceBySection(targetSection);
            model.addAttribute("report", walkInReport);
        }

        model.addAttribute("chosen_section", targetSection);
        model.addAttribute("chosen_session_name", sessionName);
        model.addAttribute("report_type", reportType);
        return "role/admin/report/report.html";
    }

    @RequestMapping(path = "/report/submit/report_download/session/{section_id}/{session_id}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadReportSession(ModelMap model, @PathVariable String section_id,
            @PathVariable String session_id) {
        ReportExportHelper helper = new ReportExportHelper();
        Section sectionObj = adminReportService.getAllSectionsCurrentSemester().stream()
                .filter(section -> section.getId().equals(UUID.fromString(section_id))).findFirst().orElse(null);
        ScheduledSession sessionObj = adminReportService.getAllSessionsCurrentSemester().stream()
                .filter(session -> session.getId().equals(UUID.fromString(session_id))).findFirst().orElse(null);
        assert sectionObj != null;
        assert sessionObj != null;
        List<Pair<User, SessionAttendance>> sessionReport = adminReportService.getReportForSession(sectionObj,
                sessionObj);
        String content = helper.convertSessionAttendanceToString(sessionReport, sessionObj);
        FileUtil fileUtil = new FileUtil();
        return fileUtil.writeToCSVFile(content);
    }

    @RequestMapping(path = "/report/submit/report_download/walk_in/{section_id}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadReportWalkIn(ModelMap model, @PathVariable String section_id) {
        ReportExportHelper helper = new ReportExportHelper();
        Section sectionObj = adminReportService.getAllSectionsCurrentSemester().stream()
                .filter(section -> section.getId().equals(UUID.fromString(section_id))).findFirst().orElse(null);
        assert sectionObj != null;
        List<WalkInAttendance> walkInReport = adminReportService.getWalkInAttendanceBySection(sectionObj);
        String content = helper.convertWalkInAttendanceToString(walkInReport);
        FileUtil fileUtil = new FileUtil();
        return fileUtil.writeToCSVFile(content);
    }

    @RequestMapping("/mentors")
    public String getMentorRating(ModelMap model) throws ParseException {
        AdminMentorResultDTO adminMentorResultDTO = adminMentorService.getMentorDetails();
        model.addAttribute("adminMentorResultDTO", adminMentorResultDTO);
        return "role/admin/user/mentor_specialties.html";
    }

    @RequestMapping("/schedule/timesheets")
    public String getTimesheet(ModelMap model) {
        List<AdminTimesheetResultDTO> mentors = adminTimesheetService.getMentor();
        model.addAttribute("mentors", mentors);
        return "role/admin/schedule/timesheets.html";
    }

    @RequestMapping("/schedule/timesheets/report")
    public String submitNewCourse(ModelMap model, @RequestParam("mentor") String userId,
            @RequestParam("startdate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam("enddate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) throws ParseException {

        AdminTimesheetResultDTO adminTimesheetResultDTO = new AdminTimesheetResultDTO();
        adminTimesheetResultDTO.setUserId(userId);
        adminTimesheetResultDTO.setStartDate(DateUtil.atStartOfDay(startDate));
        adminTimesheetResultDTO.setEndDate(DateUtil.atEndOfDay(endDate));
        String mentorName = adminTimesheetService.mentorName(adminTimesheetResultDTO);
        adminTimesheetResultDTO.setMentorName(mentorName);

        TreeMap<Date, HashMap<Date, Date>> timesheet = new TreeMap<Date, HashMap<Date, Date>>();
        timesheet = adminTimesheetService.getTimesheet(adminTimesheetResultDTO);
        adminTimesheetResultDTO.setTimesheet(timesheet);
        adminTimesheetService.getFinalTimesheet(adminTimesheetResultDTO);

        model.addAttribute("adminTimesheetResultDTO", adminTimesheetResultDTO);
        List<AdminTimesheetResultDTO> mentors = adminTimesheetService.getMentor();
        model.addAttribute("mentors", mentors);

        return "role/admin/schedule/timesheets.html";

    }

    /**
     * This API will display the schedule of mentors in each shift in calendar
     * format
     */
    @RequestMapping("/schedule/calendar")
    public String scheduleCalendar(ModelMap model) {
        AdminScheduleCalendarResultDTO calendar = adminScheduleCalendarService.getCalendarScheduleDetails();
        model.addAttribute("scheduleCalendar", calendar);
        return "role/admin/schedule/calendar.html";
    }

    /**
     * This API will change the shift leader
     */
    @RequestMapping(value = "/schedule/ajax/shift/leader", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<?> ajaxShiftLeaderAction(@RequestBody RequestObjectSchedule requestObject) {
        adminScheduleCalendarService.updateShiftLeader(requestObject);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * This API will remove mentor from a shift
     */
    @RequestMapping(value = "/schedule/ajax/shift/remove", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<?> ajaxShiftRemove(@RequestBody RequestObjectShift requestObjectShift) {
        adminScheduleCalendarService.removeMentorFromShift(requestObjectShift);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * This API will add mentor to a shift
     */
    @RequestMapping(value = "/schedule/ajax/shift", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<?> scheduleAjaxShift(@RequestBody RequestObjectShift requestObjectShift) {
        adminScheduleCalendarService.updateShift(requestObjectShift);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * This API will change color code of subject
     */

    @RequestMapping(value = "/subject/ajax/color", consumes = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> updateColor(@RequestBody RequestObjectColor requestObject) {
        adminScheduleCalendarService.updateSubjectColor(requestObject);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * This API will display all the shifts on clicking Schedule Config
     */
    @RequestMapping("/schedule")
    public String listScheduleTimes(ModelMap model) {
        List<AdminScheduleTimesResultDTO> scheduleTimes = adminScheduleCalendarService.getScheduleTimes();
        List<Room> scheduleRooms = adminScheduleCalendarService.getScheduleRooms();
        model.addAttribute("scheduleTimes", scheduleTimes);
        model.addAttribute("rooms", scheduleRooms);
        return "role/admin/schedule/schedule_setup.html";
    }

    /**
     * This API will increase number of mentors for a shift
     */
    @RequestMapping(value = "/schedule/increase", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<?> increaseMaxMentorsAction(@RequestBody RequestObjectMaxMentor requestObjectMaxMentors) {
        adminScheduleCalendarService.updateIncMaxMentors(requestObjectMaxMentors);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * This API will decrease number of mentors for a shift
     */
    @RequestMapping(value = "/schedule/decrease", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<?> decreaseMaxMentorsAction(@RequestBody RequestObjectMaxMentor requestObjectMaxMentors) {
        adminScheduleCalendarService.updateDecMaxMentors(requestObjectMaxMentors);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * This API will create a new shift
     */
    @RequestMapping("/schedule/create")
    public String createShift(@RequestParam("day") String day, @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime, @RequestParam(required = false, value = "room") String location) {

        AdminCreateShiftResultDTO adminCreateShiftResultDTO = new AdminCreateShiftResultDTO();
        adminCreateShiftResultDTO.setDay(Integer.parseInt(day));
        if (startTime.length() < 6)
            startTime = startTime + ":00";
        if (endTime.length() < 6)
            endTime = endTime + ":00";

        adminCreateShiftResultDTO.setStartTime(Time.valueOf(startTime));
        adminCreateShiftResultDTO.setEndTime(Time.valueOf(endTime));
        adminCreateShiftResultDTO.setRoom(UUID.fromString(location));

        adminScheduleCalendarService.createShift(adminCreateShiftResultDTO);
        return "redirect:/admin/schedule";
    }

    /**
     * This API will update the schedule for the remaining days in the currently
     * active semester
     */
    @RequestMapping("/schedule/update")
    public String updateSchedule() {
        adminScheduleCalendarService.getUpdateDetails();
        return "redirect:/admin/schedule/calendar";
    }

    /*********** Start of Semester ***********/
    /**
     * This API will fetch all available semesters
     */
    @RequestMapping("/semester")
    public String getSemesterList(ModelMap model) {

        List<AdminSemeterResultDTO> adminSemeterResultDTOList = adminSemesterService.getSemesterList();
        model.addAttribute("semesters", adminSemeterResultDTOList);
        return "role/admin/semester/list.html";
    }

    /**
     * This API will create a new semester
     */
    @RequestMapping("/semester/create")
    public String addNewSemester(ModelMap model) {
        // model.addAttribute("Seasons",adminSemesterService.getSeasons());
        model.addAttribute("Seasons", adminSemesterService.getSeasonsNames());
        return "role/admin/semester/add.html";
    }

    @RequestMapping("/semester/create/submit")
    public String createNewSemester(ModelMap model, @RequestParam("season") String season,
            @RequestParam("year") String year, @RequestParam("startdate") String startdate,
            @RequestParam("enddate") String enddate,
            @RequestParam(required = false, value = "supported") boolean isSupported) {
        try {
            adminSemesterService.saveSemester(season, year, startdate, enddate, isSupported);

        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            return "redirect:/admin/semester";
        }
    }

    /**
     * This API will edit the specific semester
     */
    @RequestMapping("/semester/edit/{semesterID}")
    public String editSemester(ModelMap model, @PathVariable("semesterID") String semesterID) {

        // model.addAttribute("seasons",adminSemesterService.getSeasons());
        model.addAttribute("seasons", adminSemesterService.getSeasonsNames());
        AdminSemeterResultDTO adminSemeterResultDTO = adminSemesterService.getSemesterDetails(semesterID);
        model.addAttribute("semester", adminSemeterResultDTO);

        return "role/admin/semester/edit.html";
    }

    /**
     * This API will update the specific semester
     */
    @RequestMapping("/semester/update/{semesterID}")
    public String saveEditedSemester(ModelMap model, @PathVariable("semesterID") String semesterID,
            @RequestParam("season") String season, @RequestParam("year") String year,
            @RequestParam("startdate") String startdate, @RequestParam("enddate") String enddate,
            @RequestParam(required = false, value = "active") boolean isSupported) {
        try {
            adminSemesterService.saveEditedSemester(semesterID, season, year, startdate, enddate, isSupported);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            return "redirect:/admin/semester";
        }
    }

    /**
     * This API will delete the specific semester
     */
    @RequestMapping("/semester/delete/{semesterId}")
    public String deleteSemester(@PathVariable("semesterId") String semesterId) {
        adminSemesterService.deleteSemester(semesterId);
        return "redirect:/admin/semester";
    }

    /*********** End of Semester ***********/

    /*********** Start of Section ***********/
    /**
     * This API will display all the available sections
     */
    @RequestMapping("/section")
    public String getSectionsList(ModelMap model) {
        model.addAttribute("sections", adminSectionService.getRawSectionList());
        return "role/admin/section/list.html";
    }

    /**
     * This API will create a new section
     */
    @RequestMapping("section/create")
    public String addNewSection(ModelMap model) {
        model.addAttribute("courses", adminSectionService.getCourseList());
        model.addAttribute("semesters", adminSectionService.getSemesterList());
        model.addAttribute("instructors", adminSectionService.getInstructors());
        return "role/admin/section/add.html";
    }

    @RequestMapping("section/create/submit")
    public String saveNewSection(@RequestParam("cnumber") String cnumber, @RequestParam("number") String number,
            @RequestParam("semester") String semester, @RequestParam("instructor") String instructor,
            @RequestParam("tassistant") String tassistant, @RequestParam("description") String adminNotes) {
        adminSectionService.buildSection(cnumber, number, semester, instructor, tassistant, adminNotes);
        return "redirect:/admin/section";
    }

    /**
     * This API will edit a specific section
     */
    @RequestMapping(value = { "/section/edit/{sectionId}" })
    public String editSection(ModelMap model, @PathVariable(value = "sectionId") String sectionId) {
        model.addAttribute("section", adminSectionService.getSection(sectionId));
        model.addAttribute("courses", adminSectionService.getCourseList());
        model.addAttribute("semesters", adminSectionService.getSemesterList());
        model.addAttribute("instructors", adminSectionService.getInstructors());
        return "role/admin/section/edit.html";

    }

    /**
     * For choosing an excel file to upload.
     */
    @RequestMapping(value = { "/section/uploadRoster" })
    public String uploadAllRoster(ModelMap model) {
        return "role/admin/section/roster_upload.html";
    }

    /**
     * @Post For saving the roster from the excel file(Student Roster Upload)
     * To upload a new roster from sections
     */
    @RequestMapping(value = { "/section/saveAllRoster" }, method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String saveAllRoster(ModelMap model, @RequestParam(value = "file") MultipartFile file) throws IOException {
        adminSectionService.saveRoster(file);
        return getSectionListRoster(model);
    }

    /**
     * @Get For saving the roster from the excel file(Student Roster Upload)
     */
    @RequestMapping(value = { "/section/saveAllRoster" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getSectionListRoster(ModelMap model) {
        return getSectionsList(model);
    }

    @RequestMapping(value = { "/section/{sectionId}/upload" })
    public String uploadRoster(ModelMap model, @PathVariable(value = "sectionId") String sectionId) {
        model.addAttribute("sectionId", sectionId);
        return "role/admin/section/section_roster.html";
    }

    /**
     * @Post For saving the roster from the excel file (Bulk Upload)
     * To edit a section roster
     */
    @RequestMapping(value = {
            "/section/{sectionId}/admin/section/{sectionId}/saveRoster" }, method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String saveRoster(ModelMap model, @PathVariable(value = "sectionId") String sectionId,
            @RequestParam(value = "file") MultipartFile file) throws IOException {
        adminSectionService.saveRoster(UUID.fromString(sectionId), file);
        model.addAttribute("section", adminSectionService.getSection(sectionId));
        return getSectionByID(model, sectionId);
    }

    /**
     * @Get For saving the roster from the excel file(Bulk Upload)
     */
    @RequestMapping(value = {
            "/section/{sectionId}/admin/section/{sectionId}/saveRoster" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getSectionByID(ModelMap model, @PathVariable(value = "sectionId") String sectionId) {
        model.addAttribute("section", adminSectionService.getSection(sectionId));
        return getSectionsList(model);
    }

    /**
     * This API will display the student roster for a specific section
     */
    @RequestMapping(value = { "/section/roster/{sectionId}" })
    public String showRoster(ModelMap model, @PathVariable(value = "sectionId", required = true) String sectionId) {
        Section section = adminSectionService.getSection(sectionId);
        model.addAttribute("section", section);
        model.addAttribute("sectionName", section.toString());
        return "role/admin/section/roster.html";
    }

    /**
     * This API will update a specific section
     */
    @RequestMapping(value = { "/section/update/{sectionId}" })
    public String udpateSection(@PathVariable(value = "sectionId", required = true) String sectionId,
            @RequestParam("cnumber") String cnumber, @RequestParam("number") String number,
            @RequestParam("semester") String semester, @RequestParam("instructor") String instructor,
            @RequestParam("tassistant") String tassistant, @RequestParam("description") String adminNotes) {
        adminSectionService.saveEditedSection(sectionId, cnumber, number, semester, instructor, tassistant, adminNotes);
        return "redirect:/admin/section";
    }

    /**
     * This API will delete a specific section
     */
    @RequestMapping("/section/delete/{sectionId}")
    public String deleteSection(@PathVariable(value = "sectionId", required = true) String sectionId) {
        adminSectionService.deleteSection(sectionId);
        return "redirect:/admin/section";
    }

    /*********** End of Section ***********/

    /*********** Admin Users User View ***********/

    @RequestMapping("/users/user")
    public String getAllUsers(ModelMap model) {
        return "role/admin/user/list.html";
    }

    @RequestMapping("/users/user/create")
    public String createUser(ModelMap model) {

        AdminUserFormResultDTO form = adminUserService.getNewUserForm();
        model.addAttribute("form", form);
        return "role/admin/user/add.html";
    }

    @RequestMapping("/users/user/create/submit")
    public String submitNewUser(ModelMap model, @RequestParam String username, @RequestParam String firstname,
            @RequestParam String lastname, @RequestParam String cardid, @RequestParam String scancode,
            @RequestParam("role") String[] roleSelected) {

        AdminUserFormResultDTO adminUserFormResultDTO = adminUserService.createNewUser(username, firstname, lastname,
                cardid, scancode, roleSelected);
        String message = adminUserFormResultDTO.getMessage();
        if (message != null && !message.isEmpty()) {
            model.addAttribute("message", adminUserFormResultDTO.getMessage());
            model.addAttribute("form", adminUserFormResultDTO);
            return "role/admin/user/add.html";
        }
        return "redirect:/admin/users/user";
    }

    @RequestMapping("/users/user/update/{userId}")
    public String updateUser(ModelMap model, @PathVariable("userId") String userId) {

        AdminUserFormResultDTO form = adminUserService.getUpdateUserForm(UUID.fromString(userId));
        model.addAttribute("form", form);
        return "role/admin/user/edit.html";
    }

    @RequestMapping("/users/user/update/submit/{userId}")
    public String submitUpdatedUser(ModelMap model, @PathVariable("userId") String userId,
            @RequestParam String username, @RequestParam String firstname, @RequestParam String lastname,
            @RequestParam String cardid, @RequestParam String scancode, @RequestParam("role") String[] roleSelected) {

        AdminUserFormResultDTO adminUserFormResultDTO = adminUserService.updateCreatedUser(UUID.fromString(userId),
                username, firstname, lastname, cardid, scancode, roleSelected);
        String message = adminUserFormResultDTO.getMessage();
        if (message != null && !message.isEmpty()) {
            model.addAttribute("message", adminUserFormResultDTO.getMessage());
            model.addAttribute("form", adminUserFormResultDTO);
            return "role/admin/user/edit.html";
        }
        return "redirect:/admin/users/user";
    }

    @RequestMapping(value = "/users/user/queryByPage", method = RequestMethod.POST)
    public ResponseEntity<UserDataTableResultDTO> queryByPage(@Valid @RequestBody DataTablesInput input,
            @RequestParam(value = "role", required = false) String role) {
        UserDataTableResultDTO userDataTableResultDTO = adminUserService.queryUsersByPage(input);
        return ResponseEntity.ok(userDataTableResultDTO);
    }

    @RequestMapping("/users/user/delete/{userId}")
    public String deleteUser(@PathVariable("userId") String userId) {
        System.out.println("userId: " + userId);
        adminUserService.deleteUser(UUID.fromString(userId));
        return "redirect:/admin/users/user";
    }

    /*********** Admin Announcement View ***********/

    @RequestMapping("/announcement")
    public String listAnnouncements(ModelMap model) {
        AdminAnnouncementResultDTO adminAnnouncementResultDTO = new AdminAnnouncementResultDTO();
        List<Announcement> announcementList = adminAnnouncementService.getAnnouncement();
        adminAnnouncementResultDTO.setAnnouncementList(announcementList);

        model.addAttribute("adminAnnouncementResultDTO", adminAnnouncementResultDTO);
        return "role/admin/announcement/list.html";
    }

    @RequestMapping("/announcement/create")
    public String createAnnouncements(ModelMap model) {
        AdminAnnouncementResultDTO adminAnnouncementResultDTO = new AdminAnnouncementResultDTO();
        List<Role> roleList = adminAnnouncementService.getRole();
        adminAnnouncementResultDTO.setRoleList(roleList);

        model.addAttribute("adminAnnouncementResultDTO", adminAnnouncementResultDTO);
        return "role/admin/announcement/add.html";
    }

    @RequestMapping("/announcement/create/submit")
    public String submitAnnouncements(@RequestParam("subject") String subject, @RequestParam("message") String message,
            @RequestParam("startdate") String startdate, @RequestParam("enddate") String enddate,
            @RequestParam("role[]") String[] role, @RequestParam(required = false, value = "active") String active,
            @RequestParam(required = false, value = "displayannouncement") String displayannouncement)
            throws ParseException {

        AdminAnnouncementResultDTO adminAnnouncementResultDTO = new AdminAnnouncementResultDTO();
        adminAnnouncementResultDTO.setSubject(subject);
        adminAnnouncementResultDTO.setMessage(message);
        adminAnnouncementResultDTO.setStartdate(startdate);
        adminAnnouncementResultDTO.setEnddate(enddate);
        adminAnnouncementResultDTO.setActive(active);
        adminAnnouncementResultDTO.setDisplayannouncement(displayannouncement);

        List<UUID> roles = new ArrayList<UUID>();
        for (String r : role) {
            roles.add(UUID.fromString(r));
        }

        adminAnnouncementResultDTO.setRoles(roles);
        adminAnnouncementResultDTO.setUsername(defaultUsernameService.getUsername());
        adminAnnouncementService.submitAnnouncement(adminAnnouncementResultDTO);

        return "redirect:/admin/announcement";

    }

    @RequestMapping("/announcement/edit/{id}")
    public String editAnnouncement(ModelMap model, @PathVariable("id") String id) {

        AdminAnnouncementResultDTO adminAnnouncementResultDTO = new AdminAnnouncementResultDTO();
        adminAnnouncementResultDTO.setAnnouncementId(id);
        List<Role> roleList = adminAnnouncementService.getRole();
        adminAnnouncementResultDTO.setRoleList(roleList);
        adminAnnouncementService.getAnnouncementDetails(adminAnnouncementResultDTO);

        model.addAttribute("adminAnnouncementResultDTO", adminAnnouncementResultDTO);
        return "role/admin/announcement/edit.html";

    }

    @RequestMapping("/announcement/update/{announcementId}")
    public String updateAnnouncement(@PathVariable("announcementId") String announcementId,
            @RequestParam("subject") String subject, @RequestParam("message") String message,
            @RequestParam("startdate") String startdate, @RequestParam("enddate") String enddate,
            @RequestParam("role[]") String[] role, @RequestParam(required = false, value = "active") String active,
            @RequestParam(required = false, value = "displayannouncement") String displayannouncement)
            throws ParseException {

        AdminAnnouncementResultDTO adminAnnouncementResultDTO = new AdminAnnouncementResultDTO();
        adminAnnouncementResultDTO.setAnnouncementId(announcementId);
        adminAnnouncementResultDTO.setSubject(subject);
        adminAnnouncementResultDTO.setMessage(message);
        adminAnnouncementResultDTO.setStartdate(startdate);
        adminAnnouncementResultDTO.setEnddate(enddate);
        adminAnnouncementResultDTO.setActive(active);
        adminAnnouncementResultDTO.setDisplayannouncement(displayannouncement);

        List<UUID> roles = new ArrayList<UUID>();
        for (String r : role) {
            roles.add(UUID.fromString(r));
        }

        adminAnnouncementResultDTO.setRoles(roles);
        adminAnnouncementResultDTO.setUsername(defaultUsernameService.getUsername());
        adminAnnouncementService.updateAnnouncement(adminAnnouncementResultDTO);

        return "redirect:/admin/announcement";

    }

    @RequestMapping("/announcement/delete/{announcementId}")
    public String deleteAnnouncement(@PathVariable("announcementId") String announcementId) {

        String returnPath = adminAnnouncementService.deleteCourse(UUID.fromString(announcementId));
        return returnPath;
    }

    /*********** Admin Session Calendar ***********/

    @RequestMapping("/session/calendar")
    public String adminCalendar(ModelMap model) {

        model.addAttribute("sessions", adminSessionCalendarService.getUnScheduledSessions());
        model.addAttribute("locations", adminSessionCalendarService.getLocations());
        log.debug(adminSessionCalendarService.getUnScheduledSessions());
        return "role/admin/session/calendar.html";
    }

    @RequestMapping(value = "/session/events", method = RequestMethod.POST)
    public @ResponseBody List<AdminSessionCalendarEventsDTO> sessionEventTimeSlots(
            AdminSessionEventsTimeSlotsView timeLine) {
        try {
            return adminSessionCalendarService.fetchEvents(timeLine.getStart(), timeLine.getEnd());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping(value = "/session/calendar/scheduleEvent", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> adminSessionCalendarEventReceive(AdminSessionTimeSlotCreateDTO sessionTimeSlot) {
        try {
            UUID result = adminSessionCalendarService.createSessionTimeSlot(sessionTimeSlot);
            return result != null ? new ResponseEntity<String>(result.toString(), HttpStatus.OK)
                    : new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/session/calendar/editEvent", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> adminSessionCalendarEventDrop(AdminSessionTimeSlotEditDTO sessionTimeSlot) {
        try {
            return adminSessionCalendarService.editSessionTimeSlot(sessionTimeSlot)
                    ? new ResponseEntity<Void>(HttpStatus.OK)
                    : new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/session/sessionAssignments", method = RequestMethod.GET)
    public ResponseEntity<?> adminSessionCalendarShiftAssignmentModal(@RequestParam String date,
            @RequestParam String start) {
        try {
            List<AdminSessionCalendarShiftAssignmentMentorsDTO> mentorList = adminSessionCalendarService
                    .getMentorsForShiftAssignments(date, start);
            return mentorList != null
                    ? new ResponseEntity<List<AdminSessionCalendarShiftAssignmentMentorsDTO>>(mentorList, HttpStatus.OK)
                    : new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/session/calendar/assignShifts", method = RequestMethod.POST)
    public ResponseEntity<?> adminSessionCalendarAssignShifts(@ModelAttribute AdminSessionCalendarShiftAssignmentDTO data) {

        try {
            boolean result = adminSessionCalendarService.assignShifts(data);
            return result ? new ResponseEntity<Void>(HttpStatus.OK) : new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);    
        }
        
    }

}
