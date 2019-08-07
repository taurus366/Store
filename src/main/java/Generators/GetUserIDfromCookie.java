package Generators;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import DataBases.DBconnectionLink;
import models.isValidModels;


public class GetUserIDfromCookie {
	DBconnectionLink connectLink = new DBconnectionLink();
	PreparedStatement myStmt = null;
	Connection conn = null;
	ResultSet rs = null;
	
	public  int GetUserIDbyCookie(String cookie) throws ClassNotFoundException, SQLException {
		ArrayList<isValidModels> userID = new ArrayList<>();

		conn = connectLink.getConnectionLink();
		myStmt = (PreparedStatement) conn.prepareStatement("select * from tokens where `token` =? limit 1");
		myStmt.setString(1, cookie);
		rs = myStmt.executeQuery();

		while (rs.next()) {

			isValidModels userinfo = new isValidModels();

			userinfo.user_id = rs.getInt(2);
			
			userID.add(userinfo);

		}
		int userIID=0;
		for(int i=0;i<userID.size();i++) {
			isValidModels test = userID.get(i);
			userIID= test.user_id;
			
			
		}
		
		return userIID;
	}
}
