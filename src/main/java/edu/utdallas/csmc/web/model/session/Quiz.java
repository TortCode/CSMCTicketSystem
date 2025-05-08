package edu.utdallas.csmc.web.model.session;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "quiz")
//    FIXME: @DiscriminatorValue not present in old code so not added here. Can add: @DiscriminatorValue(value = "quiz")
@DiscriminatorValue(value = "quiz")
public class Quiz extends Session implements Serializable {

    @OneToOne(mappedBy = "quiz", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private QuizTimeSlot timeSlot;

    @OneToMany(mappedBy = "quiz", cascade = {CascadeType.PERSIST})
    private List<QuizAttendance> attendances;

}
