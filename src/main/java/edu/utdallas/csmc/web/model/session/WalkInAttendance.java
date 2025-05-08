package edu.utdallas.csmc.web.model.session;

import edu.utdallas.csmc.web.model.course.Course;
import edu.utdallas.csmc.web.model.course.Section;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "walkin_attendance")
@DiscriminatorValue(value = "walkin")
public class WalkInAttendance extends Attendance implements Serializable {

    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = true, referencedColumnName = "id")
    private WalkInActivity activity;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = true, referencedColumnName = "id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = true, referencedColumnName = "id")
    private Section section;

    @Column(name = "topic", length = 32, nullable = false)
    private String topic;

    @Column(name = "feedback", length = 128, nullable = true)
    private String feedback;

}
