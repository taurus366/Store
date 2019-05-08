package BooleanCheck;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import DataBases.DBconnectionLink;

@SuppressWarnings("unused")
public class DeleteOldTokens {
	DBconnectionLink connectLink = new DBconnectionLink();
	Connection conn = null;
	PreparedStatement myStmt = null;

	/**
	 * Delete old Token from tokens
	 * 
	 * @throws ClassNotFoundException if class didn't find
	 * @throws SQLException           if query to SQL is not successful
	 */
	public void DeleteOldTokens() throws ClassNotFoundException, SQLException {

		conn = connectLink.getConnectionLink();
		myStmt = (PreparedStatement) conn.prepareStatement("DELETE FROM `tokens` WHERE  `expire_date` < now()");
		myStmt.executeUpdate();

	}

}
