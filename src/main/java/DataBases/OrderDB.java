package DataBases;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import models.isValidModels;

public class OrderDB {
	DBconnectionLink connectLink = new DBconnectionLink();
	PreparedStatement myStmt = null;
	Connection conn = null;
	ResultSet rs = null;

	/**
	 * Create a new row into DB orders which holds user's info(address) then create
	 * a new row also into DB orders_article which holds articles ID
	 * 
	 * @param token   User's token
	 * @param session User's session ID
	 * @return send a message to the display that operation is completed successful
	 * @throws ClassNotFoundException if class did not found
	 * @throws SQLException           if query to SQL is not successful
	 */
	@SuppressWarnings("unchecked")
	public Response postUserOrder(String token, String session) throws ClassNotFoundException, SQLException {

		ArrayList<isValidModels> userInfo = new ArrayList<>();

		conn = connectLink.getConnectionLink();
		myStmt = (PreparedStatement) conn.prepareStatement("Select * from tokens where `token` =?");
		myStmt.setString(1, token);
		rs = myStmt.executeQuery();

		while (rs.next()) {

			isValidModels userinfo = new isValidModels();

			userinfo.userID = rs.getInt(2);
			userinfo.user_token = rs.getString(3);
			userInfo.add(userinfo);

		}
		int user_id = 0;
		for (int i = 0; i < userInfo.size(); i++) {
			isValidModels user = userInfo.get(i);
			user_id = user.userID;
		}

		conn = connectLink.getConnectionLink();
		myStmt = (PreparedStatement) conn.prepareStatement("Select * from cart where `user_id` =?");
		myStmt.setInt(1, user_id);
		rs = myStmt.executeQuery();

		ArrayList<isValidModels> cartinfo = new ArrayList<>();
		while (rs.next()) {
			isValidModels articles = new isValidModels();
			articles.articleID = rs.getInt(4);
			cartinfo.add(articles);

		}
		if (cartinfo.size() == 0) {
			return Response.status(Response.Status.NOT_FOUND)
					.entity("Your cart is empty! because of this you must first add some articles to your cart!")
					.build();
		}

		conn = connectLink.getConnectionLink();
		myStmt = (PreparedStatement) conn.prepareStatement("select * from `tokens` where `token` = ?");
		myStmt.setString(1, token);
		rs = myStmt.executeQuery();
		JSONArray userid = new JSONArray();

		while (rs.next()) {
			isValidModels user = new isValidModels();
			user.user_id = rs.getInt(2);
			userid.add(user);

		}
		int Userid = 0;
		for (int i = 0; i < userid.size(); i++) {
			isValidModels useridinfo = (isValidModels) userid.get(i);
			Userid = useridinfo.user_id;
		}
		conn = connectLink.getConnectionLink();
		myStmt = (PreparedStatement) conn.prepareStatement("select * from `users_address` where `user_id` = ?");
		myStmt.setInt(1, Userid);
		rs = myStmt.executeQuery();
		JSONArray useraddressinfo = new JSONArray();
		while (rs.next()) {
			isValidModels user = new isValidModels();
			user.user_id = rs.getInt(2);
			user.firstname = rs.getString(3);
			user.lastname = rs.getString(4);
			user.phone = rs.getString(5);
			user.country = rs.getString(6);
			user.postalcode = rs.getString(7);
			user.city = rs.getString(8);
			user.address = rs.getString(9);
			useraddressinfo.add(user);

		}

		int user_idinfo = 0;
		String firstname = null;
		String lastname = null;
		String phone = null;
		String country = null;
		String postalcode = null;
		String city = null;
		String address = null;

		for (int i = 0; i < useraddressinfo.size(); i++) {
			isValidModels useraddress = (isValidModels) useraddressinfo.get(i);
			user_idinfo = useraddress.user_id;
			firstname = useraddress.firstname;
			lastname = useraddress.lastname;
			phone = useraddress.phone;
			country = useraddress.country;
			postalcode = useraddress.postalcode;
			city = useraddress.city;
			address = useraddress.address;
		}

		conn = connectLink.getConnectionLink();
		myStmt = (PreparedStatement) conn.prepareStatement("INSERT INTO `" + connectLink.schema
				+ "`.`orders` (`id`,`session_id`,`user_id`,`ordered_time`, `firstname`, `lastname`, `phone`, `country`, `postalcode`,`city`, `address`) VALUES (default,?,?,now(),?,?,?,?,?,?,?)");
		myStmt.setString(1, session);
		myStmt.setInt(2, user_idinfo);
		myStmt.setString(3, firstname);
		myStmt.setString(4, lastname);
		myStmt.setString(5, phone);
		myStmt.setString(6, country);
		myStmt.setString(7, postalcode);
		myStmt.setString(8, city);
		myStmt.setString(9, address);
		myStmt.executeUpdate();

		conn = connectLink.getConnectionLink();
		myStmt = (PreparedStatement) conn
				.prepareStatement("select * FROM `orders` WHERE   `user_id` =?   order by `ordered_time` desc limit 1");
		myStmt.setInt(1, user_id);
		rs = myStmt.executeQuery();
		ArrayList<isValidModels> orderid = new ArrayList<>();
		while (rs.next()) {
			isValidModels order = new isValidModels();
			order.order_id = rs.getInt(1);
			orderid.add(order);

		}
		int OrderID = 0;
		for (int i = 0; i < orderid.size(); i++) {
			isValidModels order = orderid.get(i);
			OrderID = order.order_id;
		}

		for (int i = 0; i < cartinfo.size(); i++) {
			isValidModels cart = cartinfo.get(i);

			conn = connectLink.getConnectionLink();
			myStmt = (PreparedStatement) conn.prepareStatement("INSERT INTO `" + connectLink.schema
					+ "`.`orders_article` (`id`, `order_id`,`article_id`) VALUES (default,?,?)");

			myStmt.setInt(1, OrderID);
			myStmt.setInt(2, cart.articleID);

			myStmt.executeUpdate();

			myStmt = (PreparedStatement) conn
					.prepareStatement("DELETE FROM `" + connectLink.schema + "`.`cart` WHERE (`user_id` =?)");
			myStmt.setInt(1, user_id);
			myStmt.executeUpdate();

		}

		return Response.status(Response.Status.OK).entity("You have submitted your order!").build();

	}

	/**
	 * Show orders info about User who was filtered by user ID (only Admins can use
	 * this)
	 * 
	 * @param id User's Id
	 * @return send a message to the display that operation is completed successful
	 * @throws ClassNotFoundException if class did not found
	 * @throws SQLException           if query to SQL is not successful
	 */
	@SuppressWarnings("unchecked")
	public Response GetOrdersbyUserID(int id) throws ClassNotFoundException, SQLException {

		conn = connectLink.getConnectionLink();
		myStmt = (PreparedStatement) conn.prepareStatement(
				"select ord.`user_id` as 'userID',ord.`ordered_time` as 'ordered',ord.`firstname` as 'name',ord.`lastname`,ord.`phone`,ord.`country`,ord.`postalcode`,ord.`city`,ord.`address`,art.`id` as 'itemID',art.`title`,art.`content`,art.`author` from `orders` as ord inner join `orders_article` as orda on ord.`id` = orda.`order_id` inner join `articles` as art on orda.`article_id` = art.`id` where `user_id` = ?");
		myStmt.setInt(1, id);
		rs = myStmt.executeQuery();

		JSONArray orderinfo = new JSONArray();
		while (rs.next()) {
			JSONObject info = new JSONObject();
			info.put("UserID", rs.getInt(1));
			info.put("ordered", rs.getString(2));
			info.put("name", rs.getString(3));
			info.put("lastname", rs.getString(4));
			info.put("phone", rs.getString(5));
			info.put("country", rs.getString(6));
			info.put("postalcode", rs.getString(7));
			info.put("city", rs.getString(8));
			info.put("address", rs.getString(9));
			info.put("itemID", rs.getString(10));
			info.put("title", rs.getString(11));
			info.put("content", rs.getString(12));
			info.put("author", rs.getString(13));
			orderinfo.add(info);

		}
		if (orderinfo.size() == 0) {
			return Response.status(Response.Status.NOT_FOUND).entity("no order for that user id !").build();
		}

		return Response.status(Response.Status.OK).entity(orderinfo.toString()).build();
	}

}
