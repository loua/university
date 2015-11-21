package laloia.university.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.persistence.TypedQuery;

import org.dbunit.Assertion;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.util.fileloader.DataFileLoader;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DepartmentTest extends AbstractJPATest {

    @Test
    public void persistDepartment() throws Exception {
        deleteAll("/departmentTestDataSet.xml");
        Department dept = createDepartment();

        persistAndFlush(dept);

        DataFileLoader loader = new FlatXmlDataFileLoader();
        IDataSet expectedDataSet = loader
                .load("/expectedDepartmentDataSet.xml");
        ITable expectedTable = expectedDataSet.getTable("DEPARTMENT");

        IDatabaseConnection dbConnection = getActiveDbUnitConnection();
        IDataSet databaseDataSet = dbConnection.createDataSet();
        ITable actualTable = databaseDataSet.getTable("DEPARTMENT");
        ITable filteredTable = DefaultColumnFilter.includedColumnsTable(actualTable, expectedTable
                .getTableMetaData().getColumns());

        // Assert actual database table match expected table
        Assertion.assertEquals(expectedTable, filteredTable);
    }

    @Test
    public void persistCourseWithoutDepartment() throws Exception {
        Course course = new Course("Introduction to Computer Science", "100");

        persistAndFlush(course);

        assertNull(course.getDepartment());
        assertTrue("course.id > 0", course.getId() > 0);
    }

    @Test
    public void persistCourseWithNewDepartment() throws Exception {
        Course course = new Course("Intro to Computer Science", "100");

        course.setDepartment(createDepartment());

        persistAndFlush(course);

        assertNotNull("course.department != null", course.getDepartment());
        assertTrue("course.id > 0", course.getId() > 0);
    }

    @Test
    public void persistCourseWithExistingDepartment() throws Exception {
        cleanInsertDataSet("/departmentTestDataSet.xml");
        Course course = new Course("Introduction to Computer Science", "100");
        final long expectedDeptId = 1L;
        Department department = em.find(Department.class, expectedDeptId);

        course.setDepartment(department);
        persistAndFlush(course);

        em.clear();
        Course newCourse = em.find(Course.class, course.getId());
        assertEquals(expectedDeptId, newCourse.getDepartment().getId());
    }

    @Test
    public void findCourse() throws Exception {
        cleanInsertDataSet("/departmentTestDataSet.xml");
        final long expectedCourseId = 1L;

        Course course = em.find(Course.class, expectedCourseId);

        assertNotNull("course.department != null", course.getDepartment());
    }

    @Test
    public void findCourseWithDepartment() throws Exception {
        cleanInsertDataSet("/departmentTestDataSet.xml");
        final long expectedCourseId = 2;

        Course course = em.find(Course.class, expectedCourseId);

        assertFalse(punitUtil.isLoaded(course, "department"));
    }

    @Test
    public void findDepartmentWithoutCourses() throws Exception {
        cleanInsertDataSet("/departmentTestDataSet.xml");
        final long expectedDeptId = 1L;

        Department department = em.find(Department.class, expectedDeptId);
        em.detach(department);

        assertNotNull("department", department);
        assertEquals("department.id", expectedDeptId, department.getId());
        assertNotNull("department.courses", department.getCourses());
        assertEquals("department.courses.size", 0, department.getCourses().size());
    }

    @Test
    public void findDepartmentLazyLoadCourses() throws Exception {
        cleanInsertDataSet("/departmentTestDataSet.xml");
        final long expectedDeptId = 2L;

        Department department = em.find(Department.class, expectedDeptId);

        assertTrue(punitUtil.isLoaded(department));
        assertFalse(punitUtil.isLoaded(department, "courses"));

        int coursesSize = department.getCourses().size();

        assertTrue(punitUtil.isLoaded(department, "courses"));
        assertEquals("All courses loaded", coursesSize, 2);
    }

    @Test
    public void findDepartmentAndFetchCourses() throws Exception {
        cleanInsertDataSet("/departmentTestDataSet.xml");
        final long expectedDeptId = 2L;

        TypedQuery<Department> query = em.createNamedQuery("FindAndFetchCourses", Department.class);
        query.setParameter("deptId", expectedDeptId);
        Department department = query.getSingleResult();
        em.detach(department);

        assertNotNull("department", department);
        assertEquals("department.id", expectedDeptId, department.getId());
        assertNotNull("department.courses", department.getCourses());
        assertEquals("department.courses.size", 2, department.getCourses().size());
    }

    Department createDepartment() {
        Department dept = new Department();
        dept.setCode("CS");
        dept.setName("Computer Science");

        return dept;
    }
}
