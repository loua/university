package laloia.university.model;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnitUtil;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.util.fileloader.DataFileLoader;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class AbstractJPATest {
	private static String persistenceUnit = "university-model";
	private static EntityManagerFactory emf;

	EntityManager em;
	EntityTransaction trx;
	PersistenceUnitUtil punitUtil;

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
		punitUtil = em.getEntityManagerFactory().getPersistenceUnitUtil();
		trx = em.getTransaction();
		trx.begin();
	}

	@After
	public void tearDownBase() throws Exception {
		if (em.isOpen()) {
			trx.rollback();
			em.close();
		}
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

	void generateFlatXmlDataSet(String fileName) throws Exception {
		IDatabaseConnection conn = getDbConnectionFromDriver();
		IDataSet fullDataSet = conn.createDataSet();
		FileOutputStream os = new FileOutputStream(fileName);
		FlatXmlDataSet.write(fullDataSet, os);
		conn.close();
		os.close();
	}

	void cleanInsertFlatXmlDataSet(String fileName) throws Exception {
		DataFileLoader loader = new FlatXmlDataFileLoader();
		IDataSet dataSet = loader.load(fileName);
		IDatabaseConnection conn = getDbConnectionFromDriver();
		DatabaseOperation.CLEAN_INSERT.execute(conn, dataSet);
		conn.close();
	}
}
