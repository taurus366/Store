package ApiControllers;

import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import BooleanCheck.CheckValidBoolean;
import BooleanCheck.DeleteOldSessions;
import BooleanCheck.DeleteOldTokens;
import BooleanCheck.UpdateSessionId;
import DataBases.DBconnectionLink;
import DataBases.GuestCartDB;
import DataBases.UserCartDB;
import Generators.FromStringToMd5;
import Generators.SessionGenerator;

@Path("/")
public class CartController {
	DBconnectionLink connectLink = new DBconnectionLink();
	Connection conn = null;
	ResultSet rs = null;
	PreparedStatement myStmt = null;
	javax.ws.rs.core.Response response;
	UserCartDB UserCartDB = new UserCartDB();
	CheckValidBoolean isvalid = new CheckValidBoolean();
	SessionGenerator sessionGenerator = new SessionGenerator();
	DeleteOldSessions oldsession = new DeleteOldSessions();
	GuestCartDB guestDB = new GuestCartDB();
	DeleteOldTokens deleteoldTokens = new DeleteOldTokens();
	FromStringToMd5 toMD5 = new FromStringToMd5();
	UpdateSessionId updateSessionidTime = new UpdateSessionId();

	/**
	 * Post user items into DB cart fetch token,id and session then send to verify
	 * if it is user's token and is the session is valid check the article id is
	 * exist into DB articles
	 * 
	 * @param id      Article ID
	 * @param token   Authentication token
	 * @param session Session ID
	 * @return send all info to /Databases
	 * @throws ClassNotFoundException   if class did not found
	 * @throws SQLException             if query to SQL is not successful
	 * @throws NoSuchAlgorithmException
	 * @throws URISyntaxException 
	 */

	@POST
	@Path("/cart/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postUsercart(@PathParam("id") int id, @HeaderParam("Auth") String token,
			@HeaderParam("session") String session, @CookieParam(value="myStrCookie") String cookieParam1)
			throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, URISyntaxException {
			// should check if the cookie is already was created!
			if(cookieParam1.length()>0||cookieParam1!=null) {
				
				if(isvalid.isvalidCookie(cookieParam1)) {
					// should send to DB  command  add item to Cart!
					
					
				}
			}
		
		//Redirect to login page !
			java.net.URI url = new java.net.URI("/Store/login.html");
				return Response.temporaryRedirect(url).build();

//		try {
//			deleteoldTokens.DeleteOldTokens();
//			oldsession.DeleteoldSessions();
//			updateSessionidTime.UpdateSessionId(toMD5.GenerateMd5(session));
//
//			if (session.length() > 0 && token.length() > 0) {
//				if (isvalid.isArticul(id)) {
//					if (isvalid.IsValidTokenAndSession(toMD5.GenerateMd5(session), token)) {
//
//						return UserCartDB.postUserCart(id, token, session);
//					} else {
//						return Response.status(Response.Status.UNAUTHORIZED).entity(
//								"We catch that your token's user_id and session's user_id aren't same! Please log in!")
//								.build();
//					}
//				}
//
//			} else if (session.length() == 0 && token.length() == 0) {
//				if (isvalid.isArticul(id)) {
//
//					int GuestnoSession = 1;
//					return guestDB.postGuestCart(id, sessionGenerator.sessionGenerator(), GuestnoSession);
//				}
//
//			} else if (session.length() > 0 && token.length() == 0) {
//				if (isvalid.isvalidsessionID(toMD5.GenerateMd5(session))) {
//					if (isvalid.isArticul(id)) {
//						int GuestSession = 2;
//
//						return guestDB.postGuestCart(id, session, GuestSession);
//					}
//
//				} else {
//					return Response.status(Response.Status.UNAUTHORIZED).entity(
//							"We couldn't find same session id in our DB , if you don't know your session id Please be sure that your session id is empty in Header params to Generate new session ID !")
//							.build();
//				}
//
//			}
//		} catch (NullPointerException e) {
//			return Response.status(Response.Status.NOT_FOUND)
//					.entity("If you are a guest please create a param 'session'  and `Auth`").build();
//		}
//
//		return Response.status(Response.Status.NOT_FOUND).entity("You wrote invalid articul ID!").build();

	}

	/**
	 * Get(show) user's cart info
	 * 
	 * fetch token, and session then send to verify if it is user's token and is the
	 * session is valid
	 * 
	 * 
	 * @param token   Authentication token
	 * @param session Session ID
	 * @return send all info to /Databases
	 * @throws ClassNotFoundException   if class did not found
	 * @throws SQLException             if query to SQL is not successful
	 * @throws NoSuchAlgorithmException
	 * @throws URISyntaxException 
	 */

	@GET
	@Path("/cart")
	public Response doGETuserCart(@HeaderParam("Auth") String token, @HeaderParam("session") String session,  @CookieParam(value="myStrCookie") String cookieParam1)
			throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, URISyntaxException {
		if(cookieParam1.length()>0||cookieParam1!=null) {
		
			//should return user's cart!
			
		}
		// or >>>
		java.net.URI url = new java.net.URI("/Store/login.html");
		return Response.temporaryRedirect(url).build();
		
		
//		try {
//			deleteoldTokens.DeleteOldTokens();
//			oldsession.DeleteoldSessions();
//			updateSessionidTime.UpdateSessionId(toMD5.GenerateMd5(session));
//		} catch (Exception e) {
//			return Response.status(Response.Status.UNAUTHORIZED)
//					.entity("We realised that you aren't using any session ID or token...!").build();
//		}
//
//		if (token.length() == 0 && session.length() > 0) {
//
//			if (isvalid.isvalidsessionID(toMD5.GenerateMd5(session))) {
//
//				if (isvalid.isvalidGuestsSessionID(session)) {
//					return guestDB.getGuestCartinfo(toMD5.GenerateMd5(session));
//				} else {
//					return Response.status(Response.Status.UNAUTHORIZED).entity(
//							"You are using a session ID which has a token ID , you have to login to Generate  token ID !")
//							.build();
//				}
//
//			} else {
//				return Response.status(Response.Status.UNAUTHORIZED).entity("invalid Session ID!").build();
//			}
//		} else if (token.length() > 0 && session.length() > 0) {
//			if (isvalid.isValidUser(token)) {
//				if (isvalid.IsValidTokenAndSession(toMD5.GenerateMd5(session), token)) {
//					return UserCartDB.doGetUsercart(token);
//				} else {
//					return Response.status(Response.Status.UNAUTHORIZED).entity(
//							"We catched that your token's user_id and session's user_id aren't same!it would be the sessionid hase expired! Please log in! then you can check your cart! ")
//							.build();
//				}
//
//			} else {
//				return Response.status(Response.Status.UNAUTHORIZED).entity("Yout token is not valid! ").build();
//			}
//
//		}
//
//		return Response.status(Response.Status.UNAUTHORIZED)
//				.entity("We couldn't recognise you!Please log in to Generate new tokenID and sessionID!").build();
	}

	/**
	 * Delete article id from user's cart (delete item from user's cart)
	 * 
	 * fetch token,id and session then send to verify if it is user's token and is
	 * the session is valid then check if the ID is exists in user's cart
	 * 
	 * @param id      Article ID
	 * @param token   Authentication token
	 * @param session Session ID
	 * @return send all info to /Databases
	 * @throws ClassNotFoundException   if class did not found
	 * @throws SQLException             if query to SQL is not successful
	 * @throws NoSuchAlgorithmException
	 */

	@DELETE
	@Path("/cart/{id}")
	public Response doDeletefromCart(@PathParam("id") int id, @HeaderParam("Auth") String token,
			@HeaderParam("session") String session)
			throws ClassNotFoundException, SQLException, NoSuchAlgorithmException {
		oldsession.DeleteoldSessions();
		deleteoldTokens.DeleteOldTokens();
		updateSessionidTime.UpdateSessionId(toMD5.GenerateMd5(session));
		if (session.length() > 0 && token.length() == 0) {
			if (isvalid.isvalidsessionID(toMD5.GenerateMd5(session))) {
				if (isvalid.isValidArticleIDintoCartbySessionID(id, toMD5.GenerateMd5(session))) {
					return guestDB.deleteGuestCart(id, toMD5.GenerateMd5(session));

				} else {
					return Response.status(Response.Status.UNAUTHORIZED)
							.entity("We couldn't find the item into your cart!").build();
				}

			} else {
				return Response.status(Response.Status.UNAUTHORIZED).entity("invalid Session ID!").build();
			}
		}

		if (isvalid.isValidUser(token)) {

			if (isvalid.IsValidTokenAndSession(toMD5.GenerateMd5(session), token)) {
				if (isvalid.isValidarticleIDinCART(id, token)) {
					return UserCartDB.doRemoveUserCartItem(id, token);
				} else {
					return Response.status(Response.Status.NOT_FOUND)
							.entity("We couldn't find Article with same id into your cart !").build();
				}
			} else {
				return Response.status(Response.Status.UNAUTHORIZED).entity(
						"We catched that your token's user_id and session's user_id aren't same!it would be the sessionid hase expired! Please log in! then you can delete articles from cart! ")
						.build();
			}

		}
		return Response.status(Response.Status.UNAUTHORIZED)
				.entity("We couldn't recognise you! Please check your token!").build();

	}
}
