package test.java.com.excilys.cdb.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import main.java.com.excilys.cdb.connectionmanager.ConnectionManager;
import main.java.com.excilys.cdb.dao.ComputerDAO;
import main.java.com.excilys.cdb.model.Computer;

public class ComputerDAOTest {

	private static void initDatabase() {
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
			statement.execute("CREATE USER admincdb PASSWORD qwerty1234 ADMIN");
			connection.commit();
			statement.executeUpdate(
					"insert into company (id,name) values (  1,\'Apple Inc.\');");
			statement.executeUpdate("insert into computer (id,name,introduced,discontinued,company_id) values (  1,'MacBook Pro 15.4 inch',null,null,1);");
			statement.executeUpdate("insert into computer (id,name,introduced,discontinued,company_id) values (  3,'CM-200',null,null,2);");
			statement.executeUpdate("insert into computer (id,name,introduced,discontinued,company_id) values (  4,'Lalala','2010-10-10','2012-12-12',2);");
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		initDatabase();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		try (Connection connection = ConnectionManager.getInstance().getConnection(); Statement statement = connection.createStatement();) {
			statement.executeUpdate("DROP TABLE computer;\n"
					+ "DROP TABLE company;");
			connection.commit();
		}
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testUpdateComputer() {
		Computer computer = new Computer.ComputerBuilder().withId(3).withName("CM-200").withCompanyId(2).build();
		computer.setName("j");
		ComputerDAO.getInstance().updateComputer(computer);
		Computer computer2 = ComputerDAO.getInstance().getById(3).get();
		assertEquals(computer, computer2);
	}

	@Test
	public void testDeleteComputer() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetByIdObjectArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetByIdLong() {
		Computer c = ComputerDAO.getInstance().getById(1).get();
		
		assertEquals(c, new Computer.ComputerBuilder().withId(1).withName("MacBook Pro 15.4 inch").withCompanyId(1).build());
	}

	@Test
	public void testGetAllLongLong() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCount() {
//		PowerMockito.mockStatic(ConnectionManager.class);
//		Computer c4 = mock(Computer.class);
//		when(ConnectionManager.getInstance().getConnection()).thenReturn(getConnection());
		long count = ComputerDAO.getInstance().getCount();
		
		assertTrue(count == 3);
	}

	@Test
	public void testBuildItemResultSet1() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSQLArgs() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAllObjectArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCountObjectArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPrimaryKey() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteByPrimaryKey() {
		fail("Not yet implemented");
	}

	@Test
	public void testExecuteStatement() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddValueToStatement() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsNull() {
		fail("Not yet implemented");
	}

}
