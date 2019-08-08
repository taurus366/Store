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
import Generators.GetUserIDfromCookie;
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
	GetUserIDfromCookie getUserID;
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
	public Response postUsercart(@PathParam("id") int id, @CookieParam(value="myStrCookie") String cookie)
			throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, URISyntaxException {
			// should check if the cookie is already was created!
			if(cookie.length()>0||cookie!=null) {
				
				if(isvalid.isvalidCookie(cookie)) {
					// should send to DB  command  add item to Cart!
					try {
						UserCartDB.postUserCart(id, getUserID.GetUserIDbyCookie(cookie));
					} catch (Exception e) {
						return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
						// TODO: handle exception
					}
					
				}
			}
		
		//Redirect to login page !
			java.net.URI url = new java.net.URI("/Store/login.html");
				return Response.temporaryRedirect(url).build();


	}

	private Object getUserID(String cookie) {
		// TODO Auto-generated method stub
		return null;
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
	public Response doGETuserCart(@CookieParam(value="myStrCookie") String cookieParam1)
			throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, URISyntaxException {
		if(cookieParam1.length()>0||cookieParam1!=null) {
		//get user id !<>
			  if(isvalid.isvalidCookie(cookieParam1)) {
				  // first get user id by cookie then send to GetUserCart !
				  UserCartDB.GetUserCart(getUserID.GetUserIDbyCookie(cookieParam1));
			  }
			
			//should return user's cart! by cookieParam1 !
			
		}
		// or >>>
		java.net.URI url = new java.net.URI("/Store/login.html");
		return Response.temporaryRedirect(url).build();
		
		

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
	public Response doDeletefromCart(@PathParam("id") int id,@CookieParam(value="myStrCookie") String cookieParam1)
			throws ClassNotFoundException, SQLException, NoSuchAlgorithmException {
		return null;
	}
}
