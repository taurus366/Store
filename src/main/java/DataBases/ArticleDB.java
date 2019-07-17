package DataBases;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import models.isValidModels;

public class ArticleDB {
	DBconnectionLink connectLink = new DBconnectionLink();
	PreparedStatement myStmt = null;
	Connection conn = null;
	ResultSet rs = null;

	/**
	 * article row is deleted by ID , but only admins can use this , it depends by
	 * token to check if it is admin.
	 * 
	 * @param id Article ID
	 * @return send message to display that the article with id has deleted
	 * @throws ClassNotFoundException if class didn't find
	 * @throws SQLException           if query to SQL is not successful
	 */
	public Response deleteArticle(int id) throws ClassNotFoundException, SQLException {

		ArrayList<isValidModels> articlesinfo = new ArrayList<>();

		conn = connectLink.getConnectionLink();
		myStmt = (PreparedStatement) conn
				.prepareStatement("DELETE FROM `" + connectLink.schema + "`.`articles` WHERE (`id` =?) LIMIT 1");
		myStmt.setInt(1, id);
		myStmt.executeUpdate();

		myStmt = (PreparedStatement) conn.prepareStatement("Select * from cart where `article_id` =?");
		myStmt.setInt(1, id);
		rs = myStmt.executeQuery();

		while (rs.next()) {

			isValidModels article = new isValidModels();

			article.articleID = rs.getInt(3);

			articlesinfo.add(article);

		}
		for (int i = 0; i < articlesinfo.size(); i++) {
			isValidModels article = articlesinfo.get(i);
			if (article.articleID == id) {
				myStmt = (PreparedStatement) conn.prepareStatement(
						"DELETE FROM `" + connectLink.schema + "`.`cart` WHERE (`article_id` =?) LIMIT 1");
				myStmt.setInt(1, id);
				myStmt.executeUpdate();

			}

		}

		return Response.status(Response.Status.OK).entity("You deleted article with ID:" + id).build();
	}

	/**
	 * Create a new row into DB articles
	 * 
	 * @param title   required field to create title of the article
	 * @param content required field to create content of the article
	 * @param author  required field to create author of the article
	 * @return send message to display that article was completely created
	 * @throws ClassNotFoundException if class didn't find
	 * @throws SQLException           if query to SQL is not successful
	 */
	public Response doPOSTarticles(String title, String content, String author)
			throws ClassNotFoundException, SQLException {

		try {

			conn = connectLink.getConnectionLink();
			myStmt = (PreparedStatement) conn.prepareStatement("INSERT INTO `" + connectLink.schema
					+ "`.`articles` (`id`, `title`, `content`, `author`, `creation date`) VALUES (default,?,?,?,now())");

			myStmt.setString(1, title);
			myStmt.setString(2, content);
			myStmt.setString(3, author);

			myStmt.executeUpdate();
		}

		catch (SQLException e) {

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
		return Response.status(Response.Status.OK).entity("You have added an article to DB!").build();

	}

	/**
	 * Return all articles to display from DB articles
	 * 
	 * @return to display article list
	 * @throws ClassNotFoundException if class didn't find
	 * @throws SQLException           if query to SQL is not successful
	 */
	@SuppressWarnings("unchecked")
	public Response doGETallArticlesfromDB() throws ClassNotFoundException, SQLException {

		//String sql = "SELECT * FROM articles";
		String sql = "select\r\n" + 
				"arp.`url` as 'url',\r\n" + 
				"art.`id` as 'id',\r\n" + 
				"art.`title` as 'title',\r\n" + 
				"art.`content` as 'content',\r\n" + 
				"art.`author` as 'author'\r\n" + 
				"from `articles_photo` as arp\r\n" + 
				"inner join `articles` as art on\r\n" + 
				"arp.`articles_id` = art.`id`";
		JSONArray Articleslist = new JSONArray();

		try {

			conn = connectLink.getConnectionLink();

			Statement stm = (Statement) conn.createStatement();

			rs = stm.executeQuery(sql);
			while (rs.next()) {

				JSONObject articleInfo = new JSONObject();
				articleInfo.put("url", rs.getString(1));
				articleInfo.put("id", rs.getString(2));
				articleInfo.put("title", rs.getString(3));
				articleInfo.put("content", rs.getString(4));
				articleInfo.put("author", rs.getString(5));
				Articleslist.add(articleInfo);

			}

		} catch (Exception ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();

		}

		return Response.status(Status.OK).entity(Articleslist.toString()).build();

	}

	/**
	 * article can be modified by article ID
	 * 
	 * @param id      article ID
	 * @param title   required field to modify title of the article
	 * @param content required field to modify content of the article
	 * @param author  required field to modify author of the article
	 * @returnsend a message to the display that operation is completed successful
	 * @throws SQLException           if query to SQL is not successful
	 * @throws ClassNotFoundException if class didn't find
	 */
	public Response doModifyArticle(int id, String title, String content, String author)
			throws SQLException, ClassNotFoundException {
		try {

			conn = connectLink.getConnectionLink();
			myStmt = (PreparedStatement) conn.prepareStatement("UPDATE `" + connectLink.schema
					+ "`.`articles` SET `title` =?, `content` =?, `author` =? , `modified_date` = now() WHERE (`id` =?)");

			myStmt.setString(1, title);
			myStmt.setString(2, content);
			myStmt.setString(3, author);
			myStmt.setInt(4, id);
			myStmt.executeUpdate();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}

		return Response.status(Response.Status.OK).entity("You have changed articul with id:" + id).build();

	}

	/**
	 * Send articles which filtered by title to display
	 * 
	 * @param title required field to filter articles
	 * @return to display article list By title
	 * @throws ClassNotFoundException if class didn't find
	 * @throws SQLException           if query to SQL is not successful
	 */

	@SuppressWarnings("unchecked")
	public Response GetArticlesfromDBbyTITLE(String title) throws ClassNotFoundException, SQLException {

		conn = connectLink.getConnectionLink();
		myStmt = (PreparedStatement) conn
				.prepareStatement("select * from `articles` where `title` LIKE CONCAT ('%', ? , '%')");
		myStmt.setString(1, title);
		rs = myStmt.executeQuery();
		JSONArray ArticlesbyTitle = new JSONArray();
		while (rs.next()) {

			JSONObject articleInfo = new JSONObject();
			articleInfo.put("ID", rs.getInt(1));
			articleInfo.put("Title", rs.getString(2));
			articleInfo.put("Content", rs.getString(3));
			articleInfo.put("Author", rs.getString(4));
			articleInfo.put("Created", rs.getString(5));
			ArticlesbyTitle.add(articleInfo);

		}
		if(ArticlesbyTitle.size()==0) {
			return Response.status(Status.NOT_FOUND).entity("We couldn't find article with same title").build();
		}
		return Response.status(Status.OK).entity(ArticlesbyTitle.toString()).build();

	}

	/**
	 * Send articles which filtered by author to display
	 * 
	 * @param author required field to filter articles
	 * @return to display article list by author
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public Response GetArticlesfromDBbyAUTHOR(String author) throws ClassNotFoundException, SQLException {

		conn = connectLink.getConnectionLink();
		myStmt = (PreparedStatement) conn
				.prepareStatement("select * from `articles` where `author` LIKE CONCAT ('%', ? , '%')");
		myStmt.setString(1, author);
		rs = myStmt.executeQuery();
		JSONArray ArticlesbyAuthor = new JSONArray();
		while (rs.next()) {

			JSONObject articleInfo = new JSONObject();
			articleInfo.put("ID", rs.getInt(1));
			articleInfo.put("Title", rs.getString(2));
			articleInfo.put("Content", rs.getString(3));
			articleInfo.put("Author", rs.getString(4));
			articleInfo.put("Created", rs.getString(5));
			ArticlesbyAuthor.add(articleInfo);

		}
		if(ArticlesbyAuthor.size()==0) {
			return Response.status(Status.NOT_FOUND).entity("We couldn't find article with same author").build();
		}

		return Response.status(Status.OK).entity(ArticlesbyAuthor.toString()).build();
	}
}
