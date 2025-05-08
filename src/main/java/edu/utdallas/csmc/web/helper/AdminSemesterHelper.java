package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.dto.AdminSemesterSeasonsDTO;
import edu.utdallas.csmc.web.dto.AdminSemeterResultDTO;
import edu.utdallas.csmc.web.model.misc.Semester;
import edu.utdallas.csmc.web.model.misc.SemesterSeason;
import edu.utdallas.csmc.web.repository.SemesterRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import javax.management.StandardEmitterMBean;
import javax.persistence.criteria.CriteriaBuilder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Log4j2
public class AdminSemesterHelper {

    @Autowired
    private SemesterRepository semesterRepository;

    public void buildSemesterResultDTOList(final List<AdminSemeterResultDTO> adminSemeterResultDTOList ,List<Semester> semesterList){

        for(Semester semester:semesterList){
            AdminSemeterResultDTO adminSemeterResultDTO = new AdminSemeterResultDTO();
            adminSemeterResultDTO.setSemesterID(semester.getId());
            adminSemeterResultDTO.setSeason(semester.getSeason());
            adminSemeterResultDTO.setYear(semester.getYear());
            adminSemeterResultDTO.setStartDate(semester.getStartDate());
            adminSemeterResultDTO.setEndDate(semester.getEndDate());
            adminSemeterResultDTO.setActive(semester.isActive());
            adminSemeterResultDTO.setSemesterIDString(semester.getId().toString());

            adminSemeterResultDTOList.add(adminSemeterResultDTO);
        }

//        return adminSemeterResultDTOList;
    }

    public void buildSemesterSeasonsDTOList(final List<AdminSemesterSeasonsDTO> adminSemesterSeasonsDTOList, List<SemesterSeason> semesterSeasons){
        AdminSemesterSeasonsDTO adminSemesterSeasonsDTO;
        adminSemesterSeasonsDTO = new AdminSemesterSeasonsDTO();
        adminSemesterSeasonsDTO.setSeasonName("Please select a season");
        adminSemesterSeasonsDTO.setSeasonID(new UUID(0,0));
        adminSemesterSeasonsDTOList.add(adminSemesterSeasonsDTO);

        for(SemesterSeason semesterSeason:semesterSeasons){
            adminSemesterSeasonsDTO = new AdminSemesterSeasonsDTO();
            adminSemesterSeasonsDTO.setSeasonName(semesterSeason.getName());
            adminSemesterSeasonsDTO.setSeasonID(semesterSeason.getId());
            adminSemesterSeasonsDTOList.add(adminSemesterSeasonsDTO);
        }
    }

    public Date getDatefromString(String dateString) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
    }

    public  boolean validateDetails(String season, String year, Date startDate, Date endDate){
        int yr=0;
        int comparedate = endDate.compareTo(startDate);
        if (comparedate != 1){
            return false;
        }

        try {
             yr = Integer.parseInt(year);
        }catch (Exception e){
            return false;
        }

        if(startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() != yr){
            return false;
        }

        if(endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() != yr){
            return false;
        }

        return true;
    }

    public boolean validateSemesters(){
        return true;
    }

    public boolean buildSemesterModel(final Semester newSemester, String season, String year, String startDateString, String endDateString, Boolean isSupported) throws ParseException {
        Date startDate = getDatefromString(startDateString);
        Date endDate = getDatefromString(endDateString);

        if(validateDetails(season,year,startDate,endDate )){
            newSemester.setActive(isSupported);
            newSemester.setSeason(season);
            newSemester.setYear(Integer.parseInt(year));
            newSemester.setStartDate(startDate);
            newSemester.setEndDate(endDate);
            newSemester.setSchedule(null);
            return true;
        }else {
            log.info("Not all fields are valid : ");
            return false;
        }

    }

    public void setSemesterNotActive(final Semester semester){
        semester.setActive(false);
    }

    public void buildSemesterDetail(final AdminSemeterResultDTO adminSemeterResultDTO, Semester semester){
        adminSemeterResultDTO.setSeason(semester.getSeason());
        adminSemeterResultDTO.setYear(semester.getYear());
        adminSemeterResultDTO.setSemesterID(semester.getId());
        adminSemeterResultDTO.setStartDate(semester.getStartDate());
        adminSemeterResultDTO.setEndDate(semester.getEndDate());
        adminSemeterResultDTO.setActive(semester.isActive());
    }

    public boolean buildExistingSemesterModel(final Semester semester, String season, int year, Date startDate, Date endDate, boolean isActive){
        if(semester!=null){
            semester.setSeason(season);
            semester.setYear(year);
            semester.setStartDate(startDate);
            semester.setEndDate(endDate);
            semester.setActive(isActive);
            return true;
        }
        else
        {
            log.error("Invalid Semester");
            return false;
        }
    }
}
