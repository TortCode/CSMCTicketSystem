package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.model.misc.OperationHours;
import edu.utdallas.csmc.web.model.misc.Swipe;
import edu.utdallas.csmc.web.repository.OperationHoursRepository;
import edu.utdallas.csmc.web.repository.SwipeRepository;
import edu.utdallas.csmc.web.util.DateUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class AdminHomeService {

    @Autowired
    private OperationHoursRepository operationHoursRepository;

    @Autowired
    private SwipeRepository swipeRepository;

    public ModelMap getHomeScreenDetails(ModelMap model, Date date) {
        if(date == null){
            date = new Date();
        }
        //day=0 means Sunday and day=6 means Saturday
        int day =  DateUtil.getDayOfWeek(date);

        Optional<OperationHours> operationHours = operationHoursRepository.findByDay(day);

        Time startTime = operationHours.get().getStartTime();
        Time endTime = operationHours.get().getEndTime();

        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime localStartTime = startTime.toLocalTime();
        LocalTime localEndTime = endTime.toLocalTime();

        List<Date> dates = new ArrayList<>();
        List<List<Swipe>> swipes = new ArrayList<>();
        Date start = null;
        Date end = null;

        LocalTime tempEndTime = localStartTime.plusMinutes(30);
        while(0 != tempEndTime.compareTo(localEndTime.plusMinutes(30))) {
            LocalDateTime a = LocalDateTime.of(localDate,localStartTime);
            LocalDateTime b = LocalDateTime.of(localDate,tempEndTime);

            start = Date.from(a.atZone(ZoneId.systemDefault()).toInstant());
            end = Date.from(b.atZone(ZoneId.systemDefault()).toInstant());

            dates.add(start);
            List<Swipe> s = swipeRepository.findByTime(start,end);
            swipes.add(s);

            localStartTime = tempEndTime;
            tempEndTime = tempEndTime.plusMinutes(30);
        }
        dates.add(end);
        model.addAttribute("swipes",swipes);
        model.addAttribute("keys",dates);

        return model;
    }
}
