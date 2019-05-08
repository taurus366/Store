package ApiAdmin;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import BooleanCheck.CheckValidBoolean;
import BooleanCheck.DeleteOldTokens;
import DataBases.ArticleDB;
import DataBases.DBconnectionLink;
import DataBases.OrderDB;
import Generators.FromStringToMd5;
import models.CreateNewArticle;
import models.ModifyArticle;

@Path("/")
public class ArticleController {
	DBconnectionLink connectLink = new DBconnectionLink();
	Connection conn = null;
	ResultSet rs = null;
	PreparedStatement myStmt = null;
	javax.ws.rs.core.Response response;
	ArticleDB dbArticle = new ArticleDB();
	CheckValidBoolean isvalid = new CheckValidBoolean();
	DeleteOldTokens deleteoldTokens = new DeleteOldTokens();
	OrderDB orderdb = new OrderDB();
	FromStringToMd5 toMD5 = new FromStringToMd5();

	/**
	 * (only Admins can use this) fetch token,id and session then send to verify if
	 * it is Admin's token and is the session is valid then check if the ID is
	 * exists in ^articles^ then send them to doModifyArticle in
	 * /DataBases/ArticleDB.java
	 * 
	 * @param token       Authentication token
	 * @param session     Session ID
	 * @param id          Article ID
	 * @param articleinfo Required fields to modify existing Article
	 * @return send all info to /Databases
	 * @throws ClassNotFoundException   if class did not found
	 * @throws SQLException             if query to SQL is not successful
	 * @throws NoSuchAlgorithmException
	 */
	@POST
	@Path("/articles/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doModifyArticle(@HeaderParam("Auth") String token, @HeaderParam("session") String session,
			@PathParam("id") int id, ModifyArticle articleinfo)
			throws ClassNotFoundException, SQLException, NoSuchAlgorithmException {
		deleteoldTokens.DeleteOldTokens();

		if (isvalid.isAdmin(token)) {
			if (isvalid.IsValidTokenAndSession(toMD5.GenerateMd5(session), token)) {
				if (isvalid.isArticul(id)) {
					try {
						if (articleinfo.getAuthor().length() == 0 || articleinfo.getContent().length() == 0
								|| articleinfo.getTitle().length() == 0) {

						}

					} catch (Exception e) {
						return Response.status(Response.Status.BAD_REQUEST)
								.entity("You have to write all fields ^title^ ^content^ ^author^").build();
					}

					return dbArticle.doModifyArticle(id, articleinfo.getTitle(), articleinfo.getContent(),
							articleinfo.getAuthor()); // return DB !

				} else {
					return Response.status(Response.Status.NOT_FOUND).entity("We couldn't find article with same ID !")
							.build();
				}

			} else {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Your sessionid and tokens aren't match , please log in then continue!").build();
			}

		}
		return Response.status(Response.Status.UNAUTHORIZED).entity("Only admins can use this request !").build();
	}

	/**
	 * Delete existing Article from DataBase! (only Admins can use this) fetch
	 * token,id and session then send to verify if it is Admin's token and is the
	 * session is valid then check if the ID is exists into DB--> Articles then
	 * Delete it
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
	@Path("/articles/{id}") // <<<<<<<<<>>>>>>>
	public Response doDeleteArticle(@PathParam("id") int id, @HeaderParam("Auth") String token,
			@HeaderParam("session") String session)
			throws ClassNotFoundException, SQLException, NoSuchAlgorithmException {
		deleteoldTokens.DeleteOldTokens();

		if (isvalid.isAdmin(token)) {
			if (isvalid.IsValidTokenAndSession(toMD5.GenerateMd5(session), token)) {
				if (isvalid.isArticul(id)) {
					return dbArticle.deleteArticle(id);
				} else {
					return Response.status(Response.Status.FORBIDDEN)
							.entity("We couldn't fint same article ID into DataBase!").build();
				}

			} else {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Your sessionid and tokens aren't match , please log in then continue!").build();
			}

		}
		return Response.status(Response.Status.NOT_FOUND).entity("Only admins can use this request!").build();

	}

	/**
	 * Create new Article! (only Admins can use this) fetch token , articles(fields)
	 * , session then send to verify if it is Admin's token and is the session is
	 * valid then create new Article into DB--> Articles
	 * 
	 * @param articles Required fields to create new Article
	 * @param token    Authentication token
	 * @param session
	 * @return send all info to /Databases
	 * @throws ClassNotFoundException   if class did not found
	 * @throws SQLException             if query to SQL is not successful
	 * @throws NoSuchAlgorithmException
	 */
	@SuppressWarnings("static-access")
	@POST
	@Path("/articles")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doPostarticles(CreateNewArticle articles, @HeaderParam("Auth") String token,
			@HeaderParam("session") String session)
			throws ClassNotFoundException, SQLException, NoSuchAlgorithmException {
		deleteoldTokens.DeleteOldTokens();

		if (isvalid.isAdmin(token)) {
			if (isvalid.IsValidTokenAndSession(toMD5.GenerateMd5(session), token)) {
				try {
					if (articles.getAuthor().equals(null) || articles.getContent().equals(null)
							|| articles.getTitle().equals(null)) {
					}
				} catch (NullPointerException e) {
					return response.status(Status.BAD_REQUEST)
							.entity("You have to write all fields! ^title^ ^content^ ^author^").build();
				}
			} else {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Your sessionid and tokens aren't match , please log in then continue!").build();
			}

			return dbArticle.doPOSTarticles(articles.getTitle(), articles.getContent(), articles.getAuthor());

		}

		return response.status(Status.UNAUTHORIZED).entity("Only admins can use this request!").build();
	}

	/**
	 * Show orders info about User who was filtered by user ID (only Admins can use
	 * this)
	 * 
	 * @param id      User's ID
	 * @param token   User's token ID
	 * @param session User's session ID
	 * @return send info to /Databases
	 * @throws ClassNotFoundException   if class didn't find
	 * @throws SQLException             if query to SQL is not successful
	 * @throws NoSuchAlgorithmException
	 */
	@GET
	@Path("/orders/{id}")
	public Response GetOrdersbyID(@PathParam("id") int id, @HeaderParam("Auth") String token,
			@HeaderParam("session") String session)
			throws ClassNotFoundException, SQLException, NoSuchAlgorithmException {
		if (isvalid.isAdmin(token)) {
			if (isvalid.IsValidTokenAndSession(toMD5.GenerateMd5(session), token)) {

				if (isvalid.isValidUserID(id)) {
					return orderdb.GetOrdersbyUserID(id);
				} else {
					return Response.status(Response.Status.NOT_FOUND).entity(" ID wasn't found into Users DB!").build();
				}

			} else {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Your sessionid and tokens aren't match , please log in then try again!").build();
			}

		} else {
			return Response.status(Response.Status.UNAUTHORIZED).entity("Only admins can use this request !").build();
		}

	}

}
