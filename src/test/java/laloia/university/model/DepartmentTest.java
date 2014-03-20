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

public class DepartmentTest extends AbstractJPATest {

	@Test
	public void persistDepartmentWithCourse() throws Exception {
		Department dept = createDepartment();
		//dept.add(createCourse());

		persistAndFlush(dept);

		DataFileLoader loader = new FlatXmlDataFileLoader();
		IDataSet expectedDataSet = loader
				.load("/expectedDepartmentDataSet.xml");
		ITable expectedTable = expectedDataSet.getTable("DEPARTMENT");

		IDatabaseConnection dbConnection = getDbConnection();
		IDataSet databaseDataSet = dbConnection.createDataSet();
		ITable actualTable = databaseDataSet.getTable("DEPARTMENT");
		DefaultColumnFilter.includedColumnsTable(actualTable, expectedTable
				.getTableMetaData().getColumns());

		// Assert actual database table match expected table
		Assertion.assertEquals(expectedTable, actualTable);
	}

	@Test
	public void persistCourseWithoutDepartment() {
		Course course = createCourse();

		persistAndFlush(course);

		assertNull(course.getDepartment());
		assertTrue("course.id > 0", course.getId() > 0);
	}

	@Test
	public void persistCourseWithNewDepartment() {
		Course course = new Course();
		course.setName("Intro to Computer Science");
		course.setNumber("100");

		course.setDepartment(createDepartment());

		persistAndFlush(course);

		assertNotNull("course.department != null", course.getDepartment());
		assertTrue("course.id > 0", course.getId() > 0);
	}
	
	@Test
	public void persistCourseWithExistingDepartment() throws Exception {
		cleanInsertFlatXmlDataSet("/departmentTestDataSet.xml");
		Course course = createCourse();
		final long expectedDeptId = 1L;
		Department department = em.find(Department.class, expectedDeptId);

		course.setDepartment(department);
		persistAndFlush(course);
		
		//em.refresh(course);
		em.clear();
		Course newCourse = em.find(Course.class, course.getId());
		assertEquals(expectedDeptId, newCourse.getDepartment().getId());
	}
	
	@Test
	public void findCourse() throws Exception {
		cleanInsertFlatXmlDataSet("/departmentTestDataSet.xml");
		final long expectedCourseId = 1L;
		
		Course course = em.find(Course.class, expectedCourseId);

		assertNotNull("course.department != null", course.getDepartment());
	}	
	
	@Test
	public void findDepartmentWithoutCourses() throws Exception {
		cleanInsertFlatXmlDataSet("/departmentTestDataSet.xml");
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
		cleanInsertFlatXmlDataSet("/departmentTestDataSet.xml");
		final long expectedDeptId = 2L;
		
		Department department = em.find(Department.class, expectedDeptId);

		assertTrue(punitUtil.isLoaded(department));
		assertFalse(punitUtil.isLoaded(department, "courses"));
	}
	
	@Test
	public void findDepartmentAndFetchCourses() throws Exception {
		cleanInsertFlatXmlDataSet("/departmentTestDataSet.xml");
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

	Course createCourse() {
		Course course = new Course();
		course.setName("Introduction to Computer Science");
		course.setNumber("100");
		course.setDescription("An introduction to concepts in computer science for the beginner");
		return course;
	}
}
