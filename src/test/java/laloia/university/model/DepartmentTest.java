package laloia.university.model;

import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.dbunit.DBTestCase;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DepartmentTest {

	private static EntityManagerFactory emf;
	private static EntityManager em;
	private static String persistenceUnit = "university-model";

	@BeforeClass
	public static void initTestFixture() throws Exception {
		emf = Persistence.createEntityManagerFactory(persistenceUnit);
		em = emf.createEntityManager();
	}

	@AfterClass
	public static void closeTestFixture() {
		em.close();
		emf.close();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		Department dept = new Department();
		dept.setCode("CS");
		dept.setName("Computer Science");
		Course course = new Course();
		course.setName("Introduction to Computer Science");
		course.setNumber("100");
		course.setDescription("An introduction to concepts in computer science for the beginner");
		dept.add(course);
		EntityTransaction trx = em.getTransaction();
		trx.begin();
		em.persist(dept);
		em.flush();
		System.out.println("dept.Id=" + dept.getId());
		System.out.println("dept.courses=" + dept.getCourses().size());
		
		trx.commit();

		//IDataSet databaseDataSet = new DatabaseConnection(getConnection()).createDataSet();
        //ITable actualTable = databaseDataSet.getTable("TABLE_NAME");

	}

	private Connection getConnection() {
		Connection connection = em.unwrap(java.sql.Connection.class);
		return connection;
	}
}
