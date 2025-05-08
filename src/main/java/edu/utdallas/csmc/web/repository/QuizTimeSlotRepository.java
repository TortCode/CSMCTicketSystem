package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.session.QuizTimeSlot;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface QuizTimeSlotRepository extends TimeSlotRepository<QuizTimeSlot> {

    @Query("select st from QuizTimeSlot st where st.startTime between :startOfDay and :endOfDay order by st.startTime asc ")
    List<QuizTimeSlot> findQuizTimeSlotsForEntireDayByDate(@Param("startOfDay") Date startOfDay,
            @Param("endOfDay") Date endOfDay);

}
