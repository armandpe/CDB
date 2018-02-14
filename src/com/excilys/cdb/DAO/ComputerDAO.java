package com.excilys.cdb.DAO;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import com.excilys.cdb.ConnectionManager.ConnectionManager;
import com.excilys.cdb.Model.Computer;

public class ComputerDAO extends DAO<Computer> {

	private static ComputerDAO computerDAO;

	private ComputerDAO() {}

	public static ComputerDAO getInstance() {
		if(computerDAO == null) {
			computerDAO = new ComputerDAO();
		}

		return computerDAO;
	}

	@Override
	protected Optional<Computer> buildItem(ResultSet rs) {
		try {
			Computer c = new Computer();
			c.setId(rs.getLong("id"));
			c.setName(rs.getString("name"));
			Timestamp temp = rs.getTimestamp("introduced");
			c.setIntroduced(temp == null ? null : temp.toLocalDateTime().toLocalDate());
			temp = rs.getTimestamp("introduced");
			c.setDiscontinued(temp == null ? null : temp.toLocalDateTime().toLocalDate());
			c.setCompanyId(rs.getLong("company_id"));
			return Optional.of(c);
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return Optional.ofNullable(null);
		}
	}


	public void Nat() {
		//		String query= "INSERT INTO computer ( name, introduced, discontinued ,company_id) VALUES (?, ?, ?, ?)";
		//        PreparedStatement ps;
		//       SingletonConn con= SingletonConn.INSTANCE;        
		//       con.initConn();
		//       try {
		//           
		//           ps = con.getConn().prepareStatement(query);
		//           ps.setString(1, obj.getName());
		//           ps.setTimestamp(2,obj.getIntroduced());
		//           ps.setTimestamp(3, obj.getDiscontinued());
		//           ps.setLong(4,obj.getCompany_id());
		//           ps.executeUpdate();
		//           tatement stmt = connection.createStatement();

		//           con.closeConn();
		//           return true;
	}

	public int createComputer(Computer c) {

		ConnectionManager cManager = ConnectionManager.getInstance(); 
		Connection connection = cManager.getConnection();
		Map<String, String> map = getMapperSQLFields();
		String[] template = {};

		LinkedHashMap<String, Object> paramValues = new LinkedHashMap<>();

		Set<String> keys = map.keySet();

		for(Entry<String, String> entry : map.entrySet())
		{
			Object value = null;
			try {
				Field field = Computer.class.getDeclaredField(entry.getValue());
				field.setAccessible(true);
				value = field.get(c);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			paramValues.put(entry.getKey(), value);
		}

		HashMap<String, Integer> keyOrder = new HashMap<>(); 

		int i = 0;
		for(Entry<String, Object> entry : paramValues.entrySet())
		{
			keyOrder.put(entry.getKey(), ++i);
		}

		String query = "INSERT INTO " + getTable() + " ( " + a2Str(paramValues.keySet().toArray(template)) + " ) VALUES ( ";

		for(i = 0; i < keys.size(); ++i)
			query += "?,";

		query = query.substring(0, query.length()-1);

		query += " )";

		PreparedStatement ps;

		try {
			ps = connection.prepareStatement(query);

			for(Entry<String, Object> entry : paramValues.entrySet())
			{
				addValueToStatement(ps, entry, keyOrder);
			}

			System.out.println(query);
			return ps.executeUpdate();
		}catch(Exception e) { 
			e.printStackTrace();
			System.out.println("Erreur requete : " + e.getMessage());
			return -1;
		}

	}

	public void addValueToStatement(PreparedStatement ps, Entry<String, Object> entry, Map<String, Integer> keyOrder) throws SQLException {

		Object value = entry.getValue();

		Class<? extends Object> c = value.getClass();

		int order = keyOrder.get(entry.getKey());

		if(c == String.class) {
			ps.setString(order, (String) value);
		}
		else if(c == LocalDateTime.class) {
			if(value != null)
				ps.setDate(order, Date.valueOf(((LocalDateTime) value).toLocalDate()));
			else
				ps.setDate(order, null);
		}
		else if(c == LocalDate.class) {
			if(value != null)
				ps.setDate(order, Date.valueOf((LocalDate) value));
			else
				ps.setDate(order, null);	
		}
		else if(c == Integer.class) {
			ps.setInt(order, (Integer) value);
		}
		else if(c == Long.class) {
			ps.setLong(order, (Long) value);
		}
		
	}



	protected long updateComputer(Computer c) {

		return 0;
	}

	protected long deleteComputer(long id) {

		return 0;
	}

	@Override
	protected String getTable() {
		return "computer";
	}

	@Override
	public String getModelClassFullName() {
		return Computer.class.getName();
	}

}
