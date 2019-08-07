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

	/**
	 * Insert chosen article ID by user into DB cart , it depends by token ID of the
	 * User
	 * 
	 * @param articleid article ID
	 * @param token     token ID of the User
	 * @param session   session ID of the User
	 * @returnsend a message to the display that operation is completed successful
	 * @throws ClassNotFoundException   if class did not found
	 * @throws SQLException             if query to SQL is not successful
	 * @throws NoSuchAlgorithmException
	 */
	public Response postUserCart(int articleid, String token, String session)
			throws ClassNotFoundException, SQLException, NoSuchAlgorithmException {

		String sessionMD5 = toMD5.GenerateMd5(session);

		ArrayList<isValidModels> usersCartinfo = new ArrayList<>();

		conn = connectLink.getConnectionLink();
		myStmt = (PreparedStatement) conn.prepareStatement("select * from tokens where `token` =?");
		myStmt.setString(1, token);
		rs = myStmt.executeQuery();

		while (rs.next()) {

			isValidModels userinfo = new isValidModels();

			userinfo.user_id = rs.getInt(2);
			userinfo.token = rs.getString(3);
			usersCartinfo.add(userinfo);

		}
		for (int i = 0; i < usersCartinfo.size(); i++) {
			isValidModels usercartinfo = usersCartinfo.get(i);
			if (usercartinfo.token.equals(token)) {

				conn = connectLink.getConnectionLink();
				myStmt = (PreparedStatement) conn.prepareStatement("INSERT INTO `" + connectLink.schema
						+ "`.`cart` (`id`,`session_id`, `user_id`, `article_id`, `added_time`) VALUES (default,?,?,?,now())");

				myStmt.setString(1, sessionMD5);
				myStmt.setInt(2, usercartinfo.user_id);
				myStmt.setInt(3, articleid);

				myStmt.executeUpdate();

				return Response.status(Response.Status.OK).entity("Your item added into the cart!").build();
			}
		}

		return Response.status(Response.Status.UNAUTHORIZED)
				.entity("We couldn't authorized you Please review your token").build();
	}

	/**
	 * Remove article by ID from User's cart
	 * 
	 * @param id    article ID
	 * @param token User's token ID
	 * @return send a message to the display that operation is completed successful
	 * @throws SQLException           if query to SQL is not successful
	 * @throws ClassNotFoundException if class did not found
	 */
	public Response doRemoveUserCartItem(int id, String token) throws SQLException, ClassNotFoundException {

		conn = connectLink.getConnectionLink();

		myStmt = (PreparedStatement) conn.prepareStatement("Select * from tokens where `token` =?");
		myStmt.setString(1, token);
		rs = myStmt.executeQuery();
		ArrayList<isValidModels> userinfo = new ArrayList<>();
		while (rs.next()) {

			isValidModels user = new isValidModels();

			user.user_id = rs.getInt(2);
			user.token = rs.getString(3);
			userinfo.add(user);
		}
		int user_id = 0;
		for (int i = 0; i < userinfo.size(); i++) {
			isValidModels userInfo = userinfo.get(i);
			if (userInfo.token.equals(token)) {
				user_id = userInfo.user_id;
			}
		}

		myStmt = (PreparedStatement) conn.prepareStatement(
				"DELETE FROM `" + connectLink.schema + "`.`cart` WHERE `user_id`=? and `article_id` =? LIMIT 1");
		myStmt.setInt(1, user_id);
		myStmt.setInt(2, id);
		myStmt.executeUpdate();

		return Response.status(Response.Status.OK).entity("You removed article with id:" + id + " " + "from cart !")
				.build();
	}

	/**
	 * Send to display info about User's cart , it depends by token ID
	 * 
	 * @param token User's token ID
	 * @return send a message to the display that operation is completed successful
	 * @throws ClassNotFoundException if class did not found
	 * @throws SQLException           if query to SQL is not successful
	 */
	@SuppressWarnings("unchecked")
	public Response doGetUsercart(String cookie) throws ClassNotFoundException, SQLException {

		ArrayList<isValidModels> userinfo = new ArrayList<>();

		conn = connectLink.getConnectionLink();

		myStmt = (PreparedStatement) conn.prepareStatement(
				"select  us.`id` as 'user_id',us.`email` as 'user_email',tok.`token` as 'user_token'from `users` as us inner join `tokens` as tok on us.`id` = tok.`user_id` where `token` =?");
		myStmt.setString(1, cookie);
		rs = myStmt.executeQuery();

		while (rs.next()) {

			isValidModels user = new isValidModels();

			user.email = rs.getString(2);
			user.token = rs.getString(3);
			userinfo.add(user);
		}
		String user_email = null;

		for (int i = 0; i < userinfo.size(); i++) {
			isValidModels user = userinfo.get(i);
			if (user.token.equals(cookie)) {
				user_email = user.email;
			}
		}

		myStmt = (PreparedStatement) conn.prepareStatement(
				"select art.`title` as 'article_name',art.`id` as 'article_id'  from `cart` as cart inner join `articles` as art on cart.`article_id` = art.`id` inner join `users` as us on cart.`user_id` = us.`id` where `email` =?");
		myStmt.setString(1, user_email);
		rs = myStmt.executeQuery();

		JSONArray orderinfo = new JSONArray();

		while (rs.next()) {

			JSONObject article = new JSONObject();
			article.put("article_name", rs.getString(1));
			article.put("article_id", rs.getInt(2));
			orderinfo.add(article);

		}

		if (orderinfo.size() == 0) {
			return Response.status(Response.Status.NOT_FOUND).entity("Your cart is empty!!").build();
		}
		return Response.status(Response.Status.OK).entity(orderinfo.toString()).build();

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
