package DataBases;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;

public class DBconnectionLink {

	public String schema = "lessons_ali";
	public String name = "lessons_ali";
	public String password = "EFjXgqwAZu9Ryqts";

	public Connection getConnectionLink() throws SQLException, ClassNotFoundException {

		Class.forName("com.mysql.jdbc.Driver");

		return (Connection) DriverManager.getConnection("jdbc:mysql://office.nasbg.com:3306/" + schema, name, password);
	}
}
