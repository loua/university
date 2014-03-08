package laloia.university.model;

import static org.junit.Assert.assertTrue;

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
	public void test() throws Exception {
		//
		Department dept = new Department();
		dept.setCode("CS");
		dept.setName("Computer Science");
		Course course = new Course();
		course.setName("Introduction to Computer Science");
		course.setNumber("100");
		course.setDescription("An introduction to concepts in computer science for the beginner");
		dept.add(course);

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
	public void courseTest() {
		Course course = new Course();
		course.setName("Intro to Computer Science");
		course.setNumber("100");

		persistAndFlush(course);

		assertTrue("course.id > 0", course.getId() > 0);
	}

}
