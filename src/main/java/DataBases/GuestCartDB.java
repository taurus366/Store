package DataBases;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import Generators.FromStringToMd5;

public class GuestCartDB {
	DBconnectionLink connectLink = new DBconnectionLink();
	PreparedStatement myStmt = null;
	Connection conn = null;
	ResultSet rs = null;
	FromStringToMd5 tomd5 = new FromStringToMd5();

	/**
	 * Send to display Guest's cart info , it depends by session ID
	 * 
	 * @param session Guest's session ID
	 * @return send a message to the display that operation is completed successful
	 * @throws NoSuchAlgorithmException
	 * @throws ClassNotFoundException   if class did not found
	 * @throws SQLException             if query to SQL is not successful
	 */
	@SuppressWarnings("unchecked")
	public Response getGuestCartinfo(String session)
			throws NoSuchAlgorithmException, ClassNotFoundException, SQLException {

		JSONArray GuestCartInfo = new JSONArray();

		conn = connectLink.getConnectionLink();

		myStmt = (PreparedStatement) conn.prepareStatement(
				"select art.`id` as 'articul_id',art.`title` as 'name',ss.`session` as 'session_id',cart.`session_id` as 'guest_sessionID' from `articles` as art inner join `cart` as cart on art.`id` = cart.`article_id` inner join `sessions` as ss on cart.`session_id` = ss.`session` where `session_id` =?");
		myStmt.setString(1, session);
		rs = myStmt.executeQuery();

		while (rs.next()) {

			JSONObject article = new JSONObject();
			article.put("article_name", rs.getString(2));

			article.put("article_id", rs.getInt(1));
			GuestCartInfo.add(article);

		}

		if (GuestCartInfo.size() == 0) {

			return Response.status(Response.Status.NOT_FOUND).entity("Your cart is empty!").build();

		}

		return Response.status(Response.Status.OK).entity(GuestCartInfo.toString()).build();

	}

	/**
	 * Delete Guest's cart which guest has added earlier , it depends by guest's
	 * session ID
	 * 
	 * @param articleid article ID
	 * @param session   session ID of the Guest
	 * @return send a message to the display that operation is completed successful
	 * @throws NoSuchAlgorithmException
	 * @throws ClassNotFoundException   if class did not found
	 * @throws SQLException             if query to SQL is not successful
	 */
	public Response deleteGuestCart(int articleid, String session)
			throws NoSuchAlgorithmException, ClassNotFoundException, SQLException {

		conn = connectLink.getConnectionLink();

		myStmt = (PreparedStatement) conn
				.prepareStatement("delete from `cart` where `session_id` =? and `article_id`=? limit 1");

		myStmt.setString(1, session);
		myStmt.setInt(2, articleid);

		myStmt.executeUpdate();

		return Response.status(Response.Status.OK).entity("You removed article from cart!").build();

	}

	/**
	 * Create a new row into DB cart
	 * 
	 * @param articleid article ID
	 * @param session   session ID of the Guest
	 * @param type      if type == 1 it is only for guests which doesn't have a
	 *                  session ID if it is == 0 guests which have session ID
	 * @return send a message to the display that operation is completed successful
	 * @throws SQLException             if query to SQL is not successful
	 * @throws ClassNotFoundException   if class did not found
	 * @throws NoSuchAlgorithmException
	 */
	public Response postGuestCart(int articleid, String session, int type)
			throws SQLException, ClassNotFoundException, NoSuchAlgorithmException {

		String sessionMD5 = tomd5.GenerateMd5(session);

		// type 1 only for guest who doesn't have a sessionID !
		if (type == 1) {

			conn = connectLink.getConnectionLink();
			myStmt = (PreparedStatement) conn.prepareStatement("INSERT INTO `" + connectLink.schema
					+ "`.`sessions` (`id`,`user_id`, `session`, `start_date`, `last_activity_date`) VALUES (default,?,?,now(),now())");
			myStmt.setInt(1, 0);
			myStmt.setString(2, sessionMD5);

			myStmt.executeUpdate();
		}

		conn = connectLink.getConnectionLink();

		myStmt = (PreparedStatement) conn.prepareStatement("INSERT INTO `" + connectLink.schema
				+ "`.`cart` (`id`,`session_id`, `user_id`, `article_id`, `added_time`) VALUES (default,?,?,?,now())");

		myStmt.setString(1, sessionMD5);
		myStmt.setInt(2, 0);
		myStmt.setInt(3, articleid);

		myStmt.executeUpdate();
		return Response.status(Response.Status.OK).entity("Your session ID  is : " + " " + session + " "
				+ "and  Your item added into the cart! > If you don't send any request for 30 minutes the sessionid will be deleted and cart will be cleared also!")
				.build();

	}

}
