package DataBases;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import Generators.FromStringToMd5;
import models.isValidModels;

public class UserCartDB {
	DBconnectionLink connectLink = new DBconnectionLink();
	PreparedStatement myStmt = null;
	Connection conn = null;
	ResultSet rs = null;
	FromStringToMd5 toMD5 = new FromStringToMd5();

	
	public void postUserCart(int articleid, int user_id) throws ClassNotFoundException, SQLException
			 {

		

		conn = connectLink.getConnectionLink();
		myStmt = (PreparedStatement) conn.prepareStatement("INSERT INTO `" + connectLink.schema
				+ "`.`cart` (`id`,`session_id`, `user_id`, `article_id`, `added_time`) VALUES (default,?,?,?,now())");

		myStmt.setString(1, "0");
		myStmt.setInt(2, user_id);
		myStmt.setInt(3, articleid);

		myStmt.executeUpdate();
		
		
	
	}

	
	public Response doRemoveUserCartItem(int id, String token) throws SQLException, ClassNotFoundException {

		return null;
	}

	
	@SuppressWarnings("unchecked")
	public Response doGetUsercart(String cookie) throws ClassNotFoundException, SQLException {

		return null;
	}
	public String GetUserCart(int user_id) throws ClassNotFoundException, SQLException {
		
		ArrayList<isValidModels> userinfo = new ArrayList<>();

		conn = connectLink.getConnectionLink();

		myStmt = (PreparedStatement) conn.prepareStatement(
				"select  from cart where user_id =?");
		myStmt.setInt(1, user_id);
		rs = myStmt.executeQuery();

		while (rs.next()) {

			isValidModels user = new isValidModels();

			user.email = rs.getString(2);
			user.token = rs.getString(3);
			userinfo.add(user);
		}
		
		
		
		return null;
		
	}

}
