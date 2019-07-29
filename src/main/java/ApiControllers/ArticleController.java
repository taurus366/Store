package ApiControllers;

import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;


import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import javax.ws.rs.core.Cookie;

import javax.ws.rs.core.Response;



import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import BooleanCheck.CheckValidBoolean;
import BooleanCheck.DeleteOldSessions;
import BooleanCheck.DeleteOldTokens;
import DataBases.ArticleDB;
import DataBases.DBconnectionLink;

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
	DeleteOldSessions oldsession = new DeleteOldSessions();

	/**
	 * Get all Articles from DB by filtering title or by author fetch title,author
	 * then check which is 0 or >0 , then show Articles by filtering
	 * 
	 * @param title  title of the Articles
	 * @param author author of the Articles
	 * @return send all info to /Databases
	 * @throws ClassNotFoundException   if class did not found
	 * @throws SQLException             if query to SQL is not successful
	 * @throws NoSuchAlgorithmException
	 * @throws URISyntaxException 
	 */
	@GET
	@Path("/articles")
	public Response GetArticleBYTitle(@DefaultValue("0") @QueryParam("title") String title,
			@DefaultValue("0") @QueryParam("author") String author ,@CookieParam(value="myStrCookie") String cookieParam1)
			throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, URISyntaxException {
		//deleteoldTokens.DeleteOldTokens();
		//oldsession.DeleteoldSessions();
		
	  if(cookieParam1.length()==0 || cookieParam1 == null) {
		 // must redirected to login html
		 // java.net.URI url = new java.net.URI("/Store/login.html");
		//	return Response.temporaryRedirect(url).build();
		  if(isvalid.isvalidCookie(cookieParam1)) {  // i have to write code about check the cookie code is true!
	  
		System.out.println(cookieParam1);
		System.out.println("TEST~!");
		if (title.equals("0") && author.equals("0")) {
			
			return dbArticle.doGETallArticlesfromDB();
		} else if (!title.equals("0") && author.equals("0")) {

			return dbArticle.GetArticlesfromDBbyTITLE(title);
		} else if (!author.equals("0") && title.equals("0")) {

			return dbArticle.GetArticlesfromDBbyAUTHOR(author);
		}
	  }
	  }
		  java.net.URI url = new java.net.URI("/Store/login.html");
			return Response.temporaryRedirect(url).build();
//		return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//				.entity("Please be sure that  ^title^ or ^author^ param are empty or each one !").build();

	}
}
