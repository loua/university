package laloia.university.model;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnitUtil;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.util.fileloader.DataFileLoader;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class AbstractJPATest {

    private static final String PERSISTENCE_UNIT = "university-model";
    private static final String DB_PROPERTIES_FILE = "db.properties";

    private static EntityManagerFactory emf;
    private static Properties dbProperties;

    EntityManager em;
    EntityTransaction trx;
    PersistenceUnitUtil punitUtil;

    @BeforeClass
    public static void setUpOnce() throws Exception {
        InputStream input = AbstractJPATest.class.getClassLoader().getResourceAsStream(DB_PROPERTIES_FILE);
        if (input == null) {
            throw new RuntimeException("Unable to locate property file " + DB_PROPERTIES_FILE);
        }
        dbProperties = new Properties();
        dbProperties.load(input);
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, dbProperties);

    }

    @AfterClass
    public static void tearDownOnce() {
        emf.close();
    }

    @Before
    public void setUpBase() throws Exception {
        setUp();
        emf.getCache().evictAll();
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
        emf.getCache().evictAll();
        tearDown();
    }
    
    void setUp() throws Exception {
    }
    
    void tearDown() throws Exception {
    }

    /**
     * Save the entity to the database.
     *
     * @param entity
     */
    void save(Object entity) {
        em.persist(entity);
        em.flush();
    }

    // Returns a DbUnit connection used to manage data for unit tests
    private IDatabaseConnection createDbUnitConnectionFromDriver() throws Exception {
        Class.forName(dbProperties.getProperty("javax.persistence.jdbc.driver"));

        IDatabaseConnection connection = new DatabaseConnection(DriverManager.getConnection(
                dbProperties.getProperty("javax.persistence.jdbc.url"),
                dbProperties.getProperty("javax.persistence.jdbc.user"),
                dbProperties.getProperty("javax.persistence.jdbc.password")));

        DatabaseConfig dbConfig = connection.getConfig();
        dbConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new HsqldbDataTypeFactory());

        return connection;
    }

    /**
     * Gets the active JPA managed connection as a DbUnit connection. Use this connection to verify results using DbUnit
     * while the transaction is in progress.
     *
     * @return IDatabaseConnection
     * @throws Exception
     */
    IDatabaseConnection getActiveDbUnitConnection() throws Exception {
        IDatabaseConnection connection = new DatabaseConnection(getActiveConnection());
        DatabaseConfig dbConfig = connection.getConfig();
        dbConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new HsqldbDataTypeFactory());

        return connection;
    }

    /**
     * Gets the active JPA managed connection
     *
     * @return java.sql.Connection
     */
    Connection getActiveConnection() {
        return em.unwrap(java.sql.Connection.class);
    }

    void generateFlatXmlDataSet(String fileName) throws Exception {
        IDatabaseConnection conn = null;
        FileOutputStream os = null;
        try {
            conn = createDbUnitConnectionFromDriver();
            IDataSet fullDataSet = conn.createDataSet();
            os = new FileOutputStream(fileName);
            FlatXmlDataSet.write(fullDataSet, os);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }

    /**
     * Performs a clean insert of the specified XML file.
     *
     * @param fileName
     * @throws Exception
     */
    void cleanInsertDataSet(String fileName) throws Exception {
        IDatabaseConnection conn = null;
        try {
            DataFileLoader loader = new FlatXmlDataFileLoader();
            IDataSet dataSet = loader.load(fileName);
            conn = createDbUnitConnectionFromDriver();
            DatabaseOperation.CLEAN_INSERT.execute(conn, dataSet);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * Deletes all data from tables specified in the XML file.
     *
     * @param fileName
     * @throws Exception
     */
    void deleteAll(String fileName) throws Exception {
        IDatabaseConnection conn = null;
        try {
            DataFileLoader loader = new FlatXmlDataFileLoader();
            IDataSet dataSet = loader.load(fileName);
            conn = createDbUnitConnectionFromDriver();
            DatabaseOperation.DELETE_ALL.execute(conn, dataSet);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
}
