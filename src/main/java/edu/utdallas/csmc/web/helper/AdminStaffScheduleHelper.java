package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.dto.AdminScheduleTimesResultDTO;
import edu.utdallas.csmc.web.model.schedule.Shift;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.List;

@Log4j2
public class AdminStaffScheduleHelper {
    public void schduleTimesHelper(final List<AdminScheduleTimesResultDTO> listScheduleTimes, List<Shift> shifts) {
        HashMap<Integer, String> dayMap = new HashMap<>();
        dayMap.put(0, "Sunday");
        dayMap.put(1, "Monday");
        dayMap.put(2, "Tuesday");
        dayMap.put(3, "Wednesday");
        dayMap.put(4, "Thursday");
        dayMap.put(5, "Friday");
        dayMap.put(6, "Saturday");

        for(int i=0; i<shifts.size(); i++){
            for(int j=0; j< shifts.get(i).getSubjects().size(); j++){
                AdminScheduleTimesResultDTO shiftSchedule= new AdminScheduleTimesResultDTO();
                shiftSchedule.setDay(dayMap.get(shifts.get(i).getDay()));
                shiftSchedule.setStartTime(shifts.get(i).getStartTime());
                shiftSchedule.setEndTime(shifts.get(i).getEndTime());
                shiftSchedule.setSubject(shifts.get(i).getSubjects().get(j));
                shiftSchedule.setMaxMentors(shifts.get(i).getSubjects().get(j).getMaxMentors());
                listScheduleTimes.add(shiftSchedule);
            }
        }

    }
}
