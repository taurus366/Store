package BooleanCheck;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import DataBases.DBconnectionLink;
import models.isValidModels;

@SuppressWarnings("unused")
public class CheckValidBoolean {
	DBconnectionLink connectLink = new DBconnectionLink();
	PreparedStatement myStmt = null;
	Connection conn = null;
	ResultSet rs = null;

	/**
	 * Check if Article id is exist into Cart for the user Authenticated by token
	 * 
	 * @param articleID
	 * @param token     Authentication token
	 * @return send back if it is true or false
	 * @throws ClassNotFoundException if class did not found
	 * @throws SQLException           if query to SQL is not successful
	 */
	public boolean isValidarticleIDinCART(int articleID, String token) throws ClassNotFoundException, SQLException {

		ArrayList<isValidModels> userinfo = new ArrayList<>();

		conn = connectLink.getConnectionLink();

		myStmt = (PreparedStatement) conn.prepareStatement("select * from tokens where `token` =?");
		myStmt.setString(1, token);
		rs = myStmt.executeQuery();

		while (rs.next()) {

			isValidModels user = new isValidModels();
			user.user_token = rs.getString(3);
			user.userID = rs.getInt(2);

			userinfo.add(user);
		}
		int user_id = 0;
		for (int i = 0; i < userinfo.size(); i++) {
			isValidModels info = userinfo.get(i);
			if (info.user_token.equals(token)) {
				user_id = info.userID;
			}
		}

		if (user_id == 0) {
			return false;
		}

		ArrayList<isValidModels> articleinfo = new ArrayList<>();

		conn = connectLink.getConnectionLink();

		myStmt = (PreparedStatement) conn.prepareStatement("select * from cart where `article_id` =? and `user_id` =?");
		myStmt.setInt(1, articleID);
		myStmt.setInt(2, user_id);
		rs = myStmt.executeQuery();

		while (rs.next()) {

			isValidModels article = new isValidModels();
			article.articleID = rs.getInt(4);
			article.userID = rs.getInt(3);

			articleinfo.add(article);
		}
		for (int i = 0; i < articleinfo.size(); i++) {
			isValidModels info = articleinfo.get(i);
			if (info.articleID == articleID && info.userID == user_id) {
				return true;
			}
		}
		return false;

	}

	/**
	 * Check if the article id is exist into guest cart before delete from cart
	 * 
	 * @param articleid
	 * @param session   Session ID
	 * @return send back if it is true or false
	 * @throws ClassNotFoundException   if class did not found
	 * @throws SQLException             if query to SQL is not successful
	 * @throws NoSuchAlgorithmException
	 */
	public boolean isValidArticleIDintoCartbySessionID(int articleid, String session)
			throws ClassNotFoundException, SQLException, NoSuchAlgorithmException {
		ArrayList<isValidModels> guestinfo = new ArrayList<>();

		conn = connectLink.getConnectionLink();

		myStmt = (PreparedStatement) conn.prepareStatement("select * from sessions where `session` =?");
		myStmt.setString(1, session);
		rs = myStmt.executeQuery();

		while (rs.next()) {

			isValidModels guest = new isValidModels();
			guest.session_id = rs.getString(3);

			guestinfo.add(guest);
		}
		if (guestinfo.size() == 0) {
			return false;
		}
		String session_id = null;
		for (int i = 0; i < guestinfo.size(); i++) {
			isValidModels info = guestinfo.get(i);
			if (info.session_id.equals(session)) {

				session_id = info.session_id;

			}

		}

		ArrayList<isValidModels> articleinfo = new ArrayList<>();

		conn = connectLink.getConnectionLink();

		myStmt = (PreparedStatement) conn
				.prepareStatement("select * from cart where `article_id` =? and `session_id` =?");
		myStmt.setInt(1, articleid);
		myStmt.setString(2, session_id);
		rs = myStmt.executeQuery();

		while (rs.next()) {

			isValidModels article = new isValidModels();
			article.articleID = rs.getInt(4);

			articleinfo.add(article);
		}
		for (int i = 0; i < articleinfo.size(); i++) {
			isValidModels info = articleinfo.get(i);
			if (info.articleID == articleid) {
				return true;
			}
		}
		return false;

	}

	/**
	 * fetch id then check is it exist in Articles DB
	 * 
	 * @param id Article ID
	 * @return send back if it is true or false
	 * @throws ClassNotFoundException if class did not found
	 * @throws SQLException           if query to SQL is not successful
	 */

	public boolean isArticul(int id) throws ClassNotFoundException, SQLException {

		ArrayList<isValidModels> articleinfo = new ArrayList<>();

		conn = connectLink.getConnectionLink();

		myStmt = (PreparedStatement) conn.prepareStatement("select * from articles where `id` =?");
		myStmt.setInt(1, id);
		rs = myStmt.executeQuery();

		while (rs.next()) {

			isValidModels article = new isValidModels();
			article.id = rs.getInt(1);
			articleinfo.add(article);

		}

		for (int i = 0; i < articleinfo.size(); i++) {
			isValidModels info = articleinfo.get(i);

			if (info.id == id) {
				return true;
			}
		}

		return false;
	}

	/**
	 * it is checked if the user's email exist(duplicate) into DB users
	 * 
	 * @param email user's email
	 * @return send back if it is true or false
	 * @throws ClassNotFoundException if class did not found
	 * @throws SQLException           if query to SQL is not successful
	 */

	public boolean isDuplicateUser(String email) throws ClassNotFoundException, SQLException {

		ArrayList<isValidModels> userinfo = new ArrayList<>();

		conn = connectLink.getConnectionLink();

		myStmt = (PreparedStatement) conn.prepareStatement("select * from users where `email` =?");
		myStmt.setString(1, email);
		rs = myStmt.executeQuery();

		while (rs.next()) {

			isValidModels user = new isValidModels();
			user.email = rs.getString(2);

			userinfo.add(user);
		}
		for (int i = 0; i < userinfo.size(); i++) {
			isValidModels isvalid = userinfo.get(i);
			if (isvalid.email.equals(email)) {
				return false;
			}
		}
		return true;

	}

	/**
	 * is checked if the token belong to admin into users DB field type = admin or
	 * user
	 * 
	 * @param token
	 * @return
	 * @throws ClassNotFoundException if class did not found
	 * @throws SQLException           if query to SQL is not successful
	 */

	public boolean isAdmin(String token) throws ClassNotFoundException, SQLException {

		ArrayList<isValidModels> admininfo = new ArrayList<>();

		conn = connectLink.getConnectionLink();

		myStmt = (PreparedStatement) conn.prepareStatement(
				"select us.`id` as 'user_id',us.`type` as 'type',tok.`token` as 'user_token' from `tokens` as tok inner join `users` as us on us.`id` = tok.`user_id` where tok.`token` =?");
		myStmt.setString(1, token);
		rs = myStmt.executeQuery();

		while (rs.next()) {

			isValidModels admin = new isValidModels();
			admin.user_id = rs.getInt(1);
			admin.type = rs.getString(2);
			admin.token = rs.getString(3);
			admininfo.add(admin);
		}
		for (int i = 0; i < admininfo.size(); i++) {
			isValidModels admin = admininfo.get(i);
			if (admin.type.equals("admin")) {
				return true;

			}

		}

		return false;

	}

	/**
	 * it is checked if the token exists into DB tokens
	 * 
	 * @param token user's token
	 * @return send back if it is false or true
	 * @throws ClassNotFoundException if class did not found
	 * @throws SQLException           if query to SQL is not successful
	 */

	public boolean isValidUser(String token) throws ClassNotFoundException, SQLException {

		ArrayList<isValidModels> userinfo = new ArrayList<>();

		conn = connectLink.getConnectionLink();

		myStmt = (PreparedStatement) conn.prepareStatement("select * from tokens where `token` =?");
		myStmt.setString(1, token);
		rs = myStmt.executeQuery();

		while (rs.next()) {

			isValidModels user = new isValidModels();
			user.id = rs.getInt(2);
			user.token = rs.getString(3);

			userinfo.add(user);
		}
		for (int i = 0; i < userinfo.size(); i++) {
			isValidModels info = userinfo.get(i);
			if (info.token.equals(token)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * it is checked if the user's id is exist into the DB users
	 * 
	 * @param id user's id
	 * @return send back if it is true or false
	 * @throws ClassNotFoundException if class did not found
	 * @throws SQLException           if query to SQL is not successful
	 */
	public boolean isValidUserID(int id) throws ClassNotFoundException, SQLException {

		conn = connectLink.getConnectionLink();

		myStmt = (PreparedStatement) conn.prepareStatement("select * from users where `id` =?");
		myStmt.setInt(1, id);
		rs = myStmt.executeQuery();

		ArrayList<isValidModels> userInfo = new ArrayList<>();

		while (rs.next()) {

			isValidModels user = new isValidModels();
			user.user_id = rs.getInt(1);

			userInfo.add(user);
		}
		if (userInfo.size() == 0) {
			return false;
		}

		return true;
	}

	/**
	 * it is checked if the user's session id is exist into the sessions DB
	 * 
	 * @param session Session ID
	 * @return send back if it is true or false
	 * @throws ClassNotFoundException if class did not found
	 * @throws SQLException           if query to SQL is not successful
	 */
	public boolean isValidArticleIDintoCartbySession(String session) throws ClassNotFoundException, SQLException {
		ArrayList<isValidModels> userinfo = new ArrayList<>();

		conn = connectLink.getConnectionLink();

		myStmt = (PreparedStatement) conn.prepareStatement("select * from cart where `session_id` =?");
		myStmt.setString(1, session);
		rs = myStmt.executeQuery();

		while (rs.next()) {

			isValidModels user = new isValidModels();
			user.session_id = rs.getString(2);

			userinfo.add(user);
		}
		if (userinfo.size() > 0) {
			return true;
		}
		return false;
	}
	
	/**Check if the Guest's session ID is valid
	 * 
	 * @param session Guest's session ID
	 * @return send back if true or false
	 * @throws ClassNotFoundException if class did not found
	 * @throws SQLException if query to SQL is not successful
	 */ 
	public Boolean isvalidGuestsSessionID(String session) throws ClassNotFoundException, SQLException {
		conn = connectLink.getConnectionLink();
		
		
		myStmt = (PreparedStatement) conn.prepareStatement(
				"Select * from `sessions` where `session`=? and `user_id`='0'");
		myStmt.setString(1, session);
		rs = myStmt.executeQuery();
		
		ArrayList<isValidModels> sessioninfo = new ArrayList<>();
		
		while (rs.next()) {
			
			isValidModels sessionID = new isValidModels();
			sessionID.user_id = rs.getInt(2);
			sessionID.session_id = rs.getString(3);
			sessioninfo.add(sessionID);
			

		}
		if(sessioninfo.size()>0) {
			return true;
		}
		
		
		
		return false;
	}
	
	/**
	 * it is checked if the session id is valid
	 * 
	 * @param session Session ID
	 * @return send back if it is true or false
	 * @throws SQLException             if query to SQL is not successful
	 * @throws ClassNotFoundException   if class did not found
	 * @throws NoSuchAlgorithmException
	 */

	public boolean isvalidsessionID(String session)
			throws SQLException, ClassNotFoundException, NoSuchAlgorithmException {

		ArrayList<isValidModels> usersCartinfo = new ArrayList<>();

		conn = connectLink.getConnectionLink();
		myStmt = (PreparedStatement) conn.prepareStatement("select * from sessions where `session` =?");
		myStmt.setString(1, session);
		rs = myStmt.executeQuery();

		while (rs.next()) {

			isValidModels userCart = new isValidModels();

			userCart.session_id = rs.getString(3);
			usersCartinfo.add(userCart);
		}
		if (usersCartinfo.size() > 0) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * is checked if the token and session is belong to same user_id
	 * 
	 * @param session Session ID
	 * @param token   Authentication token
	 * @return send back if it is true or false
	 * @throws ClassNotFoundException   if class did not found
	 * @throws SQLException             if query to SQL is not successful
	 * @throws NoSuchAlgorithmException
	 */
	public boolean IsValidTokenAndSession(String session, String token)
			throws ClassNotFoundException, SQLException, NoSuchAlgorithmException {

		ArrayList<isValidModels> TokenInfo = new ArrayList<>();
		ArrayList<isValidModels> sessionInfo = new ArrayList<>();

		conn = connectLink.getConnectionLink();
		myStmt = (PreparedStatement) conn.prepareStatement("select * from tokens where `token` =?");
		myStmt.setString(1, token);
		rs = myStmt.executeQuery();

		while (rs.next()) {

			isValidModels userinfo = new isValidModels();

			userinfo.user_id = rs.getInt(2);
			userinfo.token = rs.getString(3);
			TokenInfo.add(userinfo);

		}
		int user_idFromTokens = 0;
		for (int i = 0; i < TokenInfo.size(); i++) {
			isValidModels tokeninfo = TokenInfo.get(i);
			user_idFromTokens = tokeninfo.user_id;
		}

		conn = connectLink.getConnectionLink();
		myStmt = (PreparedStatement) conn.prepareStatement("select * from sessions where `session` =?");
		myStmt.setString(1, session);
		rs = myStmt.executeQuery();

		while (rs.next()) {

			isValidModels userinfo = new isValidModels();

			userinfo.user_id = rs.getInt(2);
			userinfo.session_id = rs.getString(3);
			sessionInfo.add(userinfo);

		}

		if (sessionInfo.size() == 0) {
			return false;
		}
		int user_idFromsessions = 0;
		for (int i = 0; i < sessionInfo.size(); i++) {
			isValidModels sessioninfo = sessionInfo.get(i);
			user_idFromsessions = sessioninfo.user_id;
		}

		if (user_idFromsessions == user_idFromTokens) {
			return true;
		}

		return false;
	}
	public boolean isvalidCookie(String cookie) throws ClassNotFoundException, SQLException {
		conn = connectLink.getConnectionLink();
		myStmt = (PreparedStatement) conn.prepareStatement("select * from tokens where `token` =?");
		myStmt.setString(1, cookie);
		rs = myStmt.executeQuery();
		ArrayList<isValidModels> Cookieinfo = new ArrayList<>();
		while (rs.next()) {

			isValidModels userinfo = new isValidModels();

			
			userinfo.token = rs.getString(3);
			Cookieinfo.add(userinfo);

		}
		if(Cookieinfo!=null || Cookieinfo.size()>0) {
			
			return true;
		}
		
		return false;
	}
}
