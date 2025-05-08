package edu.utdallas.csmc.web.model.session;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name="quiz_timeslot")
@DiscriminatorValue(value="quiztimeslot")
public class QuizTimeSlot extends TimeSlot implements Serializable {

    @OneToOne
    @JoinColumn(name = "quiz_id", referencedColumnName = "id")
    private Quiz quiz;

    public boolean hasAttended(String username){
        if(!this.getAttendances().isEmpty()){
            for(SessionAttendance currentAttendance: this.getAttendances()){
                if(currentAttendance.getUser().getUsername().equals(username)){
                    return true;
                }
            }
        }
        return false;
    }
}
