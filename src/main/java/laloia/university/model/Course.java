package laloia.university.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

/**
 * Entity implementation class for Entity: Course
 *
 */
@Entity
public class Course {

    private long id;
    private String number;
    private String description;
    private String name;
    private int creditHours;
    private Department department;

    public Course() {
    }

    public Course(String name, String number) {
        this.name = name;
        this.number = number;
    }

    @TableGenerator(name = "COURSE", table = "ID_GEN", pkColumnName = "NAME", pkColumnValue = "Course", valueColumnName = "LAST_ID", initialValue = 100)
    @Id
    @GeneratedValue(generator = "COURSE", strategy = GenerationType.TABLE)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCreditHours() {
        return this.creditHours;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }

    // The owning side of this relationship
    // Default fetch type is eager
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
