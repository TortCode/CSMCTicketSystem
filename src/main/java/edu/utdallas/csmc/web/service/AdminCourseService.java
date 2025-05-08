package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.AdminCourseDetailsResultDTO;
import edu.utdallas.csmc.web.dto.AdminCoursesResultDTO;
import edu.utdallas.csmc.web.helper.AdminCourseHelper;
import edu.utdallas.csmc.web.model.course.Course;
import edu.utdallas.csmc.web.model.course.Department;
import edu.utdallas.csmc.web.model.misc.Semester;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.repository.CourseRepository;
import edu.utdallas.csmc.web.repository.DepartmentRepository;
import edu.utdallas.csmc.web.repository.SemesterRepository;
import edu.utdallas.csmc.web.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * This class defines all the service functions related to the Admin Courses View.
 */
@Service
@Log4j2
public class AdminCourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminSemesterService adminSemesterService;

    @Autowired
    private SemesterRepository semesterRepository;

    AdminCourseHelper adminCourseHelper = new AdminCourseHelper();

    private final JdbcTemplate jdbcTemplate;

    public AdminCourseService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * This function fetches all the details related to the Courses supported by the CSMC.
     */
    public List<AdminCoursesResultDTO> getSupportedCourseDetails() {
        List<AdminCoursesResultDTO> adminSupportedCoursesResultDTOList = new ArrayList<>();
        List<Course> courseList = courseRepository.findCourseBySupported(true);
        adminCourseHelper.buildAdminCourseResultDTOList(adminSupportedCoursesResultDTOList, courseList);

        return adminSupportedCoursesResultDTOList;
    }

    /**
     * This function fetches all the details related to the Courses not supported by the CSMC.
     */
    public List<AdminCoursesResultDTO> getUnsupportedCourseDetails() {
        List<AdminCoursesResultDTO> adminUnsupportedCoursesResultDTOList = new ArrayList<>();
        List<Course> courseList = courseRepository.findCourseBySupported(false);
        adminCourseHelper.buildAdminCourseResultDTOList(adminUnsupportedCoursesResultDTOList, courseList);

        return adminUnsupportedCoursesResultDTOList;
    }

    /**
     * This function fetches all the departments with CSMC that a new course can belong to.
     */
    public List<String> getDepartmentsForNewCourse() {
        List<String> departments = new ArrayList<>();
        List<Department> departmentList = departmentRepository.findAllByOrderByName();
        adminCourseHelper.buildDepartmentsList(departments, departmentList);

        return departments;
    }

    /**
     * This function adds the new course based on the details received.
     */
    public void submitCourseDetails(AdminCourseDetailsResultDTO adminCourseDetailsResultDTO) {

        Optional<Department> department = departmentRepository.findDepartmentByName(adminCourseDetailsResultDTO.getDepartmentName());
        Optional<User> user = userRepository.findByUsername(adminCourseDetailsResultDTO.getUsername());
        Course newCourse = adminCourseHelper.buildNewCourseModel(department, user, adminCourseDetailsResultDTO);

        courseRepository.save(newCourse);
    }

    /**
     * This function fetches the details about the course from the database to show to the admin for editing.
     */
    public void getCourseDetails(final AdminCourseDetailsResultDTO adminCourseDetailsResultDTO) {

        Optional<Course> course = courseRepository.findById(UUID.fromString(adminCourseDetailsResultDTO.getCourseId()));
        if (course.isPresent()) {
            Course currentCourse = course.get();
            adminCourseHelper.buildAdminCourseDetailsFromCourse(adminCourseDetailsResultDTO, currentCourse);
        } else {
            log.info("Course Not Found!");
        }
    }

    /**
     * This function updates the details about the course in the database.
     */
    public void updateCourseDetails(AdminCourseDetailsResultDTO adminCourseDetailsResultDTO) {
        Optional<Course> course = courseRepository.findById(UUID.fromString(adminCourseDetailsResultDTO.getCourseId()));
        if (course.isPresent()) {
            Course currentCourse = course.get();
            Optional<User> user = userRepository.findByUsername(adminCourseDetailsResultDTO.getUsername());
            Optional<Department> department = departmentRepository.findDepartmentByName(adminCourseDetailsResultDTO.getDepartmentName());

            adminCourseHelper.buildUpdatedCourseFromDTO(currentCourse, adminCourseDetailsResultDTO, user, department);

            // Save the updated model back to the database
            courseRepository.save(currentCourse);

        } else {
            log.info("Course not Found!");
        }
    }

    /**
     * This function deletes the new course based on the input id if it is not associated with any sections through Foreign Key.
     */
    public String deleteCourse(UUID courseId) {

        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isPresent()) {
            try {
                courseRepository.delete(course.get());

            } catch (ConstraintViolationException ex) {
                log.error(ex);
                // TODO: See how this can be shown on Front End
                log.info("This Course has associated data(course number) with Section and cannot be deleted.");
                return "redirect:/admin/course/edit/" + courseId;
            }
        } else {
            log.info("Course not found for id" + courseId.toString());
        }

        return "redirect:/admin/course";
    }

    public Map<String, Integer> getCourseVisitsDetailsForLastFiveSemesters() throws ParseException {
        Map<String, Integer> courseVisits = new LinkedHashMap<>();
        List<Semester> semesters = semesterRepository.findLastFiveSemesters();
        Collections.reverse(semesters);
        for(Semester semester : semesters) {
            String semesterDisplay = semester.getSeason()+" "+semester.getYear();
            String[] sem = semesterDisplay.split(" ");
            String season = sem[0];
            String year = sem[1];

            Integer formattedYear = Integer.parseInt(year);

            String startDate = adminSemesterService.getStartDateFromSeasonAndYear(season, formattedYear);
            String endDate = adminSemesterService.getEndDateFromSeasonAndYear(season, formattedYear);

            // Define the date-time format for input and output
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Parse the input date-time string
            Date startDate_date = inputDateFormat.parse(startDate);
            Date endDate_date = inputDateFormat.parse(endDate);

            // Parse the input date and format it in the desired output format
            String finalStartDate = outputDateFormat.format(startDate_date);
            String finalEndDate = outputDateFormat.format(endDate_date);

            int courseVisit = courseRepository.getCourseVisitsForCourseForRequestedSemester(finalStartDate, finalEndDate);
            courseVisits.put(semesterDisplay, courseVisit);
        }
        return courseVisits;
    }

    public TreeMap<String, Integer> getCourseVisitsDetailsForDateRange(String formattedStartDate, String formattedEndDate) {
        TreeMap<String, Integer> courseVisits = new TreeMap<>();
        // Define a DateTimeFormatter for both input and output formats
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Parse the input date and format it in the desired output format
        String startDate = LocalDate.parse(formattedStartDate, inputFormatter).format(outputFormatter);
        String endDate = LocalDate.parse(formattedEndDate, inputFormatter).format(outputFormatter);

        List<CourseRepository.CourseVisitDetails> courseVisitDetails = courseRepository.getCourseVisitsForDateRange(startDate, endDate);
        for(CourseRepository.CourseVisitDetails courseVisitDetail : courseVisitDetails) {
            courseVisits.put(courseVisitDetail.getCourseNumber(), courseVisitDetail.getVisitCount());
        }

        return courseVisits;
    }

    public Map<String, Map<String, Integer>> getCourseVisitsDetailsWeeklyByDayOfWeek() throws ParseException {
        Map<String, Map<String, Integer>> totalCourseVisits = new LinkedHashMap<>();
        List<Semester> semesters = semesterRepository.findLastThreeSemesters();
        Collections.reverse(semesters);
        for(Semester semester : semesters) {
            String semesterDisplay = semester.getSeason()+" "+semester.getYear();
            String[] sem = semesterDisplay.split(" ");
            String season = sem[0];
            String year = sem[1];

            Integer formattedYear = Integer.parseInt(year);

            String startDate = adminSemesterService.getStartDateFromSeasonAndYear(season, formattedYear);
            String endDate = adminSemesterService.getEndDateFromSeasonAndYear(season, formattedYear);

            // Define the date-time format for input and output
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Parse the input date-time string
            Date startDate_date = inputDateFormat.parse(startDate);
            Date endDate_date = inputDateFormat.parse(endDate);

            // Parse the input date and format it in the desired output format
            String finalStartDate = outputDateFormat.format(startDate_date);
            String finalEndDate = outputDateFormat.format(endDate_date);
            String sql = "SELECT dayname(a.time_in) as day_of_week, COUNT(s.course_id) as visit_count FROM section s INNER JOIN section_students ss ON s.id = ss.section_id INNER JOIN attendance a ON a.user_id = ss.user_id INNER JOIN course c ON s.course_id = c.id WHERE course_id IN (SELECT id FROM course) AND (a.time_in BETWEEN '"+ finalStartDate + "' AND '" + finalEndDate + "' AND a.time_out BETWEEN '" + finalStartDate + "' AND '" + finalEndDate+"' ) OR (a.time_in BETWEEN '"+finalStartDate+ "' AND '"+ finalEndDate+"' AND a.time_out IS NULL) GROUP BY dayname(a.time_in) order by dayname(a.time_in);";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            System.out.println("rows -- "+rows);
            Map<String, Integer> courseVisitsByDayOfWeek = new HashMap<>();
            for (Map<String, Object> row : rows) {
                String dayOfWeek = (String) row.get("day_of_week");
                Integer visitCount = ((Number) row.get("visit_count")).intValue();
                System.out.println("dayOfWeek -- "+dayOfWeek);
                System.out.println("visitCount -- "+visitCount);
                courseVisitsByDayOfWeek.put(dayOfWeek, visitCount);
            }
            totalCourseVisits.put(semesterDisplay, courseVisitsByDayOfWeek);
            for (Map.Entry<String, Map<String,Integer>> entry : totalCourseVisits.entrySet()) {
                System.out.println("Key -- "+entry.getKey()+"  Value -- "+entry.getValue().size());
            }
        }
        return totalCourseVisits;
    }
}
