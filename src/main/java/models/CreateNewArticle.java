package models;

public class CreateNewArticle {

	String title;
	String content;
	String author;

	public String getTitle() {
		return title;

	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public CreateNewArticle(String title, String content, String author) {
		this.title = title;
		this.content = content;
		this.author = author;

	}

	public CreateNewArticle() {

	}

}
