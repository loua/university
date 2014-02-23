package laloia.university.model;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
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

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Department dept = new Department();
		dept.setId(1);
		dept.setCode("CS");
		dept.setName("Computer Science");
		Course course = new Course();
		course.setId(1);
		course.setName("Introduction to Computer Science");
		course.setNumber("100");
		course.setDescription("An introduction to concepts in computer science for the beginner");
		dept.add(course);
		EntityTransaction trx = em.getTransaction();
		trx.begin();
		em.persist(dept);
		trx.commit();
	}

}
