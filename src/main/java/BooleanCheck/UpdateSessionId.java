package BooleanCheck;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import DataBases.DBconnectionLink;

@SuppressWarnings("unused")
public class UpdateSessionId {
	DBconnectionLink connectLink = new DBconnectionLink();
	Connection conn = null;
	PreparedStatement myStmt = null;

	/**
	 * Update session id when users or guests send request to the server
	 * 
	 * @param session (User's or Guest's) Session ID
	 * @throws ClassNotFoundException if class did not found
	 * @throws SQLException           if query to SQL is not successful
	 */

	public void UpdateSessionId(String session) throws ClassNotFoundException, SQLException {

		conn = connectLink.getConnectionLink();

		myStmt = (PreparedStatement) conn.prepareStatement(
				"UPDATE `lessons_ali`.`sessions` SET `last_activity_date` = now() WHERE ( `session` = ?) ");
		myStmt.setString(1, session);
		myStmt.executeUpdate();

	}

}
