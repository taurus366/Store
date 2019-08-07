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
		deleteoldTokens.DeleteOldTokens();
		oldsession.DeleteoldSessions();
		

		try {
			updateSessionidTime.UpdateSessionId(toMD5.GenerateMd5(session));
			if (token.length() == 0 && session.length() > 0) {
				// only guests!

				try {
					if (orders.getAddress().length() == 0 || orders.getCity().length() == 0
							|| orders.getCountry().length() == 0 || orders.getFirstname().length() == 0
							|| orders.getLastname().length() == 0 || orders.getPhone().length() == 0
							|| orders.getPostalcode().length() == 0) {
					}

				} catch (NullPointerException e) {
					// TODO: handle exception
					return Response.status(Response.Status.BAD_REQUEST).entity(
							"Please write all fields ^firstname^,^lastname^,^phone^,^country^,^postalcode^,^city^,^address^")
							.build();
				}

				if (isvalid.isvalidsessionID(toMD5.GenerateMd5(session))) {

					if (isvalid.isValidArticleIDintoCartbySession(toMD5.GenerateMd5(session))) {

						return guestorder.postGuestOrder(toMD5.GenerateMd5(session), orders.getFirstname(),
								orders.getLastname(), orders.getPhone(), orders.getPhone(), orders.getPostalcode(),
								orders.getCity(), orders.getAddress());
					} else {

						return Response.status(Response.Status.NOT_FOUND).entity(
								"Your cart is empty because of this you can't order before you didn't add any items into your cart !!!")
								.build();

					}

				} else {
					return Response.status(Response.Status.NOT_FOUND).entity(
							"Your sessionID is not valid , sessionsID who are not active more than 30 minutes are deleted! Because of this you must add items again to cart then try to order!")
							.build();
				}
			} else if (session.length() > 0 && token.length() > 0) {

				if (isvalid.isValidUser(token)) {
					if (isvalid.IsValidTokenAndSession(toMD5.GenerateMd5(session), token)) {
						// only registered users!
						return OrderDB.postUserOrder(token, toMD5.GenerateMd5(session));

					} else {
						return Response.status(Response.Status.NOT_FOUND).entity(
								"user_id into sessions and user_id into tokens are not same ,They would be expired Please log in!")
								.build();

					}

				} else {
					return Response.status(Response.Status.UNAUTHORIZED).entity(
							"We couldn't recognise you ! Please check your token  because it doesn't exist into tokens DB , it can be deleted by our system Reason: expired !")
							.build();
				}
			}
			if (session.length() == 0 || token.length() == 0) {
				return Response.status(Response.Status.NOT_FOUND).entity(
						"We have found that you aren't using  any sessionID or token and because of this you must log in if you are already registered , just add some items to cart if you are a guest!")
						.build();

			}

		} catch (NullPointerException e) {
			return Response.status(Response.Status.NOT_FOUND)
					.entity("If you see this  Please add HeaderParam   'Auth' and Header 'session' , if you are a guest please be sure that Auth and  session are wrote and Auth is clean ! ").build();
		}

		return Response.status(Response.Status.UNAUTHORIZED).entity("If you see this this is why something went wrong!")
				.build();
	}
}
