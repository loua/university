package laloia.university.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.TableGenerator;

@Entity
@NamedQuery(name = "FindDepartmentAndFetchCourses", query = "select d from Department d join fetch d.courses where d.id = :deptId")
public class Department {

    private long id;
    private String name;
    private String code;
    private List<Course> courses;

    public Department() {
        courses = new ArrayList<Course>();
    }

    @TableGenerator(name = "DEPT", table = "ID_GEN", pkColumnName = "NAME", pkColumnValue = "Dept", valueColumnName = "LAST_ID", initialValue = 100)
    @Id
    @GeneratedValue(generator = "DEPT", strategy = GenerationType.TABLE)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    // This is the inverse (non-owning) side of the relationship
    // mapped by Course.department
    @OneToMany(mappedBy = "department")
    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public void add(Course course) {
        courses.add(course);
        course.setDepartment(this);
    }

}
