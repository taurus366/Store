package ApiControllers;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import BooleanCheck.CheckValidBoolean;
import BooleanCheck.DeleteOldSessions;
import BooleanCheck.DeleteOldTokens;
import BooleanCheck.UpdateSessionId;
import DataBases.DBconnectionLink;
import DataBases.GuestOrderDB;
import DataBases.OrderDB;
import Generators.FromStringToMd5;
import models.Order;

@Path("/")
public class OrderController {
	DBconnectionLink connectLink = new DBconnectionLink();
	Connection conn = null;
	ResultSet rs = null;
	PreparedStatement myStmt = null;
	javax.ws.rs.core.Response response;
	OrderDB OrderDB = new OrderDB();
	CheckValidBoolean isvalid = new CheckValidBoolean();
	GuestOrderDB guestorder = new GuestOrderDB();
	DeleteOldTokens deleteoldTokens = new DeleteOldTokens();
	FromStringToMd5 toMD5 = new FromStringToMd5();
	DeleteOldSessions oldsession = new DeleteOldSessions();
	UpdateSessionId updateSessionidTime = new UpdateSessionId();

	/**
	 * Makes a new order from an existing in Cart articles All articles from cart is
	 * copied into order_articles then copy user address if it is registered from
	 * users_address into orders or for guests write address while ordering.
	 * 
	 * 
	 * @param orders Required fields to create a new order
	 * @param token  Authentication token
	 * @throws ClassNotFoundException if class did not found
	 * @throws SQLException           if query to SQL is not successful
	 * @return JSON object
	 * @throws NoSuchAlgorithmException
	 */

	@POST
	@Path("/order")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postOrders(Order orders, @HeaderParam("Auth") String token, @HeaderParam("session") String session)
			throws ClassNotFoundException, SQLException, NoSuchAlgorithmException {
		return null;
}
}
