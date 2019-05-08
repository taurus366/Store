package BooleanCheck;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import DataBases.DBconnectionLink;
import models.isValidModels;

public class DeleteOldSessions {

	DBconnectionLink connectLink = new DBconnectionLink();
	Connection conn = null;
	ResultSet rs = null;
	PreparedStatement myStmt = null;

	/**
	 * Delete old sessions from sessions DB and Clean Guest's cart
	 * 
	 * @throws ClassNotFoundException   if class didn't find
	 * @throws SQLException             if query to SQL is not successful
	 * @throws NoSuchAlgorithmException
	 */

	public void DeleteoldSessions() throws ClassNotFoundException, SQLException, NoSuchAlgorithmException {

		conn = connectLink.getConnectionLink();

		myStmt = (PreparedStatement) conn.prepareStatement(
				"select * FROM `sessions` WHERE `last_activity_date` < DATE_SUB(NOW(),INTERVAL 30 MINUTE)");

		rs = myStmt.executeQuery();

		ArrayList<isValidModels> userinfo = new ArrayList<>();

		while (rs.next()) {

			isValidModels info = new isValidModels();
			info.user_id = rs.getInt(2);
			info.session_id = rs.getString(3);

			userinfo.add(info);
		}
		if (userinfo.size() > 0) {

			conn = connectLink.getConnectionLink();

			myStmt = (PreparedStatement) conn.prepareStatement(
					"DELETE FROM `sessions` WHERE `last_activity_date` < DATE_SUB(NOW(), INTERVAL 30 MINUTE)");
			myStmt.executeUpdate();
			for (int i = 0; i < userinfo.size(); i++) {
				isValidModels usersinfo = userinfo.get(i);

				conn = connectLink.getConnectionLink();

				myStmt = (PreparedStatement) conn
						.prepareStatement("DELETE FROM `cart` WHERE `session_id`=? and `user_id`=?");
				myStmt.setString(1, usersinfo.session_id);
				myStmt.setInt(2, usersinfo.user_id);
				myStmt.executeUpdate();
			}

		}

	}
}
