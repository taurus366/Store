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

	
	
	@GET
	@Path("/articles")
	public Response GetArticleBYTitle(@CookieParam(value="myStrCookie") String cookieParam1)
			throws ClassNotFoundException, SQLException, NoSuchAlgorithmException, URISyntaxException {
		
		
	  if(cookieParam1.length()!=0 || cookieParam1 != null || cookieParam1.length()>0) {
		
		 
		  if(isvalid.isvalidCookie(cookieParam1)) {  // i have to write code about check the cookie code is true!
			  return dbArticle.doGETallArticlesfromDB();
			  
			  

	  }
	  }
		  java.net.URI url = new java.net.URI("/Store/login.html");
			return Response.temporaryRedirect(url).build();


	}
}
