package laloia.university.model;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class AbstractJPATest {
	private static String persistenceUnit = "university-model";
	private static EntityManagerFactory emf;
	
	EntityManager em;
	EntityTransaction trx;

	@BeforeClass
	public static void setUpOnce() throws Exception {
		emf = Persistence.createEntityManagerFactory(persistenceUnit);
	}

	@AfterClass
	public static void tearDownOnce() {
		emf.close();
	}

	@Before
	public void setUpBase() throws Exception {
		em = emf.createEntityManager();
		trx = em.getTransaction();
		trx.begin();
	}

	@After
	public void tearDownBase() throws Exception {
		em.close();
		trx.rollback();
	}
	
	void persistAndFlush(Object entity) {
		em.persist(entity);
		em.flush();
	}

	IDatabaseConnection getDbConnectionFromDriver() throws Exception {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		return new DatabaseConnection(DriverManager.getConnection(
				"jdbc:derby:memory:university;create=true", "app", "password"));
	}

	IDatabaseConnection getDbConnection() throws Exception {
		return new DatabaseConnection(getCurrentConnection());
	}

	Connection getCurrentConnection() {
		return em.unwrap(java.sql.Connection.class);
	}

}
