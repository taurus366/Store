package DataBases;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.core.Response;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import BooleanCheck.DeleteOldSessions;
import BooleanCheck.UpdateSessionId;
import Generators.FromStringToMd5;
import models.isValidModels;

public class GuestOrderDB {
	DBconnectionLink connectLink = new DBconnectionLink();
	PreparedStatement myStmt = null;
	Connection conn = null;
	ResultSet rs = null;
	DeleteOldSessions oldsession = new DeleteOldSessions();
	UpdateSessionId updateSessionidTime = new UpdateSessionId();
	FromStringToMd5 toMD5 = new FromStringToMd5();

	/**
	 * Create a new row Guest's order into DB orders which has address of the guest
	 * , then create a new row into DB orders_article which holds articles ID
	 * 
	 * @param session    session ID of the Guest
	 * @param firstname  required field to fill in the row
	 * @param lastname   required field to fill in the row
	 * @param phone      required field to fill in the row
	 * @param country    required field to fill in the row
	 * @param postalcode required field to fill in the row
	 * @param city       required field to fill in the row
	 * @param address    required field to fill in the row
	 * @return send a message to the display that operation is completed successful
	 * @throws ClassNotFoundException   if class did not found
	 * @throws SQLException             if query to SQL is not successful
	 * @throws NoSuchAlgorithmException
	 */
	public Response postGuestOrder(String session, String firstname, String lastname, String phone, String country,
			String postalcode, String city, String address)
			throws ClassNotFoundException, SQLException, NoSuchAlgorithmException {

		oldsession.DeleteoldSessions();
		updateSessionidTime.UpdateSessionId(toMD5.GenerateMd5(session));

		conn = connectLink.getConnectionLink();
		myStmt = (PreparedStatement) conn.prepareStatement("INSERT INTO `" + connectLink.schema
				+ "`.`orders` (`id`, `session_id`,`user_id`,`ordered_time`,`firstname`,`lastname`,`phone`,`country`,`postalcode`,`city`,`address`) VALUES (default,?,0,now(),?,?,?,?,?,?,?)");
		myStmt.setString(1, session);
		myStmt.setString(2, firstname);
		myStmt.setString(3, lastname);
		myStmt.setString(4, phone);
		myStmt.setString(5, country);
		myStmt.setString(6, postalcode);
		myStmt.setString(7, city);
		myStmt.setString(8, address);
		myStmt.executeUpdate();

		conn = connectLink.getConnectionLink();
		myStmt = (PreparedStatement) conn.prepareStatement(
				"select * FROM `orders` WHERE   `session_id` =?   order by `ordered_time` desc limit 1");

		myStmt.setString(1, session);
		rs = myStmt.executeQuery();

		ArrayList<isValidModels> OrderID = new ArrayList<>();

		while (rs.next()) {

			isValidModels cartinfo = new isValidModels();

			cartinfo.order_id = rs.getInt(1);
			OrderID.add(cartinfo);

		}
		int order_id = 0;
		for (int i = 0; i < OrderID.size(); i++) {
			isValidModels orderid = OrderID.get(i);
			order_id = orderid.order_id;
		}

		conn = connectLink.getConnectionLink();
		myStmt = (PreparedStatement) conn.prepareStatement("Select * from cart where `session_id` =?");
		myStmt.setString(1, session);
		rs = myStmt.executeQuery();

		ArrayList<isValidModels> GuestCartInfo = new ArrayList<>();

		while (rs.next()) {

			isValidModels cartinfo = new isValidModels();

			cartinfo.articleID = rs.getInt(4);
			GuestCartInfo.add(cartinfo);

		}
		for (int i = 0; i < GuestCartInfo.size(); i++) {
			isValidModels cartinfo = GuestCartInfo.get(i);

			conn = connectLink.getConnectionLink();
			myStmt = (PreparedStatement) conn.prepareStatement("INSERT INTO `" + connectLink.schema
					+ "`.`orders_article` (`id`, `order_id`,`article_id`) VALUES (default,?,?)");
			myStmt.setInt(1, order_id);
			myStmt.setInt(2, cartinfo.articleID);

			myStmt.executeUpdate();

		}

		conn = connectLink.getConnectionLink();
		myStmt = (PreparedStatement) conn.prepareStatement(" delete from `cart` where `session_id`=?");
		myStmt.setString(1, session);

		myStmt.executeUpdate();

		return Response.status(Response.Status.BAD_REQUEST).entity("Your order submitted!").build();

	}

}
