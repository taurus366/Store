package ApiControllers;

import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.sun.org.apache.xerces.internal.util.URI.MalformedURIException;

import BooleanCheck.CheckValidBoolean;
import BooleanCheck.DeleteOldSessions;
import BooleanCheck.DeleteOldTokens;
import DataBases.UserDB;
import DataBases.DBconnectionLink;
import Generators.FromStringToMd5;
import Generators.SessionGenerator;
import Generators.TokenGenerator;
import models.Login;
import models.RegisterUser;

@Path("/")
public class UserController {
	DBconnectionLink connectLink = new DBconnectionLink();
	Connection conn = null;
	ResultSet rs = null;
	PreparedStatement myStmt = null;
	javax.ws.rs.core.Response response;
	UserDB UserDB = new UserDB();
	CheckValidBoolean isvalid = new CheckValidBoolean();
	DeleteOldTokens deleteoldTokens = new DeleteOldTokens();
	FromStringToMd5 tomd5 = new FromStringToMd5();
	SessionGenerator session = new SessionGenerator();
	TokenGenerator tokenG = new TokenGenerator();
	DeleteOldSessions oldsession = new DeleteOldSessions();

	/**
	 * 
	 * @param user Required fields to create new user
	 * @return send all info to /Databases
	 * @throws ClassNotFoundException   if class did not found
	 * @throws SQLException             if query to SQL is not successful
	 * @throws NoSuchAlgorithmException
	 * @throws URISyntaxException 
	 * @throws MalformedURIException 
	 */

	@POST
	@Path("/auth/register")
	//@Consumes(MediaType.APPLICATION_JSON)
	//RegisterUser user
	public Response doPostUser( @FormParam("firstname")String firstname,@FormParam("lastname")String lastname,@FormParam("phone")String phone,@FormParam("postalcode")String postalcode,@FormParam("country")String country,@FormParam("city")String city,@FormParam("address")String address,@FormParam("email")String email,@FormParam("password")String password)
			throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, MalformedURIException, URISyntaxException {
		deleteoldTokens.DeleteOldTokens();
		oldsession.DeleteoldSessions();
		
		
		
//		try {
//			if (user.getEmail().length() == 0 || user.getPassword().length() == 0 || user.getFirstname().length() == 0
//					|| user.getLastname().length() == 0 || user.getPhone().length() == 0
//					|| user.getCountry().length() == 0 || user.getPostalcode().length() == 0
//					|| user.getCity().length() == 0 || user.getAddress().length() == 0) {
//			}
//		} catch (NullPointerException e) {
//			return Response.status(Response.Status.NOT_ACCEPTABLE).entity(
//					"You have to write all fields ^email^ ^password^ ^firstname^ ^lastname^ ^phone^ ^country^ ^postalcode^ ^city^ ^address^ !")
//					.build();
//
//		}

//		if (isvalid.isDuplicateUser(user.getEmail())) {
//			return UserDB.doPOSTuser(user.getEmail(), user.getPassword(), user.getFirstname(), user.getLastname(),
//					user.getPhone(), user.getCountry(), user.getPostalcode(), user.getCity(), user.getAddress());
//		}

//		return Response.status(Response.Status.NOT_ACCEPTABLE)
//				.entity("User with same email has already registered! , please choose another email").build();
		
		if(isvalid.isDuplicateUser(email)) {
			try {
				UserDB.doPOSTuser(email, password, firstname, lastname, phone, country, postalcode, city, address);
			} catch (Exception e) {
				// TODO: handle exception
				return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
			}
			
			//return UserDB.doPOSTuser(email, password, firstname, lastname, phone, country, postalcode, city, address);
			java.net.URI url = new java.net.URI("/Store/login.html");
			return Response.temporaryRedirect(url).build();
		}
		
		java.net.URI url = new java.net.URI("/Store/register.html");
		return Response.temporaryRedirect(url).build();
	}

	/**
	 * Generate new Authentication token and session ID for the user fetch
	 * loginmodel's Fields then create new user into Users
	 * 
	 * @param loginmodel Required fields to check DB if they exist
	 * @return send all info to /Databases
	 * @throws NoSuchAlgorithmException
	 * @throws ClassNotFoundException   if class did not found
	 * @throws SQLException             if query to SQL is not successful
	 * @throws URISyntaxException 
	 */
	@GET
	@Path("/auth/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response Userlogin(@QueryParam("email")String email,@QueryParam("password")String password) throws NoSuchAlgorithmException, ClassNotFoundException, SQLException, URISyntaxException {
		deleteoldTokens.DeleteOldTokens();
		oldsession.DeleteoldSessions();

		if(UserDB.doUserloginCheck(email, tomd5.GenerateMd5(password))) {
			 StringBuilder token = tokenG.GenerateToken();
			 try {
				 UserDB.postUserTokentoDB(token,email);
				
				 
			} catch (Exception e) {
				return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
				
				
				// TODO: handle exception
			}
			
			// HERE !
			 NewCookie cookie = new NewCookie("cookie", token.toString());
			    //return Response.ok().cookie(cookie).build();
			    
			    java.net.URI url = new java.net.URI("/Store/all.ok");
				return Response.temporaryRedirect(url).cookie(cookie).build();
			
		}
		
		
		java.net.URI url = new java.net.URI("/Store/login.html");
		return Response.temporaryRedirect(url).build(); 
		
		
		
//		try {
//			return UserDB.doUserloginCheck(loginmodel.getEmail(), tomd5.GenerateMd5(loginmodel.getPassword()),
//					session.sessionGenerator(), tokenG.GenerateToken());
//
//		} catch (Exception e) {
//			return Response.status(Response.Status.UNAUTHORIZED).entity("Please write all fields ^email^ ^password^!")
//					.build();
//
//		}

	}
}
