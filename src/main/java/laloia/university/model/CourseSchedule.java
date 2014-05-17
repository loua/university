package laloia.university.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: CourseSchedule
 *
 */
@Entity
public class CourseSchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;
    private Course course;
    private String sectionId;
    private List<MeetingTime> meetingTimes;

    public CourseSchedule() {
        super();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String section) {
        this.sectionId = section;
    }

    @ElementCollection
    public List<MeetingTime> getMeetingTimes() {
        return meetingTimes;
    }

    public void setMeetingTimes(List<MeetingTime> meetingTimes) {
        this.meetingTimes = meetingTimes;
    }

}
