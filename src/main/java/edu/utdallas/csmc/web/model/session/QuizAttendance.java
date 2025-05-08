package edu.utdallas.csmc.web.model.session;

import edu.utdallas.csmc.web.model.course.Course;
import edu.utdallas.csmc.web.model.course.Section;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

//    FIXME: Original Database id -> (attendance table) id. Current Implementation shows id -> (session_attendance table) id. Check if change needed.
@Getter
@Setter
@Entity
@Table(name="quiz_attendance")
@DiscriminatorValue(value = "quiz")
public class QuizAttendance extends SessionAttendance implements Serializable {

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = true, referencedColumnName = "id")
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = true, referencedColumnName = "id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = true, referencedColumnName = "id")
    private Section section;

    @Column(name = "feedback",length = 128, nullable = true)
    private String feedback;

}
