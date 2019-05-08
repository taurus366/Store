package ApiControllers;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
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
	 */

	@POST
	@Path("/auth/register")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doPostUser(RegisterUser user)
			throws ClassNotFoundException, SQLException, NoSuchAlgorithmException {
		deleteoldTokens.DeleteOldTokens();
		oldsession.DeleteoldSessions();

		try {
			if (user.getEmail().length() == 0 || user.getPassword().length() == 0 || user.getFirstname().length() == 0
					|| user.getLastname().length() == 0 || user.getPhone().length() == 0
					|| user.getCountry().length() == 0 || user.getPostalcode().length() == 0
					|| user.getCity().length() == 0 || user.getAddress().length() == 0) {
			}
		} catch (NullPointerException e) {
			return Response.status(Response.Status.NOT_ACCEPTABLE).entity(
					"You have to write all fields ^email^ ^password^ ^firstname^ ^lastname^ ^phone^ ^country^ ^postalcode^ ^city^ ^address^ !")
					.build();

		}

		if (isvalid.isDuplicateUser(user.getEmail())) {
			return UserDB.doPOSTuser(user.getEmail(), user.getPassword(), user.getFirstname(), user.getLastname(),
					user.getPhone(), user.getCountry(), user.getPostalcode(), user.getCity(), user.getAddress());
		}

		return Response.status(Response.Status.NOT_ACCEPTABLE)
				.entity("User with same email has already registered! , please choose another email").build();
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
	 */
	@GET
	@Path("/auth/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response Userlogin(Login loginmodel) throws NoSuchAlgorithmException, ClassNotFoundException, SQLException {
		deleteoldTokens.DeleteOldTokens();
		oldsession.DeleteoldSessions();

		try {
			return UserDB.doUserloginCheck(loginmodel.getEmail(), tomd5.GenerateMd5(loginmodel.getPassword()),
					session.sessionGenerator(), tokenG.GenerateToken());

		} catch (Exception e) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("Please write all fields ^email^ ^password^!")
					.build();

		}

	}
}
