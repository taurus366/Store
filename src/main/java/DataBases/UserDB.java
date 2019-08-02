package DataBases;

import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.util.URI.MalformedURIException;

import Generators.FromStringToMd5;
import models.Login;
import models.isValidModels;

public class UserDB {
	DBconnectionLink connectLink = new DBconnectionLink();
	PreparedStatement myStmt = null;
	Connection conn = null;
	ResultSet rs = null;
	FromStringToMd5 md5 = new FromStringToMd5();
	@Context
	UriInfo uriInfo;

	/**
	 * Create a new row about User's info like email and password into DB Users then
	 * create a new row about Users address into DB Users_address
	 * 
	 * @param email      Required field to fill in to DB users
	 * @param password   Required field to fill in to DB users (MD5)
	 * @param firstname  Required field to fill in to DB users_address
	 * @param lastname   Required field to fill in to DB users_address
	 * @param phone      Required field to fill in to DB users_address
	 * @param country    Required field to fill in to DB users_address
	 * @param postalcode Required field to fill in to DB users_address
	 * @param city       Required field to fill in to DB users_address
	 * @param address    Required field to fill in to DB users_address
	 * @return 
	 * @return 
	 * @return send a message to the display that operation is completed successful
	 * @throws ClassNotFoundException if class did not found
	 * @throws SQLException           if query to SQL is not successful
	 * @throws MalformedURIException 
	 * @throws URISyntaxException 
	 */
	public void doPOSTuser(String email, String password, String firstname, String lastname, String phone,
			String country, String postalcode, String city, String address)

	
			throws ClassNotFoundException, SQLException, MalformedURIException, URISyntaxException {
		

		//try {

			conn = connectLink.getConnectionLink();

			myStmt = (PreparedStatement) conn.prepareStatement("INSERT INTO `" + connectLink.schema
					+ "`.`users` (`id`, `email` ,`password`, `creation date`,`type`) VALUES (default,?,Md5('" + password
					+ "'),now(),'user')");

			myStmt.setString(1, email);
			myStmt.executeUpdate();

			myStmt = (PreparedStatement) conn.prepareStatement("select * from `users` where `email` =?");
			myStmt.setString(1, email);
			rs = myStmt.executeQuery();
			ArrayList<isValidModels> userinfo = new ArrayList<>();
			while (rs.next()) {

				isValidModels user = new isValidModels();
				user.user_id = rs.getInt(1);
				userinfo.add(user);
			}
			int userID = 0;
			for (int i = 0; i < userinfo.size(); i++) {
				isValidModels userInfo = userinfo.get(i);
				userID = userInfo.user_id;
			}

			myStmt = (PreparedStatement) conn.prepareStatement("INSERT INTO `" + connectLink.schema
					+ "`.`users_address` (`id`, `user_id`,`firstname`,`lastname`,`phone`,`country`,`postalcode`,`city`,`address`) VALUES (default,?,?,?,?,?,?,?,?)");
			myStmt.setInt(1, userID);
			myStmt.setString(2, firstname);
			myStmt.setString(3, lastname);
			myStmt.setString(4, phone);
			myStmt.setString(5, country);
			myStmt.setString(6, postalcode);
			myStmt.setString(7, city);
			myStmt.setString(8, address);
			myStmt.executeUpdate();

		//} catch (SQLException e) {
			//return e.getMessage();
			//return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
			//return e.getMessage();
		//}
		//java.net.URI uri = new java.net.URI("/login/success");
		//return Response.temporaryRedirect(uri).build();
		//return uri;
		//return address;

		//return Response.status(Response.Status.OK).entity("You registered !").build();

	}

	/**
	 * Generate new token ID and session ID for user to log in if the email and
	 * password are valid
	 * 
	 * @param email     user email
	 * @param password  user password
	 * @param sessionid user session Id
	 * @param token     user token ID
	 * @return send a message to the display that operation is completed successful
	 * @throws NoSuchAlgorithmException
	 * @throws ClassNotFoundException   if class did not found
	 * @throws SQLException             if query to SQL is not successful
	 */
	public Response doUserloginCheck(String email, String password, String sessionid, StringBuilder token)
			throws NoSuchAlgorithmException, ClassNotFoundException, SQLException {

		String sessionidmd5 = md5.GenerateMd5(sessionid);

		ArrayList<Login> users = new ArrayList<>();

		conn = connectLink.getConnectionLink();

		myStmt = (PreparedStatement) conn.prepareStatement("select * from users where `password` =?");
		myStmt.setString(1, password);
		rs = myStmt.executeQuery();

		while (rs.next()) {

			Login userinfo = new Login();
			userinfo.id = rs.getString(1);
			userinfo.email = rs.getString(2);
			userinfo.password = rs.getString(3);

			users.add(userinfo);
		}

		for (int i = 0; i < users.size(); i++) {
			Login login = users.get(i);
			if (login.password.equals(password) && login.email.equals(email)) {

				try {

					conn = connectLink.getConnectionLink();

					myStmt = (PreparedStatement) conn.prepareStatement("INSERT INTO `" + connectLink.schema
							+ "`.`sessions` (`id`, `user_id` ,`session`, `start_date`,`last_activity_date`) VALUES (default,?,?,now(),now())");

					myStmt.setString(1, login.id);
					myStmt.setString(2, sessionidmd5);

					myStmt.executeUpdate();

					conn = connectLink.getConnectionLink();

					myStmt = (PreparedStatement) conn.prepareStatement("INSERT INTO `" + connectLink.schema
							+ "`.`tokens` (`id`, `user_id` ,`token`, `create_date`,`expire_date`) VALUES (default,?,'"
							+ token + "',now(),now() + INTERVAL 30 MINUTE)");

					myStmt.setString(1, login.id);
					myStmt.executeUpdate();

					return Response.status(Status.OK).entity("Your token has generated and IT IS >" + " " + token + " "
							+ "and your session id is >" + " " + sessionid + "<").build();

				} catch (Exception e) {
					// TODO: handle exception
					return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
				}

			}

		}
		return Response.status(Status.UNAUTHORIZED).entity("You wrote invalid 'email' or 'password' !").build();

	}
	
	public boolean doUserloginCheck(String email,String password) throws ClassNotFoundException, SQLException {
		
		ArrayList<Login> users = new ArrayList<>();
		
		conn = connectLink.getConnectionLink();

		myStmt = (PreparedStatement) conn.prepareStatement("select * from users where `email` =? and `password` =?");
		myStmt.setString(1, email);
		myStmt.setString(2, password);
		rs = myStmt.executeQuery();
		
		while (rs.next()) {

			Login userinfo = new Login();
			userinfo.id = rs.getString(1);
			userinfo.email = rs.getString(2);
			userinfo.password = rs.getString(3);

			users.add(userinfo);
		}
		
		if(users.size()>0) {
			return true;
		}
		return false;		
	}
	
	public void postUserTokentoDB(StringBuilder token,String email) throws SQLException, ClassNotFoundException {
		
		conn = connectLink.getConnectionLink();
		myStmt = (PreparedStatement) conn.prepareStatement("select * from users where `email` =? limit 1");
		myStmt.setString(1, email);
		rs = myStmt.executeQuery();
		
		ArrayList<Login> users = new ArrayList<>();
		
		while (rs.next()) {

			Login userinfo = new Login();
			userinfo.id = rs.getString(1);
			userinfo.email = rs.getString(2);
			userinfo.password = rs.getString(3);

			users.add(userinfo);
			
		}
		
		String id = null;
		for(int i=0;i<users.size();i++) {
			Login login = users.get(i);
			
			id = login.id;
		}
		
		
		conn = connectLink.getConnectionLink();

		myStmt = (PreparedStatement) conn.prepareStatement("INSERT INTO `" + connectLink.schema
				+ "`.`tokens` (`id`, `user_id` ,`token`, `create_date`,`expire_date`) VALUES (default,?,'"
				+ token + "',now(),now() + INTERVAL 30 MINUTE)");

		myStmt.setString(1, id);
		myStmt.executeUpdate();
		
	}
}
