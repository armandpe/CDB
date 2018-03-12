package test.java.com.excilys.cdb.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import main.java.com.excilys.cdb.Main;
import main.java.com.excilys.cdb.connectionmanager.ConnectionManager;
import main.java.com.excilys.cdb.dao.ComputerDAO;
import main.java.com.excilys.cdb.dao.FailedDAOOperationException;
import main.java.com.excilys.cdb.model.Company;
import main.java.com.excilys.cdb.model.Computer;

public class ComputerDAOTest {

	private static boolean first = true;
	
	static final Logger LOGGER = LoggerFactory.getLogger(ComputerDAOTest.class);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}
	
	private static void dropDatabase() {

	}

	private static void initDatabase() {
		try (Connection connection = ConnectionManager.getInstance().getConnection(); Statement statement = connection.createStatement();) {
			statement.executeUpdate("DROP TABLE computer;\n"
					+ "DROP TABLE company;");
			connection.commit();
		} catch (SQLException e) {
			LOGGER.error(Main.getErrorMessage(null, e.getMessage()));
		}
		
		try (Connection connection = ConnectionManager.getInstance().getConnection(); 
				Statement statement = connection.createStatement();) {
			statement.execute(
					"  create table company (\n" + 
					"    id                        bigint not null ,\n" + 
					"    name                      varchar(255),\n" + 
					"    constraint pk_company primary key (id))\n" + 
					"  ;\n" + 
					"\n" + 
					"  create table computer (\n" + 
					"    id                        bigint not null ,\n" + 
					"    name                      varchar(255),\n" + 
					"    introduced                timestamp NULL,\n" + 
					"    discontinued              timestamp NULL,\n" + 
					"    company_id                bigint default NULL,\n" + 
					"    constraint pk_computer primary key (id))\n" + 
					"  ;");
			connection.commit();
			if (first) {
				statement.execute("CREATE USER admincdb PASSWORD qwerty1234 ADMIN");
				first = false;
			}
			connection.commit();
			statement.executeUpdate(
					"insert into company (id,name) values (  1,\'Apple Inc.\');");
			statement.executeUpdate("insert into computer (id,name,introduced,discontinued,company_id) values (  1,'MacBook Pro 15.4 inch',null,null,1);");
			statement.executeUpdate("insert into computer (id,name,introduced,discontinued,company_id) values (  3,'CM-200',null,null,2);");
			statement.executeUpdate("insert into computer (id,name,introduced,discontinued,company_id) values (  4,'Lalala','2010-10-10','2012-12-12',2);");
			connection.commit();
		} catch (SQLException e) {
			LOGGER.error(Main.getErrorMessage(null, e.getMessage()));
		}
	}

	@Before
	public void setUp() throws Exception {
		initDatabase();
	}

	@After
	public void tearDown() throws Exception {
		dropDatabase();
	}

	@Test
	public void testCreateComputerComputer() {
		Computer toAdd = new Computer.ComputerBuilder().withId(2).withName("Mb").withCompany(new Company(1, "Apple Inc.")).build();
		try {
			ComputerDAO.getInstance().createComputer(toAdd);
			assertTrue(ComputerDAO.getInstance().getCount(null) == 4 && ComputerDAO.getInstance().getById(2).get().equals(toAdd));
		} catch (FailedDAOOperationException e) {
			LOGGER.error(Main.getErrorMessage(null, e.getMessage()));
			fail();
		}
	}

	@Test
	public void testDeleteComputer() {
		try {
			ComputerDAO.getInstance().deleteComputer(1);
			assertTrue(ComputerDAO.getInstance().getCount(null) == 2);
		} catch (FailedDAOOperationException e) {
			LOGGER.error(Main.getErrorMessage(null, e.getMessage()));
			fail();
		}
	}

	@Test
	public void testGetAllLongLong() {
		try {
			assertEquals(3, ComputerDAO.getInstance().getAll(0, 10).size());
		} catch (FailedDAOOperationException e) {
			LOGGER.error(Main.getErrorMessage("Error getAll", e.getMessage()));
			fail();
		}
	}

	@Test
	public void testGetByIdLong() {
		try {
			Computer c = ComputerDAO.getInstance().getById(1).get();
			assertEquals(c, new Computer.ComputerBuilder().withId(1).withName("MacBook Pro 15.4 inch").withCompany(new Company(1, "Apple Inc.")).build());

		} catch (FailedDAOOperationException e) {
			LOGGER.error(Main.getErrorMessage("Error getById", e.getMessage()));
			fail();
		}
		
	}

	@Test
	public void testGetCount() {
		long count;
		try {
			count = ComputerDAO.getInstance().getCount(null);
			assertTrue(count == 3);
		} catch (FailedDAOOperationException e) {
			LOGGER.error(Main.getErrorMessage("Error getCount", e.getMessage()));
			fail();
		}
	}
	
	@Test
	public void testUpdateComputer() {
		Computer computer = new Computer.ComputerBuilder().withId(3).withName("CM-200").withCompany(new Company(1, "Apple Inc.")).build();
		computer.setName("j");
		try {
			ComputerDAO.getInstance().updateComputer(computer);
			Computer computer2 = ComputerDAO.getInstance().getById(3).get();
			assertEquals(computer, computer2);
		} catch (FailedDAOOperationException e) {
			LOGGER.error(Main.getErrorMessage(null, e.getMessage()));
			fail();
		}
		
	}

}
