package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.AdminSectionResultDTO;
import edu.utdallas.csmc.web.dto.AdminSectionRosterInfoDTO;
import edu.utdallas.csmc.web.helper.AdminSectionHelper;
import edu.utdallas.csmc.web.model.course.Course;
import edu.utdallas.csmc.web.model.course.Department;
import edu.utdallas.csmc.web.model.course.Section;
import edu.utdallas.csmc.web.model.misc.Semester;
import edu.utdallas.csmc.web.model.user.Role;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.repository.*;
import lombok.extern.flogger.Flogger;
import lombok.extern.log4j.Log4j2;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;

@Service
@Log4j2
public class AdminSectionService {

    @Autowired
    public SectionRepository sectionRepository;

    @Autowired
    public CourseRepository courseRepository;

    @Autowired
    public SemesterRepository semesterRepository;

    @Autowired
    public RoleRepository roleRepository;

    @Autowired
    public UserRepository userRepository;

    private AdminSectionHelper adminSectionHelper = new AdminSectionHelper();

    public void buildSection(String cnumberID, String secNumber, String semesterID, String instrID, String ta,
            String adminNotes) {
        try {
            Course course = courseRepository.getCourse(UUID.fromString(cnumberID));
            Semester semester = null;
            if (semesterID != null && semesterID != "") {
                semester = semesterRepository.getSemester(UUID.fromString(semesterID));
            } else {
                throw new InvalidPropertiesFormatException(semesterID);
            }
            List<User> instList = new ArrayList<>();
            User user = null;
            if (instrID != null && instrID != "") {
                user = userRepository.getUser(UUID.fromString(instrID));
                instList.add(user);
            }
            DefaultUsernameService defaultUsernameService = new DefaultUsernameService();
            // Optional<User> createdBy =
            // userRepository.findByUsername(defaultUsernameService.getUsername());
            User createdBy = userRepository.getUser(defaultUsernameService.getUsername());
            Section section = new Section();
            adminSectionHelper.buildSection(section, course, semester, instList, secNumber, adminNotes, createdBy);
            sectionRepository.save(section);
        } catch (InvalidPropertiesFormatException exception) {
            exception.printStackTrace();
            log.error("Please include a valid Semester name. For Ex. 2020 Spring");
        }

    }

    public List<Course> getCourseList() {
        return courseRepository.findCourseBySupported(true);
    }

    public List<Semester> getSemesterList() {
        return semesterRepository.findSemesterList();
    }

    public List<User> getInstructors() {
        Role role = roleRepository.getRole("Instructor".toLowerCase());
        List<User> instructors = userRepository.getUserByRole(role.getId());
        instructors.sort(Comparator.comparing(User::getFirstName));
        return instructors;
        //return userRepository.getUserByRole(role.getId());
    }

    public Section getSection(String id) {
        return sectionRepository.getSectionFromId(UUID.fromString(id));
    }

    public List<AdminSectionResultDTO> getSectionList() {
        adminSectionHelper.printSectionList(sectionRepository.getSectionList());
        List<AdminSectionResultDTO> adminSectionResultDTOList = new ArrayList<>();
        adminSectionHelper.buildSectionResultDTO(adminSectionResultDTOList, sectionRepository.getSectionList());
        return adminSectionResultDTOList;
    }

    public List<Section> getRawSectionList() {
        return sectionRepository.getSectionList();
    }

    public void saveEditedSection(String sectionId, String couseID, String secNumber, String semesterID, String IntsID,
            String tA, String desc) {
        Section section = sectionRepository.getSectionFromId(UUID.fromString(sectionId));
        if (section != null) {
            Course course = courseRepository.getCourse(UUID.fromString(couseID));
            if (course != null) {
                Semester semester = semesterRepository.getSemester(UUID.fromString(semesterID));
                if (semester != null) {
                    User inst = userRepository.getUser(UUID.fromString(IntsID));
                    if (inst != null) {
                        List<User> instrs = new ArrayList<>();
                        instrs.add(inst);
                        section.setCourse(course);
                        section.setNumber(secNumber);
                        section.setSemester(semester);
                        section.setInstructors(instrs);
                        section.setDescription(desc);
                        sectionRepository.save(section);
                    } else {
                        log.error("Invalid instructor id");
                    }
                } else {
                    log.error("invalid Semester");
                }
            } else {
                log.error("Invalid Course");
            }
        } else {
            log.error("Invalid Section ID");
        }
        return;
    }

    public void getAllSectionIDs(MultipartFile rosterFile) {
        HashSet<String> hsAllSection = new HashSet<>();
        HashMap<String, List<User>> hm = new HashMap<>();

        try {
            HSSFWorkbook workbook = new HSSFWorkbook(rosterFile.getInputStream());
            HSSFSheet worksheet = workbook.getSheetAt(0);
            ArrayList<Section> allSections = (ArrayList<Section>) sectionRepository.getSectionList();
            for (Section s : allSections) {
                hsAllSection.add(s.getNumber());
            }
            for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
                HSSFRow row = worksheet.getRow(i);
                String fileSection;
                if (row.getCell(7).toString().contains(".")) {
                    fileSection = row.getCell(7).toString().replace(".", ",").split(",")[0];
                } else {
                    fileSection = row.getCell(7).toString();
                }

                String fileCourseNumber;
                if (row.getCell(6).toString().contains(".")) {
                    fileCourseNumber = row.getCell(6).toString().replace(".", ",").split(",")[0];
                } else {
                    fileCourseNumber = row.getCell(6).toString();
                }

                String filenetId = row.getCell(3).toString().split("@")[0];
                if (hm.containsKey(fileSection)) {
                    hm.get(fileSection).add(userRepository.getUser(filenetId));
                } else {
                    List<User> ll = new ArrayList<>();
                    ll.add(userRepository.getUser(filenetId));
                    hm.put(fileSection, ll);
                }
                String season = "";
                int year = 0;
                if (row.getCell(10) != null) {
                    if (row.getCell(10).getStringCellValue().contains(" ")) {
                        season = row.getCell(10).getStringCellValue().split(" ")[1];
                        year = Integer.parseInt(row.getCell(10).getStringCellValue().split(" ")[0]);
                    }
                }
                if (season == null && season != "") {
                    log.error("Please include Semester details. For ex. 2020 Spring");
                    break;
                }
                // For Instructor ID:
                // userRepository.getUser(row.getCell(11).getStringCellValue()).getId().toString()
                if (!hsAllSection.contains(fileSection)) {
                    buildSection(courseRepository.getCourseID(fileCourseNumber), fileSection,
                            semesterRepository.getSemesterID(season, year), "", "", "");
                }
            }
            for (String sectionId : hm.keySet()) {
                Section section = sectionRepository.getSectionFromNumber(sectionId);
                section.setStudents(hm.get(sectionId));
                sectionRepository.save(section);
            }

        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    public void saveRoster(UUID sectionID, MultipartFile rosterFile) {

        Section section = sectionRepository.findById(sectionID).get();
        try {
            List<AdminSectionRosterInfoDTO> infoList = parseFile(rosterFile);
            Semester activeSemester = semesterRepository.findSemesterByActive(true).get();

            for (AdminSectionRosterInfoDTO info : infoList) {
                UUID courseID = UUID.fromString(courseRepository.getCourseID(info.getCourseNumber()));
                Course course = courseRepository.findById(courseID).get();

                Optional<Section> sectionExist = sectionRepository.findSectionByCourseAndNumber(course, activeSemester,
                        info.getSectionNumber());
                Section currSection;

                if (sectionExist.isPresent()) {
                    currSection = sectionExist.get();
                    if (currSection == section) {
                        Optional<User> studentExist = userRepository.findByUsername(info.getNetId());
                        User student;

                        List<Role> role = new ArrayList<>();
                        role.add(roleRepository.getRole("student"));

                        if (studentExist.isPresent()) {
                            student = studentExist.get();
                            student.setRoles(role);
                        } else {
                            student = new User();
                            student.setFirstName(info.getFirstName());
                            student.setLastName(info.getLastName());
                            student.setUsername(info.getNetId());
                            student.setRoles(role);
                        }

                        userRepository.save(student);

                        if (!currSection.getStudents().contains(student))
                            currSection.getStudents().add(student);
                        sectionRepository.save(currSection);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveRoster(MultipartFile rosterFile) {
        try {
            List<AdminSectionRosterInfoDTO> infoList = parseFile(rosterFile);
            Semester activeSemester = semesterRepository.findSemesterByActive(true).get();

            for (AdminSectionRosterInfoDTO info : infoList) {
                log.info(info.getCourseNumber());
                log.info(courseRepository.getCourseID(info.getCourseNumber()));
                UUID courseID = UUID.fromString(courseRepository.getCourseID(info.getCourseNumber()));
                Course course = courseRepository.findById(courseID).get();

                Optional<Section> sectionExist = sectionRepository.findSectionByCourseAndNumber(course, activeSemester,
                        info.getSectionNumber());
                Section section;

                if (sectionExist.isPresent()) {
                    section = sectionExist.get();
                } else {
                    section = new Section();
                    section.setCourse(course);
                    section.setNumber(info.getSectionNumber());
                    section.setSemester(activeSemester);
                    section.setStudents(new ArrayList<>());
                    section.setCreatedOn(new Date());
                    section.setLastModifiedOn(new Date());
                    sectionRepository.save(section);
                }

                Optional<User> studentExist = userRepository.findByUsername(info.getNetId());
                User student;

                List<Role> role = new ArrayList<>();
                role.add(roleRepository.getRole("student"));

                if (studentExist.isPresent()) {
                    student = studentExist.get();
                    student.setRoles(role);
                    student.setFirstName(info.getFirstName());
                    student.setLastName(info.getLastName());
                    student.setUsername(info.getNetId());
                } else {
                    student = new User();
                    student.setFirstName(info.getFirstName());
                    student.setLastName(info.getLastName());
                    student.setUsername(info.getNetId());
                    student.setRoles(role);
                }

                userRepository.save(student);

                if (!section.getStudents().contains(student))
                    section.getStudents().add(student);
                sectionRepository.save(section);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<AdminSectionRosterInfoDTO> parseFile(MultipartFile rosterFile) {

        List<AdminSectionRosterInfoDTO> infoList = new ArrayList<>();

        try {
            HSSFWorkbook workbook = new HSSFWorkbook(rosterFile.getInputStream());
            log.info(workbook);
            HSSFSheet worksheet = workbook.getSheetAt(0);
            log.info(worksheet);
            Iterator rows = worksheet.rowIterator();
            boolean first = true;

            while (rows.hasNext()) {
                if (first) {
                    rows.next();
                    first = false;
                    continue;
                }

                HSSFRow row = (HSSFRow) rows.next();
                log.info(row);
                AdminSectionRosterInfoDTO studentInfo = new AdminSectionRosterInfoDTO();

                String name = row.getCell(2).toString();
                studentInfo.setLastName(name.split(",")[0]);
                studentInfo.setFirstName(name.split(",")[1]);
                log.info(name);

                String netId = row.getCell(3).toString();
                log.info(netId);
                studentInfo.setNetId(netId.substring(0, netId.indexOf("@")));

                String department = row.getCell(4).toString();
                studentInfo.setDepartment(department);

                String courseNumber = row.getCell(5).toString();
                log.info(courseNumber);
                studentInfo.setCourseNumber(courseNumber);

                String sectionNumber = row.getCell(6).toString();
                log.info(sectionNumber);
                studentInfo.setSectionNumber(sectionNumber);

                infoList.add(studentInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info(infoList);
        return infoList;
    }

    public void deleteSection(String sectionID) {
        Section section = sectionRepository.getSectionFromId(UUID.fromString(sectionID));
        if (section != null) {
            sectionRepository.delete(section);
        } else {
            log.error("Invalid Section ID");
        }
    }
}
