package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.AdminSemesterSeasonsDTO;
import edu.utdallas.csmc.web.dto.AdminSemeterResultDTO;
import edu.utdallas.csmc.web.helper.AdminSemesterHelper;
import edu.utdallas.csmc.web.model.misc.Semester;
import edu.utdallas.csmc.web.model.schedule.Schedule;
import edu.utdallas.csmc.web.model.misc.SemesterSeason;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.repository.SemesterRepository;
import edu.utdallas.csmc.web.repository.ScheduleRepository;
import edu.utdallas.csmc.web.repository.SemesterSeasonRepository;
import edu.utdallas.csmc.web.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Log4j2
public class AdminSemesterService {

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private SemesterSeasonRepository semesterSeasonRepository;

    private AdminSemesterHelper adminSemesterHelper = new AdminSemesterHelper();
    @Autowired
    private UserRepository userRepository;

    public String errorMessage ="";

    public AdminSemeterResultDTO getSemesterDetails(String semesterID){
        AdminSemeterResultDTO adminSemeterResultDTO = new AdminSemeterResultDTO();
        Semester semester = semesterRepository.getSemester(UUID.fromString(semesterID));
        adminSemesterHelper.buildSemesterDetail(adminSemeterResultDTO,semester);
        return adminSemeterResultDTO;
    }

    public List<AdminSemesterSeasonsDTO> getSeasons(){
        List<AdminSemesterSeasonsDTO> adminSemesterSeasonsDTOList = new ArrayList<>();
        List<SemesterSeason> semesterSeasons = semesterRepository.findSeasons();
        if(semesterSeasons.size()==0)
        {
            createSemesterSeasons();
            semesterSeasons = semesterRepository.findSeasons();
        }
        adminSemesterHelper.buildSemesterSeasonsDTOList(adminSemesterSeasonsDTOList,semesterSeasons);
        return adminSemesterSeasonsDTOList;
    }

    public List<String> getSeasonsNames(){
        List<String> seasonsNames = new ArrayList<>();
        seasonsNames.add("Please Select a Season");
        seasonsNames.add("Spring");
        seasonsNames.add("Summer");
        seasonsNames.add("Fall");
        return seasonsNames;
    }

    public void createSemesterSeasons(){
        SemesterSeason semesterSeasonSpring = new SemesterSeason();
        semesterSeasonSpring.setName("Spring");
        semesterSeasonSpring.setPrefix("Spring");
        semesterSeasonRepository.save(semesterSeasonSpring);
    }

    public List<AdminSemeterResultDTO> getSemesterList(){
        List<AdminSemeterResultDTO> adminSemeterResultDTOList = new ArrayList<>();
        adminSemesterHelper.buildSemesterResultDTOList(adminSemeterResultDTOList,semesterRepository.findSemesterList());
        return adminSemeterResultDTOList;
    }


    public Date getDatefromString(String dateString) throws ParseException {
        return adminSemesterHelper.getDatefromString(dateString);
    }

    public void saveEditedSemester(String semesterID, String season, String year, String startDate, String endDate, boolean isActive) throws ParseException {
        Semester semester = semesterRepository.getSemester(UUID.fromString(semesterID));

        if(isActive){
            List<Semester> semesters = semesterRepository.findSemesterList();
            for (Semester semester1 : semesters){
                adminSemesterHelper.setSemesterNotActive(semester1);
                semesterRepository.save(semester1);
            }
        }

        if(adminSemesterHelper.buildExistingSemesterModel(semester,season,Integer.parseInt(year),adminSemesterHelper.getDatefromString(startDate),adminSemesterHelper.getDatefromString(endDate),isActive)){
            List<Semester> semester1 = semesterRepository.getSemesterFromSeasonandYear(season,Integer.parseInt(year));
            if(semester1.size()<=1){
                semesterRepository.save(semester);
            }else {
                log.error("Semester already exists. ");
            }
        }else {
            log.error("Semester Not saved.");
        }
        return;
    }

    public void saveSemester(String season, String year, String startDate, String endDate, boolean isActive) throws ParseException {
        Semester newSemester = new Semester();
//        SemesterSeason semesterSeason = semesterSeasonRepository.findSemesterSeasonbyId(UUID.fromString(season));
//        adminSemesterHelper.buildSemesterModel(newSemester,semesterSeason.getName(),year,startDate,endDate,isSupported);
        if(isActive){
            List<Semester> semesters = semesterRepository.findSemesterList();
            for (Semester semester1 : semesters){
                adminSemesterHelper.setSemesterNotActive(semester1);
                semesterRepository.save(semester1);
            }
        }
        if (adminSemesterHelper.buildSemesterModel(newSemester,season,year,startDate,endDate,isActive))
        {
            List<Semester> semester= semesterRepository.getSemesterFromSeasonandYear(season,Integer.parseInt(year));

            if(semester.size()==0){
                semesterRepository.save(newSemester);
                Schedule newSchedule = new Schedule();
                newSchedule.setSemester(newSemester);
                scheduleRepository.save(newSchedule);
            }else {
                log.error("Semester already exists . ");
            }
        }else {
            log.error("semester not saved");
        }

        return;
    }


    public void deleteSemester(String semesterId){
        Semester semester = semesterRepository.getSemester(UUID.fromString(semesterId));
        try {
            semesterRepository.delete(semester);
        }catch (Exception e){
            log.error(e.getMessage());
            log.info("This semester is associated with schedule or other table and hence cannot be deleted. ");

        }finally {
            return;
        }
    }

    public String getStartDateFromSeasonAndYear(String season, Integer year) {
        return semesterRepository.getSemesterStartDateFromSeasonAndYear(season, year);
    }

    public String getEndDateFromSeasonAndYear(String season, Integer year) {
        return semesterRepository.getSemesterEndDateFromSeasonAndYear(season, year);
    }

    public Map<Integer, Map<String, String>> getWeeklyDatesGivenSemesterDates(Date semesterStart, Date semesterEnd) {
        Map<Integer, Map<String, String>> weekDatesMap = new HashMap<>();
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(semesterStart);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(semesterEnd);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        int weekNumber = 1;
        while (startCalendar.getTime().before(semesterEnd)) {
            Map<String, String> weekDates = new HashMap<>();

            String startDateString = dateFormat.format(startCalendar.getTime());
            String endDateString = dateFormat.format(getWeekEndDate(startCalendar, endCalendar).getTime());

            weekDates.put("start", startDateString);
            weekDates.put("end", endDateString);

            weekDatesMap.put(weekNumber, weekDates);

            startCalendar.add(Calendar.DATE, 7);
            startCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            startCalendar.add(Calendar.DATE, 1);

            weekNumber++;
        }

        return weekDatesMap;
    }

    private static Calendar getWeekEndDate(Calendar startCalendar, Calendar endCalendar) {
        Calendar weekEnd = (Calendar) startCalendar.clone();
        weekEnd.add(Calendar.DATE, 6);
        if (weekEnd.after(endCalendar)) {
            weekEnd = (Calendar) endCalendar.clone();
        }
        return weekEnd;
    }
}
